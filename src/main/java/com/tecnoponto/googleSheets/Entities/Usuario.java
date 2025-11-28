package com.tecnoponto.googleSheets.Entities;

import lombok.Data;

@Data
public class Usuario {
    private String nome;
    private String senha = "pass";
}
