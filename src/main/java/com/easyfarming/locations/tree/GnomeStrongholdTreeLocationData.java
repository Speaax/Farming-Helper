package com.easyfarming.locations.tree;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.core.Location;
import com.easyfarming.core.Teleport;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.util.Collections;
import java.util.List;

/**
 * Location definition for Gnome Stronghold Tree patch.
 */
public class GnomeStrongholdTreeLocationData {

    private static final WorldPoint GNOME_STRONGHOLD_TREE_PATCH_POINT = new WorldPoint(2436, 3415, 0);

    /**
     * Creates Location for Gnome Stronghold Tree patch.
     * @param config The EasyFarmingConfig instance
     * @return A Location instance for Gnome Stronghold Tree patch
     */
    public static Location create(EasyFarmingConfig config) {
        Location location = new Location(
            EasyFarmingConfig::enumTreeGnomeStrongoldTeleport,
            config,
            "Gnome Stronghold",
            false // farmLimps
        );

        // Royal seed pod
        location.addTeleportOption(new Teleport(
            "Royal_seed_pod",
            Teleport.Category.ITEM,
            "Teleport to Gnome Stronghold with Royal seed pod.",
            ItemID.MM2_ROYAL_SEED_POD,
            "",
            0,
            0,
            9782,
            GNOME_STRONGHOLD_TREE_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.MM2_ROYAL_SEED_POD, 1)
            )
        ));

        // Spirit Tree
        location.addTeleportOption(new Teleport(
            "Spirit_Tree",
            Teleport.Category.SPIRIT_TREE,
            "Teleport to Gnome Stronghold via a Spirit Tree.",
            0,
            "",
            187,
            3,
            9781,
            GNOME_STRONGHOLD_TREE_PATCH_POINT,
            Collections.emptyList()
        ));

        // Slayer Ring (to Stronghold Slayer Cave, run to tree patch)
        location.addTeleportOption(new Teleport(
            "Slayer_Ring",
            Teleport.Category.ITEM,
            "Teleport to Stronghold Slayer Cave with Slayer Ring and run to Gnome Stronghold tree patch.",
            ItemID.RING_OF_SLAYING,
            "",
            0,
            0,
            9781,
            GNOME_STRONGHOLD_TREE_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.RING_OF_SLAYING, 1)
            )
        ));

        return location;
    }
}

