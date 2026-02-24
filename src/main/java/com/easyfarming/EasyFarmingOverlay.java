package com.easyfarming;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.*;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;

import net.runelite.api.*;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.PanelComponent;
import java.awt.image.BufferedImage;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import java.util.Iterator;

import com.easyfarming.utils.Constants;

public class EasyFarmingOverlay extends Overlay {

    private final Client client;
    private final EasyFarmingPlugin plugin;
    private final PanelComponent panelComponent = new PanelComponent();
    private final ItemManager itemManager;
    private final InfoBoxManager infoBoxManager;

    // Track current InfoBoxes by item ID
    private final Map<Integer, RequiredItemInfoBox> currentInfoBoxes = new HashMap<>();

    public static final List<Integer> TELEPORT_CRYSTAL_IDS = Arrays.asList(ItemID.MOURNING_TELEPORT_CRYSTAL_1,
            ItemID.MOURNING_TELEPORT_CRYSTAL_2, ItemID.MOURNING_TELEPORT_CRYSTAL_3, ItemID.MOURNING_TELEPORT_CRYSTAL_4,
            ItemID.MOURNING_TELEPORT_CRYSTAL_5, ItemID.PRIF_TELEPORT_CRYSTAL);
    private static final int BASE_TELEPORT_CRYSTAL_ID = ItemID.MOURNING_TELEPORT_CRYSTAL_1;

    public List<Integer> getTeleportCrystalIds() {
        return TELEPORT_CRYSTAL_IDS;
    }

    public boolean isTeleportCrystal(int itemId) {
        return TELEPORT_CRYSTAL_IDS.contains(itemId);
    }

    public static final List<Integer> SKILLS_NECKLACE_IDS = Arrays.asList(ItemID.JEWL_NECKLACE_OF_SKILLS_1,
            ItemID.JEWL_NECKLACE_OF_SKILLS_2, ItemID.JEWL_NECKLACE_OF_SKILLS_3, ItemID.JEWL_NECKLACE_OF_SKILLS_4,
            ItemID.JEWL_NECKLACE_OF_SKILLS_5, ItemID.JEWL_NECKLACE_OF_SKILLS_6);

    // Bottomless compost bucket variants (empty and all filled states)
    // These IDs should match ItemID.java constants:
    // BOTTOMLESS_COMPOST_BUCKET (empty), and filled variants 22994-22998
    private static final List<Integer> BOTTOMLESS_COMPOST_BUCKET_IDS = Arrays.asList(
            ItemID.BOTTOMLESS_COMPOST_BUCKET, // Empty
            22994, // Filled variant 1
            22995, // Filled variant 2
            22996, // Filled variant 3
            22997, // Filled variant 4
            22998 // Filled variant 5
    );
    private static final int BASE_SKILLS_NECKLACE_ID = ItemID.JEWL_NECKLACE_OF_SKILLS_1;

    public List<Integer> getSkillsNecklaceIds() {
        return SKILLS_NECKLACE_IDS;
    }

    public boolean isSkillsNecklace(int itemId) {
        return SKILLS_NECKLACE_IDS.contains(itemId);
    }

    public static final List<Integer> EXPLORERS_RING_IDS = Arrays.asList(ItemID.LUMBRIDGE_RING_MEDIUM,
            ItemID.LUMBRIDGE_RING_HARD, ItemID.LUMBRIDGE_RING_ELITE);
    private static final int BASE_EXPLORERS_RING_ID = ItemID.LUMBRIDGE_RING_MEDIUM;

    public List<Integer> getExplorersRingIds() {
        return EXPLORERS_RING_IDS;
    }

    public boolean isExplorersRing(int itemId) {
        return EXPLORERS_RING_IDS.contains(itemId);
    }

    public static final List<Integer> ARDY_CLOAK_IDS = Arrays.asList(ItemID.ARDY_CAPE_MEDIUM, ItemID.ARDY_CAPE_HARD,
            ItemID.ARDY_CAPE_ELITE);
    private static final int BASE_ARDY_CLOAK_ID = ItemID.ARDY_CAPE_MEDIUM;

    public List<Integer> getArdyCloakIds() {
        return ARDY_CLOAK_IDS;
    }

    public boolean isArdyCloak(int itemId) {
        return ARDY_CLOAK_IDS.contains(itemId);
    }

    /** Delegates to {@link com.easyfarming.items.ItemRelations#WATERING_CANS}. */
    public static final Set<Integer> WATERING_CAN_IDS_SET = com.easyfarming.items.ItemRelations.WATERING_CANS;
    public static final List<Integer> WATERING_CAN_IDS = Constants.WATERING_CAN_IDS;
    private static final int BASE_WATERING_CAN_ID = Constants.WATERING_CAN_IDS.get(0);

