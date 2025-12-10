package com.easyfarming.locations.hops;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.Location;
import com.easyfarming.core.Teleport;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.util.Collections;
import java.util.List;

/**
 * Location definition for Entrana Hops patch.
 */
public class EntranaHopsLocationData {
    
    private static final WorldPoint ENTRANA_HOPS_PATCH_POINT = new WorldPoint(2811, 3337, 0);
    
    /**
     * Gets the patch point for Entrana Hops patch.
     * @return The WorldPoint for the Entrana Hops patch
     */
    public static WorldPoint getPatchPoint() {
        return ENTRANA_HOPS_PATCH_POINT;
    }
    
    /**
     * Creates Location for Entrana Hops patch.
     * Note: Entrana requires no weapons/armor, accessed via boat or balloon.
     * @param config The EasyFarmingConfig instance
     * @return A Location instance for Entrana Hops patch
     */
    public static Location create(EasyFarmingConfig config) {
        Location location = new Location(
            EasyFarmingConfig::enumHopsEntranaTeleport,
            config,
            "Entrana",
            false // farmLimps
        );
        
        // Explorer's Ring (medium/hard/elite) to Port Sarim
        location.addTeleportOption(new Teleport(
            "Explorers_Ring",
            Teleport.Category.ITEM,
            "Teleport to Port Sarim with Explorer's Ring, then take boat to Entrana and run to hops patch.",
            ItemID.LUMBRIDGE_RING_MEDIUM,
            "",
            0,
            0,
            11060,
            ENTRANA_HOPS_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.LUMBRIDGE_RING_MEDIUM, 1)
            )
        ));
        
        return location;
    }
}

