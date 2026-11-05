package idp.controller;

import idp.dto.DeleteCategoryResult;
import idp.entity.ProjectCategory;

public class pmDeleteCategoryC {

    private final ProjectCategory entity = new ProjectCategory();

    public DeleteCategoryResult delete(String categoryID) {
        return entity.delete(categoryID);
    }
}
