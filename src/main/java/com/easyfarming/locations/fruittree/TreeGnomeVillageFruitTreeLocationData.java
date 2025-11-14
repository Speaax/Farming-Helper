package com.easyfarming.locations.fruittree;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.core.ItemRequirement;
import com.easyfarming.core.Teleport;
import com.easyfarming.locations.LocationData;
import com.easyfarming.locations.TeleportData;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.util.Collections;

/**
 * LocationData definition for Tree Gnome Village Fruit Tree patch.
 */
public class TreeGnomeVillageFruitTreeLocationData {
    
    private static final WorldPoint TREE_GNOME_VILLAGE_FRUIT_TREE_PATCH_POINT = new WorldPoint(2490, 3180, 0);
    
    /**
     * Creates LocationData for Tree Gnome Village Fruit Tree patch.
     */
    public static LocationData create() {
        LocationData locationData = new LocationData(
            "Tree Gnome Village",
            false, // farmLimps
            TREE_GNOME_VILLAGE_FRUIT_TREE_PATCH_POINT,
            EasyFarmingConfig::enumFruitTreeTreeGnomeVillageTeleport
        );
        
        // Royal seed pod
        locationData.addTeleport(new TeleportData(
            "Royal_seed_pod",
            Teleport.Category.ITEM,
            "Teleport to Tree Gnome Village with Royal seed pod and use Spirit tree to Tree Gnome Village.",
            ItemID.MM2_ROYAL_SEED_POD,
            "null",
            0,
            0,
            9782,
            TREE_GNOME_VILLAGE_FRUIT_TREE_PATCH_POINT,
            () -> Collections.singletonList(
                new com.easyfarming.core.ItemRequirement(ItemID.MM2_ROYAL_SEED_POD, 1)
            )
        ));
        
        // Spirit Tree
        locationData.addTeleport(new TeleportData(
            "Spirit_Tree",
            Teleport.Category.SPIRIT_TREE,
            "Teleport to Tree Gnome Village via a Spirit Tree.",
            0,
            "null",
            187,
            3,
            10033,
            TREE_GNOME_VILLAGE_FRUIT_TREE_PATCH_POINT,
            () -> Collections.emptyList()
        ));
        
        return locationData;
    }
}

