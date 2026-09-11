package com.easyfarming.locations.special;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.core.Location;
import com.easyfarming.core.Teleport;
import com.easyfarming.utils.Constants;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public final class GreatConchCalquatLocationData {

    private GreatConchCalquatLocationData() {
    }

    public static Location create(EasyFarmingConfig config, Supplier<List<ItemRequirement>> fairyRingSupplier) {
        Location location = new Location(null, config, "Great Conch", false);
        location.addTeleportOption(new Teleport(
                "Fairy_Ring", Teleport.Category.FAIRY_RING,
                "Use Fairy Ring CJQ, then take the charter ship and Sailing route to the Great Conch calquat patch.",
                0, "", 0, 0,
                Constants.GREAT_CONCH_CALQUAT_PATCH_POINT.getRegionID(), Constants.GREAT_CONCH_CALQUAT_PATCH_POINT,
                fairyRingSupplier.get()));
        location.addTeleportOption(new Teleport(
                "None", Teleport.Category.NONE,
                "No teleport - reach Great Conch by charter ship and Sailing, then travel to the calquat patch.",
                0, "", 0, 0,
                Constants.GREAT_CONCH_CALQUAT_PATCH_POINT.getRegionID(), Constants.GREAT_CONCH_CALQUAT_PATCH_POINT,
                Collections.emptyList()));
        return location;
    }
}
