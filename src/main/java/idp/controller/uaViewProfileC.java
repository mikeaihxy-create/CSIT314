package idp.controller;

import idp.entity.UserProfile;

public class uaViewProfileC {

    private final UserProfile entity = new UserProfile();

    public UserProfile view(String profileID) {
        return entity.view(profileID);
    }
}
