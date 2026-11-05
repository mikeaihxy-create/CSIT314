package idp.boundary;

import idp.controller.uaSearchAccountC;
import idp.entity.UserAccount;
import idp.util.Messages;
import idp.util.Texts;
import java.util.List;

public class uaSearchAccountPg {

    private final uaSearchAccountC controller = new uaSearchAccountC();

    private String keyword;
    private String role;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<UserAccount> search() {
        List<UserAccount> matches = controller.search(keyword, role, status);
        return displaySearchResult(matches);
    }

    public List<UserAccount> displaySearchResult(List<UserAccount> matches) {
        if (Texts.isBlank(keyword)) {
            this.message = Messages.ENTER_KEYWORD;
        } else if (matches.isEmpty()) {
            this.message = Messages.NO_ACCOUNTS;
        } else {
            this.message = "";
        }
        return matches;
    }
}
