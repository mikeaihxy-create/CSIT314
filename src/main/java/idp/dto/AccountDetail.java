package idp.dto;

import idp.enumeration.Role;

public record AccountDetail(String fullName, String email, Role role) {
}
