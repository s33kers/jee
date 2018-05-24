package jee.mif.bl.services.impl;

import jee.mif.bl.aspect.Monitor;
import jee.mif.bl.model.ActionEnum;
import jee.mif.bl.model.Result;
import jee.mif.bl.repositories.SurveyRepository;
import jee.mif.bl.services.AnswerSheetReader;
import jee.mif.bl.services.HeaderSheetReader;
import jee.mif.bl.services.QuestionSheetReader;
import jee.mif.bl.services.SurveyImporter;
import jee.mif.bl.services.SurveyService;
import jee.mif.bl.services.UserService;
import jee.mif.bl.services.model.AnswerRow;
import jee.mif.bl.services.model.AnswerSheetColumn;
import jee.mif.bl.services.model.BaseQuestionRow;
import jee.mif.bl.services.model.ChooseQuestion;
import jee.mif.bl.services.model.SurveyHeader;
import jee.mif.bl.services.model.PageRow;
import jee.mif.bl.services.model.ScaleQuestionRow;
import jee.mif.bl.services.model.SheetName;
import jee.mif.bl.services.model.TextQuestionRow;
import jee.mif.model.survey.BaseQuestion;
import jee.mif.model.survey.ChooseQuestionOption;
import jee.mif.model.survey.Page;
import jee.mif.model.survey.QuestionAnswer;
import jee.mif.model.survey.ScaleQuestion;
import jee.mif.model.survey.Survey;
import jee.mif.model.survey.SurveyAnswer;
import jee.mif.model.survey.TextQuestion;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;

@Component
public class SurveyImporterImpl implements SurveyImporter {

    @Autowired
    private HeaderSheetReader headerSheetReader;

    @Autowired
    private QuestionSheetReader questionSheetReader;

    @Autowired
    private AnswerSheetReader answerSheetReader;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private UserService userService;

    @Monitor
    @Async
    @Override
    public void importXlsx(byte[] file, Long userId, Long surveyId) {
        Survey survey = createAndSaveUploadingSurvey(surveyId, userId);
        proccessImport(file, survey);
    }

