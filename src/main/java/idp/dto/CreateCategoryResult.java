package idp.dto;

import idp.entity.ProjectCategory;

public record CreateCategoryResult(boolean success, String message, ProjectCategory category) implements Result {
}
