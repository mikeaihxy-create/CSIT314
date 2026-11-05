package idp.controller;

import idp.dto.CategoryDetail;
import idp.dto.UpdateCategoryResult;
import idp.entity.ProjectCategory;

public class pmUpdateCategoryC {

    private final ProjectCategory entity = new ProjectCategory();

    public UpdateCategoryResult update(String categoryID, CategoryDetail detail) {
        return entity.update(categoryID, detail);
    }
}
