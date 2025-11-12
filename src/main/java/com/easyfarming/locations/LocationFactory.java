package com.easyfarming.locations;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.core.Location;
import java.util.List;
import java.util.stream.Collectors;

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
public class LocationFactory {
    
    public static Location createLocation(LocationData locationData, EasyFarmingConfig config) {
        Location location = new Location(
            locationData.getConfigFunction(),
            config,
            locationData.getName(),
            locationData.getFarmLimps()
        );
        
        for (TeleportData teleportData : locationData.getTeleportOptions()) {
            location.addTeleportOption(teleportData.toTeleport());
        }
        
        return location;
    }
    
    public static List<Location> createLocations(List<LocationData> locationDataList, EasyFarmingConfig config) {
        return locationDataList.stream()
            .map(data -> createLocation(data, config))
            .collect(Collectors.toList());
    }
}

