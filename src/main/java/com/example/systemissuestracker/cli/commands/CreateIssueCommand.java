package com.example.systemissuestracker.cli.commands;

import com.example.systemissuestracker.model.Issue;
import com.example.systemissuestracker.service.SystemIssueService;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Component
@Command(name = "create", description = "Create a new issue")
public class CreateIssueCommand implements Runnable {

    private final SystemIssueService service;

    public CreateIssueCommand(SystemIssueService service) {
        this.service = service;
    }

    @Option(names = {"--description", "--d"}, required = true, description = "Issue description")
    private String description;

    @Option(names = {"--parent", "--p"}, required = false, description = "Parent issue id")
    private String parentId;

    @Override
    public void run() {
        Issue issue = service.createIssue(description, parentId);

        System.out.println("Created issue: " + issue.getId());
    }
}
