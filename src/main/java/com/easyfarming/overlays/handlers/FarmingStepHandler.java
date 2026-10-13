package com.easyfarming.overlays.handlers;

import com.easyfarming.*;
import com.easyfarming.core.Teleport;
import com.easyfarming.customrun.PatchTypes;
import com.easyfarming.locations.RegionLocationRegistry;
import com.easyfarming.overlays.handlers.patch.AllotmentPatchStep;
import com.easyfarming.overlays.handlers.patch.FlowerPatchStep;
import com.easyfarming.overlays.handlers.patch.FruitTreePatchStep;
import com.easyfarming.overlays.handlers.patch.HerbPatchStep;
import com.easyfarming.overlays.handlers.patch.HopsPatchStep;
import com.easyfarming.overlays.handlers.patch.PatchFactory;
import com.easyfarming.overlays.handlers.patch.PatchStepBundle;
import com.easyfarming.overlays.handlers.patch.PatchStepHost;
import com.easyfarming.overlays.handlers.patch.TreePatchStep;
import com.easyfarming.overlays.highlighting.*;
import com.easyfarming.overlays.utils.ColorProvider;
import com.easyfarming.overlays.utils.PatchStateChecker;
import net.runelite.api.Client;
import net.runelite.api.ObjectComposition;
import net.runelite.api.coords.WorldPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.util.List;

/**
 * Handles farming step logic for herb, flower, tree, and fruit tree patches.
 */
@Singleton
public class FarmingStepHandler implements PatchStepHost {
    private static final Logger log = LoggerFactory.getLogger(FarmingStepHandler.class);

    /** Hint arrow toward the current patch tile when farther than this (tiles); does not gate instructions. */
    private static final int PATCH_HINT_ARROW_RANGE_TILES = 15;

    private final Client client;
    private final EasyFarmingConfig config;
    private final AreaCheck areaCheck;
    private final CustomRunPatchProgress patchProgress;
    private final HerbPatchStep herbPatchStep;
    private final HopsPatchStep hopsPatchStep;
    private final FlowerPatchStep flowerPatchStep;
    private final AllotmentPatchStep allotmentPatchStep;
    private final TreePatchStep treePatchStep;
    private final FruitTreePatchStep fruitTreePatchStep;
    
    @Inject
    public FarmingStepHandler(Client client, EasyFarmingPlugin plugin, EasyFarmingConfig config,
                              AreaCheck areaCheck,
                              PatchHighlighter patchHighlighter,
                              ItemHighlighter itemHighlighter, CompostHighlighter compostHighlighter,
                              FarmerHighlighter farmerHighlighter, PatchStateChecker patchStateChecker,
                              ColorProvider colorProvider, EasyFarmingOverlay farmingHelperOverlay,
                              GameObjectHighlighter gameObjectHighlighter) {
        this.client = client;
        this.config = config;
        this.areaCheck = areaCheck;
        this.patchProgress = new CustomRunPatchProgress();
        PatchStepBundle steps = PatchFactory.create(
                client, plugin, config, farmingHelperOverlay, patchHighlighter, itemHighlighter,
                compostHighlighter, farmerHighlighter, gameObjectHighlighter, patchStateChecker,
                colorProvider, patchProgress, this);
        this.herbPatchStep = steps.getHerb();
        this.hopsPatchStep = steps.getHops();
        this.flowerPatchStep = steps.getFlower();
        this.allotmentPatchStep = steps.getAllotment();
        this.treePatchStep = steps.getTree();
        this.fruitTreePatchStep = steps.getFruitTree();
    }
    
    /**
     * Handles herb patch farming steps.
     */
    public void herbSteps(Graphics2D graphics, Teleport teleport) {
        herbPatchStep.run(graphics, teleport);
    }
    
    /**
     * Handles hops patch farming steps.
     */
    public void hopsSteps(Graphics2D graphics, Teleport teleport) {
        hopsPatchStep.run(graphics, teleport);
    }
    
    /**
     * Gets the varbit ID for a hops patch by checking the object composition.
     * @param objectId The object ID of the hops patch
     * @return The varbit ID, or -1 if not found
     */
    @Override
    public int getHopsPatchVarbitId(Integer objectId) {
        if (objectId == null || objectId == -1) {
            return -1;
        }
        ObjectComposition objectComposition = client.getObjectDefinition(objectId);
        if (objectComposition != null) {
            return objectComposition.getVarbitId();
        }
        return -1;
    }
    
