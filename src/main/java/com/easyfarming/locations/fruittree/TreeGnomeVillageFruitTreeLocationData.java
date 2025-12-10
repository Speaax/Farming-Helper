package com.easyfarming.locations.fruittree;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.Location;
import com.easyfarming.core.Teleport;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.util.Collections;

/**
 * Location definition for Tree Gnome Village Fruit Tree patch.
 */
public class TreeGnomeVillageFruitTreeLocationData {
    
    private static final WorldPoint TREE_GNOME_VILLAGE_FRUIT_TREE_PATCH_POINT = new WorldPoint(2490, 3180, 0);
    
    /**
     * Creates Location for Tree Gnome Village Fruit Tree patch.
     * @param config The EasyFarmingConfig instance
     * @return A Location instance for Tree Gnome Village Fruit Tree patch
     */
    public static Location create(EasyFarmingConfig config) {
        Location location = new Location(
            EasyFarmingConfig::enumFruitTreeTreeGnomeVillageTeleport,
            config,
            "Tree Gnome Village",
            false // farmLimps
        );
        
        // Royal seed pod
        location.addTeleportOption(new Teleport(
            "Royal_seed_pod",
            Teleport.Category.ITEM,
            "Teleport to Tree Gnome Village with Royal seed pod and use Spirit tree to Tree Gnome Village.",
            ItemID.MM2_ROYAL_SEED_POD,
            "",
            0,
            0,
            9782,
            TREE_GNOME_VILLAGE_FRUIT_TREE_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.MM2_ROYAL_SEED_POD, 1)
            )
        ));
        
        // Spirit Tree
        location.addTeleportOption(new Teleport(
            "Spirit_Tree",
            Teleport.Category.SPIRIT_TREE,
            "Teleport to Tree Gnome Village via a Spirit Tree.",
            0,
            "",
            187,
            3,
            10033,
            TREE_GNOME_VILLAGE_FRUIT_TREE_PATCH_POINT,
            Collections.emptyList()
        ));
        
        return location;
    }
}

