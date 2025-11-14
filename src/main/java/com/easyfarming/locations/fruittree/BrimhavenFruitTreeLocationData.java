package com.easyfarming.locations.fruittree;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.core.ItemRequirement;
import com.easyfarming.core.Teleport;
import com.easyfarming.locations.LocationData;
import com.easyfarming.locations.TeleportData;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * LocationData definition for Brimhaven Fruit Tree patch.
 */
public class BrimhavenFruitTreeLocationData {
    
    private static final WorldPoint BRIMHAVEN_FRUIT_TREE_PATCH_POINT = new WorldPoint(2764, 3212, 0);
    
    /**
     * Creates LocationData for Brimhaven Fruit Tree patch.
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     */
    public static LocationData create(Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        LocationData locationData = new LocationData(
            "Brimhaven",
            false, // farmLimps
            BRIMHAVEN_FRUIT_TREE_PATCH_POINT,
            EasyFarmingConfig::enumFruitTreeBrimhavenTeleport
        );
        
        // Portal Nexus
        locationData.addTeleport(new TeleportData(
            "Portal_Nexus",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Ardougne with Portal Nexus and take the boat to Brimhaven.",
            0,
            "null",
            17,
            13,
            10547,
            BRIMHAVEN_FRUIT_TREE_PATCH_POINT,
            houseTeleportSupplier
        ));
        
        // Ardougne teleport (spellbook)
        locationData.addTeleport(new TeleportData(
            "Ardougne_teleport",
            Teleport.Category.SPELLBOOK,
            "Teleport to Ardougne with Spellbook and take the boat to Brimhaven.",
            0,
            "null",
            218,
            41,
            10547,
            BRIMHAVEN_FRUIT_TREE_PATCH_POINT,
            () -> Arrays.asList(
                new com.easyfarming.core.ItemRequirement(ItemID.COINS, 30),
                new com.easyfarming.core.ItemRequirement(ItemID.LAWRUNE, 2),
                new com.easyfarming.core.ItemRequirement(ItemID.WATERRUNE, 2)
            )
        ));
        
        // Ardougne Tele Tab
        locationData.addTeleport(new TeleportData(
            "Ardougne_Tele_Tab",
            Teleport.Category.ITEM,
            "Teleport to Ardougne with Ardougne tele tab and take the boat to Brimhaven.",
            ItemID.POH_TABLET_ARDOUGNETELEPORT,
            "null",
            0,
            0,
            10547,
            BRIMHAVEN_FRUIT_TREE_PATCH_POINT,
            () -> Arrays.asList(
                new com.easyfarming.core.ItemRequirement(ItemID.POH_TABLET_ARDOUGNETELEPORT, 1),
                new com.easyfarming.core.ItemRequirement(ItemID.COINS, 30)
            )
        ));
        
        // POH Tele Tab
        locationData.addTeleport(new TeleportData(
            "POH_Tele_Tab",
            Teleport.Category.ITEM,
            "Teleport to house with POH tele tab, use Portal Nexus to Ardougne and take the boat to Brimhaven.",
            ItemID.POH_TABLET_TELEPORTTOHOUSE,
            "null",
            0,
            0,
            10547,
            BRIMHAVEN_FRUIT_TREE_PATCH_POINT,
            () -> Arrays.asList(
                new com.easyfarming.core.ItemRequirement(ItemID.POH_TABLET_TELEPORTTOHOUSE, 1),
                new com.easyfarming.core.ItemRequirement(ItemID.COINS, 30)
            )
        ));
        
        return locationData;
    }
}

