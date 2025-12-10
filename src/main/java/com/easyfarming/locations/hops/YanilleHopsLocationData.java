package com.easyfarming.locations.hops;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.Location;
import com.easyfarming.core.Teleport;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Location definition for Yanille Hops patch.
 */
public class YanilleHopsLocationData {
    
    private static final WorldPoint YANILLE_HOPS_PATCH_POINT = new WorldPoint(2576, 3105, 0);
    
    /**
     * Gets the patch point for Yanille Hops patch.
     * @return The WorldPoint for the Yanille Hops patch
     */
    public static WorldPoint getPatchPoint() {
        return YANILLE_HOPS_PATCH_POINT;
    }
    
    /**
     * Creates Location for Yanille Hops patch.
     * @param config The EasyFarmingConfig instance
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     * @return A Location instance for Yanille Hops patch
     */
    public static Location create(EasyFarmingConfig config, Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        Location location = new Location(
            EasyFarmingConfig::enumHopsYanilleTeleport,
            config,
            "Yanille",
            false // farmLimps
        );
        
        // Portal Nexus Yanille
        location.addTeleportOption(new Teleport(
            "Portal_Nexus",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Yanille with Portal Nexus, and run north to hops patch.",
            0,
            "",
            17,
            13,
            10288,
            YANILLE_HOPS_PATCH_POINT,
            houseTeleportSupplier.get()
        ));

        // Watchtower Teleport (spellbook) - requires hard Ardougne Diary
        location.addTeleportOption(new Teleport(
            "Watchtower_Teleport",
            Teleport.Category.SPELLBOOK,
            "Teleport to Yanille with Watchtower Teleport (requires hard Ardougne Diary), and run north to hops patch.",
            0,
            "",
            218,
            47,
            10288,
            YANILLE_HOPS_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.LAWRUNE, 2)
            )
        ));

        // Yanille Teleport (Watchtower teleport with diary goes to Yanille)
        location.addTeleportOption(new Teleport(
            "Yanille",
            Teleport.Category.SPELLBOOK,
            "Teleport to Yanille with Watchtower Teleport (requires hard Ardougne Diary), and run north to hops patch.",
            0,
            "",
            218,
            47,
            10288,
            YANILLE_HOPS_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.LAWRUNE, 2)
            )
        ));
        
        return location;
    }
}

