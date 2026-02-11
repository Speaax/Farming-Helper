package com.easyfarming.locations.fruittree;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.core.Location;
import com.easyfarming.core.Teleport;
import com.easyfarming.utils.Constants;
import net.runelite.api.coords.WorldPoint;

import java.util.Collections;

/**
 * Location definition for Lletya Fruit Tree patch.
 */
public class LletyaFruitTreeLocationData {
    
    private static final WorldPoint LLETYA_FRUIT_TREE_PATCH_POINT = new WorldPoint(2346, 3162, 0);
    
    /**
     * Creates Location for Lletya Fruit Tree patch.
     * @param config The EasyFarmingConfig instance
     * @return A Location instance for Lletya Fruit Tree patch
     */
    public static Location create(EasyFarmingConfig config) {
        Location location = new Location(
            EasyFarmingConfig::enumFruitTreeLletyaTeleport,
            config,
            "Lletya",
            false // farmLimps
        );
        
        // Teleport crystal - any variant (Mourning's 1-5 or Eternal) satisfies
        location.addTeleportOption(new Teleport(
            "Teleport_crystal",
            Teleport.Category.ITEM,
            "Teleport to Lletya with teleport crystal.",
            Constants.BASE_TELEPORT_CRYSTAL_ID,
            "",
            0,
            0,
            9265,
            LLETYA_FRUIT_TREE_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(Constants.BASE_TELEPORT_CRYSTAL_ID, 1)
            )
        ));

        return location;
    }
}

