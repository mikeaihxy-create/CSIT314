package idp.boundary;

import idp.controller.pmUpdateCategoryC;
import idp.dto.CategoryDetail;
import idp.dto.UpdateCategoryResult;
import idp.util.Messages;

public class pmUpdateCategoryPg {

    private final pmUpdateCategoryC controller = new pmUpdateCategoryC();

    private String categoryID;
    private String name;
    private String description;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UpdateCategoryResult update() {
        UpdateCategoryResult result = controller.update(categoryID, new CategoryDetail(name, description));
        return displayUpdateResult(result);
    }

    public UpdateCategoryResult displayUpdateResult(UpdateCategoryResult result) {
        this.message = result.message();
        return result;
    }
}
