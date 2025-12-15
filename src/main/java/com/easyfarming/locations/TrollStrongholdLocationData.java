package com.easyfarming.locations;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.core.Location;
import com.easyfarming.core.Teleport;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Location definition for Troll Stronghold.
 */
public class TrollStrongholdLocationData {
    
    private static final WorldPoint TROLL_STRONGHOLD_HERB_PATCH_POINT = new WorldPoint(2824, 3696, 0);
    
    /**
     * Gets the patch point for Troll Stronghold herb patch.
     * @return The WorldPoint for the Troll Stronghold herb patch
     */
    public static WorldPoint getPatchPoint() {
        return TROLL_STRONGHOLD_HERB_PATCH_POINT;
    }
    
    /**
     * Creates Location for Troll Stronghold.
     * @param config The EasyFarmingConfig instance
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     *                              (typically from ItemAndLocation.getHouseTeleportItemRequirements())
     * @return A Location instance for Troll Stronghold
     */
    public static Location create(EasyFarmingConfig config, Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        Location location = new Location(
            EasyFarmingConfig::enumOptionEnumTrollStrongholdTeleport,
            config,
            "Troll Stronghold",
            false // farmLimps
        );
        
        // Stony Basalt
        location.addTeleportOption(new Teleport(
            "Stony_Basalt",
            Teleport.Category.ITEM,
            "Teleport to Troll Stronghold with Stony Basalt.",
            ItemID.STRONGHOLD_TELEPORT_BASALT,
            "",
            0,
            0,
            11321,
            TROLL_STRONGHOLD_HERB_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.STRONGHOLD_TELEPORT_BASALT, 1)
            )
        ));
        
        // Portal Nexus
        location.addTeleportOption(new Teleport(
            "Portal_Nexus",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Troll Stronghold with Portal Nexus.",
            0,
            "",
            17,
            13,
            11321,
            TROLL_STRONGHOLD_HERB_PATCH_POINT,
            houseTeleportSupplier.get()
        ));
        
        return location;
    }
}

