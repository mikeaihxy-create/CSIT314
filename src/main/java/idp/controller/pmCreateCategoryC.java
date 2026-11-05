package idp.controller;

import idp.dto.CategoryDetail;
import idp.dto.CreateCategoryResult;
import idp.entity.ProjectCategory;

public class pmCreateCategoryC {

    private final ProjectCategory entity = new ProjectCategory();

    public CreateCategoryResult create(CategoryDetail detail) {
        return entity.create(detail);
    }
}
