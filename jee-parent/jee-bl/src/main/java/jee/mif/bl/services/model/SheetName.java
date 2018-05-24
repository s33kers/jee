package jee.mif.bl.services.model;

public enum SheetName {
    HEADER("Header"),
    SURVEY("Survey"),
    ANSWER("Answer");

    public final String value;

    SheetName(String value) {
        this.value = value;
    }
}