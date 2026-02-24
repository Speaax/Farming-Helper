package com.easyfarming.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomRun {
    private String name;
    private List<CustomLocation> locations;
    private Map<String, Integer> filterStates;

    public CustomRun(String name, List<CustomLocation> locations) {
        this.name = name;
        this.locations = locations;
        this.filterStates = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CustomLocation> getLocations() {
        return locations;
    }

    public void setLocations(List<CustomLocation> locations) {
        this.locations = locations;
    }

    public Map<String, Integer> getFilterStates() {
        if (filterStates == null) {
            filterStates = new HashMap<>();
        }
        return filterStates;
    }

    public void setFilterStates(Map<String, Integer> filterStates) {
        this.filterStates = filterStates;
    }
}
