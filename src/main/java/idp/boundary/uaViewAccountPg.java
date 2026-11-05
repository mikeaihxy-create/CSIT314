package idp.boundary;

import idp.controller.uaViewAccountC;
import idp.entity.UserAccount;
import idp.util.Messages;

public class uaViewAccountPg {

    private final uaViewAccountC controller = new uaViewAccountC();

    private String accountID;

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

    public UserAccount view() {
        UserAccount found = controller.view(accountID);
        return displayViewResult(found);
    }

    public UserAccount displayViewResult(UserAccount found) {
        if (found == null) {
            this.message = Messages.ACCOUNT_NOT_FOUND;
        } else {
            this.message = "";
        }
        return found;
    }
}
