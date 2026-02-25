package com.easyfarming;

import java.awt.*;
import javax.inject.Inject;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import com.easyfarming.overlays.highlighting.GameObjectHighlighter;
import com.easyfarming.utils.Constants;

/**
 * Draws teleport-related scene highlights (e.g. Spirit Tree object) at ABOVE_SCENE
 * so they appear correctly on the game scene/map instead of above widgets.
 */
public class FarmingTeleportSceneOverlay extends Overlay {
    private final GameObjectHighlighter gameObjectHighlighter;

    /** Set by NavigationHandler when spirit tree object should be highlighted next frame. */
    private Color spiritTreeHighlightColor = null;

    @Inject
    public FarmingTeleportSceneOverlay(GameObjectHighlighter gameObjectHighlighter) {
        this.gameObjectHighlighter = gameObjectHighlighter;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    /**
     * Request spirit tree game objects to be highlighted on the next frame (above scene).
     * Called from NavigationHandler when the Spirit Tree interface is not open.
     */
    public void requestSpiritTreeHighlight(Color color) {
        this.spiritTreeHighlightColor = color;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (spiritTreeHighlightColor == null) {
            return null;
        }
        Color color = spiritTreeHighlightColor;
        spiritTreeHighlightColor = null;
        for (Integer objectId : Constants.SPIRIT_TREE_IDS) {
            gameObjectHighlighter.highlightGameObject(objectId, color).render(graphics);
        }
        return null;
    }
}