    /**
     * Handles flower patch farming steps.
     */
    public void flowerSteps(Graphics2D graphics, boolean farmLimps) {
        flowerPatchStep.run(graphics, farmLimps);
    }
    
    /**
     * Gets the varbit ID for an allotment patch by checking the object composition.
     * @param objectId The object ID of the allotment patch
     * @return The varbit ID, or -1 if not found
     */
    @Override
    public int getAllotmentPatchVarbitId(int objectId) {
        if (objectId == -1) {
            return -1;
        }
        ObjectComposition objectComposition = client.getObjectDefinition(objectId);
        if (objectComposition != null) {
            return objectComposition.getVarbitId();
        }
        return -1;
    }
    
    /**
     * Gets the varbit ID for a herb patch by checking the object composition.
     * @param objectId The object ID of the herb patch
     * @return The varbit ID, or -1 if not found
     */
    @Override
    public int getHerbPatchVarbitId(Integer objectId) {
        if (objectId == null || objectId == -1) {
            return -1;
        }
        ObjectComposition objectComposition = client.getObjectDefinition(objectId);
        if (objectComposition != null) {
            return objectComposition.getVarbitId();
        }
        return -1;
    }
    
    /**
     * Gets the varbit ID for a flower patch by checking the object composition.
     * @param objectId The object ID of the flower patch
     * @return The varbit ID, or -1 if not found
     */
    @Override
    public int getFlowerPatchVarbitId(Integer objectId) {
        if (objectId == null || objectId == -1) {
            return -1;
        }
        ObjectComposition objectComposition = client.getObjectDefinition(objectId);
        if (objectComposition != null) {
            return objectComposition.getVarbitId();
        }
        return -1;
    }
    
    /**
     * Gets the varbit ID for a fruit tree patch by checking the object composition.
     * @param objectId The object ID of the fruit tree patch
     * @return The varbit ID, or -1 if not found
     */
    @Override
    public int getFruitTreePatchVarbitId(Integer objectId) {
        if (objectId == null || objectId == -1) {
            return -1;
        }
        ObjectComposition objectComposition = client.getObjectDefinition(objectId);
        if (objectComposition != null) {
            return objectComposition.getVarbitId();
        }
        return -1;
    }
    
    /**
     * Handles allotment patch farming steps.
     * Calls north patch handler first, then south patch handler when north is done.
     */
    public void allotmentSteps(Graphics2D graphics, Teleport teleport) {
        allotmentPatchStep.run(graphics, teleport);
    }
    
    /**
     * Handles tree patch farming steps.
     */
    public void treeSteps(Graphics2D graphics, Teleport teleport) {
        treePatchStep.run(graphics, teleport);
    }
    
    /**
     * Handles fruit tree patch farming steps.
     */
    public void fruitTreeSteps(Graphics2D graphics, Teleport teleport) {
        fruitTreePatchStep.run(graphics, teleport);
    }

    /**
     * Points the hint arrow at the patch when the player is farther than {@link #PATCH_HINT_ARROW_RANGE_TILES}
     * from the patch anchor. Does not change instruction text. NPC/farmer arrows set in the same frame may be
     * replaced only while still outside that range (direction toward the patch).
     */
    @Override
    public void applyPatchDirectionHintArrow(WorldPoint patchWorldPoint) {
        if (patchWorldPoint == null || client.getLocalPlayer() == null) {
            return;
        }
        if (areaCheck.isPlayerWithinArea(patchWorldPoint, PATCH_HINT_ARROW_RANGE_TILES)) {
            return;
        }
        setHintArrow(patchWorldPoint);
    }

    /** Herb patch anchor (same tile basis as {@link com.easyfarming.FarmingTeleportOverlay#getHerbPatchPoint}). */
    @Override
    public WorldPoint getHerbPatchWorldPoint(String locationName) {
        if (locationName == null) {
            return null;
        }
        switch (locationName) {
            case "Ardougne":
                return new WorldPoint(2670, 3374, 0);
            case "Catherby":
                return new WorldPoint(2813, 3463, 0);
            case "Falador":
                return new WorldPoint(3058, 3307, 0);
            case "Farming Guild":
                return new WorldPoint(1238, 3726, 0);
            case "Harmony Island":
                return new WorldPoint(3789, 2837, 0);
            case "Kourend":
                return new WorldPoint(1738, 3550, 0);
            case "Morytania":
                return new WorldPoint(3601, 3525, 0);
            case "Troll Stronghold":
                return new WorldPoint(2824, 3696, 0);
            case "Weiss":
                return new WorldPoint(2847, 3931, 0);
            case "Civitas illa Fortis":
                return new WorldPoint(1586, 3099, 0);
            default:
                return null;
        }
    }

