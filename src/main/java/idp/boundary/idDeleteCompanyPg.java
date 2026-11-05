package idp.boundary;

import idp.controller.idDeleteCompanyC;
import idp.dto.DeleteCompanyResult;
import idp.util.Messages;

public class idDeleteCompanyPg {

    private final idDeleteCompanyC controller = new idDeleteCompanyC();

    private String companyID;

    private String message = "";

    public String getMessage() {
        return message;
    }

    public String getCompanyID() {
        return companyID;
    }

    public void setCompanyID(String companyID) {
        this.companyID = companyID;
    }

    public DeleteCompanyResult delete() {
        DeleteCompanyResult result = controller.delete(companyID);
        return displayDeleteResult(result);
    }

    public DeleteCompanyResult displayDeleteResult(DeleteCompanyResult result) {
        this.message = result.message();
        return result;
    }
}
