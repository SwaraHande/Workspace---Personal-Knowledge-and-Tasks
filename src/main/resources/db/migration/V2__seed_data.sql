-- V2__seed_data.sql
-- Sample workspace, pages, blocks, database and templates

-- Default user
INSERT INTO users (id, username, display_name, email)
VALUES ('user_default', 'swara', 'Swara', 'swara@workspace.local')
ON CONFLICT (id) DO NOTHING;

-- Default workspace
INSERT INTO workspaces (id, name, icon, description, owner_id)
VALUES ('ws_default', 'My Workspace', '🚀', 'Personal Notion-style workspace', 'user_default')
ON CONFLICT (id) DO NOTHING;

-- Workspace member
INSERT INTO workspace_members (id, workspace_id, user_id, role)
VALUES ('wm_default', 'ws_default', 'user_default', 'OWNER')
ON CONFLICT (workspace_id, user_id) DO NOTHING;

-- Default user settings
INSERT INTO user_settings (user_id, theme, font_size, autosave_interval, sidebar_collapsed)
VALUES ('user_default', 'DARK', 14, 1500, false)
ON CONFLICT (user_id) DO NOTHING;

-- Seed Templates
INSERT INTO templates (id, name, description, category, icon, content_json, is_builtin)
VALUES
('tmpl_meeting', 'Meeting Notes', 'Capture agendas, attendee notes, and action items', 'Work', '📝',
 '[{"type":"HEADING_1","content":"Meeting Notes"},{"type":"CALLOUT","content":"📅 Date: Today | Attendees: Team"},{"type":"HEADING_2","content":"Agenda"},{"type":"BULLET_LIST","content":"Review sprint objectives"},{"type":"BULLET_LIST","content":"Architecture deep-dive"},{"type":"HEADING_2","content":"Action Items"},{"type":"TODO","content":"Update schema migrations","checked":false},{"type":"TODO","content":"Test PostgreSQL connection","checked":true}]',
 true),
('tmpl_journal', 'Daily Journal', 'Reflect on your day, gratitude, and key achievements', 'Personal', '📔',
 '[{"type":"HEADING_1","content":"Daily Journal"},{"type":"QUOTE","content":"Focus on making today 1% better than yesterday."},{"type":"HEADING_2","content":"Top 3 Priorities"},{"type":"TODO","content":"Finish Phase 1 features","checked":false},{"type":"TODO","content":"Daily standup review","checked":false},{"type":"HEADING_2","content":"Evening Reflection"},{"type":"PARAGRAPH","content":"Key learning from today:"}]',
 true),
('tmpl_tasks', 'Task List', 'Organized checklist for personal or team productivity', 'Productivity', '✅',
 '[{"type":"HEADING_1","content":"Task List"},{"type":"CALLOUT","content":"Prioritize tasks by urgency and impact."},{"type":"HEADING_2","content":"High Priority"},{"type":"TODO","content":"Critical database backup validation","checked":false},{"type":"HEADING_2","content":"Medium Priority"},{"type":"TODO","content":"Refactor sidebar component","checked":false}]',
 true),
('tmpl_study', 'Study Notes', 'Structured notes with key concepts, code, and questions', 'Education', '🎓',
 '[{"type":"HEADING_1","content":"Study Notes"},{"type":"HEADING_2","content":"Key Concepts"},{"type":"BULLET_LIST","content":"ACID properties in PostgreSQL"},{"type":"BULLET_LIST","content":"Swing Event Dispatch Thread (EDT) safety"},{"type":"HEADING_2","content":"Code Example"},{"type":"CODE","content":"SwingUtilities.invokeLater(() -> frame.setVisible(true));"}]',
 true)
ON CONFLICT (id) DO NOTHING;

-- Seed Sample Pages
INSERT INTO pages (id, workspace_id, parent_id, title, icon, is_favorite, position)
VALUES
('page_welcome', 'ws_default', NULL, 'Welcome to Workspace', '👋', true, 0),
('page_goals', 'ws_default', NULL, 'Personal Goals 2026', '🎯', true, 1),
('page_q4_sprint', 'ws_default', 'page_goals', 'Q4 Key Objectives', '📌', false, 0),
('page_quick_notes', 'ws_default', NULL, 'Quick Notes & Scratchpad', '⚡', false, 2)
ON CONFLICT (id) DO NOTHING;

