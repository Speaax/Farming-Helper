package com.easyfarming.overlays.utils;

import com.easyfarming.EasyFarmingConfig;
import java.awt.Color;

/**
 * Provides color computation from config values.
 * Colors are computed dynamically to reflect config changes.
 */
public class ColorProvider {
    private final EasyFarmingConfig config;
    
    public ColorProvider(EasyFarmingConfig config) {
        this.config = config;
    }
    
    /**
     * Gets the left-click color with alpha applied.
     */
    public Color getLeftClickColorWithAlpha() {
        return new Color(
            config.highlightLeftClickColor().getRed(),
            config.highlightLeftClickColor().getGreen(),
            config.highlightLeftClickColor().getBlue(),
            config.highlightAlpha()
        );
    }
    
    /**
     * Gets the right-click color with alpha applied.
     */
    public Color getRightClickColorWithAlpha() {
        return new Color(
            config.highlightRightClickColor().getRed(),
            config.highlightRightClickColor().getGreen(),
            config.highlightRightClickColor().getBlue(),
            config.highlightAlpha()
        );
    }
    
    /**
     * Gets the use-item color with alpha applied.
     */
    public Color getHighlightUseItemWithAlpha() {
        return new Color(
            config.highlightUseItemColor().getRed(),
            config.highlightUseItemColor().getGreen(),
            config.highlightUseItemColor().getBlue(),
            config.highlightAlpha()
        );
    }
}

