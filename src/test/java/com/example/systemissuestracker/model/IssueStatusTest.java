package com.example.systemissuestracker.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IssueStatusTest {

    @Test
    void testValidTransitions() {
        assertTrue(IssueStatus.OPEN.canTransitionTo(IssueStatus.IN_PROGRESS));
        assertTrue(IssueStatus.OPEN.canTransitionTo(IssueStatus.CLOSED));
        assertTrue(IssueStatus.IN_PROGRESS.canTransitionTo(IssueStatus.OPEN));
        assertTrue(IssueStatus.IN_PROGRESS.canTransitionTo(IssueStatus.CLOSED));
    }

    @Test
    void testInvalidTransitionsFromClosed() {
        assertFalse(IssueStatus.CLOSED.canTransitionTo(IssueStatus.OPEN));
        assertFalse(IssueStatus.CLOSED.canTransitionTo(IssueStatus.IN_PROGRESS));
        assertFalse(IssueStatus.CLOSED.canTransitionTo(IssueStatus.CLOSED));
    }

    @Test
    void testInvalidTransitionsToSelf() {
        assertFalse(IssueStatus.OPEN.canTransitionTo(IssueStatus.OPEN));
        assertFalse(IssueStatus.IN_PROGRESS.canTransitionTo(IssueStatus.IN_PROGRESS));
    }
}
