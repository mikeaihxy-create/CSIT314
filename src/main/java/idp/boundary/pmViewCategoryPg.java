package idp.boundary;

import idp.controller.pmViewCategoryC;
import idp.entity.ProjectCategory;
import idp.util.Messages;
import java.util.List;

public class pmViewCategoryPg {

    private final pmViewCategoryC controller = new pmViewCategoryC();

    private String message = "";

    public String getMessage() {
        return message;
    }

    public List<ProjectCategory> view() {
        return displayViewResult(controller.view());
    }

    public List<ProjectCategory> displayViewResult(List<ProjectCategory> rows) {
        this.message = "";
        return rows;
    }
}
