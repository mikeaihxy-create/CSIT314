package idp.boundary;

import idp.controller.uaCreateAccountC;
import idp.dto.AccountDetail;
import idp.dto.CreateAccountDetail;
import idp.dto.CreateAccountResult;
import idp.enumeration.Role;
import idp.util.Messages;

public class uaCreateAccountPg {

    private final uaCreateAccountC controller = new uaCreateAccountC();

    private String username;
    private String email;
    private String password;
    private String role;

    private String message = "";

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public CreateAccountResult create() {
        CreateAccountResult result = controller.create(new CreateAccountDetail(username, email, password, Role.fromString(role)));
        return displayCreateResult(result);
    }

    public CreateAccountResult displayCreateResult(CreateAccountResult result) {
        this.message = result.message();
        return result;
    }
}