    public List<Integer> getWateringCanIds() {
        return WATERING_CAN_IDS;
    }

    public boolean isWateringCan(int itemId) {
        return com.easyfarming.items.ItemRelations.matches(itemId, com.easyfarming.items.ItemRelations.ANY_WATERING_CAN);
    }

    public static final List<Integer> COMBAT_BRACELET_IDS = Constants.COMBAT_BRACELET_IDS;
    private static final int BASE_COMBAT_BRACELET_ID = Constants.BASE_COMBAT_BRACELET_ID;

    public boolean isCombatBracelet(int itemId) {
        return Constants.isCombatBracelet(itemId);
    }

    public List<Integer> getHerbPatchIds() {
        return Constants.HERB_PATCH_IDS;
    }

    public List<Integer> getHopsPatchIds() {
        return Constants.HOPS_PATCH_IDS;
    }

    // ── Seed groups now delegate to ItemRelations ──────────────────────────
    private static final int BASE_SEED_ID        = com.easyfarming.items.ItemRelations.ANY_HERB_SEED;
    private static final int BASE_HOPS_SEED_ID   = com.easyfarming.items.ItemRelations.ANY_HOPS_SEED;

    public List<Integer> getHerbSeedIds()  { return new ArrayList<>(com.easyfarming.items.ItemRelations.HERB_SEEDS); }
    public List<Integer> getHopsSeedIds()  { return Constants.HOPS_SEED_IDS; }

    private boolean isHerbSeed(int itemId)  { return com.easyfarming.items.ItemRelations.matches(itemId, com.easyfarming.items.ItemRelations.ANY_HERB_SEED); }
    private boolean isHopsSeed(int itemId)  { return com.easyfarming.items.ItemRelations.matches(itemId, com.easyfarming.items.ItemRelations.ANY_HOPS_SEED); }


    public List<Integer> getFlowerPatchIds() {
        return Constants.FLOWER_PATCH_IDS;
    }

    @Deprecated
    public List<Integer> getAllotmentPatchIds() {
        // Deprecated: Use getAllotmentPatchIdsForLocation() instead
        // Returns Ardougne patches for backward compatibility
        return Constants.ALLOTMENT_PATCH_IDS_BY_LOCATION.getOrDefault("Ardougne", Collections.emptyList());
    }

    /**
     * Gets allotment patch IDs for a specific location.
     * 
     * @param locationName The name of the location
     * @return List of patch object IDs [north patch, south patch], or empty list if
     *         location has no allotment patches
     */
    public List<Integer> getAllotmentPatchIdsForLocation(String locationName) {
        return Constants.ALLOTMENT_PATCH_IDS_BY_LOCATION.getOrDefault(locationName, Collections.emptyList());
    }

    /**
     * Gets herb patch ID for a specific location.
     * 
     * @param locationName The name of the location
     * @return The patch object ID, or null if location has no herb patch
     */
    public Integer getHerbPatchIdForLocation(String locationName) {
        return Constants.HERB_PATCH_IDS_BY_LOCATION.get(locationName);
    }

    /**
     * Gets flower patch ID for a specific location.
     * 
     * @param locationName The name of the location
     * @return The patch object ID, or null if location has no flower patch
     */
    public Integer getFlowerPatchIdForLocation(String locationName) {
        return Constants.FLOWER_PATCH_IDS_BY_LOCATION.get(locationName);
    }

    /**
     * Gets hops patch ID for a specific location.
     * 
     * @param locationName The name of the location
     * @return The patch object ID, or null if location has no hops patch
     */
    public Integer getHopsPatchIdForLocation(String locationName) {
        return Constants.HOPS_PATCH_IDS_BY_LOCATION.get(locationName);
    }

    /**
     * Gets fruit tree patch ID for a specific location.
     * 
     * @param locationName The name of the location
     * @return The patch object ID, or null if location has no fruit tree patch
     */
    public Integer getFruitTreePatchIdForLocation(String locationName) {
        return Constants.FRUIT_TREE_PATCH_IDS_BY_LOCATION.get(locationName);
    }

    // ── Allotment seeds ────────────────────────────────────────────────────
    private static final int BASE_ALLOTMENT_SEED_ID = com.easyfarming.items.ItemRelations.ANY_ALLOTMENT_SEED;

