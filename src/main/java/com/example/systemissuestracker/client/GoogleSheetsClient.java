package com.example.systemissuestracker.client;

import java.util.List;

public interface GoogleSheetsClient {
    List<List<Object>> getRange(String sheet, String range);

    void appendRow(String sheet, List<Object> row);

    void updateRow(String sheet, int rowIndex, List<Object> row);

    String getCell(String cell);

    void updateCell(String cell, String value);
}
