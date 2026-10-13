package com.easyfarming.customrun;

import java.util.Objects;

/**
 * Snapshot of the instruction currently shown in the farming info overlay,
 * used by the active-location pencil to edit the live step.
 */
public final class CurrentStepInstruction {
    public enum Kind {
        NAVIGATION,
        FARMING
    }

    private final Kind kind;
    private final String locationName;
    private final String teleportOption;
    private final String defaultText;

    private CurrentStepInstruction(Kind kind, String locationName, String teleportOption, String defaultText) {
        this.kind = kind;
        this.locationName = locationName;
        this.teleportOption = teleportOption;
        this.defaultText = defaultText != null ? defaultText : "";
    }

    public static CurrentStepInstruction navigation(String locationName, String teleportOption, String defaultText) {
        return new CurrentStepInstruction(Kind.NAVIGATION, locationName, teleportOption, defaultText);
    }

    public static CurrentStepInstruction farming(String locationName, String defaultText) {
        return new CurrentStepInstruction(Kind.FARMING, locationName, null, defaultText);
    }

    public Kind getKind() {
        return kind;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getTeleportOption() {
        return teleportOption;
    }

    public String getDefaultText() {
        return defaultText;
    }

    /** Stable identity for UI refresh dedupe. */
    public String identity() {
        if (kind == Kind.NAVIGATION) {
            return "nav|" + (locationName != null ? locationName : "") + "|"
                    + (teleportOption != null ? teleportOption : "");
        }
        return "farm|" + (locationName != null ? locationName : "") + "|" + defaultText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CurrentStepInstruction)) {
            return false;
        }
        CurrentStepInstruction that = (CurrentStepInstruction) o;
        return kind == that.kind
                && Objects.equals(locationName, that.locationName)
                && Objects.equals(teleportOption, that.teleportOption)
                && Objects.equals(defaultText, that.defaultText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, locationName, teleportOption, defaultText);
    }
}
