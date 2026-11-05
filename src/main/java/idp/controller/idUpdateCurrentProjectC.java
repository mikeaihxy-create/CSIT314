package idp.controller;

import idp.dto.ProjectDetail;
import idp.dto.UpdateProjectResult;
import idp.entity.DesignProject;

public class idUpdateCurrentProjectC {

    private final DesignProject entity = new DesignProject();

    public UpdateProjectResult update(String projectID, ProjectDetail detail) {
        return entity.updateCurrent(projectID, detail);
    }
}
