package idp.boundary;

import idp.controller.idSearchCompanyC;
import idp.entity.CompanyProfile;
import idp.util.Messages;
import idp.util.Texts;
import java.util.List;

public class idSearchCompanyPg {

    private final idSearchCompanyC controller = new idSearchCompanyC();

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

    public List<CompanyProfile> search() {
        List<CompanyProfile> matches = controller.search(keyword);
        return displaySearchResult(matches);
    }

    public List<CompanyProfile> displaySearchResult(List<CompanyProfile> matches) {
        if (Texts.isBlank(keyword)) {
            this.message = Messages.ENTER_KEYWORD;
        } else if (matches.isEmpty()) {
            this.message = Messages.NO_COMPANIES;
        } else {
            this.message = "";
        }
        return matches;
    }
}
