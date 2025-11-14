package com.easyfarming.locations;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.core.ItemRequirement;
import com.easyfarming.core.Teleport;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * LocationData definition for Troll Stronghold.
 */
public class TrollStrongholdLocationData {
    
    private static final WorldPoint TROLL_STRONGHOLD_HERB_PATCH_POINT = new WorldPoint(2824, 3696, 0);
    
    /**
     * Creates LocationData for Troll Stronghold.
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     *                              (typically from ItemAndLocation.getHouseTeleportItemRequirements())
     */
    public static LocationData create(Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        LocationData locationData = new LocationData(
            "Troll Stronghold",
            false, // farmLimps
            TROLL_STRONGHOLD_HERB_PATCH_POINT,
            EasyFarmingConfig::enumOptionEnumTrollStrongholdTeleport
        );
        
        // Stony Basalt
        locationData.addTeleport(new TeleportData(
            "Stony_Basalt",
            Teleport.Category.ITEM,
            "Teleport to Troll Stronghold with Stony Basalt.",
            ItemID.STRONGHOLD_TELEPORT_BASALT,
            "null",
            0,
            0,
            11321,
            TROLL_STRONGHOLD_HERB_PATCH_POINT,
            () -> Collections.singletonList(
                new com.easyfarming.core.ItemRequirement(ItemID.STRONGHOLD_TELEPORT_BASALT, 1)
            )
        ));
        
        // Portal Nexus
        locationData.addTeleport(new TeleportData(
            "Portal_Nexus",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Troll Stronghold with Portal Nexus.",
            0,
            "null",
            17,
            13,
            11321,
            TROLL_STRONGHOLD_HERB_PATCH_POINT,
            houseTeleportSupplier
        ));
        
        return locationData;
    }
}

