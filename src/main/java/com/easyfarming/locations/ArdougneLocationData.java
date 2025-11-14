package com.easyfarming.locations;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.core.ItemRequirement;
import com.easyfarming.core.Teleport;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * LocationData definition for Ardougne.
 */
public class ArdougneLocationData {
    
    private static final WorldPoint ARDOUGNE_HERB_PATCH_POINT = new WorldPoint(2670, 3374, 0);
    
    /**
     * Creates LocationData for Ardougne.
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     *                              (typically from ItemAndLocation.getHouseTeleportItemRequirements())
     */
    public static LocationData create(Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        LocationData locationData = new LocationData(
            "Ardougne",
            true, // farmLimps
            ARDOUGNE_HERB_PATCH_POINT,
            EasyFarmingConfig::enumOptionEnumArdougneTeleport
        );
        
        // Portal Nexus teleport
        locationData.addTeleport(new TeleportData(
            "Portal_Nexus",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Ardougne with Portal Nexus, and run north.",
            0,
            "null",
            17,
            13,
            10547,
            ARDOUGNE_HERB_PATCH_POINT,
            houseTeleportSupplier
        ));
        
        // Ardougne teleport (spellbook)
        locationData.addTeleport(new TeleportData(
            "Ardougne_teleport",
            Teleport.Category.SPELLBOOK,
            "Teleport to Ardougne with standard spellbook, and run north.",
            0,
            "null",
            218,
            41,
            10547,
            ARDOUGNE_HERB_PATCH_POINT,
            () -> Arrays.asList(
                new com.easyfarming.core.ItemRequirement(ItemID.LAWRUNE, 2),
                new com.easyfarming.core.ItemRequirement(ItemID.WATERRUNE, 2)
            )
        ));
        
        // Ardougne Tele Tab
        locationData.addTeleport(new TeleportData(
            "Ardougne_Tele_Tab",
            Teleport.Category.ITEM,
            "Teleport to Ardougne with Ardougne tele tab, and run north.",
            ItemID.POH_TABLET_ARDOUGNETELEPORT,
            "null",
            0,
            0,
            10547,
            ARDOUGNE_HERB_PATCH_POINT,
            () -> Collections.singletonList(
                new com.easyfarming.core.ItemRequirement(ItemID.POH_TABLET_ARDOUGNETELEPORT, 1)
            )
        ));
        
        // Ardy cloak
        locationData.addTeleport(new TeleportData(
            "Ardy_cloak",
            Teleport.Category.ITEM,
            "Teleport to Ardougne Farm with Ardougne cloak.",
            ItemID.ARDY_CAPE_MEDIUM,
            "Farm Teleport",
            0,
            0,
            10548,
            ARDOUGNE_HERB_PATCH_POINT,
            () -> Collections.singletonList(
                new com.easyfarming.core.ItemRequirement(ItemID.ARDY_CAPE_MEDIUM, 1)
            )
        ));
        
        // Skills Necklace
        locationData.addTeleport(new TeleportData(
            "Skills_Necklace",
            Teleport.Category.ITEM,
            "Teleport to Fishing guild with Skills necklace, and run east.",
            ItemID.JEWL_NECKLACE_OF_SKILLS_1,
            "null",
            0,
            0,
            10292,
            ARDOUGNE_HERB_PATCH_POINT,
            () -> Collections.singletonList(
                new com.easyfarming.core.ItemRequirement(ItemID.JEWL_NECKLACE_OF_SKILLS_1, 1)
            )
        ));
        
        return locationData;
    }
}

