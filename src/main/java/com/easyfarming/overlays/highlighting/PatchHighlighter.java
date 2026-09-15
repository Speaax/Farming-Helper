package com.easyfarming.overlays.highlighting;

import com.easyfarming.EasyFarmingOverlay;
import com.easyfarming.customrun.PatchTypes;
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
            gameObjectHighlighter.renderGameObjectHighlight(graphics, patchId, color);
        }
    }

    /**
     * Highlights a specific herb patch by object ID (for current location).
     */
    public void highlightSpecificHerbPatch(Graphics2D graphics, int objectId, Color color) {
        gameObjectHighlighter.renderGameObjectHighlight(graphics, objectId, color);
    }

    public void highlightFlowerPatches(Graphics2D graphics, Color color) {
        for (Integer patchId : farmingHelperOverlay.getFlowerPatchIds()) {
            gameObjectHighlighter.renderGameObjectHighlight(graphics, patchId, color);
        }
    }
    
    public void highlightAllotmentPatches(Graphics2D graphics, Color color) {
        for (List<Integer> patchIds : Constants.ALLOTMENT_PATCH_IDS_BY_LOCATION.values()) {
            for (Integer patchId : patchIds) {
                if (patchId == null) continue;
                gameObjectHighlighter.renderGameObjectHighlight(graphics, patchId, color);
            }
        }
    }

    /**
     * Highlights only the allotment patch object IDs for {@code locationName} (north/south where applicable).
     */
    public void highlightAllotmentPatchesForLocation(Graphics2D graphics, String locationName, Color color) {
        List<Integer> patchIds = Constants.ALLOTMENT_PATCH_IDS_BY_LOCATION.get(locationName);
        if (patchIds == null) {
            return;
        }
        for (Integer patchId : patchIds) {
            if (patchId == null) {
                continue;
            }
            gameObjectHighlighter.renderGameObjectHighlight(graphics, patchId, color);
        }
    }
    
    /**
     * Highlights a specific allotment patch by object ID.
     * @param graphics Graphics context
     * @param objectId The object ID of the specific patch to highlight
     * @param color The color to use for highlighting
     */
    public void highlightSpecificAllotmentPatch(Graphics2D graphics, int objectId, Color color) {
        gameObjectHighlighter.renderGameObjectHighlight(graphics, objectId, color);
    }
    
    public void highlightTreePatches(Graphics2D graphics, Color color) {
        for (Integer patchId : farmingHelperOverlay.getTreePatchIds()) {
            gameObjectHighlighter.renderGameObjectHighlight(graphics, patchId, color);
        }
    }

    public void highlightSpecificTreePatch(Graphics2D graphics, int objectId, Color color) {
        gameObjectHighlighter.renderGameObjectHighlight(graphics, objectId, color);
    }
    
    public void highlightFruitTreePatches(Graphics2D graphics, Color color) {
        for (Integer patchId : farmingHelperOverlay.getFruitTreePatchIds()) {
            gameObjectHighlighter.renderGameObjectHighlight(graphics, patchId, color);
        }
    }

    public void highlightSpecificFruitTreePatch(Graphics2D graphics, int objectId, Color color) {
        gameObjectHighlighter.renderGameObjectHighlight(graphics, objectId, color);
    }
    
    public void highlightHopsPatches(Graphics2D graphics, Color color) {
        for (Integer patchId : farmingHelperOverlay.getHopsPatchIds()) {
            gameObjectHighlighter.renderGameObjectHighlight(graphics, patchId, color);
        }
    }
    
    /**
     * Highlights a specific hops patch by object ID.
     * @param graphics Graphics context
     * @param objectId The object ID of the specific patch to highlight
     * @param color The color to use for highlighting
     */
    public void highlightSpecificHopsPatch(Graphics2D graphics, int objectId, Color color) {
        gameObjectHighlighter.renderGameObjectHighlight(graphics, objectId, color);
    }
    
    /**
     * Highlights a specific flower patch by object ID.
     * @param graphics Graphics context
     * @param objectId The object ID of the specific patch to highlight
     * @param color The color to use for highlighting
     */
    public void highlightSpecificFlowerPatch(Graphics2D graphics, int objectId, Color color) {
        gameObjectHighlighter.renderGameObjectHighlight(graphics, objectId, color);
    }

    private void highlightPatchForLocation(String locationName, String patchType, Graphics2D graphics, Color color) {
        Integer patchId = null;
        switch (patchType) {
            case PatchTypes.TREE:
                patchId = farmingHelperOverlay.getTreePatchIdForLocation(locationName);
                break;
            case PatchTypes.FRUIT_TREE:
                patchId = farmingHelperOverlay.getFruitTreePatchIdForLocation(locationName);
                break;
            case PatchTypes.HOPS:
                patchId = farmingHelperOverlay.getHopsPatchIdForLocation(locationName);
                break;
            default:
                break;
        }
        if (patchId != null) {
            gameObjectHighlighter.renderGameObjectHighlight(graphics, patchId, color);
            return;
        }
        switch (patchType) {
            case PatchTypes.TREE:
                highlightTreePatches(graphics, color);
                break;
            case PatchTypes.FRUIT_TREE:
                highlightFruitTreePatches(graphics, color);
                break;
            case PatchTypes.HOPS:
                highlightHopsPatches(graphics, color);
                break;
            default:
                break;
        }
    }
    
    /**
     * Highlights farming patches for a specific location based on patch type.
     * @param locationName The name of the location
     * @param graphics Graphics context for highlighting
     * @param patchType One of PatchTypes.HERB, FLOWER, ALLOTMENT, TREE, FRUIT_TREE, HOPS
     * @param leftClickColor Color for left-click highlights
     * @param useItemColor Color for use-item highlights
     */
    public void highlightFarmingPatchesForLocation(String locationName, Graphics2D graphics,
                                                   String patchType,
                                                   Color leftClickColor, Color useItemColor) {
        if (patchType == null) {
            return;
        }
        switch (patchType) {
            case PatchTypes.HERB:
                if (isHerbLocation(locationName)) {
                    highlightHerbPatches(graphics, leftClickColor);
                }
                break;
            case PatchTypes.FLOWER:
                if (isHerbLocation(locationName)) {
                    highlightFlowerPatches(graphics, leftClickColor);
                }
                break;
            case PatchTypes.ALLOTMENT:
                if (isHerbLocation(locationName)) {
                    highlightAllotmentPatchesForLocation(graphics, locationName, leftClickColor);
                }
                break;
            case PatchTypes.TREE:
                if (isTreeLocation(locationName)) {
                    highlightPatchForLocation(locationName, patchType, graphics, leftClickColor);
                }
                break;
            case PatchTypes.FRUIT_TREE:
                if (isFruitTreeLocation(locationName)) {
                    highlightPatchForLocation(locationName, patchType, graphics, leftClickColor);
                }
                break;
            case PatchTypes.HOPS:
                if (isHopsLocation(locationName)) {
                    highlightPatchForLocation(locationName, patchType, graphics, leftClickColor);
                }
                break;
            default:
                break;
        }
    }

    private static boolean isHerbLocation(String locationName) {
        return locationName.equals("Ardougne") || locationName.equals("Catherby")
                || locationName.equals("Falador") || locationName.equals("Farming Guild")
                || locationName.equals("Harmony Island") || locationName.equals("Kourend")
                || locationName.equals("Morytania") || locationName.equals("Troll Stronghold")
                || locationName.equals("Weiss") || locationName.equals("Civitas illa Fortis");
    }

    private static boolean isTreeLocation(String locationName) {
        return locationName.equals("Falador") || locationName.equals("Farming Guild")
                || locationName.equals("Gnome Stronghold") || locationName.equals("Lumbridge")
                || locationName.equals("Nemus Retreat") || locationName.equals("Taverley")
                || locationName.equals("Varrock");
    }

    private static boolean isFruitTreeLocation(String locationName) {
        return locationName.equals("Brimhaven") || locationName.equals("Catherby")
                || locationName.equals("Farming Guild") || locationName.equals("Gnome Stronghold")
                || locationName.equals("Kastori") || locationName.equals("Lletya")
                || locationName.equals("Tree Gnome Village");
    }

    private static boolean isHopsLocation(String locationName) {
        return locationName.equals("Lumbridge") || locationName.equals("Seers Village")
                || locationName.equals("Yanille") || locationName.equals("Entrana")
                || locationName.equals("Aldarin");
    }
}

