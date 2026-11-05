package idp.boundary;

import idp.controller.uaSearchProfileC;
import idp.entity.UserProfile;
import idp.util.Messages;
import idp.util.Texts;
import java.util.List;

public class uaSearchProfilePg {

    private final uaSearchProfileC controller = new uaSearchProfileC();

    private String keyword;
    private String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<UserProfile> search() {
        List<UserProfile> matches = controller.search(keyword, status);
        return displaySearchResult(matches);
    }

    public List<UserProfile> displaySearchResult(List<UserProfile> matches) {
        if (Texts.isBlank(keyword)) {
            this.message = Messages.ENTER_KEYWORD;
        } else if (matches.isEmpty()) {
            this.message = Messages.NO_PROFILES;
        } else {
            this.message = "";
        }
        return matches;
    }
}
