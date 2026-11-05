package idp.dto;

import idp.entity.Favorite;

public record SaveProjectResult(boolean success, String message, Favorite favorite) implements Result {
}
