package com.easyfarming.locations;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.core.Location;
import com.easyfarming.core.Teleport;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Factory for creating Location instances from LocationData.
 * This allows locations to be defined as data rather than code.
 * 
 * Part of an incomplete refactor; do not modify without coordinating.
 * 
 * @deprecated This is part of an incomplete refactor. The factory pattern is still being used,
 *             but the architecture may change in future refactors. Prefer using
 *             {@link com.easyfarming.core.Teleport} as the canonical teleport model.
 */
@Deprecated
public class LocationFactory {
    
    /**
     * Creates a com.easyfarming.core.Location from LocationData.
     * This is the adapter method that bridges the new data-driven approach
     * with the current Location class structure.
     */
    public static Location createLocation(LocationData locationData, EasyFarmingConfig config) {
        Location location = new Location(
            locationData.getConfigFunction(),
            config,
            locationData.getName(),
            locationData.getFarmLimps()
        );
        
        for (TeleportData teleportData : locationData.getTeleportOptions()) {
            location.addTeleportOption(convertTeleportData(teleportData));
        }
        
        return location;
    }
    
    /**
     * Converts TeleportData to com.easyfarming.core.Teleport.
     */
    private static Teleport convertTeleportData(TeleportData teleportData) {
        // Get item requirements directly (no conversion needed)
        List<ItemRequirement> itemRequirements = teleportData.getItemRequirementsSupplier().get();
        
        return new Teleport(
            teleportData.getEnumOption(),
            teleportData.getCategory(),
            teleportData.getDescription(),
            teleportData.getId(),
            teleportData.getRightClickOption(),
            teleportData.getInterfaceGroupId(),
            teleportData.getInterfaceChildId(),
            teleportData.getRegionId(),
            teleportData.getPoint(),
            itemRequirements
        );
    }
    
    /**
     * Creates multiple locations from a list of LocationData.
     */
    public static List<Location> createLocations(List<LocationData> locationDataList, EasyFarmingConfig config) {
        return locationDataList.stream()
            .map(data -> createLocation(data, config))
            .collect(Collectors.toList());
    }
}

