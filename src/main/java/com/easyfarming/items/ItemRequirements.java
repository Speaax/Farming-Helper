package com.easyfarming.items;

import com.easyfarming.core.Location;
import com.easyfarming.core.Teleport;
import net.runelite.api.Item;
import net.runelite.api.gameval.ItemID;

import java.util.*;

/**
 * Single source of truth for what items a custom run requires, and
 * whether those items have been collected.
 *
 * <p>All "any X" requirements are stored using the sentinel IDs from
 * {@link ItemRelations} so that the inventory checker can use
 * {@link ItemRelations#matches} without any per-type special casing.
 */
public final class ItemRequirements {

    // ── Compost bucket variants counted as "has bucket" ───────────────────
    private static final Set<Integer> BOTTOMLESS_BUCKET_IDS = new HashSet<>(Arrays.asList(
            ItemID.BOTTOMLESS_COMPOST_BUCKET,
            22994, 22995, 22996, 22997, 22998   // filled states
    ));

    // ── Teleport-item groups that collapse to a sentinel for display ───────
    private static final Map<Integer, Integer> TELEPORT_NORMALISE;
    static {
        Map<Integer, Integer> m = new HashMap<>();
        // Teleport crystal variants → base
        m.put(ItemID.MOURNING_TELEPORT_CRYSTAL_2, ItemID.MOURNING_TELEPORT_CRYSTAL_1);
        m.put(ItemID.MOURNING_TELEPORT_CRYSTAL_3, ItemID.MOURNING_TELEPORT_CRYSTAL_1);
        m.put(ItemID.MOURNING_TELEPORT_CRYSTAL_4, ItemID.MOURNING_TELEPORT_CRYSTAL_1);
        m.put(ItemID.MOURNING_TELEPORT_CRYSTAL_5, ItemID.MOURNING_TELEPORT_CRYSTAL_1);
        m.put(ItemID.PRIF_TELEPORT_CRYSTAL,        ItemID.MOURNING_TELEPORT_CRYSTAL_1);
        // Quetzal whistle variants → basic
        m.put(ItemID.HG_QUETZALWHISTLE_ENHANCED,  ItemID.HG_QUETZALWHISTLE_BASIC);
        m.put(ItemID.HG_QUETZALWHISTLE_PERFECTED, ItemID.HG_QUETZALWHISTLE_BASIC);
        // Explorer's ring variants → medium
        m.put(ItemID.LUMBRIDGE_RING_HARD,  ItemID.LUMBRIDGE_RING_MEDIUM);
        m.put(ItemID.LUMBRIDGE_RING_ELITE, ItemID.LUMBRIDGE_RING_MEDIUM);
        // Ardougne cloak variants → medium
        m.put(ItemID.ARDY_CAPE_HARD,  ItemID.ARDY_CAPE_MEDIUM);
        m.put(ItemID.ARDY_CAPE_ELITE, ItemID.ARDY_CAPE_MEDIUM);
        // Hunter skillcape trimmed → regular
        m.put(ItemID.SKILLCAPE_HUNTING_TRIMMED, ItemID.SKILLCAPE_HUNTING);
        TELEPORT_NORMALISE = Collections.unmodifiableMap(m);
    }

    // Items that should be capped at 1 regardless of how many locations need them
    private static final Set<Integer> CAP_AT_ONE_IDS = new HashSet<>(Arrays.asList(
            ItemID.SKILLCAPE_CONSTRUCTION, ItemID.SKILLCAPE_CONSTRUCTION_TRIMMED, ItemID.SKILLCAPE_MAX,
            ItemID.HG_QUETZALWHISTLE_BASIC, ItemID.SKILLCAPE_HUNTING, ItemID.SKILLCAPE_HUNTING_TRIMMED,
            ItemID.LUMBRIDGE_RING_MEDIUM, ItemID.ARDY_CAPE_MEDIUM,
            ItemID.DRAMEN_STAFF, ItemID.MM2_ROYAL_SEED_POD
    ));

    private ItemRequirements() {}

