package idp.controller;

import idp.entity.DesignProject;

public class idViewCurrentProjectC {

    private final DesignProject entity = new DesignProject();

    public DesignProject view(String projectID) {
        return entity.viewCurrent(projectID);
    }
}
