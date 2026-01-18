# System Issues Tracker CLI

This project is a Command Line Interface (CLI) application built with Spring Boot that allows you to track system issues using a Google Sheet as a backend data store. It's designed with a layered architecture to be modular, extensible, and easily adaptable to different persistence mechanisms.

##  Features

*   **Issue Creation**: Create new issues with a description and an optional parent issue ID.
*   **Status Updates**: Update the status of existing issues (OPEN, IN_PROGRESS, CLOSED).
*   **Issue Listing**: List issues filtered by their status.
*   **Auto-generated IDs**: Issues are assigned unique, incremental IDs (e.g., AD-1, AD-2).
*   **Interactive CLI**: Run commands interactively from a shell-like interface.
*   **Dockerized**: Easily build and run the application as a Docker container.
*   **Storage-Agnostic Core**: The business logic is decoupled from the Google Sheets implementation, allowing easy switching to other backends (e.g., Jira, a relational database).

##  Architecture Overview

*   **Model Layer**: Defines core domain objects like `Issue` and `IssueStatus`.
*   **Mapper Layer**: `IssueRowMapper` handles the bidirectional mapping between `Issue` objects and `List<Object>` (Google Sheet rows).
*   **Client Layer**: `GoogleSheetsClient` provides a low-level abstraction over the raw Google Sheets API calls.
*   **ID Generation Layer**: `IssueIdGenerator` is responsible for generating new issue IDs and managing the auto-increment counter persistently in a Google Sheet cell.
*   **Facade Layer (`IssueFacade`)**: This is the core abstraction for data persistence. `SystemIssueService` depends on this interface. `GoogleSheetsIssueFacade` is the concrete implementation that adapts operations for Google Sheets, orchestrating the client, mapper, and ID generator.
*   **Service Layer (`SystemIssueService`)**: Contains the business logic, validation rules, and status transition rules. It is completely storage-agnostic, depending only on the `IssueFacade` interface.
*   **CLI Layer (`PicocliRunner`, Command Classes)**: Uses the Picocli library to define commands and parse command-line arguments. It interacts with the `SystemIssueService`.

## 🚀 Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:

*   **Java Development Kit (JDK) 17 or higher**
*   **Gradle 8.x** (typically bundled with Spring Boot projects as `gradlew`)
*   **Docker Desktop** (if you plan to run the application as a Docker container)

### ☁️ Google Cloud & Sheets Setup

The application uses Google Sheets for data storage. You need to configure a Google Cloud Project and Google Sheet:

