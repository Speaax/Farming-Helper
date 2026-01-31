package com.easyfarming.locations.tree;

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
 * Location definition for Farming Guild Tree patch.
 */
public class FarmingGuildTreeLocationData {
    
    private static final WorldPoint FARMING_GUILD_TREE_PATCH_POINT = new WorldPoint(1232, 3736, 0);
    
    /**
     * Creates Location for Farming Guild Tree patch.
     * @param config The EasyFarmingConfig instance
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     * @return A Location instance for Farming Guild Tree patch
     */
    public static Location create(EasyFarmingConfig config, Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        Location location = new Location(
            EasyFarmingConfig::enumTreeFarmingGuildTeleport,
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
            0,
            0,
            4922,
            FARMING_GUILD_TREE_PATCH_POINT,
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
            FARMING_GUILD_TREE_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.JEWL_NECKLACE_OF_SKILLS_1, 1)
            )
        ));
        
        // Spirit Tree
        location.addTeleportOption(new Teleport(
            "Spirit_Tree",
            Teleport.Category.SPIRIT_TREE,
            "Use a Spirit Tree and teleport to the Farming Guild.",
            0,
            "",
            187,
            3,
            4922,
            FARMING_GUILD_TREE_PATCH_POINT,
            Collections.emptyList()
        ));
        
        return location;
    }
}

