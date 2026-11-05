package idp.boundary;

import idp.controller.idViewCompanyC;
import idp.entity.CompanyProfile;
import idp.util.Messages;

public class idViewCompanyPg {

    private final idViewCompanyC controller = new idViewCompanyC();

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

    public CompanyProfile view() {
        CompanyProfile found = controller.view(companyID);
        return displayViewResult(found);
    }

    public CompanyProfile displayViewResult(CompanyProfile found) {
        if (found == null) {
            this.message = Messages.COMPANY_NOT_FOUND;
        } else {
            this.message = "";
        }
        return found;
    }
}
