package idp.controller;

import idp.entity.DesignProject;
import java.util.List;

public class idSearchCurrentProjectC {

    private final DesignProject entity = new DesignProject();

    public List<DesignProject> search(String keyword) {
        return entity.searchCurrent(keyword);
    }
}
