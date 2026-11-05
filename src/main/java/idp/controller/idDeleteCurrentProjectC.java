package idp.controller;

import idp.dto.DeleteProjectResult;
import idp.entity.DesignProject;

public class idDeleteCurrentProjectC {

    private final DesignProject entity = new DesignProject();

    public DeleteProjectResult delete(String projectID) {
        return entity.deleteCurrent(projectID);
    }
}
