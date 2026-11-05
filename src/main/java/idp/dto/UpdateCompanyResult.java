package idp.dto;

import idp.entity.CompanyProfile;

public record UpdateCompanyResult(boolean success, String message, CompanyProfile company) implements Result {
}
