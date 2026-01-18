package com.example.systemissuestracker.idgenerator;

import com.example.systemissuestracker.client.GoogleSheetsClient;
import com.example.systemissuestracker.util.GoogleSheetsConstants;
import org.springframework.stereotype.Component;

@Component
public class GoogleSheetsIssueIdGenerator implements IssueIdGenerator {

    private final GoogleSheetsClient sheetsClient;

    public GoogleSheetsIssueIdGenerator(GoogleSheetsClient sheetsClient) {
        this.sheetsClient = sheetsClient;
    }

    @Override
    public String generateNextId() {
        int newIdNumber = readMaxId() + 1;
        String newId = GoogleSheetsConstants.ID_PREFIX + "-" + newIdNumber;
        updateMaxId(newIdNumber);
        return newId;
    }

    private int readMaxId() {
        String cellValue = sheetsClient.getCell(GoogleSheetsConstants.MAX_ID_CELL);
        if (cellValue == null) {
            return 0;
        }
        try {
            return Integer.parseInt(cellValue);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void updateMaxId(int newMaxId) {
        sheetsClient.updateCell(GoogleSheetsConstants.MAX_ID_CELL, String.valueOf(newMaxId));
    }
}
