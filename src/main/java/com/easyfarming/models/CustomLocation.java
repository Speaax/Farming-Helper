package com.easyfarming.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomLocation {
    private String name;
    private List<String> enabledPatches; // This operates as the ordered list of patches
    private Map<String, Boolean> patchActiveStates; // Tracks the on/off state of each patch
    private String teleportOption;

    public CustomLocation(String name, List<String> enabledPatches, String teleportOption) {
        this.name = name;
        this.enabledPatches = enabledPatches;
        this.teleportOption = teleportOption;
        this.patchActiveStates = new HashMap<>();
        if (enabledPatches != null) {
            for (String patch : enabledPatches) {
                this.patchActiveStates.put(patch, false);
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getEnabledPatches() {
        return enabledPatches;
    }

    public void setEnabledPatches(List<String> enabledPatches) {
        this.enabledPatches = enabledPatches;
    }

    public String getTeleportOption() {
        return teleportOption;
    }

    public void setTeleportOption(String teleportOption) {
        this.teleportOption = teleportOption;
    }

    public Map<String, Boolean> getPatchActiveStates() {
        if (patchActiveStates == null) {
            patchActiveStates = new HashMap<>();
            if (enabledPatches != null) {
                for (String patch : enabledPatches) {
                    patchActiveStates.put(patch, false);
                }
            }
        }
        return patchActiveStates;
    }

    public void setPatchActiveStates(Map<String, Boolean> patchActiveStates) {
        this.patchActiveStates = patchActiveStates;
    }
}
