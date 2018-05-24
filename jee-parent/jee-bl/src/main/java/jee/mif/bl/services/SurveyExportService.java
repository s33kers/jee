package jee.mif.bl.services;

import jee.mif.bl.model.Result;
import jee.mif.model.survey.Survey;

import java.io.OutputStream;

public interface SurveyExportService {

    Result<Void> exportXlsx(Survey survey, OutputStream outputStream);

}
