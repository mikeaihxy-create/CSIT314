package idp.boundary;

import idp.controller.cViewProjectC;
import idp.entity.DesignProject;
import idp.util.Messages;

public class cViewProjectPg {

    private final cViewProjectC controller = new cViewProjectC();

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
            this.message = Messages.PROJECT_NOT_AVAILABLE;
        } else {
            this.message = "";
        }
        return found;
    }
}
