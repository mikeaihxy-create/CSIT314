package idp.boundary;

import idp.controller.idCreatePastProjectC;
import idp.dto.CreateProjectResult;
import idp.dto.ProjectDetail;
import idp.util.Forms;
import idp.util.Messages;

public class idCreatePastProjectPg {

    private final idCreatePastProjectC controller = new idCreatePastProjectC();

    private String title;
    private String description;
    private String style;
    private String budget;
    private String categoryID;
    private String completionDate;
    private String imageUrls;

    private String message = "";

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
    }

    public String getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String imageUrls) {
        this.imageUrls = imageUrls;
    }

    public CreateProjectResult create() {
        CreateProjectResult result = controller.create(new ProjectDetail(title, description, style, Forms.money(budget), Forms.id(categoryID), Forms.date(completionDate), Forms.images(imageUrls)));
        return displayCreateResult(result);
    }

    public CreateProjectResult displayCreateResult(CreateProjectResult result) {
        this.message = result.message();
        return result;
    }
}
