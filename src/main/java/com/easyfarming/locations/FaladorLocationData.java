package com.easyfarming.locations;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.core.Location;
import com.easyfarming.core.Teleport;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Location definition for Falador.
 */
public class FaladorLocationData {
    
    private static final WorldPoint FALADOR_HERB_PATCH_POINT = new WorldPoint(3058, 3307, 0);
    
    /**
     * Gets the patch point for Falador herb patch.
     * @return The WorldPoint for the Falador herb patch
     */
    public static WorldPoint getPatchPoint() {
        return FALADOR_HERB_PATCH_POINT;
    }
    
    /**
     * Creates Location for Falador.
     * @param config The EasyFarmingConfig instance
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     *                              (typically from ItemAndLocation.getHouseTeleportItemRequirements())
     * @return A Location instance for Falador
     */
    public static Location create(EasyFarmingConfig config, Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        Location location = new Location(
            EasyFarmingConfig::enumOptionEnumFaladorTeleport,
            config,
            "Falador",
            true // farmLimps
        );
        
        // Portal Nexus
        location.addTeleportOption(new Teleport(
            "Portal_Nexus",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Falador with Portal Nexus, and run south-east.",
            0,
            "",
            17,
            13,
            11828,
            FALADOR_HERB_PATCH_POINT,
            houseTeleportSupplier.get()
        ));
        
        // Explorers ring
        location.addTeleportOption(new Teleport(
            "Explorers_ring",
            Teleport.Category.ITEM,
            "Teleport to Falador with Explorers ring, and run slightly north.",
            ItemID.LUMBRIDGE_RING_MEDIUM,
            "Teleport",
            0,
            0,
            12083,
            FALADOR_HERB_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.LUMBRIDGE_RING_MEDIUM, 1)
            )
        ));
        
        // Falador Teleport (spellbook)
        location.addTeleportOption(new Teleport(
            "Falador_Teleport",
            Teleport.Category.SPELLBOOK,
            "Teleport to Falador with standard spellbook, and run south-east.",
            0,
            "",
            218,
            29,
            11828,
            FALADOR_HERB_PATCH_POINT,
            Arrays.asList(
                new ItemRequirement(ItemID.AIRRUNE, 3),
                new ItemRequirement(ItemID.LAWRUNE, 1),
                new ItemRequirement(ItemID.WATERRUNE, 1)
            )
        ));
        
        // Falador Tele Tab
        location.addTeleportOption(new Teleport(
            "Falador_Tele_Tab",
            Teleport.Category.ITEM,
            "Teleport to Falador with Falador Tele Tab, and run south-east.",
            ItemID.POH_TABLET_FALADORTELEPORT,
            "",
            0,
            0,
            11828,
            FALADOR_HERB_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.POH_TABLET_FALADORTELEPORT, 1)
            )
        ));
        
        // Draynor Tele Tab
        location.addTeleportOption(new Teleport(
            "Draynor_Tele_Tab",
            Teleport.Category.ITEM,
            "Teleport to Draynor Manor with Draynor Manor Tele Tab, and run south-west.",
            ItemID.TELETAB_DRAYNOR,
            "",
            0,
            0,
            12340,
            FALADOR_HERB_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.TELETAB_DRAYNOR, 1)
            )
        ));
        
        return location;
    }
}

