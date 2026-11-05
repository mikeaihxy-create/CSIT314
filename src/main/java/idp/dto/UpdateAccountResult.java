package idp.dto;

import idp.entity.UserAccount;

public record UpdateAccountResult(boolean success, String message, UserAccount account) implements Result {
}
