package com.easyfarming.locations;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.core.Teleport;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * LocationData definition for Falador.
 */
public class FaladorLocationData {
    
    private static final WorldPoint FALADOR_HERB_PATCH_POINT = new WorldPoint(3058, 3307, 0);
    
    /**
     * Creates LocationData for Falador.
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     *                              (typically from ItemAndLocation.getHouseTeleportItemRequirements())
     */
    public static LocationData create(Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        LocationData locationData = new LocationData(
            "Falador",
            true, // farmLimps
            FALADOR_HERB_PATCH_POINT,
            EasyFarmingConfig::enumOptionEnumFaladorTeleport
        );
        
        // Portal Nexus
        locationData.addTeleport(new TeleportData(
            "Portal_Nexus",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Falador with Portal Nexus, and run south-east.",
            0,
            "null",
            17,
            13,
            11828,
            FALADOR_HERB_PATCH_POINT,
            houseTeleportSupplier
        ));
        
        // Explorers ring
        locationData.addTeleport(new TeleportData(
            "Explorers_ring",
            Teleport.Category.ITEM,
            "Teleport to Falador with Explorers ring, and run slightly north.",
            ItemID.LUMBRIDGE_RING_MEDIUM,
            "Teleport",
            0,
            0,
            12083,
            FALADOR_HERB_PATCH_POINT,
            () -> Collections.singletonList(
                new ItemRequirement(ItemID.LUMBRIDGE_RING_MEDIUM, 1)
            )
        ));
        
        // Falador Teleport (spellbook)
        locationData.addTeleport(new TeleportData(
            "Falador_Teleport",
            Teleport.Category.SPELLBOOK,
            "Teleport to Falador with standard spellbook, and run south-east.",
            0,
            "null",
            218,
            29,
            11828,
            FALADOR_HERB_PATCH_POINT,
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
            "Teleport to Falador with Falador Tele Tab, and run south-east.",
            ItemID.POH_TABLET_FALADORTELEPORT,
            "null",
            0,
            0,
            11828,
            FALADOR_HERB_PATCH_POINT,
            () -> Collections.singletonList(
                new ItemRequirement(ItemID.POH_TABLET_FALADORTELEPORT, 1)
            )
        ));
        
        // Draynor Tele Tab
        locationData.addTeleport(new TeleportData(
            "Draynor_Tele_Tab",
            Teleport.Category.ITEM,
            "Teleport to Draynor Manor with Draynor Manor Tele Tab, and run south-west.",
            ItemID.TELETAB_DRAYNOR,
            "null",
            0,
            0,
            12340,
            FALADOR_HERB_PATCH_POINT,
            () -> Collections.singletonList(
                new ItemRequirement(ItemID.TELETAB_DRAYNOR, 1)
            )
        ));
        
        return locationData;
    }
}

