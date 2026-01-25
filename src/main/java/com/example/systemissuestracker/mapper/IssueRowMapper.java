package com.example.systemissuestracker.mapper;

import com.example.systemissuestracker.model.Issue;
import com.example.systemissuestracker.model.IssueStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class IssueRowMapper {

    public List<Object> issueToRow(Issue issue) {
        List<Object> row = new ArrayList<>();
        row.add(issue.getId());
        row.add(issue.getDescription());
        row.add(issue.getParentId() != null ? issue.getParentId() : "");
        row.add(issue.getStatus().name());
        row.add(issue.getCreatedAt() != null ? issue.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "");
        row.add(issue.getUpdatedAt() != null ? issue.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "");
        return row;
    }

    public Issue rowToIssue(List<Object> row) {
        if (row.size() < 6) {
            return null;
        }

        String id = row.get(0).toString();
        String description = row.get(1).toString();
        String parentId = row.size() > 2 && row.get(2) != null ? row.get(2).toString() : null;
        if (parentId != null && parentId.isEmpty()) parentId = null;

        IssueStatus status = IssueStatus.valueOf(row.get(3).toString().toUpperCase());
        LocalDateTime createdAt = LocalDateTime.parse(row.get(4).toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime updatedAt = LocalDateTime.parse(row.get(5).toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        return new Issue(id, description, parentId, status, createdAt, updatedAt);
    }
}
