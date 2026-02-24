package com.easyfarming;

import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.swing.*;
import java.awt.*;

import com.easyfarming.ui.OverviewPanel;
import com.easyfarming.ui.RunDetailPanel;

public class EasyFarmingPanel extends PluginPanel
{
    private final EasyFarmingPlugin plugin;
    private final OverlayManager overlayManager;
    private final FarmingTeleportOverlay farmingTeleportOverlay;
    private final net.runelite.client.game.ItemManager itemManager;

    private final JPanel cardContainer;
    private final CardLayout cardLayout;
    private static final String OVERVIEW_PANEL = "OVERVIEW";
    private static final String DETAIL_PANEL   = "DETAIL";

    private OverviewPanel overviewPanel;
    private RunDetailPanel currentDetailPanel;

    public EasyFarmingPanel(EasyFarmingPlugin plugin,
                            OverlayManager overlayManager,
                            FarmingTeleportOverlay farmingTeleportOverlay,
                            net.runelite.client.game.ItemManager itemManager)
    {
        super(false);
        this.plugin                 = plugin;
        this.overlayManager         = overlayManager;
        this.farmingTeleportOverlay = farmingTeleportOverlay;
        this.itemManager            = itemManager;

        setLayout(new BorderLayout());

        cardLayout    = new CardLayout();
        cardContainer = new JPanel(cardLayout);

        overviewPanel = new OverviewPanel(plugin, this);
        cardContainer.add(overviewPanel, OVERVIEW_PANEL);

        add(cardContainer, BorderLayout.CENTER);
    }

    public void showOverview() {
        if (overviewPanel != null) {
            overviewPanel.rebuildList();
        }
        cardLayout.show(cardContainer, OVERVIEW_PANEL);
        if (currentDetailPanel != null) {
            cardContainer.remove(currentDetailPanel);
            currentDetailPanel = null;
        }
    }

    public void showRunDetail(String runName) {
        if (currentDetailPanel != null) {
            cardContainer.remove(currentDetailPanel);
        }
        currentDetailPanel = new RunDetailPanel(plugin, this, runName, itemManager);
        cardContainer.add(currentDetailPanel, DETAIL_PANEL);
        cardLayout.show(cardContainer, DETAIL_PANEL);
    }
}