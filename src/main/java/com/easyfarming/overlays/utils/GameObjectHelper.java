package com.easyfarming.overlays.utils;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Tile;
import net.runelite.api.WorldView;
import com.easyfarming.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for finding game objects by name or ID.
 */
public class GameObjectHelper {
    private final Client client;
    
    public GameObjectHelper(Client client) {
        this.client = client;
    }
    
    /**
     * Gets game object IDs by name in the current scene.
     */
    public List<Integer> getGameObjectIdsByName(String name) {
        List<Integer> foundObjectIds = new ArrayList<>();
        WorldView top_wv = client.getTopLevelWorldView();
        Tile[][][] tiles = top_wv.getScene().getTiles();
        
        for (int x = 0; x < Constants.SCENE_SIZE; x++) {
            for (int y = 0; y < Constants.SCENE_SIZE; y++) {
                Tile tile = tiles[top_wv.getPlane()][x][y];
                if (tile == null) {
                    continue;
                }
                
                for (GameObject gameObject : tile.getGameObjects()) {
                    if (gameObject != null) {
                        ObjectComposition objectComposition = client.getObjectDefinition(gameObject.getId());
                        if (objectComposition != null && objectComposition.getName().equals(name)) {
                            foundObjectIds.add(gameObject.getId());
                        }
                    }
                }
            }
        }
        
        return foundObjectIds;
    }
}

