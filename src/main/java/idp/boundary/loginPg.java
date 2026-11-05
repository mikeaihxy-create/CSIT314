package idp.boundary;

import idp.controller.loginC;
import idp.dto.LoginDetail;
import idp.dto.LoginResult;
import idp.util.Messages;

public class loginPg {

    private final loginC controller = new loginC();

    private String usernameOrEmail;
    private String password;

    private String message = "";

    public String getMessage() {
        return message;
    }

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LoginResult login() {
        LoginResult result = controller.login(new LoginDetail(usernameOrEmail, password));
        return displayLoginResult(result);
    }

    public LoginResult displayLoginResult(LoginResult result) {
        this.message = result.message();
        return result;
    }
}
