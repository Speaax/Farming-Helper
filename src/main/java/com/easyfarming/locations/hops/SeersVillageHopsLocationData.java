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
 * LocationData definition for Seers Village Hops patch (McGrubor's Wood).
 */
public class SeersVillageHopsLocationData {
    
    private static final WorldPoint SEERS_VILLAGE_HOPS_PATCH_POINT = new WorldPoint(2667, 3526, 0);
    
    /**
     * Creates LocationData for Seers Village Hops patch.
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     */
    public static LocationData create(Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        LocationData locationData = new LocationData(
            "Seers Village",
            false, // farmLimps
            SEERS_VILLAGE_HOPS_PATCH_POINT,
            EasyFarmingConfig::enumHopsSeersVillageTeleport
        );
        
        // Portal Nexus Camelot
        locationData.addTeleport(new TeleportData(
            "Portal_Nexus_Camelot",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Camelot with Portal Nexus, and run northwest to hops patch.",
            0,
            "",
            17,
            13,
            10551,
            SEERS_VILLAGE_HOPS_PATCH_POINT,
            houseTeleportSupplier
        ));

        // Camelot Teleport (spellbook)
        locationData.addTeleport(new TeleportData(
            "Camelot_Teleport",
            Teleport.Category.SPELLBOOK,
            "Teleport to Camelot with standard spellbook, and run northwest to hops patch.",
            0,
            "",
            218,
            34,
            10551,
            SEERS_VILLAGE_HOPS_PATCH_POINT,
            () -> Arrays.asList(
                new ItemRequirement(ItemID.AIRRUNE, 5),
                new ItemRequirement(ItemID.LAWRUNE, 1)
            )
        ));

        // Camelot Tele Tab
        locationData.addTeleport(new TeleportData(
            "Camelot_Tele_Tab",
            Teleport.Category.ITEM,
            "Teleport to Camelot with Camelot tele tab, and run northwest to hops patch.",
            ItemID.POH_TABLET_CAMELOTTELEPORT,
            "",
            0,
            0,
            10551,
            SEERS_VILLAGE_HOPS_PATCH_POINT,
            () -> Collections.singletonList(
                new ItemRequirement(ItemID.POH_TABLET_CAMELOTTELEPORT, 1)
            )
        ));

        // Seers Village (Camelot teleport with diary goes to Seers)
        locationData.addTeleport(new TeleportData(
            "Seers_Village",
            Teleport.Category.SPELLBOOK,
            "Teleport to Seers Village with Camelot Teleport (requires hard Kandarin Diary), and run northwest to hops patch.",
            0,
            "",
            218,
            34,
            10551,
            SEERS_VILLAGE_HOPS_PATCH_POINT,
            () -> Arrays.asList(
                new ItemRequirement(ItemID.AIRRUNE, 5),
                new ItemRequirement(ItemID.LAWRUNE, 1)
            )
        ));
        
        
        return locationData;
    }
}

