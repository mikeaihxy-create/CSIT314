package idp.controller;

import idp.dto.CreateProfileDetail;
import idp.dto.CreateProfileResult;
import idp.entity.UserProfile;

public class uaCreateProfileC {

    private final UserProfile entity = new UserProfile();

    public CreateProfileResult create(CreateProfileDetail detail) {
        return entity.create(detail);
    }
}
