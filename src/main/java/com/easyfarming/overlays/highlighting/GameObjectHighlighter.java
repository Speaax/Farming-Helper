package com.easyfarming.overlays.highlighting;

import com.easyfarming.EasyFarmingPlugin;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.Tile;
import net.runelite.api.WorldView;
import net.runelite.client.ui.overlay.Overlay;
import com.easyfarming.utils.Constants;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles highlighting of game objects in the world.
 */
public class GameObjectHighlighter {
    private final Client client;
    private final EasyFarmingPlugin plugin;
    
    @Inject
    public GameObjectHighlighter(Client client, EasyFarmingPlugin plugin) {
        this.client = client;
        this.plugin = plugin;
    }
    
    /**
     * Finds all game objects with the specified ID in the current scene.
     */
    public List<GameObject> findGameObjectsByID(int objectID) {
        List<GameObject> gameObjects = new ArrayList<>();
        for (int x = 0; x < Constants.SCENE_SIZE; x++) {
            for (int y = 0; y < Constants.SCENE_SIZE; y++) {
                WorldView top_wv = client.getTopLevelWorldView();
                Tile tile = top_wv.getScene().getTiles()[top_wv.getPlane()][x][y];
                if (tile == null) {
                    continue;
                }
                
                for (GameObject gameObject : tile.getGameObjects()) {
                    if (gameObject != null && gameObject.getId() == objectID) {
                        gameObjects.add(gameObject);
                    }
                }
            }
        }
        return gameObjects;
    }
    
    /**
     * Draws the clickbox for a game object.
     */
    public void drawGameObjectClickbox(Graphics2D graphics, GameObject gameObject, Color color) {
        Shape objectClickbox = gameObject.getClickbox();
        if (objectClickbox != null) {
            graphics.setColor(color);
            graphics.draw(objectClickbox);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() / 5));
            graphics.fill(objectClickbox);
        }
    }
    
    /**
     * Creates an overlay that highlights a game object by ID.
     */
    public Overlay highlightGameObject(int objectId, Color color) {
        return new Overlay() {
            @Override
            public Dimension render(Graphics2D graphics) {
                Client client = plugin.getClient();
                if (client != null) {
                    List<GameObject> gameObjects = findGameObjectsByID(objectId);
                    for (GameObject gameObject : gameObjects) {
                        drawGameObjectClickbox(graphics, gameObject, color);
                    }
                }
                return null;
            }
        };
    }
}