    public List<Integer> getAllotmentSeedIds() { return Constants.ALLOTMENT_SEED_IDS; }
    private boolean isAllotmentSeed(int itemId) { return com.easyfarming.items.ItemRelations.matches(itemId, com.easyfarming.items.ItemRelations.ANY_ALLOTMENT_SEED); }

    public List<Integer> getTreePatchIds() { return Constants.TREE_PATCH_IDS; }

    // ── Sapling groups now delegate to ItemRelations ───────────────────────
    private static final int BASE_SAPLING_ID      = com.easyfarming.items.ItemRelations.ANY_TREE_SAPLING;
    private static final int BASE_FRUIT_SAPLING_ID = com.easyfarming.items.ItemRelations.ANY_FRUIT_SAPLING;

    public List<Integer> getTreeSaplingIds()      { return new ArrayList<>(com.easyfarming.items.ItemRelations.TREE_SAPLINGS); }
    public List<Integer> getFruitTreeSaplingIds() { return new ArrayList<>(com.easyfarming.items.ItemRelations.FRUIT_SAPLINGS); }

    private boolean isTreeSapling(int itemId)      { return com.easyfarming.items.ItemRelations.matches(itemId, com.easyfarming.items.ItemRelations.ANY_TREE_SAPLING); }
    private boolean isFruitTreeSapling(int itemId) { return com.easyfarming.items.ItemRelations.matches(itemId, com.easyfarming.items.ItemRelations.ANY_FRUIT_SAPLING); }

    public List<Integer> getFruitTreePatchIds() { return Constants.FRUIT_TREE_PATCH_IDS; }


    public static final List<Integer> RUNE_POUCH_ID = Arrays.asList(ItemID.BH_RUNE_POUCH, ItemID.DIVINE_RUNE_POUCH);

    public static final List<Integer> RUNE_POUCH_AMOUNT_VARBITS = Arrays.asList(VarbitID.RUNE_POUCH_QUANTITY_1,
            VarbitID.RUNE_POUCH_QUANTITY_2, VarbitID.RUNE_POUCH_QUANTITY_3, VarbitID.RUNE_POUCH_QUANTITY_4);

    public static final List<Integer> RUNE_POUCH_RUNE_VARBITS = Arrays.asList(VarbitID.RUNE_POUCH_TYPE_1,
            VarbitID.RUNE_POUCH_TYPE_2, VarbitID.RUNE_POUCH_TYPE_3, VarbitID.RUNE_POUCH_TYPE_4);

    private static final Map<Integer, List<Integer>> COMBINATION_RUNE_SUBRUNES_MAP;

    private static final Map<Integer, List<Integer>> STAFF_RUNES_MAP;

    private static final int STAFF_RUNE_AMOUNT = 999; // Large amount to satisfy requirements without overflow

