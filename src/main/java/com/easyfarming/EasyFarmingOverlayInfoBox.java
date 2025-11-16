package com.easyfarming;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import java.awt.Dimension;
import java.awt.Graphics2D;

public class EasyFarmingOverlayInfoBox extends Overlay {
    private final Client client;
    private final PanelComponent panelComponent = new PanelComponent();
    private final EasyFarmingPlugin plugin;

    private String text;
    private String debugText;

    @Inject
    public EasyFarmingOverlayInfoBox(Client client, EasyFarmingPlugin plugin) {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.TOP_LEFT);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
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

        panelComponent.getChildren().clear();

        if (text != null) {
            panelComponent.getChildren().add(LineComponent.builder().left(text).build());
        }

        if (debugText != null) {
            panelComponent.getChildren().add(LineComponent.builder().left(debugText).build());
        }

        return panelComponent.render(graphics);
    }
}