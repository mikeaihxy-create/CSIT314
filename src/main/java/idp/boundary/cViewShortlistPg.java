package idp.boundary;

import idp.controller.cViewShortlistC;
import idp.entity.Favorite;
import idp.util.Messages;
import java.util.List;

public class cViewShortlistPg {

    private final cViewShortlistC controller = new cViewShortlistC();

    private String customerID;

    private String message = "";

    public String getMessage() {
        return message;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public List<Favorite> view() {
        List<Favorite> saved = controller.view(customerID);
        return displayViewResult(saved);
    }

    public List<Favorite> displayViewResult(List<Favorite> saved) {
        this.message = saved.isEmpty() ? Messages.NO_SAVED_PROJECTS : "";
        return saved;
    }
}
