package com.easyfarming.locations;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.Location;
import com.easyfarming.core.Teleport;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.util.Collections;
import java.util.List;

/**
 * Location definition for Morytania.
 */
public class MorytaniaLocationData {
    
    private static final WorldPoint MORYTANIA_HERB_PATCH_POINT = new WorldPoint(3601, 3525, 0);
    
    /**
     * Gets the patch point for Morytania herb patch.
     * @return The WorldPoint for the Morytania herb patch
     */
    public static WorldPoint getPatchPoint() {
        return MORYTANIA_HERB_PATCH_POINT;
    }
    
    /**
     * Creates Location for Morytania.
     * @param config The EasyFarmingConfig instance
     * @return A Location instance for Morytania
     */
    public static Location create(EasyFarmingConfig config) {
        Location location = new Location(
            EasyFarmingConfig::enumOptionEnumMorytaniaTeleport,
            config,
            "Morytania",
            true // farmLimps
        );
        
        // Ectophial
        location.addTeleportOption(new Teleport(
            "Ectophial",
            Teleport.Category.ITEM,
            "Teleport to Morytania with Ectophial and run West to the patch.",
            ItemID.ECTOPHIAL,
            "",
            0,
            0,
            14647,
            MORYTANIA_HERB_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.ECTOPHIAL, 1)
            )
        ));
        
        return location;
    }
}

