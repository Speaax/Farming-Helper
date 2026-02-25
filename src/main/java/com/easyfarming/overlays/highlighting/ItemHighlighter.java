package com.easyfarming.overlays.highlighting;

import com.easyfarming.EasyFarmingOverlay;
import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.overlays.utils.ColorProvider;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.widgets.Widget;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Handles highlighting of items in inventory and various item-related highlights.
 */
public class ItemHighlighter {
    private final Client client;
    private final EasyFarmingOverlay farmingHelperOverlay;
    private final EasyFarmingConfig config;
    private final ColorProvider colorProvider;
    
    @Inject
    public ItemHighlighter(Client client, EasyFarmingOverlay farmingHelperOverlay, 
                          EasyFarmingConfig config, ColorProvider colorProvider) {
        this.client = client;
        this.farmingHelperOverlay = farmingHelperOverlay;
        this.config = config;
        this.colorProvider = colorProvider;
    }
    
    /**
     * Retrieves inventory items and corresponding widgets for slot-based highlighting.
     * @return pair of items and widgets, or null if inventory/widget not available
     */
    private InventoryItemsAndWidgets getInventoryItemsAndWidgets() {
        net.runelite.api.ItemContainer inventory = client.getItemContainer(InventoryID.INV);
        if (inventory == null) {
            return null;
        }
        Item[] items = inventory.getItems();
        Widget inventoryWidget = client.getWidget(InterfaceID.INVENTORY);
        if (inventoryWidget == null) {
            inventoryWidget = client.getWidget(149, 0);
        }
        if (inventoryWidget == null) {
            return null;
        }
        Widget[] children = inventoryWidget.getChildren();
        Widget[] dynamicChildren = inventoryWidget.getDynamicChildren();
        Widget[] childrenToUse = (dynamicChildren != null && dynamicChildren.length > 0) ? dynamicChildren : children;
        if (childrenToUse == null) {
            return null;
        }
        return new InventoryItemsAndWidgets(items, childrenToUse);
    }

    private static final class InventoryItemsAndWidgets {
        final Item[] items;
        final Widget[] widgets;
        InventoryItemsAndWidgets(Item[] items, Widget[] widgets) {
            this.items = items;
            this.widgets = widgets;
        }
    }

    /**
     * Highlights an item in the inventory by its ID.
     * Uses master's approach: match by slot index (ItemContainer + widget children) so highlighting is reliable.
     */
    public void itemHighlight(Graphics2D graphics, int itemID, Color color) {
        InventoryItemsAndWidgets data = getInventoryItemsAndWidgets();
        if (data == null) {
            return;
        }
        Item[] items = data.items;
        Widget[] childrenToUse = data.widgets;
        for (int i = 0; i < items.length && i < childrenToUse.length; i++) {
            Item item = items[i];
            if (item != null && itemMatchesTarget(item.getId(), itemID)) {
                Widget itemWidget = childrenToUse[i];
                if (itemWidget != null) {
                    drawHighlightOnWidget(graphics, itemWidget, color);
                }
            }
        }
    }

    /**
     * Highlights compost in inventory for the selected compost type.
     * For Bottomless: highlights empty and all filled bottomless bucket variants.
     * For Compost/Supercompost/Ultracompost: highlights that bucket type and all bottomless variants (any tier).
     */
    public void highlightCompost(Graphics2D graphics, int selectedCompostId, Color color) {
        List<Integer> idsToHighlight = new ArrayList<>();
        if (selectedCompostId == ItemID.BOTTOMLESS_COMPOST_BUCKET) {
            idsToHighlight.addAll(farmingHelperOverlay.getBottomlessCompostBucketIds());
        } else if (selectedCompostId == ItemID.BUCKET_COMPOST
                || selectedCompostId == ItemID.BUCKET_SUPERCOMPOST
                || selectedCompostId == ItemID.BUCKET_ULTRACOMPOST) {
            idsToHighlight.add(selectedCompostId);
            idsToHighlight.addAll(farmingHelperOverlay.getBottomlessCompostBucketIds());
        } else {
            idsToHighlight.add(selectedCompostId);
        }
        highlightInventorySlotsWithIds(graphics, idsToHighlight, color);
    }

