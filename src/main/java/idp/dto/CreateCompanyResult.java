package idp.dto;

import idp.entity.CompanyProfile;

public record CreateCompanyResult(boolean success, String message, CompanyProfile company) implements Result {
}
