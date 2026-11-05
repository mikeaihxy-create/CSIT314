package idp.controller;

import idp.entity.DesignProject;
import java.util.List;

public class cSearchProjectC {

    private final DesignProject entity = new DesignProject();

    public List<DesignProject> search(String keyword, String style, String budget, String categoryID) {
        return entity.search(keyword, style, budget, categoryID);
    }
}
