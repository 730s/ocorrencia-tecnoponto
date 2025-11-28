package com.tecnoponto.googleSheets.Service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.tecnoponto.googleSheets.Entities.Ocorrencia;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.time.format.DateTimeFormatter;

@Service
public class GoogleSheetsService {

        private static final String APPLICATION_NAME = "Google Sheets API Java";
        private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

        private static final String SPREADSHEET_ID = "1JOiMnKj2B79yOKps2RTdOX6mddoRCsgjg2WVLFsJh8o";
        private static final String RANGE = "Erros de acesso!A1:H1";

        private Sheets getSheetsService() throws IOException, GeneralSecurityException {
                NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

                GoogleCredential credential = GoogleCredential
                                .fromStream(getClass().getClassLoader().getResourceAsStream("credentials.json"))
                                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));

                return new Sheets.Builder(httpTransport, JSON_FACTORY, credential)
                                .setApplicationName(APPLICATION_NAME)
                                .build();
        }

        public void adicionarOcorrencia(Ocorrencia ocorrencia) throws IOException, GeneralSecurityException {

        Sheets sheetsService = getSheetsService();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                ValueRange body = new ValueRange().setValues(
                                Collections.singletonList(
                                                java.util.Arrays.asList(
                                                                ocorrencia.getEmailAcesso(),
                                                                ocorrencia.getUnidadeDeNegocio(),
                                                                ocorrencia.getCnpj(),
                                                                ocorrencia.getDataCriacao().format(formatter),
                                                                ocorrencia.getResponsavel().name().replace("_", " "),
                                                                ocorrencia.getErro().name().replace("_", " "),
                                                                ocorrencia.getStatus().name().replace("_", " "),
                                                                ocorrencia.getPrioridade().name())));

                sheetsService.spreadsheets().values()
                                .append(SPREADSHEET_ID, RANGE, body)
                                .setValueInputOption("RAW")
                                .execute();
        }
}
