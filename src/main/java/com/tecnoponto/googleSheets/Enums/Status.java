package com.tecnoponto.googleSheets.Enums;

public enum Status {
    EM_ABERTO,
    AJUSTADO,
    DESENVOLVIMENTO;

    @com.fasterxml.jackson.annotation.JsonCreator
    public static Status fromString(String value) {
        if (value == null) {
            return null;
        }
        return Status.valueOf(value.trim().replace(" ", "_").toUpperCase());
    }
}
