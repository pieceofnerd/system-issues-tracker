package com.example.systemissuestracker.model;

import java.time.LocalDateTime;
import java.util.Objects;

public final class Issue {

    private final String id;
    private final String description;
    private final String parentId;
    private final IssueStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;


    public Issue(String id, String description, String parentId, IssueStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.description = description;
        this.parentId = parentId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Issue createNew(String description, String parentId) {
        return new Issue(
                null,
                description.trim(),
                parentId,
                IssueStatus.OPEN,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }


    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getParentId() {
        return parentId;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Issue withId(String newId) {
        return new Issue(newId, this.description, this.parentId, this.status, this.createdAt, this.updatedAt);
    }

    public Issue withStatus(IssueStatus newStatus) {
        return new Issue(this.id, this.description, this.parentId, newStatus, this.createdAt, LocalDateTime.now());
    }

    public Issue withCreatedAt(LocalDateTime newCreatedAt) {
        return new Issue(this.id, this.description, this.parentId, this.status, newCreatedAt, this.updatedAt);
    }

    public Issue withUpdatedAt(LocalDateTime newUpdatedAt) {
        return new Issue(this.id, this.description, this.parentId, this.status, this.createdAt, newUpdatedAt);
    }

    public Issue withParentId(String newParentId) {
        return new Issue(this.id, this.description, newParentId, this.status, this.createdAt, this.updatedAt);
    }

    @Override
    public String toString() {
        return "Issue{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", parentId='" + parentId + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Issue issue = (Issue) o;
        return Objects.equals(id, issue.id) && Objects.equals(description, issue.description) && Objects.equals(parentId, issue.parentId) && status == issue.status && Objects.equals(createdAt, issue.createdAt) && Objects.equals(updatedAt, issue.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, parentId, status, createdAt, updatedAt);
    }
}