-- Seed Welcome Page Blocks
INSERT INTO page_blocks (id, page_id, block_type, content, properties, position)
VALUES
('b_w1', 'page_welcome', 'HEADING_1', 'Welcome to your Notion-inspired Workspace!', '{}', 0),
('b_w2', 'page_welcome', 'CALLOUT', '💡 Tip: Type "/" in any block to open the slash-command menu to insert headings, to-dos, code blocks, quotes, and more!', '{}', 1),
('b_w3', 'page_welcome', 'PARAGRAPH', 'This desktop application brings the power, speed, and elegance of Notion directly to your native Java desktop with PostgreSQL persistence.', '{}', 2),
('b_w4', 'page_welcome', 'HEADING_2', '✨ Features at a Glance', '{}', 3),
('b_w5', 'page_welcome', 'BULLET_LIST', 'Hierarchical nested pages with drag, reorder, and instant navigation', '{}', 4),
('b_w6', 'page_welcome', 'BULLET_LIST', 'Block-based editor with slash commands, shortcuts, and inline styling', '{}', 5),
('b_w7', 'page_welcome', 'BULLET_LIST', 'Notion-style interactive databases with Table, Board, Calendar, List, and Gallery views', '{}', 6),
('b_w8', 'page_welcome', 'BULLET_LIST', 'Instant debounced search (Ctrl+P / Ctrl+F)', '{}', 7),
('b_w9', 'page_welcome', 'BULLET_LIST', 'Dark & Light themes, Markdown/CSV import and export', '{}', 8),
('b_w10', 'page_welcome', 'DIVIDER', '', '{}', 9),
('b_w11', 'page_welcome', 'HEADING_2', 'Getting Started Checklist', '{}', 10),
('b_w12', 'page_welcome', 'TODO', 'Explore the sidebar and create your first subpage', '{"checked":false}', 11),
('b_w13', 'page_welcome', 'TODO', 'Try pressing Ctrl+P to quickly search across pages', '{"checked":false}', 12),
('b_w14', 'page_welcome', 'TODO', 'Switch between Light and Dark themes in Settings', '{"checked":true}', 13),
('b_w15', 'page_welcome', 'CODE', '// Ready to write Java or SQL!\nSELECT * FROM pages WHERE in_trash = false;', '{"language":"sql"}', 14),
('b_w16', 'page_welcome', 'QUOTE', 'Simplicity is the prerequisite for reliability. — Edsger W. Dijkstra', '{}', 15)
ON CONFLICT (id) DO NOTHING;

-- Seed Sample Database: Sprint Tasks
INSERT INTO database_definitions (id, workspace_id, page_id, title, description)
VALUES ('db_sprint', 'ws_default', 'page_welcome', 'Sprint Tasks', 'Notion-style relational task tracker')
ON CONFLICT (id) DO NOTHING;

-- Database properties
INSERT INTO database_properties (id, database_id, name, property_type, options, position)
VALUES
('prop_name', 'db_sprint', 'Task Name', 'TEXT', '[]', 0),
('prop_status', 'db_sprint', 'Status', 'SELECT', '["Todo","In Progress","Done"]', 1),
('prop_priority', 'db_sprint', 'Priority', 'SELECT', '["High","Medium","Low"]', 2),
('prop_due', 'db_sprint', 'Due Date', 'DATE', '[]', 3),
('prop_done', 'db_sprint', 'Completed', 'CHECKBOX', '[]', 4)
ON CONFLICT (id) DO NOTHING;

-- Database rows
INSERT INTO database_rows (id, database_id, cell_values, position)
VALUES
('row_1', 'db_sprint', '{"Task Name":"Set up PostgreSQL schema migrations","Status":"Done","Priority":"High","Due Date":"2026-10-25","Completed":true}', 0),
('row_2', 'db_sprint', '{"Task Name":"Build Notion-like Swing Editor","Status":"In Progress","Priority":"High","Due Date":"2026-10-26","Completed":false}', 1),
('row_3', 'db_sprint', '{"Task Name":"Implement Kanban and Table views","Status":"Todo","Priority":"Medium","Due Date":"2026-10-27","Completed":false}', 2),
('row_4', 'db_sprint', '{"Task Name":"Export workspace pages to Markdown","Status":"Todo","Priority":"Low","Due Date":"2026-10-28","Completed":false}', 3)
ON CONFLICT (id) DO NOTHING;

-- Database views
INSERT INTO database_views (id, database_id, name, view_type)
VALUES
('view_table', 'db_sprint', 'Table View', 'TABLE'),
('view_board', 'db_sprint', 'Board View', 'BOARD'),
('view_calendar', 'db_sprint', 'Calendar View', 'CALENDAR'),
('view_list', 'db_sprint', 'List View', 'LIST')
ON CONFLICT (id) DO NOTHING;