    // ── Public API ─────────────────────────────────────────────────────────

    /**
     * Builds the full set of items required for the given enabled locations.
     *
     * @param enabledLocations ordered list of locations in the run
     * @param useSpade         always true (kept as param for symmetry)
     * @param useDibber        include seed dibber?
     * @param useSecateurs     include magic secateurs?
     * @param useRake          include rake?
     * @param compostId        the selected compost item ID, or -1 for none
     * @return map of itemId → quantity required
     */
    public static Map<Integer, Integer> buildRequirements(
            List<Location> enabledLocations,
            boolean useSpade,
            boolean useDibber,
            boolean useSecateurs,
            boolean useRake,
            int compostId) {

        if (enabledLocations == null || enabledLocations.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Integer, Integer> req = new HashMap<>();
        boolean needsDibber     = false;
        boolean needsWateringCan = false;

        for (Location location : enabledLocations) {
            // ── Teleport items ──────────────────────────────────────────────
            Teleport teleport = location.getSelectedTeleport();
            if (teleport != null && teleport.getItemRequirements() != null) {
                addTeleportItems(teleport.getItemRequirements(), req);
            }

            // ── Patch items ─────────────────────────────────────────────────
            List<String> order  = location.getCustomPatchOrder();
            Map<String, Boolean> states = location.getCustomPatchStates();
            if (order == null || states == null) continue;

            for (String patch : order) {
                if (!states.getOrDefault(patch, false)) continue;

                String lower = patch.toLowerCase();

                // spade always needed
                if (lower.contains("herb")) {
                    req.merge(ItemRelations.ANY_HERB_SEED, 1, Integer::sum);
                    needsDibber = true;
                    addCompost(req, compostId, 1);

                } else if (lower.contains("flower")) {
                    req.merge(ItemID.LIMPWURT_SEED, 1, Integer::sum);
                    needsDibber = true;
                    addCompost(req, compostId, 1);

                } else if (lower.contains("allotment")) {
                    req.merge(ItemRelations.ANY_ALLOTMENT_SEED, 3, Integer::sum);
                    needsDibber = true;
                    addCompost(req, compostId, 1);

                } else if (lower.contains("fruit tree")) {
                    req.merge(ItemRelations.ANY_FRUIT_SAPLING, 1, Integer::sum);
                    req.merge(ItemID.COINS, 200, Integer::sum);
                    addCompost(req, compostId, 1);

                } else if (lower.contains("tree")) {
                    req.merge(ItemRelations.ANY_TREE_SAPLING, 1, Integer::sum);
                    req.merge(ItemID.COINS, 200, Integer::sum);
                    addCompost(req, compostId, 1);

                } else if (lower.contains("hops")) {
                    req.merge(ItemRelations.ANY_HOPS_SEED, 4, Integer::sum);
                    needsDibber = true;
                    needsWateringCan = true;
                    addCompost(req, compostId, 1);
                }
            }
        }

        // ── Tools ───────────────────────────────────────────────────────────
        if (useSpade)    req.put(ItemID.SPADE, 1);
        if (useDibber && needsDibber) req.put(ItemID.DIBBER, 1);
        if (useSecateurs) req.put(ItemID.FAIRY_ENCHANTED_SECATEURS, 1);
        if (useRake)     req.put(ItemID.RAKE, 1);
        if (needsWateringCan) req.put(ItemRelations.ANY_WATERING_CAN, 1);

        // Bottomless bucket collapse (if selected, replace per-location compost with 1 bucket)
        if (compostId == ItemID.BOTTOMLESS_COMPOST_BUCKET) {
            // remove any regular compost that may have been added before we knew
            req.entrySet().removeIf(e -> isRegularCompost(e.getKey()));
            req.put(ItemID.BOTTOMLESS_COMPOST_BUCKET, 1);
        }

        return req;
    }

    /**
     * Compares {@code requirements} against what the player has, returning
     * a map of {@code itemId → deficit} for every missing item.  An empty
     * map means all items are collected.
     *
     * @param requirements      output of {@link #buildRequirements}
     * @param inventoryItems    player inventory items (may be null/empty)
     * @param equippedItemCounts equipped items as id→quantity map
     * @return missing items (empty = all collected)
     */
    public static Map<Integer, Integer> getMissingItems(
            Map<Integer, Integer> requirements,
            Item[] inventoryItems,
            Map<Integer, Integer> equippedItemCounts) {

        if (requirements == null || requirements.isEmpty()) return Collections.emptyMap();
        if (inventoryItems == null) inventoryItems = new Item[0];
        if (equippedItemCounts == null) equippedItemCounts = Collections.emptyMap();

        // Build flat arrays for ItemRelations.countMatching
        int[] invIds  = new int[inventoryItems.length];
        int[] invQtys = new int[inventoryItems.length];
        for (int i = 0; i < inventoryItems.length; i++) {
            Item it = inventoryItems[i];
            if (it != null) {
                invIds[i]  = it.getId();
                invQtys[i] = it.getQuantity();
            }
        }

        Map<Integer, Integer> missing = new LinkedHashMap<>();

        for (Map.Entry<Integer, Integer> entry : requirements.entrySet()) {
            int itemId   = entry.getKey();
            int required = entry.getValue();

            int have = countHave(itemId, invIds, invQtys, equippedItemCounts, inventoryItems);

            if (have < required) {
                missing.put(itemId, required - have);
            }
        }

        return missing;
    }

    // ── Private helpers ─────────────────────────────────────────────────────

    private static int countHave(int itemId,
                                  int[] invIds, int[] invQtys,
                                  Map<Integer, Integer> equipped,
                                  Item[] rawInventory) {
        // Category items (any herb seed, any sapling, etc.)
        if (ItemRelations.isCategory(itemId)) {
            int total = ItemRelations.countMatching(invIds, invQtys, itemId);
            // also check equipped slots
            for (Map.Entry<Integer, Integer> e : equipped.entrySet()) {
                if (ItemRelations.matches(e.getKey(), itemId)) total += e.getValue();
            }
            return total;
        }

        // Bottomless compost bucket – any variant counts as 1
        if (itemId == ItemID.BOTTOMLESS_COMPOST_BUCKET) {
            for (Item it : rawInventory) {
                if (it != null && BOTTOMLESS_BUCKET_IDS.contains(it.getId())) return 1;
            }
            return equipped.entrySet().stream()
                    .anyMatch(e -> BOTTOMLESS_BUCKET_IDS.contains(e.getKey())) ? 1 : 0;
        }

        // Regular item: sum inventory + equipped
        int total = 0;
        for (int i = 0; i < invIds.length; i++) {
            if (invIds[i] == itemId) total += invQtys[i];
        }
        total += equipped.getOrDefault(itemId, 0);
        return total;
    }

    /** Adds teleport item requirements, normalising variant IDs and capping singletons. */
    private static void addTeleportItems(Map<Integer, Integer> teleportReqs,
                                          Map<Integer, Integer> target) {
        for (Map.Entry<Integer, Integer> e : teleportReqs.entrySet()) {
            int id  = TELEPORT_NORMALISE.getOrDefault(e.getKey(), e.getKey());
            int qty = e.getValue();
            if (CAP_AT_ONE_IDS.contains(id)) {
                target.merge(id, qty, (a, b) -> Math.min(1, a + b));
            } else {
                target.merge(id, qty, Integer::sum);
            }
        }
    }

    /** Adds compost to requirements unless it is none or the bottomless bucket. */
    private static void addCompost(Map<Integer, Integer> req, int compostId, int patches) {
        if (compostId != -1 && compostId != ItemID.BOTTOMLESS_COMPOST_BUCKET) {
            req.merge(compostId, patches, Integer::sum);
        }
    }

    private static boolean isRegularCompost(int id) {
        return id == ItemID.BUCKET_COMPOST || id == ItemID.BUCKET_SUPERCOMPOST || id == ItemID.BUCKET_ULTRACOMPOST;
    }
}
