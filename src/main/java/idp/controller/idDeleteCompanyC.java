package idp.controller;

import idp.dto.DeleteCompanyResult;
import idp.entity.CompanyProfile;

public class idDeleteCompanyC {

    private final CompanyProfile entity = new CompanyProfile();

    public DeleteCompanyResult delete(String companyID) {
        return entity.delete(companyID);
    }
}
