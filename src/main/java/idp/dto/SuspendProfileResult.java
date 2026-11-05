package idp.dto;

import idp.entity.UserProfile;

public record SuspendProfileResult(boolean success, String message, UserProfile profile) implements Result {
}
