package idp.controller;

import idp.entity.DesignProject;
import idp.entity.Favorite;
import java.util.List;

public class cSearchShortlistC {

    private final Favorite entity = new Favorite();

    public List<DesignProject> search(String customerID, String keyword) {
        return entity.search(customerID, keyword);
    }
}
