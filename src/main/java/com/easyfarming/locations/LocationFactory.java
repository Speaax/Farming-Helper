package com.easyfarming.locations;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.Location;
import net.runelite.api.coords.WorldPoint;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Factory for creating Location instances from LocationData.
 * This allows locations to be defined as data rather than code.
 */
public class LocationFactory {
    
    /**
     * Creates a com.easyfarming.Location from LocationData.
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
            location.addTeleportOption(convertTeleportData(teleportData, location));
        }
        
        return location;
    }
    
    /**
     * Converts TeleportData to com.easyfarming.Location.Teleport.
     */
    private static Location.Teleport convertTeleportData(TeleportData teleportData, Location location) {
        // Convert core.Teleport.Category to Location.TeleportCategory
        Location.TeleportCategory category = convertCategory(teleportData.getCategory());
        
        // Convert core.ItemRequirement to com.easyfarming.ItemRequirement
        List<ItemRequirement> itemRequirements = teleportData.getItemRequirementsSupplier().get().stream()
            .map(ir -> new ItemRequirement(ir.getItemId(), ir.getQuantity()))
            .collect(java.util.stream.Collectors.toList());
        
        return location.new Teleport(
            teleportData.getEnumOption(),
            category,
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
     * Converts core.Teleport.Category to Location.TeleportCategory.
     */
    private static Location.TeleportCategory convertCategory(com.easyfarming.core.Teleport.Category category) {
        switch (category) {
            case ITEM: return Location.TeleportCategory.ITEM;
            case PORTAL_NEXUS: return Location.TeleportCategory.PORTAL_NEXUS;
            case SPIRIT_TREE: return Location.TeleportCategory.SPIRIT_TREE;
            case JEWELLERY_BOX: return Location.TeleportCategory.JEWELLERY_BOX;
            case MOUNTED_XERICS: return Location.TeleportCategory.MOUNTED_XERICS;
            case SPELLBOOK: return Location.TeleportCategory.SPELLBOOK;
            default: throw new IllegalArgumentException("Unknown category: " + category);
        }
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

