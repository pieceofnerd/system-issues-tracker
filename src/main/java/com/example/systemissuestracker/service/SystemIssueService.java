package com.example.systemissuestracker.service;

import com.example.systemissuestracker.model.Issue;
import com.example.systemissuestracker.model.IssueId;
import com.example.systemissuestracker.model.IssueStatus;

import java.util.List;

public interface SystemIssueService {
    Issue createIssue(String description, IssueId parentId);

    Issue updateStatus(IssueId issueId, IssueStatus newStatus);

    List<Issue> listByStatus(IssueStatus status);
}