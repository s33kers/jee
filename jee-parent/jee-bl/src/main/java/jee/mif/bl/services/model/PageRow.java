package jee.mif.bl.services.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tadas.
 */
public class PageRow {

    private List<BaseQuestionRow> baseQuestionRows = new ArrayList<>();

    public List<BaseQuestionRow> getBaseQuestionRows() {
        return baseQuestionRows;
    }

    public void setBaseQuestionRows(List<BaseQuestionRow> baseQuestionRows) {
        this.baseQuestionRows = baseQuestionRows;
    }
}
