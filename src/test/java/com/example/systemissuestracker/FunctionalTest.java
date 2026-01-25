package com.example.systemissuestracker;

import com.example.systemissuestracker.cli.PicocliRunner;
import com.example.systemissuestracker.client.GoogleSheetsClient;
import com.example.systemissuestracker.model.IssueStatus;
import com.example.systemissuestracker.util.GoogleSheetsConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(args = "--help")
@ActiveProfiles("test")
class FunctionalTest {

    @Autowired
    private PicocliRunner picocliRunner;

    @MockBean
    private GoogleSheetsClient googleSheetsClient;

    private List<List<Object>> issuesSheetData;
    private AtomicInteger maxIdCounter;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() {
        issuesSheetData = new ArrayList<>();
        issuesSheetData.add(Arrays.asList("ID", "Description", "ParentID", "Status", "CreatedAt", "UpdatedAt"));

        maxIdCounter = new AtomicInteger(0);

        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        mockGoogleSheetsClient();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        reset(googleSheetsClient);
    }

    private void mockGoogleSheetsClient() {

        when(googleSheetsClient.getRange(eq(GoogleSheetsConstants.ISSUES_SHEET_NAME), anyString()))
                .thenAnswer(invocation -> issuesSheetData);


        doAnswer(invocation -> {
            List<Object> row = invocation.getArgument(1);
            if (row.size() < 6) {
                while (row.size() < 6) row.add("");
            }
            row.set(4, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            row.set(5, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            issuesSheetData.add(row);
            return null;
        }).when(googleSheetsClient).appendRow(eq(GoogleSheetsConstants.ISSUES_SHEET_NAME), anyList());


        doAnswer(invocation -> {
            int rowIndex = invocation.getArgument(1);
            List<Object> row = invocation.getArgument(2);
            if (rowIndex > 0 && rowIndex <= issuesSheetData.size()) {

                if (row.size() < 6) {
                    while (row.size() < 6) row.add("");
                }
                row.set(5, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

                issuesSheetData.set(rowIndex - 1, row);
            }
            return null;
        }).when(googleSheetsClient).updateRow(eq(GoogleSheetsConstants.ISSUES_SHEET_NAME), anyInt(), anyList());


        when(googleSheetsClient.getCell(eq(GoogleSheetsConstants.MAX_ID_CELL)))
                .thenAnswer(invocation -> String.valueOf(maxIdCounter.get()));


        doAnswer(invocation -> {
            String value = invocation.getArgument(1);
            maxIdCounter.set(Integer.parseInt(value));
            return null;
        }).when(googleSheetsClient).updateCell(eq(GoogleSheetsConstants.MAX_ID_CELL), anyString());
    }

    @Test
    void executeFullFunctionalFlow() {
        String description1 = "Functional Test Issue 1";
        String[] createArgs1 = {"create", "--description", description1};
        picocliRunner.run(createArgs1);


        assertTrue(outContent.toString().contains("Created issue: " + GoogleSheetsConstants.ID_PREFIX + "-1"));
        outContent.reset();
        assertEquals(2, issuesSheetData.size());
        assertTrue(issuesSheetData.get(1).contains(GoogleSheetsConstants.ID_PREFIX + "-1"));
        assertTrue(issuesSheetData.get(1).contains(description1));
        assertTrue(issuesSheetData.get(1).contains(IssueStatus.OPEN.name()));
        assertEquals(1, maxIdCounter.get());


        String description2 = "Functional Test Issue 2 (Child)";
        String parentId2 = "AD-1";
        String[] createArgs2 = {"create", "--description", description2, "--parent", parentId2};
        picocliRunner.run(createArgs2);

        assertTrue(outContent.toString().contains("Created issue: " + GoogleSheetsConstants.ID_PREFIX + "-2"));
        outContent.reset();

        assertEquals(3, issuesSheetData.size());
        assertTrue(issuesSheetData.get(2).contains(GoogleSheetsConstants.ID_PREFIX + "-2"));
        assertTrue(issuesSheetData.get(2).contains(description2));
        assertTrue(issuesSheetData.get(2).contains(parentId2));
        assertEquals(2, maxIdCounter.get());


        String[] listArgs1 = {"list", "--status", "OPEN"};
        picocliRunner.run(listArgs1);


        assertTrue(outContent.toString().contains(GoogleSheetsConstants.ID_PREFIX + "-1"));
        assertTrue(outContent.toString().contains(GoogleSheetsConstants.ID_PREFIX + "-2"));
        outContent.reset();


        String issueIdToUpdate = "AD-1";
        String newStatus = "IN_PROGRESS";
        String[] updateArgs = {"update-status", "--id", issueIdToUpdate, "--status", newStatus};
        picocliRunner.run(updateArgs);

        assertTrue(outContent.toString().contains("Updated issue " + issueIdToUpdate + " to " + newStatus));
        outContent.reset();

        assertTrue(issuesSheetData.get(1).contains(newStatus));

        String[] listArgs2 = {"list", "--status", "IN_PROGRESS"};
        picocliRunner.run(listArgs2);

        assertTrue(outContent.toString().contains(GoogleSheetsConstants.ID_PREFIX + "-1"));
        assertFalse(outContent.toString().contains(GoogleSheetsConstants.ID_PREFIX + "-2"));
        outContent.reset();


        String[] listArgs3 = {"list", "--status", "OPEN"};
        picocliRunner.run(listArgs3);

        assertFalse(outContent.toString().contains(GoogleSheetsConstants.ID_PREFIX + "-1"));
        assertTrue(outContent.toString().contains(GoogleSheetsConstants.ID_PREFIX + "-2"));
        outContent.reset();
    }

    @Test
    void createIssueWithMissingDescription() {
        String[] createArgs = {"create"};
        picocliRunner.run(createArgs);
        assertTrue(errContent.toString().contains("Missing required option: '--description=<description>'"));
    }

    @Test
    void updateStatusWithInvalidStatus() {
        String[] updateArgs = {"update-status", "--id", "AD-1", "--status", "INVALID"};
        picocliRunner.run(updateArgs);
        assertTrue(errContent.toString().contains("Invalid value for option '--status'"));
    }

    @Test
    void listIssuesByNothing() {
        String[] listArgs = {"list"};
        picocliRunner.run(listArgs);
        assertTrue(errContent.toString().contains("Missing required option: '--status=<status>'"));
    }
}
