package com.easyfarming.utils;

import net.runelite.api.gameval.ItemID;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Fairy rings accept a Dramen staff, Lunar staff, or unfinished Lunar staff stages.
 * Checklist requirements are stored as {@link ItemID#DRAMEN_STAFF}; this helper
 * counts any acceptable substitute from an item-count map.
 */
public final class FairyRingStaffRequirements {

    /** Finished Lunar staff (Jagex name: Moonclan liminal staff). */
    public static final int LUNAR_STAFF = ItemID.LUNAR_MOONCLAN_LIMINAL_STAFF;

    /**
     * Any of these satisfies the fairy-ring staff checklist requirement.
     * Unfinished Lunar Diplomacy stages still function as a Dramen staff in-game.
     */
    public static final List<Integer> FAIRY_RING_STAFF_IDS = Collections.unmodifiableList(Arrays.asList(
            ItemID.DRAMEN_STAFF,
            LUNAR_STAFF,
            ItemID.DRAMEN_STAFF_AIR,
            ItemID.DRAMEN_STAFF_FIRE,
            ItemID.DRAMEN_STAFF_WATER
    ));

    private FairyRingStaffRequirements() {}

    /** Whether {@code itemId} is a Dramen/Lunar (or unfinished Lunar) staff. */
    public static boolean isFairyRingStaff(int itemId) {
        return FAIRY_RING_STAFF_IDS.contains(itemId);
    }

    /**
     * Total count of any fairy-ring-capable staff in {@code itemCounts}
     * (inventory + equipped merged map is fine).
     */
    public static int countFairyRingStaffs(Map<Integer, Integer> itemCounts) {
        if (itemCounts == null || itemCounts.isEmpty()) {
            return 0;
        }
        int total = 0;
        for (int staffId : FAIRY_RING_STAFF_IDS) {
            total += itemCounts.getOrDefault(staffId, 0);
        }
        return total;
    }
}
