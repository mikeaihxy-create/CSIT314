package idp.dto;

import idp.entity.UserAccount;
import idp.enumeration.Role;

public record LoginResult(boolean success, String message, UserAccount account, Role role, String sessionId) implements Result {
}
