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
 * LocationData definition for Aldarin Hops patch.
 */
public class AldarinHopsLocationData {
    
    private static final WorldPoint ALDARIN_HOPS_PATCH_POINT = new WorldPoint(1365, 2939, 0);
    
    /**
     * Creates LocationData for Aldarin Hops patch.
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     */
    public static LocationData create(Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        LocationData locationData = new LocationData(
            "Aldarin",
            false, // farmLimps
            ALDARIN_HOPS_PATCH_POINT,
            EasyFarmingConfig::enumHopsAldarinTeleport
        );
        
        // Portal Nexus Aldarin
        locationData.addTeleport(new TeleportData(
            "Portal_Nexus",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Aldarin with Portal Nexus, and run to hops patch.",
            0,
            "",
            17,
            13,
            5421,
            ALDARIN_HOPS_PATCH_POINT,
            houseTeleportSupplier
        ));

        // Quetzal Transport System
        locationData.addTeleport(new TeleportData(
            "Quetzal_Transport",
            Teleport.Category.ITEM,
            "Teleport to Aldarin with Quetzal Transport System.",
            ItemID.HG_QUETZALWHISTLE_BASIC,
            "",
            0,
            0,
            5421,
            ALDARIN_HOPS_PATCH_POINT,
            () -> Collections.singletonList(
                new ItemRequirement(ItemID.HG_QUETZALWHISTLE_BASIC, 1)
            )
        ));
        
        return locationData;
    }
}

