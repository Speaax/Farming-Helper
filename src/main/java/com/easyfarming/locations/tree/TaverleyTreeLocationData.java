package com.easyfarming.locations.tree;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.core.Location;
import com.easyfarming.core.Teleport;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Location definition for Taverley Tree patch.
 */
public class TaverleyTreeLocationData {

    private static final WorldPoint TAVERLEY_TREE_PATCH_POINT = new WorldPoint(2936, 3438, 0);

    /**
     * Creates Location for Taverley Tree patch.
     * @param config The EasyFarmingConfig instance
     * @param houseTeleportSupplier Supplier that provides house teleport item requirements
     * @return A Location instance for Taverley Tree patch
     */
    public static Location create(EasyFarmingConfig config, Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        Location location = new Location(
            EasyFarmingConfig::enumTreeTaverleyTeleport,
            config,
            "Taverley",
            false // farmLimps
        );

        // Portal Nexus
        location.addTeleportOption(new Teleport(
            "Portal_Nexus",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Falador with Portal Nexus and run to Taverley.",            0,
            "Teleport to Falador with Falador Tele Tab and run to Taverley.",            17,
            13,
            11828,
            TAVERLEY_TREE_PATCH_POINT,
            houseTeleportSupplier.get()
        ));

        // Teleport (spellbook)
        location.addTeleportOption(new Teleport(
            "Teleport",
            Teleport.Category.SPELLBOOK,
            "Teleport to Falador with spellbook and run to Taverley.",            0,
            "",
            218,
            29,
            11828,
            TAVERLEY_TREE_PATCH_POINT,
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
            "Teleport to Falador with Falador Tele Tab and run to Taverley.",
            ItemID.POH_TABLET_FALADORTELEPORT,
            "",
            0,
            0,
            11828,
            TAVERLEY_TREE_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.POH_TABLET_FALADORTELEPORT, 1)
            )
        ));

        // Normal POH Tab (POH set to Taverley)
        location.addTeleportOption(new Teleport(
            "Normal_POH_Tab",
            Teleport.Category.ITEM,
            "Teleport to Taverley with POH Teletab (POH set to Taverley).",
            ItemID.POH_TABLET_TAVERLEYTELEPORT,
            "",
            0,
            0,
            11828,
            TAVERLEY_TREE_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.POH_TABLET_TAVERLEYTELEPORT, 1)
            )
        ));

        // Games Necklace (to Burthorpe, then run to Taverley)
        location.addTeleportOption(new Teleport(
            "Games_Necklace_Burthorpe",
            Teleport.Category.ITEM,
            "Teleport to Burthorpe with Games Necklace and run to Taverley tree patch.",
            ItemID.GAMES_NECKLACE8,
            "",
            0,
            0,
            11828,
            TAVERLEY_TREE_PATCH_POINT,
            Collections.singletonList(
                new ItemRequirement(ItemID.GAMES_NECKLACE8, 1)
            )
        ));

        return location;
    }
}

