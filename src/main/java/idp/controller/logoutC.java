package idp.controller;

import idp.dto.LogoutResult;
import idp.entity.UserAccount;

public class logoutC {

    private final UserAccount entity = new UserAccount();

    public LogoutResult logout() {
        return entity.logout();
    }
}
