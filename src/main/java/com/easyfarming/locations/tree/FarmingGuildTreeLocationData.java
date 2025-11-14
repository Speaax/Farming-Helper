package com.easyfarming.locations.tree;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.core.ItemRequirement;
import com.easyfarming.core.Teleport;
import com.easyfarming.locations.LocationData;
import com.easyfarming.locations.TeleportData;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * LocationData definition for Farming Guild Tree patch.
 */
public class FarmingGuildTreeLocationData {
    
    private static final WorldPoint FARMING_GUILD_TREE_PATCH_POINT = new WorldPoint(1232, 3736, 0);
    
    /**
     * Creates LocationData for Farming Guild Tree patch.
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     */
    public static LocationData create(Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        LocationData locationData = new LocationData(
            "Farming Guild",
            false, // farmLimps
            FARMING_GUILD_TREE_PATCH_POINT,
            EasyFarmingConfig::enumTreeFarmingGuildTeleport
        );
        
        // Jewellery box
        locationData.addTeleport(new TeleportData(
            "Jewellery_box",
            Teleport.Category.JEWELLERY_BOX,
            "Teleport to Farming Guild with Jewellery box.",
            0,
            "null",
            0,
            0,
            4922,
            FARMING_GUILD_TREE_PATCH_POINT,
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
            FARMING_GUILD_TREE_PATCH_POINT,
            () -> Collections.singletonList(
                new com.easyfarming.core.ItemRequirement(ItemID.JEWL_NECKLACE_OF_SKILLS_1, 1)
            )
        ));
        
        return locationData;
    }
}

