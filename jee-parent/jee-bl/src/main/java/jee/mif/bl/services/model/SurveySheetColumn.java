package jee.mif.bl.services.model;

public enum SurveySheetColumn {
    QUESTION_NUMBER("$questionNumber"),
    MANDATORY("$mandatory"),
    QUESTION("$question"),
    QUESTION_TYPE("$questionType"),
    OPTION_LIST("$optionsList");

    private final String value;

    SurveySheetColumn(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
