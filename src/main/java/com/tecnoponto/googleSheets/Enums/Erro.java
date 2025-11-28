package com.tecnoponto.googleSheets.Enums;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum Erro {
    SEM_UNIDADE_DE_NEGOCIO,
    SISTEMA_EXPIRADO,
    LOGIN_NAO_ENCONTRADO,
    SISTEMA_INATIVO,
    SEM_BH,
    BH_INCOMPLETO,
    BASE_SEM_INFORMACOES;

    @com.fasterxml.jackson.annotation.JsonCreator
    public static Erro fromString(String value) {
        if (value == null) {
            return null;
        }
        return Erro.valueOf(value.trim().replace(" ", "_").toUpperCase());
    }
}
