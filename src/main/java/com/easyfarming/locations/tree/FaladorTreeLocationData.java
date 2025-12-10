package com.easyfarming.locations.tree;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.Location;
import com.easyfarming.core.Teleport;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Location definition for Falador Tree patch.
 */
public class FaladorTreeLocationData {
    
    private static final WorldPoint FALADOR_TREE_PATCH_POINT = new WorldPoint(3000, 3373, 0);
    
    /**
     * Creates Location for Falador Tree patch.
     * @param config The EasyFarmingConfig instance
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     * @return A Location instance for Falador Tree patch
     */
    public static Location create(EasyFarmingConfig config, Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        Location location = new Location(
            EasyFarmingConfig::enumTreeFaladorTeleport,
            config,
            "Falador",
            false // farmLimps
        );
        
        // Portal Nexus
        location.addTeleportOption(new Teleport(
            "Portal_Nexus",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Falador with Portal Nexus.",
            0,
            "",
            17,
            13,
            11828,
            FALADOR_TREE_PATCH_POINT,
            houseTeleportSupplier.get()
        ));
        
        // Teleport (spellbook)
        location.addTeleportOption(new Teleport(
            "Teleport",
            Teleport.Category.SPELLBOOK,
            "Teleport to Falador with Spellbook and run to Falador park.",
            0,
            "",
            218,
            29,
            11828,
            FALADOR_TREE_PATCH_POINT,
            Arrays.asList(
                new ItemRequirement(ItemID.AIRRUNE, 3),
                new ItemRequirement(ItemID.LAWRUNE, 1),
                new ItemRequirement(ItemID.WATERRUNE, 1)
            )
        ));
        
        // Falador Tele Tab
        location.addTeleportOption(new Teleport(
            "Falador_Tele_Tab",
            Teleport.Category.ITEM,
            "Teleport to Falador with Falador Tele Tab and run to Falador park.",
            ItemID.POH_TABLET_FALADORTELEPORT,
            "",
            0,
            0,
            11828,
            FALADOR_TREE_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.POH_TABLET_FALADORTELEPORT, 1)
            )
        ));
        
        return location;
    }
}

