package idp.enumeration;

public enum EventType {

    PROJECT_VIEW,
    PROJECT_SHORTLIST,
    PROJECT_CREATE,
    PROJECT_UPDATE,
    STATUS_CHANGE;

    public static EventType fromString(String value) {
        if (value == null) {
            return null;
        }
        for (EventType type : values()) {
            if (type.name().equalsIgnoreCase(value.trim())) {
                return type;
            }
        }
        return null;
    }
}
