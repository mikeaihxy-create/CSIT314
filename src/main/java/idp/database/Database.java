package idp.database;

import idp.entity.ActivityEvent;
import idp.entity.CompanyProfile;
import idp.entity.DesignProject;
import idp.entity.Favorite;
import idp.entity.ProjectCategory;
import idp.entity.ProjectImage;
import idp.entity.UserAccount;
import idp.entity.UserProfile;

public final class Database {

    private static Database instance;

    private final Table<UserAccount> accounts = new Table<>();
    private final Table<UserProfile> profiles = new Table<>();
    private final Table<CompanyProfile> companies = new Table<>();
    private final Table<DesignProject> projects = new Table<>();
    private final Table<ProjectImage> images = new Table<>();
    private final Table<ProjectCategory> categories = new Table<>();
    private final Table<Favorite> favorites = new Table<>();
    private final Table<ActivityEvent> events = new Table<>();

    private Database() {
    }

    public static synchronized Database get() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public static synchronized void reset() {
        Database db = get();
        db.accounts.clear();
        db.profiles.clear();
        db.companies.clear();
        db.projects.clear();
        db.images.clear();
        db.categories.clear();
        db.favorites.clear();
        db.events.clear();
    }

    public Table<UserAccount> accounts() {
        return accounts;
    }

    public Table<UserProfile> profiles() {
        return profiles;
    }

    public Table<CompanyProfile> companies() {
        return companies;
    }

    public Table<DesignProject> projects() {
        return projects;
    }

    public Table<ProjectImage> images() {
        return images;
    }

    public Table<ProjectCategory> categories() {
        return categories;
    }

    public Table<Favorite> favorites() {
        return favorites;
    }

    public Table<ActivityEvent> events() {
        return events;
    }
}
