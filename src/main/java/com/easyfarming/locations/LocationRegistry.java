package com.easyfarming.locations;

import com.easyfarming.core.Location;
import com.easyfarming.items.RunType;
import java.util.HashMap;
import java.util.Map;

public class LocationRegistry {
    private final Map<RunType, Map<String, Location>> locationsByType;
    
    public LocationRegistry() {
        this.locationsByType = new HashMap<>();
        for (RunType runType : RunType.values()) {
            locationsByType.put(runType, new HashMap<>());
        }
    }
    
    public void registerLocation(RunType runType, String locationName, Location location) {
        locationsByType.get(runType).put(locationName, location);
    }
    
    public Location getLocation(RunType runType, String locationName) {
        Map<String, Location> locations = locationsByType.get(runType);
        return locations != null ? locations.get(locationName) : null;
    }
    
    public Map<String, Location> getLocationsForType(RunType runType) {
        return locationsByType.get(runType);
    }
}

