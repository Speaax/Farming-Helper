package com.easyfarming.locations.special;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.core.Location;
import com.easyfarming.core.Teleport;
import com.easyfarming.utils.Constants;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import net.runelite.api.gameval.ItemID;

public final class TaiBwoWannaiCalquatLocationData {

    private TaiBwoWannaiCalquatLocationData() {
    }

    public static Location create(EasyFarmingConfig config, Supplier<List<ItemRequirement>> fairyRingSupplier) {
        Location location = new Location(null, config, "Tai Bwo Wannai", false);
        location.addTeleportOption(new Teleport(
                "Tai_Bwo_Wannai_Teleport", Teleport.Category.ITEM,
                "Teleport to Tai Bwo Wannai with a Tai Bwo Wannai teleport scroll, then walk to the calquat patch.",
                ItemID.TELEPORTSCROLL_TAIBWO, "", 0, 0,
                Constants.TAI_BWO_WANNAI_CALQUAT_PATCH_POINT.getRegionID(), Constants.TAI_BWO_WANNAI_CALQUAT_PATCH_POINT,
                Collections.singletonList(new ItemRequirement(ItemID.TELEPORTSCROLL_TAIBWO, 1))));
        location.addTeleportOption(new Teleport(
                "Brimhaven_POH_Tablet", Teleport.Category.ITEM,
                "Teleport to Brimhaven with a POH tablet, then walk to the Tai Bwo Wannai calquat patch.",
                ItemID.NZONE_TELETAB_BRIMHAVEN, "", 0, 0,
                Constants.TAI_BWO_WANNAI_CALQUAT_PATCH_POINT.getRegionID(), Constants.TAI_BWO_WANNAI_CALQUAT_PATCH_POINT,
                Collections.singletonList(new ItemRequirement(ItemID.NZONE_TELETAB_BRIMHAVEN, 1))));
        location.addTeleportOption(new Teleport(
                "Fairy_Ring", Teleport.Category.FAIRY_RING,
                "Use Fairy Ring CKR, then walk to the Tai Bwo Wannai calquat patch.",
                0, "", 0, 0,
                Constants.TAI_BWO_WANNAI_CALQUAT_PATCH_POINT.getRegionID(), Constants.TAI_BWO_WANNAI_CALQUAT_PATCH_POINT,
                fairyRingSupplier.get()));
        location.addTeleportOption(Teleport.none("Tai Bwo Wannai calquat patch",
                Constants.TAI_BWO_WANNAI_CALQUAT_PATCH_POINT.getRegionID(), Constants.TAI_BWO_WANNAI_CALQUAT_PATCH_POINT));
        return location;
    }
}
