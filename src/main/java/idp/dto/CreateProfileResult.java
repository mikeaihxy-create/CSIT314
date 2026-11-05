package idp.dto;

import idp.entity.UserProfile;

public record CreateProfileResult(boolean success, String message, UserProfile profile) implements Result {
}
