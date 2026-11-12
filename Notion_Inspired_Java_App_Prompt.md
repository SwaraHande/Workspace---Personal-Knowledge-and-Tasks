# Project: Build a Full-Featured Notion-Inspired Desktop Application in Java



Act as a senior Java desktop application engineer, software architect, UI/UX designer, and PostgreSQL database engineer. Build a complete, production-quality desktop productivity application inspired by Notion. The application must reproduce the core features, interaction patterns, visual quality, and user experience of Notion as closely as reasonably possible while using original branding.



Do not build a simple notes application or a static UI prototype. Build a functional application with a real database, persistent data, interactive components, and a polished desktop experience.



## 1. Technology Stack



Use the following technologies:



- **Language:** Java 21 or a compatible modern LTS Java version.

- **GUI:** Java Swing as the primary UI framework, with AWT where necessary.

- **Database:** PostgreSQL.

- **Database connectivity:** JDBC using the PostgreSQL JDBC driver.

- **Build system:** Maven.

- **Rich-text editing:** Implement a reusable document editor with support for formatted text, headings, lists, links, checklists, code blocks, quotes, and other block types. Use Swing components or a compatible Java library where practical.

- **Icons:** Use a consistent open-source icon set or original icons.

- **Testing:** JUnit 5.

- **Logging:** SLF4J with an appropriate logging implementation.



Do not use JavaFX, Electron, React, a web-based frontend, or a browser wrapper. The entire primary application interface must be a native Java Swing/AWT desktop application.



Use a modular architecture with clear separation between UI, business logic, persistence, and data models.



## 2. Design Philosophy and UI/UX



Create a desktop interface inspired by Notion's clean, flexible, document-oriented workspace.



The UI should include:



- A collapsible left sidebar.

- A central page editor.

- A contextual top toolbar.

- Breadcrumb navigation.

- A command/search interface.

- Context menus and slash commands.

- Drag-and-drop organization.

- Resizable panels.

- Smooth and responsive interactions.

- Keyboard shortcuts.

- Dark themes.

- Consistent spacing, typography, borders, icons, hover states, and selection states.



Use a restrained neutral color palette, subtle separators, rounded corners, and a clear visual hierarchy. Make the interface feel modern, refined, and professional.



Avoid the default unstyled Swing appearance. Customize Swing components using reusable UI components, custom painting where necessary, appropriate layout managers, and a centralized theme system.



The application must adapt to window resizing and support common desktop resolutions.



### Main application layout



Create the following structure:



1\. **Left sidebar**

   - Workspace selector.

   - Workspace members or account area.

   - Quick access to Home, Search, Settings, and Trash.

   - Favorites section.

   - Private pages section.

   - Shared pages section.

   - Nested page tree.

   - Expand/collapse controls.

   - Add-page button.

   - Context menus for pages and folders.

   - Sidebar collapse/expand control.



2\. **Top navigation**

   - Breadcrumbs showing the current page hierarchy.

   - Back and forward navigation.

   - Search button.

   - Share button.

   - Favorite toggle.

   - Page options menu.

   - Last-edited information.



3\. **Main editor**

   - Editable page title.

   - Page icon and optional cover image.

   - Rich-text/block editor.

   - Inline formatting toolbar.

   - Slash-command menu.

   - Drag handles for blocks.

   - Add-block controls.

   - Page comments or discussion panel.

   - Autosave status indicator.



4\. **Optional right panel**

   - Page discussion.

   - Page details.

   - Activity history.

   - Backlinks and related pages.



## 3. Core Features



### A. Workspaces and navigation



Implement:



- Create, rename, and delete workspaces.

- Create nested pages and subpages.

- Move pages between parents.

- Reorder pages.

- Expand and collapse page trees.

- Favorite and unfavorite pages.

- Search for pages.

- Duplicate pages.

- Archive and restore pages.

- Trash and permanently delete pages.

- Breadcrumb-based navigation.

- Recently visited pages.

- Recent activity history.