    @Override
    public WorldPoint getFlowerPatchWorldPoint(String locationName) {
        return getHerbPatchWorldPoint(locationName);
    }

    @Override
    public WorldPoint getTreePatchWorldPoint(String locationName) {
        if (locationName == null) {
            return null;
        }
        switch (locationName) {
            case "Falador":
                return new WorldPoint(3000, 3373, 0);
            case "Farming Guild":
                return new WorldPoint(1232, 3736, 0);
            case "Gnome Stronghold":
                return new WorldPoint(2436, 3415, 0);
            case "Lumbridge":
                return new WorldPoint(3193, 3231, 0);
            case "Taverley":
                return new WorldPoint(2936, 3438, 0);
            case "Varrock":
                return new WorldPoint(3229, 3459, 0);
            case "Nemus Retreat":
                return new WorldPoint(1366, 3321, 0);
            default:
                return null;
        }
    }

    /**
     * Maps the player's region to a tree-run location name.
     * Package-visible for unit tests; delegates to {@link RegionLocationRegistry}.
     */
    static String getTreeLocationNameFromRegionId(int regionId) {
        return RegionLocationRegistry.getTreeLocationNameFromRegionId(regionId);
    }

    @Override
    public WorldPoint getFruitTreePatchWorldPoint(String locationName) {
        if (locationName == null) {
            return null;
        }
        switch (locationName) {
            case "Brimhaven":
                return new WorldPoint(2764, 3212, 0);
            case "Catherby":
                return new WorldPoint(2860, 3433, 0);
            case "Farming Guild":
                return new WorldPoint(1243, 3759, 0);
            case "Gnome Stronghold":
                return new WorldPoint(2475, 3446, 0);
            case "Lletya":
                return new WorldPoint(2346, 3162, 0);
            case "Tree Gnome Village":
                return new WorldPoint(2490, 3180, 0);
            case "Kastori":
                return new WorldPoint(1350, 3057, 0);
            default:
                return null;
        }
    }

    @Override
    public WorldPoint getHopsPatchWorldPoint(String locationName) {
        if (locationName == null || "Unknown".equals(locationName)) {
            return null;
        }
        switch (locationName) {
            case "Aldarin":
                return new WorldPoint(1365, 2939, 0);
            case "Entrana":
                return new WorldPoint(2811, 3337, 0);
            case "Lumbridge":
                return new WorldPoint(3229, 3315, 0);
            case "Seers Village":
                return new WorldPoint(2667, 3526, 0);
            case "Yanille":
                return new WorldPoint(2576, 3105, 0);
            default:
                return null;
        }
    }
    
    /**
     * Sets a hint arrow pointing to the specified WorldPoint.
     * @param point The WorldPoint to point the hint arrow to
     */
    private void setHintArrow(WorldPoint point) {
        if (point != null && client != null) {
            try {
                client.setHintArrow(point);
            } catch (Exception e) {
                log.debug("Failed to set hint arrow", e);
            }
        }
    }
    