    static {
        Map<Integer, List<Integer>> tempMap = new HashMap<>();
        tempMap.put(ItemID.DUSTRUNE, Arrays.asList(ItemID.AIRRUNE, ItemID.EARTHRUNE));
        tempMap.put(ItemID.MISTRUNE, Arrays.asList(ItemID.AIRRUNE, ItemID.WATERRUNE));
        tempMap.put(ItemID.MUDRUNE, Arrays.asList(ItemID.WATERRUNE, ItemID.EARTHRUNE));
        tempMap.put(ItemID.LAVARUNE, Arrays.asList(ItemID.FIRERUNE, ItemID.EARTHRUNE));
        tempMap.put(ItemID.STEAMRUNE, Arrays.asList(ItemID.FIRERUNE, ItemID.WATERRUNE));
        tempMap.put(ItemID.SMOKERUNE, Arrays.asList(ItemID.FIRERUNE, ItemID.AIRRUNE));
        COMBINATION_RUNE_SUBRUNES_MAP = Collections.unmodifiableMap(tempMap);

        // Staff to rune mapping - Elemental staffs
        Map<Integer, List<Integer>> staffMap = new HashMap<>();

        // Air staffs
        staffMap.put(ItemID.STAFF_OF_AIR, Arrays.asList(ItemID.AIRRUNE));
        staffMap.put(ItemID.AIR_BATTLESTAFF, Arrays.asList(ItemID.AIRRUNE));
        staffMap.put(ItemID.MYSTIC_AIR_STAFF, Arrays.asList(ItemID.AIRRUNE));

        // Water staffs
        staffMap.put(ItemID.STAFF_OF_WATER, Arrays.asList(ItemID.WATERRUNE));
        staffMap.put(ItemID.WATER_BATTLESTAFF, Arrays.asList(ItemID.WATERRUNE));
        staffMap.put(ItemID.MYSTIC_WATER_STAFF, Arrays.asList(ItemID.WATERRUNE));

        // Earth staffs
        staffMap.put(ItemID.STAFF_OF_EARTH, Arrays.asList(ItemID.EARTHRUNE));
        staffMap.put(ItemID.EARTH_BATTLESTAFF, Arrays.asList(ItemID.EARTHRUNE));
        staffMap.put(ItemID.MYSTIC_EARTH_STAFF, Arrays.asList(ItemID.EARTHRUNE));

        // Fire staffs
        staffMap.put(ItemID.STAFF_OF_FIRE, Arrays.asList(ItemID.FIRERUNE));
        staffMap.put(ItemID.FIRE_BATTLESTAFF, Arrays.asList(ItemID.FIRERUNE));
        staffMap.put(ItemID.MYSTIC_FIRE_STAFF, Arrays.asList(ItemID.FIRERUNE));

        // Combination staffs - Lava (Fire + Earth)
        staffMap.put(ItemID.LAVA_BATTLESTAFF, Arrays.asList(ItemID.FIRERUNE, ItemID.EARTHRUNE));
        staffMap.put(ItemID.MYSTIC_LAVA_STAFF, Arrays.asList(ItemID.FIRERUNE, ItemID.EARTHRUNE));

        // Combination staffs - Steam (Fire + Water)
        staffMap.put(ItemID.STEAM_BATTLESTAFF, Arrays.asList(ItemID.FIRERUNE, ItemID.WATERRUNE));
        staffMap.put(ItemID.MYSTIC_STEAM_BATTLESTAFF, Arrays.asList(ItemID.FIRERUNE, ItemID.WATERRUNE));

        // Combination staffs - Mist (Air + Water)
        staffMap.put(ItemID.MIST_BATTLESTAFF, Arrays.asList(ItemID.AIRRUNE, ItemID.WATERRUNE));
        staffMap.put(ItemID.MYSTIC_MIST_BATTLESTAFF, Arrays.asList(ItemID.AIRRUNE, ItemID.WATERRUNE));

        // Combination staffs - Dust (Air + Earth)
        staffMap.put(ItemID.DUST_BATTLESTAFF, Arrays.asList(ItemID.AIRRUNE, ItemID.EARTHRUNE));
        staffMap.put(ItemID.MYSTIC_DUST_BATTLESTAFF, Arrays.asList(ItemID.AIRRUNE, ItemID.EARTHRUNE));

        // Combination staffs - Smoke (Fire + Air)
        staffMap.put(ItemID.SMOKE_BATTLESTAFF, Arrays.asList(ItemID.FIRERUNE, ItemID.AIRRUNE));
        staffMap.put(ItemID.MYSTIC_SMOKE_BATTLESTAFF, Arrays.asList(ItemID.FIRERUNE, ItemID.AIRRUNE));

        // Combination staffs - Mud (Water + Earth)
        staffMap.put(ItemID.MUD_BATTLESTAFF, Arrays.asList(ItemID.WATERRUNE, ItemID.EARTHRUNE));
        staffMap.put(ItemID.MYSTIC_MUD_STAFF, Arrays.asList(ItemID.WATERRUNE, ItemID.EARTHRUNE));

        // Twinflame Staff (Fire + Water)
        staffMap.put(30634, Arrays.asList(ItemID.FIRERUNE, ItemID.WATERRUNE));

        // Tome of Earth (Earth) - Charged
        // We use the charged ID (30064) to ensure it provides runes
        staffMap.put(30064, Arrays.asList(ItemID.EARTHRUNE));

        STAFF_RUNES_MAP = Collections.unmodifiableMap(staffMap);
    }

    private int getRuneItemIdFromVarbitValue(int varbitValue) {
        switch (varbitValue) {
            case 1:
                return ItemID.AIRRUNE;
            case 2:
                return ItemID.WATERRUNE;
            case 3:
                return ItemID.EARTHRUNE;
            case 4:
                return ItemID.FIRERUNE;
            case 5:
                return ItemID.MINDRUNE;
            case 6:
                return ItemID.CHAOSRUNE;
            case 7:
                return ItemID.DEATHRUNE;
            case 8:
                return ItemID.BLOODRUNE;
            case 9:
                return ItemID.COSMICRUNE;
            case 10:
                return ItemID.NATURERUNE;
            case 11:
                return ItemID.LAWRUNE;
            case 12:
                return ItemID.BODYRUNE;
            case 13:
                return ItemID.SOULRUNE;
            case 14:
                return ItemID.ASTRALRUNE;
            case 15:
                return ItemID.MISTRUNE;
            case 16:
                return ItemID.MUDRUNE;
            case 17:
                return ItemID.DUSTRUNE;
            case 18:
                return ItemID.LAVARUNE;
            case 19:
                return ItemID.STEAMRUNE;
            case 20:
                return ItemID.SMOKERUNE;
            case 21:
                return ItemID.WRATHRUNE;
            // Add more cases for other runes
            default:
                return -1;
        }
    }

