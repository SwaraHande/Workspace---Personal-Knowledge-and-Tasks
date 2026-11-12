package com.workspace.app.service;

import com.workspace.app.model.Workspace;
import com.workspace.app.repository.WorkspaceRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WorkspaceService {
    private final WorkspaceRepository workspaceRepository;

    public WorkspaceService(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    public List<Workspace> getAllWorkspaces() {
        List<Workspace> list = workspaceRepository.findAll();
        if (list.isEmpty()) {
            Workspace defaultWs = new Workspace("ws_default", "My Workspace", "🚀", "Personal workspace", "user_default");
            workspaceRepository.save(defaultWs);
            list.add(defaultWs);
        }
        return list;
    }

    public Optional<Workspace> getWorkspaceById(String id) {
        return workspaceRepository.findById(id);
    }

    public Workspace createWorkspace(String name, String icon) {
        String id = "ws_" + UUID.randomUUID().toString().substring(0, 8);
        Workspace ws = new Workspace(id, name, icon != null ? icon : "🚀", "", "user_default");
        workspaceRepository.save(ws);
        return ws;
    }

    public void updateWorkspace(Workspace ws) {
        workspaceRepository.save(ws);
    }

    public void deleteWorkspace(String id) {
        workspaceRepository.delete(id);
    }
}
