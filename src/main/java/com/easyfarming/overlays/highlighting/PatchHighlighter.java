package com.easyfarming.overlays.highlighting;

import com.easyfarming.EasyFarmingOverlay;
import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.runelite.farming.FarmingPatch;
import com.easyfarming.runelite.farming.PatchImplementation;
import net.runelite.api.Client;

import javax.inject.Inject;
import java.awt.*;
import java.util.Set;

/**
 * Handles highlighting of farming patches (herb, flower, tree, fruit tree).
 */
public class PatchHighlighter {
    private final Client client;
    private final EasyFarmingOverlay farmingHelperOverlay;
    private final GameObjectHighlighter gameObjectHighlighter;
    private final EasyFarmingPlugin plugin;
    
    @Inject
    public PatchHighlighter(Client client, EasyFarmingOverlay farmingHelperOverlay, GameObjectHighlighter gameObjectHighlighter, EasyFarmingPlugin plugin) {
        this.client = client;
        this.farmingHelperOverlay = farmingHelperOverlay;
        this.gameObjectHighlighter = gameObjectHighlighter;
        this.plugin = plugin;
    }
    
    private void highlightPatchesByImplementation(Graphics2D graphics, Color color, PatchImplementation... implementations) {
        if (plugin.getFarmingWorld() == null) return;
        
        for (Set<FarmingPatch> patchSet : plugin.getFarmingWorld().getTabs().values()) {
            for (FarmingPatch patch : patchSet) {
                boolean match = false;
                for (PatchImplementation impl : implementations) {
                    if (patch.getImplementation() == impl) {
                        match = true;
                        break;
                    }
                }
                
                if (match) {
                    gameObjectHighlighter.renderGameObjectByVarbit(graphics, patch.getVarbit(), color);
                }
            }
        }
    }
    
    public void highlightHerbPatches(Graphics2D graphics, Color color) {
        highlightPatchesByImplementation(graphics, color, PatchImplementation.HERB, PatchImplementation.BELLADONNA);
    }
    
    public void highlightFlowerPatches(Graphics2D graphics, Color color) {
        highlightPatchesByImplementation(graphics, color, PatchImplementation.FLOWER);
    }
    
    public void highlightAllotmentPatches(Graphics2D graphics, Color color) {
        highlightPatchesByImplementation(graphics, color, PatchImplementation.ALLOTMENT);
    }
    
    public void highlightSpecificAllotmentPatch(Graphics2D graphics, int objectId, Color color) {
        gameObjectHighlighter.highlightGameObject(objectId, color).render(graphics);
    }
    
    public void highlightTreePatches(Graphics2D graphics, Color color) {
        highlightPatchesByImplementation(graphics, color, PatchImplementation.TREE);
    }
    
    public void highlightFruitTreePatches(Graphics2D graphics, Color color) {
        highlightPatchesByImplementation(graphics, color, PatchImplementation.FRUIT_TREE);
    }
    
    public void highlightHopsPatches(Graphics2D graphics, Color color) {
        highlightPatchesByImplementation(graphics, color, PatchImplementation.HOPS);
    }
    
    public void highlightSpecificHopsPatch(Graphics2D graphics, int objectId, Color color) {
        gameObjectHighlighter.highlightGameObject(objectId, color).render(graphics);
    }
    
    public void highlightSpecificFlowerPatch(Graphics2D graphics, int objectId, Color color) {
        gameObjectHighlighter.highlightGameObject(objectId, color).render(graphics);
    }
    
    public void highlightFarmingPatchesForLocation(String locationName, Graphics2D graphics,
                                                   Color leftClickColor, Color useItemColor) {
        if (plugin.getFarmingWorld() == null) return;
        
        for (Set<FarmingPatch> patchSet : plugin.getFarmingWorld().getTabs().values()) {
            for (FarmingPatch patch : patchSet) {
                if (patch.getRegion().getName().contains(locationName)) {
                    PatchImplementation impl = patch.getImplementation();
                    boolean shouldHighlight = false;
                    
                    if (plugin.getFarmingTeleportOverlay() != null && plugin.getFarmingTeleportOverlay().getActiveCustomRun() != null) {
                        java.util.List<com.easyfarming.core.Location> enabledLocs = plugin.getFarmingTeleportOverlay().getEnabledLocations();
                        int curLocIdx = plugin.getFarmingTeleportOverlay().getCurrentLocationIndex();
                        if (enabledLocs != null && curLocIdx < enabledLocs.size()) {
                            com.easyfarming.core.Location loc = enabledLocs.get(curLocIdx);
                            if (loc.getName().contains(locationName)) {
                                java.util.List<String> order = loc.getCustomPatchOrder();
                                int curPatchIdx = plugin.getFarmingTeleportOverlay().getCurrentPatchIndex();
                                if (order != null && curPatchIdx < order.size()) {
                                    String curPatch = order.get(curPatchIdx).toLowerCase();
                                    if (curPatch.contains("herb") && (impl == PatchImplementation.HERB || impl == PatchImplementation.BELLADONNA)) shouldHighlight = true;
                                    else if (curPatch.contains("flower") && impl == PatchImplementation.FLOWER) shouldHighlight = true;
                                    else if (curPatch.contains("allotment") && impl == PatchImplementation.ALLOTMENT) shouldHighlight = true;
                                    else if (curPatch.contains("tree") && !curPatch.contains("fruit") && impl == PatchImplementation.TREE) shouldHighlight = true;
                                    else if (curPatch.contains("fruit tree") && impl == PatchImplementation.FRUIT_TREE) shouldHighlight = true;
                                    else if (curPatch.contains("hops") && impl == PatchImplementation.HOPS) shouldHighlight = true;
                                }
                            }
                        }
                    }
                    
                    if (shouldHighlight) {
                        gameObjectHighlighter.renderGameObjectByVarbit(graphics, patch.getVarbit(), leftClickColor);
                    }
                }
            }
        }
    }
}
