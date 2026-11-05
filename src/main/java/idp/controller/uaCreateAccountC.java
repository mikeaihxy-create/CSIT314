package idp.controller;

import idp.dto.CreateAccountDetail;
import idp.dto.CreateAccountResult;
import idp.entity.UserAccount;

public class uaCreateAccountC {

    private final UserAccount entity = new UserAccount();

    public CreateAccountResult create(CreateAccountDetail detail) {
        return entity.create(detail);
    }
}
