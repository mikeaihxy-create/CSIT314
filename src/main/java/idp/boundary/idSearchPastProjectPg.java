package idp.boundary;

import idp.controller.idSearchPastProjectC;
import idp.entity.DesignProject;
import idp.util.Messages;
import idp.util.Texts;
import java.util.List;

public class idSearchPastProjectPg {

    private final idSearchPastProjectC controller = new idSearchPastProjectC();

    private String keyword;

    private String message = "";

    public String getMessage() {
        return message;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<DesignProject> search() {
        List<DesignProject> matches = controller.search(keyword);
        return displaySearchResult(matches);
    }

    public List<DesignProject> displaySearchResult(List<DesignProject> matches) {
        if (Texts.isBlank(keyword)) {
            this.message = Messages.ENTER_KEYWORD;
        } else if (matches.isEmpty()) {
            this.message = Messages.NO_PAST_PROJECTS;
        } else {
            this.message = "";
        }
        return matches;
    }
}
