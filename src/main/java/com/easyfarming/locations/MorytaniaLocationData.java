package com.easyfarming.locations;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.core.Teleport;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.util.Collections;
import java.util.List;

/**
 * LocationData definition for Morytania.
 */
public class MorytaniaLocationData {
    
    private static final WorldPoint MORYTANIA_HERB_PATCH_POINT = new WorldPoint(3601, 3525, 0);
    
    /**
     * Creates LocationData for Morytania.
     */
    public static LocationData create() {
        LocationData locationData = new LocationData(
            "Morytania",
            true, // farmLimps
            MORYTANIA_HERB_PATCH_POINT,
            EasyFarmingConfig::enumOptionEnumMorytaniaTeleport
        );
        
        // Ectophial
        locationData.addTeleport(new TeleportData(
            "Ectophial",
            Teleport.Category.ITEM,
            "Teleport to Morytania with Ectophial and run West to the patch.",
            ItemID.ECTOPHIAL,
            "",
            0,
            0,
            14647,
            MORYTANIA_HERB_PATCH_POINT,
            () -> Collections.singletonList(
                new ItemRequirement(ItemID.ECTOPHIAL, 1)
            )
        ));
        
        return locationData;
    }
}

