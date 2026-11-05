package idp.dto;

import idp.entity.DesignProject;

public record CreateProjectResult(boolean success, String message, DesignProject project) implements Result {
}
