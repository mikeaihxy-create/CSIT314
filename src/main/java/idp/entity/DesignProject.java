package idp.entity;

import idp.database.Database;
import idp.dto.CreateProjectResult;
import idp.dto.DeleteProjectResult;
import idp.dto.ProjectDetail;
import idp.dto.ProjectShortlistResult;
import idp.dto.ProjectViewResult;
import idp.dto.UpdateProjectResult;
import idp.enumeration.EventType;
import idp.enumeration.ProjectStage;
import idp.enumeration.Role;
import idp.session.SessionManager;
import idp.util.Clock;
import idp.util.Identifiable;
import idp.util.Messages;
import idp.util.Texts;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DesignProject implements Identifiable {

    private long projectID;
    private long ownerAccountID;
    private long categoryID;
    private String title;
    private String description;
    private String style;
    private BigDecimal budget = BigDecimal.ZERO;
    private ProjectStage stage = ProjectStage.CURRENT;
    private LocalDate completionDate;
    private int viewCount;
    private int shortlistCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DesignProject() {
    }

    @Override
    public long getId() {
        return projectID;
    }

    @Override
    public void setId(long id) {
        this.projectID = id;
    }

    public long getProjectID() {
        return projectID;
    }

    public void setProjectID(long projectID) {
        this.projectID = projectID;
    }

    public long getOwnerAccountID() {
        return ownerAccountID;
    }

    public void setOwnerAccountID(long ownerAccountID) {
        this.ownerAccountID = ownerAccountID;
    }

    public long getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(long categoryID) {
        this.categoryID = categoryID;
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

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public ProjectStage getStage() {
        return stage;
    }

    public void setStage(ProjectStage stage) {
        this.stage = stage;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getShortlistCount() {
        return shortlistCount;
    }

    public void setShortlistCount(int shortlistCount) {
        this.shortlistCount = shortlistCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isCurrent() {
        return stage == ProjectStage.CURRENT;
    }

    public CreateProjectResult createCurrent(ProjectDetail detail) {
        return create(detail, ProjectStage.CURRENT);
    }

    public CreateProjectResult createPast(ProjectDetail detail) {
        return create(detail, ProjectStage.PAST);
    }

    private CreateProjectResult create(ProjectDetail detail, ProjectStage targetStage) {
        if (!designerRole()) {
            return new CreateProjectResult(false, Messages.NOT_AUTHORISED, null);
        }
        if (!validateDetail(detail, targetStage)) {
            return new CreateProjectResult(false, Messages.CHECK_DETAILS, null);
        }
        if (!categoryExists(detail.categoryID())) {
            return new CreateProjectResult(false, Messages.CHOOSE_CATEGORY, null);
        }
        DesignProject project = new DesignProject();
        project.setOwnerAccountID(SessionManager.get().currentAccountId());
        project.setStage(targetStage);
        project.setViewCount(0);
        project.setShortlistCount(0);
        project.setCreatedAt(Clock.now());
        project.setUpdatedAt(project.getCreatedAt());
        project.applyDetail(detail);
        Database.get().projects().save(project);
        ProjectImage.replaceFor(project.getId(), detail.imageUrls());
        ActivityEvent.log(project.getOwnerAccountID(), project.getId(), EventType.PROJECT_CREATE);
        return new CreateProjectResult(true,
                targetStage == ProjectStage.CURRENT ? Messages.CURRENT_PROJECT_CREATED : Messages.PAST_PROJECT_CREATED,
                project);
    }

    private boolean designerRole() {
        return SessionManager.get().currentRole() == Role.INTERIOR_DESIGNER;
    }

    private boolean validateDetail(ProjectDetail detail, ProjectStage targetStage) {
        if (detail == null || Texts.isBlank(detail.title()) || Texts.isBlank(detail.description())
                || Texts.isBlank(detail.style())) {
            return false;
        }
        if (detail.budget() == null || detail.budget().signum() < 0) {
            return false;
        }
        if (targetStage != ProjectStage.PAST) {
            return true;
        }
        return detail.completionDate() != null
                && !detail.completionDate().isAfter(Clock.today());
    }

    private boolean categoryExists(long categoryID) {
        ProjectCategory category = Database.get().categories().find(categoryID);
        return category != null && category.isActive();
    }

    private void applyDetail(ProjectDetail detail) {
        this.title = Texts.trim(detail.title());
        this.description = Texts.trim(detail.description());
        this.style = Texts.trim(detail.style());
        this.budget = detail.budget();
        this.categoryID = detail.categoryID();
        if (detail.completionDate() != null) {
            this.completionDate = detail.completionDate();
        }
        this.updatedAt = Clock.now();
    }

    public DesignProject viewCurrent(String projectID) {
        return findOwned(projectID, ProjectStage.CURRENT);
    }

    public DesignProject viewPast(String projectID) {
        return findOwned(projectID, ProjectStage.PAST);
    }

    private DesignProject findOwned(String projectID, ProjectStage targetStage) {
        if (!designerRole()) {
            return null;
        }
        Long id = parseId(projectID);
        if (id == null) {
            return null;
        }
        DesignProject project = Database.get().projects().find(id);
        if (project == null
                || project.getStage() != targetStage
                || project.getOwnerAccountID() != SessionManager.get().currentAccountId()) {
            return null;
        }
        return project;
    }

    public UpdateProjectResult updateCurrent(String projectID, ProjectDetail detail) {
        return update(projectID, detail, ProjectStage.CURRENT);
    }

    public UpdateProjectResult updatePast(String projectID, ProjectDetail detail) {
        return update(projectID, detail, ProjectStage.PAST);
    }

    private UpdateProjectResult update(String projectID, ProjectDetail detail, ProjectStage targetStage) {
        if (!designerRole()) {
            return new UpdateProjectResult(false, Messages.NOT_AUTHORISED, null);
        }
        DesignProject project = findOwned(projectID, targetStage);
        if (project == null) {
            return new UpdateProjectResult(false,
                    targetStage == ProjectStage.CURRENT ? Messages.CURRENT_PROJECT_NOT_FOUND : Messages.PAST_PROJECT_NOT_FOUND,
                    null);
        }
        if (!validateDetail(detail, targetStage)) {
            return new UpdateProjectResult(false, Messages.CHECK_CHANGES, null);
        }
        if (!categoryExists(detail.categoryID())) {
            return new UpdateProjectResult(false, Messages.CHOOSE_CATEGORY, null);
        }
        project.applyDetail(detail);
        ProjectImage.replaceFor(project.getId(), detail.imageUrls());
        ActivityEvent.log(project.getOwnerAccountID(), project.getId(), EventType.PROJECT_UPDATE);
        return new UpdateProjectResult(true,
                targetStage == ProjectStage.CURRENT ? Messages.CURRENT_PROJECT_UPDATED : Messages.PAST_PROJECT_UPDATED,
                project);
    }

    public DeleteProjectResult deleteCurrent(String projectID) {
        return delete(projectID, ProjectStage.CURRENT);
    }

    public DeleteProjectResult deletePast(String projectID) {
        return delete(projectID, ProjectStage.PAST);
    }

    private DeleteProjectResult delete(String projectID, ProjectStage targetStage) {
        if (!designerRole()) {
            return new DeleteProjectResult(false, Messages.NOT_AUTHORISED);
        }
        DesignProject project = findOwned(projectID, targetStage);
        if (project == null) {
            return new DeleteProjectResult(false,
                    targetStage == ProjectStage.CURRENT ? Messages.CURRENT_PROJECT_NOT_FOUND : Messages.PAST_PROJECT_NOT_FOUND);
        }
        removeProject(project);
        return new DeleteProjectResult(true,
                targetStage == ProjectStage.CURRENT ? Messages.CURRENT_PROJECT_DELETED : Messages.PAST_PROJECT_DELETED);
    }

    private void removeProject(DesignProject project) {
        ProjectImage.deleteFor(project.getId());
        for (Favorite favorite : Database.get().favorites().all()) {
            if (favorite.getProjectID() == project.getId()) {
                Database.get().favorites().remove(favorite.getId());
            }
        }
        Database.get().projects().remove(project.getId());
    }

    public List<DesignProject> searchCurrent(String keyword) {
        return searchOwn(keyword, ProjectStage.CURRENT);
    }

    public List<DesignProject> searchPast(String keyword) {
        return searchOwn(keyword, ProjectStage.PAST);
    }

    private List<DesignProject> searchOwn(String keyword, ProjectStage targetStage) {
        List<DesignProject> matches = new ArrayList<>();
        if (!designerRole() || Texts.isBlank(keyword)) {
            return matches;
        }
        long owner = SessionManager.get().currentAccountId();
        for (DesignProject project : Database.get().projects().all()) {
            if (project.getOwnerAccountID() == owner
                    && project.getStage() == targetStage
                    && matchesKeyword(project, keyword)) {
                matches.add(project);
            }
        }
        matches.sort((a, b) -> Long.compare(a.getId(), b.getId()));
        return matches;
    }

    private boolean matchesKeyword(DesignProject project, String keyword) {
        return Texts.containsIgnoreCase(project.getTitle(), keyword)
                || Texts.containsIgnoreCase(project.getDescription(), keyword)
                || Texts.containsIgnoreCase(project.getStyle(), keyword);
    }

    public ProjectViewResult viewProjectViews() {
        if (!designerRole()) {
            return new ProjectViewResult(false, Messages.NOT_AUTHORISED, 0, new ArrayList<>());
        }
        List<DesignProject> projects = ownProjects();
        int total = 0;
        for (DesignProject project : projects) {
            total += project.getViewCount();
        }
        String message = projects.isEmpty() ? Messages.NO_PROJECT_DATA : "";
        return new ProjectViewResult(true, message, total, projects);
    }

    public ProjectShortlistResult viewProjectShortlists() {
        if (!designerRole()) {
            return new ProjectShortlistResult(false, Messages.NOT_AUTHORISED, 0, new ArrayList<>());
        }
        List<DesignProject> projects = ownProjects();
        int total = 0;
        for (DesignProject project : projects) {
            total += project.getShortlistCount();
        }
        String message = projects.isEmpty() ? Messages.NO_PROJECT_DATA : "";
        return new ProjectShortlistResult(true, message, total, projects);
    }

    private List<DesignProject> ownProjects() {
        List<DesignProject> projects = new ArrayList<>();
        long owner = SessionManager.get().currentAccountId();
        for (DesignProject project : Database.get().projects().all()) {
            if (project.getOwnerAccountID() == owner) {
                projects.add(project);
            }
        }
        projects.sort((a, b) -> Long.compare(a.getId(), b.getId()));
        return projects;
    }

    public List<DesignProject> search(String keyword, String style, String budget, String categoryID) {
        List<DesignProject> matches = new ArrayList<>();
        if (SessionManager.get().currentRole() != Role.CUSTOMER) {
            return matches;
        }
        if (!Texts.anyPresent(keyword, style, budget, categoryID)) {
            return matches;
        }
        Long categoryFilter = parseId(categoryID);
        for (DesignProject project : Database.get().projects().all()) {
            if (!project.isCurrent()) {
                continue;
            }
            if (Texts.isPresent(keyword) && !matchesKeyword(project, keyword)) {
                continue;
            }
            if (Texts.isPresent(style) && !project.getStyle().equalsIgnoreCase(style.trim())) {
                continue;
            }
            if (categoryFilter != null && project.getCategoryID() != categoryFilter) {
                continue;
            }
            if (Texts.isPresent(budget) && !matchesBudget(project.getBudget(), budget)) {
                continue;
            }
            matches.add(project);
        }
        matches.sort((a, b) -> Long.compare(a.getId(), b.getId()));
        return matches;
    }

    private boolean matchesBudget(BigDecimal value, String filter) {
        if (value == null) {
            return false;
        }
        String text = filter.trim();
        try {
            if (text.contains("-")) {
                String[] parts = text.split("-", 2);
                BigDecimal min = new BigDecimal(parts[0].trim());
                BigDecimal max = new BigDecimal(parts[1].trim());
                return value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
            }
            return value.compareTo(new BigDecimal(text)) <= 0;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public DesignProject viewPublished(String projectID) {
        if (SessionManager.get().currentRole() != Role.CUSTOMER) {
            return null;
        }
        Long id = parseId(projectID);
        if (id == null) {
            return null;
        }
        DesignProject project = Database.get().projects().find(id);
        if (project == null || !project.isCurrent()) {
            return null;
        }
        project.incrementView();
        ActivityEvent.log(SessionManager.get().currentAccountId(), project.getId(), EventType.PROJECT_VIEW);
        return project;
    }

    public void incrementView() {
        viewCount++;
    }

    public void incrementShortlist() {
        shortlistCount++;
    }

    public void changeStage(ProjectStage newStage, LocalDate newCompletionDate) {
        this.stage = newStage;
        this.completionDate = newCompletionDate;
        this.updatedAt = Clock.now();
        ActivityEvent.log(ownerAccountID, projectID, EventType.STATUS_CHANGE);
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

    @Override
    public String toString() {
        return "DesignProject{" + projectID + ", " + title + ", " + stage + '}';
    }
}
