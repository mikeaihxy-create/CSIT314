package idp.boundary;

import idp.controller.uaSuspendProfileC;
import idp.dto.SuspendProfileResult;
import idp.util.Messages;

public class uaSuspendProfilePg {

    private final uaSuspendProfileC controller = new uaSuspendProfileC();

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

    public SuspendProfileResult suspend() {
        SuspendProfileResult result = controller.suspend(profileID);
        return displaySuspendResult(result);
    }

    public SuspendProfileResult displaySuspendResult(SuspendProfileResult result) {
        this.message = result.message();
        return result;
    }
}
