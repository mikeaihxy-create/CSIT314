package idp.boundary;

import idp.controller.idDeletePastProjectC;
import idp.dto.DeleteProjectResult;
import idp.util.Messages;

public class idDeletePastProjectPg {

    private final idDeletePastProjectC controller = new idDeletePastProjectC();

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

    public DeleteProjectResult delete() {
        DeleteProjectResult result = controller.delete(projectID);
        return displayDeleteResult(result);
    }

    public DeleteProjectResult displayDeleteResult(DeleteProjectResult result) {
        this.message = result.message();
        return result;
    }
}
