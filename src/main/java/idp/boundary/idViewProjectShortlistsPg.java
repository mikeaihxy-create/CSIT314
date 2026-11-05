package idp.boundary;

import idp.controller.idViewProjectShortlistsC;
import idp.dto.ProjectShortlistResult;
import idp.util.Messages;

public class idViewProjectShortlistsPg {

    private final idViewProjectShortlistsC controller = new idViewProjectShortlistsC();

    private String message = "";

    public String getMessage() {
        return message;
    }

    public ProjectShortlistResult viewProjectShortlists() {
        ProjectShortlistResult result = controller.viewProjectShortlists();
        return displayShortlistCounts(result);
    }

    public ProjectShortlistResult displayShortlistCounts(ProjectShortlistResult result) {
        this.message = result.message();
        return result;
    }
}
