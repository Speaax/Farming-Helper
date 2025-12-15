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
 * Location definition for Catherby.
 */
public class CatherbyLocationData {
    
    private static final WorldPoint CATHERBY_HERB_PATCH_POINT = new WorldPoint(2813, 3463, 0);
    
    /**
     * Gets the patch point for Catherby herb patch.
     * @return The WorldPoint for the Catherby herb patch
     */
    public static WorldPoint getPatchPoint() {
        return CATHERBY_HERB_PATCH_POINT;
    }
    
    /**
     * Creates Location for Catherby.
     * @param config The EasyFarmingConfig instance
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     *                              (typically from ItemAndLocation.getHouseTeleportItemRequirements())
     * @return A Location instance for Catherby
     */
    public static Location create(EasyFarmingConfig config, Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        Location location = new Location(
            EasyFarmingConfig::enumOptionEnumCatherbyTeleport,
            config,
            "Catherby",
            true // farmLimps
        );
        
        // Portal Nexus Catherby
        location.addTeleportOption(new Teleport(
            "Portal_Nexus_Catherby",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Catherby with Portal Nexus.",
            0,
            "",
            17,
            13,
            11061,
            CATHERBY_HERB_PATCH_POINT,
            houseTeleportSupplier.get()
        ));
        
        // Portal Nexus Camelot
        location.addTeleportOption(new Teleport(
            "Portal_Nexus_Camelot",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Camelot with Portal Nexus.",
            0,
            "",
            17,
            13,
            11062,
            CATHERBY_HERB_PATCH_POINT,
            houseTeleportSupplier.get()
        ));
        
        // Camelot Teleport (spellbook)
        location.addTeleportOption(new Teleport(
            "Camelot_Teleport",
            Teleport.Category.SPELLBOOK,
            "Teleport to Camelot using the standard spellbook, and run east to Catherby herb patch.",
            0,
            "",
            218,
            34,
            11062,
            CATHERBY_HERB_PATCH_POINT,
            Arrays.asList(
                new ItemRequirement(ItemID.AIRRUNE, 5),
                new ItemRequirement(ItemID.LAWRUNE, 1)
            )
        ));
        
        // Camelot Tele Tab
        location.addTeleportOption(new Teleport(
            "Camelot_Tele_Tab",
            Teleport.Category.ITEM,
            "Teleport to Camelot using a Camelot tele tab, and run east to Catherby herb patch.",
            ItemID.POH_TABLET_CAMELOTTELEPORT,
            "",
            0,
            0,
            11062,
            CATHERBY_HERB_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.POH_TABLET_CAMELOTTELEPORT, 1)
            )
        ));
        
        // Catherby Tele Tab
        location.addTeleportOption(new Teleport(
            "Catherby_Tele_Tab",
            Teleport.Category.ITEM,
            "Teleport to Catherby using Catherby teleport tab.",
            ItemID.LUNAR_TABLET_CATHERBY_TELEPORT,
            "",
            0,
            0,
            11061,
            CATHERBY_HERB_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.LUNAR_TABLET_CATHERBY_TELEPORT, 1)
            )
        ));
        
        return location;
    }
}

