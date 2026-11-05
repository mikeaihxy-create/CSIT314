package idp.testdata;

import idp.database.Database;
import idp.entity.ActivityEvent;
import idp.entity.CompanyProfile;
import idp.entity.DesignProject;
import idp.entity.Favorite;
import idp.entity.ProjectCategory;
import idp.entity.ProjectImage;
import idp.entity.UserAccount;
import idp.entity.UserProfile;
import idp.enumeration.AccountStatus;
import idp.enumeration.CategoryStatus;
import idp.enumeration.CompanyStatus;
import idp.enumeration.EventType;
import idp.enumeration.ProjectStage;
import idp.enumeration.Role;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class TestDataLoader {

    public static final String DIRECTORY_PROPERTY = "idp.testdata";

    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Path directory;

    public TestDataLoader() {
        this(defaultDirectory());
    }

    public TestDataLoader(Path directory) {
        this.directory = Objects.requireNonNull(directory, "directory");
    }

    public static Path defaultDirectory() {
        String configured = System.getProperty(DIRECTORY_PROPERTY);
        if (configured != null && !configured.isBlank()) {
            return Paths.get(configured).toAbsolutePath().normalize();
        }
        Path[] candidates = {Paths.get("testdata"), Paths.get("IDP", "testdata"), Paths.get("..", "testdata")};
        for (Path candidate : candidates) {
            if (Files.isDirectory(candidate)) {
                return candidate.toAbsolutePath().normalize();
            }
        }
        return Paths.get("testdata").toAbsolutePath().normalize();
    }

    public Path directory() {
        return directory;
    }

    public Summary load() {
        int accounts = loadAccounts();
        int categories = loadCategories();
        int profiles = loadProfiles();
        int companies = loadCompanies();
        int projects = loadProjects();
        int images = loadImages();
        int favorites = loadFavorites();
        int events = loadEvents();
        return new Summary(accounts, profiles, companies, categories, projects, images, favorites, events);
    }

    private int loadAccounts() {
        List<Map<String, String>> rows = rows("accounts.csv");
        for (Map<String, String> row : rows) {
            UserAccount account = new UserAccount();
            account.setAccountID(number(row, "account_id"));
            account.setUsername(text(row, "username"));
            account.setEmail(text(row, "email"));
            account.setPassword(text(row, "password"));
            account.setRole(Role.valueOf(text(row, "role")));
            account.setStatus(AccountStatus.valueOf(text(row, "status")));
            account.setCreatedAt(stamp(row, "created_at"));
            Database.get().accounts().saveWithId(account.getAccountID(), account);
        }
        return rows.size();
    }

    private int loadProfiles() {
        List<Map<String, String>> rows = rows("profiles.csv");
        for (Map<String, String> row : rows) {
            UserProfile profile = new UserProfile();
            profile.setProfileID(number(row, "profile_id"));
            profile.setAccountID(number(row, "account_id"));
            profile.setFullName(text(row, "full_name"));
            profile.setPhone(text(row, "phone"));
            profile.setAddress(text(row, "address"));
            profile.setStatus(AccountStatus.valueOf(text(row, "status")));
            profile.setCreatedAt(stamp(row, "created_at"));
            profile.setUpdatedAt(stamp(row, "updated_at"));
            Database.get().profiles().saveWithId(profile.getProfileID(), profile);
        }
        return rows.size();
    }

    private int loadCompanies() {
        List<Map<String, String>> rows = rows("companies.csv");
        for (Map<String, String> row : rows) {
            CompanyProfile company = new CompanyProfile();
            company.setCompanyID(number(row, "company_id"));
            company.setOwnerAccountID(number(row, "owner_account_id"));
            company.setName(text(row, "name"));
            company.setDescription(text(row, "description"));
            company.setLocation(text(row, "location"));
            company.setContactEmail(text(row, "contact_email"));
            company.setPhone(text(row, "phone"));
            company.setLogoUrl(text(row, "logo_url"));
            company.setStatus(CompanyStatus.valueOf(text(row, "status")));
            Database.get().companies().saveWithId(company.getCompanyID(), company);
        }
        return rows.size();
    }

    private int loadCategories() {
        List<Map<String, String>> rows = rows("categories.csv");
        for (Map<String, String> row : rows) {
            ProjectCategory category = new ProjectCategory();
            category.setCategoryID(number(row, "category_id"));
            category.setName(text(row, "name"));
            category.setDescription(text(row, "description"));
            category.setStatus(CategoryStatus.valueOf(text(row, "status")));
            Database.get().categories().saveWithId(category.getCategoryID(), category);
        }
        return rows.size();
    }

    private int loadProjects() {
        List<Map<String, String>> rows = rows("projects.csv");
        for (Map<String, String> row : rows) {
            DesignProject project = new DesignProject();
            project.setProjectID(number(row, "project_id"));
            project.setOwnerAccountID(number(row, "owner_account_id"));
            project.setCategoryID(number(row, "category_id"));
            project.setTitle(text(row, "title"));
            project.setDescription(text(row, "description"));
            project.setStyle(text(row, "style"));
            project.setBudget(new BigDecimal(text(row, "budget")));
            project.setStage(ProjectStage.valueOf(text(row, "stage")));
            String completion = text(row, "completion_date");
            project.setCompletionDate(completion.isEmpty() ? null : LocalDate.parse(completion));
            project.setViewCount(Integer.parseInt(text(row, "view_count")));
            project.setShortlistCount(Integer.parseInt(text(row, "shortlist_count")));
            project.setCreatedAt(stamp(row, "created_at"));
            project.setUpdatedAt(stamp(row, "updated_at"));
            Database.get().projects().saveWithId(project.getProjectID(), project);
        }
        return rows.size();
    }

    private int loadImages() {
        List<Map<String, String>> rows = rows("images.csv");
        for (Map<String, String> row : rows) {
            ProjectImage image = new ProjectImage();
            image.setImageID(number(row, "image_id"));
            image.setProjectID(number(row, "project_id"));
            image.setImageUrl(text(row, "image_url"));
            image.setDisplayOrder(Integer.parseInt(text(row, "display_order")));
            image.setCover(Boolean.parseBoolean(text(row, "is_cover")));
            image.setUploadedAt(stamp(row, "uploaded_at"));
            Database.get().images().saveWithId(image.getImageID(), image);
        }
        return rows.size();
    }

    private int loadFavorites() {
        List<Map<String, String>> rows = rows("favorites.csv");
        for (Map<String, String> row : rows) {
            Favorite favorite = new Favorite();
            favorite.setFavoriteID(number(row, "favorite_id"));
            favorite.setCustomerAccountID(number(row, "customer_account_id"));
            favorite.setProjectID(number(row, "project_id"));
            favorite.setSavedAt(stamp(row, "saved_at"));
            Database.get().favorites().saveWithId(favorite.getFavoriteID(), favorite);
        }
        return rows.size();
    }

    private int loadEvents() {
        List<Map<String, String>> rows = rows("events.csv");
        for (Map<String, String> row : rows) {
            ActivityEvent event = new ActivityEvent();
            event.setEventID(number(row, "event_id"));
            event.setActorAccountID(number(row, "actor_account_id"));
            event.setProjectID(number(row, "project_id"));
            event.setEventType(EventType.valueOf(text(row, "event_type")));
            event.setOccurredAt(stamp(row, "occurred_at"));
            Database.get().events().saveWithId(event.getEventID(), event);
        }
        return rows.size();
    }

    public List<Map<String, String>> rows(String fileName) {
        Path file = directory.resolve(fileName);
        if (!Files.isRegularFile(file)) {
            throw new IllegalStateException("Test data file not found: " + file);
        }
        try {
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            if (lines.isEmpty()) {
                return List.of();
            }
            String[] header = lines.get(0).split(",", -1);
            List<Map<String, String>> rows = new ArrayList<>(lines.size() - 1);
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isBlank()) {
                    continue;
                }
                String[] cells = line.split(",", -1);
                Map<String, String> row = new LinkedHashMap<>();
                for (int c = 0; c < header.length; c++) {
                    row.put(header[c].trim(), c < cells.length ? cells[c].trim() : "");
                }
                rows.add(row);
            }
            return rows;
        } catch (IOException failure) {
            throw new UncheckedIOException("Cannot read " + file, failure);
        }
    }

    private static String text(Map<String, String> row, String column) {
        String value = row.get(column);
        if (value == null) {
            throw new IllegalStateException("Missing column: " + column);
        }
        return value;
    }

    private static long number(Map<String, String> row, String column) {
        return Long.parseLong(text(row, column));
    }

    private static LocalDateTime stamp(Map<String, String> row, String column) {
        String value = text(row, column);
        return value.isEmpty() ? null : LocalDateTime.parse(value, STAMP);
    }

    public record Summary(int accounts, int profiles, int companies, int categories,
                          int projects, int images, int favorites, int events) {

        public int total() {
            return accounts + profiles + companies + categories + projects + images + favorites + events;
        }

        @Override
        public String toString() {
            return "accounts=" + accounts
                    + ", profiles=" + profiles
                    + ", companies=" + companies
                    + ", categories=" + categories
                    + ", projects=" + projects
                    + ", images=" + images
                    + ", favorites=" + favorites
                    + ", events=" + events;
        }
    }
}
