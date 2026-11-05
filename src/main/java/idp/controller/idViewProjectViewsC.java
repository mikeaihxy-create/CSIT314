package idp.controller;

import idp.dto.ProjectViewResult;
import idp.entity.DesignProject;

public class idViewProjectViewsC {

    private final DesignProject entity = new DesignProject();

    public ProjectViewResult viewProjectViews() {
        return entity.viewProjectViews();
    }
}
