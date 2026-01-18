package com.example.systemissuestracker.facade;

import com.example.systemissuestracker.client.GoogleSheetsClient;
import com.example.systemissuestracker.idgenerator.IssueIdGenerator;
import com.example.systemissuestracker.mapper.IssueRowMapper;
import com.example.systemissuestracker.model.Issue;
import com.example.systemissuestracker.model.IssueStatus;
import com.example.systemissuestracker.util.GoogleSheetsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class GoogleSheetsIssueFacade implements IssueFacade {

    private final GoogleSheetsClient sheetsClient;
    private final IssueRowMapper issueRowMapper;
    private final IssueIdGenerator issueIdGenerator;

    @Autowired
    public GoogleSheetsIssueFacade(
            GoogleSheetsClient sheetsClient,
            IssueRowMapper issueRowMapper,
            IssueIdGenerator issueIdGenerator
    ) {
        this.sheetsClient = sheetsClient;
        this.issueRowMapper = issueRowMapper;
        this.issueIdGenerator = issueIdGenerator;
    }

    @Override
    public Issue create(Issue issue) {
        if (issue.getId() != null) {
            throw new IllegalArgumentException("Cannot create an issue with a pre-assigned ID. ID must be null.");
        }

        Issue newIssue = issue
                .withId(issueIdGenerator.generateNextId())
                .withCreatedAt(LocalDateTime.now())
                .withUpdatedAt(LocalDateTime.now());

        sheetsClient.appendRow(GoogleSheetsConstants.ISSUES_SHEET_NAME, issueRowMapper.issueToRow(newIssue));
        return newIssue;
    }

    @Override
    public Issue update(Issue issue) {
        if (issue.getId() == null) {
            throw new IllegalArgumentException("Cannot update an issue without an ID.");
        }

        Optional<Integer> rowIndexOpt = findRowIndexOfIssue(issue.getId());
        if (rowIndexOpt.isEmpty()) {
            throw new RuntimeException("Issue not found with ID: " + issue.getId());
        }
        int rowIndex = rowIndexOpt.get();

        Issue updatedIssue = issue.withUpdatedAt(LocalDateTime.now());

        sheetsClient.updateRow(GoogleSheetsConstants.ISSUES_SHEET_NAME, rowIndex, issueRowMapper.issueToRow(updatedIssue));
        return updatedIssue;
    }

    @Override
    public Optional<Issue> findById(String id) {
        List<List<Object>> allData = getAllSheetData();
        if (allData == null || allData.isEmpty()) {
            return Optional.empty();
        }
        return allData.stream()
                .filter(row -> !row.isEmpty() && row.get(0).toString().equals(id))
                .findFirst()
                .map(issueRowMapper::rowToIssue);
    }

    @Override
    public List<Issue> findByStatus(IssueStatus status) {
        List<List<Object>> allData = getAllSheetData();
        if (allData == null || allData.isEmpty()) {
            return Collections.emptyList();
        }
        return allData.stream()
                .filter(row -> row.size() > 3 && row.get(3).toString().equalsIgnoreCase(status.name()))
                .map(issueRowMapper::rowToIssue)
                .collect(Collectors.toList());
    }

    private Optional<Integer> findRowIndexOfIssue(String id) {
        List<List<Object>> allData = getAllSheetData();
        if (allData == null) return Optional.empty();
        for (int i = 0; i < allData.size(); i++) {
            if (!allData.get(i).isEmpty() && allData.get(i).get(0).toString().equals(id)) {
                return Optional.of(i + 1);
            }
        }
        return Optional.empty();
    }

    private List<List<Object>> getAllSheetData() {
        return sheetsClient.getRange(GoogleSheetsConstants.ISSUES_SHEET_NAME, "A:F");
    }
}
