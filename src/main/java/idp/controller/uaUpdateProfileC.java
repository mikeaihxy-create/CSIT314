package idp.controller;

import idp.dto.ProfileDetail;
import idp.dto.UpdateProfileResult;
import idp.entity.UserProfile;

public class uaUpdateProfileC {

    private final UserProfile entity = new UserProfile();

    public UpdateProfileResult update(String profileID, ProfileDetail detail) {
        return entity.update(profileID, detail);
    }
}
