package jee.mif.bl.services;

import jee.mif.bl.model.Result;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

public interface SheetReaderHelper {

    Result<Void> checkColumnNames(Row row, List<String> names);

    boolean isRowEmpty(Row row);

    boolean isPageSeparator(Row row);

    Result<Integer> readInteger(Cell cell);

    Result<String> readString(Cell cell);

    Result<Boolean> readBoolean(Cell cell);

    Result<LocalDateTime> readDate(Cell cell);

    Result<String> readAny(Cell cell);

    boolean isBlank(Cell cell);

    Result<List<String>> readValuesUntilBlank(Iterator<Cell> cellIterator);

}
