package com.easyfarming.customrun;

import java.util.ArrayList;
import java.util.List;

/**
 * User-defined run: name, tool requirements (secateurs, dibber, rake), and an ordered list
 * of locations, each with its own teleport option and selected patch types.
 */
public class CustomRun {
    private String name;
    private List<RunLocation> locations;
    /** Tool inclusion: saved with the run so Save persists current filter bar state. */
    private boolean includeSecateurs = true;
    private boolean includeDibber = true;
    private boolean includeRake = true;

    public CustomRun() {
        this.locations = new ArrayList<>();
    }

    public CustomRun(String name, List<RunLocation> locations) {
        this.name = name;
        this.locations = locations != null ? new ArrayList<>(locations) : new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RunLocation> getLocations() {
        return locations;
    }

    public void setLocations(List<RunLocation> locations) {
        this.locations = locations != null ? new ArrayList<>(locations) : new ArrayList<>();
    }

    public boolean isIncludeSecateurs() {
        return includeSecateurs;
    }

    public void setIncludeSecateurs(boolean includeSecateurs) {
        this.includeSecateurs = includeSecateurs;
    }

    public boolean isIncludeDibber() {
        return includeDibber;
    }

    public void setIncludeDibber(boolean includeDibber) {
        this.includeDibber = includeDibber;
    }

    public boolean isIncludeRake() {
        return includeRake;
    }

    public void setIncludeRake(boolean includeRake) {
        this.includeRake = includeRake;
    }
}
