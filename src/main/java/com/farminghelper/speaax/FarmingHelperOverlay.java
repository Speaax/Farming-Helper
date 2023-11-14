package com.farminghelper.speaax;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.*;
import javax.inject.Inject;

import net.runelite.api.*;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import java.awt.image.BufferedImage;
import net.runelite.client.game.ItemManager;

import java.awt.Color;
import com.farminghelper.speaax.ItemsAndLocations.HerbRunItemAndLocation;
import com.farminghelper.speaax.ItemsAndLocations.TreeRunItemAndLocation;
import com.farminghelper.speaax.ItemsAndLocations.FruitTreeRunItemAndLocation;
import com.farminghelper.speaax.ItemsAndLocations.HardwoodRunItemAndLocation;

public class FarmingHelperOverlay extends Overlay {

    private HerbRunItemAndLocation herbRunItemAndLocation;
    private TreeRunItemAndLocation treeRunItemAndLocation;
    private FruitTreeRunItemAndLocation fruitTreeRunItemAndLocation;
    private HardwoodRunItemAndLocation hardwoodRunItemAndLocation;
    private final Client client;
    private final FarmingHelperPlugin plugin;
    private final PanelComponent panelComponent = new PanelComponent();
    @Inject
    private ItemManager itemManager;

    public static final List<Integer> TELEPORT_CRYSTAL_IDS = Arrays.asList(ItemID.TELEPORT_CRYSTAL_1, ItemID.TELEPORT_CRYSTAL_2, ItemID.TELEPORT_CRYSTAL_3, ItemID.TELEPORT_CRYSTAL_4, ItemID.TELEPORT_CRYSTAL_5);
    private static final int BASE_TELEPORT_CRYSTAL_ID = ItemID.TELEPORT_CRYSTAL_1;
    public List<Integer> getTeleportCrystalIdsIds() {
        return TELEPORT_CRYSTAL_IDS;
    }
    private boolean isTeleportCrystal(int itemId) {
        return TELEPORT_CRYSTAL_IDS.contains(itemId);
    }

    public static final List<Integer> SKILLS_NECKLACE_IDS = Arrays.asList(ItemID.SKILLS_NECKLACE1, ItemID.SKILLS_NECKLACE2, ItemID.SKILLS_NECKLACE3, ItemID.SKILLS_NECKLACE4, ItemID.SKILLS_NECKLACE5, ItemID.SKILLS_NECKLACE6);
    private static final int BASE_SKILLS_NECKLACE_ID = ItemID.SKILLS_NECKLACE1;
    public List<Integer> getSkillsNecklaceIdsIds() {
        return SKILLS_NECKLACE_IDS;
    }
    private boolean isSkillsNecklace(int itemId) {
        return SKILLS_NECKLACE_IDS.contains(itemId);
    }

    public static final List<Integer> DIGSITE_PENDANT_IDS = Arrays.asList(
        ItemID.DIGSITE_PENDANT_1,
        ItemID.DIGSITE_PENDANT_2,
        ItemID.DIGSITE_PENDANT_3,
        ItemID.DIGSITE_PENDANT_4,
        ItemID.DIGSITE_PENDANT_5
    );

    private static final int BASE_DIGSITE_PENDANT_ID = ItemID.DIGSITE_PENDANT_1;

    private boolean isDigsitePendant(int itemId) {
        return DIGSITE_PENDANT_IDS.contains(itemId);
    }


    public static final List<Integer> HERB_PATCH_IDS = Arrays.asList(33176, 27115, 8152, 8150, 8153, 18816, 8151, 9372, 33979 );
    public List<Integer> getHerbPatchIds() {
        return HERB_PATCH_IDS;
    }
    private static final List<Integer> HERB_SEED_IDS = Arrays.asList(5291, 5292, 5293, 5294, 5295, 5296, 5297, 5298, 5299, 5300, 5301, 5302, 5303, 5304  );
    private static final int BASE_SEED_ID = 5291;
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


