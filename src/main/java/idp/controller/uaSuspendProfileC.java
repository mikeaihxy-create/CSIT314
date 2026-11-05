package idp.controller;

import idp.dto.SuspendProfileResult;
import idp.entity.UserProfile;

public class uaSuspendProfileC {

    private final UserProfile entity = new UserProfile();

    public SuspendProfileResult suspend(String profileID) {
        return entity.suspend(profileID);
    }
}
