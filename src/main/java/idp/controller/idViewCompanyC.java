package idp.controller;

import idp.entity.CompanyProfile;

public class idViewCompanyC {

    private final CompanyProfile entity = new CompanyProfile();

    public CompanyProfile view(String companyID) {
        return entity.view(companyID);
    }
}
