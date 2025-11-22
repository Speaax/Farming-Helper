package com.easyfarming.locations.hops;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.core.Teleport;
import com.easyfarming.locations.LocationData;
import com.easyfarming.locations.TeleportData;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * LocationData definition for Yanille Hops patch.
 */
public class YanilleHopsLocationData {
    
    private static final WorldPoint YANILLE_HOPS_PATCH_POINT = new WorldPoint(2576, 3105, 0);
    
    /**
     * Creates LocationData for Yanille Hops patch.
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     */
    public static LocationData create(Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        LocationData locationData = new LocationData(
            "Yanille",
            false, // farmLimps
            YANILLE_HOPS_PATCH_POINT,
            EasyFarmingConfig::enumHopsYanilleTeleport
        );
        
        // Portal Nexus Yanille
        locationData.addTeleport(new TeleportData(
            "Portal_Nexus",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Yanille with Portal Nexus, and run north to hops patch.",
            0,
            "",
            17,
            13,
            10288,
            YANILLE_HOPS_PATCH_POINT,
            houseTeleportSupplier
        ));

        // Watchtower Teleport (spellbook) - requires hard Ardougne Diary
        locationData.addTeleport(new TeleportData(
            "Watchtower_Teleport",
            Teleport.Category.SPELLBOOK,
            "Teleport to Yanille with Watchtower Teleport (requires hard Ardougne Diary), and run north to hops patch.",
            0,
            "",
            218,
            47,
            10288,
            YANILLE_HOPS_PATCH_POINT,
            () -> Collections.singletonList(
                new ItemRequirement(ItemID.LAWRUNE, 2)
            )
        ));

        // Yanille Teleport (Watchtower teleport with diary goes to Yanille)
        locationData.addTeleport(new TeleportData(
            "Yanille",
            Teleport.Category.SPELLBOOK,
            "Teleport to Yanille with Watchtower Teleport (requires hard Ardougne Diary), and run north to hops patch.",
            0,
            "",
            218,
            47,
            10288,
            YANILLE_HOPS_PATCH_POINT,
            () -> Collections.singletonList(
                new ItemRequirement(ItemID.LAWRUNE, 2)
            )
        ));
        
        return locationData;
    }
}

