package com.tecnoponto.googleSheets.Entities;

import com.tecnoponto.googleSheets.Enums.Erro;
import com.tecnoponto.googleSheets.Enums.Prioridade;
import com.tecnoponto.googleSheets.Enums.Responsavel;
import com.tecnoponto.googleSheets.Enums.Status;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class Ocorrencia {

    private String cnpj;
    private String emailAcesso;
    private String unidadeDeNegocio;
    private Responsavel responsavel;
    private Erro erro;
    private LocalDateTime dataCriacao = LocalDateTime.now();
    private Prioridade prioridade;
    private Status status;

}
