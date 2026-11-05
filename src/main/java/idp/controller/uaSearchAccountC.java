package idp.controller;

import idp.entity.UserAccount;
import java.util.List;

public class uaSearchAccountC {

    private final UserAccount entity = new UserAccount();

    public List<UserAccount> search(String keyword, String role, String status) {
        return entity.search(keyword, role, status);
    }
}
