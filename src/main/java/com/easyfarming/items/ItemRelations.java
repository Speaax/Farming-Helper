package com.easyfarming.items;

import net.runelite.api.gameval.ItemID;
import java.util.*;

/**
 * Groups semantically-equivalent items so a single call can check whether
 * any real item satisfies a generic category requirement.
 *
 * Each category is identified by a sentinel integer constant.
 * Use the {@code matches(realItemId, categoryId)} helper to test membership
 * and {@code countMatching(inventoryIds, inventoryQtys, categoryId)} to tally
 * how many of a category are present.
 */
public final class ItemRelations {

    // ── Category sentinel IDs ──────────────────────────────────────────────
    public static final int ANY_HERB_SEED         = ItemID.GUAM_SEED;
    public static final int ANY_ALLOTMENT_SEED    = ItemID.SNAPE_GRASS_SEED;
    public static final int ANY_TREE_SAPLING      = ItemID.PLANTPOT_OAK_SAPLING;
    public static final int ANY_FRUIT_SAPLING     = ItemID.PLANTPOT_APPLE_SAPLING;
    public static final int ANY_HOPS_SEED         = ItemID.BARLEY_SEED;
    public static final int ANY_WATERING_CAN      = ItemID.WATERING_CAN_0;

    // ── Member sets ────────────────────────────────────────────────────────
    public static final Set<Integer> HERB_SEEDS = new HashSet<>(Arrays.asList(
            ItemID.GUAM_SEED, ItemID.MARRENTILL_SEED, ItemID.TARROMIN_SEED,
            ItemID.HARRALANDER_SEED, ItemID.RANARR_SEED, ItemID.TOADFLAX_SEED,
            ItemID.IRIT_SEED, ItemID.AVANTOE_SEED, ItemID.KWUARM_SEED,
            ItemID.SNAPDRAGON_SEED, ItemID.CADANTINE_SEED, ItemID.LANTADYME_SEED,
            ItemID.DWARF_WEED_SEED, ItemID.TORSTOL_SEED, ItemID.HUASCA_SEED
    ));

    public static final Set<Integer> ALLOTMENT_SEEDS = new HashSet<>(Arrays.asList(
            ItemID.POTATO_SEED, ItemID.ONION_SEED, ItemID.CABBAGE_SEED,
            ItemID.TOMATO_SEED, ItemID.SWEETCORN_SEED, ItemID.STRAWBERRY_SEED,
            ItemID.WATERMELON_SEED, ItemID.SNAPE_GRASS_SEED
    ));

    public static final Set<Integer> TREE_SAPLINGS = new HashSet<>(Arrays.asList(
            ItemID.PLANTPOT_OAK_SAPLING, ItemID.PLANTPOT_WILLOW_SAPLING,
            ItemID.PLANTPOT_MAPLE_SAPLING, ItemID.PLANTPOT_YEW_SAPLING,
            ItemID.PLANTPOT_MAGIC_TREE_SAPLING
    ));

    public static final Set<Integer> FRUIT_SAPLINGS = new HashSet<>(Arrays.asList(
            ItemID.PLANTPOT_APPLE_SAPLING, ItemID.PLANTPOT_BANANA_SAPLING,
            ItemID.PLANTPOT_ORANGE_SAPLING, ItemID.PLANTPOT_CURRY_SAPLING,
            ItemID.PLANTPOT_PINEAPPLE_SAPLING, ItemID.PLANTPOT_PAPAYA_SAPLING,
            ItemID.PLANTPOT_PALM_SAPLING, ItemID.PLANTPOT_DRAGONFRUIT_SAPLING
    ));

    public static final Set<Integer> HOPS_SEEDS = new HashSet<>(Arrays.asList(
            ItemID.BARLEY_SEED, ItemID.HAMMERSTONE_HOP_SEED, ItemID.ASGARNIAN_HOP_SEED,
            ItemID.JUTE_SEED, ItemID.YANILLIAN_HOP_SEED, ItemID.KRANDORIAN_HOP_SEED,
            ItemID.WILDBLOOD_HOP_SEED
    ));

    public static final Set<Integer> WATERING_CANS = new HashSet<>(Arrays.asList(
            ItemID.WATERING_CAN_0, ItemID.WATERING_CAN_1, ItemID.WATERING_CAN_2,
            ItemID.WATERING_CAN_3, ItemID.WATERING_CAN_4, ItemID.WATERING_CAN_5,
            ItemID.WATERING_CAN_6, ItemID.WATERING_CAN_7, ItemID.WATERING_CAN_8
    ));

    // Map from sentinel → member set  (built once at class-load time)
    private static final Map<Integer, Set<Integer>> CATEGORY_MAP;
    static {
        Map<Integer, Set<Integer>> m = new HashMap<>();
        m.put(ANY_HERB_SEED,      HERB_SEEDS);
        m.put(ANY_ALLOTMENT_SEED, ALLOTMENT_SEEDS);
        m.put(ANY_TREE_SAPLING,   TREE_SAPLINGS);
        m.put(ANY_FRUIT_SAPLING,  FRUIT_SAPLINGS);
        m.put(ANY_HOPS_SEED,      HOPS_SEEDS);
        m.put(ANY_WATERING_CAN,   WATERING_CANS);
        CATEGORY_MAP = Collections.unmodifiableMap(m);
    }

    private ItemRelations() {}

    /**
     * Returns true if {@code realItemId} belongs to the group identified by
     * {@code categoryId}.  If {@code categoryId} is not a known sentinel the
     * method falls back to an exact-ID comparison.
     */
    public static boolean matches(int realItemId, int categoryId) {
        Set<Integer> group = CATEGORY_MAP.get(categoryId);
        if (group != null) return group.contains(realItemId);
        return realItemId == categoryId;
    }

    /**
     * Returns true when {@code categoryId} describes a group of many items
     * rather than a specific single item.
     */
    public static boolean isCategory(int categoryId) {
        return CATEGORY_MAP.containsKey(categoryId);
    }

    /**
     * Sums all quantities in {@code inventoryQtys} whose matching
     * {@code inventoryIds} element satisfies {@code matches(id, categoryId)}.
     */
    public static int countMatching(int[] inventoryIds, int[] inventoryQtys, int categoryId) {
        int total = 0;
        for (int i = 0; i < inventoryIds.length; i++) {
            if (matches(inventoryIds[i], categoryId)) {
                total += inventoryQtys[i];
            }
        }
        return total;
    }
}
