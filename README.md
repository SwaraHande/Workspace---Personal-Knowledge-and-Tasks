# Notion-Inspired Desktop Application (Java Swing + PostgreSQL)

A full-featured, production-grade desktop productivity application inspired by Notion, built in native **Java 21 / Swing** with persistent storage in **PostgreSQL**.

---

## 1. Features

- **Collapsible Sidebar & Nested Page Tree**:
  - Hierarchical pages and subpages with real-time expand/collapse.
  - Workspace selector and creation.
  - Favorites section and Quick Access (Search, Templates, Settings, Trash).
  - Context menu on pages: Add Subpage, Duplicate, Rename, Toggle Favorite, Move to Trash.
- **Rich Block-Based Document Editor**:
  - Full support for block types: Paragraph, Heading 1, Heading 2, Heading 3, Bulleted List, Numbered List, To-do Checklist (with dynamic cross-out), Toggle List, Quote, Callout, Monospace Code Block (with one-click clipboard copy), Divider, and Embedded Database.
  - **Slash (`/`) Commands Menu**: Type `/` in any block to trigger a searchable block-selection popup.
  - Seamless keyboard flow: `Enter` to create the next block, `Backspace` on empty block to delete and refocus, `Up`/`Down` arrows to navigate between blocks.
- **Notion-Style Relational Databases**:
  - **Table View**: Relational columns (Text, Select tags, Date, Checkbox), in-line editing, column-click sorting, and row addition.
  - **Board (Kanban) View**: Cards grouped dynamically by status (`Todo`, `In Progress`, `Done`), with priority badges, due dates, and status progression buttons (`⇄`).
  - **Calendar View**: Monthly calendar displaying scheduled items and due dates on date cells.
- **Debounced Autosave & PostgreSQL Persistence**:
  - Changes are debounced (1500ms) to prevent database hammering and executed in background `SwingWorker` threads to keep the Swing Event Dispatch Thread (EDT) fast and responsive.
  - Live status indicator: `Saved`, `Saving...`, `Save Error`.
  - Immediate save shortcut: `Ctrl + S`.
- **Global Quick Search (`Ctrl + P` / `Ctrl + F`)**:
  - Instant debounced modal searching through page titles and block content with keyboard selection.
- **Template Gallery**:
  - Pre-built templates: *Meeting Notes*, *Daily Journal*, *Project Tracker*, *Task List*, *Study Notes*.
- **Trash & Archive**:
  - Soft-delete pages with full restoration or permanent deletion.
- **Themes & Modern UI**:
  - Premium Dark Theme (Obsidian) and Crisp Light Theme built on FlatLaf with rounded corners, custom scrollbars, and modern typography.
- **Import & Export**:
  - Export pages to **Markdown (.md)**, **HTML (.html)**, and **Plain Text (.txt)**.
  - Export databases to **CSV (.csv)**.

---

## 2. Technology Stack

- **Language:** Java 21+ (Compatible with modern LTS & Java SE 26)
- **GUI Framework:** Java Swing with FlatLaf 3.5+ look & feel
- **Database:** PostgreSQL with HikariCP connection pooling
- **Build System:** Apache Maven
- **Serialization:** Jackson Databind 2.17+
- **Logging:** SLF4J + Logback
- **Testing:** JUnit 5

---

## 3. Database Setup & Configuration

### Configuration (`.env`)
The application reads configuration from environment variables or a local `.env` file:

```properties
DB_HOST=localhost
DB_PORT=5432
DB_NAME=workspace_db
DB_USER=postgres
DB_PASSWORD=root
DB_MAX_POOL_SIZE=10
APP_THEME=DARK
APP_AUTOSAVE_INTERVAL_MS=1500
```

### PostgreSQL Connection
You can verify your database connection in PowerShell using:
```powershell
& "D:\postgres\bin\psql.exe" -h localhost -p 5432 -U postgres -d workspace_db
```

The application automatically checks and executes all schema migrations (`V1__initial_schema.sql` and `V2__seed_data.sql`) upon startup.

---

## 4. Building and Running

### Build with Maven
To compile the project and run all tests:
```powershell
& "D:\apache-maven-3.9.6\bin\mvn.cmd" clean test
```

### Build Executable Fat JAR
To package the shaded executable JAR containing all dependencies:
```powershell
& "D:\apache-maven-3.9.6\bin\mvn.cmd" clean package
```
This generates `target/notion-workspace-app-1.0.0.jar`.

### Run the Application
You can run the application directly using either:

1. **Java Executable JAR**:
```powershell
java -jar target/notion-workspace-app-1.0.0.jar
```

2. **Maven Exec Plugin**:
```powershell
& "D:\apache-maven-3.9.6\bin\mvn.cmd" compile exec:java -Dexec.mainClass="com.workspace.app.app.Main"
```

---

## 5. Keyboard Shortcuts

| Shortcut | Action |
|---|---|
| `Ctrl + N` | Create a new page |
| `Ctrl + P` / `Ctrl + F` | Quick global search modal |
| `Ctrl + S` | Force save immediately |
| `/` (in empty block) | Open Slash Command popup |
| `Enter` | Create next block below |
| `Backspace` (on empty block) | Delete block and focus previous |
| `Up` / `Down` | Navigate between blocks or search results |

---

## 6. Troubleshooting

- **SCRAM-SHA-256 Authentication Error**: Ensure your `.env` contains the correct `DB_PASSWORD`. By default, PostgreSQL on local installs requires the password configured during installation (e.g. `root`).
- **Database `workspace_db` does not exist**: Create it with `psql -U postgres -c "CREATE DATABASE workspace_db;"`.
- **UI Font Scaling on High-DPI screens**: FlatLaf automatically handles system scaling on Windows 10/11.
