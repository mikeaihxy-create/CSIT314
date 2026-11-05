package idp.controller;

import idp.dto.DeleteProjectResult;
import idp.entity.DesignProject;

public class idDeletePastProjectC {

    private final DesignProject entity = new DesignProject();

    public DeleteProjectResult delete(String projectID) {
        return entity.deletePast(projectID);
    }
}
