package com.example.systemissuestracker.service.impl;

import com.example.systemissuestracker.model.Issue;
import com.example.systemissuestracker.model.IssueId;
import com.example.systemissuestracker.model.IssueStatus;
import com.example.systemissuestracker.repository.SystemIssueRepository;
import com.example.systemissuestracker.service.SystemIssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SystemIssueServiceImpl implements SystemIssueService {

    private final SystemIssueRepository repository;

    @Autowired
    public SystemIssueServiceImpl(SystemIssueRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Issue createIssue(String description, IssueId parentId) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description is required");
        }

        if (parentId != null && repository.findById(parentId).isEmpty()) {
            throw new IllegalArgumentException("Parent issue does not exist: " + parentId.getShortId());
        }

        Issue issue = Issue.createNew(description, parentId);

        return repository.save(issue);
    }

    @Override
    @Transactional
    public Issue updateStatus(IssueId issueId, IssueStatus newStatus) {
        Issue issue = repository.findById(issueId)
                .orElseThrow(() -> new IllegalArgumentException("Issue not found: " + issueId.getShortId()));

        if (issue.getStatus() == newStatus) {
            return issue;
        }

        if (!issue.getStatus().canTransitionTo(newStatus)) {
            throw new IllegalStateException("Cannot transition from " + issue.getStatus() + " to " + newStatus);
        }

        issue.setStatus(newStatus);

        return repository.save(issue);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Issue> listByStatus(IssueStatus status) {
        return repository.findByStatus(status);
    }
}
