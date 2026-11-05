package idp.controller;

import idp.entity.CompanyProfile;
import java.util.List;

public class idSearchCompanyC {

    private final CompanyProfile entity = new CompanyProfile();

    public List<CompanyProfile> search(String keyword) {
        return entity.search(keyword);
    }
}
