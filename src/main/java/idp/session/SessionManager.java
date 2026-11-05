package idp.session;

import idp.entity.UserAccount;
import idp.enumeration.Role;

import java.util.ArrayList;
import java.util.List;

public final class SessionManager {

    private static final SessionManager INSTANCE = new SessionManager();

    private final List<String> activeSessionIds = new ArrayList<>();
    private UserAccount currentAccount;
    private String currentSessionId;

    private SessionManager() {
    }

    public static SessionManager get() {
        return INSTANCE;
    }

    public String start(UserAccount account) {
        String sessionId = "S-" + account.getId() + "-" + (activeSessionIds.size() + 1);
        activeSessionIds.add(sessionId);
        currentAccount = account;
        currentSessionId = sessionId;
        return sessionId;
    }

    public void end() {
        if (currentSessionId != null) {
            activeSessionIds.remove(currentSessionId);
        }
        currentSessionId = null;
        currentAccount = null;
    }

    public void endSessionsFor(long accountID) {
        activeSessionIds.removeIf(id -> id.startsWith("S-" + accountID + "-"));
        if (currentAccount != null && currentAccount.getId() == accountID) {
            currentAccount = null;
            currentSessionId = null;
        }
    }

    public boolean hasSessionFor(long accountID) {
        return activeSessionIds.stream().anyMatch(id -> id.startsWith("S-" + accountID + "-"));
    }

    public boolean isLoggedIn() {
        return currentAccount != null;
    }

    public UserAccount currentAccount() {
        return currentAccount;
    }

    public long currentAccountId() {
        return currentAccount == null ? 0L : currentAccount.getId();
    }

    public Role currentRole() {
        return currentAccount == null ? null : currentAccount.getRole();
    }

    public String currentSessionId() {
        return currentSessionId;
    }

    public int activeSessionCount() {
        return activeSessionIds.size();
    }

    public void reset() {
        activeSessionIds.clear();
        currentAccount = null;
        currentSessionId = null;
    }
}
