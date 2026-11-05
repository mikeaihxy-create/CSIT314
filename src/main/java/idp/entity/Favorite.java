package idp.entity;

import idp.database.Database;
import idp.dto.SaveProjectResult;
import idp.enumeration.EventType;
import idp.enumeration.Role;
import idp.session.SessionManager;
import idp.util.Clock;
import idp.util.Identifiable;
import idp.util.Messages;
import idp.util.Texts;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Favorite implements Identifiable {

    private long favoriteID;
    private long customerAccountID;
    private long projectID;
    private LocalDateTime savedAt;

    public Favorite() {
    }

    public Favorite(long customerAccountID, long projectID) {
        this.customerAccountID = customerAccountID;
        this.projectID = projectID;
        this.savedAt = Clock.now();
    }

    @Override
    public long getId() {
        return favoriteID;
    }

    @Override
    public void setId(long id) {
        this.favoriteID = id;
    }

    public long getFavoriteID() {
        return favoriteID;
    }

    public void setFavoriteID(long favoriteID) {
        this.favoriteID = favoriteID;
    }

    public long getCustomerAccountID() {
        return customerAccountID;
    }

    public void setCustomerAccountID(long customerAccountID) {
        this.customerAccountID = customerAccountID;
    }

    public long getProjectID() {
        return projectID;
    }

    public void setProjectID(long projectID) {
        this.projectID = projectID;
    }

    public LocalDateTime getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(LocalDateTime savedAt) {
        this.savedAt = savedAt;
    }

    public void remove() {
        Database.get().favorites().remove(favoriteID);
    }

    public SaveProjectResult save(String customerID, String projectID) {
        Long customer = resolveCustomer(customerID);
        if (!customerRole() || customer == null || customer != SessionManager.get().currentAccountId()) {
            return new SaveProjectResult(false, Messages.NOT_AUTHORISED, null);
        }
        Long id = parseId(projectID);
        DesignProject project = id == null ? null : Database.get().projects().find(id);
        if (project == null || !project.isCurrent()) {
            return new SaveProjectResult(false, Messages.PROJECT_NOT_AVAILABLE, null);
        }
        if (isAlreadySaved(customer, project.getId())) {
            return new SaveProjectResult(false, Messages.PROJECT_ALREADY_SAVED, null);
        }
        Favorite favorite = new Favorite(customer, project.getId());
        Database.get().favorites().save(favorite);
        project.incrementShortlist();
        ActivityEvent.log(customer, project.getId(), EventType.PROJECT_SHORTLIST);
        return new SaveProjectResult(true, Messages.PROJECT_SAVED, favorite);
    }

    private boolean customerRole() {
        return SessionManager.get().currentRole() == Role.CUSTOMER;
    }

    private Long resolveCustomer(String customerID) {
        Long parsed = parseId(customerID);
        return parsed != null ? parsed : SessionManager.get().currentAccountId();
    }

    public static boolean isAlreadySaved(long customerAccountID, long projectID) {
        for (Favorite favorite : Database.get().favorites().all()) {
            if (favorite.getCustomerAccountID() == customerAccountID && favorite.getProjectID() == projectID) {
                return true;
            }
        }
        return false;
    }

    public List<DesignProject> search(String customerID, String keyword) {
        List<DesignProject> matches = new ArrayList<>();
        Long customer = resolveCustomer(customerID);
        if (!customerRole() || customer == null || Texts.isBlank(keyword)) {
            return matches;
        }
        for (Favorite favorite : ofCustomer(customer)) {
            DesignProject project = Database.get().projects().find(favorite.getProjectID());
            if (project == null) {
                continue;
            }
            if (Texts.containsIgnoreCase(project.getTitle(), keyword)
                    || Texts.containsIgnoreCase(project.getDescription(), keyword)
                    || Texts.containsIgnoreCase(project.getStyle(), keyword)) {
                matches.add(project);
            }
        }
        matches.sort((a, b) -> Long.compare(a.getId(), b.getId()));
        return matches;
    }

    public List<Favorite> view(String customerID) {
        List<Favorite> saved = new ArrayList<>();
        Long customer = resolveCustomer(customerID);
        if (!customerRole() || customer == null) {
            return saved;
        }
        saved.addAll(ofCustomer(customer));
        saved.sort((a, b) -> {
            if (a.getSavedAt() == null || b.getSavedAt() == null) {
                return Long.compare(a.getId(), b.getId());
            }
            return b.getSavedAt().compareTo(a.getSavedAt());
        });
        return saved;
    }

    public static List<Favorite> ofCustomer(long customerAccountID) {
        List<Favorite> saved = new ArrayList<>();
        for (Favorite favorite : Database.get().favorites().all()) {
            if (favorite.getCustomerAccountID() == customerAccountID) {
                saved.add(favorite);
            }
        }
        saved.sort((a, b) -> Long.compare(a.getId(), b.getId()));
        return saved;
    }

    public static List<Favorite> ofProject(long projectID) {
        List<Favorite> saved = new ArrayList<>();
        for (Favorite favorite : Database.get().favorites().all()) {
            if (favorite.getProjectID() == projectID) {
                saved.add(favorite);
            }
        }
        return saved;
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
        return "Favorite{" + favoriteID + ", customer=" + customerAccountID + ", project=" + projectID + '}';
    }
}
