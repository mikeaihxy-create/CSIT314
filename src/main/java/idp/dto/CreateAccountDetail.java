package idp.dto;

import idp.enumeration.Role;

public record CreateAccountDetail(String username, String email, String password, Role role) {
}
