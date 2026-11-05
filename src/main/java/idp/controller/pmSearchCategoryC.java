package idp.controller;

import idp.entity.ProjectCategory;
import java.util.List;

public class pmSearchCategoryC {

    private final ProjectCategory entity = new ProjectCategory();

    public List<ProjectCategory> search(String keyword) {
        return entity.search(keyword);
    }
}
