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
 * Location definition for the tree patch at Nemus Retreat (Auburn Valley, Varlamore).
 * RuneLite timetracking lists this under the Auburnvale region with {@link net.runelite.api.gameval.VarbitID#FARMING_TRANSMIT_A}.
 */
public class NemusRetreatTreeLocationData {

    private static final WorldPoint NEMUS_RETREAT_TREE_PATCH_POINT = new WorldPoint(1366, 3321, 0);

    public static Location create(EasyFarmingConfig config, Supplier<List<ItemRequirement>> fairyRingSupplier) {
        Location location = new Location(
                EasyFarmingConfig::enumTreeNemusRetreatTeleport,
                config,
                "Nemus Retreat",
                false
        );

        location.addTeleportOption(new Teleport(
                "Quetzal_whistle",
                Teleport.Category.ITEM,
                "Use the quetzal whistle to fly to Auburnvale, then run south-west to the Nemus tree patch.",
                ItemID.HG_QUETZALWHISTLE_BASIC,
                "",
                0,
                0,
                6192,
                new WorldPoint(1586, 3099, 0),
                Collections.singletonList(
                        new ItemRequirement(ItemID.HG_QUETZALWHISTLE_BASIC, 1)
                )
        ));

        location.addTeleportOption(new Teleport(
                "Quetzal_Transport",
                Teleport.Category.SPELLBOOK,
                "Teleport to Civitas with Civitas teleport spell, then use the Quetzal Transport System to Auburnvale, then run south-west to Nemus Retreat and the tree patch.",
                0,
                "",
                218,
                44,
                6704,
                new WorldPoint(1586, 3099, 0),
                Arrays.asList(
                        new ItemRequirement(ItemID.LAWRUNE, 2),
                        new ItemRequirement(ItemID.EARTHRUNE, 1),
                        new ItemRequirement(ItemID.FIRERUNE, 1)
                )
        ));

        location.addTeleportOption(new Teleport(
                "Pendant_of_Ates",
                Teleport.Category.ITEM,
                "Teleport to Nemus Retreat with Pendant of Ates, then run north to the tree patch.",
                ItemID.PENDANT_OF_ATES,
                "",
                0,
                0,
                5427,
                NEMUS_RETREAT_TREE_PATCH_POINT,
                Collections.singletonList(
                        new ItemRequirement(ItemID.PENDANT_OF_ATES, 1)
                )
        ));

        location.addTeleportOption(new Teleport(
                "Fairy_Ring",
                Teleport.Category.FAIRY_RING,
                "Use Fairy Ring AIS, then run west and use the shortcuts to Nemus Retreat tree patch.",
                0,
                "",
                0,
                0,
                5427,
                NEMUS_RETREAT_TREE_PATCH_POINT,
                fairyRingSupplier.get()
        ));

        return location;
    }
}
