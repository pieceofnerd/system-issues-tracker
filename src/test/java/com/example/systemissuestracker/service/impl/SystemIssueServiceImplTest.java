package com.example.systemissuestracker.service.impl;

import com.example.systemissuestracker.facade.IssueFacade;
import com.example.systemissuestracker.model.Issue;
import com.example.systemissuestracker.model.IssueStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SystemIssueServiceImplTest {

    @Mock private IssueFacade issueFacade;
    @InjectMocks private SystemIssueServiceImpl systemIssueService;

    private Issue issue;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now().withNano(0);
        issue = new Issue("AD-2", "Test Issue", "AD-1", IssueStatus.OPEN, now, now);
    }

    @Test
    void createIssueWithoutParentId() {
        Issue createdIssue = issue.withId("AD-2");
        when(issueFacade.create(any(Issue.class))).thenReturn(createdIssue);

        Issue result = systemIssueService.createIssue("New Issue", null);

        assertNotNull(result);
        assertEquals("AD-2", result.getId());
        verify(issueFacade).create(argThat(issue -> issue.getDescription().equals("New Issue") && issue.getParentId() == null));
        verify(issueFacade, never()).findById(anyString());
    }

    @Test
    void createIssueWithParentId() {

        when(issueFacade.findById("AD-1")).thenReturn(Optional.of(new Issue("AD-1", "Parrent issue", null, IssueStatus.OPEN, LocalDateTime.now(), LocalDateTime.now())));
        when(issueFacade.create(any(Issue.class))).thenReturn(issue);

        Issue result = systemIssueService.createIssue("New Issue", "AD-1"); // User input '0'

        assertNotNull(result);
        assertEquals("AD-2", result.getId());
        verify(issueFacade).findById("AD-1");
        verify(issueFacade).create(argThat(issue -> issue.getDescription().equals("New Issue") && issue.getParentId().equals("AD-1")));
    }

    @Test
    void createIssueWithoutDescription() {
        assertThrows(IllegalArgumentException.class, () -> systemIssueService.createIssue("", null));
        verifyNoInteractions(issueFacade);
    }

    @Test
    void createIssueWithNoExistingParentId() {
        when(issueFacade.findById("AD-99")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> systemIssueService.createIssue("Desc", "AD-99"));
        verify(issueFacade).findById("AD-99");
        verify(issueFacade, never()).create(any(Issue.class));
    }

    @Test
    void updateStatusSuccessfully() {
        Issue updatedIssue = issue.withStatus(IssueStatus.IN_PROGRESS);

        when(issueFacade.findById("AD-2")).thenReturn(Optional.of(issue));
        when(issueFacade.update(any(Issue.class))).thenReturn(updatedIssue);

        Issue result = systemIssueService.updateStatus("AD-2", IssueStatus.IN_PROGRESS);

        assertNotNull(result);
        assertEquals(IssueStatus.IN_PROGRESS, result.getStatus());
        verify(issueFacade).findById("AD-2");
        verify(issueFacade).update(argThat(issue -> issue.getId().equals("AD-2") && issue.getStatus() == IssueStatus.IN_PROGRESS));
    }

    @Test
    void updateStatusWithTheSameStatus() {
        when(issueFacade.findById("AD-2")).thenReturn(Optional.of(issue));

        Issue result = systemIssueService.updateStatus("AD-2", IssueStatus.OPEN);

        assertNotNull(result);
        assertEquals(IssueStatus.OPEN, result.getStatus());
        verify(issueFacade).findById("AD-2");
    }

    @Test
    void updateStatusIssueNotFound() {
        when(issueFacade.findById("AD-99")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> systemIssueService.updateStatus("AD-99", IssueStatus.CLOSED));
        verify(issueFacade).findById("AD-99");
        verify(issueFacade, never()).update(any(Issue.class));
    }

    @Test
    void updateStatusInvalidStatusTransition() {
        Issue closedIssue = issue.withStatus(IssueStatus.CLOSED);
        when(issueFacade.findById("AD-2")).thenReturn(Optional.of(closedIssue));

        assertThrows(IllegalStateException.class, () -> systemIssueService.updateStatus("AD-2", IssueStatus.OPEN));
        verify(issueFacade).findById("AD-2");
        verify(issueFacade, never()).update(any(Issue.class));
    }

    @Test
    void listByStatus() {
        List<Issue> expectedList = Collections.singletonList(issue);
        when(issueFacade.findByStatus(IssueStatus.OPEN)).thenReturn(expectedList);

        List<Issue> result = systemIssueService.listByStatus(IssueStatus.OPEN);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(issue, result.get(0));
        verify(issueFacade).findByStatus(IssueStatus.OPEN);
    }
}
