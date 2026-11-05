package idp.boundary;

import idp.controller.logoutC;
import idp.dto.LogoutResult;
import idp.util.Messages;

public class logoutPg {

    private final logoutC controller = new logoutC();

    private String message = "";

    public String getMessage() {
        return message;
    }

    public LogoutResult logout() {
        LogoutResult result = controller.logout();
        return displayLogoutResult(result);
    }

    public LogoutResult displayLogoutResult(LogoutResult result) {
        this.message = result.message();
        return result;
    }
}
