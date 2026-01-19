package com.example.systemissuestracker.facade;

import com.example.systemissuestracker.client.GoogleSheetsClient;
import com.example.systemissuestracker.exception.IssueNotFoundException;
import com.example.systemissuestracker.idgenerator.IssueIdGenerator;
import com.example.systemissuestracker.mapper.IssueRowMapper;
import com.example.systemissuestracker.model.Issue;
import com.example.systemissuestracker.model.IssueStatus;
import com.example.systemissuestracker.util.GoogleSheetsConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.anyString;

@ExtendWith(MockitoExtension.class)
class GoogleSheetsIssueFacadeTest {

    @Mock
    private GoogleSheetsClient sheetsClient;
    @Mock
    private IssueRowMapper issueRowMapper;
    @Mock
    private IssueIdGenerator issueIdGenerator;
    @InjectMocks
    private GoogleSheetsIssueFacade facade;

    private static final List<Object> row = Arrays.asList("AD-2", "Test Desc", "AD-1", "OPEN", LocalDateTime.now(), LocalDateTime.now());
    private static final Issue issue = new Issue("AD-2", "Test desc", "AD-1", IssueStatus.OPEN, LocalDateTime.now(), LocalDateTime.now());


    @Test
    void createIssue() {
        List<Object> row = Arrays.asList("AD-3", "Test Desc", "AD-1", "OPEN", LocalDateTime.now().toString(), LocalDateTime.now().toString());
        Issue issueToCreate = Issue.createNew("Test Desc", null);
        when(issueIdGenerator.generateNextId()).thenReturn("AD-3");
        when(issueRowMapper.issueToRow(any(Issue.class))).thenReturn(row);

        Issue result = facade.create(issueToCreate);

        assertNotNull(result.getId());
        assertEquals("AD-3", result.getId());
        verify(issueIdGenerator).generateNextId();
        verify(sheetsClient).appendRow(eq(GoogleSheetsConstants.ISSUES_SHEET_NAME), anyList());
        verify(issueRowMapper).issueToRow(any(Issue.class));
    }

    @Test
    void createIssueThrowsIfIssueIdAlreadyHasId() {
        assertThrows(IllegalArgumentException.class, () -> facade.create(issue));
    }

    @Test
    void updateRowForExistingIssue() {
        List<List<Object>> allData = Collections.singletonList(row);
        when(sheetsClient.getRange(anyString(), anyString())).thenReturn(allData);

        facade.update(issue);

        verify(sheetsClient).updateRow(eq(GoogleSheetsConstants.ISSUES_SHEET_NAME), eq(1), anyList());
        verify(issueRowMapper).issueToRow(any(Issue.class));
    }

    @Test
    void testUpdate_throwsIfIssueNotFound() {
        Issue issue = new Issue("AD-99", "Test desc", null, IssueStatus.OPEN, LocalDateTime.now(), LocalDateTime.now());
        when(sheetsClient.getRange(anyString(), anyString())).thenReturn(Collections.emptyList());
        assertThrows(IssueNotFoundException.class, () -> facade.update(issue));
    }

    @Test
    void findById() {
        List<List<Object>> allData = Collections.singletonList(row);
        when(sheetsClient.getRange(anyString(), anyString())).thenReturn(allData);
        when(issueRowMapper.rowToIssue(row)).thenReturn(issue);

        Optional<Issue> result = facade.findById("AD-2");

        assertTrue(result.isPresent());
        assertEquals(issue, result.get());
    }

    @Test
    void findByIdWhenIssueNotFound() {
        when(sheetsClient.getRange(anyString(), anyString())).thenReturn(Collections.emptyList());

        Optional<Issue> result = facade.findById("AD-99");

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByStatus() {
        List<List<Object>> allData = Collections.singletonList(row);
        when(sheetsClient.getRange(anyString(), anyString())).thenReturn(allData);
        when(issueRowMapper.rowToIssue(row)).thenReturn(issue);

        List<Issue> results = facade.findByStatus(IssueStatus.OPEN);

        assertEquals(1, results.size());
        assertEquals(issue, results.get(0));
    }
}
