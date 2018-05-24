package jee.mif.bl.services.impl;

import jee.mif.bl.model.ActionEnum;
import jee.mif.bl.model.Result;
import jee.mif.bl.services.QuestionSheetReader;
import jee.mif.bl.services.SheetReaderHelper;
import jee.mif.bl.services.model.BaseQuestionRow;
import jee.mif.bl.services.model.ChooseQuestion;
import jee.mif.bl.services.model.PageRow;
import jee.mif.bl.services.model.ScaleQuestionRow;
import jee.mif.bl.services.model.SheetName;
import jee.mif.bl.services.model.SurveySheetColumn;
import jee.mif.bl.services.model.TextQuestionRow;
import jee.mif.model.survey.QuestionType;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Component
public class QuestionSheetReaderImpl implements QuestionSheetReader {

    private static final List<SurveySheetColumn> COLUMNS = Arrays.asList(
            SurveySheetColumn.QUESTION_NUMBER,
            SurveySheetColumn.MANDATORY,
            SurveySheetColumn.QUESTION,
            SurveySheetColumn.QUESTION_TYPE,
            SurveySheetColumn.OPTION_LIST
    );

    @Autowired
    private SheetReaderHelper sheetReaderHelper;

    @Override
    public List<PageRow> readQuestionSheet(Sheet questionSheet, StringJoiner joiner) {
        Iterator<Row> rowIterator = questionSheet.rowIterator();
        if (!rowIterator.hasNext()) {
            joiner.add( SheetName.SURVEY.value + " sheet does not have any rows");
            return null;
        }

        Row firstRow = rowIterator.next();
        List<String> columnNames = COLUMNS.stream().map(SurveySheetColumn::getValue).collect(Collectors.toList());
        Result<Void> columnCheck = sheetReaderHelper.checkColumnNames(firstRow, columnNames);
        if (!columnCheck.isSuccess()) {
            joiner.add(columnCheck.getAdditionalInfo());
            return null;
        }

        return readQuestions(rowIterator, joiner);
    }

    private List<PageRow> readQuestions(Iterator<Row> rowIterator, StringJoiner joiner) {
        List<PageRow> pages = new ArrayList<>();
        PageRow page = new PageRow();
        pages.add(page);
        int previousRowNumber = 0;
        while (rowIterator.hasNext()) {
            if (joiner.length() > 8000) {
                break;
            }
            Row row = rowIterator.next();
            if (sheetReaderHelper.isRowEmpty(row)) {
                break;
            } else if (row.getRowNum() - previousRowNumber >= 2) {
                break;
            }
            previousRowNumber = row.getRowNum();

            if (sheetReaderHelper.isPageSeparator(row)) {
                page = new PageRow();
                pages.add(page);
                continue;
            }

            Result<BaseQuestionRow> question = readQuestion(row.cellIterator());
            if (!question.isSuccess()) {
                joiner.add(question.getAdditionalInfo());
            } else {
                page.getBaseQuestionRows().add(question.getResult());
            }
        }

        return pages;
    }

    private Result<BaseQuestionRow> readQuestion(Iterator<Cell> cellIterator) {
        Result<Integer> questionNumber = readQuestionNumber(cellIterator);
        if (!questionNumber.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, questionNumber.getAdditionalInfo());
        }

