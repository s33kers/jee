package jee.mif.bl.services;

import jee.mif.bl.services.model.PageRow;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;
import java.util.StringJoiner;

public interface QuestionSheetReader {

    List<PageRow> readQuestionSheet(Sheet questionSheet, StringJoiner joiner);

}
