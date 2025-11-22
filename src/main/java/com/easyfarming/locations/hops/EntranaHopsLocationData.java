package com.easyfarming.locations.hops;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.core.Teleport;
import com.easyfarming.locations.LocationData;
import com.easyfarming.locations.TeleportData;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * LocationData definition for Entrana Hops patch.
 */
public class EntranaHopsLocationData {
    
    private static final WorldPoint ENTRANA_HOPS_PATCH_POINT = new WorldPoint(2811, 3337, 0);
    
    /**
     * Creates LocationData for Entrana Hops patch.
     * Note: Entrana requires no weapons/armor, accessed via boat or balloon.
     */
    public static LocationData create() {
        LocationData locationData = new LocationData(
            "Entrana",
            false, // farmLimps
            ENTRANA_HOPS_PATCH_POINT,
            EasyFarmingConfig::enumHopsEntranaTeleport
        );
        
        // Explorer's Ring (medium/hard/elite) to Port Sarim
        locationData.addTeleport(new TeleportData(
            "Explorers_Ring",
            Teleport.Category.ITEM,
            "Teleport to Port Sarim with Explorer's Ring, then take boat to Entrana and run to hops patch.",
            ItemID.LUMBRIDGE_RING_MEDIUM,
            "",
            0,
            0,
            11060,
            ENTRANA_HOPS_PATCH_POINT,
            () -> Collections.singletonList(
                new ItemRequirement(ItemID.LUMBRIDGE_RING_MEDIUM, 1)
            )
        ));
        
        
        return locationData;
    }
}

