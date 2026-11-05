package idp.boundary;

import idp.controller.idViewProjectViewsC;
import idp.dto.ProjectViewResult;
import idp.util.Messages;

public class idViewProjectViewsPg {

    private final idViewProjectViewsC controller = new idViewProjectViewsC();

    private String message = "";

    public String getMessage() {
        return message;
    }

    public ProjectViewResult viewProjectViews() {
        ProjectViewResult result = controller.viewProjectViews();
        return displayViewCounts(result);
    }

    public ProjectViewResult displayViewCounts(ProjectViewResult result) {
        this.message = result.message();
        return result;
    }
}
