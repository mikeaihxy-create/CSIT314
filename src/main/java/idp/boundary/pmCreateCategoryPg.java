package idp.boundary;

import idp.controller.pmCreateCategoryC;
import idp.dto.CategoryDetail;
import idp.dto.CreateCategoryResult;
import idp.util.Messages;

public class pmCreateCategoryPg {

    private final pmCreateCategoryC controller = new pmCreateCategoryC();

    private String name;
    private String description;

    private String message = "";

    public String getMessage() {
        return message;
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

    public CreateCategoryResult create() {
        CreateCategoryResult result = controller.create(new CategoryDetail(name, description));
        return displayCreateResult(result);
    }

    public CreateCategoryResult displayCreateResult(CreateCategoryResult result) {
        this.message = result.message();
        return result;
    }
}
