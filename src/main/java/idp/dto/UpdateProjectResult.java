package idp.dto;

import idp.entity.DesignProject;

public record UpdateProjectResult(boolean success, String message, DesignProject project) implements Result {
}
