package com.easyfarming.locations;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.Location;
import com.easyfarming.core.Teleport;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Location definition for Harmony Island.
 */
public class HarmonyLocationData {
    
    private static final WorldPoint HARMONY_HERB_PATCH_POINT = new WorldPoint(3789, 2837, 0);
    
    /**
     * Gets the patch point for Harmony Island herb patch.
     * @return The WorldPoint for the Harmony herb patch
     */
    public static WorldPoint getPatchPoint() {
        return HARMONY_HERB_PATCH_POINT;
    }
    
    /**
     * Creates Location for Harmony Island.
     * @param config The EasyFarmingConfig instance
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     *                              (typically from ItemAndLocation.getHouseTeleportItemRequirements())
     * @return A Location instance for Harmony Island
     */
    public static Location create(EasyFarmingConfig config, Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        Location location = new Location(
            EasyFarmingConfig::enumOptionEnumHarmonyTeleport,
            config,
            "Harmony Island",
            false // farmLimps
        );
        
        // Portal Nexus
        location.addTeleportOption(new Teleport(
            "Portal_Nexus",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Harmony with Portal Nexus.",
            0,
            "",
            17,
            13,
            15148,
            HARMONY_HERB_PATCH_POINT,
            houseTeleportSupplier.get()
        ));
        
        // Harmony Tele Tab
        location.addTeleportOption(new Teleport(
            "Harmony_Tele_tab",
            Teleport.Category.ITEM,
            "Teleport to Harmony with Harmony Tele Tab.",
            ItemID.TELETAB_HARMONY,
            "",
            0,
            0,
            15148,
            HARMONY_HERB_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.TELETAB_HARMONY, 1)
            )
        ));
        
        return location;
    }
}