    /**
     * Highlights every inventory slot whose item matches any of the given IDs (with variant handling for compost etc.).
     * Uses master's index-based approach: ItemContainer slots + widget children by index.
     */
    public void highlightInventorySlotsWithIds(Graphics2D graphics, List<Integer> targetIds, Color color) {
        if (targetIds == null || targetIds.isEmpty()) {
            return;
        }
        Set<Integer> set = new HashSet<>(targetIds);
        InventoryItemsAndWidgets data = getInventoryItemsAndWidgets();
        if (data == null) {
            return;
        }
        Item[] items = data.items;
        Widget[] childrenToUse = data.widgets;
        for (int i = 0; i < items.length && i < childrenToUse.length; i++) {
            Item item = items[i];
            if (item != null && itemMatchesAny(item.getId(), set)) {
                Widget itemWidget = childrenToUse[i];
                if (itemWidget != null) {
                    drawHighlightOnWidget(graphics, itemWidget, color);
                }
            }
        }
    }

    private void drawHighlightOnWidget(Graphics2D graphics, Widget w, Color color) {
        Rectangle bounds = w.getBounds();
        if (bounds != null && bounds.width > 0 && bounds.height > 0) {
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
            graphics.fill(bounds);
            graphics.setColor(color);
            graphics.draw(bounds);
        }
    }

    private boolean itemMatchesTarget(int itemId, int targetId) {
        return itemId == targetId
                || isQuetzalWhistleHighlight(itemId, targetId)
                || isExplorersRingHighlight(itemId, targetId)
                || isArdyCloakHighlight(itemId, targetId)
                || isSkillsNecklaceHighlight(itemId, targetId)
                || isBottomlessBucketHighlight(itemId, targetId)
                || isCombatBraceletHighlight(itemId, targetId);
    }

    private boolean itemMatchesAny(int itemId, Set<Integer> targetIds) {
        for (Integer targetId : targetIds) {
            if (itemMatchesTarget(itemId, targetId)) return true;
        }
        return false;
    }
    
    /**
     * Checks if an item ID matches a quetzal whistle highlight pattern.
     */
    private boolean isQuetzalWhistleHighlight(int itemId, int targetId) {
        return farmingHelperOverlay.isQuetzalWhistle(itemId) && farmingHelperOverlay.isQuetzalWhistle(targetId);
    }
    
    /**
     * Checks if an item ID matches an Explorer's Ring highlight pattern.
     */
    private boolean isExplorersRingHighlight(int itemId, int targetId) {
        return farmingHelperOverlay.isExplorersRing(itemId) && farmingHelperOverlay.isExplorersRing(targetId);
    }
    
    /**
     * Checks if an item ID matches an Ardougne Cloak highlight pattern.
     */
    private boolean isArdyCloakHighlight(int itemId, int targetId) {
        return farmingHelperOverlay.isArdyCloak(itemId) && farmingHelperOverlay.isArdyCloak(targetId);
    }
    
    /**
     * Checks if an item ID matches a Skills Necklace highlight pattern.
     */
    private boolean isSkillsNecklaceHighlight(int itemId, int targetId) {
        return farmingHelperOverlay.isSkillsNecklace(itemId) && farmingHelperOverlay.isSkillsNecklace(targetId);
    }
    
    /**
     * Checks if an item ID matches a bottomless compost bucket highlight pattern.
     * Handles both empty bucket and all filled variants (22994-22998).
     */
    private boolean isBottomlessBucketHighlight(int itemId, int targetId) {
        if (targetId == ItemID.BOTTOMLESS_COMPOST_BUCKET) {
            // Check if itemId is the base bucket or any filled variant
            return itemId == ItemID.BOTTOMLESS_COMPOST_BUCKET ||
                   (itemId >= 22994 && itemId <= 22998);
        }
        return false;
    }

    /**
     * Checks if an item ID matches a Combat bracelet highlight pattern.
     * Handles all charged variants (2-6 charges).
     */
    private boolean isCombatBraceletHighlight(int itemId, int targetId) {
        return farmingHelperOverlay.isCombatBracelet(itemId) && farmingHelperOverlay.isCombatBracelet(targetId);
    }
    
