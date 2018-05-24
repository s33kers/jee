package jee.mif.bl.services.impl;

import jee.mif.bl.model.ActionEnum;
import jee.mif.bl.model.Result;
import jee.mif.bl.services.HeaderSheetReader;
import jee.mif.bl.services.SheetReaderHelper;
import jee.mif.bl.services.model.SheetName;
import jee.mif.bl.services.model.SurveyHeader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Iterator;
import java.util.StringJoiner;

@Component
public class HeaderSheetReaderImpl implements HeaderSheetReader {

    @Autowired
    private SheetReaderHelper sheetReaderHelper;

    @Override
    public SurveyHeader readHeaderSheet(Sheet headerSheet, StringJoiner joiner) {
        Iterator<Row> rowIterator = headerSheet.rowIterator();
        if (!rowIterator.hasNext()) {
            joiner.add(SheetName.HEADER.value + " sheet does not have any rows");
            return null;
        }

        return readProperties(rowIterator, joiner);
    }

    private SurveyHeader readProperties(Iterator<Row> rowIterator, StringJoiner joiner) {
        SurveyHeader surveyHeader = new SurveyHeader();

        Result<String> title = readNextRowValue(rowIterator, "$name");
        if (!title.isSuccess()) {
            joiner.add(title.getAdditionalInfo());
        } else {
            if (title.getResult().length() > 255) {
                joiner.add("Title is too long. Maximum length is 255 characters");
            } else {
                surveyHeader.setTitle(title.getResult());
            }
        }

        Result<String> description = readNextRowValue(rowIterator, "$description");
        if (!description.isSuccess()) {
            joiner.add( description.getAdditionalInfo());
        } else {
            if (description.getResult().length() > 4000) {
                joiner.add("Description is too long. Maximum length is 4000 characters");
            } else {
                surveyHeader.setDescription(description.getResult());
            }
        }

        Result<LocalDateTime> validate = readNextRowDateValue(rowIterator, "$validate");
        if (!validate.isSuccess()) {
            joiner.add(validate.getAdditionalInfo());
        } else {
            surveyHeader.setValidDate(validate.getResult().toLocalDate());
        }

        Result<String> resultsPublic = readNextRowValue(rowIterator, "$public");
        if (!resultsPublic.isSuccess()) {
            joiner.add(resultsPublic.getAdditionalInfo());
        } else {
            if ("YES".equals(resultsPublic.getResult())) {
                surveyHeader.setResultsPublic(true);
            } else if ("NO".equals(resultsPublic.getResult())) {
                surveyHeader.setResultsPublic(false);
            } else {
                joiner.add(SheetName.HEADER + " parameter \"$public\" must be \"YES\" or \"NO\" (currently it is \"" + resultsPublic.getResult() + "\")");
            }
        }

        return surveyHeader;
    }

    private Result<String> readNextRowValue(Iterator<Row> rowIterator, String name) {
        if (!rowIterator.hasNext()) {
            return new Result<>(ActionEnum.FAILURE, "Sheet " + SheetName.HEADER + " does not contain row with parameter \"" + name + "\"");
        }

        Row row = rowIterator.next();
        if (sheetReaderHelper.isRowEmpty(row)) {
            return new Result<>(ActionEnum.FAILURE, "Sheet " + SheetName.HEADER + " does not contain row with parameter \"" + name + "\"");
        }

        return readRowValue(row, name);
    }

    private Result<String> readRowValue(Row row, String name) {
        Cell nameCell = row.getCell(0);
        Cell valueCell = row.getCell(1);
        if (nameCell == null || valueCell == null) {
            return new Result<>(ActionEnum.FAILURE, "Sheet " + SheetName.HEADER + " row #" + row.getRowNum() + " does not have parameter");
        }

        Result<String> nameResult = sheetReaderHelper.readAny(nameCell);
        if (!nameResult.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, "Sheet " + SheetName.HEADER + " row #" + row.getRowNum() + " has invalid name");
        }

        Result<String> valueResult = sheetReaderHelper.readAny(valueCell);
        if (!valueResult.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, "Sheet " + SheetName.HEADER + " row #" + row.getRowNum() + " has invalid value");
        }

        if (!name.equals(nameResult.getResult())) {
            return new Result<>(ActionEnum.FAILURE, "Sheet " + SheetName.HEADER + " row #" + row.getRowNum() + " has invalid name \"" + nameResult.getResult() + "\", should be \"" + name + "\"");
        }

        return new Result<>(valueResult.getResult(), ActionEnum.SUCCESS);
    }

    private Result<LocalDateTime> readNextRowDateValue(Iterator<Row> rowIterator, String name) {
        if (!rowIterator.hasNext()) {
            return new Result<>(ActionEnum.FAILURE, "Sheet " + SheetName.HEADER + " does not contain row with parameter \"" + name + "\"");
        }

        Row row = rowIterator.next();
        if (sheetReaderHelper.isRowEmpty(row)) {
            return new Result<>(ActionEnum.FAILURE, "Sheet " + SheetName.HEADER + " does not contain row with parameter \"" + name + "\"");
        }

        return readRowDateValue(row, name);
    }

    private Result<LocalDateTime> readRowDateValue(Row row, String name) {
        Cell nameCell = row.getCell(0);
        Cell valueCell = row.getCell(1);
        if (nameCell == null || valueCell == null) {
            return new Result<>(ActionEnum.FAILURE, "Sheet " + SheetName.HEADER + " row #" + row.getRowNum() + " does not have parameter");
        }

        Result<String> nameResult = sheetReaderHelper.readAny(nameCell);
        if (!nameResult.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, "Sheet " + SheetName.HEADER + " row #" + row.getRowNum() + " has invalid name");
        }

        Result<LocalDateTime> valueResult = sheetReaderHelper.readDate(valueCell);
        if (!valueResult.isSuccess()) {
            return new Result<>(ActionEnum.FAILURE, valueResult.getAdditionalInfo());
        }

        if (!name.equals(nameResult.getResult())) {
            return new Result<>(ActionEnum.FAILURE, "Sheet " + SheetName.HEADER + " row #" + row.getRowNum() + " has invalid name \"" + nameResult.getResult() + "\", should be \"" + name + "\"");
        }

        return new Result<>(valueResult.getResult(), ActionEnum.SUCCESS);
    }

}
