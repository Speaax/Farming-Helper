package com.easyfarming.locations;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.core.Teleport;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * LocationData definition for Harmony Island.
 */
public class HarmonyLocationData {
    
    private static final WorldPoint HARMONY_HERB_PATCH_POINT = new WorldPoint(3789, 2837, 0);
    
    /**
     * Creates LocationData for Harmony Island.
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     *                              (typically from ItemAndLocation.getHouseTeleportItemRequirements())
     */
    public static LocationData create(Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        LocationData locationData = new LocationData(
            "Harmony Island",
            false, // farmLimps
            HARMONY_HERB_PATCH_POINT,
            EasyFarmingConfig::enumOptionEnumHarmonyTeleport
        );
        
        // Portal Nexus
        locationData.addTeleport(new TeleportData(
            "Portal_Nexus",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Harmony with Portal Nexus.",
            0,
            "",
            17,
            13,
            15148,
            HARMONY_HERB_PATCH_POINT,
            houseTeleportSupplier
        ));
        
        // Harmony Tele Tab
        locationData.addTeleport(new TeleportData(
            "Harmony_Tele_tab",
            Teleport.Category.ITEM,
            "Teleport to Harmony with Harmony Tele Tab.",
            ItemID.TELETAB_HARMONY,
            "",
            0,
            0,
            15148,
            HARMONY_HERB_PATCH_POINT,
            () -> Collections.singletonList(
                new ItemRequirement(ItemID.TELETAB_HARMONY, 1)
            )
        ));
        
        return locationData;
    }
}

