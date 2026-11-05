package idp.entity;

import idp.database.Database;
import idp.dto.CreateProfileDetail;
import idp.dto.CreateProfileResult;
import idp.dto.ProfileDetail;
import idp.dto.SuspendProfileResult;
import idp.dto.UpdateProfileResult;
import idp.enumeration.AccountStatus;
import idp.enumeration.Role;
import idp.session.SessionManager;
import idp.util.Clock;
import idp.util.Identifiable;
import idp.util.Messages;
import idp.util.Texts;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserProfile implements Identifiable {

    private long profileID;
    private long accountID;
    private String fullName;
    private String phone;
    private String address;
    private AccountStatus status = AccountStatus.ACTIVE;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserProfile() {
    }

    public UserProfile(long accountID, String fullName, String phone, String address) {
        this.accountID = accountID;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.status = AccountStatus.ACTIVE;
        this.createdAt = Clock.now();
        this.updatedAt = this.createdAt;
    }

    @Override
    public long getId() {
        return profileID;
    }

    @Override
    public void setId(long id) {
        this.profileID = id;
    }

    public long getProfileID() {
        return profileID;
    }

    public void setProfileID(long profileID) {
        this.profileID = profileID;
    }

    public long getAccountID() {
        return accountID;
    }

    public void setAccountID(long accountID) {
        this.accountID = accountID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
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

    public boolean isActive() {
        return status != null && status.isActive();
    }

    public CreateProfileResult create(CreateProfileDetail detail) {
        if (!checkRole()) {
            return new CreateProfileResult(false, Messages.NOT_AUTHORISED, null);
        }
        if (!validateDetail(detail)) {
            return new CreateProfileResult(false, Messages.CHECK_DETAILS, null);
        }
        if (Database.get().accounts().find(detail.accountID()) == null) {
            return new CreateProfileResult(false, Messages.CHECK_DETAILS, null);
        }
        if (findByAccount(detail.accountID()) != null) {
            return new CreateProfileResult(false, Messages.CHECK_DETAILS, null);
        }
        UserProfile profile = new UserProfile(
                detail.accountID(),
                Texts.trim(detail.fullName()),
                Texts.trim(detail.phone()),
                Texts.trim(detail.address()));
        Database.get().profiles().save(profile);
        return new CreateProfileResult(true, Messages.PROFILE_CREATED, profile);
    }

    private boolean checkRole() {
        return SessionManager.get().currentRole() == Role.USER_ADMIN;
    }

    private boolean validateDetail(CreateProfileDetail detail) {
        return detail != null
                && detail.accountID() > 0
                && Texts.isPresent(detail.fullName());
    }

    private UserProfile findByAccount(long accountID) {
        for (UserProfile profile : Database.get().profiles().all()) {
            if (profile.getAccountID() == accountID) {
                return profile;
            }
        }
        return null;
    }

    public UserProfile view(String profileID) {
        if (!checkRole()) {
            return null;
        }
        Long id = parseId(profileID);
        return id == null ? null : Database.get().profiles().find(id);
    }

    public UpdateProfileResult update(String profileID, ProfileDetail detail) {
        if (!checkRole()) {
            return new UpdateProfileResult(false, Messages.NOT_AUTHORISED, null);
        }
        UserProfile profile = view(profileID);
        if (profile == null) {
            return new UpdateProfileResult(false, Messages.PROFILE_NOT_FOUND, null);
        }
        if (!profile.isActive()) {
            return new UpdateProfileResult(false, Messages.PROFILE_IS_SUSPENDED, profile);
        }
        if (!validateChanges(detail)) {
            return new UpdateProfileResult(false, Messages.CHECK_CHANGES, null);
        }
        profile.setFullName(Texts.trim(detail.fullName()));
        profile.setPhone(Texts.trim(detail.phone()));
        profile.setAddress(Texts.trim(detail.address()));
        profile.setUpdatedAt(Clock.now());
        return new UpdateProfileResult(true, Messages.PROFILE_UPDATED, profile);
    }

    private boolean validateChanges(ProfileDetail detail) {
        return detail != null && Texts.isPresent(detail.fullName());
    }

    public SuspendProfileResult suspend(String profileID) {
        if (!checkRole()) {
            return new SuspendProfileResult(false, Messages.NOT_AUTHORISED, null);
        }
        UserProfile profile = view(profileID);
        if (profile == null) {
            return new SuspendProfileResult(false, Messages.PROFILE_NOT_FOUND, null);
        }
        if (!profile.isActive()) {
            return new SuspendProfileResult(false, Messages.PROFILE_ALREADY_SUSPENDED, profile);
        }
        profile.setStatus(AccountStatus.SUSPENDED);
        profile.setUpdatedAt(Clock.now());
        return new SuspendProfileResult(true, Messages.PROFILE_SUSPENDED, profile);
    }

    public List<UserProfile> search(String keyword, String status) {
        List<UserProfile> matches = new ArrayList<>();
        if (!checkRole() || Texts.isBlank(keyword)) {
            return matches;
        }
        AccountStatus statusFilter = Texts.isBlank(status) ? null : AccountStatus.fromString(status);
        for (UserProfile profile : Database.get().profiles().all()) {
            boolean keywordMatch = Texts.containsIgnoreCase(profile.getFullName(), keyword)
                    || Texts.containsIgnoreCase(profile.getPhone(), keyword)
                    || Texts.containsIgnoreCase(profile.getAddress(), keyword);
            boolean statusMatch = statusFilter == null || profile.getStatus() == statusFilter;
            if (keywordMatch && statusMatch) {
                matches.add(profile);
            }
        }
        matches.sort((a, b) -> Long.compare(a.getId(), b.getId()));
        return matches;
    }

    public static UserProfile ofAccount(long accountID) {
        for (UserProfile profile : Database.get().profiles().all()) {
            if (profile.getAccountID() == accountID) {
                return profile;
            }
        }
        return null;
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
        return "UserProfile{" + profileID + ", account " + accountID + ", " + fullName + '}';
    }
}
