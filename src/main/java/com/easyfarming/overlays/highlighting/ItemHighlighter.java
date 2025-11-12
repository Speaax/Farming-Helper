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
import java.util.List;

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
     * Highlights an item in the inventory by its ID.
     */
    public void itemHighlight(Graphics2D graphics, int itemID, Color color) {
        net.runelite.api.ItemContainer inventory = client.getItemContainer(InventoryID.INV);
        
        if (inventory != null) {
            Item[] items = inventory.getItems();
            
            Widget inventoryWidget = client.getWidget(InterfaceID.INVENTORY);
            if (inventoryWidget == null) {
                inventoryWidget = client.getWidget(149, 0);
            }
            
            if (inventoryWidget != null) {
                Widget[] children = inventoryWidget.getChildren();
                Widget[] dynamicChildren = inventoryWidget.getDynamicChildren();
                
                Widget[] childrenToUse = (dynamicChildren != null && dynamicChildren.length > 0) ? dynamicChildren : children;
                
                if (childrenToUse != null) {
                    for (int i = 0; i < items.length && i < childrenToUse.length; i++) {
                        Item item = items[i];
                        
                        if (item != null && (item.getId() == itemID || 
                            isQuetzalWhistleHighlight(item.getId(), itemID) ||
                            isExplorersRingHighlight(item.getId(), itemID) ||
                            isArdyCloakHighlight(item.getId(), itemID))) {
                            Widget itemWidget = childrenToUse[i];
                            if (itemWidget != null) {
                                Rectangle bounds = itemWidget.getBounds();
                                if (bounds != null && bounds.width > 0 && bounds.height > 0) {
                                    graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
                                    graphics.fill(bounds);
                                    graphics.setColor(color);
                                    graphics.draw(bounds);
                                }
                            }
                        }
                    }
                }
            }
        }
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
     * Highlights herb seeds in inventory.
     */
    public void highlightHerbSeeds(Graphics2D graphics) {
        Color color = colorProvider.getHighlightUseItemWithAlpha();
        for (Integer seedId : farmingHelperOverlay.getHerbSeedIds()) {
            itemHighlight(graphics, seedId, color);
        }
    }
    
    /**
     * Highlights tree saplings in inventory.
     */
    public void highlightTreeSapling(Graphics2D graphics) {
        Color color = colorProvider.getHighlightUseItemWithAlpha();
        for (Integer seedId : farmingHelperOverlay.getTreeSaplingIds()) {
            itemHighlight(graphics, seedId, color);
        }
    }
    
    /**
     * Highlights fruit tree saplings in inventory.
     */
    public void highlightFruitTreeSapling(Graphics2D graphics) {
        Color color = colorProvider.getHighlightUseItemWithAlpha();
        for (Integer seedId : farmingHelperOverlay.getFruitTreeSaplingIds()) {
            itemHighlight(graphics, seedId, color);
        }
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
        Color color = colorProvider.getLeftClickColorWithAlpha();
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
            if (item.getId() == itemId) {
                return true;
            }
        }
        
        return false;
    }
}