    private void proccessImport(byte[] inputStream, Survey survey) {
        StringJoiner joiner = new StringJoiner(";</br>");
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(inputStream))) {
            Thread.sleep(10000);
            survey = importXlsxHelper(workbook, survey, joiner);
        } catch (Exception e) {
            joiner.add("Unexpected error happened.");
        }

        if (joiner.length() != 0) {
            survey.setUploadingResults(joiner.toString());
            survey.setDraft(true);
        } else {
            survey.setDraft(false);
        }
        survey.setUploading(false);
        survey.setUploaded(true);

        surveyRepository.save(survey);
    }

    private Survey importXlsxHelper(Workbook workbook, Survey survey, StringJoiner joiner) {
        Sheet headerSheet = workbook.getSheet(SheetName.HEADER.value);
        SurveyHeader surveyHeader = null;
        if (headerSheet == null) {
            joiner.add("XLSX file does not contain sheet named \"" + SheetName.HEADER.value + "\"");
        } else {
            surveyHeader = headerSheetReader.readHeaderSheet(headerSheet, joiner);
        }

        Sheet questionSheet = workbook.getSheet(SheetName.SURVEY.value);
        List<PageRow> questionRows = null;
        if (questionSheet == null) {
            joiner.add("XLSX file does not contain sheet named \"" + SheetName.SURVEY.value + "\"");
        } else {
            questionRows = questionSheetReader.readQuestionSheet(questionSheet, joiner);
        }

        Sheet answerSheet = workbook.getSheet(SheetName.ANSWER.value);
        List<AnswerRow> answerRows = null;
        if (answerSheet == null) {
            joiner.add("XLSX file does not contain sheet named \"" + SheetName.ANSWER.value + "\"");
        } else {
            answerRows = answerSheetReader.readAnswerSheet(answerSheet, joiner);
        }

        if (joiner.length() != 0) {
            return survey;
        }

        return createSurvey(surveyHeader, questionRows, answerRows, survey, joiner);
    }


    @Transactional
    private Survey createAndSaveUploadingSurvey(Long surveyId, Long userId) {
        Survey survey;
        if (surveyId == null) {
            survey = new Survey();
        } else {
            survey = surveyRepository.findOne(surveyId);
        }
        survey.setCreationDate(LocalDateTime.now());
        survey.setUser(userService.getJeeUserById(userId).getResult());
        survey.setUploaded(false);
        survey.setUploading(true);
        survey.setDraft(true);
        survey.setUploadingResults(null);
        return surveyRepository.save(survey);
    }

    private Survey createSurvey(SurveyHeader surveyHeader, List<PageRow> questionRows, List<AnswerRow> answerRows, Survey survey, StringJoiner joiner) {
        survey.setValidDate(surveyHeader.getValidDate() != null ? surveyHeader.getValidDate().atStartOfDay() : null);
        survey.setTitle(surveyHeader.getTitle());
        survey.setDescription(surveyHeader.getDescription());
        survey.setResultsPublic(surveyHeader.isResultsPublic());
        survey.setUrlToken(UUID.randomUUID().toString());

        Map<Integer, SurveyAnswer> surveyAnswers = new HashMap<>();

        Result<List<Page>> pages = createPages(questionRows, answerRows, joiner, survey, surveyAnswers);
        if (!pages.isSuccess()) {
            return survey;
        }
        survey.setPages(pages.getResult());
        survey.setSurveyAnswers(new ArrayList<>(surveyAnswers.values()));

        return survey;
    }

    private Result<List<Page>> createPages(List<PageRow> pageRows, List<AnswerRow> answerRows, StringJoiner joiner, Survey survey, Map<Integer, SurveyAnswer> surveyAnswers) {
        List<Page> pages = new ArrayList<>();
        for (PageRow pagerow : pageRows) {
            Page page = new Page();
            List<BaseQuestion> questions = new ArrayList<>();

            for (BaseQuestionRow questionRow : pagerow.getBaseQuestionRows()) {
                if (questionRow instanceof TextQuestionRow) {
                    Result<TextQuestion> textQuestion = createTextQuestion((TextQuestionRow) questionRow, answerRows, survey, surveyAnswers);
                    if (textQuestion.isSuccess()) {
                        questions.add(textQuestion.getResult());
                    } else {
                        joiner.add(textQuestion.getAdditionalInfo());
                    }
                } else if (questionRow instanceof ChooseQuestion) {
                    Result<jee.mif.model.survey.ChooseQuestion> chooseQuestion = createChooseQuestion((ChooseQuestion) questionRow, answerRows, survey, surveyAnswers);
                    if (chooseQuestion.isSuccess()) {
                        questions.add(chooseQuestion.getResult());
                    } else {
                        joiner.add(chooseQuestion.getAdditionalInfo());
                    }
                } else if (questionRow instanceof ScaleQuestionRow) {
                    Result<ScaleQuestion> scaleQuestion = createScaleQuestion((ScaleQuestionRow) questionRow, answerRows, survey, surveyAnswers);
                    if (scaleQuestion.isSuccess()) {
                        questions.add(scaleQuestion.getResult());
                    } else {
                        joiner.add(scaleQuestion.getAdditionalInfo());
                    }
                }
            }
            page.setBaseQuestions(questions);
            pages.add(page);
        }

        return new Result<>(pages, ActionEnum.SUCCESS);
    }

    private Result<TextQuestion> createTextQuestion(TextQuestionRow questionRow,
                                                    List<AnswerRow> answerRows,
                                                    Survey survey,
                                                    Map<Integer, SurveyAnswer> surveyAnswers) {
        TextQuestion textQuestion = new TextQuestion();
        textQuestion.setQuestion(questionRow.getQuestion());
        textQuestion.setMandatory(questionRow.getMandatory());
        textQuestion.setQuestionType(questionRow.getType());

        Result<List<QuestionAnswer>> questionAnswers = createSingleValueAnswer(questionRow.getNumber(),
                BooleanUtils.isTrue(questionRow.getMandatory()), answerRows, survey, surveyAnswers);
        if (!questionAnswers.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, questionAnswers.getAdditionalInfo());
        }
        textQuestion.setAnswers(questionAnswers.getResult());

        return new Result<>(textQuestion, ActionEnum.SUCCESS);
    }

    private Result<jee.mif.model.survey.ChooseQuestion> createChooseQuestion(ChooseQuestion questionRow,
                                                                             List<AnswerRow> answerRows,
                                                                             Survey survey,
                                                                             Map<Integer, SurveyAnswer> surveyAnswers) {
        jee.mif.model.survey.ChooseQuestion chooseQuestion = new jee.mif.model.survey.ChooseQuestion();
        chooseQuestion.setQuestion(questionRow.getQuestion());
        chooseQuestion.setMandatory(questionRow.getMandatory());
        chooseQuestion.setQuestionType(questionRow.getType());

        List<ChooseQuestionOption> chooseQuestionOptions = new ArrayList<>();
        for (String choice : questionRow.getChoices()) {
            ChooseQuestionOption answer = new ChooseQuestionOption();
            answer.setOption(choice);
            chooseQuestionOptions.add(answer);
        }
        chooseQuestion.setChooseQuestionOptions(chooseQuestionOptions);

        Result<List<QuestionAnswer>> questionAnswers = createMultipleValueAnswer(questionRow.getNumber(),
                BooleanUtils.isTrue(questionRow.getMandatory()), chooseQuestionOptions, answerRows, survey, surveyAnswers);
        if (!questionAnswers.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, questionAnswers.getAdditionalInfo());
        }
        chooseQuestion.setAnswers(questionAnswers.getResult());

        return new Result<>(chooseQuestion, ActionEnum.SUCCESS);
    }

    private Result<ScaleQuestion> createScaleQuestion(ScaleQuestionRow questionRow,
                                                      List<AnswerRow> answerRows,
                                                      Survey survey,
                                                      Map<Integer, SurveyAnswer> surveyAnswers) {
        ScaleQuestion scaleQuestion = new ScaleQuestion();
        scaleQuestion.setQuestion(questionRow.getQuestion());
        scaleQuestion.setMandatory(questionRow.getMandatory());
        scaleQuestion.setQuestionType(questionRow.getType());
        scaleQuestion.setMinValue(new BigDecimal(questionRow.getMinValue()));
        scaleQuestion.setMaxValue(new BigDecimal(questionRow.getMaxValue()));

        Result<List<QuestionAnswer>> questionAnswers = createSingleValueAnswer(questionRow.getNumber(),
                BooleanUtils.isTrue(questionRow.getMandatory()), answerRows, survey, surveyAnswers);
        if (!questionAnswers.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, questionAnswers.getAdditionalInfo());
        }
        scaleQuestion.setAnswers(questionAnswers.getResult());

        return new Result<>(scaleQuestion, ActionEnum.SUCCESS);
    }

    private Result<List<QuestionAnswer>> createSingleValueAnswer(Integer questionNumber,
                                                                 boolean isMandatory,
                                                                 List<AnswerRow> answerRows,
                                                                 Survey survey,
                                                                 Map<Integer, SurveyAnswer> surveyAnswers) {
        List<QuestionAnswer> answers = new ArrayList<>();

        for (AnswerRow answerRow : answerRows) {
            if (!questionNumber.equals(answerRow.getQuestionNumber())) {
                continue;
            }

            QuestionAnswer questionAnswer = new QuestionAnswer();

            if (answerRow.getAnswers().isEmpty()) {
                if (isMandatory) {
                    String error = "Could not read answer where column " + AnswerSheetColumn.ANSWER_ID + " = " + answerRow.getId() +
                            " and column " + AnswerSheetColumn.QUESTION_NUMBER + " = " + answerRow.getQuestionNumber() +
                            " because it does not have any answers!";
                    return new Result<>(ActionEnum.FAILURE, error);
                } else {
                    continue;
                }
            }


            if (answerRow.getAnswers().size() > 1) {
                String error = "Could not read answer where column " + AnswerSheetColumn.ANSWER_ID + " = " + answerRow.getId() +
                        " and column " + AnswerSheetColumn.QUESTION_NUMBER + " = " + answerRow.getQuestionNumber() +
                        " because it has multiple answer but can only have a single one!";
                return new Result<>(ActionEnum.FAILURE, error);
            }

            questionAnswer.setAnswer(answerRow.getAnswers().get(0));
            answers.add(questionAnswer);
            addAnswer(surveyAnswers, answerRow.getId(), survey, questionAnswer);
        }

        return new Result<>(answers, ActionEnum.SUCCESS);
    }

    private Result<List<QuestionAnswer>> createMultipleValueAnswer(Integer questionNumber,
                                                                   boolean isMandatory,
                                                                   List<ChooseQuestionOption> options,
                                                                   List<AnswerRow> answerRows,
                                                                   Survey survey,
                                                                   Map<Integer, SurveyAnswer> surveyAnswers) {
        List<QuestionAnswer> answers = new ArrayList<>();

        for (AnswerRow answerRow : answerRows) {
            if (!questionNumber.equals(answerRow.getQuestionNumber())) {
                continue;
            }

            QuestionAnswer questionAnswer = new QuestionAnswer();

            if (answerRow.getAnswers().isEmpty()) {
                if (isMandatory) {
                    String error = "Could not read answer where column " + AnswerSheetColumn.ANSWER_ID + " = " + answerRow.getId() +
                            " and column " + AnswerSheetColumn.QUESTION_NUMBER + " = " + answerRow.getQuestionNumber() +
                            " because it does not have any answers!";
                    return new Result<>(ActionEnum.FAILURE, error);
                } else {
                    continue;
                }
            }

            Set<ChooseQuestionOption> chooseQuestionOptions = new HashSet<>();
            for (String answer : answerRow.getAnswers()) {
                try {
                    int optionIndex = Integer.valueOf(answer);

                    if (optionIndex <= 0 || optionIndex > options.size()) {
                        String error = "Could not read answer where column " + AnswerSheetColumn.ANSWER_ID + " = " + answerRow.getId() +
                                " and column " + AnswerSheetColumn.QUESTION_NUMBER + " = " + answerRow.getQuestionNumber() +
                                " because there is no such option identified by number: \"" + answer + "\"." +
                                " There is " + options.size() + " options, so the number should be between 1 and " + options.size();
                        return new Result<>(ActionEnum.FAILURE, error);
                    }

                    chooseQuestionOptions.add(options.get(optionIndex - 1));
                } catch (NumberFormatException ex) {
                    String error = "Could not read answer where column " + AnswerSheetColumn.ANSWER_ID + " = " + answerRow.getId() +
                            " and column " + AnswerSheetColumn.QUESTION_NUMBER + " = " + answerRow.getQuestionNumber() +
                            " because one of the answers is an invalid number: \"" + answer + "\"";
                    return new Result<>(ActionEnum.FAILURE, error);
                }
            }
            questionAnswer.setChooseQuestionOptions(chooseQuestionOptions);
            answers.add(questionAnswer);
            addAnswer(surveyAnswers, answerRow.getId(), survey, questionAnswer);
        }

        return new Result<>(answers, ActionEnum.SUCCESS);
    }

    private void addAnswer(Map<Integer, SurveyAnswer> surveyAnswers, int surveyAnswerId, Survey survey, QuestionAnswer questionAnswer) {
        SurveyAnswer surveyAnswer;
        if (surveyAnswers.containsKey(surveyAnswerId)) {
            surveyAnswer = surveyAnswers.get(surveyAnswerId);
        } else {
            surveyAnswer = new SurveyAnswer();
            surveyAnswer.setSurvey(survey);
            surveyAnswer.setCompleted(true);
            surveyAnswer.setQuestionAnswers(new HashSet<>());
            surveyAnswers.put(surveyAnswerId, surveyAnswer);
        }
        surveyAnswer.getQuestionAnswers().add(questionAnswer);
    }
}
