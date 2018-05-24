package jee.mif.bl.services;

import jee.mif.bl.model.Result;
import jee.mif.bl.services.model.SurveyHeader;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.StringJoiner;

public interface HeaderSheetReader {

    SurveyHeader readHeaderSheet(Sheet headerSheet, StringJoiner joiner);

}
