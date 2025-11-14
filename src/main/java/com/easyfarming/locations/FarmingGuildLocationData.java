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
 * LocationData definition for Farming Guild.
 */
public class FarmingGuildLocationData {
    
    private static final WorldPoint FARMING_GUILD_HERB_PATCH_POINT = new WorldPoint(1238, 3726, 0);
    
    /**
     * Creates LocationData for Farming Guild.
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     *                              (typically from ItemAndLocation.getHouseTeleportItemRequirements())
     */
    public static LocationData create(Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        LocationData locationData = new LocationData(
            "Farming Guild",
            true, // farmLimps
            FARMING_GUILD_HERB_PATCH_POINT,
            EasyFarmingConfig::enumOptionEnumFarmingGuildTeleport
        );
        
        // Jewellery box
        locationData.addTeleport(new TeleportData(
            "Jewellery_box",
            Teleport.Category.JEWELLERY_BOX,
            "Teleport to Farming guild with Jewellery box.",
            29155,
            "null",
            0,
            0,
            4922,
            FARMING_GUILD_HERB_PATCH_POINT,
            houseTeleportSupplier
        ));
        
        // Skills Necklace
        locationData.addTeleport(new TeleportData(
            "Skills_Necklace",
            Teleport.Category.ITEM,
            "Teleport to Farming guild using Skills necklace.",
            ItemID.JEWL_NECKLACE_OF_SKILLS_1,
            "null",
            0,
            0,
            4922,
            FARMING_GUILD_HERB_PATCH_POINT,
            () -> Collections.singletonList(
                new com.easyfarming.core.ItemRequirement(ItemID.JEWL_NECKLACE_OF_SKILLS_1, 1)
            )
        ));
        
        return locationData;
    }
}

