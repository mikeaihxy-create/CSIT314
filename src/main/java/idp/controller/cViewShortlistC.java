package idp.controller;

import idp.entity.Favorite;
import java.util.List;

public class cViewShortlistC {

    private final Favorite entity = new Favorite();

    public List<Favorite> view(String customerID) {
        return entity.view(customerID);
    }
}
