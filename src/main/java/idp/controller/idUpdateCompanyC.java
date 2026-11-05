package idp.controller;

import idp.dto.CompanyDetail;
import idp.dto.UpdateCompanyResult;
import idp.entity.CompanyProfile;

public class idUpdateCompanyC {

    private final CompanyProfile entity = new CompanyProfile();

    public UpdateCompanyResult update(String companyID, CompanyDetail detail) {
        return entity.update(companyID, detail);
    }
}
