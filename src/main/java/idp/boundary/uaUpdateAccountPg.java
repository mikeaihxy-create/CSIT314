package idp.boundary;

import idp.controller.uaUpdateAccountC;
import idp.dto.AccountDetail;
import idp.dto.UpdateAccountResult;
import idp.enumeration.Role;
import idp.util.Messages;

public class uaUpdateAccountPg {

    private final uaUpdateAccountC controller = new uaUpdateAccountC();

    private String accountID;
    private String fullName;
    private String email;
    private String role;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public UpdateAccountResult update() {
        UpdateAccountResult result = controller.update(accountID, new AccountDetail(fullName, email, Role.fromString(role)));
        return displayUpdateResult(result);
    }

    public UpdateAccountResult displayUpdateResult(UpdateAccountResult result) {
        this.message = result.message();
        return result;
    }
}
