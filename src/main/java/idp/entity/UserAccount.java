package idp.entity;

import idp.database.Database;
import idp.dto.AccountDetail;
import idp.dto.CreateAccountDetail;
import idp.dto.CreateAccountResult;
import idp.dto.LoginDetail;
import idp.dto.LoginResult;
import idp.dto.LogoutResult;
import idp.dto.SuspendAccountResult;
import idp.dto.UpdateAccountResult;
import idp.enumeration.AccountStatus;
import idp.enumeration.Role;
import idp.session.SessionManager;
import idp.util.Clock;
import idp.util.Identifiable;
import idp.util.Messages;
import idp.util.Passwords;
import idp.util.Texts;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class UserAccount implements Identifiable {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private long accountID;
    private String username;
    private String email;
    private String passwordHash;
    private Role role;
    private AccountStatus status = AccountStatus.ACTIVE;
    private LocalDateTime createdAt;

    public UserAccount() {
    }

    public UserAccount(String username, String email, String password, Role role) {
        this.username = username;
        this.email = email;
        this.passwordHash = Passwords.hash(password);
        this.role = role;
        this.status = AccountStatus.ACTIVE;
        this.createdAt = Clock.now();
    }

    @Override
    public long getId() {
        return accountID;
    }

    @Override
    public void setId(long id) {
        this.accountID = id;
    }

    public long getAccountID() {
        return accountID;
    }

    public void setAccountID(long accountID) {
        this.accountID = accountID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setPassword(String password) {
        this.passwordHash = Passwords.hash(password);
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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

    public boolean isActive() {
        return status != null && status.isActive();
    }

    public LoginResult login(LoginDetail loginDetail) {
        if (!validateInput(loginDetail)) {
            return new LoginResult(false, Messages.FILL_REQUIRED_FIELDS, null, null, null);
        }
        UserAccount account = findByUsernameOrEmail(loginDetail.usernameOrEmail());
        if (!checkCredentials(account, loginDetail.password())) {
            return new LoginResult(false, Messages.LOGIN_WRONG, null, null, null);
        }
        if (!checkStatus(account)) {
            return new LoginResult(false, Messages.ACCOUNT_NOT_ACTIVE, null, null, null);
        }
        String sessionId = createSession(account);
        return new LoginResult(true, "Login successful.", account, account.getRole(), sessionId);
    }

    private boolean validateInput(LoginDetail detail) {
        return detail != null
                && Texts.isPresent(detail.usernameOrEmail())
                && Texts.isPresent(detail.password());
    }

    private boolean checkCredentials(UserAccount account, String password) {
        return account != null && Passwords.matches(password, account.getPasswordHash());
    }

    private boolean checkStatus(UserAccount account) {
        return account.isActive();
    }

    private String createSession(UserAccount account) {
        return SessionManager.get().start(account);
    }

    public LogoutResult logout() {
        SessionManager.get().end();
        return new LogoutResult(true, Messages.LOGGED_OUT);
    }

    public CreateAccountResult create(CreateAccountDetail detail) {
        if (!checkRole()) {
            return new CreateAccountResult(false, Messages.NOT_AUTHORISED, null);
        }
        if (!validateDetail(detail)) {
            return new CreateAccountResult(false, Messages.CHECK_DETAILS, null);
        }
        if (usernameExists(detail.username())) {
            return new CreateAccountResult(false, Messages.USERNAME_OR_EMAIL_USED, null);
        }
        if (emailExists(detail.email(), 0L)) {
            return new CreateAccountResult(false, Messages.USERNAME_OR_EMAIL_USED, null);
        }
        if (detail.role() == null) {
            return new CreateAccountResult(false, Messages.CHOOSE_ROLE, null);
        }
        UserAccount account = new UserAccount(
                Texts.trim(detail.username()),
                Texts.trim(detail.email()),
                detail.password(),
                detail.role());
        save(account);
        return new CreateAccountResult(true, Messages.ACCOUNT_CREATED, account);
    }

    private boolean checkRole() {
        return SessionManager.get().currentRole() == Role.USER_ADMIN;
    }

    private boolean validateDetail(CreateAccountDetail detail) {
        return detail != null
                && Texts.isPresent(detail.username())
                && detail.username().trim().length() >= 3
                && Texts.isPresent(detail.email())
                && EMAIL.matcher(detail.email().trim()).matches()
                && Texts.isPresent(detail.password())
                && detail.password().length() >= 6;
    }

    private boolean usernameExists(String username) {
        for (UserAccount account : Database.get().accounts().all()) {
            if (account.getUsername() != null && account.getUsername().equalsIgnoreCase(Texts.trim(username))) {
                return true;
            }
        }
        return false;
    }

    private boolean emailExists(String email, long exceptAccountID) {
        for (UserAccount account : Database.get().accounts().all()) {
            if (account.getId() != exceptAccountID
                    && account.getEmail() != null
                    && account.getEmail().equalsIgnoreCase(Texts.trim(email))) {
                return true;
            }
        }
        return false;
    }

    private void save(UserAccount account) {
        Database.get().accounts().save(account);
    }

    public UserAccount view(String accountID) {
        if (!checkRole()) {
            return null;
        }
        return findAccount(accountID);
    }

    private UserAccount findAccount(String accountID) {
        Long id = parseId(accountID);
        return id == null ? null : Database.get().accounts().find(id);
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

    public UpdateAccountResult update(String accountID, AccountDetail detail) {
        if (!checkRole()) {
            return new UpdateAccountResult(false, Messages.NOT_AUTHORISED, null);
        }
        UserAccount account = findAccount(accountID);
        if (account == null) {
            return new UpdateAccountResult(false, Messages.ACCOUNT_NOT_FOUND, null);
        }
        if (!validateChanges(detail)) {
            return new UpdateAccountResult(false, Messages.CHECK_CHANGES, null);
        }
        if (emailExists(detail.email(), account.getId())) {
            return new UpdateAccountResult(false, Messages.EMAIL_USED, null);
        }
        if (detail.role() == null) {
            return new UpdateAccountResult(false, Messages.CHOOSE_ROLE, null);
        }
        account.setEmail(Texts.trim(detail.email()));
        account.setRole(detail.role());
        renameLinkedProfile(account.getId(), detail.fullName());
        return new UpdateAccountResult(true, Messages.ACCOUNT_UPDATED, account);
    }

    private boolean validateChanges(AccountDetail detail) {
        return detail != null
                && Texts.isPresent(detail.fullName())
                && Texts.isPresent(detail.email())
                && EMAIL.matcher(detail.email().trim()).matches();
    }

    private void renameLinkedProfile(long accountID, String fullName) {
        if (Texts.isBlank(fullName)) {
            return;
        }
        for (UserProfile profile : Database.get().profiles().all()) {
            if (profile.getAccountID() == accountID) {
                profile.setFullName(Texts.trim(fullName));
                profile.setUpdatedAt(Clock.now());
            }
        }
    }

    public SuspendAccountResult suspend(String accountID) {
        if (!checkRole()) {
            return new SuspendAccountResult(false, Messages.NOT_AUTHORISED, null);
        }
        UserAccount account = findAccount(accountID);
        if (account == null) {
            return new SuspendAccountResult(false, Messages.ACCOUNT_NOT_FOUND, null);
        }
        if (!account.isActive()) {
            return new SuspendAccountResult(false, Messages.ACCOUNT_ALREADY_SUSPENDED, account);
        }
        account.setStatus(AccountStatus.SUSPENDED);
        SessionManager.get().endSessionsFor(account.getId());
        return new SuspendAccountResult(true, Messages.ACCOUNT_SUSPENDED, account);
    }

    public List<UserAccount> search(String keyword, String role, String status) {
        List<UserAccount> matches = new ArrayList<>();
        if (!checkRole() || Texts.isBlank(keyword)) {
            return matches;
        }
        Role roleFilter = Role.fromString(role);
        AccountStatus statusFilter = Texts.isBlank(status) ? null : AccountStatus.fromString(status);
        for (UserAccount account : Database.get().accounts().all()) {
            boolean keywordMatch = Texts.containsIgnoreCase(account.getUsername(), keyword)
                    || Texts.containsIgnoreCase(account.getEmail(), keyword);
            boolean roleMatch = roleFilter == null || account.getRole() == roleFilter;
            boolean statusMatch = statusFilter == null || account.getStatus() == statusFilter;
            if (keywordMatch && roleMatch && statusMatch) {
                matches.add(account);
            }
        }
        matches.sort((a, b) -> Long.compare(a.getId(), b.getId()));
        return matches;
    }

    public static UserAccount lookup(long accountID) {
        return Database.get().accounts().find(accountID);
    }

    private UserAccount findByUsernameOrEmail(String usernameOrEmail) {
        String value = Texts.trim(usernameOrEmail);
        for (UserAccount account : Database.get().accounts().all()) {
            if (account.getUsername() != null && account.getUsername().equalsIgnoreCase(value)) {
                return account;
            }
            if (account.getEmail() != null && account.getEmail().equalsIgnoreCase(value)) {
                return account;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "UserAccount{" + accountID + ", " + username + ", " + role + ", " + status + '}';
    }
}
