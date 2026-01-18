package com.example.systemissuestracker.model;

import java.util.UUID;

public final class IssueId {
    private final UUID id;
    private final String shortId;

    public IssueId() {
        this.id = UUID.randomUUID();
        this.shortId = id.toString().substring(0, 5);
    }

    public UUID getId() {
        return id;
    }

    public String getShortId() {
        return shortId;
    }
}
