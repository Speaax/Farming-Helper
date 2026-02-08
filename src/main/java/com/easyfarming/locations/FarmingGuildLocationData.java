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
 * Location definition for Farming Guild.
 */
public class FarmingGuildLocationData {
    
    private static final WorldPoint FARMING_GUILD_HERB_PATCH_POINT = new WorldPoint(1238, 3726, 0);
    
    /**
     * Gets the patch point for Farming Guild herb patch.
     * @return The WorldPoint for the Farming Guild herb patch
     */
    public static WorldPoint getPatchPoint() {
        return FARMING_GUILD_HERB_PATCH_POINT;
    }
    
    /**
     * Creates Location for Farming Guild.
     * @param config The EasyFarmingConfig instance
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     *                              (typically from ItemAndLocation.getHouseTeleportItemRequirements())
     * @param fairyRingSupplier Supplier that provides fairy ring item requirements
     *                          (Dramen staff if Lumbridge Elite diary not complete)
     * @return A Location instance for Farming Guild
     */
    public static Location create(EasyFarmingConfig config, Supplier<List<ItemRequirement>> houseTeleportSupplier,
                                  Supplier<List<ItemRequirement>> fairyRingSupplier) {
        Location location = new Location(
            EasyFarmingConfig::enumOptionEnumFarmingGuildTeleport,
            config,
            "Farming Guild",
            true // farmLimps
        );
        
        // Jewellery box
        location.addTeleportOption(new Teleport(
            "Jewellery_box",
            Teleport.Category.JEWELLERY_BOX,
            "Teleport to Farming guild with Jewellery box.",
            29155,
            "",
            0,
            0,
            4922,
            FARMING_GUILD_HERB_PATCH_POINT,
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
            FARMING_GUILD_HERB_PATCH_POINT,
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
            FARMING_GUILD_HERB_PATCH_POINT,
            Collections.emptyList()
        ));
        
        // Fairy Ring CIR
        location.addTeleportOption(new Teleport(
            "Fairy_Ring",
            Teleport.Category.FAIRY_RING,
            "Use a Fairy Ring (CIR) to teleport near the Farming Guild.",
            0,
            "",
            0,
            0,
            4922,
            FARMING_GUILD_HERB_PATCH_POINT,
            fairyRingSupplier.get()
        ));
        
        // Farming Skillcape
        location.addTeleportOption(new Teleport(
            "Farming_Skillcape",
            Teleport.Category.ITEM,
            "Teleport to the Farming Guild with Farming skillcape.",
            ItemID.SKILLCAPE_FARMING,
            "",
            0,
            0,
            4922,
            FARMING_GUILD_HERB_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.SKILLCAPE_FARMING, 1)
            )
        ));
        
        return location;
    }
}

