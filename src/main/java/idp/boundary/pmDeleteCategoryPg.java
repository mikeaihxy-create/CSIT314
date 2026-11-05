package idp.boundary;

import idp.controller.pmDeleteCategoryC;
import idp.dto.DeleteCategoryResult;
import idp.util.Messages;

public class pmDeleteCategoryPg {

    private final pmDeleteCategoryC controller = new pmDeleteCategoryC();

    private String categoryID;

    private String message = "";

    public String getMessage() {
        return message;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public DeleteCategoryResult delete() {
        DeleteCategoryResult result = controller.delete(categoryID);
        return displayDeleteResult(result);
    }

    public DeleteCategoryResult displayDeleteResult(DeleteCategoryResult result) {
        this.message = result.message();
        return result;
    }
}
