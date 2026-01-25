package com.example.systemissuestracker.cli.commands;

import com.example.systemissuestracker.model.Issue;
import com.example.systemissuestracker.model.IssueStatus;
import com.example.systemissuestracker.service.SystemIssueService;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.List;

@Component
@Command(name = "list", description = "List issues by status")
public class ListIssuesCommand implements Runnable {

    private final SystemIssueService service;

    public ListIssuesCommand(SystemIssueService service) {
        this.service = service;
    }

    @Option(names ={ "--status", "--s"}, required = true, description = "Status (OPEN, IN_PROGRESS, CLOSED)")
    private IssueStatus status;

    @Override
    public void run() {
        List<Issue> issues = service.listByStatus(status);

        if (issues.isEmpty()) {
            System.out.println("No issues found.");
            return;
        }

        issues.forEach(issue -> {
            System.out.println(
                    issue.getId() + " | " +
                            issue.getDescription() + " | " +
                            issue.getStatus() + " | " +
                            issue.getCreatedAt()
            );
        });
    }
}
