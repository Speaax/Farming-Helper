package com.easyfarming.locations.fruittree;

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
 * Location definition for Farming Guild Fruit Tree patch.
 */
public class FarmingGuildFruitTreeLocationData {
    
    private static final WorldPoint FARMING_GUILD_FRUIT_TREE_PATCH_POINT = new WorldPoint(1243, 3759, 0);
    
    /**
     * Creates Location for Farming Guild Fruit Tree patch.
     * @param config The EasyFarmingConfig instance
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     * @return A Location instance for Farming Guild Fruit Tree patch
     */
    public static Location create(EasyFarmingConfig config, Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        Location location = new Location(
            EasyFarmingConfig::enumFruitTreeFarmingGuildTeleport,
            config,
            "Farming Guild",
            false // farmLimps
        );
        
        // Jewellery box
        location.addTeleportOption(new Teleport(
            "Jewellery_box",
            Teleport.Category.JEWELLERY_BOX,
            "Teleport to Farming Guild with Jewellery box.",
            0,
            "",
            17,
            13,
            4922,
            FARMING_GUILD_FRUIT_TREE_PATCH_POINT,
            houseTeleportSupplier.get()
        ));
        
        // Skills Necklace
        location.addTeleportOption(new Teleport(
            "Skills_Necklace",
            Teleport.Category.ITEM,
            "Teleport to Farming guild using Skills necklace.",
            ItemID.JEWL_NECKLACE_OF_SKILLS_1,
            "",
            0,
            0,
            4922,
            FARMING_GUILD_FRUIT_TREE_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.JEWL_NECKLACE_OF_SKILLS_1, 1)
            )
        ));
        
        return location;
    }
}

