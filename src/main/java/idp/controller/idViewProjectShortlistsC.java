package idp.controller;

import idp.dto.ProjectShortlistResult;
import idp.entity.DesignProject;

public class idViewProjectShortlistsC {

    private final DesignProject entity = new DesignProject();

    public ProjectShortlistResult viewProjectShortlists() {
        return entity.viewProjectShortlists();
    }
}