    /**
     * Highlights allotment seeds in inventory.
     */
    public void highlightAllotmentSeeds(Graphics2D graphics) {
        Color useItemColor = colorProvider.getHighlightUseItemWithAlpha();
        highlightInventorySlotsWithIds(graphics, farmingHelperOverlay.getAllotmentSeedIds(), useItemColor);
    }

    /**
     * Highlights herb seeds in inventory.
     */
    public void highlightHerbSeeds(Graphics2D graphics) {
        Color color = colorProvider.getHighlightUseItemWithAlpha();
        highlightInventorySlotsWithIds(graphics, farmingHelperOverlay.getHerbSeedIds(), color);
    }

    /**
     * Highlights hops seeds in inventory.
     */
    public void highlightHopsSeeds(Graphics2D graphics) {
        Color color = colorProvider.getHighlightUseItemWithAlpha();
        highlightInventorySlotsWithIds(graphics, farmingHelperOverlay.getHopsSeedIds(), color);
    }

    /**
     * Highlights tree saplings in inventory.
     */
    public void highlightTreeSapling(Graphics2D graphics) {
        Color color = colorProvider.getHighlightUseItemWithAlpha();
        highlightInventorySlotsWithIds(graphics, farmingHelperOverlay.getTreeSaplingIds(), color);
    }

    /**
     * Highlights fruit tree saplings in inventory.
     */
    public void highlightFruitTreeSapling(Graphics2D graphics) {
        Color color = colorProvider.getHighlightUseItemWithAlpha();
        highlightInventorySlotsWithIds(graphics, farmingHelperOverlay.getFruitTreeSaplingIds(), color);
    }
    
    /**
     * Highlights teleport crystals in inventory.
     */
    public void highlightTeleportCrystal(Graphics2D graphics) {
        Color color = colorProvider.getLeftClickColorWithAlpha();
        for (Integer crystalId : farmingHelperOverlay.getTeleportCrystalIds()) {
            itemHighlight(graphics, crystalId, color);
        }
    }
    
    /**
     * Highlights skills necklaces in inventory.
     */
    public void highlightSkillsNecklace(Graphics2D graphics) {
        Color color = colorProvider.getRightClickColorWithAlpha();
        for (Integer necklaceId : farmingHelperOverlay.getSkillsNecklaceIds()) {
            itemHighlight(graphics, necklaceId, color);
        }
    }
    
    /**
     * Gets the selected compost item ID.
     */
    public Integer selectedCompostID() {
        EasyFarmingConfig.OptionEnumCompost selectedCompost = config.enumConfigCompost();
        switch (selectedCompost) {
            case Compost:
                return ItemID.BUCKET_COMPOST;
            case Supercompost:
                return ItemID.BUCKET_SUPERCOMPOST;
            case Ultracompost:
                return ItemID.BUCKET_ULTRACOMPOST;
            case Bottomless:
                return ItemID.BOTTOMLESS_COMPOST_BUCKET;
        }
        return -1;
    }
    
    /**
     * Checks if an item is in the inventory.
     * For bottomless compost bucket, also checks for filled variants.
     */
    public boolean isItemInInventory(int itemId) {
        net.runelite.api.ItemContainer inventory = client.getItemContainer(InventoryID.INV);
        
        Item[] items;
        if (inventory == null || inventory.getItems() == null) {
            items = new Item[0];
        } else {
            items = inventory.getItems();
        }
        
        for (Item item : items) {
            int checkItemId = item.getId();
            if (checkItemId == itemId) {
                return true;
            }
            // Special handling for bottomless compost bucket - check filled variants
            if (itemId == ItemID.BOTTOMLESS_COMPOST_BUCKET) {
                // Check for all bottomless bucket variants (empty: BOTTOMLESS_COMPOST_BUCKET, 
                // filled: 22994-22998 for various compost types)
                if (checkItemId == ItemID.BOTTOMLESS_COMPOST_BUCKET ||
                    (checkItemId >= 22994 && checkItemId <= 22998)) {
                    return true;
                }
            }
        }
        
        return false;
    }
}

