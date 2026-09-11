package com.easyfarming.locations.special;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import com.easyfarming.core.Location;
import com.easyfarming.core.Teleport;
import com.easyfarming.utils.Constants;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import net.runelite.api.gameval.ItemID;

public final class LocusOasisHardwoodLocationData {

    private LocusOasisHardwoodLocationData() {
    }

    public static Location create(EasyFarmingConfig config, Supplier<List<ItemRequirement>> fairyRingSupplier) {
        Location location = new Location(null, config, "Locus Oasis", false);
        location.addTeleportOption(new Teleport(
                "Colossal_Wyrm_Teleport", Teleport.Category.ITEM,
                "Use a Colossal Wyrm teleport scroll, then travel to the Locus Oasis hardwood patch.",
                ItemID.TELEPORTSCROLL_COLOSSAL_WYRM, "", 0, 0,
                Constants.LOCUS_OASIS_HARDWOOD_PATCH_POINT.getRegionID(), Constants.LOCUS_OASIS_HARDWOOD_PATCH_POINT,
                Collections.singletonList(new ItemRequirement(ItemID.TELEPORTSCROLL_COLOSSAL_WYRM, 1))));
        location.addTeleportOption(new Teleport(
                "Fairy_Ring", Teleport.Category.FAIRY_RING,
                "Use Fairy Ring AJP, then travel to the Locus Oasis hardwood patch.",
                0, "", 0, 0,
                Constants.LOCUS_OASIS_HARDWOOD_PATCH_POINT.getRegionID(), Constants.LOCUS_OASIS_HARDWOOD_PATCH_POINT,
                fairyRingSupplier.get()));
        location.addTeleportOption(new Teleport(
                "Quetzal_whistle", Teleport.Category.ITEM,
                "Use a Quetzal whistle, select Colossal Wyrm Remains, then run north to the hardwood patch.",
                ItemID.HG_QUETZALWHISTLE_BASIC, "", 0, 0,
                Constants.LOCUS_OASIS_HARDWOOD_PATCH_POINT.getRegionID(), Constants.LOCUS_OASIS_HARDWOOD_PATCH_POINT,
                Collections.singletonList(new ItemRequirement(ItemID.HG_QUETZALWHISTLE_BASIC, 1))));
        location.addTeleportOption(new Teleport(
                "Quetzal_Transport", Teleport.Category.SPELLBOOK,
                "Teleport to Civitas with the Civitas teleport spell, use the Quetzal Transport System to Colossal Wyrm Remains, then run north to the hardwood patch.",
                0, "", Constants.INTERFACE_MAGIC_SPELLBOOK, Constants.SPELL_CHILD_FORTIS_TELEPORT,
                Constants.LOCUS_OASIS_HARDWOOD_PATCH_POINT.getRegionID(), Constants.LOCUS_OASIS_HARDWOOD_PATCH_POINT,
                Arrays.asList(new ItemRequirement(ItemID.LAWRUNE, 2), new ItemRequirement(ItemID.EARTHRUNE, 1),
                        new ItemRequirement(ItemID.FIRERUNE, 1))));
        location.addTeleportOption(Teleport.none("Locus Oasis hardwood patch",
                Constants.LOCUS_OASIS_HARDWOOD_PATCH_POINT.getRegionID(), Constants.LOCUS_OASIS_HARDWOOD_PATCH_POINT));
        return location;
    }
}
