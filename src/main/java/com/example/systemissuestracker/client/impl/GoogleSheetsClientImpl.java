package com.example.systemissuestracker.client.impl;

import com.example.systemissuestracker.client.GoogleSheetsClient;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class GoogleSheetsClientImpl implements GoogleSheetsClient {

    private final Sheets sheetsService;

    @Value("${google.spreadsheet.id}")
    private String spreadsheetId;

    public GoogleSheetsClientImpl(Sheets sheetsService) {
        this.sheetsService = sheetsService;
    }

    @Override
    public List<List<Object>> getRange(String sheet, String range) {
        try {
            String fullRange = sheet + "!" + range;
            ValueRange response = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, fullRange)
                    .execute();
            return response.getValues();
        } catch (IOException e) {
            throw new RuntimeException("Error getting range from Google Sheets", e);
        }
    }

    @Override
    public void appendRow(String sheet, List<Object> row) {
        try {
            ValueRange appendBody = new ValueRange().setValues(Collections.singletonList(row));
            sheetsService.spreadsheets().values()
                    .append(spreadsheetId, sheet, appendBody)
                    .setValueInputOption("USER_ENTERED")
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException("Error appending row to Google Sheets", e);
        }
    }

    @Override
    public void updateRow(String sheet, int rowIndex, List<Object> row) {
        try {
            String range = sheet + "!A" + rowIndex;
            ValueRange updateBody = new ValueRange().setValues(Collections.singletonList(row));
            sheetsService.spreadsheets().values()
                    .update(spreadsheetId, range, updateBody)
                    .setValueInputOption("USER_ENTERED")
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException("Error updating row in Google Sheets", e);
        }
    }

    @Override
    public String getCell(String cell) {
        try {
            ValueRange response = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, cell)
                    .execute();
            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty() || values.get(0).isEmpty()) {
                return null;
            }
            return values.get(0).get(0).toString();
        } catch (IOException e) {
            throw new RuntimeException("Error getting cell from Google Sheets", e);
        }
    }

    @Override
    public void updateCell(String cell, String value) {
        try {
            ValueRange body = new ValueRange().setValues(Collections.singletonList(Collections.singletonList(value)));
            sheetsService.spreadsheets().values()
                    .update(spreadsheetId, cell, body)
                    .setValueInputOption("RAW")
                    .execute();
        } catch (IOException e) {
            throw new RuntimeException("Error updating cell in Google Sheets", e);
        }
    }
}
