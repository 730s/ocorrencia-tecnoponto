package com.tecnoponto.googleSheets.Service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.tecnoponto.googleSheets.Entities.Ocorrencia;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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

                String envCredentials = System.getenv("GOOGLE_CREDENTIALS_JSON");
                InputStream credentialsStream;

                if (envCredentials != null && !envCredentials.isEmpty()) {
                        credentialsStream = new ByteArrayInputStream(envCredentials.getBytes(StandardCharsets.UTF_8));
                } else {
                        credentialsStream = getClass().getClassLoader().getResourceAsStream("credentials.json");
                }

                if (credentialsStream == null) {
                        throw new IOException(
                                        "Credentials not found in environment variable GOOGLE_CREDENTIALS_JSON or classpath credentials.json");
                }

                GoogleCredential credential = GoogleCredential
                                .fromStream(credentialsStream)
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
                                                                ocorrencia.getDataCriacao().minusHours(3)
                                                                                .format(formatter),
                                                                ocorrencia.getResponsavel().name().replace("_", " "),
                                                                ocorrencia.getErro(),
                                                                ocorrencia.getStatus().name().replace("_", " "),
                                                                ocorrencia.getPrioridade().name())));

                com.google.api.services.sheets.v4.model.AppendValuesResponse response = sheetsService.spreadsheets()
                                .values()
                                .append(SPREADSHEET_ID, RANGE, body)
                                .setValueInputOption("RAW")
                                .execute();

                String updatedRange = response.getUpdates().getUpdatedRange();
                int rowStart = 0;
                if (updatedRange != null && updatedRange.contains("!")) {
                        String rangePart = updatedRange.split("!")[1];
                        String rowStr = rangePart.replaceAll("\\D+", "");
                        int colonIndex = rangePart.indexOf(':');
                        if (colonIndex > 0) {
                                String startCell = rangePart.substring(0, colonIndex);
                                rowStr = startCell.replaceAll("\\D+", "");
                        } else {
                                rowStr = rangePart.replaceAll("\\D+", "");
                        }

                        if (!rowStr.isEmpty()) {
                                rowStart = Integer.parseInt(rowStr) - 1; // 0-indexed
                        }
                }

                if (rowStart > 0) {
                        int sheetId = getSheetId(sheetsService, "Erros de acesso"); // Fixed: removed "!"

                        java.util.List<String> allErrors = getUniqueErrors();
                        java.util.List<ConditionValue> conditionValues = new java.util.ArrayList<>();
                        for (String err : allErrors) {
                                conditionValues.add(new ConditionValue().setUserEnteredValue(err));
                        }

                        BooleanCondition condition = new BooleanCondition()
                                        .setType("ONE_OF_LIST")
                                        .setValues(conditionValues);

                        DataValidationRule rule = new DataValidationRule()
                                        .setCondition(condition)
                                        .setShowCustomUi(true)
                                        .setStrict(true);

                        GridRange validationRange = new GridRange()
                                        .setSheetId(sheetId)
                                        .setStartRowIndex(1)
                                        .setStartColumnIndex(5)
                                        .setEndColumnIndex(6);

                        Request validationRequest = new Request()
                                        .setSetDataValidation(new SetDataValidationRequest()
                                                        .setRange(validationRange)
                                                        .setRule(rule));

                        BatchUpdateSpreadsheetRequest batchRequest = new BatchUpdateSpreadsheetRequest()
                                        .setRequests(java.util.Arrays.asList(validationRequest));

                        sheetsService.spreadsheets().batchUpdate(SPREADSHEET_ID, batchRequest).execute();
                }
        }

        private int getSheetId(Sheets service, String sheetTitle) throws IOException {
                Spreadsheet spreadsheet = service.spreadsheets().get(SPREADSHEET_ID).execute();
                for (Sheet sheet : spreadsheet.getSheets()) {
                        if (sheet.getProperties().getTitle().equals(sheetTitle)) {
                                return sheet.getProperties().getSheetId();
                        }
                }
                return 0;
        }

        public java.util.List<java.util.List<Object>> getOcorrencias() throws IOException, GeneralSecurityException {
                Sheets sheetsService = getSheetsService();
                ValueRange response = sheetsService.spreadsheets().values()
                                .get(SPREADSHEET_ID, "Erros de acesso!A:H")
                                .execute();
                return response.getValues();
        }

        public java.util.List<String> getUniqueErrors() throws IOException, GeneralSecurityException {
                Sheets sheetsService = getSheetsService();
                ValueRange response = sheetsService.spreadsheets().values()
                                .get(SPREADSHEET_ID, "Erros de acesso!F:F") // Column F is index 5 (Erro)
                                .execute();

                java.util.List<java.util.List<Object>> values = response.getValues();
                java.util.Set<String> uniqueErrors = new java.util.HashSet<>();

                for (com.tecnoponto.googleSheets.Enums.Erro erro : com.tecnoponto.googleSheets.Enums.Erro.values()) {
                        uniqueErrors.add(erro.name().replace("_", " "));
                }

                if (values != null) {
                        for (java.util.List<Object> row : values) {
                                if (row != null && !row.isEmpty()) {
                                        uniqueErrors.add(row.get(0).toString());
                                }
                        }
                }

                uniqueErrors.remove("Erro");

                return new java.util.ArrayList<>(uniqueErrors);
        }
}