    private Map<Integer, Integer> getRunePouchContentsVarbits() {
        Map<Integer, Integer> runePouchContents = new HashMap<>();

        for (int i = 0; i < RUNE_POUCH_RUNE_VARBITS.size(); i++) {
            int runeVarbitValue = client.getVarbitValue(RUNE_POUCH_RUNE_VARBITS.get(i));
            int runeAmount = client.getVarbitValue(RUNE_POUCH_AMOUNT_VARBITS.get(i));

            int runeId = getRuneItemIdFromVarbitValue(runeVarbitValue);

            if (runeId != -1 && runeAmount > 0) {
                handleCombinationRunes(runeId, runeAmount, runePouchContents);
            }
        }
        return runePouchContents;
    }

    private Map<Integer, Integer> buildExpandedRuneMap(Item[] items) {
        // Start with rune pouch contents
        Map<Integer, Integer> expandedRuneMap = new HashMap<>(getRunePouchContentsVarbits());

        // Add combination runes from inventory
        for (Item item : items) {
            if (item != null) {
                int itemIdRune = item.getId();
                int itemQuantity = item.getQuantity();

                if (COMBINATION_RUNE_SUBRUNES_MAP.containsKey(itemIdRune)) {
                    List<Integer> subRunes = COMBINATION_RUNE_SUBRUNES_MAP.get(itemIdRune);
                    for (int subRune : subRunes) {
                        expandedRuneMap.put(subRune, expandedRuneMap.getOrDefault(subRune, 0) + itemQuantity);
                    }
                } else if (STAFF_RUNES_MAP.containsKey(itemIdRune)) {
                    // Handle staffs - add their runes with large amount
                    List<Integer> staffRunes = STAFF_RUNES_MAP.get(itemIdRune);
                    for (int rune : staffRunes) {
                        // Use max to ensure we always have enough, but cap at STAFF_RUNE_AMOUNT to
                        // avoid overflow
                        expandedRuneMap.put(rune, Math.max(expandedRuneMap.getOrDefault(rune, 0), STAFF_RUNE_AMOUNT));
                    }
                } else {
                    // Add regular runes from inventory
                    expandedRuneMap.put(itemIdRune, expandedRuneMap.getOrDefault(itemIdRune, 0) + itemQuantity);
                }
            }
        }

        // Add staffs from equipped items
        Map<Integer, Integer> equippedItems = getEquippedItems();
        for (Map.Entry<Integer, Integer> equippedEntry : equippedItems.entrySet()) {
            int equippedItemId = equippedEntry.getKey();
            if (STAFF_RUNES_MAP.containsKey(equippedItemId)) {
                List<Integer> staffRunes = STAFF_RUNES_MAP.get(equippedItemId);
                for (int rune : staffRunes) {
                    // Use max to ensure we always have enough, but cap at STAFF_RUNE_AMOUNT to
                    // avoid overflow
                    expandedRuneMap.put(rune, Math.max(expandedRuneMap.getOrDefault(rune, 0), STAFF_RUNE_AMOUNT));
                }
            }
        }

        return expandedRuneMap;
    }

