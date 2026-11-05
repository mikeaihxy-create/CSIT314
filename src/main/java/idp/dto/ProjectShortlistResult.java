package idp.dto;

import java.util.List;
import idp.entity.DesignProject;

public record ProjectShortlistResult(boolean success, String message, int totalShortlists, List<DesignProject> projects) implements Result {
}
