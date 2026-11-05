package idp.boundary;

import idp.controller.cSearchShortlistC;
import idp.entity.DesignProject;
import idp.util.Messages;
import idp.util.Texts;
import java.util.List;

public class cSearchShortlistPg {

    private final cSearchShortlistC controller = new cSearchShortlistC();

    private String customerID;
    private String keyword;

    private String message = "";

    public String getMessage() {
        return message;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<DesignProject> search() {
        List<DesignProject> matches = controller.search(customerID, keyword);
        return displaySearchResult(matches);
    }

    public List<DesignProject> displaySearchResult(List<DesignProject> matches) {
        if (Texts.isBlank(keyword)) {
            this.message = Messages.ENTER_KEYWORD;
        } else if (matches.isEmpty()) {
            this.message = Messages.NO_SHORTLISTED_PROJECTS;
        } else {
            this.message = "";
        }
        return matches;
    }
}
