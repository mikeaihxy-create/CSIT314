package idp.controller;

import idp.entity.UserAccount;

public class uaViewAccountC {

    private final UserAccount entity = new UserAccount();

    public UserAccount view(String accountID) {
        return entity.view(accountID);
    }
}
