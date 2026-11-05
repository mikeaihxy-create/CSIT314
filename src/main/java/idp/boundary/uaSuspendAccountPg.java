package idp.boundary;

import idp.controller.uaSuspendAccountC;
import idp.dto.SuspendAccountResult;
import idp.util.Messages;

public class uaSuspendAccountPg {

    private final uaSuspendAccountC controller = new uaSuspendAccountC();

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

    public SuspendAccountResult suspend() {
        SuspendAccountResult result = controller.suspend(accountID);
        return displaySuspendResult(result);
    }

    public SuspendAccountResult displaySuspendResult(SuspendAccountResult result) {
        this.message = result.message();
        return result;
    }
}
