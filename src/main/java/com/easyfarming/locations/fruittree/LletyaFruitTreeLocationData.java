package com.easyfarming.locations.fruittree;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.core.Teleport;
import com.easyfarming.locations.LocationData;
import com.easyfarming.locations.TeleportData;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.util.Collections;

/**
 * LocationData definition for Lletya Fruit Tree patch.
 */
public class LletyaFruitTreeLocationData {
    
    private static final WorldPoint LLETYA_FRUIT_TREE_PATCH_POINT = new WorldPoint(2346, 3162, 0);
    
    /**
     * Creates LocationData for Lletya Fruit Tree patch.
     */
    public static LocationData create() {
        LocationData locationData = new LocationData(
            "Lletya",
            false, // farmLimps
            LLETYA_FRUIT_TREE_PATCH_POINT,
            EasyFarmingConfig::enumFruitTreeLletyaTeleport
        );
        
        // Teleport crystal
        locationData.addTeleport(new TeleportData(
            "Teleport_crystal",
            Teleport.Category.ITEM,
            "Teleport to Lletya with Teleport crystal.",
            ItemID.MOURNING_TELEPORT_CRYSTAL_1,
            "",
            0,
            0,
            9265,
            LLETYA_FRUIT_TREE_PATCH_POINT,
            () -> Collections.singletonList(
                new ItemRequirement(ItemID.MOURNING_TELEPORT_CRYSTAL_1, 1)
            )
        ));
        
        return locationData;
    }
}

