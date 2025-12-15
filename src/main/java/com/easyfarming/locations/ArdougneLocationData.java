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
 * Location definition for Ardougne.
 */
public class ArdougneLocationData {
    
    private static final WorldPoint ARDOUGNE_HERB_PATCH_POINT = new WorldPoint(2670, 3374, 0);
    
    /**
     * Gets the patch point for Ardougne herb patch.
     * @return The WorldPoint for the Ardougne herb patch
     */
    public static WorldPoint getPatchPoint() {
        return ARDOUGNE_HERB_PATCH_POINT;
    }
    
    /**
     * Creates Location for Ardougne.
     * @param config The EasyFarmingConfig instance
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     *                              (typically from ItemAndLocation.getHouseTeleportItemRequirements())
     * @return A Location instance for Ardougne
     */
    public static Location create(EasyFarmingConfig config, Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        Location location = new Location(
            EasyFarmingConfig::enumOptionEnumArdougneTeleport,
            config,
            "Ardougne",
            true // farmLimps
        );
        
        // Portal Nexus teleport
        location.addTeleportOption(new Teleport(
            "Portal_Nexus",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Ardougne with Portal Nexus, and run north.",
            0,
            "",
            17,
            13,
            10547,
            ARDOUGNE_HERB_PATCH_POINT,
            houseTeleportSupplier.get()
        ));
        
        // Ardougne teleport (spellbook)
        location.addTeleportOption(new Teleport(
            "Ardougne_teleport",
            Teleport.Category.SPELLBOOK,
            "Teleport to Ardougne with standard spellbook, and run north.",
            0,
            "",
            218,
            41,
            10547,
            ARDOUGNE_HERB_PATCH_POINT,
            Arrays.asList(
                new ItemRequirement(ItemID.LAWRUNE, 2),
                new ItemRequirement(ItemID.WATERRUNE, 2)
            )
        ));
        
        // Ardougne Tele Tab
        location.addTeleportOption(new Teleport(
            "Ardougne_Tele_Tab",
            Teleport.Category.ITEM,
            "Teleport to Ardougne with Ardougne tele tab, and run north.",
            ItemID.POH_TABLET_ARDOUGNETELEPORT,
            "",
            0,
            0,
            10547,
            ARDOUGNE_HERB_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.POH_TABLET_ARDOUGNETELEPORT, 1)
            )
        ));
        
        // Ardy cloak
        location.addTeleportOption(new Teleport(
            "Ardy_cloak",
            Teleport.Category.ITEM,
            "Teleport to Ardougne Farm with Ardougne cloak.",
            ItemID.ARDY_CAPE_MEDIUM,
            "Farm Teleport",
            0,
            0,
            10548,
            ARDOUGNE_HERB_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.ARDY_CAPE_MEDIUM, 1)
            )
        ));
        
        // Skills Necklace
        location.addTeleportOption(new Teleport(
            "Skills_Necklace",
            Teleport.Category.ITEM,
            "Teleport to Fishing guild with Skills necklace, and run east.",
            ItemID.JEWL_NECKLACE_OF_SKILLS_1,
            "",
            0,
            0,
            10292,
            ARDOUGNE_HERB_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.JEWL_NECKLACE_OF_SKILLS_1, 1)
            )
        ));
        
        return location;
    }
}

