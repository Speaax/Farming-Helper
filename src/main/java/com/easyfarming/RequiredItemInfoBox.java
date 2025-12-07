package com.easyfarming;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class RequiredItemInfoBox extends InfoBox {
    private final int itemId;
    private final int missingCount;

    public RequiredItemInfoBox(BufferedImage image, Plugin plugin, int itemId, int missingCount) {
        super(image, plugin);
        this.itemId = itemId;
        this.missingCount = missingCount;
        setTooltip("Missing: " + missingCount);
    }

    public int getItemId() {
        return itemId;
    }

    public int getMissingCount() {
        return missingCount;
    }

    @Override
    public String getText() {
        return missingCount > 1 ? String.valueOf(missingCount) : null;
    }

    @Override
    public Color getTextColor() {
        return Color.WHITE;
    }
}

