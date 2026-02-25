package com.easyfarming.overlays.highlighting;

import com.easyfarming.EasyFarmingPlugin;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Tile;
import net.runelite.api.WorldView;
import net.runelite.client.ui.overlay.Overlay;
import com.easyfarming.utils.Constants;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles highlighting of game objects in the world.
 * Caches scene lookups per (objectId, region, plane) to avoid full scene scans every frame,
 * which caused severe FPS drops when e.g. showing "Rake weeds" overlay (issue #63).
 */
public class GameObjectHighlighter {
    private final Client client;
    private final EasyFarmingPlugin plugin;

    /** Cache of objectId -> list of game objects in current scene. */
    private final Map<Integer, List<GameObject>> objectCache = new HashMap<>();
    /** Scene key when cache was built: regionId * 10 + plane. */
    private int lastSceneKey = -1;

    @Inject
    public GameObjectHighlighter(Client client, EasyFarmingPlugin plugin) {
        this.client = client;
        this.plugin = plugin;
    }

    /**
     * Finds all game objects with the specified ID in the current scene.
     * Results are cached per scene (region + plane); cache is cleared when the player
     * changes region or plane so we do not scan the full 104x104 scene every frame.
     */
    public List<GameObject> findGameObjectsByID(int objectID) {
        if (client.getLocalPlayer() == null) {
            return new ArrayList<>();
        }
        WorldView wv = client.getTopLevelWorldView();
        if (wv == null || wv.getScene() == null) {
            return new ArrayList<>();
        }
        int plane = wv.getPlane();
        int regionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        int sceneKey = regionId * 10 + plane;

        if (sceneKey != lastSceneKey) {
            objectCache.clear();
            lastSceneKey = sceneKey;
        }

        List<GameObject> cached = objectCache.get(objectID);
        if (cached != null) {
            return cached;
        }

        List<GameObject> gameObjects = new ArrayList<>();
        Tile[][][] tiles = wv.getScene().getTiles();
        for (int x = 0; x < Constants.SCENE_SIZE; x++) {
            for (int y = 0; y < Constants.SCENE_SIZE; y++) {
                Tile tile = tiles[plane][x][y];
                if (tile == null) {
                    continue;
                }
                for (GameObject gameObject : tile.getGameObjects()) {
                    if (gameObject != null && objectIdMatches(gameObject.getId(), objectID)) {
                        gameObjects.add(gameObject);
                    }
                }
            }
        }
        objectCache.put(objectID, gameObjects);
        return gameObjects;
    }

    /**
     * Returns true if the scene object id matches the target (including impostor/resolved composition).
     * Farming patches can use different scene IDs depending on state; composition id matches the base object.
     */
    private boolean objectIdMatches(int sceneId, int targetId) {
        if (sceneId == targetId) {
            return true;
        }
        try {
            ObjectComposition comp = client.getObjectDefinition(sceneId);
            if (comp == null) {
                return false;
            }
            while (comp.getImpostor() != null) {
                comp = comp.getImpostor();
            }
            return comp.getId() == targetId;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Draws the clickbox for a game object.
     * Handles null/invalid objects gracefully (e.g. when patch is transitioning).
     */
    public void drawGameObjectClickbox(Graphics2D graphics, GameObject gameObject, Color color) {
        if (gameObject == null) {
            return;
        }
        try {
            Shape objectClickbox = gameObject.getClickbox();
            if (objectClickbox != null) {
                graphics.setColor(color);
                graphics.draw(objectClickbox);
                graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() / 5));
                graphics.fill(objectClickbox);
            }
        } catch (Exception e) {
            // Ignore errors when object is transitioning (e.g. patch just cleared)
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