    /**
     * Sets a hint arrow pointing to an NPC by name.
     * @param npcName The name of the NPC to point the hint arrow to
     */
    @Override
    public void setHintArrowToNPC(String npcName) {
        if (npcName == null || client == null) {
            return;
        }
        
        try {
            net.runelite.api.IndexedObjectSet<? extends net.runelite.api.NPC> npcs = client.getTopLevelWorldView().npcs();
            if (npcs != null) {
                for (net.runelite.api.NPC npc : npcs) {
                    if (npc != null && npc.getName() != null && npc.getName().equals(npcName)) {
                        client.setHintArrow(npc);
                        return; // Found the NPC, set arrow and return
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Failed to set hint arrow", e);
        }
    }
    
    /**
     * Sets hint arrow to the first available NPC from a list of NPC names.
     * @param npcNames List of NPC names to search for
     */
    @Override
    public void setHintArrowToFirstAvailableNPC(List<String> npcNames) {
        if (client != null && npcNames != null) {
            try {
                net.runelite.api.IndexedObjectSet<? extends net.runelite.api.NPC> npcs = client.getTopLevelWorldView().npcs();
                if (npcs != null) {
                    for (String npcName : npcNames) {
                        for (net.runelite.api.NPC npc : npcs) {
                            if (npc != null && npc.getName() != null && npc.getName().equals(npcName)) {
                                client.setHintArrow(npc);
                                return; // Found an NPC, set arrow and return
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.debug("Failed to set hint arrow", e);
            }
        }
        // If no NPC found, clear any existing hint arrow
        if (client != null) {
            client.clearHintArrow();
        }
    }
    
    /**
     * Clears the hint arrow using the client's clearHintArrow method.
     * Public method to allow clearing from other classes (e.g., when overlay is removed).
     */
    @Override
    public void clearHintArrow() {
        client.clearHintArrow();
    }

    @Override
    public boolean payForProtection() {
        return config.generalPayForProtection();
    }
    
    public boolean isHerbPatchDone() {
        return patchProgress.isHerbPatchDone();
    }

    public void setHerbPatchDone(boolean done) {
        patchProgress.setHerbPatchDone(done);
    }

    public boolean isFlowerPatchDone() {
        return patchProgress.isFlowerPatchDone();
    }

    public void setFlowerPatchDone(boolean done) {
        patchProgress.setFlowerPatchDone(done);
    }

    public boolean isAllotmentPatchDone() {
        return patchProgress.isAllotmentPatchDone();
    }

    public void setAllotmentPatchDone(boolean done) {
        patchProgress.setAllotmentPatchDone(done);
    }

    public boolean isTreePatchDone() {
        return patchProgress.isTreePatchDone();
    }

    public void setTreePatchDone(boolean done) {
        patchProgress.setTreePatchDone(done);
    }

    public boolean isFruitTreePatchDone() {
        return patchProgress.isFruitTreePatchDone();
    }

    public void setFruitTreePatchDone(boolean done) {
        patchProgress.setFruitTreePatchDone(done);
    }

    public boolean isHopsPatchDone() {
        return patchProgress.isHopsPatchDone();
    }

    public void setHopsPatchDone(boolean done) {
        patchProgress.setHopsPatchDone(done);
    }

    /**
     * Clears all patch-type done flags.
     */
    public void resetAllPatchDoneFlags() {
        patchProgress.resetAllPatchDoneFlags();
    }

    /**
     * Resets all persistent compost states (called when moving to next location).
     */
    public void resetCompostStates() {
        patchProgress.resetCompostStates();
    }

    /**
     * Clears done flags and sticky compost states when advancing patch type or location.
     */
    public void resetForNextLocation() {
        patchProgress.resetForNextLocation();
    }

    /**
     * Force-advances past the active patch type in a custom run by marking only that type done.
     * The next {@link com.easyfarming.FarmingTeleportOverlay#render} routes to the next patch /
     * location via the normal completion flow. Used by "Skip current step" while already at the
     * destination. Navigation skips are handled in the overlay (force-arrive) so they do not
     * consume the current patch. Resets sticky compost / allotment state and clears the hint arrow.
     * {@code lastMessage} is cleared by {@link EasyFarmingPlugin#skipCurrentStep()}.
     *
     * @param patchType current custom-run patch type key ({@link com.easyfarming.customrun.PatchTypes}), or null
     */
    public void forceAdvancePatchType(String patchType) {
        if (patchType == null) {
            clearHintArrow();
            return;
        }
        switch (patchType) {
            case PatchTypes.HERB:
                patchProgress.setHerbPatchDone(true);
                break;
            case PatchTypes.FLOWER:
                patchProgress.setFlowerPatchDone(true);
                break;
            case PatchTypes.ALLOTMENT:
                patchProgress.setAllotmentPatchDone(true);
                patchProgress.resetAllotmentState();
                break;
            case PatchTypes.TREE:
                patchProgress.setTreePatchDone(true);
                break;
            case PatchTypes.FRUIT_TREE:
                patchProgress.setFruitTreePatchDone(true);
                break;
            case PatchTypes.HOPS:
                patchProgress.setHopsPatchDone(true);
                break;
            default:
                break;
        }
        resetCompostStates();
        clearHintArrow();
    }
}

