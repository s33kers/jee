package jee.mif.bl.services.impl;

import jee.mif.bl.model.ActionEnum;
import jee.mif.bl.model.Result;
import jee.mif.bl.services.AnswerSheetReader;
import jee.mif.bl.services.SheetReaderHelper;
import jee.mif.bl.services.model.AnswerRow;
import jee.mif.bl.services.model.AnswerSheetColumn;
import jee.mif.bl.services.model.SheetName;
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
public class AnswerSheetReaderImpl implements AnswerSheetReader {

    @Autowired
    private SheetReaderHelper sheetReaderHelper;

    private static final List<AnswerSheetColumn> COLUMNS = Arrays.asList(
            AnswerSheetColumn.ANSWER_ID,
            AnswerSheetColumn.QUESTION_NUMBER,
            AnswerSheetColumn.ANSWER
    );

    @Override
    public List<AnswerRow> readAnswerSheet(Sheet answerSheet, StringJoiner joiner) {
        Iterator<Row> rowIterator = answerSheet.rowIterator();
        if (!rowIterator.hasNext()) {
            joiner.add(SheetName.ANSWER.value + " sheet does not have any rows");
            return null;
        }

        Row firstRow = rowIterator.next();
        List<String> columnNames = COLUMNS.stream().map(AnswerSheetColumn::getValue).collect(Collectors.toList());
        Result<Void> columnCheck = sheetReaderHelper.checkColumnNames(firstRow, columnNames);
        if (!columnCheck.isSuccess()) {
            joiner.add(columnCheck.getAdditionalInfo());
            return null;
        }

        return readAnswers(rowIterator, joiner);
    }

    private List<AnswerRow> readAnswers(Iterator<Row> rowIterator, StringJoiner joiner) {
        List<AnswerRow> answers = new ArrayList<>();

        int previousRowNumber = 0;
        while (rowIterator.hasNext()) {
            if (joiner.length() > 8000) {
                break;
            }
            Row row = rowIterator.next();
            if (sheetReaderHelper.isRowEmpty(row)) {
                break;
            } else if (row.getRowNum() - previousRowNumber > 1) {
                break;
            }
            previousRowNumber = row.getRowNum();

            Result<AnswerRow> answer = readAnswer(row.cellIterator());
            if (!answer.isSuccess()) {
                joiner.add(answer.getAdditionalInfo());
            } else {
                answers.add(answer.getResult());
            }
        }

        return answers;
    }

    private Result<AnswerRow> readAnswer(Iterator<Cell> cellIterator) {
        Result<Integer> answerId = readAnswerId(cellIterator);
        if (!answerId.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, answerId.getAdditionalInfo());
        }

        Result<Integer> questionNumber = readQuestionNumber(cellIterator, answerId.getResult());
        if (!questionNumber.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, questionNumber.getAdditionalInfo());
        }

        Result<List<String>> answers = readAnswers(cellIterator, answerId.getResult(), questionNumber.getResult());
        if (!answers.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, answers.getAdditionalInfo());
        }

        AnswerRow answerRow = new AnswerRow(answerId.getResult(), questionNumber.getResult(), answers.getResult());
        return new Result<>(answerRow, ActionEnum.SUCCESS);
    }

    private Result<Integer> readAnswerId(Iterator<Cell> cellIterator) {
        if (!cellIterator.hasNext()) {
            String error = "Could not read answer id because there is no cells";
            return new Result<>(ActionEnum.FAILURE, error);
        }

        return sheetReaderHelper.readInteger(cellIterator.next());
    }

    private Result<Integer> readQuestionNumber(Iterator<Cell> cellIterator, Integer answerId) {
        if (!cellIterator.hasNext()) {
            String error = "Could not rad answer from row where " +
                    AnswerSheetColumn.ANSWER_ID + " = " + answerId +
                    " because row does not have " + AnswerSheetColumn.QUESTION_NUMBER + " column";
            return new Result<>(ActionEnum.FAILURE, error);
        }

        return sheetReaderHelper.readInteger(cellIterator.next());
    }

    private Result<List<String>> readAnswers(Iterator<Cell> cellIterator, Integer answerId, Integer questionNumber) {
        return sheetReaderHelper.readValuesUntilBlank(cellIterator);
    }


}
