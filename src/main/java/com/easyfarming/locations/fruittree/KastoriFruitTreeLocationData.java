package com.easyfarming.locations.fruittree;

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
 * Location definition for the Kastori fruit tree patch (Varlamore).
 * RuneLite timetracking maps this patch to {@link net.runelite.api.gameval.VarbitID#FARMING_TRANSMIT_B}
 * (same transmit slot as the Gnome Stronghold fruit tree).
 */
public class KastoriFruitTreeLocationData {

    private static final WorldPoint KASTORI_FRUIT_TREE_PATCH_POINT = new WorldPoint(1350, 3057, 0);

    public static Location create(EasyFarmingConfig config) {
        Location location = new Location(
                EasyFarmingConfig::enumFruitTreeKastoriTeleport,
                config,
                "Kastori",
                false
        );

        addTeleportOptions(location, KASTORI_FRUIT_TREE_PATCH_POINT, "fruit tree");
        return location;
    }

    public static Location createCalquat(EasyFarmingConfig config) {
        Location location = new Location(null, config, "Kastori", false);
        addTeleportOptions(location, Constants.KASTORI_CALQUAT_PATCH_POINT, "calquat");
        return location;
    }

    private static void addTeleportOptions(Location location, WorldPoint patchPoint, String patchType) {
        location.addTeleportOption(new Teleport(
                "Quetzal_Transport",
                Teleport.Category.SPELLBOOK,
                "Teleport to Civitas with Civitas teleport spell, then use the Quetzal Transport System to Kastori and run north to the " + patchType + " patch.",
                0,
                "",
                Constants.INTERFACE_MAGIC_SPELLBOOK,
                Constants.SPELL_CHILD_FORTIS_TELEPORT,
                patchPoint.getRegionID(),
                patchPoint,
                Arrays.asList(
                        new ItemRequirement(ItemID.LAWRUNE, 2),
                        new ItemRequirement(ItemID.EARTHRUNE, 1),
                        new ItemRequirement(ItemID.FIRERUNE, 1)
                )
        ));

        location.addTeleportOption(new Teleport(
                "Pendant_of_Ates",
                Teleport.Category.ITEM,
                "Teleport to Kastori with Pendant of Ates, then run south-west to the " + patchType + " patch.",
                ItemID.PENDANT_OF_ATES,
                "",
                0,
                0,
                patchPoint.getRegionID(),
                patchPoint,
                Collections.singletonList(
                        new ItemRequirement(ItemID.PENDANT_OF_ATES, 1)
                )
        ));

        // No teleport - travel manually.
        location.addTeleportOption(Teleport.none("Kastori", patchPoint.getRegionID(), patchPoint));
    }
}
