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
import net.runelite.api.gameval.ObjectID;

public final class AnglersRetreatHardwoodLocationData {

    private AnglersRetreatHardwoodLocationData() {
    }

    public static Location create(EasyFarmingConfig config, Supplier<List<ItemRequirement>> houseTeleportSupplier) {
        Location location = new Location(null, config, "Anglers' Retreat", false);
        location.addTeleportOption(new Teleport(
                "Fishing_Trawler", Teleport.Category.SPELLBOOK,
                "Open the Minigame Teleport, select Fishing Trawler (requires 15 Fishing) to reach Port Khazard, take a charter ship to Corsair Cove, then use the repaired rowboat to Anglers' Retreat.",
                0, "", Constants.INTERFACE_MAGIC_SPELLBOOK, Constants.SPELL_CHILD_MINIGAME_TELEPORT,
                Constants.ANGLERS_RETREAT_HARDWOOD_PATCH_POINT.getRegionID(), Constants.ANGLERS_RETREAT_HARDWOOD_PATCH_POINT,
                Collections.emptyList()));
        location.addTeleportOption(new Teleport(
                "Rimmington_POH", Teleport.Category.MOUNTED_POH,
                "Teleport to your house if your house is in Rimmington, exit, run west and ask Cabin Boy Colin to take you to Corsair Cove (requires The Corsair Curse), then run to the repaired rowboat and row to Anglers' Retreat.",
                ObjectID.POH_EXIT_PORTAL, "", 0, 0,
                Constants.ANGLERS_RETREAT_HARDWOOD_PATCH_POINT.getRegionID(), Constants.ANGLERS_RETREAT_HARDWOOD_PATCH_POINT,
                houseTeleportSupplier.get()));
        location.addTeleportOption(new Teleport(
                "Sailors_amulet", Teleport.Category.ITEM,
                "Teleport to The Pandemonium with a sailors' amulet, take a charter ship to Corsair Cove, then use the repaired rowboat to Anglers' Retreat. Deepfin Point is faster if unlocked.",
                ItemID.SAILORS_AMULET, "", 0, 0,
                Constants.ANGLERS_RETREAT_HARDWOOD_PATCH_POINT.getRegionID(), Constants.ANGLERS_RETREAT_HARDWOOD_PATCH_POINT,
                Collections.singletonList(new ItemRequirement(ItemID.SAILORS_AMULET, 1))));
        location.addTeleportOption(new Teleport(
                "Mythical_cape", Teleport.Category.ITEM,
                "Teleport to the Myths' Guild with a mythical cape, run to the repaired Corsair Cove rowboat, then row to Anglers' Retreat.",
                ItemID.MYTHICAL_CAPE, "", 0, 0,
                Constants.ANGLERS_RETREAT_HARDWOOD_PATCH_POINT.getRegionID(), Constants.ANGLERS_RETREAT_HARDWOOD_PATCH_POINT,
                Collections.singletonList(new ItemRequirement(ItemID.MYTHICAL_CAPE, 1))));
        location.addTeleportOption(new Teleport(
                "Mounted_Mythical_cape", Teleport.Category.MOUNTED_POH,
                "Use the mounted mythical cape in your house to teleport to the Myths' Guild, run to the repaired Corsair Cove rowboat, then row to Anglers' Retreat.",
                Constants.MOUNTED_MYTHICAL_CAPE_OBJECT_ID, "", 0, 0,
                Constants.ANGLERS_RETREAT_HARDWOOD_PATCH_POINT.getRegionID(), Constants.ANGLERS_RETREAT_HARDWOOD_PATCH_POINT,
                houseTeleportSupplier.get()));
        location.addTeleportOption(new Teleport(
                "Spirit_Tree", Teleport.Category.SPIRIT_TREE,
                "Use a Spirit Tree to Feldip Hills, run east to the repaired rowboat, then row to Anglers' Retreat.",
                0, "", Constants.INTERFACE_SPIRIT_TREE, Constants.INTERFACE_SPIRIT_TREE_CHILD,
                Constants.ANGLERS_RETREAT_HARDWOOD_PATCH_POINT.getRegionID(), Constants.ANGLERS_RETREAT_HARDWOOD_PATCH_POINT,
                Collections.emptyList()));
        location.addTeleportOption(Teleport.none("Anglers' Retreat hardwood patch",
                Constants.ANGLERS_RETREAT_HARDWOOD_PATCH_POINT.getRegionID(), Constants.ANGLERS_RETREAT_HARDWOOD_PATCH_POINT));
        return location;
    }
}
