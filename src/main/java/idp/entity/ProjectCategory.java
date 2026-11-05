package idp.entity;

import idp.database.Database;
import idp.dto.CategoryDetail;
import idp.dto.CreateCategoryResult;
import idp.dto.DeleteCategoryResult;
import idp.dto.UpdateCategoryResult;
import idp.enumeration.CategoryStatus;
import idp.enumeration.Role;
import idp.session.SessionManager;
import idp.util.Identifiable;
import idp.util.Messages;
import idp.util.Texts;

import java.util.ArrayList;
import java.util.List;

public class ProjectCategory implements Identifiable {

    private long categoryID;
    private String name;
    private String description;
    private CategoryStatus status = CategoryStatus.ACTIVE;

    public ProjectCategory() {
    }

    public ProjectCategory(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public long getId() {
        return categoryID;
    }

    @Override
    public void setId(long id) {
        this.categoryID = id;
    }

    public long getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(long categoryID) {
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

    public CategoryStatus getStatus() {
        return status;
    }

    public void setStatus(CategoryStatus status) {
        this.status = status;
    }

    public boolean isActive() {
        return status == CategoryStatus.ACTIVE;
    }

    public boolean isInUse() {
        for (DesignProject project : Database.get().projects().all()) {
            if (project.getCategoryID() == categoryID) {
                return true;
            }
        }
        return false;
    }

    public void updateDetails(String newName, String newDescription) {
        this.name = Texts.trim(newName);
        this.description = Texts.trim(newDescription);
    }

    public CreateCategoryResult create(CategoryDetail detail) {
        if (!platformManagementRole()) {
            return new CreateCategoryResult(false, Messages.NOT_AUTHORISED, null);
        }
        if (!validateDetail(detail)) {
            return new CreateCategoryResult(false, Messages.CHECK_DETAILS, null);
        }
        if (nameExists(detail.name(), 0)) {
            return new CreateCategoryResult(false, Messages.CATEGORY_NAME_USED, null);
        }
        ProjectCategory category = new ProjectCategory();
        category.setName(Texts.trim(detail.name()));
        category.setDescription(Texts.trim(detail.description()));
        category.setStatus(CategoryStatus.ACTIVE);
        Database.get().categories().save(category);
        return new CreateCategoryResult(true, Messages.CATEGORY_CREATED, category);
    }

    private boolean platformManagementRole() {
        return SessionManager.get().currentRole() == Role.PLATFORM_MANAGEMENT;
    }

    private boolean validateDetail(CategoryDetail detail) {
        return detail != null
                && Texts.isPresent(detail.name())
                && Texts.isPresent(detail.description());
    }

    private boolean nameExists(String name, long exceptId) {
        for (ProjectCategory category : Database.get().categories().all()) {
            if (category.getId() != exceptId && category.getName() != null
                    && category.getName().equalsIgnoreCase(Texts.trim(name))) {
                return true;
            }
        }
        return false;
    }

    public List<ProjectCategory> view() {
        List<ProjectCategory> categories = new ArrayList<>();
        if (!platformManagementRole()) {
            return categories;
        }
        categories.addAll(Database.get().categories().all());
        categories.sort((a, b) -> Long.compare(a.getId(), b.getId()));
        return categories;
    }

    public UpdateCategoryResult update(String categoryID, CategoryDetail detail) {
        if (!platformManagementRole()) {
            return new UpdateCategoryResult(false, Messages.NOT_AUTHORISED, null);
        }
        ProjectCategory category = find(categoryID);
        if (category == null) {
            return new UpdateCategoryResult(false, Messages.CATEGORY_NOT_FOUND, null);
        }
        if (!validateDetail(detail)) {
            return new UpdateCategoryResult(false, Messages.CHECK_CHANGES, null);
        }
        if (nameExists(detail.name(), category.getId())) {
            return new UpdateCategoryResult(false, Messages.CATEGORY_NAME_USED, null);
        }
        category.updateDetails(detail.name(), detail.description());
        return new UpdateCategoryResult(true, Messages.CATEGORY_UPDATED, category);
    }

    public DeleteCategoryResult delete(String categoryID) {
        if (!platformManagementRole()) {
            return new DeleteCategoryResult(false, Messages.NOT_AUTHORISED);
        }
        ProjectCategory category = find(categoryID);
        if (category == null) {
            return new DeleteCategoryResult(false, Messages.CATEGORY_NOT_FOUND);
        }
        if (category.isInUse()) {
            return new DeleteCategoryResult(false, Messages.CATEGORY_IN_USE);
        }
        Database.get().categories().remove(category.getId());
        return new DeleteCategoryResult(true, Messages.CATEGORY_DELETED);
    }

    public List<ProjectCategory> search(String keyword) {
        List<ProjectCategory> matches = new ArrayList<>();
        if (!platformManagementRole() || Texts.isBlank(keyword)) {
            return matches;
        }
        for (ProjectCategory category : Database.get().categories().all()) {
            if (Texts.containsIgnoreCase(category.getName(), keyword)
                    || Texts.containsIgnoreCase(category.getDescription(), keyword)) {
                matches.add(category);
            }
        }
        matches.sort((a, b) -> Long.compare(a.getId(), b.getId()));
        return matches;
    }

    private ProjectCategory find(String categoryID) {
        Long id = parseId(categoryID);
        return id == null ? null : Database.get().categories().find(id);
    }

    private Long parseId(String value) {
        if (Texts.isBlank(value)) {
            return null;
        }
        try {
            return Long.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static ProjectCategory lookup(long categoryID) {
        return Database.get().categories().find(categoryID);
    }

    @Override
    public String toString() {
        return "ProjectCategory{" + categoryID + ", " + name + '}';
    }
}
