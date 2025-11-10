package com.easyfarming.overlays.highlighting;

import com.easyfarming.overlays.utils.ColorProvider;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;

import javax.inject.Inject;
import java.awt.*;

/**
 * Handles highlighting of widgets and interface components.
 */
public class WidgetHighlighter {
    private final Client client;
    private final ColorProvider colorProvider;
    
    @Inject
    public WidgetHighlighter(Client client, ColorProvider colorProvider) {
        this.client = client;
        this.colorProvider = colorProvider;
    }
    
    /**
     * Creates an overlay that highlights a widget interface.
     */
    public Overlay interfaceOverlay(int groupId, int childId) {
        return new Overlay() {
            @Override
            public Dimension render(Graphics2D graphics) {
                Client client = WidgetHighlighter.this.client;
                if (client != null) {
                    Widget widget = client.getWidget(groupId, childId);
                    if (widget != null) {
                        Rectangle bounds = widget.getBounds();
                        Color color = colorProvider.getLeftClickColorWithAlpha();
                        graphics.setColor(color);
                        
                        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.65f);
                        graphics.setComposite(alphaComposite);
                        
                        graphics.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
                        
                        graphics.setComposite(AlphaComposite.SrcOver);
                    }
                }
                return null;
            }
        };
    }
    
    /**
     * Highlights a dynamic component within a widget.
     */
    public void highlightDynamicComponent(Graphics2D graphics, Widget widget, int dynamicChildIndex) {
        if (widget != null) {
            Widget[] dynamicChildren = widget.getDynamicChildren();
            if (dynamicChildren != null && dynamicChildIndex >= 0 && dynamicChildIndex < dynamicChildren.length) {
                Widget child = dynamicChildren[dynamicChildIndex];
                if (child != null) {
                    Rectangle bounds = child.getBounds();
                    if (bounds != null && bounds.width > 0 && bounds.height > 0) {
                        Color color = colorProvider.getLeftClickColorWithAlpha();
                        graphics.setColor(color);
                        
                        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.65f);
                        graphics.setComposite(alphaComposite);
                        
                        graphics.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
                        
                        graphics.setComposite(AlphaComposite.SrcOver);
                    }
                }
            }
        }
    }
}

