package jee.mif.bl.services.impl;

import jee.mif.bl.model.ActionEnum;
import jee.mif.bl.model.Result;
import jee.mif.bl.services.SheetReaderHelper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Component
public class SheetReaderHelperImpl implements SheetReaderHelper {

    @Override
    public Result<Void> checkColumnNames(Row row, List<String> names) {
        String sheetName = row.getSheet().getSheetName();
        Iterator<Cell> cellIterator = row.cellIterator();
        Iterator<String> nameIterator = names.iterator();

        while (cellIterator.hasNext() && nameIterator.hasNext()) {
            Cell cell = cellIterator.next();
            String cellValue = cell.getStringCellValue();
            String name = nameIterator.next();
            if (!name.equals(cellValue)) {
                String error = "Invalid column value in sheet " + sheetName + ". Column is named \"" + cellValue +
                        "\" but I expected it to be \"" + name + "\"";
                return new Result<>(ActionEnum.FAILURE, error);
            }
        }

        if (cellIterator.hasNext()) {
            String error = "Too many columns in sheet " + sheetName + ": \"" + cellIterator.next() + "\"";
            while (cellIterator.hasNext()) {
                error += ", \"" + cellIterator.next() + "\"";
            }
            return new Result<>(ActionEnum.FAILURE, error);
        }

        if (nameIterator.hasNext()) {
            String error = "Missing columns in sheet " + sheetName + ": \"" + nameIterator.next() + "\"";
            while (nameIterator.hasNext()) {
                error += ", \"" + nameIterator.next() + "\"";
            }
            return new Result<>(ActionEnum.FAILURE, error);
        }

        return new Result<>(ActionEnum.SUCCESS);
    }

    @Override
    public boolean isRowEmpty(Row row) {
        Cell cell = row.getCell(0);
        return cell == null || CellType.BLANK.equals(cell.getCellTypeEnum());
    }

    @Override
    public boolean isPageSeparator(Row row) {
        Cell cell = row.getCell(0);
        if (cell == null) {
            return false;
        }
        Result<String> value = readAny(cell);
        return value.isSuccess() && "*".equals(value.getResult());
    }

    @Override
    public Result<Integer> readInteger(Cell cell) {
        String sheetName = cell.getSheet().getSheetName();
        String cellAddress = cell.getAddress().formatAsString();

        try {
            int cellValue = (int) cell.getNumericCellValue();
            return new Result<>(cellValue, ActionEnum.SUCCESS);
        } catch (IllegalStateException e) {
            String error = "Could not read integer from cell " + cellAddress + " in sheet " + sheetName +
                    " because cell type is " + cell.getCellTypeEnum().name();
            return new Result<>(ActionEnum.FAILURE, error);
        }
    }

    @Override
    public Result<String> readString(Cell cell) {
        String sheetName = cell.getSheet().getSheetName();
        String cellAddress = cell.getAddress().formatAsString();

        if (CellType.STRING != cell.getCellTypeEnum()) {
            String error = "Could not read string from cell " + cellAddress + " in sheet " + sheetName +
                    " because cell type is " + cell.getCellTypeEnum().name();
            return new Result<>(ActionEnum.FAILURE, error);
        }

        String cellValue = cell.getStringCellValue();
        return new Result<>(cellValue, ActionEnum.SUCCESS);
    }

    @Override
    public Result<Boolean> readBoolean(Cell cell) {
        String sheetName = cell.getSheet().getSheetName();
        String cellAddress = cell.getAddress().formatAsString();

        if (CellType.BOOLEAN != cell.getCellTypeEnum()) {
            String error = "Could not read boolean from cell " + cellAddress + " in sheet " + sheetName +
                    " because cell type is " + cell.getCellTypeEnum().name();
            return new Result<>(ActionEnum.FAILURE, error);
        }

        boolean cellValue = cell.getBooleanCellValue();
        return new Result<>(cellValue, ActionEnum.SUCCESS);
    }

    @Override
    public Result<LocalDateTime> readDate(Cell cell) {
        String sheetName = cell.getSheet().getSheetName();
        String cellAddress = cell.getAddress().formatAsString();

        try {
            Date cellValue = cell.getDateCellValue();
            return new Result<>(LocalDateTime.ofInstant(cellValue.toInstant(), ZoneId.systemDefault()), ActionEnum.SUCCESS);
        } catch(IllegalStateException e) {
            String error = "Could not read date from cell " + cellAddress + " in sheet " + sheetName +
                    " because cell type is wrong (not a date)";
            return new Result<>(ActionEnum.FAILURE, error);
        } catch (NumberFormatException e) {
            String error = "Could not read date from cell " + cellAddress + " in sheet " + sheetName +
                    " because cell format is wrong (not a valid date)";
            return new Result<>(ActionEnum.FAILURE, error);
        }
    }

    @Override
    public Result<String> readAny(Cell cell) {
        switch (cell.getCellTypeEnum()) {
            case STRING: {
                Result<String> value = readString(cell);
                if (!value.isSuccess()) {
                    return new Result<>(ActionEnum.FAILURE, value.getAdditionalInfo());
                }
                return value;
            }
            case NUMERIC: {
                Result<Integer> value = readInteger(cell);
                if (!value.isSuccess()) {
                    return new Result<>(ActionEnum.FAILURE, value.getAdditionalInfo());
                }
                return new Result<>(String.valueOf(value.getResult()), ActionEnum.SUCCESS);
            }
            case BOOLEAN: {
                Result<Boolean> value = readBoolean(cell);
                if (!value.isSuccess()) {
                    return new Result<>(ActionEnum.FAILURE, value.getAdditionalInfo());
                }
                return new Result<>(String.valueOf(value.getResult()), ActionEnum.SUCCESS);
            }
            default: {
                String sheetName = cell.getSheet().getSheetName();
                String cellAddress = cell.getAddress().formatAsString();
                String error = "Could not read any value from cell " + cellAddress + " in sheet " + sheetName +
                        " because cell type is " + cell.getCellTypeEnum().name();
                return new Result<>(ActionEnum.FAILURE, error);
            }
        }
    }

    @Override
    public boolean isBlank(Cell cell) {
        return CellType.BLANK == cell.getCellTypeEnum();
    }

    @Override
    public Result<List<String>> readValuesUntilBlank(Iterator<Cell> cellIterator) {
        List<String> values = new ArrayList<>();

        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            if (isBlank(cell)) {
                break;
            }

            Result<String> value = readAny(cell);
            if (!value.isSuccess()) {
                return new Result<>(ActionEnum.FAILURE, value.getAdditionalInfo());
            }

            values.add(value.getResult());
        }

        return new Result<>(values, ActionEnum.SUCCESS);
    }

}
