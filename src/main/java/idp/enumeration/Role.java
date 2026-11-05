package idp.enumeration;

public enum Role {

    USER_ADMIN,
    INTERIOR_DESIGNER,
    CUSTOMER,
    PLATFORM_MANAGEMENT;

    public static Role fromString(String value) {
        if (value == null) {
            return null;
        }
        for (Role role : values()) {
            if (role.name().equalsIgnoreCase(value.trim())) {
                return role;
            }
        }
        return null;
    }
}
