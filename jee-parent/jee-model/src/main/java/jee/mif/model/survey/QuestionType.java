package jee.mif.model.survey;

/**
 * Created by Tadas.
 */
public enum QuestionType {
    TEXT,
    CHECKBOX,
    MULTIPLECHOICE,
    SCALE;

    public static QuestionType fromValue(String value) {
        for (QuestionType type : values()) {
            if (type.name().equals(value.toUpperCase())) {
                return type;
            }
        }
        return null;
    }
}
