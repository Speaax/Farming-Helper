package com.easyfarming.locations.tree;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.core.ItemRequirement;
import com.easyfarming.core.Teleport;
import com.easyfarming.locations.LocationData;
import com.easyfarming.locations.TeleportData;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.util.Collections;
import java.util.List;

/**
 * LocationData definition for Gnome Stronghold Tree patch.
 */
public class GnomeStrongholdTreeLocationData {
    
    private static final WorldPoint GNOME_STRONGHOLD_TREE_PATCH_POINT = new WorldPoint(2436, 3415, 0);
    
    /**
     * Creates LocationData for Gnome Stronghold Tree patch.
     */
    public static LocationData create() {
        LocationData locationData = new LocationData(
            "Gnome Stronghold",
            false, // farmLimps
            GNOME_STRONGHOLD_TREE_PATCH_POINT,
            EasyFarmingConfig::enumTreeGnomeStrongoldTeleport
        );
        
        // Royal seed pod
        locationData.addTeleport(new TeleportData(
            "Royal_seed_pod",
            Teleport.Category.ITEM,
            "Teleport to Gnome Stronghold with Royal seed pod.",
            ItemID.MM2_ROYAL_SEED_POD,
            "null",
            0,
            0,
            9782,
            GNOME_STRONGHOLD_TREE_PATCH_POINT,
            () -> Collections.singletonList(
                new com.easyfarming.core.ItemRequirement(ItemID.MM2_ROYAL_SEED_POD, 1)
            )
        ));
        
        // Spirit Tree
        locationData.addTeleport(new TeleportData(
            "Spirit_Tree",
            Teleport.Category.SPIRIT_TREE,
            "Teleport to Gnome Stronghold via a Spirit Tree.",
            0,
            "null",
            187,
            3,
            9781,
            GNOME_STRONGHOLD_TREE_PATCH_POINT,
            () -> Collections.emptyList()
        ));
        
        return locationData;
    }
}

