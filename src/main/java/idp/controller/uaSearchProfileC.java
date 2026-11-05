package idp.controller;

import idp.entity.UserProfile;
import java.util.List;

public class uaSearchProfileC {

    private final UserProfile entity = new UserProfile();

    public List<UserProfile> search(String keyword, String status) {
        return entity.search(keyword, status);
    }
}
