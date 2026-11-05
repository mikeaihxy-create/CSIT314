package idp.boundary;

import idp.controller.idCreateCompanyC;
import idp.dto.CompanyDetail;
import idp.dto.CreateCompanyResult;
import idp.util.Messages;

public class idCreateCompanyPg {

    private final idCreateCompanyC controller = new idCreateCompanyC();

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

    public CreateCompanyResult create() {
        CreateCompanyResult result = controller.create(new CompanyDetail(name, description, location, contactEmail, phone, logoUrl));
        return displayCreateResult(result);
    }

    public CreateCompanyResult displayCreateResult(CreateCompanyResult result) {
        this.message = result.message();
        return result;
    }
}
