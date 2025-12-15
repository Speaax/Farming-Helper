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
 * Location definition for Civitas illa Fortis.
 */
public class CivitasLocationData {
    
    private static final WorldPoint CIVITAS_HERB_PATCH_POINT = new WorldPoint(1586, 3099, 0);
    
    /**
     * Gets the patch point for Civitas illa Fortis herb patch.
     * @return The WorldPoint for the Civitas herb patch
     */
    public static WorldPoint getPatchPoint() {
        return CIVITAS_HERB_PATCH_POINT;
    }
    
    /**
     * Creates Location for Civitas illa Fortis.
     * @param config The EasyFarmingConfig instance
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     *                              (typically from ItemAndLocation.getHouseTeleportItemRequirements())
     * @return A Location instance for Civitas illa Fortis
     */
    public static Location create(EasyFarmingConfig config, Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        Location location = new Location(
            EasyFarmingConfig::enumOptionEnumCivitasTeleport,
            config,
            "Civitas illa Fortis",
            true // farmLimps
        );
        
        // Portal Nexus teleport
        location.addTeleportOption(new Teleport(
            "Portal_Nexus",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Civitas illa Fortis with Portal Nexus.",
            0,
            "",
            17,
            13,
            6192,
            CIVITAS_HERB_PATCH_POINT,
            houseTeleportSupplier.get()
        ));
        
        // Civitas Teleport (spellbook)
        location.addTeleportOption(new Teleport(
            "Civitas_Teleport",
            Teleport.Category.SPELLBOOK,
            "Teleport to Civitas illa Fortis with standard spellbook, and run west.",
            0,
            "",
            218,
            43,
            6192,
            CIVITAS_HERB_PATCH_POINT,
            Arrays.asList(
                new ItemRequirement(ItemID.LAWRUNE, 2),
                new ItemRequirement(ItemID.EARTHRUNE, 1),
                new ItemRequirement(ItemID.FIRERUNE, 1)
            )
        ));
        
        // Civitas Tele Tab
        location.addTeleportOption(new Teleport(
            "Civitas_Tele_Tab",
            Teleport.Category.ITEM,
            "Teleport to Civitas illa Fortis with Civitas teleport tab, and run west.",
            ItemID.POH_TABLET_FORTISTELEPORT,
            "",
            0,
            0,
            6192,
            CIVITAS_HERB_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.POH_TABLET_FORTISTELEPORT, 1)
            )
        ));
        
        // Quetzal whistle
        location.addTeleportOption(new Teleport(
            "Quetzal_whistle",
            Teleport.Category.ITEM,
            "Teleport to the Hunter's Guild with the quetzal whistle, and run north.",
            ItemID.HG_QUETZALWHISTLE_BASIC,
            "",
            0,
            0,
            6192,
            CIVITAS_HERB_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.HG_QUETZALWHISTLE_BASIC, 1)
            )
        ));
        
        // Hunter Skillcape
        location.addTeleportOption(new Teleport(
            "Hunter_Skillcape",
            Teleport.Category.ITEM,
            "Teleport to Civitas illa Fortis with Hunter skillcape.",
            ItemID.SKILLCAPE_HUNTING,
            "",
            0,
            0,
            6192,
            CIVITAS_HERB_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.SKILLCAPE_HUNTING, 1)
            )
        ));
        
        return location;
    }
}

