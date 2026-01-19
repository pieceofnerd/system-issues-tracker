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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListIssuesCommandTest {

    @Mock
    private SystemIssueService issueService;

    @InjectMocks
    private ListIssuesCommand listIssuesCommand;

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
    void issuesSuccessfullyFounded() {
        Issue issue = new Issue("AD-1", "Test Issue", null, IssueStatus.OPEN, LocalDateTime.now(), LocalDateTime.now());
        List<Issue> issues = Collections.singletonList(issue);
        when(issueService.listByStatus(eq(IssueStatus.OPEN))).thenReturn(issues);

        CommandLine cmd = new CommandLine(listIssuesCommand);
        cmd.execute("--status", "OPEN");

        verify(issueService).listByStatus(IssueStatus.OPEN);
        assertTrue(outContent.toString().contains("AD-1"));
        assertTrue(outContent.toString().contains("Test Issue"));
        assertTrue(outContent.toString().contains(IssueStatus.OPEN.toString()));
    }

    @Test
    void issuesSuccessfullyNotFounded() {
        when(issueService.listByStatus(eq(IssueStatus.CLOSED))).thenReturn(Collections.emptyList());

        CommandLine cmd = new CommandLine(listIssuesCommand);
        cmd.execute("--status", "CLOSED");

        verify(issueService).listByStatus(IssueStatus.CLOSED);
        assertTrue(outContent.toString().contains("No issues found"));
    }

    @Test
    void executeListIssuesWithMissingStatus() {
        CommandLine cmd = new CommandLine(listIssuesCommand);
        cmd.execute();

        assertTrue(errContent.toString().contains("Missing required option: '--status=<status>'"));
        verifyNoInteractions(issueService);
    }

    @Test
    void executeListIssuesByInvalidStatus() {
        CommandLine cmd = new CommandLine(listIssuesCommand);
        cmd.execute("--status", "INVALID");

        assertTrue(errContent.toString().contains("Invalid value for option '--status'"));
        verifyNoInteractions(issueService);
    }

}
