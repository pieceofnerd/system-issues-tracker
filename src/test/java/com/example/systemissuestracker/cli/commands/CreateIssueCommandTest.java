package com.example.systemissuestracker.cli.commands;

import com.example.systemissuestracker.model.Issue;
import com.example.systemissuestracker.model.IssueStatus;
import com.example.systemissuestracker.service.SystemIssueService;
import com.example.systemissuestracker.util.GoogleSheetsConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateIssueCommandTest {

    @Mock
    private SystemIssueService issueService;

    @InjectMocks
    private CreateIssueCommand createIssueCommand;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void createIssueWithNoParent() {
        Issue createdIssue = new Issue("AD-1", "Test Description", null, IssueStatus.OPEN, LocalDateTime.now(), LocalDateTime.now());
        when(issueService.createIssue(eq("Test Description"), eq(null))).thenReturn(createdIssue);

        CommandLine cmd = new CommandLine(createIssueCommand);
        cmd.execute("--description", "Test Description");

        verify(issueService).createIssue("Test Description", null);
        assertTrue(outContent.toString().contains("Created issue: " + GoogleSheetsConstants.ID_PREFIX + "-1"));
    }

    @Test
    void createIssueWithParentId() {
        Issue createdIssue = new Issue("AD-1", "Test Description", "AD-0", IssueStatus.OPEN, LocalDateTime.now(), LocalDateTime.now());
        when(issueService.createIssue(eq("Test Description"), eq("AD-0"))).thenReturn(createdIssue);

        CommandLine cmd = new CommandLine(createIssueCommand);
        cmd.execute("--description", "Test Description", "--parent", "AD-0");

        verify(issueService).createIssue("Test Description", "AD-0");
        assertTrue(outContent.toString().contains("Created issue: " + GoogleSheetsConstants.ID_PREFIX + "-1"));
    }

    @Test
    void createIssueWithoutDescription() {
        CommandLine cmd = new CommandLine(createIssueCommand);
        cmd.execute();

        assertTrue(errContent.toString().contains("Missing required option: '--description=<description>'"));
        verifyNoInteractions(issueService);
    }

    @Test
    void createIssueWithInvalidInput() {
        when(issueService.createIssue(anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("Invalid input"));

        CommandLine cmd = new CommandLine(createIssueCommand);
        cmd.execute("--description", "Invalid case", "--parent", "invalid");

        assertTrue(errContent.toString().contains("Invalid input"));
    }
}
