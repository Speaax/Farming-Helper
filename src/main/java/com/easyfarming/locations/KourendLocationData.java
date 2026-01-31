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
 * Location definition for Kourend.
 */
public class KourendLocationData {
    
    private static final WorldPoint KOUREND_HERB_PATCH_POINT = new WorldPoint(1738, 3550, 0);
    
    /**
     * Gets the patch point for Kourend herb patch.
     * @return The WorldPoint for the Kourend herb patch
     */
    public static WorldPoint getPatchPoint() {
        return KOUREND_HERB_PATCH_POINT;
    }
    
    /**
     * Creates Location for Kourend.
     * @param config The EasyFarmingConfig instance
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     *                              (typically from ItemAndLocation.getHouseTeleportItemRequirements())
     * @return A Location instance for Kourend
     */
    public static Location create(EasyFarmingConfig config, Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        Location location = new Location(
            EasyFarmingConfig::enumOptionEnumKourendTeleport,
            config,
            "Kourend",
            true // farmLimps
        );
        
        // Xeric's Talisman
        location.addTeleportOption(new Teleport(
            "Xerics_Talisman",
            Teleport.Category.ITEM,
            "Teleport to Kourend with Xeric's Talisman.",
            ItemID.XERIC_TALISMAN,
            "Rub",
            187,
            3,
            6967,
            KOUREND_HERB_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.XERIC_TALISMAN, 1)
            )
        ));
        
        // Mounted Xerics
        location.addTeleportOption(new Teleport(
            "Mounted_Xerics",
            Teleport.Category.MOUNTED_XERICS,
            "Teleport to Kourend with Xeric's Talisman in PoH.",
            0,
            "",
            187,
            3,
            6967,
            KOUREND_HERB_PATCH_POINT,
            houseTeleportSupplier.get()
        ));
        
        // Hosidius POH Tab
        location.addTeleportOption(new Teleport(
            "Hosidius_POH_Tab",
            Teleport.Category.ITEM,
            "Teleport to Hosidius with Hosidius POH Tab.",
            ItemID.NZONE_TELETAB_KOUREND,
            "Break",
            0,
            0,
            6967,
            KOUREND_HERB_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.NZONE_TELETAB_KOUREND, 1)
            )
        ));
        
        return location;
    }
}

