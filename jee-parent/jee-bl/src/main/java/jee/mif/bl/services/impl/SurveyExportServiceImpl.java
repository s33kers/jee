package jee.mif.bl.services.impl;

import jee.mif.bl.model.ActionEnum;
import jee.mif.bl.model.Result;
import jee.mif.bl.services.SurveyExportService;
import jee.mif.bl.services.model.AnswerSheetColumn;
import jee.mif.bl.services.model.SheetName;
import jee.mif.bl.services.model.SurveySheetColumn;
import jee.mif.model.survey.BaseQuestion;
import jee.mif.model.survey.ChooseQuestion;
import jee.mif.model.survey.ChooseQuestionOption;
import jee.mif.model.survey.Page;
import jee.mif.model.survey.QuestionAnswer;
import jee.mif.model.survey.ScaleQuestion;
import jee.mif.model.survey.Survey;
import jee.mif.model.survey.SurveyAnswer;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SurveyExportServiceImpl implements SurveyExportService {

    private final static String ERROR_COULD_NOT_SAVE = "Could not save XLSX file";

    private static final List<SurveySheetColumn> SURVEY_SHEET_COLUMNS = Arrays.asList(
            SurveySheetColumn.QUESTION_NUMBER,
            SurveySheetColumn.MANDATORY,
            SurveySheetColumn.QUESTION,
            SurveySheetColumn.QUESTION_TYPE,
            SurveySheetColumn.OPTION_LIST
    );

    private static final List<AnswerSheetColumn> ANSWER_SHEET_COLUMNS = Arrays.asList(
            AnswerSheetColumn.ANSWER_ID,
            AnswerSheetColumn.QUESTION_NUMBER,
            AnswerSheetColumn.ANSWER
    );

    @Override
    public Result<Void> exportXlsx(Survey survey, OutputStream outputStream) {
        Workbook workbook = new XSSFWorkbook();

        Map<Long, List<Long>> questionsOrderedOptions = new HashMap<>();

        Result<Void> headerResult = createHeaderSheet(survey, workbook);
        if (!headerResult.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, headerResult.getAdditionalInfo());
        }

        Result<Void> questionsResult = createQuestionsSheet(survey.getPages(), workbook, questionsOrderedOptions);
        if (!questionsResult.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, questionsResult.getAdditionalInfo());
        }

        Result<Void> answersResult = createAnswersSheet(survey.getSurveyAnswers(), workbook, questionsOrderedOptions);
        if (!answersResult.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, answersResult.getAdditionalInfo());
        }

        try {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result<>(ActionEnum.FAILURE, ERROR_COULD_NOT_SAVE);
        }

        return new Result<>(ActionEnum.SUCCESS);
    }

    private Result<Void> createHeaderSheet(Survey survey, Workbook workbook) {
        Sheet sheet = workbook.createSheet(SheetName.HEADER.value);

        sheet.setColumnWidth(0, 20 * 256);
        sheet.setColumnWidth(1, 50 * 256);

        Row nameRow = sheet.createRow(0);
        nameRow.createCell(0).setCellValue("$name");
        nameRow.createCell(1).setCellValue(survey.getTitle());

        Row descriptionRow = sheet.createRow(1);
        descriptionRow.createCell(0).setCellValue("$description");
        descriptionRow.createCell(1).setCellValue(survey.getDescription());

        Row validateRow = sheet.createRow(2);
        validateRow.createCell(0).setCellValue("$validate");
        Cell dateCell = validateRow.createCell(1);
        dateCell.setCellValue(Date.from(survey.getValidDate().atZone(ZoneId.systemDefault()).toInstant()));
        CellStyle cellStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));
        dateCell.setCellStyle(cellStyle);

        Row publicRow = sheet.createRow(3);
        publicRow.createCell(0).setCellValue("$public");
        publicRow.createCell(1).setCellValue(survey.getResultsPublic() ? "YES" : "NO");

        return new Result<>(ActionEnum.SUCCESS);
    }

    private Result<Void> createQuestionsSheet(List<Page> pages, Workbook workbook, Map<Long, List<Long>> questionsOrderedOptions) {
        Sheet sheet = workbook.createSheet(SheetName.SURVEY.value);

        sheet.setColumnWidth(0, 16 * 256);
        sheet.setColumnWidth(1, 16 * 256);
        sheet.setColumnWidth(2, 50 * 256);
        sheet.setColumnWidth(3, 15 * 256);

        List<String> columnNames = SURVEY_SHEET_COLUMNS.stream().map(SurveySheetColumn::getValue).collect(Collectors.toList());
        Result<Void> columnWriteResult = writeColumnNames(sheet.createRow(0), columnNames);
        if (!columnWriteResult.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, columnWriteResult.getAdditionalInfo());
        }

        int rowIndex = 1;
        for (int i = 0; i < pages.size(); ++i) {
            for (BaseQuestion question : pages.get(i).getBaseQuestions()) {
                Result<Void> writeQuestionResult = writeQuestion(sheet.createRow(rowIndex), question.getId(), question, questionsOrderedOptions);
                if (!writeQuestionResult.isSuccess()) {
                    return new Result<>(ActionEnum.FAILURE, writeQuestionResult.getAdditionalInfo());
                }
                ++rowIndex;
            }
            if (i < pages.size() - 1) {
                Result<Void> separatorRowResult = writeSeparator(sheet.createRow(rowIndex));
                if (!separatorRowResult.isSuccess()) {
                    return new Result<>(ActionEnum.FAILURE, separatorRowResult.getAdditionalInfo());
                }
                ++rowIndex;
            }
        }
        return new Result<>(ActionEnum.SUCCESS);
    }

    private Result<Void> createAnswersSheet(List<SurveyAnswer> answeredSurveys, Workbook workbook, Map<Long, List<Long>> questionsOrderedOptions) {
        Sheet sheet = workbook.createSheet(SheetName.ANSWER.value);

        sheet.setColumnWidth(0, 16 * 256);
        sheet.setColumnWidth(1, 16 * 256);
        sheet.setColumnWidth(2, 50 * 256);

        List<String> columnNames = ANSWER_SHEET_COLUMNS.stream().map(AnswerSheetColumn::getValue).collect(Collectors.toList());
        Result<Void> columnWriteResult = writeColumnNames(sheet.createRow(0), columnNames);
        if (!columnWriteResult.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, columnWriteResult.getAdditionalInfo());
        }
        System.out.println("answeredSurveys = " + answeredSurveys.size());
        int rowIndex = 1;
        for (SurveyAnswer answeredSurvey : answeredSurveys) {
            if (BooleanUtils.isNotTrue(answeredSurvey.getCompleted())) {
                continue;
            }
            for (QuestionAnswer answer : answeredSurvey.getQuestionAnswers()) {
                Result<Void> writeAnswerResult = writeAnswer(sheet.createRow(rowIndex), answeredSurvey.getId(), answer, questionsOrderedOptions);
                if (!writeAnswerResult.isSuccess()) {
                    return new Result<>(ActionEnum.FAILURE, writeAnswerResult.getAdditionalInfo());
                }
                ++rowIndex;
            }
        }
        return new Result<>(ActionEnum.SUCCESS);
    }

    private Result<Void> writeColumnNames(Row row, List<String> names) {
        for (int i = 0; i < names.size(); ++i) {
            row.createCell(i).setCellValue(names.get(i));
        }
        return new Result<>(ActionEnum.SUCCESS);
    }

    private Result<Void> writeSeparator(Row row) {
        row.createCell(0, CellType.STRING).setCellValue("*");
        return new Result<>(ActionEnum.SUCCESS);
    }

    private Result<Void> writeQuestion(Row row, Long questionNumber, BaseQuestion question, Map<Long, List<Long>> questionsOrderedOptions) {
        row.createCell(0).setCellValue(questionNumber);
        row.createCell(1).setCellValue(question.getMandatory() ? "YES" : "NO");
        row.createCell(2).setCellValue(question.getQuestion());
        row.createCell(3).setCellValue(question.getQuestionType().toString());
        switch (question.getQuestionType()) {
            case TEXT:
                break;
            case CHECKBOX:
            case MULTIPLECHOICE: {
                ChooseQuestion chooseQuestion = (ChooseQuestion) question;
                List<Long> orderedOptions = new ArrayList<>();
                for (int i = 0; i < chooseQuestion.getChooseQuestionOptions().size(); ++i) {
                    row.createCell(4 + i).setCellValue(chooseQuestion.getChooseQuestionOptions().get(i).getOption());
                    // saving choices order because set does not guarantee order
                    orderedOptions.add(chooseQuestion.getChooseQuestionOptions().get(i).getId());
                }
                questionsOrderedOptions.put(question.getId(), orderedOptions);
                break;
            }
            case SCALE: {
                ScaleQuestion scaleQuestion = (ScaleQuestion) question;
                row.createCell(4).setCellValue(scaleQuestion.getMinValue().intValue());
                row.createCell(5).setCellValue(scaleQuestion.getMaxValue().intValue());
                break;
            }
            default:
                return new Result<>(ActionEnum.FAILURE, "Unknown question type: " + question.getQuestionType().toString());
        }

        return new Result<>(ActionEnum.SUCCESS);
    }

    private Result<Void> writeAnswer(Row row, Long surveyAnswerId, QuestionAnswer answer, Map<Long, List<Long>> questionsOrderedOptions) {
        row.createCell(0).setCellValue(surveyAnswerId);
        row.createCell(1).setCellValue(answer.getBaseQuestion().getId());
        switch (answer.getBaseQuestion().getQuestionType()) {
            case TEXT:
            case SCALE: {
                row.createCell(2).setCellValue(answer.getAnswer());
                break;
            }
            case CHECKBOX:
            case MULTIPLECHOICE: {
                int optionIndex = 2;
                for (ChooseQuestionOption option : answer.getChooseQuestionOptions()) {
                    row.createCell(optionIndex).setCellValue(String.valueOf(questionsOrderedOptions.get(answer.getBaseQuestion().getId()).indexOf(option.getId()) + 1));
                    ++optionIndex;
                }
                break;
            }
            default:
                return new Result<>(ActionEnum.FAILURE, "Unknown question type: " + answer.getBaseQuestion().getQuestionType());
        }
        return new Result<>(ActionEnum.SUCCESS);
    }
}