    public static final List<Integer> TREE_PATCH_IDS = Arrays.asList(
        8389, 33732, 19147, 8391, 8388, 8390, 30482, 30480, 30481
    );
    public List<Integer> getTreePatchIds()
    {
        return TREE_PATCH_IDS;
    }
    private static final List<Integer> TREE_SAPLING_IDS = Arrays.asList(
        ItemID.OAK_SAPLING,
        ItemID.WILLOW_SAPLING,
        ItemID.MAPLE_SAPLING,
        ItemID.YEW_SAPLING,
        ItemID.MAGIC_SAPLING,
        ItemID.TEAK_SAPLING,
        ItemID.MAHOGANY_SAPLING
    );
    private static final int BASE_SAPLING_ID = ItemID.OAK_SAPLING;
    public List<Integer> getTreeSaplingIds()
    {
        return TREE_SAPLING_IDS;
    }
    private boolean isTreeSapling(int itemId)
    {
        return TREE_SAPLING_IDS.contains(itemId);
    }


    public static final List<Integer> FRUIT_TREE_PATCH_IDS = Arrays.asList(7964, 7965, 34007, 7962, 26579, 7963);
    public List<Integer> getFruitTreePatchIds() {
        return FRUIT_TREE_PATCH_IDS;
    }
    private static final List<Integer> FRUIT_TREE_SAPLING_IDS = Arrays.asList(ItemID.APPLE_SAPLING, ItemID.BANANA_SAPLING,ItemID.ORANGE_SAPLING,ItemID.CURRY_SAPLING,ItemID.PINEAPPLE_SAPLING,ItemID.PAPAYA_SAPLING,ItemID.PALM_SAPLING, ItemID.DRAGONFRUIT_SAPLING);
    private static final int BASE_FRUIT_SAPLING_ID = ItemID.APPLE_SAPLING;
    public List<Integer> getFruitTreeSaplingIds() {return FRUIT_TREE_SAPLING_IDS;}
    private boolean isFruitTreeSapling(int itemId) {return FRUIT_TREE_SAPLING_IDS.contains(itemId);}


    public static final List<Integer> HARDWOOD_PATCH_IDS = Arrays.asList(30480, 30481, 30482);
    public List<Integer> getHardwoodPatchIds() {
        return HARDWOOD_PATCH_IDS;
    }
    private static final List<Integer> HARDWOOD_SAPLING_IDS = Arrays.asList(ItemID.TEAK_SAPLING, ItemID.MAHOGANY_SAPLING);
    private static final int BASE_HARDWOOD_SAPLING_ID = ItemID.TEAK_SAPLING;
    public List<Integer> getHardwoodSaplingIds() {return HARDWOOD_SAPLING_IDS;}
    private boolean isHardwoodSapling(int itemId) {return HARDWOOD_SAPLING_IDS.contains(itemId);}


    public static final List<Integer> RUNE_POUCH_ID = Arrays.asList(ItemID.RUNE_POUCH, ItemID.DIVINE_RUNE_POUCH);
    public static final List<Integer> RUNE_POUCH_AMOUNT_VARBITS = Arrays.asList(Varbits.RUNE_POUCH_AMOUNT1, Varbits.RUNE_POUCH_AMOUNT2, Varbits.RUNE_POUCH_AMOUNT3, Varbits.RUNE_POUCH_AMOUNT4);

    public static final List<Integer> RUNE_POUCH_RUNE_VARBITS = Arrays.asList(Varbits.RUNE_POUCH_RUNE1, Varbits.RUNE_POUCH_RUNE2, Varbits.RUNE_POUCH_RUNE3, Varbits.RUNE_POUCH_RUNE4);

    private static final Map<Integer, List<Integer>> COMBINATION_RUNE_SUBRUNES_MAP;

