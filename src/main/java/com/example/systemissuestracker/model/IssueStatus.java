package com.example.systemissuestracker.model;

import java.util.Map;
import java.util.Set;

public enum IssueStatus {
    OPEN,
    IN_PROGRESS,
    CLOSED;

    private static final Map<IssueStatus, Set<IssueStatus>> ALLOWED_TRANSITIONS = Map.of(
            OPEN, Set.of(IN_PROGRESS, CLOSED),
            IN_PROGRESS, Set.of(OPEN, CLOSED),
            CLOSED, Set.of()
    );

    public boolean canTransitionTo(IssueStatus nextState) {
        return ALLOWED_TRANSITIONS.getOrDefault(this, Set.of()).contains(nextState);
    }
}
