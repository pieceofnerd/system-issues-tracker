package com.example.systemissuestracker.service;

import com.example.systemissuestracker.model.Issue;
import com.example.systemissuestracker.model.IssueStatus;

import java.util.List;

public interface SystemIssueService {
    Issue createIssue(String description, String parentId);

    Issue updateStatus(String issueId, IssueStatus newStatus);

    List<Issue> listByStatus(IssueStatus status);
}