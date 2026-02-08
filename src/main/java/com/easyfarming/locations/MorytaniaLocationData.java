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
 * Location definition for Morytania.
 */
public class MorytaniaLocationData {
    
    private static final WorldPoint MORYTANIA_HERB_PATCH_POINT = new WorldPoint(3601, 3525, 0);
    private static final WorldPoint FENKENSTRAINS_CASTLE = new WorldPoint(3557, 3565, 0);
    private static final WorldPoint CANIFIS = new WorldPoint(3496, 3509, 0);
    
    /**
     * Gets the patch point for Morytania herb patch.
     * @return The WorldPoint for the Morytania herb patch
     */
    public static WorldPoint getPatchPoint() {
        return MORYTANIA_HERB_PATCH_POINT;
    }
    
    /**
     * Creates Location for Morytania.
     * @param config The EasyFarmingConfig instance
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     *                              (from ItemAndLocation.getHouseTeleportItemRequirements())
     * @param fairyRingSupplier Supplier that provides fairy ring item requirements
     *                          (Dramen staff if Lumbridge Elite diary not complete)
     * @return A Location instance for Morytania
     */
    public static Location create(EasyFarmingConfig config, Supplier<List<ItemRequirement>> houseTeleportSupplier,
                                  Supplier<List<ItemRequirement>> fairyRingSupplier) {
        Location location = new Location(
            EasyFarmingConfig::enumOptionEnumMorytaniaTeleport,
            config,
            "Morytania",
            true // farmLimps
        );
        
        // Ectophial
        location.addTeleportOption(new Teleport(
            "Ectophial",
            Teleport.Category.ITEM,
            "Teleport to Morytania with Ectophial and run West to the patch.",
            ItemID.ECTOPHIAL,
            "",
            0,
            0,
            14647,
            MORYTANIA_HERB_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.ECTOPHIAL, 1)
            )
        ));
        
        // Fairy Ring ALQ
        location.addTeleportOption(new Teleport(
            "Fairy_Ring",
            Teleport.Category.FAIRY_RING,
            "Use a Fairy Ring (ALQ) to teleport to the Haunted Woods, and run to the patch.",
            0,
            "",
            0,
            0,
            14391,
            MORYTANIA_HERB_PATCH_POINT,
            fairyRingSupplier.get()
        ));
        
        // Portal Nexus (Fenkenstrain's Castle)
        location.addTeleportOption(new Teleport(
            "Portal_Nexus_Fenkenstrain",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Fenkenstrain's Castle with Portal Nexus, and run west to the Morytania herb patch.",
            0,
            "",
            17,
            13,
            13106,
            MORYTANIA_HERB_PATCH_POINT,
            houseTeleportSupplier.get()
        ));
        
        // Portal Nexus (Canifis)
        location.addTeleportOption(new Teleport(
            "Portal_Nexus_Canifis",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Canifis with Portal Nexus, and run east to the Morytania herb patch.",
            0,
            "",
            17,
            13,
            14647,
            MORYTANIA_HERB_PATCH_POINT,
            houseTeleportSupplier.get()
        ));
        
        return location;
    }
}
