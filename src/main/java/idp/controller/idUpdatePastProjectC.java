package idp.controller;

import idp.dto.ProjectDetail;
import idp.dto.UpdateProjectResult;
import idp.entity.DesignProject;

public class idUpdatePastProjectC {

    private final DesignProject entity = new DesignProject();

    public UpdateProjectResult update(String projectID, ProjectDetail detail) {
        return entity.updatePast(projectID, detail);
    }
}
