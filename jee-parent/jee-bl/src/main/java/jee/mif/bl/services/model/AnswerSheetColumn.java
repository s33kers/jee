package jee.mif.bl.services.model;

public enum AnswerSheetColumn {
    ANSWER_ID("$answerID"),
    QUESTION_NUMBER("$questionNumber"),
    ANSWER("$answer");

    private final String value;

    AnswerSheetColumn(String value) {
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
