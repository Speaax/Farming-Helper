package com.easyfarming;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * InfoBox that displays missing item requirements for farming runs.
 * Shows the item icon and the count of missing items (1 or more).
 * Used in the overlay to indicate which items the player still needs to gather.
 */
public class RequiredItemInfoBox extends InfoBox {
    private final int itemId;
    private final int missingCount;

    /**
     * @param itemName display name for tooltip (e.g. "Guam seed"); if null or empty, tooltip is "Missing: &lt;count&gt;"
     */
    public RequiredItemInfoBox(BufferedImage image, Plugin plugin, int itemId, int missingCount, String itemName) {
        super(image, plugin);
        this.itemId = itemId;
        this.missingCount = missingCount;
        if (itemName != null && !itemName.isEmpty()) {
            setTooltip("Missing " + missingCount + " " + itemName);
        } else {
            setTooltip("Missing: " + missingCount);
        }
    }

    public int getItemId() {
        return itemId;
    }

    public int getMissingCount() {
        return missingCount;
    }

    @Override
    public String getText() {
        return missingCount >= 1 ? String.valueOf(missingCount) : null;
    }

    @Override
    public Color getTextColor() {
        return Color.WHITE;
    }
}

