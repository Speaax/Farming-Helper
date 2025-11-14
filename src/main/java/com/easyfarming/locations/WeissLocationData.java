package com.easyfarming.locations;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.core.ItemRequirement;
import com.easyfarming.core.Teleport;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * LocationData definition for Weiss.
 */
public class WeissLocationData {
    
    private static final WorldPoint WEISS_HERB_PATCH_POINT = new WorldPoint(2847, 3931, 0);
    
    /**
     * Creates LocationData for Weiss.
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     *                              (typically from ItemAndLocation.getHouseTeleportItemRequirements())
     */
    public static LocationData create(Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        LocationData locationData = new LocationData(
            "Weiss",
            false, // farmLimps
            WEISS_HERB_PATCH_POINT,
            EasyFarmingConfig::enumOptionEnumWeissTeleport
        );
        
        // Icy Basalt
        locationData.addTeleport(new TeleportData(
            "Icy_Basalt",
            Teleport.Category.ITEM,
            "Teleport to Weiss with Icy Basalt.",
            ItemID.WEISS_TELEPORT_BASALT,
            "null",
            0,
            0,
            11325,
            WEISS_HERB_PATCH_POINT,
            () -> Collections.singletonList(
                new com.easyfarming.core.ItemRequirement(ItemID.WEISS_TELEPORT_BASALT, 1)
            )
        ));
        
        // Portal Nexus
        locationData.addTeleport(new TeleportData(
            "Portal_Nexus",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Weiss with Portal Nexus.",
            0,
            "null",
            17,
            13,
            11325,
            WEISS_HERB_PATCH_POINT,
            houseTeleportSupplier
        ));
        
        return locationData;
    }
}

