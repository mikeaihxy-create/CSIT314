package idp.boundary;

import idp.controller.cSearchProjectC;
import idp.entity.DesignProject;
import idp.util.Messages;
import idp.util.Texts;
import java.util.List;

public class cSearchProjectPg {

    private final cSearchProjectC controller = new cSearchProjectC();

    private String keyword;
    private String style;
    private String budget;
    private String categoryID;

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

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public List<DesignProject> search() {
        List<DesignProject> matches = controller.search(keyword, style, budget, categoryID);
        return displaySearchResult(matches);
    }

    public List<DesignProject> displaySearchResult(List<DesignProject> matches) {
        if (!Texts.anyPresent(keyword, style, budget, categoryID)) {
            this.message = Messages.ENTER_KEYWORD_OR_FILTER;
        } else if (matches.isEmpty()) {
            this.message = Messages.NO_PROJECTS;
        } else {
            this.message = "";
        }
        return matches;
    }
}
