package idp.controller;

import idp.dto.CompanyDetail;
import idp.dto.CreateCompanyResult;
import idp.entity.CompanyProfile;

public class idCreateCompanyC {

    private final CompanyProfile entity = new CompanyProfile();

    public CreateCompanyResult create(CompanyDetail detail) {
        return entity.create(detail);
    }
}
