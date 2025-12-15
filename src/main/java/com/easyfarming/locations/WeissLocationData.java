package com.easyfarming.locations;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.core.Location;
import com.easyfarming.core.Teleport;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Location definition for Weiss.
 */
public class WeissLocationData {
    
    private static final WorldPoint WEISS_HERB_PATCH_POINT = new WorldPoint(2847, 3931, 0);
    
    /**
     * Gets the patch point for Weiss herb patch.
     * @return The WorldPoint for the Weiss herb patch
     */
    public static WorldPoint getPatchPoint() {
        return WEISS_HERB_PATCH_POINT;
    }
    
    /**
     * Creates Location for Weiss.
     * @param config The EasyFarmingConfig instance
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     *                              (typically from ItemAndLocation.getHouseTeleportItemRequirements())
     * @return A Location instance for Weiss
     */
    public static Location create(EasyFarmingConfig config, Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        Location location = new Location(
            EasyFarmingConfig::enumOptionEnumWeissTeleport,
            config,
            "Weiss",
            false // farmLimps
        );
        
        // Icy Basalt
        location.addTeleportOption(new Teleport(
            "Icy_Basalt",
            Teleport.Category.ITEM,
            "Teleport to Weiss with Icy Basalt.",
            ItemID.WEISS_TELEPORT_BASALT,
            "",
            0,
            0,
            11325,
            WEISS_HERB_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.WEISS_TELEPORT_BASALT, 1)
            )
        ));
        
        // Portal Nexus
        location.addTeleportOption(new Teleport(
            "Portal_Nexus",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Weiss with Portal Nexus.",
            0,
            "",
            17,
            13,
            11325,
            WEISS_HERB_PATCH_POINT,
            houseTeleportSupplier.get()
        ));
        
        return location;
    }
}

