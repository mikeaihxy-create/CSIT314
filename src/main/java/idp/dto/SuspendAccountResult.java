package idp.dto;

import idp.entity.UserAccount;

public record SuspendAccountResult(boolean success, String message, UserAccount account) implements Result {
}
