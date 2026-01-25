package com.example.systemissuestracker.idgen;

import com.example.systemissuestracker.client.GoogleSheetsClient;
import com.example.systemissuestracker.exception.CorruptedIdException;
import com.example.systemissuestracker.idgenerator.GoogleSheetsIssueIdGenerator;
import com.example.systemissuestracker.util.GoogleSheetsConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleSheetsIssueIdGeneratorTest {

    @Mock
    private GoogleSheetsClient sheetsClient;

    private GoogleSheetsIssueIdGenerator idGenerator;

    @BeforeEach
    void setUp() {
        idGenerator = new GoogleSheetsIssueIdGenerator(sheetsClient);
    }

    @Test
    void generateNextIdWhenNoPreviousIdExist() {
        when(sheetsClient.getCell(GoogleSheetsConstants.MAX_ID_CELL)).thenReturn(null);

        String newId = idGenerator.generateNextId();

        assertEquals("AD-1", newId);
        verify(sheetsClient, times(1)).updateCell(GoogleSheetsConstants.MAX_ID_CELL, "1");
    }

    @Test
    void generateNextIdWhenPreviousIdExist() {
        when(sheetsClient.getCell(GoogleSheetsConstants.MAX_ID_CELL)).thenReturn("2");

        String newId = idGenerator.generateNextId();

        assertEquals("AD-3", newId);
        verify(sheetsClient, times(1)).updateCell(GoogleSheetsConstants.MAX_ID_CELL, "3");
    }

    @Test
    void generateNextIdWhenPreviousIdHasWrongFormat() {
        when(sheetsClient.getCell(GoogleSheetsConstants.MAX_ID_CELL)).thenReturn("Garbage");

        var ex = assertThrows(CorruptedIdException.class, () -> idGenerator.generateNextId());

        assertEquals("Corrupted ID counter in Metadata sheet. Expected integer but got: Garbage", ex.getMessage());
        verify(sheetsClient, never()).updateCell(any(), any());
    }
}

