package com.easyfarming.overlays.highlighting;

import com.easyfarming.EasyFarmingOverlay;
import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.InventoryTabChecker;
import com.easyfarming.core.Teleport;
import com.easyfarming.overlays.utils.ColorProvider;
import com.easyfarming.overlays.utils.GameObjectHelper;
import com.easyfarming.overlays.utils.WidgetHelper;
import com.easyfarming.utils.Constants;
import net.runelite.api.Client;
import net.runelite.api.gameval.VarClientID;
import net.runelite.api.widgets.Widget;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;

/**
 * Handles highlighting of teleport methods (items, spellbook, portal nexus, spirit tree, etc.).
 */
public class TeleportHighlighter {
    private final Client client;
    private final EasyFarmingPlugin plugin;
    private final EasyFarmingOverlay easyFarmingOverlay;
    private final ItemHighlighter itemHighlighter;
    private final WidgetHighlighter widgetHighlighter;
    private final GameObjectHighlighter gameObjectHighlighter;
    private final WidgetHelper widgetHelper;
    private final GameObjectHelper gameObjectHelper;
    private final ColorProvider colorProvider;
    
    @Inject
    public TeleportHighlighter(Client client, EasyFarmingPlugin plugin, EasyFarmingOverlay easyFarmingOverlay,
                               ItemHighlighter itemHighlighter, WidgetHighlighter widgetHighlighter,
                               GameObjectHighlighter gameObjectHighlighter, WidgetHelper widgetHelper,
                               GameObjectHelper gameObjectHelper, ColorProvider colorProvider) {
        this.client = client;
        this.plugin = plugin;
        this.easyFarmingOverlay = easyFarmingOverlay;
        this.itemHighlighter = itemHighlighter;
        this.widgetHighlighter = widgetHighlighter;
        this.gameObjectHighlighter = gameObjectHighlighter;
        this.widgetHelper = widgetHelper;
        this.gameObjectHelper = gameObjectHelper;
        this.colorProvider = colorProvider;
    }
    
    /**
     * Highlights the appropriate teleport method based on category.
     */
    public void highlightTeleportMethod(Teleport teleport, Graphics2D graphics) {
        Color leftColor = colorProvider.getLeftClickColorWithAlpha();
        Color rightColor = colorProvider.getRightClickColorWithAlpha();
        
        switch (teleport.getCategory()) {
            case ITEM:
                // Check if it's a Quetzal whistle, Royal seed pod, or Ectophial (left-click teleport)
                if (easyFarmingOverlay.isQuetzalWhistle(teleport.getId()) || 
                    easyFarmingOverlay.isRoyalSeedPod(teleport.getId()) ||
                    easyFarmingOverlay.isEctophial(teleport.getId())) {
                    itemHighlighter.itemHighlight(graphics, teleport.getId(), leftColor);
                } else {
                    itemHighlighter.itemHighlight(graphics, teleport.getId(), rightColor);
                    if (!teleport.getRightClickOption().equals("")) {
                        // MenuHighlighter would be needed here, but we'll handle it in the calling code
                    }
                }
                break;
            case SPELLBOOK:
                InventoryTabChecker.TabState tabState = InventoryTabChecker.checkTab(client, VarClientID.TOPLEVEL_PANEL);
                if (tabState == InventoryTabChecker.TabState.SPELLBOOK) {
                    widgetHighlighter.interfaceOverlay(teleport.getInterfaceGroupId(), teleport.getInterfaceChildId()).render(graphics);
                } else {
                    widgetHighlighter.interfaceOverlay(widgetHelper.getSpellbookTabGroupId(), widgetHelper.getSpellbookTabChildId()).render(graphics);
                }
                break;
            case PORTAL_NEXUS:
                if (!widgetHelper.isInterfaceOpen(17, 0)) {
                    List<Integer> portalNexusIds = gameObjectHelper.getGameObjectIdsByName("Portal Nexus");
                    for (Integer objectId : portalNexusIds) {
                        gameObjectHighlighter.highlightGameObject(objectId, leftColor).render(graphics);
                    }
                } else {
                    Widget widget = client.getWidget(Constants.INTERFACE_PORTAL_NEXUS, Constants.INTERFACE_PORTAL_NEXUS_CHILD);
                    int index = widgetHelper.getChildIndexPortalNexus(teleport.getPoint().toString());
                    widgetHighlighter.highlightDynamicComponent(graphics, widget, index);
                }
                break;
            case SPIRIT_TREE:
                if (!widgetHelper.isInterfaceOpen(187, 3)) {
                    List<Integer> spiritTreeIds = Constants.SPIRIT_TREE_IDS;
                    for (Integer objectId : spiritTreeIds) {
                        gameObjectHighlighter.highlightGameObject(objectId, leftColor).render(graphics);
                    }
                } else {
                    Widget widget = client.getWidget(Constants.INTERFACE_SPIRIT_TREE, Constants.INTERFACE_SPIRIT_TREE_CHILD);
                    int index = widgetHelper.getChildIndexSpiritTree(teleport.getPoint().toString());
                    widgetHighlighter.highlightDynamicComponent(graphics, widget, index);
                }
                break;
            case JEWELLERY_BOX:
                if (!widgetHelper.isInterfaceOpen(Constants.INTERFACE_JEWELLERY_BOX, 0)) {
                    List<Integer> jewelleryBoxIds = gameObjectHelper.getGameObjectIdsByName("Jewellery Box");
                    for (Integer objectId : jewelleryBoxIds) {
                        gameObjectHighlighter.highlightGameObject(objectId, leftColor).render(graphics);
                    }
                } else {
                    Widget widget = client.getWidget(Constants.INTERFACE_JEWELLERY_BOX, 0);
                    widgetHighlighter.highlightDynamicComponent(graphics, widget, 0);
                }
                break;
            case MOUNTED_XERICS:
                // Mounted Xeric's talisman handling is done in NavigationHandler
                break;
        }
    }
}

