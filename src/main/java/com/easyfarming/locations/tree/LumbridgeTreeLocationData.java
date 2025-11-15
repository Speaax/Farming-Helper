package com.easyfarming.locations.tree;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
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
 * LocationData definition for Lumbridge Tree patch.
 */
public class LumbridgeTreeLocationData {
    
    private static final WorldPoint LUMBRIDGE_TREE_PATCH_POINT = new WorldPoint(3193, 3231, 0);
    
    /**
     * Creates LocationData for Lumbridge Tree patch.
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     */
    public static LocationData create(Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        LocationData locationData = new LocationData(
            "Lumbridge",
            false, // farmLimps
            LUMBRIDGE_TREE_PATCH_POINT,
            EasyFarmingConfig::enumTreeLumbridgeTeleport
        );
        
        // Portal Nexus
        locationData.addTeleport(new TeleportData(
            "Portal_Nexus",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Lumbridge with Portal Nexus.",
            0,
            "",
            17,
            13,
            12850,
            LUMBRIDGE_TREE_PATCH_POINT,
            houseTeleportSupplier
        ));
        
        // Teleport (spellbook)
        locationData.addTeleport(new TeleportData(
            "Teleport",
            Teleport.Category.SPELLBOOK,
            "Teleport to Lumbridge with spellbook.",
            0,
            "",
            218,
            26,
            12850,
            LUMBRIDGE_TREE_PATCH_POINT,
            () -> Arrays.asList(
                new ItemRequirement(ItemID.AIRRUNE, 3),
                new ItemRequirement(ItemID.LAWRUNE, 1),
                new ItemRequirement(ItemID.EARTHRUNE, 1)
            )
        ));
        
        // Lumbridge Tele Tab
        locationData.addTeleport(new TeleportData(
            "Lumbridge_Tele_Tab",
            Teleport.Category.ITEM,
            "Teleport to Lumbridge with Lumbridge Tele Tab.",
            ItemID.POH_TABLET_LUMBRIDGETELEPORT,
            "",
            0,
            0,
            12850,
            LUMBRIDGE_TREE_PATCH_POINT,
            () -> Collections.singletonList(
                new ItemRequirement(ItemID.POH_TABLET_LUMBRIDGETELEPORT, 1)
            )
        ));
        
        return locationData;
    }
}

