package idp.controller;

import idp.entity.DesignProject;

public class cViewProjectC {

    private final DesignProject entity = new DesignProject();

    public DesignProject view(String projectID) {
        return entity.viewPublished(projectID);
    }
}
