package idp.dto;

public record LogoutResult(boolean success, String message) implements Result {
}
