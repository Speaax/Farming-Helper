package com.easyfarming.locations.special;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.core.Location;
import com.easyfarming.core.Teleport;
import com.easyfarming.utils.Constants;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public final class FossilIslandHardwoodLocationData {

    private FossilIslandHardwoodLocationData() {
    }

    public static Location create(EasyFarmingConfig config, Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        Location location = new Location(null, config, "Fossil Island", false);
        location.addTeleportOption(new Teleport(
                "Digsite_pendant", Teleport.Category.ITEM,
                "Teleport to the Digsite with a Digsite pendant, quick-travel with the Barge guard to Fossil Island, then use the northern agility shortcut, if available, to reach the hardwood patches.",
                Constants.BASE_DIGSITE_PENDANT_ID, "", 0, 0,
                Constants.FOSSIL_ISLAND_HARDWOOD_EAST_PATCH_POINT.getRegionID(),
                Constants.FOSSIL_ISLAND_HARDWOOD_EAST_PATCH_POINT,
                Collections.singletonList(new ItemRequirement(Constants.BASE_DIGSITE_PENDANT_ID, 1))));
        location.addTeleportOption(new Teleport(
                "Mounted_Digsite_pendant", Teleport.Category.MOUNTED_POH,
                "Use the mounted Digsite pendant in your house to teleport to Fossil Island, then travel to the hardwood patches.",
                Constants.MOUNTED_DIGSITE_PENDANT_OBJECT_IDS.get(0), "Teleport menu",
                Constants.INTERFACE_SPIRIT_TREE, Constants.INTERFACE_SPIRIT_TREE_CHILD,
                Constants.FOSSIL_ISLAND_HARDWOOD_EAST_PATCH_POINT.getRegionID(),
                Constants.FOSSIL_ISLAND_HARDWOOD_EAST_PATCH_POINT,
                houseTeleportSupplier.get()));
        location.addTeleportOption(Teleport.none("Fossil Island hardwood patches",
                Constants.FOSSIL_ISLAND_HARDWOOD_EAST_PATCH_POINT.getRegionID(),
                Constants.FOSSIL_ISLAND_HARDWOOD_EAST_PATCH_POINT));
        return location;
    }
}
