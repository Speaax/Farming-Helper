package com.easyfarming.customrun;

import java.util.ArrayList;
import java.util.List;

/**
 * User-defined run: name and an ordered list of locations, each with its own
 * teleport option and selected patch types (herb, flower, allotment, tree, fruit tree, hops).
 */
public class CustomRun {
    private String name;
    private List<RunLocation> locations;

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
}
