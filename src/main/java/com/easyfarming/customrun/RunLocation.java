package com.easyfarming.customrun;

import java.util.ArrayList;
import java.util.List;

/**
 * One location in a custom run: location name, selected teleport (enum option string),
 * and which patch types are enabled at this location (herb, flower, allotment, tree, fruit tree, hops).
 */
public class RunLocation {
    private String locationName;
    private String teleportOption;
    private List<String> patchTypes;

    public RunLocation() {
        this.patchTypes = new ArrayList<>();
    }

    public RunLocation(String locationName, String teleportOption, List<String> patchTypes) {
        this.locationName = locationName;
        this.teleportOption = teleportOption;
        this.patchTypes = patchTypes != null ? new ArrayList<>(patchTypes) : new ArrayList<>();
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getTeleportOption() {
        return teleportOption;
    }

    public void setTeleportOption(String teleportOption) {
        this.teleportOption = teleportOption;
    }

    public List<String> getPatchTypes() {
        return patchTypes;
    }

    public void setPatchTypes(List<String> patchTypes) {
        this.patchTypes = patchTypes != null ? new ArrayList<>(patchTypes) : new ArrayList<>();
    }
}
