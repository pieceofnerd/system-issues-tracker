package com.example.systemissuestracker.cli;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

import java.util.Scanner;

@Component
public class PicocliRunner implements CommandLineRunner {

    private final IFactory factory;
    private final IssueCommandLineInterface rootCommand;

    public PicocliRunner(IssueCommandLineInterface rootCommand, IFactory factory) {
        this.factory = factory;
        this.rootCommand = rootCommand;
    }

    @Override
    public void run(String... args) {
        if (args.length > 0) {
            new CommandLine(rootCommand, factory).execute(args);
            return;
        }
        runInteractiveShell();
    }

    private void runInteractiveShell() {
        Scanner scanner = new Scanner(System.in);
        String line;

        System.out.println("Welcome to the System Issue Tracker Interactive CLI.");
        System.out.println("Type a command (e.g., 'create --description \"Test\"') and press Enter.");
        System.out.println("Type 'help' (or '--help') for a list of commands and global options, or 'exit' to quit.");

        while (true) {
            System.out.print("issue-tracker> ");
            line = scanner.nextLine();

            if (line == null || "exit".equalsIgnoreCase(line.trim())) {
                System.out.println("Exiting.");
                break;
            }

            if (line.trim().isEmpty()) {
                continue;
            }

            if ("help".equalsIgnoreCase(line.trim())) {
                line = "--help";
            }

            String[] cmdArgs = line.trim().split("\\s+");
            new CommandLine(rootCommand, factory).execute(cmdArgs);
        }
    }
}