package com.easyfarming.overlays.highlighting;

import com.easyfarming.EasyFarmingOverlay;
import com.easyfarming.utils.Constants;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.client.ui.overlay.Overlay;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;

/**
 * Handles highlighting of farming patches (herb, flower, tree, fruit tree).
 */
public class PatchHighlighter {
    private final Client client;
    private final EasyFarmingOverlay farmingHelperOverlay;
    private final GameObjectHighlighter gameObjectHighlighter;
    
    @Inject
    public PatchHighlighter(Client client, EasyFarmingOverlay farmingHelperOverlay, GameObjectHighlighter gameObjectHighlighter) {
        this.client = client;
        this.farmingHelperOverlay = farmingHelperOverlay;
        this.gameObjectHighlighter = gameObjectHighlighter;
    }
    
    public void highlightHerbPatches(Graphics2D graphics, Color color) {
        for (Integer patchId : farmingHelperOverlay.getHerbPatchIds()) {
            gameObjectHighlighter.highlightGameObject(patchId, color).render(graphics);
        }
    }
    
    public void highlightFlowerPatches(Graphics2D graphics, Color color) {
        for (Integer patchId : farmingHelperOverlay.getFlowerPatchIds()) {
            gameObjectHighlighter.highlightGameObject(patchId, color).render(graphics);
        }
    }
    
    public void highlightTreePatches(Graphics2D graphics, Color color) {
        for (Integer patchId : farmingHelperOverlay.getTreePatchIds()) {
            gameObjectHighlighter.highlightGameObject(patchId, color).render(graphics);
        }
    }
    
    public void highlightFruitTreePatches(Graphics2D graphics, Color color) {
        for (Integer patchId : farmingHelperOverlay.getFruitTreePatchIds()) {
            gameObjectHighlighter.highlightGameObject(patchId, color).render(graphics);
        }
    }
    
    /**
     * Highlights farming patches for a specific location.
     * @param locationName The name of the location
     * @param graphics Graphics context for highlighting
     * @param herbRun Whether this is a herb run
     * @param treeRun Whether this is a tree run
     * @param fruitTreeRun Whether this is a fruit tree run
     * @param leftClickColor Color for left-click highlights
     * @param useItemColor Color for use-item highlights
     */
    public void highlightFarmingPatchesForLocation(String locationName, Graphics2D graphics, 
                                                   boolean herbRun, boolean treeRun, boolean fruitTreeRun,
                                                   Color leftClickColor, Color useItemColor) {
        // Herb locations
        if (herbRun && (locationName.equals("Ardougne") || locationName.equals("Catherby") || 
                       locationName.equals("Falador") || locationName.equals("Farming Guild") ||
                       locationName.equals("Harmony Island") || locationName.equals("Kourend") ||
                       locationName.equals("Morytania") || locationName.equals("Troll Stronghold") ||
                       locationName.equals("Weiss") || locationName.equals("Civitas illa Fortis"))) {
            highlightHerbPatches(graphics, leftClickColor);
        }
        
        // Tree locations
        if (treeRun && (locationName.equals("Falador") || locationName.equals("Farming Guild") ||
                       locationName.equals("Gnome Stronghold") || locationName.equals("Lumbridge") ||
                       locationName.equals("Taverley") || locationName.equals("Varrock"))) {
            highlightTreePatches(graphics, leftClickColor);
        }
        
        // Fruit tree locations
        if (fruitTreeRun && (locationName.equals("Brimhaven") || locationName.equals("Catherby") ||
                            locationName.equals("Farming Guild") || locationName.equals("Gnome Stronghold") ||
                            locationName.equals("Lletya") || locationName.equals("Tree Gnome Village"))) {
            highlightFruitTreePatches(graphics, leftClickColor);
        }
    }
}

