package com.tecnoponto.googleSheets.Controller;

import com.tecnoponto.googleSheets.Entities.Ocorrencia;
import com.tecnoponto.googleSheets.Enums.Responsavel;
import com.tecnoponto.googleSheets.Service.GoogleSheetsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ocorrencia")
@CrossOrigin(
        origins = {"http://127.0.0.1:5500", "http://localhost:5500"},
        allowCredentials = "true"
)
public class OcorrenciaController {

    @Autowired
    private GoogleSheetsService googleSheetsService;

    @PostMapping
    public String criar(@RequestBody Ocorrencia ocorrencia, HttpSession session) {
        try {
            Responsavel responsavel = (Responsavel) session.getAttribute("responsavelAutenticado");

            if (responsavel == null) {
                return "Erro: usuário não autenticado!";
            }

            ocorrencia.setResponsavel(responsavel);

            googleSheetsService.adicionarOcorrencia(ocorrencia);

            return "Ocorrência registrada com sucesso!";
        } catch (Exception e) {
            return "Erro: " + e.getMessage();
        }
    }
}
