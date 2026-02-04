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

import com.easyfarming.ItemsAndLocations.HerbRunItemAndLocation;
import com.easyfarming.ItemsAndLocations.TreeRunItemAndLocation;
import com.easyfarming.ItemsAndLocations.FruitTreeRunItemAndLocation;
import com.easyfarming.ItemsAndLocations.HopsRunItemAndLocation;
import com.easyfarming.utils.Constants;

public class EasyFarmingOverlay extends Overlay {

    private HerbRunItemAndLocation herbRunItemAndLocation;
    private TreeRunItemAndLocation treeRunItemAndLocation;
    private FruitTreeRunItemAndLocation fruitTreeRunItemAndLocation;
    private HopsRunItemAndLocation hopsRunItemAndLocation;
    private final Client client;
    private final EasyFarmingPlugin plugin;
    private final PanelComponent panelComponent = new PanelComponent();
    private final ItemManager itemManager;
    private final InfoBoxManager infoBoxManager;

    // Track current InfoBoxes by item ID
    private final Map<Integer, RequiredItemInfoBox> currentInfoBoxes = new HashMap<>();

    public static final List<Integer> TELEPORT_CRYSTAL_IDS = Arrays.asList(ItemID.MOURNING_TELEPORT_CRYSTAL_1,
            ItemID.MOURNING_TELEPORT_CRYSTAL_2, ItemID.MOURNING_TELEPORT_CRYSTAL_3, ItemID.MOURNING_TELEPORT_CRYSTAL_4,
            ItemID.MOURNING_TELEPORT_CRYSTAL_5);
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

    public static final List<Integer> WATERING_CAN_IDS = Constants.WATERING_CAN_IDS;
    private static final int BASE_WATERING_CAN_ID = Constants.WATERING_CAN_IDS.get(0);

    public List<Integer> getWateringCanIds() {
        return WATERING_CAN_IDS;
    }

    public boolean isWateringCan(int itemId) {
        return WATERING_CAN_IDS.contains(itemId);
    }

    public List<Integer> getHerbPatchIds() {
        return Constants.HERB_PATCH_IDS;
    }

    public List<Integer> getHopsPatchIds() {
        return Constants.HOPS_PATCH_IDS;
    }

    private static final List<Integer> HERB_SEED_IDS = Arrays.asList(
            ItemID.GUAM_SEED, ItemID.MARRENTILL_SEED, ItemID.TARROMIN_SEED, ItemID.HARRALANDER_SEED,
            ItemID.RANARR_SEED, ItemID.TOADFLAX_SEED, ItemID.IRIT_SEED, ItemID.AVANTOE_SEED,
            ItemID.KWUARM_SEED, ItemID.SNAPDRAGON_SEED, ItemID.CADANTINE_SEED, ItemID.LANTADYME_SEED,
            ItemID.DWARF_WEED_SEED, ItemID.TORSTOL_SEED, ItemID.HUASCA_SEED);
    private static final int BASE_SEED_ID = ItemID.GUAM_SEED;
    private static final int BASE_HOPS_SEED_ID = ItemID.BARLEY_SEED;

    public List<Integer> getHerbSeedIds() {
        return HERB_SEED_IDS;
    }

    public List<Integer> getHopsSeedIds() {
        return Constants.HOPS_SEED_IDS;
    }

    private boolean isHerbSeed(int itemId) {
        return HERB_SEED_IDS.contains(itemId);
    }

    private boolean isHopsSeed(int itemId) {
        return Constants.HOPS_SEED_IDS.contains(itemId);
    }

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

    private static final List<Integer> ALLOTMENT_SEED_IDS = Arrays.asList(
            ItemID.POTATO_SEED, ItemID.ONION_SEED, ItemID.CABBAGE_SEED, ItemID.TOMATO_SEED,
            ItemID.SWEETCORN_SEED, ItemID.STRAWBERRY_SEED, ItemID.WATERMELON_SEED, ItemID.SNAPE_GRASS_SEED);
    private static final int BASE_ALLOTMENT_SEED_ID = ItemID.SNAPE_GRASS_SEED;

    public List<Integer> getAllotmentSeedIds() {
        return Constants.ALLOTMENT_SEED_IDS;
    }

    private boolean isAllotmentSeed(int itemId) {
        return Constants.isAllotmentSeed(itemId);
    }

