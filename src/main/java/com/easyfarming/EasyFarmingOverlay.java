package com.easyfarming;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.*;
import javax.inject.Inject;

import net.runelite.api.*;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import java.awt.image.BufferedImage;
import net.runelite.client.game.ItemManager;

import java.awt.Color;
import com.easyfarming.ItemsAndLocations.HerbRunItemAndLocation;
import com.easyfarming.ItemsAndLocations.TreeRunItemAndLocation;
import com.easyfarming.ItemsAndLocations.FruitTreeRunItemAndLocation;

public class EasyFarmingOverlay extends Overlay {

    private HerbRunItemAndLocation herbRunItemAndLocation;
    private TreeRunItemAndLocation treeRunItemAndLocation;
    private FruitTreeRunItemAndLocation fruitTreeRunItemAndLocation;
    private final Client client;
    private final EasyFarmingPlugin plugin;
    private final PanelComponent panelComponent = new PanelComponent();
    @Inject
    private ItemManager itemManager;

    public static final List<Integer> TELEPORT_CRYSTAL_IDS = Arrays.asList(ItemID.MOURNING_TELEPORT_CRYSTAL_1, ItemID.MOURNING_TELEPORT_CRYSTAL_2, ItemID.MOURNING_TELEPORT_CRYSTAL_3, ItemID.MOURNING_TELEPORT_CRYSTAL_4, ItemID.MOURNING_TELEPORT_CRYSTAL_5);
    private static final int BASE_TELEPORT_CRYSTAL_ID = ItemID.MOURNING_TELEPORT_CRYSTAL_1;
    public List<Integer> getTeleportCrystalIds() {
        return TELEPORT_CRYSTAL_IDS;
    }    public boolean isTeleportCrystal(int itemId) {
        return TELEPORT_CRYSTAL_IDS.contains(itemId);
    }

    public static final List<Integer> SKILLS_NECKLACE_IDS = Arrays.asList(ItemID.JEWL_NECKLACE_OF_SKILLS_1, ItemID.JEWL_NECKLACE_OF_SKILLS_2, ItemID.JEWL_NECKLACE_OF_SKILLS_3, ItemID.JEWL_NECKLACE_OF_SKILLS_4, ItemID.JEWL_NECKLACE_OF_SKILLS_5, ItemID.JEWL_NECKLACE_OF_SKILLS_6);
    private static final int BASE_SKILLS_NECKLACE_ID = ItemID.JEWL_NECKLACE_OF_SKILLS_1;
    public List<Integer> getSkillsNecklaceIds() {
        return SKILLS_NECKLACE_IDS;
    }    public boolean isSkillsNecklace(int itemId) {
        return SKILLS_NECKLACE_IDS.contains(itemId);
    }


    public static final List<Integer> HERB_PATCH_IDS = Arrays.asList(33176, 27115, 8152, 8150, 8153, 18816, 8151, 9372, 33979 );
    public List<Integer> getHerbPatchIds() {
        return HERB_PATCH_IDS;
    }
    private static final List<Integer> HERB_SEED_IDS = Arrays.asList(
        ItemID.GUAM_SEED, ItemID.MARRENTILL_SEED, ItemID.TARROMIN_SEED, ItemID.HARRALANDER_SEED,
        ItemID.RANARR_SEED, ItemID.TOADFLAX_SEED, ItemID.IRIT_SEED, ItemID.AVANTOE_SEED,
        ItemID.KWUARM_SEED, ItemID.SNAPDRAGON_SEED, ItemID.CADANTINE_SEED, ItemID.LANTADYME_SEED,
        ItemID.DWARF_WEED_SEED, ItemID.TORSTOL_SEED, ItemID.HUASCA_SEED
    );
    private static final int BASE_SEED_ID = ItemID.GUAM_SEED;
    public List<Integer> getHerbSeedIds() {
        return HERB_SEED_IDS;
    }
    private boolean isHerbSeed(int itemId) {
        return HERB_SEED_IDS.contains(itemId);
    }



