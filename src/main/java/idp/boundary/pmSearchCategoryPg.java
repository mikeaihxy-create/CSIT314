package idp.boundary;

import idp.controller.pmSearchCategoryC;
import idp.entity.ProjectCategory;
import idp.util.Messages;
import idp.util.Texts;
import java.util.List;

public class pmSearchCategoryPg {

    private final pmSearchCategoryC controller = new pmSearchCategoryC();

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

    public List<ProjectCategory> search() {
        List<ProjectCategory> matches = controller.search(keyword);
        return displaySearchResult(matches);
    }

    public List<ProjectCategory> displaySearchResult(List<ProjectCategory> matches) {
        if (Texts.isBlank(keyword)) {
            this.message = Messages.ENTER_KEYWORD;
        } else if (matches.isEmpty()) {
            this.message = Messages.NO_CATEGORIES;
        } else {
            this.message = "";
        }
        return matches;
    }
}
