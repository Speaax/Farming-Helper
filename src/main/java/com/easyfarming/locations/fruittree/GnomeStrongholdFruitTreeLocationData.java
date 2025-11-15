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
 * LocationData definition for Gnome Stronghold Fruit Tree patch.
 */
public class GnomeStrongholdFruitTreeLocationData {
    
    private static final WorldPoint GNOME_STRONGHOLD_FRUIT_TREE_PATCH_POINT = new WorldPoint(2475, 3446, 0);
    
    /**
     * Creates LocationData for Gnome Stronghold Fruit Tree patch.
     */
    public static LocationData create() {
        LocationData locationData = new LocationData(
            "Gnome Stronghold",
            false, // farmLimps
            GNOME_STRONGHOLD_FRUIT_TREE_PATCH_POINT,
            EasyFarmingConfig::enumFruitTreeGnomeStrongholdTeleport
        );
        
        // Royal seed pod
        locationData.addTeleport(new TeleportData(
            "Royal_seed_pod",
            Teleport.Category.ITEM,
            "Teleport to Gnome Stronghold with Royal seed pod and run south to the fruit tree patch.",
            ItemID.MM2_ROYAL_SEED_POD,
            "",
            0,
            0,
            9782,
            GNOME_STRONGHOLD_FRUIT_TREE_PATCH_POINT,
            () -> Collections.singletonList(
                new ItemRequirement(ItemID.MM2_ROYAL_SEED_POD, 1)
            )
        ));
        
        // Spirit Tree
        locationData.addTeleport(new TeleportData(
            "Spirit_Tree",
            Teleport.Category.SPIRIT_TREE,
            "Teleport to Gnome Stronghold via a Spirit Tree.",
            0,
            "",
            187,
            3,
            9781,
            GNOME_STRONGHOLD_FRUIT_TREE_PATCH_POINT,
            () -> Collections.emptyList()
        ));
        
        return locationData;
    }
}

