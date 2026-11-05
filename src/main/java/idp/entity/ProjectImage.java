package idp.entity;

import idp.database.Database;
import idp.util.Clock;
import idp.util.Identifiable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProjectImage implements Identifiable {

    private long imageID;
    private long projectID;
    private String imageUrl;
    private int displayOrder;
    private boolean cover;
    private LocalDateTime uploadedAt;

    public ProjectImage() {
    }

    public ProjectImage(long projectID, String imageUrl, int displayOrder) {
        this.projectID = projectID;
        this.imageUrl = imageUrl;
        this.displayOrder = displayOrder;
        this.cover = displayOrder == 0;
        this.uploadedAt = Clock.now();
    }

    @Override
    public long getId() {
        return imageID;
    }

    @Override
    public void setId(long id) {
        this.imageID = id;
    }

    public long getImageID() {
        return imageID;
    }

    public void setImageID(long imageID) {
        this.imageID = imageID;
    }

    public long getProjectID() {
        return projectID;
    }

    public void setProjectID(long projectID) {
        this.projectID = projectID;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean isCover() {
        return cover;
    }

    public void setCover(boolean cover) {
        this.cover = cover;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public static List<ProjectImage> ofProject(long projectID) {
        List<ProjectImage> result = new ArrayList<>();
        for (ProjectImage image : Database.get().images().all()) {
            if (image.getProjectID() == projectID) {
                result.add(image);
            }
        }
        result.sort((a, b) -> Integer.compare(a.getDisplayOrder(), b.getDisplayOrder()));
        return result;
    }

    public static void replaceFor(long projectID, List<String> imageUrls) {
        for (ProjectImage image : ofProject(projectID)) {
            Database.get().images().remove(image.getId());
        }
        if (imageUrls == null) {
            return;
        }
        int order = 0;
        for (String url : imageUrls) {
            if (url != null && !url.isBlank()) {
                Database.get().images().save(new ProjectImage(projectID, url.trim(), order));
                order++;
            }
        }
    }

    public static void deleteFor(long projectID) {
        for (ProjectImage image : ofProject(projectID)) {
            Database.get().images().remove(image.getId());
        }
    }

    @Override
    public String toString() {
        return "ProjectImage{" + imageID + ", project " + projectID + ", order " + displayOrder + '}';
    }
}
