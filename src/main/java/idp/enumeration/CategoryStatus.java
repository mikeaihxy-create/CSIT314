package idp.enumeration;

public enum CategoryStatus {

    ACTIVE,
    INACTIVE;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public static CategoryStatus fromString(String value) {
        if (value == null) {
            return ACTIVE;
        }
        for (CategoryStatus status : values()) {
            if (status.name().equalsIgnoreCase(value.trim())) {
                return status;
            }
        }
        return ACTIVE;
    }
}
