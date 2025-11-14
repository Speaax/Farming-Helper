package com.easyfarming.locations.fruittree;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.core.ItemRequirement;
import com.easyfarming.core.Teleport;
import com.easyfarming.locations.LocationData;
import com.easyfarming.locations.TeleportData;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * LocationData definition for Catherby Fruit Tree patch.
 */
public class CatherbyFruitTreeLocationData {
    
    private static final WorldPoint CATHERBY_FRUIT_TREE_PATCH_POINT = new WorldPoint(2860, 3433, 0);
    
    /**
     * Creates LocationData for Catherby Fruit Tree patch.
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     */
    public static LocationData create(Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        LocationData locationData = new LocationData(
            "Catherby",
            false, // farmLimps
            CATHERBY_FRUIT_TREE_PATCH_POINT,
            EasyFarmingConfig::enumFruitTreeCatherbyTeleport
        );
        
        // Portal Nexus Catherby
        locationData.addTeleport(new TeleportData(
            "Portal_Nexus_Catherby",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Catherby with Portal Nexus.",
            0,
            "null",
            17,
            13,
            11061,
            CATHERBY_FRUIT_TREE_PATCH_POINT,
            houseTeleportSupplier
        ));
        
        // Portal Nexus Camelot
        locationData.addTeleport(new TeleportData(
            "Portal_Nexus_Camelot",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Camelot with Portal Nexus.",
            0,
            "null",
            17,
            13,
            11062,
            CATHERBY_FRUIT_TREE_PATCH_POINT,
            houseTeleportSupplier
        ));
        
        // Camelot Teleport (spellbook)
        locationData.addTeleport(new TeleportData(
            "Camelot_Teleport",
            Teleport.Category.SPELLBOOK,
            "Teleport to Camelot using the standard spellbook, and run east. (If you have configured the teleport to seers you need to right click and teleport to Camelot)",
            0,
            "null",
            218,
            34,
            11062,
            CATHERBY_FRUIT_TREE_PATCH_POINT,
            () -> Arrays.asList(
                new com.easyfarming.core.ItemRequirement(ItemID.AIRRUNE, 5),
                new com.easyfarming.core.ItemRequirement(ItemID.LAWRUNE, 1)
            )
        ));
        
        // Camelot Tele Tab
        locationData.addTeleport(new TeleportData(
            "Camelot_Tele_Tab",
            Teleport.Category.ITEM,
            "Teleport to Camelot using a Camelot tele tab, and run east.(If you have configured the teleport to seers you need to right click and teleport to Camelot)",
            ItemID.POH_TABLET_CAMELOTTELEPORT,
            "null",
            0,
            0,
            11062,
            CATHERBY_FRUIT_TREE_PATCH_POINT,
            () -> Collections.singletonList(
                new com.easyfarming.core.ItemRequirement(ItemID.POH_TABLET_CAMELOTTELEPORT, 1)
            )
        ));
        
        // Catherby Tele Tab
        locationData.addTeleport(new TeleportData(
            "Catherby_Tele_Tab",
            Teleport.Category.ITEM,
            "Teleport to Catherby using Catherby teleport tab.",
            ItemID.LUNAR_TABLET_CATHERBY_TELEPORT,
            "null",
            0,
            0,
            11061,
            CATHERBY_FRUIT_TREE_PATCH_POINT,
            () -> Collections.singletonList(
                new com.easyfarming.core.ItemRequirement(ItemID.LUNAR_TABLET_CATHERBY_TELEPORT, 1)
            )
        ));
        
        return locationData;
    }
}