Deleting a parent page must not silently destroy its descendants. Provide clear confirmation and appropriate archival or cascading behavior.



### B. Rich document editor



Implement a block-based document editor with the following block types:



- Paragraph.

- Heading 1, Heading 2, Heading 3.

- Bulleted list.

- Numbered list.

- To-do checklist.

- Toggle block.

- Quote.

- Divider.

- Code block with syntax highlighting where feasible.

- Callout.

- Hyperlink.

- Image block.

- File attachment block.

- Table block.

- Bookmark or link preview.

- Embedded content placeholder where native embedding is not practical.



Support:



- Editing text inline.

- Bold, italic, underline, strikethrough, and inline code.

- Text and background colors.

- Heading conversion.

- Lists and indentation.

- Enter and Backspace behavior between blocks.

- Keyboard navigation between blocks.

- Slash commands such as `/heading`, `/todo`, `/bullet`, `/code`, `/quote`, and `/divider`.

- Block insertion, deletion, duplication, and reordering.

- Drag-and-drop block movement.

- Undo and redo.

- Clipboard copy and paste.

- Link creation and editing.

- Autosave.

- Restoration of the last editing state.



Use a structured internal representation of document blocks. Do not store the entire document only as a flat string of text.



### C. Database-backed persistence



Every meaningful user action must persist to PostgreSQL. The application must restore the workspace, pages, blocks, favorites, settings, and other saved information after restarting.



Support:



- Automatic saving after edits.

- Debounced saves to avoid excessive database writes.

- Explicit save operations.

- Save status: Saving, Saved, and Error.

- Safe recovery after application crashes where feasible.

- Database transaction handling.

- Connection failure handling.

- Database reconnection.

- Schema versioning and migrations.



Never put database credentials directly in source code. Read configuration from environment variables or a local configuration file excluded from version control.



### D. Search



Implement a global search interface that searches:



- Page titles.

- Page content.

- Block text.

- Relevant metadata.



Include:



- Instant or debounced search.

- Search result previews.

- Highlighted matching text where practical.

- Keyboard navigation through results.

- Opening the selected page.

- Filtering by workspace or page type where practical.



Use PostgreSQL full-text search or an appropriate indexed search strategy for larger datasets.



### E. Tables and databases



Implement a useful database/table feature inspired by Notion databases.



Support:



- Create a database.

- Table view.

- Add, edit, and delete rows.

- Add and rename properties.

- Text properties.

- Number properties.

- Select properties.

- Multi-select properties.

- Date properties.

- Checkbox properties.

- URL properties.

- Person or user properties where applicable.

- Sorting by columns.

- Filtering rows.

- Reordering columns.

- Basic grouping where practical.

- Search within a database.

- Persistent views and column settings.



Store database schemas and row values in a robust, extensible format. Use appropriate relational tables and JSONB where useful, rather than creating unsafe dynamic SQL from user-defined property names.



### F. Additional views



Implement practical native desktop versions of:



- Table view.

- Board/Kanban view.

- Calendar view.

- List view.

- Simple gallery view.



Allow users to create and configure views for a database. Persist the view configuration.



### G. Templates



Implement:



- Blank page.

- Meeting notes.

- Daily journal.

- Project tracker.

- Task list.

- Study notes.

- Research notes.

- Custom reusable page templates.



Allow users to create pages from templates and save their own templates.



### H. Tasks and productivity



Support:



- To-do blocks.

- Task completion.

- Due dates.

- Priority fields where appropriate.

- Task lists.

- Basic task filtering.

- Linking tasks to pages.

- A simple Home or Dashboard page.

- Recent pages and favorites.



### I. Comments and collaboration-ready structure



Implement local comments or page discussions, including:



- Add comments to a page.

- Edit and delete one's own comments.

- Display comment timestamps.

- Resolve or reopen discussion threads where practical.



Design the data model so multi-user collaboration can be added later. Do not claim real-time collaborative editing unless it is actually implemented.



### J. Import and export



Support:



- Export a page to Markdown.

