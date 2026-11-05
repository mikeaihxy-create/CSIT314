package idp.controller;

import idp.entity.DesignProject;

public class idViewPastProjectC {

    private final DesignProject entity = new DesignProject();

    public DesignProject view(String projectID) {
        return entity.viewPast(projectID);
    }
}