    @Inject
    public EasyFarmingOverlay(Client client, EasyFarmingPlugin plugin, ItemManager itemManager,
            InfoBoxManager infoBoxManager) {
        this.client = client;
        this.plugin = plugin;
        this.itemManager = itemManager;
        this.infoBoxManager = infoBoxManager;
        setPosition(OverlayPosition.BOTTOM_RIGHT);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    private void handleCombinationRunes(int runeId, int runeAmount, Map<Integer, Integer> runePouchContents) {
        if (COMBINATION_RUNE_SUBRUNES_MAP.containsKey(runeId)) {
            List<Integer> subRunes = COMBINATION_RUNE_SUBRUNES_MAP.get(runeId);
            for (int subRune : subRunes) {
                runePouchContents.put(subRune, runePouchContents.getOrDefault(subRune, 0) + runeAmount);
            }
        } else {
            runePouchContents.put(runeId, runeAmount);
        }
    }

    public Integer checkToolLep(Integer item) {
        if (item == ItemID.BUCKET_COMPOST) {
            return client.getVarbitValue(1442);
        }
        if (item == ItemID.BUCKET_SUPERCOMPOST) {
            return client.getVarbitValue(1443);
        }
        if (item == ItemID.BUCKET_ULTRACOMPOST) {
            return client.getVarbitValue(5732);
        }
        if (item == ItemID.BOTTOMLESS_COMPOST_BUCKET) {
            if (client.getVarbitValue(7915) != 0) {
                return 1;
            }
        }
        return 0;
    }

    // Helper method to get charges from necklace ID
    private int getSkillsNecklaceCharges(int itemId) {
        switch (itemId) {
            case ItemID.JEWL_NECKLACE_OF_SKILLS_1:
                return 1;
            case ItemID.JEWL_NECKLACE_OF_SKILLS_2:
                return 2;
            case ItemID.JEWL_NECKLACE_OF_SKILLS_3:
                return 3;
            case ItemID.JEWL_NECKLACE_OF_SKILLS_4:
                return 4;
            case ItemID.JEWL_NECKLACE_OF_SKILLS_5:
                return 5;
            case ItemID.JEWL_NECKLACE_OF_SKILLS_6:
                return 6;
            default:
                return 0;
        }
    }

    public boolean isQuetzalWhistle(int itemId) {
        return itemId == ItemID.HG_QUETZALWHISTLE_BASIC ||
                itemId == ItemID.HG_QUETZALWHISTLE_ENHANCED ||
                itemId == ItemID.HG_QUETZALWHISTLE_PERFECTED;
    }

    public boolean isRoyalSeedPod(int itemId) {
        return itemId == ItemID.MM2_ROYAL_SEED_POD;
    }

    public boolean isEctophial(int itemId) {
        return itemId == ItemID.ECTOPHIAL;
    }

    /**
     * Scans equipped items and returns a map of item IDs to their counts.
     * Equipped items are counted as 1 each (equipment slots can only hold 1 item).
     * Uses EquipmentInventorySlot with ItemContainer for reliable detection of all
     * equipped items,
     * including rings and other items that may not be detected by
     * PlayerComposition.
     */
    private Map<Integer, Integer> getEquippedItems() {
        Map<Integer, Integer> equippedItems = new HashMap<>();

        // Get the equipment ItemContainer using the equipment container ID
        // Equipment container ID is 94 in RuneLite
        ItemContainer equipment = client.getItemContainer(94);
        if (equipment == null) {
            return equippedItems;
        }

        Item[] items = equipment.getItems();
        if (items == null) {
            return equippedItems;
        }

        // Iterate through all EquipmentInventorySlot values to check each slot
        for (EquipmentInventorySlot slot : EquipmentInventorySlot.values()) {
            try {
                int slotIdx = slot.getSlotIdx();
                // Ensure the slot index is within bounds
                if (slotIdx >= 0 && slotIdx < items.length) {
                    Item item = items[slotIdx];
                    if (item != null && item.getId() > 0) {
                        int itemId = item.getId();
                        // Count each equipped item as 1
                        equippedItems.put(itemId, equippedItems.getOrDefault(itemId, 0) + 1);
                    }
                }
            } catch (Exception e) {
                // Skip invalid slots gracefully
                continue;
            }
        }

        return equippedItems;
    }

    public Map<Integer, Integer> itemsToCheck;

    @Override
    public Dimension render(Graphics2D graphics) {
        // Clean up InfoBoxes if overlay is inactive
        if (!plugin.isOverlayActive()) {
            clearAllInfoBoxes();
            return null;
        }

        if (!plugin.areItemsCollected()) {
            plugin.addTextToInfoBox("Grab all the items needed");

            // Build a set of "locationName::patchName" keys for patches that are
            // actually ready (harvestable / dead / diseased / weeds).
            // Seeds and compost are only required for patches in this set.
            FarmingTeleportOverlay teleportOverlay = plugin.getFarmingTeleportOverlay();
            java.util.List<com.easyfarming.core.Location> enabledLocs =
                    teleportOverlay != null ? teleportOverlay.getEnabledLocations() : null;

            java.util.Set<String> readyPatchKeys = null; // null = all ready (fallback if no tracker)
            com.easyfarming.runelite.farming.FarmingWorld fw = plugin.getFarmingWorld();
            com.easyfarming.runelite.farming.FarmingTracker tracker = plugin.getFarmingTracker();

            if (fw != null && tracker != null && enabledLocs != null) {
                readyPatchKeys = new java.util.HashSet<>();
                for (com.easyfarming.core.Location loc : enabledLocs) {
                    java.util.List<String> order = loc.getCustomPatchOrder();
                    java.util.Map<String, Boolean> states = loc.getCustomPatchStates();
                    if (order == null || states == null) continue;
                    for (String patchName : order) {
                        if (!states.getOrDefault(patchName, false)) continue;
                        // Look up FarmingPatch by region+name
                        for (java.util.Set<com.easyfarming.runelite.farming.FarmingPatch> patchSet : fw.getTabs().values()) {
                            for (com.easyfarming.runelite.farming.FarmingPatch fp : patchSet) {
                                String regionName = fp.getRegion().getName();
                                String implName   = fp.getImplementation().name().toLowerCase();
                                implName = implName.substring(0, 1).toUpperCase() + implName.substring(1).replace("_", " ");
                                String builtName  = (fp.getName() + " " + implName).trim();
                                if (regionName.equals(loc.getName()) && builtName.equals(patchName)) {
                                    com.easyfarming.runelite.farming.PatchPrediction pred = tracker.predictPatch(fp);
                                    if (pred != null && (
                                            pred.getCropState() == com.easyfarming.runelite.farming.CropState.HARVESTABLE ||
                                            pred.getCropState() == com.easyfarming.runelite.farming.CropState.DEAD        ||
                                            pred.getCropState() == com.easyfarming.runelite.farming.CropState.DISEASED    ||
                                            pred.getProduce()   == com.easyfarming.runelite.farming.Produce.WEEDS)) {
                                        readyPatchKeys.add(loc.getName() + "::" + patchName);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Map<Integer, Integer> requirements = com.easyfarming.items.ItemRequirements.buildRequirements(
                    enabledLocs,
                    readyPatchKeys,
                    plugin.isToolSpade(),
                    plugin.isToolDibber(),
                    plugin.isToolSecateurs(),
                    plugin.isToolRake(),
                    plugin.getCompostId()
            );


            if (requirements.isEmpty()) {
                return null;
            }

            // Merge rune-pouch and equipped items so ItemRequirements can count everything
            ItemContainer inventory = client.getItemContainer(InventoryID.INV);
            Item[] items = (inventory != null && inventory.getItems() != null)
                    ? inventory.getItems() : new Item[0];

            Map<Integer, Integer> equippedItems = getEquippedItems();

            // Expand rune pouch into equipped-like map so rune requirements still work
            boolean hasRunePouch = false;
            for (Item it : items) {
                if (it != null && RUNE_POUCH_ID.contains(it.getId())) { hasRunePouch = true; break; }
            }
            if (hasRunePouch) {
                Map<Integer, Integer> expandedRunes = buildExpandedRuneMap(items);
                for (Map.Entry<Integer, Integer> e : expandedRunes.entrySet()) {
                    equippedItems.merge(e.getKey(), e.getValue(), Integer::sum);
                }
            }
            // Expand combination runes
            Map<Integer, Integer> extraRunes = new HashMap<>();
            for (Item it : items) {
                if (it == null) continue;
                List<Integer> subs = COMBINATION_RUNE_SUBRUNES_MAP.get(it.getId());
                if (subs != null) {
                    for (int sub : subs) extraRunes.merge(sub, it.getQuantity(), Integer::sum);
                }
            }
            for (Map.Entry<Integer, Integer> e : extraRunes.entrySet()) {
                equippedItems.merge(e.getKey(), e.getValue(), Integer::sum);
            }

            Map<Integer, Integer> missing = com.easyfarming.items.ItemRequirements.getMissingItems(
                    requirements, items, equippedItems);

            // Build display list sorted by item priority
            List<AbstractMap.SimpleEntry<Integer, Integer>> missingList = new ArrayList<>();
            for (Map.Entry<Integer, Integer> e : missing.entrySet()) {
                missingList.add(new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue()));
            }
            missingList.sort((a, b) -> Integer.compare(getItemPriority(a.getKey()), getItemPriority(b.getKey())));

            panelComponent.getChildren().clear();

            // Sync InfoBoxes
            Set<Integer> currentMissingIds = new HashSet<>();
            for (AbstractMap.SimpleEntry<Integer, Integer> pair : missingList) currentMissingIds.add(pair.getKey());

            if (infoBoxManager != null) {
                Iterator<Map.Entry<Integer, RequiredItemInfoBox>> it = currentInfoBoxes.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<Integer, RequiredItemInfoBox> e = it.next();
                    if (!currentMissingIds.contains(e.getKey())) {
                        infoBoxManager.removeInfoBox(e.getValue());
                        it.remove();
                    }
                }
                for (AbstractMap.SimpleEntry<Integer, Integer> pair : missingList) {
                    int itemId = pair.getKey();
                    int count  = pair.getValue();
                    java.awt.image.BufferedImage img = itemManager.getImage(itemId);
                    if (img == null) continue;
                    RequiredItemInfoBox existing = currentInfoBoxes.get(itemId);
                    if (existing != null) {
                        if (existing.getMissingCount() != count) {
                            infoBoxManager.removeInfoBox(existing);
                            RequiredItemInfoBox updated = new RequiredItemInfoBox(img, plugin, itemId, count);
                            infoBoxManager.addInfoBox(updated);
                            currentInfoBoxes.put(itemId, updated);
                        }
                    } else {
                        RequiredItemInfoBox box = new RequiredItemInfoBox(img, plugin, itemId, count);
                        infoBoxManager.addInfoBox(box);
                        currentInfoBoxes.put(itemId, box);
                    }
                }
            }

            plugin.setTeleportOverlayActive(missingList.isEmpty());

            if (missingList.isEmpty()) {
                plugin.setItemsCollected(true);
                System.out.println("[EasyFarming] All items in inventory");
            } else {
                plugin.setItemsCollected(false);
            }

            return panelComponent.render(graphics);
        }

        // Items are collected — clear InfoBoxes
        clearAllInfoBoxes();
        return null;
    }

    private void clearAllInfoBoxes() {
        if (infoBoxManager != null) {
            for (RequiredItemInfoBox infoBox : currentInfoBoxes.values()) {
                infoBoxManager.removeInfoBox(infoBox);
            }
        }
        currentInfoBoxes.clear();
    }

    /**
     * Gets the priority for sorting items in infoboxes.
     * Lower priority = appears first.
     * 
     * @param itemId The item ID to check
     * @return 0 for tools, 1 for regular teleport items, 2 for basalts, 3 for
     *         consumable supplies
     */
    private int getItemPriority(int itemId) {
        // Tools have lowest priority (appear first)
        if (isTool(itemId)) {
            return 0;
        }

        // Consumable supplies have highest priority (appear last)
        if (isConsumableSupply(itemId)) {
            return 3;
        }

        // Basalts have medium-high priority (appear last among teleport items)
        if (isBasalt(itemId)) {
            return 2;
        }

        // Regular teleport items have medium priority
        return 1;
    }

    /**
     * Determines if an item is a farming tool.
     * 
     * @param itemId The item ID to check
     * @return true if the item is a farming tool
     */
    private boolean isTool(int itemId) {
        return itemId == ItemID.SPADE ||
                itemId == ItemID.RAKE ||
                itemId == ItemID.DIBBER ||
                itemId == ItemID.FAIRY_ENCHANTED_SECATEURS ||
                isWateringCan(itemId);
    }

    /**
     * Determines if an item is a basalt teleport item.
     * 
     * @param itemId The item ID to check
     * @return true if the item is a basalt teleport item
     */
    private boolean isBasalt(int itemId) {
        return itemId == ItemID.STRONGHOLD_TELEPORT_BASALT ||
                itemId == ItemID.WEISS_TELEPORT_BASALT;
    }

    /**
     * Determines if an item is a consumable supply (seeds, compost) rather than a
     * teleport item.
     * Consumable supplies should appear after teleport items in the infobox
     * display.
     * 
     * @param itemId The item ID to check
     * @return true if the item is a consumable supply, false if it's a teleport
     *         item or tool
     */
    private boolean isConsumableSupply(int itemId) {
        // Check for seeds
        if (isHerbSeed(itemId) ||
                isTreeSapling(itemId) ||
                isFruitTreeSapling(itemId) ||
                isHopsSeed(itemId) ||
                isAllotmentSeed(itemId) ||
                itemId == ItemID.LIMPWURT_SEED) {
            return true;
        }

        // Check for compost
        if (itemId == ItemID.BUCKET_COMPOST ||
                itemId == ItemID.BUCKET_SUPERCOMPOST ||
                itemId == ItemID.BUCKET_ULTRACOMPOST ||
                itemId == ItemID.BOTTOMLESS_COMPOST_BUCKET ||
                BOTTOMLESS_COMPOST_BUCKET_IDS.contains(itemId)) {
            return true;
        }

        // Check for base seed IDs (used as placeholders)
        if (itemId == BASE_SEED_ID ||
                itemId == BASE_SAPLING_ID ||
                itemId == BASE_FRUIT_SAPLING_ID ||
                itemId == BASE_HOPS_SEED_ID ||
                itemId == BASE_ALLOTMENT_SEED_ID) {
            return true;
        }

        // Everything else (runes, teleport items, tools, etc.) is considered a teleport
        // item
        return false;
    }
}
