package com.easyfarming;

import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.swing.*;
import java.awt.*;

import com.easyfarming.customrun.CustomRun;
import com.easyfarming.ui.CustomRunDetailPanel;
import com.easyfarming.ui.OverviewPanel;

/**
 * Same UI style as the custom-run design: card layout with overview list and run detail.
 * Overview shows the 4 run types (Herb, Tree, Fruit Tree, Hops) and custom runs; detail shows locations and teleport dropdowns.
 */
public class EasyFarmingPanel extends PluginPanel {
    private static final String OVERVIEW_PANEL = "OVERVIEW";
    private static final String DETAIL_PANEL = "DETAIL";

    private final EasyFarmingPlugin plugin;
    private final OverlayManager overlayManager;
    private final FarmingTeleportOverlay farmingTeleportOverlay;
    private final net.runelite.client.game.ItemManager itemManager;

    private final JPanel cardContainer;
    private final CardLayout cardLayout;
    private OverviewPanel overviewPanel;
    private JPanel currentDetailPanel;

    public EasyFarmingPanel(EasyFarmingPlugin plugin,
                            OverlayManager overlayManager,
                            FarmingTeleportOverlay farmingTeleportOverlay,
                            net.runelite.client.game.ItemManager itemManager) {
        super(false);
        this.plugin = plugin;
        this.overlayManager = overlayManager;
        this.farmingTeleportOverlay = farmingTeleportOverlay;
        this.itemManager = itemManager;

        setLayout(new BorderLayout());
        cardLayout = new CardLayout();
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

    /** Rebuild the overview list without switching cards (e.g. after rename/delete on a row). */
    public void refreshOverviewList() {
        if (overviewPanel != null) {
            overviewPanel.rebuildList();
        }
    }

    /** Called when the active custom run ends (e.g. last location completed). Refreshes overview and detail Start button. */
    public void onCustomRunEnded() {
        refreshOverviewList();
        if (currentDetailPanel instanceof CustomRunDetailPanel) {
            ((CustomRunDetailPanel) currentDetailPanel).refreshStartButtonState();
        }
    }

    public void showRunDetail(CustomRun customRun) {
        if (currentDetailPanel != null) {
            cardContainer.remove(currentDetailPanel);
        }
        currentDetailPanel = new CustomRunDetailPanel(plugin, this, customRun, false, itemManager);
        cardContainer.add(currentDetailPanel, DETAIL_PANEL);
        cardLayout.show(cardContainer, DETAIL_PANEL);
    }

    /**
     * Starts the given custom run: overlay runs in custom mode with the run's locations and patch order.
     */
    public void startCustomRun(CustomRun customRun) {
        SwingUtilities.invokeLater(() -> {
            plugin.setOverlayActive(true);
            farmingTeleportOverlay.startCustomRun(customRun);
            EasyFarmingOverlay overlay = plugin.getEasyFarmingOverlay();
            if (overlay != null) {
                overlayManager.add(overlay);
            }
            overlayManager.add(farmingTeleportOverlay);
        });
    }
}
