package idp.enumeration;

public enum ProjectStage {

    CURRENT,
    PAST;

    public boolean isCurrent() {
        return this == CURRENT;
    }

    public static ProjectStage fromString(String value) {
        if (value == null) {
            return CURRENT;
        }
        for (ProjectStage stage : values()) {
            if (stage.name().equalsIgnoreCase(value.trim())) {
                return stage;
            }
        }
        return CURRENT;
    }
}