    private static final List<Integer> FLOWER_PATCH_IDS = Arrays.asList(27111, 7849, 7847, 7850, 7848, 33649);
    public List<Integer> getFlowerPatchIds() {
        return FLOWER_PATCH_IDS;
    }


    public static final List<Integer> TREE_PATCH_IDS = Arrays.asList(8389, 33732, 19147, 8391, 8388, 8390);
    public List<Integer> getTreePatchIds() {
        return TREE_PATCH_IDS;
    }
    private static final List<Integer> TREE_SAPLING_IDS = Arrays.asList(ItemID.PLANTPOT_OAK_SAPLING, ItemID.PLANTPOT_WILLOW_SAPLING,ItemID.PLANTPOT_MAPLE_SAPLING,ItemID.PLANTPOT_YEW_SAPLING,ItemID.PLANTPOT_MAGIC_TREE_SAPLING);
    private static final int BASE_SAPLING_ID = ItemID.PLANTPOT_OAK_SAPLING;
    public List<Integer> getTreeSaplingIds() {
        return TREE_SAPLING_IDS;
    }
    private boolean isTreeSapling(int itemId) {return TREE_SAPLING_IDS.contains(itemId);}


    public static final List<Integer> FRUIT_TREE_PATCH_IDS = Arrays.asList(7964, 7965, 34007, 7962, 26579, 7963);
    public List<Integer> getFruitTreePatchIds() {
        return FRUIT_TREE_PATCH_IDS;
    }
    private static final List<Integer> FRUIT_TREE_SAPLING_IDS = Arrays.asList(ItemID.PLANTPOT_APPLE_SAPLING, ItemID.PLANTPOT_BANANA_SAPLING,ItemID.PLANTPOT_ORANGE_SAPLING,ItemID.PLANTPOT_CURRY_SAPLING,ItemID.PLANTPOT_PINEAPPLE_SAPLING,ItemID.PLANTPOT_PAPAYA_SAPLING,ItemID.PLANTPOT_PALM_SAPLING, ItemID.PLANTPOT_DRAGONFRUIT_SAPLING);
    private static final int BASE_FRUIT_SAPLING_ID = ItemID.PLANTPOT_APPLE_SAPLING;
    public List<Integer> getFruitTreeSaplingIds() {return FRUIT_TREE_SAPLING_IDS;}
    private boolean isFruitTreeSapling(int itemId) {return FRUIT_TREE_SAPLING_IDS.contains(itemId);}


    public static final List<Integer> RUNE_POUCH_ID = Arrays.asList(ItemID.BH_RUNE_POUCH, ItemID.DIVINE_RUNE_POUCH);

    public static final List<Integer> RUNE_POUCH_AMOUNT_VARBITS = Arrays.asList(VarbitID.RUNE_POUCH_QUANTITY_1, VarbitID.RUNE_POUCH_QUANTITY_2, VarbitID.RUNE_POUCH_QUANTITY_3, VarbitID.RUNE_POUCH_QUANTITY_4);

    public static final List<Integer> RUNE_POUCH_RUNE_VARBITS = Arrays.asList(VarbitID.RUNE_POUCH_TYPE_1, VarbitID.RUNE_POUCH_TYPE_2, VarbitID.RUNE_POUCH_TYPE_3, VarbitID.RUNE_POUCH_TYPE_4);

    private static final Map<Integer, List<Integer>> COMBINATION_RUNE_SUBRUNES_MAP;

