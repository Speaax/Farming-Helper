package com.easyfarming;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.Dimension;
import java.awt.Graphics2D;

/**
 * Overlay that displays text information in the top-left corner of the game screen.
 * Used to show current farming step instructions and debug information.
 * Only renders when the overlay is active.
 * Extends OverlayPanel for Runelite-consistent styling.
 */
public class EasyFarmingOverlayInfoBox extends OverlayPanel {
    private final Client client;
    private final EasyFarmingPlugin plugin;

    private String text;
    private String debugText;

    @Inject
    public EasyFarmingOverlayInfoBox(Client client, EasyFarmingPlugin plugin) {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.TOP_LEFT);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDebugText(String debugText) {
        this.debugText = debugText;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!plugin.isOverlayActive()) {
            return null;
        }

        getPanelComponent().getChildren().clear();

        if (text != null) {
            getPanelComponent().getChildren().add(LineComponent.builder().left(text).build());
        }

        if (debugText != null) {
            getPanelComponent().getChildren().add(LineComponent.builder().left(debugText).build());
        }

        return super.render(graphics);
    }
}