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
 * LocationData definition for Varrock Tree patch.
 */
public class VarrockTreeLocationData {
    
    private static final WorldPoint VARROCK_TREE_PATCH_POINT = new WorldPoint(3229, 3459, 0);
    
    /**
     * Creates LocationData for Varrock Tree patch.
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     */
    public static LocationData create(Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        LocationData locationData = new LocationData(
            "Varrock",
            false, // farmLimps
            VARROCK_TREE_PATCH_POINT,
            EasyFarmingConfig::enumTreeVarrockTeleport
        );
        
        // Portal Nexus
        locationData.addTeleport(new TeleportData(
            "Portal_Nexus",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Varrock with Portal Nexus.",
            0,
            "",
            17,
            13,
            12853,
            VARROCK_TREE_PATCH_POINT,
            houseTeleportSupplier
        ));
        
        // Teleport (spellbook)
        locationData.addTeleport(new TeleportData(
            "Teleport",
            Teleport.Category.SPELLBOOK,
            "Teleport to Varrock with spellbook.",
            0,
            "",
            218,
            23,
            12853,
            VARROCK_TREE_PATCH_POINT,
            () -> Arrays.asList(
                new ItemRequirement(ItemID.AIRRUNE, 3),
                new ItemRequirement(ItemID.LAWRUNE, 1),
                new ItemRequirement(ItemID.FIRERUNE, 1)
            )
        ));
        
        // Varrock Tele Tab
        locationData.addTeleport(new TeleportData(
            "Varrock_Tele_Tab",
            Teleport.Category.ITEM,
            "Teleport to Varrock with Varrock Tele Tab.",
            ItemID.POH_TABLET_VARROCKTELEPORT,
            "",
            0,
            0,
            12853,
            VARROCK_TREE_PATCH_POINT,
            () -> Collections.singletonList(
                new ItemRequirement(ItemID.POH_TABLET_VARROCKTELEPORT, 1)
            )
        ));
        
        return locationData;
    }
}

