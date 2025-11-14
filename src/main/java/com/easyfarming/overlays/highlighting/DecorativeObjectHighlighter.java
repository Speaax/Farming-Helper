package com.easyfarming.overlays.highlighting;

import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.overlays.utils.ColorProvider;
import net.runelite.api.Client;
import net.runelite.api.DecorativeObject;
import net.runelite.api.Tile;
import net.runelite.api.WorldView;
import net.runelite.client.ui.overlay.Overlay;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles highlighting of decorative objects in the world.
 */
public class DecorativeObjectHighlighter {
    private final Client client;
    private final EasyFarmingPlugin plugin;
    private final ColorProvider colorProvider;
    
    @Inject
    public DecorativeObjectHighlighter(Client client, EasyFarmingPlugin plugin, ColorProvider colorProvider) {
        this.client = client;
        this.plugin = plugin;
        this.colorProvider = colorProvider;
    }
    
    /**
     * Finds all decorative objects with the specified ID in the current scene.
     */
    public List<DecorativeObject> findDecorativeObjectsByID(int objectId) {
        Client client = this.client;
        List<DecorativeObject> foundDecorativeObjects = new ArrayList<>();
        
        if (client != null) {
            WorldView top_wv = client.getTopLevelWorldView();
            Tile[][][] tiles = top_wv.getScene().getTiles();
            for (int plane = 0; plane < tiles.length; plane++) {
                for (int x = 0; x < tiles[plane].length; x++) {
                    for (int y = 0; y < tiles[plane][x].length; y++) {
                        Tile tile = tiles[plane][x][y];
                        if (tile != null) {
                            DecorativeObject decorativeObject = tile.getDecorativeObject();
                            if (decorativeObject != null && decorativeObject.getId() == objectId) {
                                foundDecorativeObjects.add(decorativeObject);
                            }
                        }
                    }
                }
            }
        }
        
        return foundDecorativeObjects;
    }
    
    /**
     * Draws the clickbox for a decorative object.
     */
    public void drawDecorativeObjectClickbox(Graphics2D graphics, DecorativeObject decorativeObject, Color color) {
        Shape clickbox = decorativeObject.getClickbox();
        if (clickbox != null) {
            graphics.setColor(color);
            graphics.draw(clickbox);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
            graphics.fill(clickbox);
        }
    }
    
    /**
     * Highlights decorative objects by ID directly (for use in render methods).
     */
    public void highlightDecorativeObject(Graphics2D graphics, int objectId, Color color) {
        List<DecorativeObject> decorativeObjects = findDecorativeObjectsByID(objectId);
        for (DecorativeObject decorativeObject : decorativeObjects) {
            drawDecorativeObjectClickbox(graphics, decorativeObject, color);
        }
    }
    
    /**
     * Creates an overlay that highlights a decorative object by ID.
     */
    public Overlay highlightDecorativeObject(int objectId) {
        return highlightDecorativeObject(objectId, colorProvider.getLeftClickColorWithAlpha());
    }
    
    /**
     * Creates an overlay that highlights a decorative object by ID with a specific color.
     */
    public Overlay highlightDecorativeObject(int objectId, Color color) {
        return new Overlay() {
            @Override
            public Dimension render(Graphics2D graphics) {
                Client client = plugin.getClient();
                if (client != null) {
                    List<DecorativeObject> decorativeObjects = findDecorativeObjectsByID(objectId);
                    for (DecorativeObject decorativeObject : decorativeObjects) {
                        drawDecorativeObjectClickbox(graphics, decorativeObject, color);
                    }
                }
                return null;
            }
        };
    }
}

