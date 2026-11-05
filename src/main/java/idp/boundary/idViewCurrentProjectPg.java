package idp.boundary;

import idp.controller.idViewCurrentProjectC;
import idp.entity.DesignProject;
import idp.util.Messages;

public class idViewCurrentProjectPg {

    private final idViewCurrentProjectC controller = new idViewCurrentProjectC();

    private String projectID;

    private String message = "";

    public String getMessage() {
        return message;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public DesignProject view() {
        DesignProject found = controller.view(projectID);
        return displayViewResult(found);
    }

    public DesignProject displayViewResult(DesignProject found) {
        if (found == null) {
            this.message = Messages.CURRENT_PROJECT_NOT_FOUND;
        } else {
            this.message = "";
        }
        return found;
    }
}
