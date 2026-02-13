package com.easyfarming.locations.hops;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.core.Location;
import com.easyfarming.core.Teleport;
import com.easyfarming.utils.Constants;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Location definition for Lumbridge Hops patch.
 */
public class LumbridgeHopsLocationData {
    
    private static final WorldPoint LUMBRIDGE_HOPS_PATCH_POINT = new WorldPoint(3229, 3315, 0);
    
    /**
     * Gets the patch point for Lumbridge Hops patch.
     * @return The WorldPoint for the Lumbridge Hops patch
     */
    public static WorldPoint getPatchPoint() {
        return LUMBRIDGE_HOPS_PATCH_POINT;
    }
    
    /**
     * Creates Location for Lumbridge Hops patch.
     * @param config The EasyFarmingConfig instance
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     * @return A Location instance for Lumbridge Hops patch
     */
    public static Location create(EasyFarmingConfig config, Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        Location location = new Location(
            EasyFarmingConfig::enumHopsLumbridgeTeleport,
            config,
            "Lumbridge",
            false // farmLimps
        );
        
        // Portal Nexus
        location.addTeleportOption(new Teleport(
            "Portal_Nexus",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Lumbridge with Portal Nexus, and run north to hops patch.",
            0,
            "",
            17,
            13,
            12851,
            LUMBRIDGE_HOPS_PATCH_POINT,
            houseTeleportSupplier.get()
        ));

        // Lumbridge Teleport (spellbook)
        location.addTeleportOption(new Teleport(
            "Teleport",
            Teleport.Category.SPELLBOOK,
            "Teleport to Lumbridge with spellbook, and run north to hops patch.",
            0,
            "",
            218,
            26,
            12851,
            LUMBRIDGE_HOPS_PATCH_POINT,
            Arrays.asList(
                new ItemRequirement(ItemID.AIRRUNE, 3),
                new ItemRequirement(ItemID.LAWRUNE, 1),
                new ItemRequirement(ItemID.EARTHRUNE, 1)
            )
        ));

        // Lumbridge Tele Tab
        location.addTeleportOption(new Teleport(
            "Lumbridge_Tele_Tab",
            Teleport.Category.ITEM,
            "Teleport to Lumbridge with Lumbridge Tele Tab, and run north to hops patch.",
            ItemID.POH_TABLET_LUMBRIDGETELEPORT,
            "",
            0,
            0,
            12851,
            LUMBRIDGE_HOPS_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.POH_TABLET_LUMBRIDGETELEPORT, 1)
            )
        ));

        // Chronicle
        location.addTeleportOption(new Teleport(
            "Chronicle",
            Teleport.Category.ITEM,
            "Teleport to Champions' Guild with Chronicle, and run south to hops patch.",
            ItemID.CHRONICLE,
            "",
            0,
            0,
            12851,
            LUMBRIDGE_HOPS_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.CHRONICLE, 1)
            )
        ));

        // Varrock Teleport (spellbook)
        location.addTeleportOption(new Teleport(
            "Varrock_Teleport",
            Teleport.Category.SPELLBOOK,
            "Teleport to Varrock with spellbook, and run south to hops patch.",
            0,
            "",
            218,
            23,
            12853,
            LUMBRIDGE_HOPS_PATCH_POINT,
            Arrays.asList(
                new ItemRequirement(ItemID.AIRRUNE, 3),
                new ItemRequirement(ItemID.LAWRUNE, 1),
                new ItemRequirement(ItemID.FIRERUNE, 1)
            )
        ));

        // Varrock Tele Tab
        location.addTeleportOption(new Teleport(
            "Varrock_Tele_Tab",
            Teleport.Category.ITEM,
            "Teleport to Varrock with Varrock Tele Tab, and run south to hops patch.",
            ItemID.POH_TABLET_VARROCKTELEPORT,
            "",
            0,
            0,
            12853,
            LUMBRIDGE_HOPS_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.POH_TABLET_VARROCKTELEPORT, 1)
            )
        ));

        // Combat Bracelet (Champions' Guild) - requires 2+ charges
        location.addTeleportOption(new Teleport(
            "Combat_Bracelet",
            Teleport.Category.ITEM,
            "Teleport to Champions' Guild with Combat Bracelet, and run south to hops patch.",
            Constants.BASE_COMBAT_BRACELET_ID,
            "",
            0,
            0,
            10044,
            LUMBRIDGE_HOPS_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(Constants.BASE_COMBAT_BRACELET_ID, 1)
            )
        ));
        
        return location;
    }
}