- Export a page to plain text.

- Export a page to HTML where practical.

- Export a table/database to CSV.

- Import Markdown files.

- Import CSV files into database tables.

- Attach files to pages.



Use appropriate file dialogs and validate imported data. Preserve as much structure as possible during import and export.



### K. Settings



Implement:



- Light theme.

- Dark theme.

- Font size preferences.

- Sidebar preferences.

- Autosave settings.

- Database connection status.

- Keyboard shortcut reference.

- Application information.

- Data backup and restore tools where feasible.



## 4. PostgreSQL Database Design



Design and implement a normalized, maintainable PostgreSQL schema.



At minimum, consider the following tables:



- `users`

- `workspaces`

- `workspace_members`

- `pages`

- `page_blocks`

- `favorites`

- `comments`

- `attachments`

- `database_definitions`

- `database_properties`

- `database_rows`

- `database_views`

- `templates`

- `user_settings`

- `page_history` or another suitable revision history structure.



Include:



- Primary keys.

- Foreign keys.

- Appropriate unique constraints.

- Indexes for common queries.

- Creation and modification timestamps.

- Soft deletion where appropriate.

- Transaction-safe operations.

- A migration mechanism.

- Seed data for a sample workspace.



For page blocks, store block type, content, ordering, parent block if applicable, and other required metadata.



For pages, support hierarchical relationships through a parent-page reference and enforce valid parent relationships in the application logic.



Choose UUIDs or another consistent identifier strategy. Use JSONB for extensible block content and database property values when justified.



Provide a complete SQL migration script and a sample environment configuration.



## 5. Application Architecture



Use a clean, modular architecture. A suggested package structure is:



`com.workspace.app`



- `app` — application entry point and lifecycle.

- `config` — application and database configuration.

- `model` — entities and data structures.

- `repository` — database queries and persistence.

- `service` — business logic and validation.

- `ui` — main window and application screens.

- `ui.components` — reusable Swing components.

- `ui.editor` — document editor and block components.

- `ui.sidebar` — page tree and workspace navigation.

- `ui.database` — table, board, and calendar views.

- `ui.dialogs` — dialogs and forms.

- `ui.theme` — light/dark themes and styling.

- `controller` — UI actions and event coordination.

- `importexport` — Markdown, HTML, and CSV handling.

- `util` — common utilities.

- `exception` — application-specific exceptions.



Use the repository-service-UI separation consistently.



Do not put SQL queries directly inside Swing event handlers. Do not place all application code inside one class.



Use prepared statements, resource-safe JDBC operations, connection pooling where appropriate, and parameterized SQL to prevent SQL injection.



Keep the Swing Event Dispatch Thread responsive. Execute database operations and other expensive work in background workers, such as `SwingWorker`, and update the UI safely on the Event Dispatch Thread.



## 6. Security and Reliability



Implement:



- Secure configuration handling.

- Password hashing if local authentication is implemented.

- Parameterized database queries.

- Input validation.

- Safe file import and export.

- Friendly error dialogs.

- Database connection diagnostics.

- Logging without exposing credentials.

- Confirmation before destructive actions.

- Proper cleanup of database resources.

- Safe handling of missing attachments.

- Consistent transaction boundaries.



Do not claim enterprise-grade security or multi-user collaboration unless those capabilities are genuinely implemented and tested.



## 7. Keyboard Shortcuts



Implement common productivity shortcuts where they do not conflict with standard operating-system behavior:



- Ctrl/Cmd + N: New page.

- Ctrl/Cmd + P: Quick page search or switcher.

- Ctrl/Cmd + Shift + P: Command menu, if supported.

- Ctrl/Cmd + B: Bold.

- Ctrl/Cmd + I: Italic.

- Ctrl/Cmd + K: Insert or edit link.

- Ctrl/Cmd + Z: Undo.

- Ctrl/Cmd + Shift + Z: Redo.

- Ctrl/Cmd + F: Search.

- Ctrl/Cmd + S: Save immediately.

