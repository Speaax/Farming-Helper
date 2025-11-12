package com.easyfarming.locations;

import com.easyfarming.core.Location;
import com.easyfarming.items.RunType;
import java.util.HashMap;
import java.util.Map;

/**
 * NOTE: This class is part of an incomplete refactoring effort.
 * 
 * This class and related classes in the locations.* package were designed to replace
 * the current ItemsAndLocations.* package structure, but the migration was never completed.
 * 
 * Currently unused - no instantiation found in the codebase.
 * 
 * SPARED FROM PURGING: This appears to be part of an unimplemented feature/refactoring
 * and may be completed in the future.
 */
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

