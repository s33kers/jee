package jee.mif.bl.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface SurveyImporter {
    /**
     * Import survey from input stream.
     * Has bigger memory footprint than passing File object,
     * because it loads whole document into memory.
     * Caller should close passed input stream himself.
     * @param xlsxInputStream XLSX document input stream
     * @return imported survey
     */
    void importXlsx(byte[] file, Long userId, Long surveyId);

}
