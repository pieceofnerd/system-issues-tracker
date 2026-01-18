package com.example.systemissuestracker.model;

import java.time.LocalDateTime;

public class Issue {

    private IssueId id;
    private String description;
    private IssueId parentId;
    private IssueStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Issue(IssueId id, String description, IssueId parentId, IssueStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.description = description;
        this.parentId = parentId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Issue createNew(String description, IssueId parentId) {
        return new Issue(
                new IssueId(),
                description.trim(),
                parentId,
                IssueStatus.OPEN,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }
}
