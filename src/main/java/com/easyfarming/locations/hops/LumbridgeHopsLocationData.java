package com.easyfarming.locations.hops;

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
 * LocationData definition for Lumbridge Hops patch.
 */
public class LumbridgeHopsLocationData {
    
    private static final WorldPoint LUMBRIDGE_HOPS_PATCH_POINT = new WorldPoint(3229, 3315, 0);
    
    /**
     * Creates LocationData for Lumbridge Hops patch.
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     */
    public static LocationData create(Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        LocationData locationData = new LocationData(
            "Lumbridge",
            false, // farmLimps
            LUMBRIDGE_HOPS_PATCH_POINT,
            EasyFarmingConfig::enumHopsLumbridgeTeleport
        );
        
        // Portal Nexus
        locationData.addTeleport(new TeleportData(
            "Portal_Nexus",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Lumbridge with Portal Nexus, and run north to hops patch.",
            0,
            "",
            17,
            13,
            12851,
            LUMBRIDGE_HOPS_PATCH_POINT,
            houseTeleportSupplier
        ));

        // Lumbridge Teleport (spellbook)
        locationData.addTeleport(new TeleportData(
            "Teleport",
            Teleport.Category.SPELLBOOK,
            "Teleport to Lumbridge with spellbook, and run north to hops patch.",
            0,
            "",
            218,
            26,
            12851,
            LUMBRIDGE_HOPS_PATCH_POINT,
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
            "Teleport to Lumbridge with Lumbridge Tele Tab, and run north to hops patch.",
            ItemID.POH_TABLET_LUMBRIDGETELEPORT,
            "",
            0,
            0,
            12851,
            LUMBRIDGE_HOPS_PATCH_POINT,
            () -> Collections.singletonList(
                new ItemRequirement(ItemID.POH_TABLET_LUMBRIDGETELEPORT, 1)
            )
        ));

        // Chronicle
        locationData.addTeleport(new TeleportData(
            "Chronicle",
            Teleport.Category.ITEM,
            "Teleport to Champions' Guild with Chronicle, and run south to hops patch.",
            ItemID.CHRONICLE,
            "",
            0,
            0,
            12851,
            LUMBRIDGE_HOPS_PATCH_POINT,
            () -> Collections.singletonList(
                new ItemRequirement(ItemID.CHRONICLE, 1)
            )
        ));
        
        
        return locationData;
    }
}

