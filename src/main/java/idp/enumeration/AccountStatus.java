package idp.enumeration;

public enum AccountStatus {

    ACTIVE,
    SUSPENDED;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public static AccountStatus fromString(String value) {
        if (value == null) {
            return ACTIVE;
        }
        for (AccountStatus status : values()) {
            if (status.name().equalsIgnoreCase(value.trim())) {
                return status;
            }
        }
        return ACTIVE;
    }
}
