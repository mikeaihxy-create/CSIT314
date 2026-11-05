package idp.dto;

import idp.entity.UserProfile;

public record UpdateProfileResult(boolean success, String message, UserProfile profile) implements Result {
}
