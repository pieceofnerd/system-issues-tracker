package com.example.systemissuestracker.cli.commands;

import com.example.systemissuestracker.model.Issue;
import com.example.systemissuestracker.model.IssueStatus;
import com.example.systemissuestracker.service.SystemIssueService;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateStatusCommandTest {

    @Mock
    private SystemIssueService issueService;

    @InjectMocks
    private UpdateStatusCommand updateStatusCommand;

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
    void updateStatusSuccessfully() {
        String ISSUE_ID_TO_UPDATE = "AD-1";
        String NEW_STATUS_IN_PROGRESS = "IN_PROGRESS";

        Issue updatedIssue = new Issue(ISSUE_ID_TO_UPDATE, "Desc", null, IssueStatus.IN_PROGRESS, LocalDateTime.now(), LocalDateTime.now());
        when(issueService.updateStatus(eq(ISSUE_ID_TO_UPDATE), eq(IssueStatus.IN_PROGRESS))).thenReturn(updatedIssue);

        CommandLine cmd = new CommandLine(updateStatusCommand);
        cmd.execute("--id", ISSUE_ID_TO_UPDATE, "--status", NEW_STATUS_IN_PROGRESS);

        verify(issueService).updateStatus(ISSUE_ID_TO_UPDATE, IssueStatus.IN_PROGRESS);
        assertTrue(outContent.toString().contains("Updated issue " + ISSUE_ID_TO_UPDATE + " to " + NEW_STATUS_IN_PROGRESS));
    }

    @Test
    void updateStatusWithoutIssueId() {
        CommandLine cmd = new CommandLine(updateStatusCommand);
        cmd.execute("--status", "OPEN");

        assertTrue(errContent.toString().contains("Missing required option: '--id=<id>'"));
        verifyNoInteractions(issueService);
    }

    @Test
    void updateStatusWithoutNewStatus() {
        CommandLine cmd = new CommandLine(updateStatusCommand);
        cmd.execute("--id", "AD-1");

        assertTrue(errContent.toString().contains("Missing required option: '--status=<status>'"));
        verifyNoInteractions(issueService);
    }

    @Test
    void updateStatusWithInvalidStatus() {
        CommandLine cmd = new CommandLine(updateStatusCommand);
        cmd.execute("--id", "AD-1", "--status", "INVALID_STATUS");

        assertTrue(errContent.toString().contains("Invalid value for option '--status'"));
        verifyNoInteractions(issueService);
    }

    @Test
    void updateStatusOfNoExistedIssue() {
        when(issueService.updateStatus(anyString(), any(IssueStatus.class))).thenThrow(new IllegalArgumentException("Issue not found"));

        CommandLine cmd = new CommandLine(updateStatusCommand);
        cmd.execute("--id", "AD-1", "--status", "CLOSED");

        assertTrue(errContent.toString().contains("Issue not found"));
    }
}
