package com.easyfarming.locations;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.core.Teleport;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * LocationData definition for Kourend.
 */
public class KourendLocationData {
    
    private static final WorldPoint KOUREND_HERB_PATCH_POINT = new WorldPoint(1738, 3550, 0);
    
    /**
     * Creates LocationData for Kourend.
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     *                              (typically from ItemAndLocation.getHouseTeleportItemRequirements())
     */
    public static LocationData create(Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        LocationData locationData = new LocationData(
            "Kourend",
            true, // farmLimps
            KOUREND_HERB_PATCH_POINT,
            EasyFarmingConfig::enumOptionEnumKourendTeleport
        );
        
        // Xeric's Talisman
        locationData.addTeleport(new TeleportData(
            "Xerics_Talisman",
            Teleport.Category.ITEM,
            "Teleport to Kourend with Xeric's Talisman.",
            ItemID.XERIC_TALISMAN,
            "Rub",
            187,
            3,
            6967,
            KOUREND_HERB_PATCH_POINT,
            () -> Collections.singletonList(
                new ItemRequirement(ItemID.XERIC_TALISMAN, 1)
            )
        ));
        
        // Mounted Xerics
        locationData.addTeleport(new TeleportData(
            "Mounted_Xerics",
            Teleport.Category.MOUNTED_XERICS,
            "Teleport to Kourend with Xeric's Talisman in PoH.",
            0,
            "null",
            187,
            3,
            6967,
            KOUREND_HERB_PATCH_POINT,
            houseTeleportSupplier
        ));
        
        return locationData;
    }
}

