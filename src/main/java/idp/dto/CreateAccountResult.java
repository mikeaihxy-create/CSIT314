package idp.dto;

import idp.entity.UserAccount;

public record CreateAccountResult(boolean success, String message, UserAccount account) implements Result {
}
