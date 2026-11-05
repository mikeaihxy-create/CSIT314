package idp.dto;

import java.util.List;
import idp.entity.DesignProject;

public record ProjectViewResult(boolean success, String message, int totalViews, List<DesignProject> projects) implements Result {
}
