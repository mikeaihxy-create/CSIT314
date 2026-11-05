package idp.controller;

import idp.dto.LoginDetail;
import idp.dto.LoginResult;
import idp.entity.UserAccount;

public class loginC {

    private final UserAccount entity = new UserAccount();

    public LoginResult login(LoginDetail loginDetail) {
        return entity.login(loginDetail);
    }
}
