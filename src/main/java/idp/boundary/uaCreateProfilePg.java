package idp.boundary;

import idp.controller.uaCreateProfileC;
import idp.dto.CreateProfileDetail;
import idp.dto.CreateProfileResult;
import idp.dto.ProfileDetail;
import idp.util.Forms;
import idp.util.Messages;

public class uaCreateProfilePg {

    private final uaCreateProfileC controller = new uaCreateProfileC();

    private String accountID;
    private String fullName;
    private String phone;
    private String address;

    private String message = "";

    public String getMessage() {
        return message;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
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

    public CreateProfileResult create() {
        CreateProfileResult result = controller.create(new CreateProfileDetail(Forms.id(accountID), fullName, phone, address));
        return displayCreateResult(result);
    }

    public CreateProfileResult displayCreateResult(CreateProfileResult result) {
        this.message = result.message();
        return result;
    }
}
