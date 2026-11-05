package idp.dto;

import idp.entity.ProjectCategory;

public record UpdateCategoryResult(boolean success, String message, ProjectCategory category) implements Result {
}
