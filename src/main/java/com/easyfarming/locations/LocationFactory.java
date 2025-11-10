package com.easyfarming.locations;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.core.Location;
import java.util.List;
import java.util.stream.Collectors;

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

