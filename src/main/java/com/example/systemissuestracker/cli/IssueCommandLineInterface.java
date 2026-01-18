package com.example.systemissuestracker.cli;

import com.example.systemissuestracker.cli.commands.CreateIssueCommand;
import com.example.systemissuestracker.cli.commands.ListIssuesCommand;
import com.example.systemissuestracker.cli.commands.UpdateStatusCommand;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(
        name = "issue-tracker",
        mixinStandardHelpOptions = true,
        version = "System Issues Tracker 1.0",
        description = {
                "A CLI tool to track system issues in a Google Sheet.",
                "Use the subcommands 'create', 'update-status', or 'list' to manage issues.",
                "Use '<subcommand> --help' for more information on a specific command."
        },
        descriptionHeading = "%n@|bold,underline Description|@:%n",
        commandListHeading = "%n@|bold,underline Commands|@:%n",
        subcommands = {
                CreateIssueCommand.class,
                UpdateStatusCommand.class,
                ListIssuesCommand.class
        }
)
public class IssueCommandLineInterface implements Runnable {

    @Override
    public void run() {
        System.out.println("\n Please specify a command. Use 'issue-tracker --help' for a list of commands.");
    }

}
