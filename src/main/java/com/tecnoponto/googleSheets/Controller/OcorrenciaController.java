package com.tecnoponto.googleSheets.Controller;

import com.tecnoponto.googleSheets.Entities.Ocorrencia;
import com.tecnoponto.googleSheets.Enums.Responsavel;
import com.tecnoponto.googleSheets.Service.GoogleSheetsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/ocorrencia")
@CrossOrigin(origins = { "http://127.0.0.1:5500", "http://localhost:5500",
        "https://frontendleo-iota.vercel.app", "https://tecnoponto-ocorrencias.vercel.app",
        "https://tecnoponto-ocorrencias.vercel.app/" }, allowCredentials = "true")
public class OcorrenciaController {

    @Autowired
    private GoogleSheetsService googleSheetsService;

    @PostMapping
    public ResponseEntity<String> criar(@RequestBody Ocorrencia ocorrencia, HttpSession session) {
        try {
            Responsavel responsavel = (Responsavel) session.getAttribute("responsavelAutenticado");

            if (responsavel == null) {
                return ResponseEntity.status(401).body("Erro: usuário não autenticado!");
            }

            ocorrencia.setResponsavel(responsavel);

            googleSheetsService.adicionarOcorrencia(ocorrencia);

            return ResponseEntity.ok("Ocorrência registrada com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }

    @GetMapping("/minhas")
    public Object getMinhasOcorrencias(HttpSession session) {
        try {
            Responsavel responsavel = (Responsavel) session.getAttribute("responsavelAutenticado");

            if (responsavel == null) {
                return "Erro: usuário não autenticado!";
            }

            List<List<Object>> todasOcorrencias = googleSheetsService.getOcorrencias();
            List<Map<String, String>> minhasOcorrencias = new ArrayList<>();

            String nomeResponsavel = responsavel.name().replace("_", " ");

            boolean isHeader = true;
            for (List<Object> row : todasOcorrencias) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                if (row.size() > 4 && row.get(4).toString().equalsIgnoreCase(nomeResponsavel)) {
                    Map<String, String> ocorrenciaMap = new HashMap<>();
                    ocorrenciaMap.put("email", row.size() > 0 ? row.get(0).toString() : "");
                    ocorrenciaMap.put("unidade", row.size() > 1 ? row.get(1).toString() : "");
                    ocorrenciaMap.put("cnpj", row.size() > 2 ? row.get(2).toString() : "");
                    ocorrenciaMap.put("data", row.size() > 3 ? row.get(3).toString() : "");
                    ocorrenciaMap.put("responsavel", row.size() > 4 ? row.get(4).toString() : "");
                    ocorrenciaMap.put("erro", row.size() > 5 ? row.get(5).toString() : "");
                    ocorrenciaMap.put("status", row.size() > 6 ? row.get(6).toString() : "");
                    ocorrenciaMap.put("prioridade", row.size() > 7 ? row.get(7).toString() : "");
                    minhasOcorrencias.add(ocorrenciaMap);
                }
            }

            return minhasOcorrencias;

        } catch (Exception e) {
            return "Erro: " + e.getMessage();
        }
    }

    @GetMapping("/erros")
    public ResponseEntity<List<String>> getErros() {
        try {
            List<String> erros = googleSheetsService.getUniqueErrors();
            return ResponseEntity.ok(erros);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
