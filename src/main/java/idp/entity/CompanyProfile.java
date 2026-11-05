package idp.entity;

import idp.database.Database;
import idp.dto.CompanyDetail;
import idp.dto.CreateCompanyResult;
import idp.dto.DeleteCompanyResult;
import idp.dto.UpdateCompanyResult;
import idp.enumeration.CompanyStatus;
import idp.enumeration.Role;
import idp.session.SessionManager;
import idp.util.Clock;
import idp.util.Identifiable;
import idp.util.Messages;
import idp.util.Texts;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CompanyProfile implements Identifiable {

    private long companyID;
    private long ownerAccountID;
    private String name;
    private String description;
    private String location;
    private String contactEmail;
    private String phone;
    private String logoUrl;
    private CompanyStatus status = CompanyStatus.ACTIVE;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CompanyProfile() {
    }

    public CompanyProfile(long ownerAccountID, String name, String description, String location,
                          String contactEmail, String phone, String logoUrl) {
        this.ownerAccountID = ownerAccountID;
        this.name = name;
        this.description = description;
        this.location = location;
        this.contactEmail = contactEmail;
        this.phone = phone;
        this.logoUrl = logoUrl;
        this.status = CompanyStatus.ACTIVE;
        this.createdAt = Clock.now();
        this.updatedAt = this.createdAt;
    }

    @Override
    public long getId() {
        return companyID;
    }

    @Override
    public void setId(long id) {
        this.companyID = id;
    }

    public long getCompanyID() {
        return companyID;
    }

    public void setCompanyID(long companyID) {
        this.companyID = companyID;
    }

    public long getOwnerAccountID() {
        return ownerAccountID;
    }

    public void setOwnerAccountID(long ownerAccountID) {
        this.ownerAccountID = ownerAccountID;
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

    public CompanyStatus getStatus() {
        return status;
    }

    public void setStatus(CompanyStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isOwnedBy(long accountID) {
        return ownerAccountID == accountID;
    }

    public CreateCompanyResult create(CompanyDetail detail) {
        if (!checkRole()) {
            return new CreateCompanyResult(false, Messages.NOT_AUTHORISED, null);
        }
        if (!validateDetail(detail)) {
            return new CreateCompanyResult(false, Messages.CHECK_DETAILS, null);
        }
        long owner = SessionManager.get().currentAccountId();
        if (findOwnedBy(owner) != null) {
            return new CreateCompanyResult(false, Messages.CHECK_DETAILS, null);
        }
        if (companyNameExists(detail.name(), 0L)) {
            return new CreateCompanyResult(false, Messages.COMPANY_NAME_USED, null);
        }
        CompanyProfile company = new CompanyProfile(
                owner,
                Texts.trim(detail.name()),
                Texts.trim(detail.description()),
                Texts.trim(detail.location()),
                Texts.trim(detail.contactEmail()),
                Texts.trim(detail.phone()),
                Texts.trim(detail.logoUrl()));
        Database.get().companies().save(company);
        return new CreateCompanyResult(true, Messages.COMPANY_CREATED, company);
    }

    private boolean checkRole() {
        return SessionManager.get().currentRole() == Role.INTERIOR_DESIGNER;
    }

    private boolean validateDetail(CompanyDetail detail) {
        return detail != null
                && Texts.isPresent(detail.name())
                && Texts.isPresent(detail.description())
                && Texts.isPresent(detail.location());
    }

    private boolean companyNameExists(String name, long exceptCompanyID) {
        for (CompanyProfile company : Database.get().companies().all()) {
            if (company.getId() != exceptCompanyID
                    && company.getName() != null
                    && company.getName().equalsIgnoreCase(Texts.trim(name))) {
                return true;
            }
        }
        return false;
    }

    private CompanyProfile findOwnedBy(long accountID) {
        for (CompanyProfile company : Database.get().companies().all()) {
            if (company.isOwnedBy(accountID)) {
                return company;
            }
        }
        return null;
    }

    public CompanyProfile view(String companyID) {
        if (!checkRole()) {
            return null;
        }
        CompanyProfile company = find(companyID);
        if (company == null || !company.isOwnedBy(SessionManager.get().currentAccountId())) {
            return null;
        }
        return company;
    }

    private CompanyProfile find(String companyID) {
        Long id = parseId(companyID);
        return id == null ? null : Database.get().companies().find(id);
    }

    public UpdateCompanyResult update(String companyID, CompanyDetail detail) {
        if (!checkRole()) {
            return new UpdateCompanyResult(false, Messages.NOT_AUTHORISED, null);
        }
        CompanyProfile company = view(companyID);
        if (company == null) {
            return new UpdateCompanyResult(false, Messages.COMPANY_NOT_FOUND, null);
        }
        if (!validateDetail(detail)) {
            return new UpdateCompanyResult(false, Messages.CHECK_CHANGES, null);
        }
        if (companyNameExists(detail.name(), company.getId())) {
            return new UpdateCompanyResult(false, Messages.COMPANY_NAME_USED, null);
        }
        company.setName(Texts.trim(detail.name()));
        company.setDescription(Texts.trim(detail.description()));
        company.setLocation(Texts.trim(detail.location()));
        company.setContactEmail(Texts.trim(detail.contactEmail()));
        company.setPhone(Texts.trim(detail.phone()));
        company.setLogoUrl(Texts.trim(detail.logoUrl()));
        company.setUpdatedAt(Clock.now());
        return new UpdateCompanyResult(true, Messages.COMPANY_UPDATED, company);
    }

    public DeleteCompanyResult delete(String companyID) {
        if (!checkRole()) {
            return new DeleteCompanyResult(false, Messages.NOT_AUTHORISED);
        }
        CompanyProfile company = view(companyID);
        if (company == null) {
            return new DeleteCompanyResult(false, Messages.COMPANY_NOT_FOUND);
        }
        Database.get().companies().remove(company.getId());
        return new DeleteCompanyResult(true, Messages.COMPANY_DELETED);
    }

    public List<CompanyProfile> search(String keyword) {
        List<CompanyProfile> matches = new ArrayList<>();
        if (!checkRole() || Texts.isBlank(keyword)) {
            return matches;
        }
        for (CompanyProfile company : Database.get().companies().all()) {
            if (Texts.containsIgnoreCase(company.getName(), keyword)
                    || Texts.containsIgnoreCase(company.getDescription(), keyword)
                    || Texts.containsIgnoreCase(company.getLocation(), keyword)) {
                matches.add(company);
            }
        }
        matches.sort((a, b) -> Long.compare(a.getId(), b.getId()));
        return matches;
    }

    private Long parseId(String value) {
        if (Texts.isBlank(value)) {
            return null;
        }
        try {
            return Long.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "CompanyProfile{" + companyID + ", " + name + '}';
    }
}
