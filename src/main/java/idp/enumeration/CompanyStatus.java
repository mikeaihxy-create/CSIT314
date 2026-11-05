package idp.enumeration;

public enum CompanyStatus {

    ACTIVE,
    INACTIVE;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public static CompanyStatus fromString(String value) {
        if (value == null) {
            return ACTIVE;
        }
        for (CompanyStatus status : values()) {
            if (status.name().equalsIgnoreCase(value.trim())) {
                return status;
            }
        }
        return ACTIVE;
    }
}