- Delete or Backspace: Delete selected content where appropriate.



Use Java key bindings rather than relying only on individual components receiving key events. Display shortcuts in menus and tooltips.



## 8. Performance and UX Requirements



- Keep UI interactions responsive.

- Avoid reloading the entire page tree after every edit.

- Use lazy loading or pagination for large datasets where appropriate.

- Use debounced search and autosave.

- Provide loading, empty, success, and error states.

- Prevent accidental duplicate saves.

- Preserve editor focus during ordinary updates.

- Maintain scroll position when refreshing content.

- Provide helpful confirmation and validation messages.

- Make common actions accessible through both menus and keyboard shortcuts.



## 9. Testing Requirements



Write unit and integration tests for:



- Page creation and deletion.

- Nested page relationships.

- Block creation, editing, and ordering.

- Search.

- Favorites.

- Database CRUD operations.

- Import and export.

- Autosave behavior.

- Database error handling.

- Schema migrations.



Provide instructions for running PostgreSQL and executing integration tests against a test database. Do not make tests depend on production data.



## 10. Deliverables



Create a complete Maven project containing:



1\. All Java source files.

2\. `pom.xml` with required dependencies.

3\. PostgreSQL migration scripts.

4\. A `.env.example` or equivalent sample configuration.

5\. A `.gitignore`.

6\. A README with setup and execution instructions.

7\. Unit and integration tests.

8\. Sample seed data.

9\. Instructions for packaging the application as an executable JAR.

10\. A troubleshooting section for PostgreSQL connectivity and common errors.



The project must build using Maven and launch into a functional desktop application.



## 11. Development Workflow



Work directly in the current project directory.



Before coding:



1\. Inspect the existing repository.

2\. Identify the available Java version, Maven configuration, and existing source files.

3\. Check whether PostgreSQL is available and identify the configuration required to connect to it.

4\. Propose a concise implementation plan.

5\. Define the initial database schema and core UI architecture.



Then implement the application incrementally in the following order:



**Phase 1:** Project setup, database connectivity, schema migrations, and main window.



**Phase 2:** Workspace sidebar, page tree, page creation, navigation, and persistence.



**Phase 3:** Block-based editor, formatting, keyboard shortcuts, and autosave.



**Phase 4:** Search, favorites, page actions, templates, and trash.



**Phase 5:** Database/table views, sorting, filtering, and additional views.



**Phase 6:** Comments, attachments, import/export, settings, and themes.



**Phase 7:** Tests, error handling, performance improvements, documentation, and packaging.



After each phase:



- Compile the project.

- Run relevant tests.

- Fix compilation errors and regressions before continuing.

- Verify that implemented functionality works end to end.

- Update the README when setup or behavior changes.



Do not merely output code snippets or describe what should be done. Create and modify the actual project files using the available development tools.



Do not leave core functionality as TODO comments, placeholder buttons, or mock data. If a feature is too large to complete in one pass, implement a working version, document its limitations, and continue with the next milestone.



Prioritize a working end-to-end application over superficial feature breadth. Keep the UI consistent and polished throughout.



## 12. Definition of Done



The initial release is complete only when:



- The application launches successfully.

- It connects to PostgreSQL using external configuration.

- Users can create, edit, organize, search, and delete pages.

- Nested pages persist correctly.

- The editor supports the implemented block types.

- Changes save automatically and survive application restarts.

- Favorites, search, and page navigation work.

- At least one database/table view supports CRUD operations, sorting, and filtering.

- Import and export work for the supported formats.

- Light and dark themes function.

- The Maven build succeeds.

- Tests for the implemented features pass.

- Setup instructions are complete and reproducible.



Start by inspecting the repository, proposing the architecture and implementation plan, and then proceed to build the working application. Do not stop after generating the plan.

## PostgreSQL Connection Command

Run this command in PowerShell to connect to the database:

```powershell
& "D:\\postgres\\bin\\psql.exe" -h localhost -p 5432 -U postgres -d workspace_db
```
