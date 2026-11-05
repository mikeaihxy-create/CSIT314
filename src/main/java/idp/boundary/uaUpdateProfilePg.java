package idp.boundary;

import idp.controller.uaUpdateProfileC;
import idp.dto.ProfileDetail;
import idp.dto.UpdateProfileResult;
import idp.util.Messages;

public class uaUpdateProfilePg {

    private final uaUpdateProfileC controller = new uaUpdateProfileC();

    private String profileID;
    private String fullName;
    private String phone;
    private String address;

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public UpdateProfileResult update() {
        UpdateProfileResult result = controller.update(profileID, new ProfileDetail(fullName, phone, address));
        return displayUpdateResult(result);
    }

    public UpdateProfileResult displayUpdateResult(UpdateProfileResult result) {
        this.message = result.message();
        return result;
    }
}
