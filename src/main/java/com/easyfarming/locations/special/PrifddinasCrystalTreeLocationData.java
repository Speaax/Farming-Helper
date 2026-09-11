package com.easyfarming.locations.special;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.core.Location;
import com.easyfarming.core.Teleport;
import com.easyfarming.utils.Constants;
import java.util.Collections;

public final class PrifddinasCrystalTreeLocationData {

    private PrifddinasCrystalTreeLocationData() {
    }

    public static Location create(EasyFarmingConfig config) {
        Location location = new Location(null, config, "Prifddinas", false);
        location.addTeleportOption(new Teleport(
                "Teleport_crystal", Teleport.Category.ITEM,
                "Teleport to Prifddinas with a teleport crystal, then walk to the crystal tree patch.",
                Constants.BASE_TELEPORT_CRYSTAL_ID, "", 0, 0,
                Constants.PRIFDDINAS_CRYSTAL_TREE_PATCH_POINT.getRegionID(), Constants.PRIFDDINAS_CRYSTAL_TREE_PATCH_POINT,
                Collections.singletonList(new ItemRequirement(Constants.BASE_TELEPORT_CRYSTAL_ID, 1))));
        location.addTeleportOption(new Teleport(
                "Spirit_Tree", Teleport.Category.SPIRIT_TREE,
                "Use a Spirit Tree to Prifddinas, then walk to the crystal tree patch.",
                0, "", 187, 3,
                Constants.PRIFDDINAS_CRYSTAL_TREE_PATCH_POINT.getRegionID(), Constants.PRIFDDINAS_CRYSTAL_TREE_PATCH_POINT,
                Collections.emptyList()));
        location.addTeleportOption(Teleport.none("Prifddinas crystal tree patch",
                Constants.PRIFDDINAS_CRYSTAL_TREE_PATCH_POINT.getRegionID(), Constants.PRIFDDINAS_CRYSTAL_TREE_PATCH_POINT));
        return location;
    }
}
