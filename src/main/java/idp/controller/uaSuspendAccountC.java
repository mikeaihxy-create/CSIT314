package idp.controller;

import idp.dto.SuspendAccountResult;
import idp.entity.UserAccount;

public class uaSuspendAccountC {

    private final UserAccount entity = new UserAccount();

    public SuspendAccountResult suspend(String accountID) {
        return entity.suspend(accountID);
    }
}
