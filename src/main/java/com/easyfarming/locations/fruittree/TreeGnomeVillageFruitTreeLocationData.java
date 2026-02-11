package com.easyfarming.locations.fruittree;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.core.Location;
import com.easyfarming.core.Teleport;
import net.runelite.api.coords.WorldPoint;

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
        
        // Spirit Tree (Royal seed pod teleports to Gnome Stronghold, not Tree Gnome Village)
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