        Result<Boolean> questionMandatory = readQuestionMandatory(cellIterator, questionNumber.getResult());
        if (!questionMandatory.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, questionMandatory.getAdditionalInfo());
        }

        Result<String> question = readQuestion(cellIterator, questionNumber.getResult());
        if (!question.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, question.getAdditionalInfo());
        }

        Result<QuestionType> questionTypeEnum = readQuestionType(cellIterator, questionNumber.getResult(), question.getResult());
        if (!questionTypeEnum.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, questionTypeEnum.getAdditionalInfo());
        }

        switch (questionTypeEnum.getResult()) {
            case TEXT:
                return readTextQuestion(questionNumber.getResult(), question.getResult(), questionMandatory.getResult());
            case CHECKBOX:
                return readMultipleChoiceQuestion(cellIterator, questionNumber.getResult(), question.getResult(), questionMandatory.getResult(), true);
            case MULTIPLECHOICE:
                return readMultipleChoiceQuestion(cellIterator, questionNumber.getResult(), question.getResult(), questionMandatory.getResult(), false);
            case SCALE:
                return readScaleQuestion(cellIterator, questionNumber.getResult(), question.getResult(), questionMandatory.getResult());
            default:
                return new Result<>(ActionEnum.FAILURE, "Could not edit question type \"" + questionTypeEnum + "\"");
        }
    }

    private Result<Integer> readQuestionNumber(Iterator<Cell> cellIterator) {
        if (!cellIterator.hasNext()) {
            String error = "Could not read question number because there is no cells";
            return new Result<>(ActionEnum.FAILURE, error);
        }

        return sheetReaderHelper.readInteger(cellIterator.next());
    }

    private Result<Boolean> readQuestionMandatory(Iterator<Cell> cellIterator, Integer questionNumber) {
        String error = "Could not read if question is mandatory from row where " +
                SurveySheetColumn.QUESTION_NUMBER + " = " + questionNumber;
        if (!cellIterator.hasNext()) {
            return new Result<>(ActionEnum.FAILURE, error + " because row does not have " + SurveySheetColumn.MANDATORY + " column");
        }
        Result<String> value = sheetReaderHelper.readString(cellIterator.next());
        switch (value.getResult().toUpperCase()) {
            case "YES":
                return new Result(Boolean.TRUE, ActionEnum.SUCCESS);
            case "NO":
                return new Result(Boolean.FALSE, ActionEnum.SUCCESS);
            default:
                return new Result(ActionEnum.FAILURE, error + " because cannot parse value");
        }
    }

    private Result<String> readQuestion(Iterator<Cell> cellIterator, Integer questionNumber) {
        if (!cellIterator.hasNext()) {
            String error = "Could not read question from row where " +
                    SurveySheetColumn.QUESTION_NUMBER + " = " + questionNumber +
                    " because row does not have " + SurveySheetColumn.QUESTION + " column";
            return new Result<>(ActionEnum.FAILURE, error);
        }

        return sheetReaderHelper.readString(cellIterator.next());
    }

    private Result<QuestionType> readQuestionType(Iterator<Cell> cellIterator, Integer questionNumber, String question) {
        if (!cellIterator.hasNext()) {
            String error = "Could not read question from row where " +
                    SurveySheetColumn.QUESTION_NUMBER + " = " + questionNumber +
                    " and " + SurveySheetColumn.QUESTION + " = \"" + question + "\"" +
                    " because row does not have " + SurveySheetColumn.QUESTION_TYPE + " column";
            return new Result<>(ActionEnum.FAILURE, error);
        }

        Result<String> questionType = sheetReaderHelper.readString(cellIterator.next());
        if (!questionType.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, questionType.getAdditionalInfo());
        }

        QuestionType questionTypeEnum = QuestionType.fromValue(questionType.getResult());
        if (questionTypeEnum == null) {
            String error = "Could not read question from row where " +
                    SurveySheetColumn.QUESTION_NUMBER + " = " + questionNumber +
                    " and " + SurveySheetColumn.QUESTION + " = \"" + question + "\"" +
                    " because column " + SurveySheetColumn.QUESTION_TYPE + " has invalid value \"" + questionType.getResult() + "\"";
            return new Result<>(ActionEnum.FAILURE, error);
        }

        return new Result<>(questionTypeEnum, ActionEnum.SUCCESS);
    }

    private Result<BaseQuestionRow> readTextQuestion(Integer questionNumber, String question, Boolean mandatory) {
        TextQuestionRow textQuestionRow = new TextQuestionRow(questionNumber, question, mandatory, QuestionType.TEXT);
        return new Result<>(textQuestionRow, ActionEnum.SUCCESS);
    }

    private Result<BaseQuestionRow> readMultipleChoiceQuestion(Iterator<Cell> cellIterator, Integer questionNumber, String question, Boolean mandatory, boolean allowMultipleAnswers) {
        QuestionType type = allowMultipleAnswers ? QuestionType.CHECKBOX : QuestionType.MULTIPLECHOICE;

        Result<List<String>> answers = sheetReaderHelper.readValuesUntilBlank(cellIterator);
        if (!answers.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, answers.getAdditionalInfo());
        }

        ChooseQuestion chooseQuestion = new ChooseQuestion(questionNumber, question, mandatory, type, answers.getResult());
        return new Result<>(chooseQuestion, ActionEnum.SUCCESS);
    }

    private Result<BaseQuestionRow> readScaleQuestion(Iterator<Cell> cellIterator, Integer questionNumber, String question, Boolean mandatory) {
        if (!cellIterator.hasNext()) {
            String error = "Could not read question from row where " +
                    SurveySheetColumn.QUESTION_NUMBER + " = " + questionNumber +
                    " and " + SurveySheetColumn.QUESTION + " = \"" + question + "\"" +
                    " because row does not have " + QuestionType.SCALE + " column with from and to values";
            return new Result<>(ActionEnum.FAILURE, error);
        }

        Result<Integer> from = sheetReaderHelper.readInteger(cellIterator.next());
        if (!from.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, from.getAdditionalInfo());
        }

        if (!cellIterator.hasNext()) {
            String error = "Could not read question from row where " +
                    SurveySheetColumn.QUESTION_NUMBER + " = " + questionNumber +
                    " and " + SurveySheetColumn.QUESTION + " = \"" + question + "\"" +
                    " because row does not have " + QuestionType.SCALE + " column with to value";
            return new Result<>(ActionEnum.FAILURE, error);
        }

        Result<Integer> to = sheetReaderHelper.readInteger(cellIterator.next());
        if (!to.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, from.getAdditionalInfo());
        }

        ScaleQuestionRow scaleQuestionRow = new ScaleQuestionRow(questionNumber, question, mandatory, QuestionType.SCALE, from.getResult(), to.getResult());
        return new Result<>(scaleQuestionRow, ActionEnum.SUCCESS);
    }

}
