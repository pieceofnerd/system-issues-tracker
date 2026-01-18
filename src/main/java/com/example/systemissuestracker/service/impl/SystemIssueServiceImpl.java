package com.example.systemissuestracker.service.impl;

import com.example.systemissuestracker.facade.IssueFacade;
import com.example.systemissuestracker.model.Issue;
import com.example.systemissuestracker.model.IssueStatus;
import com.example.systemissuestracker.service.SystemIssueService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemIssueServiceImpl implements SystemIssueService {

    private final IssueFacade issueFacade;

    public SystemIssueServiceImpl(IssueFacade issueFacade) {
        this.issueFacade = issueFacade;
    }

    @Override
    public Issue createIssue(String description, String parentId) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description is required");
        }

        if (parentId != null && issueFacade.findById(parentId).isEmpty()) {
            throw new IllegalArgumentException("Parent issue does not exist: " + parentId);
        }

        Issue newIssue = Issue.createNew(description, parentId);
        return issueFacade.create(newIssue);
    }

    @Override
    public Issue updateStatus(String issueId, IssueStatus newStatus) {
        if (issueId == null) {
            throw new IllegalArgumentException("Issue ID is required");
        }

        Issue existingIssue = issueFacade.findById(issueId)
                .orElseThrow(() -> new IllegalArgumentException("Issue not found: " + issueId));

        if (existingIssue.getStatus() == newStatus) {
            return existingIssue;
        }

        if (!existingIssue.getStatus().canTransitionTo(newStatus)) {
            throw new IllegalStateException("Cannot transition from " + existingIssue.getStatus() + " to " + newStatus);
        }

        Issue updatedIssue = existingIssue.withStatus(newStatus);
        return issueFacade.update(updatedIssue);
    }

    @Override
    public List<Issue> listByStatus(IssueStatus status) {
        return issueFacade.findByStatus(status);
    }
}
