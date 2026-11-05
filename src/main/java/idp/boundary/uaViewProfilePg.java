package idp.boundary;

import idp.controller.uaViewProfileC;
import idp.entity.UserProfile;
import idp.util.Messages;

public class uaViewProfilePg {

    private final uaViewProfileC controller = new uaViewProfileC();

    private String profileID;

    private String message = "";

    public String getMessage() {
        return message;
    }

    public String getProfileID() {
        return profileID;
    }

    public void setProfileID(String profileID) {
        this.profileID = profileID;
    }

    public UserProfile view() {
        UserProfile found = controller.view(profileID);
        return displayViewResult(found);
    }

    public UserProfile displayViewResult(UserProfile found) {
        if (found == null) {
            this.message = Messages.PROFILE_NOT_FOUND;
        } else if (!found.isActive()) {
            this.message = Messages.PROFILE_IS_SUSPENDED;
        } else {
            this.message = "";
        }
        return found;
    }
}
