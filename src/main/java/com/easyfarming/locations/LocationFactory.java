package com.easyfarming.locations;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.Location;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Factory for creating Location instances from LocationData.
 * This allows locations to be defined as data rather than code.
 */
public class LocationFactory {
    
    /**
     * Enum-safe mapping from core.Teleport.Category to Location.TeleportCategory.
     * This ensures all enum values are explicitly mapped and prevents runtime errors
     * when new enum constants are added.
     * Package-private for testing.
     */
    static final EnumMap<com.easyfarming.core.Teleport.Category, Location.TeleportCategory> CATEGORY_MAP = new EnumMap<>(com.easyfarming.core.Teleport.Category.class);
    
    static {
        CATEGORY_MAP.put(com.easyfarming.core.Teleport.Category.ITEM, Location.TeleportCategory.ITEM);
        CATEGORY_MAP.put(com.easyfarming.core.Teleport.Category.PORTAL_NEXUS, Location.TeleportCategory.PORTAL_NEXUS);
        CATEGORY_MAP.put(com.easyfarming.core.Teleport.Category.SPIRIT_TREE, Location.TeleportCategory.SPIRIT_TREE);
        CATEGORY_MAP.put(com.easyfarming.core.Teleport.Category.JEWELLERY_BOX, Location.TeleportCategory.JEWELLERY_BOX);
        CATEGORY_MAP.put(com.easyfarming.core.Teleport.Category.MOUNTED_XERICS, Location.TeleportCategory.MOUNTED_XERICS);
        CATEGORY_MAP.put(com.easyfarming.core.Teleport.Category.SPELLBOOK, Location.TeleportCategory.SPELLBOOK);
    }
    
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
        
        // Get item requirements directly (no conversion needed)
        List<ItemRequirement> itemRequirements = teleportData.getItemRequirementsSupplier().get();
        
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
     * Uses an EnumMap for enum-safe mapping that will fail fast if a new enum constant
     * is added without a corresponding mapping.
     * 
     * @param category the category to convert
     * @return the corresponding Location.TeleportCategory
     * @throws IllegalArgumentException if the category has no mapping
     */
    private static Location.TeleportCategory convertCategory(com.easyfarming.core.Teleport.Category category) {
        Location.TeleportCategory result = CATEGORY_MAP.get(category);
        if (result == null) {
            throw new IllegalArgumentException("No mapping found for category: " + category + ". Please add a mapping to CATEGORY_MAP.");
        }
        return result;
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