    static {
        Map<Integer, List<Integer>> tempMap = new HashMap<>();
        tempMap.put(ItemID.DUST_RUNE, Arrays.asList(ItemID.AIR_RUNE, ItemID.EARTH_RUNE));
        tempMap.put(ItemID.MIST_RUNE, Arrays.asList(ItemID.AIR_RUNE, ItemID.WATER_RUNE));
        tempMap.put(ItemID.MUD_RUNE, Arrays.asList(ItemID.WATER_RUNE, ItemID.EARTH_RUNE));
        tempMap.put(ItemID.LAVA_RUNE, Arrays.asList(ItemID.FIRE_RUNE, ItemID.EARTH_RUNE));
        tempMap.put(ItemID.STEAM_RUNE, Arrays.asList(ItemID.FIRE_RUNE, ItemID.WATER_RUNE));
        tempMap.put(ItemID.SMOKE_RUNE, Arrays.asList(ItemID.FIRE_RUNE, ItemID.AIR_RUNE));
        COMBINATION_RUNE_SUBRUNES_MAP = Collections.unmodifiableMap(tempMap);
    }

    private int getRuneItemIdFromVarbitValue(int varbitValue) {
        switch (varbitValue) {
            case 1:
                return ItemID.AIR_RUNE;
            case 2:
                return ItemID.WATER_RUNE;
            case 3:
                return ItemID.EARTH_RUNE;
            case 4:
                return ItemID.FIRE_RUNE;
            case 5:
                return ItemID.MIND_RUNE;
            case 6:
                return ItemID.CHAOS_RUNE;
            case 7:
                return ItemID.DEATH_RUNE;
            case 8:
                return ItemID.BLOOD_RUNE;
            case 9:
                return ItemID.COSMIC_RUNE;
            case 10:
                return ItemID.NATURE_RUNE;
            case 11:
                return ItemID.LAW_RUNE;
            case 12:
                return ItemID.BODY_RUNE;
            case 13:
                return ItemID.SOUL_RUNE;
            case 14:
                return ItemID.ASTRAL_RUNE;
            case 15:
                return ItemID.MIST_RUNE;
            case 16:
                return ItemID.MUD_RUNE;
            case 17:
                return ItemID.DUST_RUNE;
            case 18:
                return ItemID.LAVA_RUNE;
            case 19:
                return ItemID.STEAM_RUNE;
            case 20:
                return ItemID.SMOKE_RUNE;
            case 21:
                return ItemID.WRATH_RUNE;
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

    @Inject
    public FarmingHelperOverlay(Client client, FarmingHelperPlugin plugin, ItemManager itemManager, HerbRunItemAndLocation herbRunItemAndLocation, TreeRunItemAndLocation treeRunItemAndLocation, FruitTreeRunItemAndLocation fruitTreeRunItemAndLocation, HardwoodRunItemAndLocation hardwoodRunItemAndLocation) {
        this.client = client;
        this.plugin = plugin;
        this.itemManager = itemManager;
        this.herbRunItemAndLocation = herbRunItemAndLocation;
        this.treeRunItemAndLocation = treeRunItemAndLocation;
        this.fruitTreeRunItemAndLocation = fruitTreeRunItemAndLocation;
        this.hardwoodRunItemAndLocation = hardwoodRunItemAndLocation;
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
        if(item == ItemID.COMPOST) {
            return client.getVarbitValue(1442);
        }
        if(item == ItemID.SUPERCOMPOST) {
            return client.getVarbitValue(1443);
        }
        if (item == ItemID.ULTRACOMPOST) {
            return client.getVarbitValue(5732);
        }
        if (item == ItemID.BOTTOMLESS_COMPOST_BUCKET_22997) {
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
            if(plugin.getFarmingTeleportOverlay().hardwoodRun) {
                itemsToCheck = hardwoodRunItemAndLocation.getHardwoodItems();
            }

            if (itemsToCheck == null || itemsToCheck.isEmpty()) {
                return null;
            }

            ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
            Map<Integer, Integer> runePouchContents = getRunePouchContentsVarbits();

            Item[] items;
            if (inventory == null || inventory.getItems() == null) {
                items = new Item[0];
            } else {
                items = inventory.getItems();
            }

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

            int digsitePendantCount = 0;
            for (Item item : items) {
                if (isDigsitePendant(item.getId())) {
                    digsitePendantCount += item.getQuantity();
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
            if(plugin.getFarmingTeleportOverlay().hardwoodRun) {
                for (Item item : items) {
                    if (isHardwoodSapling(item.getId())) {
                        totalSeeds += item.getQuantity();
                    }
                }
            }

            panelComponent.getChildren().clear();
            int yOffset = 0;

            List<AbstractMap.SimpleEntry<Integer, Integer>> missingItemsWithCounts = new ArrayList<>();
            boolean allItemsCollected = true;
            for (Map.Entry<Integer, Integer> entry : itemsToCheck.entrySet()) {
                int itemId = entry.getKey();
                int count = entry.getValue();

                int inventoryCount = 0;
                if(plugin.getFarmingTeleportOverlay().herbRun) {
                    if (itemId == BASE_SEED_ID) {
                        inventoryCount = totalSeeds;
                    } else {
                        for (Item item : items) {
                            if (item != null && item.getId() == itemId) {
                                inventoryCount = item.getQuantity();
                                break;
                            }
                        }
                    }
                }
                // Check if the item is stored at the Tool Lep NPC
                int toolLepCount = checkToolLep(itemId);
                if (toolLepCount > 0) {
                    inventoryCount += toolLepCount;
                }
                if(plugin.getFarmingTeleportOverlay().treeRun) {
                    if (itemId == BASE_SAPLING_ID) {
                        inventoryCount = totalSeeds;
                    } else {
                        for (Item item : items) {
                            if (item != null && item.getId() == itemId) {
                                inventoryCount = item.getQuantity();
                                break;
                            }
                        }
                    }
                }
                if(plugin.getFarmingTeleportOverlay().fruitTreeRun) {
                    if (itemId == BASE_FRUIT_SAPLING_ID) {
                        inventoryCount = totalSeeds;
                    } else {
                        for (Item item : items) {
                            if (item != null && item.getId() == itemId) {
                                inventoryCount = item.getQuantity();
                                break;
                            }
                        }
                    }
                }

                if(plugin.getFarmingTeleportOverlay().hardwoodRun) {
                    if (itemId == BASE_HARDWOOD_SAPLING_ID) {
                        inventoryCount = totalSeeds;
                    } else {
                        for (Item item : items) {
                            if (item != null && item.getId() == itemId) {
                                inventoryCount = item.getQuantity();
                                break;
                            }
                        }
                    }
                }

                if (itemId == BASE_TELEPORT_CRYSTAL_ID) {
                    inventoryCount = teleportCrystalCount;
                } else {
                    for (Item item : items) {
                        if (item != null && item.getId() == itemId) {
                            inventoryCount = item.getQuantity();
                            break;
                        }
                    }
                }

                if (itemId == BASE_SKILLS_NECKLACE_ID) {
                    inventoryCount = skillsNecklaceCount;
                } else {
                    for (Item item : items) {
                        if (item != null && item.getId() == itemId) {
                            inventoryCount = item.getQuantity();
                            break;
                        }
                    }
                }

                if (itemId == BASE_DIGSITE_PENDANT_ID) {
                    inventoryCount = digsitePendantCount;
                } else {
                    for (Item item : items) {
                        if (item != null && item.getId() == itemId) {
                            inventoryCount = item.getQuantity();
                            break;
                        }
                    }
                }

                for (Item item : items) {
                    if (item != null && item.getId() == itemId) {
                        inventoryCount = item.getQuantity();
                        break;
                    }
                }


                for (Item item: items) {
                    if (item != null && RUNE_POUCH_ID.contains(item.getId())) {
                        if (runePouchContents.containsKey(itemId)) {
                            inventoryCount += runePouchContents.get(itemId);
                        }
                    }
                }

                for (Item item : items) {
                    if (item != null) {
                        int itemIdRune = item.getId();
                        int itemQuantity = item.getQuantity();

                        if (COMBINATION_RUNE_SUBRUNES_MAP.containsKey(itemIdRune)) {
                            handleCombinationRunes(itemIdRune, itemQuantity, runePouchContents);
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