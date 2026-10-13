package com.easyfarming.locations;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.core.Location;
import com.easyfarming.core.Teleport;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Canonical factory that builds runtime {@link com.easyfarming.core.Location}
 * instances from declarative {@link LocationData}.
 *
 * <p>Pipeline: {@code *LocationData} / {@link LocationData} → {@link LocationFactory}
 * → {@link com.easyfarming.core.Location} → {@link com.easyfarming.customrun.LocationCatalog}.
 *
 * @see LocationData
 * @see TeleportData
 * @see com.easyfarming.core.Location
 */
public class LocationFactory {
    
    /**
     * Creates a {@link com.easyfarming.core.Location} from {@link LocationData},
     * including converted {@link com.easyfarming.core.Teleport} options.
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
     * Converts {@link TeleportData} to {@link com.easyfarming.core.Teleport}.
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
     * Creates multiple {@link com.easyfarming.core.Location} instances from a list of {@link LocationData}.
     */
    public static List<Location> createLocations(List<LocationData> locationDataList, EasyFarmingConfig config) {
        return locationDataList.stream()
            .map(data -> createLocation(data, config))
            .collect(Collectors.toList());
    }
}