    public List<Integer> getTreePatchIds() {
        return Constants.TREE_PATCH_IDS;
    }

    private static final List<Integer> TREE_SAPLING_IDS = Arrays.asList(ItemID.PLANTPOT_OAK_SAPLING,
            ItemID.PLANTPOT_WILLOW_SAPLING, ItemID.PLANTPOT_MAPLE_SAPLING, ItemID.PLANTPOT_YEW_SAPLING,
            ItemID.PLANTPOT_MAGIC_TREE_SAPLING);
    private static final int BASE_SAPLING_ID = ItemID.PLANTPOT_OAK_SAPLING;

    public List<Integer> getTreeSaplingIds() {
        return TREE_SAPLING_IDS;
    }

    private boolean isTreeSapling(int itemId) {
        return TREE_SAPLING_IDS.contains(itemId);
    }

    public List<Integer> getFruitTreePatchIds() {
        return Constants.FRUIT_TREE_PATCH_IDS;
    }

    private static final List<Integer> FRUIT_TREE_SAPLING_IDS = Arrays.asList(ItemID.PLANTPOT_APPLE_SAPLING,
            ItemID.PLANTPOT_BANANA_SAPLING, ItemID.PLANTPOT_ORANGE_SAPLING, ItemID.PLANTPOT_CURRY_SAPLING,
            ItemID.PLANTPOT_PINEAPPLE_SAPLING, ItemID.PLANTPOT_PAPAYA_SAPLING, ItemID.PLANTPOT_PALM_SAPLING,
            ItemID.PLANTPOT_DRAGONFRUIT_SAPLING);
    private static final int BASE_FRUIT_SAPLING_ID = ItemID.PLANTPOT_APPLE_SAPLING;

    public List<Integer> getFruitTreeSaplingIds() {
        return FRUIT_TREE_SAPLING_IDS;
    }

