package idp.boundary;

import idp.controller.idUpdateCompanyC;
import idp.dto.CompanyDetail;
import idp.dto.UpdateCompanyResult;
import idp.util.Messages;

public class idUpdateCompanyPg {

    private final idUpdateCompanyC controller = new idUpdateCompanyC();

    private String companyID;
    private String name;
    private String description;
    private String location;
    private String contactEmail;
    private String phone;
    private String logoUrl;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public UpdateCompanyResult update() {
        UpdateCompanyResult result = controller.update(companyID, new CompanyDetail(name, description, location, contactEmail, phone, logoUrl));
        return displayUpdateResult(result);
    }

    public UpdateCompanyResult displayUpdateResult(UpdateCompanyResult result) {
        this.message = result.message();
        return result;
    }
}
