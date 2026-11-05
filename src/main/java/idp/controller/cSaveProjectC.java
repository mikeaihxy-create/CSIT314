package idp.controller;

import idp.dto.SaveProjectResult;
import idp.entity.Favorite;

public class cSaveProjectC {

    private final Favorite entity = new Favorite();

    public SaveProjectResult save(String customerID, String projectID) {
        return entity.save(customerID, projectID);
    }
}
