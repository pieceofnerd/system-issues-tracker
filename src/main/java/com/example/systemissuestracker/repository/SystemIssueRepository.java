package com.example.systemissuestracker.repository;

import com.example.systemissuestracker.model.Issue;
import com.example.systemissuestracker.model.IssueId;
import com.example.systemissuestracker.model.IssueStatus;

import java.util.List;
import java.util.Optional;

public interface SystemIssueRepository {
    Issue save(Issue issue);

    Optional<Issue> findById(IssueId id);

    List<Issue> findByStatus(IssueStatus status);
}
