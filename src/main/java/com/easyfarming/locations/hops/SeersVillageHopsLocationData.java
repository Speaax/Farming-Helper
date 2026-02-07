package com.easyfarming.locations.hops;

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
 * Location definition for Seers Village Hops patch (McGrubor's Wood).
 */
public class SeersVillageHopsLocationData {
    
    private static final WorldPoint SEERS_VILLAGE_HOPS_PATCH_POINT = new WorldPoint(2667, 3526, 0);
    
    /**
     * Gets the patch point for Seers Village Hops patch.
     * @return The WorldPoint for the Seers Village Hops patch
     */
    public static WorldPoint getPatchPoint() {
        return SEERS_VILLAGE_HOPS_PATCH_POINT;
    }
    
    /**
     * Creates Location for Seers Village Hops patch.
     * @param config The EasyFarmingConfig instance
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     * @param fairyRingSupplier Supplier that provides fairy ring item requirements
     * @return A Location instance for Seers Village Hops patch
     */
    public static Location create(EasyFarmingConfig config, Supplier<List<ItemRequirement>> houseTeleportSupplier,
                                  Supplier<List<ItemRequirement>> fairyRingSupplier) {
        Location location = new Location(
            EasyFarmingConfig::enumHopsSeersVillageTeleport,
            config,
            "Seers Village",
            false // farmLimps
        );
        
        // Portal Nexus Camelot
        location.addTeleportOption(new Teleport(
            "Portal_Nexus_Camelot",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Camelot with Portal Nexus, and run northwest to hops patch.",
            0,
            "",
            17,
            13,
            10551,
            SEERS_VILLAGE_HOPS_PATCH_POINT,
            houseTeleportSupplier.get()
        ));

        // Camelot Teleport (spellbook)
        location.addTeleportOption(new Teleport(
            "Camelot_Teleport",
            Teleport.Category.SPELLBOOK,
            "Teleport to Camelot with standard spellbook, and run northwest to hops patch.",
            0,
            "",
            218,
            34,
            10551,
            SEERS_VILLAGE_HOPS_PATCH_POINT,
            Arrays.asList(
                new ItemRequirement(ItemID.AIRRUNE, 5),
                new ItemRequirement(ItemID.LAWRUNE, 1)
            )
        ));

        // Camelot Tele Tab
        location.addTeleportOption(new Teleport(
            "Camelot_Tele_Tab",
            Teleport.Category.ITEM,
            "Teleport to Camelot with Camelot tele tab, and run northwest to hops patch.",
            ItemID.POH_TABLET_CAMELOTTELEPORT,
            "",
            0,
            0,
            10551,
            SEERS_VILLAGE_HOPS_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.POH_TABLET_CAMELOTTELEPORT, 1)
            )
        ));

        // Seers Village (Camelot teleport with diary goes to Seers)
        location.addTeleportOption(new Teleport(
            "Seers_Village",
            Teleport.Category.SPELLBOOK,
            "Teleport to Seers Village with Camelot Teleport (requires hard Kandarin Diary), and run northwest to hops patch.",
            0,
            "",
            218,
            34,
            10551,
            SEERS_VILLAGE_HOPS_PATCH_POINT,
            Arrays.asList(
                new ItemRequirement(ItemID.AIRRUNE, 5),
                new ItemRequirement(ItemID.LAWRUNE, 1)
            )
        ));
        
        // Fairy Ring ALS (closest fairy ring to the hops patch near McGrubor's Wood)
        location.addTeleportOption(new Teleport(
            "Fairy_Ring",
            Teleport.Category.FAIRY_RING,
            "Use a Fairy Ring (ALS) to teleport near McGrubor's Wood, and run to the hops patch.",
            0,
            "",
            0,
            0,
            10551,
            SEERS_VILLAGE_HOPS_PATCH_POINT,
            fairyRingSupplier.get()
        ));
        
        return location;
    }
}

