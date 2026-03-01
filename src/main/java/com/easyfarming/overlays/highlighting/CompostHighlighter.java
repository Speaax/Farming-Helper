package com.easyfarming.overlays.highlighting;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.overlays.utils.ColorProvider;
import com.easyfarming.overlays.utils.PatchStateChecker;
import com.easyfarming.utils.Constants;
import net.runelite.api.Client;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.widgets.Widget;

import javax.inject.Inject;
import java.awt.*;

/**
 * Handles highlighting of compost and compost withdrawal from Tool Leprechaun.
 */
public class CompostHighlighter {
    private final Client client;
    private final EasyFarmingConfig config;
    private final ItemHighlighter itemHighlighter;
    private final PatchHighlighter patchHighlighter;
    private final NPCHighlighter npcHighlighter;
    private final WidgetHighlighter widgetHighlighter;
    private final PatchStateChecker patchStateChecker;
    private final ColorProvider colorProvider;
    
    @Inject
    public CompostHighlighter(Client client, EasyFarmingConfig config, 
                             ItemHighlighter itemHighlighter, PatchHighlighter patchHighlighter,
                             NPCHighlighter npcHighlighter, WidgetHighlighter widgetHighlighter,
                             PatchStateChecker patchStateChecker, ColorProvider colorProvider) {
        this.client = client;
        this.config = config;
        this.itemHighlighter = itemHighlighter;
        this.patchHighlighter = patchHighlighter;
        this.npcHighlighter = npcHighlighter;
        this.widgetHighlighter = widgetHighlighter;
        this.patchStateChecker = patchStateChecker;
        this.colorProvider = colorProvider;
    }
    
    /**
     * Highlights compost in inventory and patches, or withdraws from Tool Leprechaun if not in inventory.
     */
    public void highlightCompost(Graphics2D graphics, boolean herbRun, boolean treeRun, 
                                boolean fruitTreeRun, int subCase) {
        Integer compostId = itemHighlighter.selectedCompostID();
        Color color = colorProvider.getHighlightUseItemWithAlpha();
        
        if (itemHighlighter.isItemInInventory(compostId)) {
            if (herbRun) {
                if (subCase == 1) {
                    patchHighlighter.highlightHerbPatches(graphics, color);
                } else if (subCase == 2) {
                    patchHighlighter.highlightFlowerPatches(graphics, color);
                }
            }
            
            if (treeRun) {
                patchHighlighter.highlightTreePatches(graphics, color);
            }
            
            if (fruitTreeRun) {
                patchHighlighter.highlightFruitTreePatches(graphics, color);
            }
            
            itemHighlighter.highlightCompost(graphics, compostId, color);
        } else {
            withdrawCompost(graphics);
        }
    }
    
    /**
     * Highlights compost without subCase parameter (for backward compatibility).
     */
    public void highlightCompost(Graphics2D graphics, boolean herbRun, boolean treeRun, boolean fruitTreeRun) {
        highlightCompost(graphics, herbRun, treeRun, fruitTreeRun, 1);
    }
    
    /**
     * Highlights the Tool Leprechaun and interface for withdrawing compost.
     */
    public void withdrawCompost(Graphics2D graphics) {
        if (!isInterfaceOpen(Constants.INTERFACE_TOOL_LEPRECHAUN, 0)) {
            npcHighlighter.highlightNpc(graphics, "Tool Leprechaun");
        } else {
            Integer compostId = itemHighlighter.selectedCompostID();
            if (compostId == ItemID.BUCKET_COMPOST) {
                widgetHighlighter.interfaceOverlay(Constants.INTERFACE_TOOL_LEPRECHAUN, 17).render(graphics);
            } else if (compostId == ItemID.BUCKET_SUPERCOMPOST) {
                widgetHighlighter.interfaceOverlay(Constants.INTERFACE_TOOL_LEPRECHAUN, 18).render(graphics);
            } else if (compostId == ItemID.BUCKET_ULTRACOMPOST) {
                widgetHighlighter.interfaceOverlay(Constants.INTERFACE_TOOL_LEPRECHAUN, 19).render(graphics);
            } else if (compostId == ItemID.BOTTOMLESS_COMPOST_BUCKET) {
                widgetHighlighter.interfaceOverlay(Constants.INTERFACE_TOOL_LEPRECHAUN, 15).render(graphics);
            }
        }
    }
    
    private boolean isInterfaceOpen(int groupId, int childId) {
        Widget widget = client.getWidget(groupId, childId);
        return widget != null && !widget.isHidden();
    }
}