1.  **Create a Google Cloud Project**:
    *   Go to the [Google Cloud Console](https://console.cloud.google.com/).
    *   Create a new project.

2.  **Enable the Google Sheets API**:
    *   In your Google Cloud project, navigate to "APIs & Services" > "Library".
    *   Search for "Google Sheets API" and enable it.

3.  **Create a Service Account**:
    *   Go to "APIs & Services" > "Credentials".
    *   Click "+ CREATE CREDENTIALS" and select "Service account".
    *   Give it a name (e.g., `sheets-issue-tracker`), grant it "Editor" permissions for now, and complete the creation steps.

4.  **Download a JSON Key**:
    *   On the Credentials page, find your new service account in the "Service Accounts" list.
    *   Click on the service account's name.
    *   Go to the "KEYS" tab, click "ADD KEY", and choose "Create new key".
    *   Select "JSON" as the key type and click "CREATE". A JSON file containing your service account's private key will be downloaded.
    *   **IMPORTANT**: Treat this file like a password. Do **NOT** commit it to your version control system (e.g., Git).

5.  **Place the Key in Your Project**:
    *   Rename the downloaded JSON file to `google-creds.json`.
    *   Place this `google-creds.json` file into your project's `src/main/resources` directory.

6.  **Create and Share Your Google Sheet**:
    *   Create a new Google Sheet (e.g., named "IssueTrackerData").
    *   Find your service account's email address. This can be found in the downloaded `google-creds.json` file (under the `client_email` field) or in the Google Cloud Console under the service account details.
    *   Click the "Share" button in your Google Sheet and share it with that service account email address, granting it **"Editor"** access.
    *   Note your **Spreadsheet ID**. This is the long alphanumeric string found in the URL of your spreadsheet: `https://docs.google.com/spreadsheets/d/YOUR_SPREADSHEET_ID_HERE/edit`.

7.  **Set up Google Sheet Tabs**:
    *   Ensure your Google Sheet has two tabs (sheets) with the exact names:
        *   **`Issues`**: This sheet will store your issue data. It's recommended to have a header row with columns like `ID`, `Description`, `ParentID`, `Status`, `CreatedAt`, `UpdatedAt`.
        *   **`Metadata`**: This sheet is used internally to store the last generated issue ID. The application will use cell `A1` on this sheet.

### ⚙️ Local Setup and Configuration

1. **Configure `application.properties`**:
    *   Open `src/main/resources/application.properties`.
    *   Add or update the following line, replacing `YOUR_SPREADSHEET_ID_GOES_HERE` with the Spreadsheet ID you noted earlier:
        ```properties
        spring.application.name=system-issues-tracker
        google.spreadsheet.id=YOUR_SPREADSHEET_ID_GOES_HERE
        ```

2**Build the Project**:
    ```bash
    ./gradlew clean bootJar
    ```
    This will create an executable JAR file in the `build/libs` directory.

### 🚀 CLI Usage

You can run the application in two modes:

#### 1. Interactive Shell Mode

Run the JAR without any arguments to enter the interactive shell:

```bash
java -jar build/libs/system-issues-tracker-0.0.1-SNAPSHOT.jar
```

You will see a welcome message and a prompt:

```
Welcome to the System Issue Tracker Interactive CLI.
Type a command (e.g., 'create --description "My issue"') and press Enter.
Type 'help' (or '--help') for a list of commands and global options, or 'exit' to quit.
issue-tracker>
```

From the `issue-tracker>` prompt, you can type commands:

*   **Create Issue**:
    ```
    create --description "Fix login flow on mobile" --parentId AD-1
    ```
*   **Update Issue Status**:
    ```
    update-status --id AD-1 --status IN_PROGRESS
    ```
*   **List Issues**:
    ```
    list --status OPEN
    ```
*   **Get Help**:
    ```
    help
    create --help
    ```
*   **Exit**:
    ```
    exit
    ```

#### 2. One-shot Command Mode

Execute a single command directly from your terminal:

```bash
java -jar build/libs/system-issues-tracker-0.0.1-SNAPSHOT.jar create --description "Implement user profile page"
java -jar build/libs/system-issues-tracker-0.0.1-SNAPSHOT.jar update-status --id AD-2 --status CLOSED
java -jar build/libs/system-issues-tracker-0.0.1-SNAPSHOT.jar list --status IN_PROGRESS
java -jar build/libs/system-issues-tracker-0.0.1-SNAPSHOT.jar --help
```

### 🐳 Docker Usage

You can containerize and run the CLI using Docker:

1.  **Build the Docker Image**:
    Navigate to the project's root directory (where the `Dockerfile` is located) and run:
    ```bash
    docker build -t issue-tracker-cli .
    ```

2.  **Run a Command (One-shot mode)**:
    ```bash
    docker run issue-tracker-cli create -d "Refactor API authentication"
    docker run issue-tracker-cli list -s OPEN
    ```

3.  **Run Interactively (Shell mode)**:
    ```bash
    docker run -it issue-tracker-cli
    ```
    This will drop you into the interactive shell within the container.

    **IMPORTANT**: The `Dockerfile` copies your `src/main/resources/google-creds.json` directly into the image for convenience. For production environments, it is strongly recommended to manage sensitive files like `google-creds.json` more securely, for example, by mounting it as a Docker volume at runtime:
    ```bash
    docker run -it -v /path/to/your/local/google-creds.json:/app/src/main/resources/google-creds.json issue-tracker-cli
    ```
