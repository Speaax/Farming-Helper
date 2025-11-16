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
 * LocationData definition for Civitas illa Fortis.
 * This is a test case for migrating from code-based location setup to data-driven setup.
 * 
 * Usage:
 *   Supplier<List<ItemRequirement>> houseTeleSupplier = () -> itemAndLocation.getHouseTeleportItemRequirements();
 *   LocationData civitasData = CivitasLocationData.create(houseTeleSupplier);
 *   Location civitasLocation = LocationFactory.createLocation(civitasData, config);
 */
public class CivitasLocationData {
    
    private static final WorldPoint CIVITAS_HERB_PATCH_POINT = new WorldPoint(1586, 3099, 0);
    
    /**
     * Creates LocationData for Civitas illa Fortis.
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     *                              (typically from ItemAndLocation.getHouseTeleportItemRequirements())
     */
    public static LocationData create(Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        LocationData locationData = new LocationData(
            "Civitas illa Fortis",
            true, // farmLimps
            CIVITAS_HERB_PATCH_POINT,
            EasyFarmingConfig::enumOptionEnumCivitasTeleport
        );
        
        // Portal Nexus teleport
        locationData.addTeleport(new TeleportData(
            "Portal_Nexus",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Civitas illa Fortis with Portal Nexus.",
            0,
            "",
            17,
            13,
            6192,
            CIVITAS_HERB_PATCH_POINT,
            houseTeleportSupplier
        ));
        
        // Civitas Teleport (spellbook)
        locationData.addTeleport(new TeleportData(
            "Civitas_Teleport",
            Teleport.Category.SPELLBOOK,
            "Teleport to Civitas illa Fortis with standard spellbook, and run west.",
            0,
            "",
            218,
            43,
            6192,
            CIVITAS_HERB_PATCH_POINT,
            () -> Arrays.asList(
                new ItemRequirement(ItemID.LAWRUNE, 2),
                new ItemRequirement(ItemID.EARTHRUNE, 1),
                new ItemRequirement(ItemID.FIRERUNE, 1)
            )
        ));
        
        // Civitas Tele Tab
        locationData.addTeleport(new TeleportData(
            "Civitas_Tele_Tab",
            Teleport.Category.ITEM,
            "Teleport to Civitas illa Fortis with Civitas teleport tab, and run west.",
            ItemID.POH_TABLET_FORTISTELEPORT,
            "",
            0,
            0,
            6192,
            CIVITAS_HERB_PATCH_POINT,
            () -> Collections.singletonList(
                new ItemRequirement(ItemID.POH_TABLET_FORTISTELEPORT, 1)
            )
        ));
        
        // Quetzal whistle
        locationData.addTeleport(new TeleportData(
            "Quetzal_whistle",
            Teleport.Category.ITEM,
            "Teleport to the Hunter's Guild with the quetzal whistle, and run north.",
            ItemID.HG_QUETZALWHISTLE_BASIC,
            "",
            0,
            0,
            6192,
            CIVITAS_HERB_PATCH_POINT,
            () -> Collections.singletonList(
                new ItemRequirement(ItemID.HG_QUETZALWHISTLE_BASIC, 1)
            )
        ));
        
        // Hunter Skillcape
        locationData.addTeleport(new TeleportData(
            "Hunter_Skillcape",
            Teleport.Category.ITEM,
            "Teleport to Civitas illa Fortis with Hunter skillcape.",
            ItemID.SKILLCAPE_HUNTING,
            "",
            0,
            0,
            6192,
            CIVITAS_HERB_PATCH_POINT,
            () -> Collections.singletonList(
                new ItemRequirement(ItemID.SKILLCAPE_HUNTING, 1)
            )
        ));
        
        return locationData;
    }
}

