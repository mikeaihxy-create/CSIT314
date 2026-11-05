package idp.controller;

import idp.dto.AccountDetail;
import idp.dto.UpdateAccountResult;
import idp.entity.UserAccount;

public class uaUpdateAccountC {

    private final UserAccount entity = new UserAccount();

    public UpdateAccountResult update(String accountID, AccountDetail detail) {
        return entity.update(accountID, detail);
    }
}
