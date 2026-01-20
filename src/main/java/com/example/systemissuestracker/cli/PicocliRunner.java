package com.example.systemissuestracker.cli;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PicocliRunner implements CommandLineRunner {

    private final IFactory factory;
    private final IssueCommandLineInterface rootCommand;
    private static final Pattern ARG_PATTERN = Pattern.compile("([\"'])(?:(?=(\\\\?))\\2.)*?\\1|\\S+");

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
        try (Scanner scanner = new Scanner(System.in)) {
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

                String[] cmdArgs = splitArgs(line.trim());
                new CommandLine(rootCommand, factory).execute(cmdArgs);
            }
        }
    }

    private String[] splitArgs(String line) {
        List<String> args = new ArrayList<>();

        Matcher matcher = ARG_PATTERN.matcher(line);

        while (matcher.find()) {
            String arg = matcher.group();
            if (arg.startsWith("\"") && arg.endsWith("\"")) {
                arg = arg.substring(1, arg.length() - 1);
            }
            args.add(arg);
        }
        return args.toArray(new String[0]);
    }
}