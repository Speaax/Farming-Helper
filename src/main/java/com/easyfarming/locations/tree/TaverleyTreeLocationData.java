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
 * LocationData definition for Taverley Tree patch.
 */
public class TaverleyTreeLocationData {
    
    private static final WorldPoint TAVERLEY_TREE_PATCH_POINT = new WorldPoint(2936, 3438, 0);
    
    /**
     * Creates LocationData for Taverley Tree patch.
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     */
    public static LocationData create(Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        LocationData locationData = new LocationData(
            "Taverley",
            false, // farmLimps
            TAVERLEY_TREE_PATCH_POINT,
            EasyFarmingConfig::enumTreeTaverleyTeleport
        );
        
        // Portal Nexus
        locationData.addTeleport(new TeleportData(
            "Portal_Nexus",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Falador with Portal Nexus and run to Taverly.",
            0,
            "",
            17,
            13,
            11828,
            TAVERLEY_TREE_PATCH_POINT,
            houseTeleportSupplier
        ));
        
        // Teleport (spellbook)
        locationData.addTeleport(new TeleportData(
            "Teleport",
            Teleport.Category.SPELLBOOK,
            "Teleport to Falador with spellbook and run to Taverly.",
            0,
            "",
            218,
            29,
            11828,
            TAVERLEY_TREE_PATCH_POINT,
            () -> Arrays.asList(
                new ItemRequirement(ItemID.AIRRUNE, 3),
                new ItemRequirement(ItemID.LAWRUNE, 1),
                new ItemRequirement(ItemID.WATERRUNE, 1)
            )
        ));
        
        // Falador Tele Tab
        locationData.addTeleport(new TeleportData(
            "Falador_Tele_Tab",
            Teleport.Category.ITEM,
            "Teleport to Falador with Falador Tele Tab and run to Taverly.",
            ItemID.POH_TABLET_FALADORTELEPORT,
            "",
            0,
            0,
            11828,
            TAVERLEY_TREE_PATCH_POINT,
            () -> Collections.singletonList(
                new ItemRequirement(ItemID.POH_TABLET_FALADORTELEPORT, 1)
            )
        ));
        
        return locationData;
    }
}

