package com.easyfarming.locations.hops;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.Location;
import com.easyfarming.core.Teleport;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * Location definition for Aldarin Hops patch.
 */
public class AldarinHopsLocationData {
    
    private static final WorldPoint ALDARIN_HOPS_PATCH_POINT = new WorldPoint(1365, 2939, 0);
    
    /**
     * Gets the patch point for Aldarin Hops patch.
     * @return The WorldPoint for the Aldarin Hops patch
     */
    public static WorldPoint getPatchPoint() {
        return ALDARIN_HOPS_PATCH_POINT;
    }
    
    /**
     * Creates Location for Aldarin Hops patch.
     * @param config The EasyFarmingConfig instance
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     * @return A Location instance for Aldarin Hops patch
     */
    public static Location create(EasyFarmingConfig config, Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        Location location = new Location(
            EasyFarmingConfig::enumHopsAldarinTeleport,
            config,
            "Aldarin",
            false // farmLimps
        );
        
        // Portal Nexus Aldarin
        location.addTeleportOption(new Teleport(
            "Portal_Nexus",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Aldarin with Portal Nexus, and run to hops patch.",
            0,
            "",
            17,
            13,
            5421,
            ALDARIN_HOPS_PATCH_POINT,
            houseTeleportSupplier.get()
        ));

        // Quetzal Transport System (via Civitas)
        // First teleport to Civitas, then use Renu to fly to Aldarin
        location.addTeleportOption(new Teleport(
            "Quetzal_Transport",
            Teleport.Category.SPELLBOOK,
            "Teleport to Civitas with Civitas teleport spell, then fly Renu to Aldarin. Run north to hops patch.",
            0,
            "",
            218, // Spellbook interface group ID
            43,  // Civitas teleport interface child ID
            6704, // Civitas region ID (teleport can land in 6704 or 6705)
            new WorldPoint(1586, 3099, 0), // Civitas/Hunter's Guild point
            Arrays.asList(
                new ItemRequirement(ItemID.LAWRUNE, 2),
                new ItemRequirement(ItemID.EARTHRUNE, 1),
                new ItemRequirement(ItemID.FIRERUNE, 1)
            )
        ));
        
        return location;
    }
}