    static {
        Map<Integer, List<Integer>> tempMap = new HashMap<>();
        tempMap.put(ItemID.DUSTRUNE, Arrays.asList(ItemID.AIRRUNE, ItemID.EARTHRUNE));
        tempMap.put(ItemID.MISTRUNE, Arrays.asList(ItemID.AIRRUNE, ItemID.WATERRUNE));
        tempMap.put(ItemID.MUDRUNE, Arrays.asList(ItemID.WATERRUNE, ItemID.EARTHRUNE));
        tempMap.put(ItemID.LAVARUNE, Arrays.asList(ItemID.FIRERUNE, ItemID.EARTHRUNE));
        tempMap.put(ItemID.STEAMRUNE, Arrays.asList(ItemID.FIRERUNE, ItemID.WATERRUNE));
        tempMap.put(ItemID.SMOKERUNE, Arrays.asList(ItemID.FIRERUNE, ItemID.AIRRUNE));
        COMBINATION_RUNE_SUBRUNES_MAP = Collections.unmodifiableMap(tempMap);
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
                } else {
                    // Add regular runes from inventory
                    expandedRuneMap.put(itemIdRune, expandedRuneMap.getOrDefault(itemIdRune, 0) + itemQuantity);
                }
            }
        }
        
        return expandedRuneMap;
    }

    @Inject
    public EasyFarmingOverlay(Client client, EasyFarmingPlugin plugin, ItemManager itemManager, HerbRunItemAndLocation herbRunItemAndLocation, TreeRunItemAndLocation treeRunItemAndLocation, FruitTreeRunItemAndLocation fruitTreeRunItemAndLocation) {
        this.client = client;
        this.plugin = plugin;
        this.itemManager = itemManager;
        this.herbRunItemAndLocation = herbRunItemAndLocation;
        this.treeRunItemAndLocation = treeRunItemAndLocation;
        this.fruitTreeRunItemAndLocation = fruitTreeRunItemAndLocation;
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
        if(item == ItemID.BUCKET_COMPOST) {
            return client.getVarbitValue(1442);
        }
        if(item == ItemID.BUCKET_SUPERCOMPOST) {
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

    public Map<Integer, Integer> itemsToCheck;
    @Override
    public Dimension render(Graphics2D graphics) {
        if (plugin.isOverlayActive() && !plugin.areItemsCollected()) {
            if (!plugin.isOverlayActive()) {
                return null;
            }
            plugin.addTextToInfoBox("Grab all the items needed");
            // List of items to check
            Map<Integer, Integer> itemsToCheck = null;
            if(plugin.getFarmingTeleportOverlay().herbRun) {
                itemsToCheck = herbRunItemAndLocation.getHerbItems();
            }
            if(plugin.getFarmingTeleportOverlay().treeRun) {
                itemsToCheck = treeRunItemAndLocation.getTreeItems();
            }
            if(plugin.getFarmingTeleportOverlay().fruitTreeRun) {
                itemsToCheck = fruitTreeRunItemAndLocation.getFruitTreeItems();
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
            int skillsNecklaceCount = 0;
            for (Item item : items) {
                if (isSkillsNecklace(item.getId())) {
                    skillsNecklaceCount += item.getQuantity();
                    break;
                }
            }

            int totalSeeds = 0;
            if(plugin.getFarmingTeleportOverlay().herbRun) {
                for (Item item : items) {
                    if (isHerbSeed(item.getId())) {
                        totalSeeds += item.getQuantity();
                    }
                }
            }
            if(plugin.getFarmingTeleportOverlay().treeRun) {
                for (Item item : items) {
                    if (isTreeSapling(item.getId())) {
                        totalSeeds += item.getQuantity();
                    }
                }
            }
            if(plugin.getFarmingTeleportOverlay().fruitTreeRun) {
                for (Item item : items) {
                    if (isFruitTreeSapling(item.getId())) {
                        totalSeeds += item.getQuantity();
                    }
                }
            }

            panelComponent.getChildren().clear();
            int yOffset = 0;

            // Single inventory scan to build item count map
            Map<Integer, Integer> inventoryItemCounts = new HashMap<>();
            for (Item item : items) {
                if (item != null) {
                    int itemId = item.getId();
                    int itemQuantity = item.getQuantity();
                    
                    if (COMBINATION_RUNE_SUBRUNES_MAP.containsKey(itemId)) {
                        // Handle combination runes
                        List<Integer> subRunes = COMBINATION_RUNE_SUBRUNES_MAP.get(itemId);
                        for (int subRune : subRunes) {
                            inventoryItemCounts.put(subRune, inventoryItemCounts.getOrDefault(subRune, 0) + itemQuantity);
                        }
                    } else {
                        // Handle regular items
                        inventoryItemCounts.put(itemId, itemQuantity);
                    }
                }
            }

            List<AbstractMap.SimpleEntry<Integer, Integer>> missingItemsWithCounts = new ArrayList<>();
            boolean allItemsCollected = true;
            for (Map.Entry<Integer, Integer> entry : itemsToCheck.entrySet()) {
                int itemId = entry.getKey();
                int count = entry.getValue();

                // Start with inventory count from single scan
                int inventoryCount = inventoryItemCounts.getOrDefault(itemId, 0);
                
                // Add tool lep count
                int toolLepCount = checkToolLep(itemId);
                if (toolLepCount > 0) {
                    inventoryCount += toolLepCount;
                }
                
                // Apply run-specific and item-specific overrides in order
                if (plugin.getFarmingTeleportOverlay().herbRun && itemId == BASE_SEED_ID) {
                    inventoryCount = totalSeeds;
                } else if (plugin.getFarmingTeleportOverlay().treeRun && itemId == BASE_SAPLING_ID) {
                    inventoryCount = totalSeeds;
                } else if (plugin.getFarmingTeleportOverlay().fruitTreeRun && itemId == BASE_FRUIT_SAPLING_ID) {
                    inventoryCount = totalSeeds;
                } else if (itemId == BASE_TELEPORT_CRYSTAL_ID) {
                    inventoryCount = teleportCrystalCount;
                } else if (itemId == BASE_SKILLS_NECKLACE_ID) {
                    inventoryCount = skillsNecklaceCount;
                }

                for (Item item: items) {
                    if (item != null && RUNE_POUCH_ID.contains(item.getId())) {
                        if (expandedRuneMap.containsKey(itemId)) {
                            inventoryCount += expandedRuneMap.get(itemId);
                        }
                    }
                }

                if (inventoryCount < count) {
                    allItemsCollected = false;
                    int missingCount = count - inventoryCount;
                    BufferedImage itemImage = itemManager.getImage(itemId);
                    if (itemImage != null) {
                        ImageComponent imageComponent = new ImageComponent(itemImage);
                        panelComponent.getChildren().add(imageComponent);

                        // Add the missing item and count to the list
                        missingItemsWithCounts.add(new AbstractMap.SimpleEntry<>(itemId, missingCount));

                        yOffset += itemImage.getHeight() + 2; // Update yOffset for the next item
                    }
                }
            }
            plugin.setTeleportOverlayActive(allItemsCollected);
            Dimension panelSize = panelComponent.render(graphics);

            // Draw item count on top of the overlay
            yOffset = 0;
            for (AbstractMap.SimpleEntry<Integer, Integer> pair : missingItemsWithCounts) {
                int itemId = pair.getKey();
                int missingCount = pair.getValue();

                BufferedImage itemImage = itemManager.getImage(itemId);
                if (itemImage != null) {
                    // Draw item count
                    if (missingCount > 1) {
                        String countText = Integer.toString(missingCount);
                        int textX = 2; // Calculate X position for the count text
                        int textY = yOffset + 15; // Calculate Y position for the count text
                        graphics.setColor(Color.WHITE);
                        graphics.drawString(countText, textX, textY);
                    }

                    yOffset += itemImage.getHeight() + 2; // Update yOffset for the next item
                }
            }
            // Check if all items have been collected
            if (missingItemsWithCounts.isEmpty()) {
                plugin.setItemsCollected(true);
            } else {
                plugin.setItemsCollected(false);
            }

            return panelSize;
        }
        return null;
    }
}