    private boolean isFruitTreeSapling(int itemId) {
        return FRUIT_TREE_SAPLING_IDS.contains(itemId);
    }

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
            InfoBoxManager infoBoxManager, HerbRunItemAndLocation herbRunItemAndLocation,
            TreeRunItemAndLocation treeRunItemAndLocation, FruitTreeRunItemAndLocation fruitTreeRunItemAndLocation,
            HopsRunItemAndLocation hopsRunItemAndLocation) {
        this.client = client;
        this.plugin = plugin;
        this.itemManager = itemManager;
        this.infoBoxManager = infoBoxManager;
        this.herbRunItemAndLocation = herbRunItemAndLocation;
        this.treeRunItemAndLocation = treeRunItemAndLocation;
        this.fruitTreeRunItemAndLocation = fruitTreeRunItemAndLocation;
        this.hopsRunItemAndLocation = hopsRunItemAndLocation;
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
            // List of items to check
            Map<Integer, Integer> itemsToCheck = null;
            if (plugin.getFarmingTeleportOverlay().herbRun) {
                itemsToCheck = herbRunItemAndLocation.getHerbItems();
            }
            if (plugin.getFarmingTeleportOverlay().treeRun) {
                itemsToCheck = treeRunItemAndLocation.getTreeItems();
            }
            if (plugin.getFarmingTeleportOverlay().fruitTreeRun) {
                itemsToCheck = fruitTreeRunItemAndLocation.getFruitTreeItems();
            }
            if (plugin.getFarmingTeleportOverlay().hopsRun) {
                itemsToCheck = hopsRunItemAndLocation.getHopsItems();
            }

            if (itemsToCheck == null || itemsToCheck.isEmpty()) {
                return null;
            }

            ItemContainer inventory = client.getItemContainer(InventoryID.INV);

            Item[] items;
            if (inventory == null || inventory.getItems() == null) {
                items = new Item[0];
            } else {
                items = inventory.getItems();
            }

            // Build expanded rune map once before any requirement checks
            Map<Integer, Integer> expandedRuneMap = buildExpandedRuneMap(items);

            int teleportCrystalCount = 0;
            for (Item item : items) {
                if (isTeleportCrystal(item.getId())) {
                    teleportCrystalCount += item.getQuantity();
                    break;
                }
            }
            int skillsNecklaceCharges = 0;
            // Count charges from inventory
            for (Item item : items) {
                if (isSkillsNecklace(item.getId())) {
                    int charges = getSkillsNecklaceCharges(item.getId());
                    skillsNecklaceCharges += charges * item.getQuantity();
                }
            }
            // Count charges from equipped items
            Map<Integer, Integer> equippedItems = getEquippedItems();
            for (Map.Entry<Integer, Integer> equippedEntry : equippedItems.entrySet()) {
                int equippedItemId = equippedEntry.getKey();
                if (isSkillsNecklace(equippedItemId)) {
                    int charges = getSkillsNecklaceCharges(equippedItemId);
                    skillsNecklaceCharges += charges;
                }
            }
            int quetzalWhistleCount = 0;
            for (Item item : items) {
                if (isQuetzalWhistle(item.getId())) {
                    quetzalWhistleCount += item.getQuantity();
                }
            }

            int totalSeeds = 0;
            int totalAllotmentSeeds = 0;
            if (plugin.getFarmingTeleportOverlay().herbRun) {
                for (Item item : items) {
                    if (isHerbSeed(item.getId())) {
                        totalSeeds += item.getQuantity();
                    }
                    if (isAllotmentSeed(item.getId())) {
                        totalAllotmentSeeds += item.getQuantity();
                    }
                }
            }
            if (plugin.getFarmingTeleportOverlay().treeRun) {
                for (Item item : items) {
                    if (isTreeSapling(item.getId())) {
                        totalSeeds += item.getQuantity();
                    }
                }
            }
            if (plugin.getFarmingTeleportOverlay().fruitTreeRun) {
                for (Item item : items) {
                    if (isFruitTreeSapling(item.getId())) {
                        totalSeeds += item.getQuantity();
                    }
                }
            }
            if (plugin.getFarmingTeleportOverlay().hopsRun) {
                for (Item item : items) {
                    if (isHopsSeed(item.getId())) {
                        totalSeeds += item.getQuantity();
                    }
                }
            }

            panelComponent.getChildren().clear();

            // Single inventory scan to build comprehensive item count map (including rune
            // pouch expansions)
            Map<Integer, Integer> inventoryItemCounts = new HashMap<>();
            boolean hasRunePouch = false;

            // First pass: scan inventory for regular items and check for rune pouch
            for (Item item : items) {
                if (item != null) {
                    int itemId = item.getId();
                    int itemQuantity = item.getQuantity();

                    // Check if this is a rune pouch
                    if (RUNE_POUCH_ID.contains(itemId)) {
                        hasRunePouch = true;
                    }

                    if (COMBINATION_RUNE_SUBRUNES_MAP.containsKey(itemId)) {
                        // Handle combination runes
                        List<Integer> subRunes = COMBINATION_RUNE_SUBRUNES_MAP.get(itemId);
                        for (int subRune : subRunes) {
                            inventoryItemCounts.put(subRune,
                                    inventoryItemCounts.getOrDefault(subRune, 0) + itemQuantity);
                        }
                    } else if (STAFF_RUNES_MAP.containsKey(itemId)) {
                        // Handle staffs - add their runes with large amount
                        List<Integer> staffRunes = STAFF_RUNES_MAP.get(itemId);
                        for (int rune : staffRunes) {
                            // Use max to ensure we always have enough, but cap at STAFF_RUNE_AMOUNT to
                            // avoid overflow
                            inventoryItemCounts.put(rune,
                                    Math.max(inventoryItemCounts.getOrDefault(rune, 0), STAFF_RUNE_AMOUNT));
                        }
                    } else {
                        // Handle regular items - sum quantities across slots (e.g. multiple compost)
                        inventoryItemCounts.put(itemId, inventoryItemCounts.getOrDefault(itemId, 0) + itemQuantity);
                    }
                }
            }

            // Second pass: if rune pouch exists, add expanded rune map contents to
            // inventory counts
            if (hasRunePouch) {
                for (Map.Entry<Integer, Integer> runeEntry : expandedRuneMap.entrySet()) {
                    int runeId = runeEntry.getKey();
                    int runeCount = runeEntry.getValue();
                    inventoryItemCounts.put(runeId, inventoryItemCounts.getOrDefault(runeId, 0) + runeCount);
                }
            }

            // Third pass: add equipped items to inventory counts
            // Note: equippedItems was already retrieved above for skills necklace charges
            for (Map.Entry<Integer, Integer> equippedEntry : equippedItems.entrySet()) {
                int equippedItemId = equippedEntry.getKey();
                int equippedCount = equippedEntry.getValue();

                // Check if equipped item is a staff
                if (STAFF_RUNES_MAP.containsKey(equippedItemId)) {
                    // Handle staffs - add their runes with large amount
                    List<Integer> staffRunes = STAFF_RUNES_MAP.get(equippedItemId);
                    for (int rune : staffRunes) {
                        // Use max to ensure we always have enough, but cap at STAFF_RUNE_AMOUNT to
                        // avoid overflow
                        inventoryItemCounts.put(rune,
                                Math.max(inventoryItemCounts.getOrDefault(rune, 0), STAFF_RUNE_AMOUNT));
                    }
                } else {
                    // Handle regular equipped items
                    inventoryItemCounts.put(equippedItemId,
                            inventoryItemCounts.getOrDefault(equippedItemId, 0) + equippedCount);
                }
            }

            List<AbstractMap.SimpleEntry<Integer, Integer>> missingItemsWithCounts = new ArrayList<>();
            boolean allItemsCollected = true;
            for (Map.Entry<Integer, Integer> entry : itemsToCheck.entrySet()) {
                int itemId = entry.getKey();
                int count = entry.getValue();

                // Start with inventory count from single scan
                int inventoryCount = inventoryItemCounts.getOrDefault(itemId, 0);

                // Special handling for bottomless compost bucket - check for filled variants in
                // inventory
                if (itemId == ItemID.BOTTOMLESS_COMPOST_BUCKET) {
                    // Check inventory for any bottomless bucket variant (empty or filled)
                    for (Item item : items) {
                        if (item != null && BOTTOMLESS_COMPOST_BUCKET_IDS.contains(item.getId())) {
                            inventoryCount = 1;
                            break;
                        }
                    }
                }

                // Add tool lep count
                int toolLepCount = checkToolLep(itemId);
                if (toolLepCount > 0) {
                    inventoryCount += toolLepCount;
                }

                // Apply run-specific and item-specific overrides in order
                if (plugin.getFarmingTeleportOverlay().herbRun && itemId == BASE_SEED_ID) {
                    inventoryCount = totalSeeds;
                } else if (plugin.getFarmingTeleportOverlay().herbRun && itemId == BASE_ALLOTMENT_SEED_ID) {
                    inventoryCount = totalAllotmentSeeds;
                } else if (plugin.getFarmingTeleportOverlay().treeRun && itemId == BASE_SAPLING_ID) {
                    inventoryCount = totalSeeds;
                } else if (plugin.getFarmingTeleportOverlay().fruitTreeRun && itemId == BASE_FRUIT_SAPLING_ID) {
                    inventoryCount = totalSeeds;
                } else if (plugin.getFarmingTeleportOverlay().hopsRun && itemId == BASE_HOPS_SEED_ID) {
                    inventoryCount = totalSeeds;
                } else if (itemId == BASE_TELEPORT_CRYSTAL_ID) {
                    inventoryCount = teleportCrystalCount;
                } else if (itemId == BASE_SKILLS_NECKLACE_ID) {
                    // Skills necklace requirement is in charges, not number of items
                    inventoryCount = skillsNecklaceCharges;
                } else if (itemId == ItemID.HG_QUETZALWHISTLE_BASIC) {
                    inventoryCount = quetzalWhistleCount;
                } else if (itemId == BASE_EXPLORERS_RING_ID) {
                    // Check if any Explorer's Ring variant is equipped or in inventory
                    // First check inventoryItemCounts (includes equipped items from third pass)
                    // Then also directly check equippedItems as a fallback to ensure detection
                    boolean hasExplorersRing = false;
                    for (int ringId : EXPLORERS_RING_IDS) {
                        // Check in inventoryItemCounts first
                        if (inventoryItemCounts.containsKey(ringId) && inventoryItemCounts.get(ringId) > 0) {
                            hasExplorersRing = true;
                            break;
                        }
                        // Also directly check equipped items as fallback
                        if (equippedItems.containsKey(ringId) && equippedItems.get(ringId) > 0) {
                            hasExplorersRing = true;
                            break;
                        }
                    }
                    inventoryCount = hasExplorersRing ? 1 : 0;
                } else if (itemId == BASE_ARDY_CLOAK_ID) {
                    // Check if any Ardougne Cloak variant is equipped or in inventory
                    // inventoryItemCounts already includes equipped items from the third pass
                    boolean hasArdyCloak = false;
                    for (int cloakId : ARDY_CLOAK_IDS) {
                        if (inventoryItemCounts.containsKey(cloakId) && inventoryItemCounts.get(cloakId) > 0) {
                            hasArdyCloak = true;
                            break;
                        }
                    }
                    inventoryCount = hasArdyCloak ? 1 : 0;
                } else if (itemId == BASE_WATERING_CAN_ID) {
                    // Check if any watering can variant is equipped or in inventory
                    // inventoryItemCounts already includes equipped items from the third pass
                    boolean hasWateringCan = false;
                    for (int canId : WATERING_CAN_IDS) {
                        if (inventoryItemCounts.containsKey(canId) && inventoryItemCounts.get(canId) > 0) {
                            hasWateringCan = true;
                            break;
                        }
                    }
                    inventoryCount = hasWateringCan ? 1 : 0;
                }

                // Rune pouch contents are already included in inventoryItemCounts

                if (inventoryCount < count) {
                    allItemsCollected = false;
                    int missingCount = count - inventoryCount;
                    BufferedImage itemImage = itemManager.getImage(itemId);
                    if (itemImage != null) {
                        // Add the missing item and count to the list
                        missingItemsWithCounts.add(new AbstractMap.SimpleEntry<>(itemId, missingCount));
                    }
                }
            }

            // Sort missing items: tools first, then teleport items (basalts last among
            // teleports), then consumable supplies
            missingItemsWithCounts.sort((entry1, entry2) -> {
                int itemId1 = entry1.getKey();
                int itemId2 = entry2.getKey();

                // Get priority: 0 = tools, 1 = regular teleport, 2 = basalt, 3 = consumable
                int priority1 = getItemPriority(itemId1);
                int priority2 = getItemPriority(itemId2);

                // Compare priorities
                return Integer.compare(priority1, priority2);
            });

            plugin.setTeleportOverlayActive(allItemsCollected);

            // Update InfoBoxes - remove ones that are no longer needed, add/update ones
            // that are
            Set<Integer> currentMissingItemIds = new HashSet<>();
            for (AbstractMap.SimpleEntry<Integer, Integer> pair : missingItemsWithCounts) {
                currentMissingItemIds.add(pair.getKey());
            }

            // Remove InfoBoxes for items that are no longer missing
            if (infoBoxManager != null) {
                Iterator<Map.Entry<Integer, RequiredItemInfoBox>> iterator = currentInfoBoxes.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Integer, RequiredItemInfoBox> entry = iterator.next();
                    int itemId = entry.getKey();
                    if (!currentMissingItemIds.contains(itemId)) {
                        infoBoxManager.removeInfoBox(entry.getValue());
                        iterator.remove();
                    }
                }
            }

            // Add or update InfoBoxes for missing items
            if (infoBoxManager != null) {
                for (AbstractMap.SimpleEntry<Integer, Integer> pair : missingItemsWithCounts) {
                    int itemId = pair.getKey();
                    int missingCount = pair.getValue();

                    BufferedImage itemImage = itemManager.getImage(itemId);
                    if (itemImage != null) {
                        RequiredItemInfoBox existingInfoBox = currentInfoBoxes.get(itemId);
                        if (existingInfoBox != null) {
                            // Update existing InfoBox if count changed
                            if (existingInfoBox.getMissingCount() != missingCount) {
                                infoBoxManager.removeInfoBox(existingInfoBox);
                                RequiredItemInfoBox newInfoBox = new RequiredItemInfoBox(itemImage, plugin, itemId,
                                        missingCount);
                                infoBoxManager.addInfoBox(newInfoBox);
                                currentInfoBoxes.put(itemId, newInfoBox);
                            }
                        } else {
                            // Create new InfoBox
                            RequiredItemInfoBox infoBox = new RequiredItemInfoBox(itemImage, plugin, itemId,
                                    missingCount);
                            infoBoxManager.addInfoBox(infoBox);
                            currentInfoBoxes.put(itemId, infoBox);
                        }
                    }
                }
            }

            // Check if all items have been collected
            if (missingItemsWithCounts.isEmpty()) {
                plugin.setItemsCollected(true);
            } else {
                plugin.setItemsCollected(false);
            }

            // Render panel (for any other content if needed)
            return panelComponent.render(graphics);
        }

        // If items are collected, clear all InfoBoxes
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