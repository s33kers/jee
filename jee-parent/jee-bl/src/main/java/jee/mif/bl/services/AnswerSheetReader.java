package jee.mif.bl.services;

import jee.mif.bl.model.Result;
import jee.mif.bl.services.model.AnswerRow;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;
import java.util.StringJoiner;

public interface AnswerSheetReader {

    List<AnswerRow> readAnswerSheet(Sheet answerSheet, StringJoiner joiner);

}
