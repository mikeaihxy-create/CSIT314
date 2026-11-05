package idp.controller;

import idp.entity.ProjectCategory;
import java.util.List;

public class pmViewCategoryC {

    private final ProjectCategory entity = new ProjectCategory();

    public List<ProjectCategory> view() {
        return entity.view();
    }
}
