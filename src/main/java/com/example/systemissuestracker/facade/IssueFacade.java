package com.example.systemissuestracker.facade;

import com.example.systemissuestracker.model.Issue;
import com.example.systemissuestracker.model.IssueStatus;

import java.util.List;
import java.util.Optional;

public interface IssueFacade {

    Issue create(Issue issue);

    Issue update(Issue issue);

    Optional<Issue> findById(String id);

    List<Issue> findByStatus(IssueStatus status);
}
