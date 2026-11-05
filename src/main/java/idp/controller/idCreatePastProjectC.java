package idp.controller;

import idp.dto.CreateProjectResult;
import idp.dto.ProjectDetail;
import idp.entity.DesignProject;

public class idCreatePastProjectC {

    private final DesignProject entity = new DesignProject();

    public CreateProjectResult create(ProjectDetail detail) {
        return entity.createPast(detail);
    }
}
