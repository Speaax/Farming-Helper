package com.easyfarming.locations.fruittree;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.Location;
import com.easyfarming.core.Teleport;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * Location definition for Brimhaven Fruit Tree patch.
 */
public class BrimhavenFruitTreeLocationData {
    
    private static final WorldPoint BRIMHAVEN_FRUIT_TREE_PATCH_POINT = new WorldPoint(2764, 3212, 0);
    
    /**
     * Creates Location for Brimhaven Fruit Tree patch.
     * @param config The EasyFarmingConfig instance
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     * @return A Location instance for Brimhaven Fruit Tree patch
     */
    public static Location create(EasyFarmingConfig config, Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        Location location = new Location(
            EasyFarmingConfig::enumFruitTreeBrimhavenTeleport,
            config,
            "Brimhaven",
            false // farmLimps
        );
        
        // Portal Nexus
        location.addTeleportOption(new Teleport(
            "Portal_Nexus",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Ardougne with Portal Nexus and take the boat to Brimhaven.",
            0,
            "",
            17,
            13,
            10547,
            BRIMHAVEN_FRUIT_TREE_PATCH_POINT,
            houseTeleportSupplier.get()
        ));
        
        // Ardougne teleport (spellbook)
        location.addTeleportOption(new Teleport(
            "Ardougne_teleport",
            Teleport.Category.SPELLBOOK,
            "Teleport to Ardougne with Spellbook and take the boat to Brimhaven.",
            0,
            "",
            218,
            41,
            10547,
            BRIMHAVEN_FRUIT_TREE_PATCH_POINT,
            Arrays.asList(
                new ItemRequirement(ItemID.COINS, 30),
                new ItemRequirement(ItemID.LAWRUNE, 2),
                new ItemRequirement(ItemID.WATERRUNE, 2)
            )
        ));
        
        // Ardougne Tele Tab
        location.addTeleportOption(new Teleport(
            "Ardougne_Tele_Tab",
            Teleport.Category.ITEM,
            "Teleport to Ardougne with Ardougne tele tab and take the boat to Brimhaven.",
            ItemID.POH_TABLET_ARDOUGNETELEPORT,
            "",
            0,
            0,
            10547,
            BRIMHAVEN_FRUIT_TREE_PATCH_POINT,
            Arrays.asList(
                new ItemRequirement(ItemID.POH_TABLET_ARDOUGNETELEPORT, 1),
                new ItemRequirement(ItemID.COINS, 30)
            )
        ));
        
        // POH Tele Tab
        location.addTeleportOption(new Teleport(
            "POH_Tele_Tab",
            Teleport.Category.ITEM,
            "Teleport to house with POH tele tab, use Portal Nexus to Ardougne and take the boat to Brimhaven.",
            ItemID.POH_TABLET_TELEPORTTOHOUSE,
            "",
            0,
            0,
            10547,
            BRIMHAVEN_FRUIT_TREE_PATCH_POINT,
            Arrays.asList(
                new ItemRequirement(ItemID.POH_TABLET_TELEPORTTOHOUSE, 1),
                new ItemRequirement(ItemID.COINS, 30)
            )
        ));
        
        return location;
    }
}

