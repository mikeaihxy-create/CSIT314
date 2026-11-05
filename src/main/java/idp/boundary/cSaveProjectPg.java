package idp.boundary;

import idp.controller.cSaveProjectC;
import idp.dto.SaveProjectResult;
import idp.util.Messages;

public class cSaveProjectPg {

    private final cSaveProjectC controller = new cSaveProjectC();

    private String customerID;
    private String projectID;

    private String message = "";

    public String getMessage() {
        return message;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public SaveProjectResult save() {
        SaveProjectResult result = controller.save(customerID, projectID);
        return displaySaveResult(result);
    }

    public SaveProjectResult displaySaveResult(SaveProjectResult result) {
        this.message = result.message();
        return result;
    }
}
