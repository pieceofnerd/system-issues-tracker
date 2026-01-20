package com.example.systemissuestracker.cli.commands;

import com.example.systemissuestracker.model.Issue;
import com.example.systemissuestracker.model.IssueStatus;
import com.example.systemissuestracker.service.SystemIssueService;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Component
@Command(name = "update-status", description = "Update issue status")
public class UpdateStatusCommand implements Runnable {

    private final SystemIssueService service;

    public UpdateStatusCommand(SystemIssueService service) {
        this.service = service;
    }

    @Option(names = "--id", required = true, description = "Issue id")
    private String id;

    @Option(names ={ "--status", "--s"}, required = true, description = "New status (OPEN, IN_PROGRESS, CLOSED)")
    private IssueStatus status;

    @Override
    public void run() {
        Issue updated = service.updateStatus(id, status);
        System.out.println("Updated issue " + updated.getId() + " to " + updated.getStatus());
    }
}
