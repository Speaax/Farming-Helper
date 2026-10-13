package com.easyfarming.overlays.handlers.patch;

import com.easyfarming.AllotmentPatchChecker;
import com.easyfarming.EasyFarmingOverlay;
import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.core.Teleport;
import com.easyfarming.overlays.highlighting.CompostHighlighter;
import com.easyfarming.overlays.highlighting.GameObjectHighlighter;
import com.easyfarming.overlays.highlighting.ItemHighlighter;
import com.easyfarming.overlays.highlighting.PatchHighlighter;
import com.easyfarming.overlays.utils.ColorProvider;
import com.easyfarming.overlays.utils.PatchStateChecker;
import com.easyfarming.utils.Constants;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.gameval.ItemID;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

/**
 * Allotment patch processing for one frame: north then south patch, highlights, and instruction text.
 * Extracted from {@code FarmingStepHandler.allotmentSteps} / north / south with identical behavior.
 */
public final class AllotmentPatchStep implements PatchStep {

    private final Client client;
    private final EasyFarmingPlugin plugin;
    private final EasyFarmingOverlay farmingHelperOverlay;
    private final PatchHighlighter patchHighlighter;
    private final ItemHighlighter itemHighlighter;
    private final CompostHighlighter compostHighlighter;
    private final GameObjectHighlighter gameObjectHighlighter;
    private final PatchStateChecker patchStateChecker;
    private final ColorProvider colorProvider;
    private final AllotmentPatchStateView allotmentPatchState;
    private final AllotmentProgress progress;
    private final AllotmentStepSupport support;

    public AllotmentPatchStep(
            Client client,
            EasyFarmingPlugin plugin,
            EasyFarmingOverlay farmingHelperOverlay,
            PatchHighlighter patchHighlighter,
            ItemHighlighter itemHighlighter,
            CompostHighlighter compostHighlighter,
            GameObjectHighlighter gameObjectHighlighter,
            PatchStateChecker patchStateChecker,
            ColorProvider colorProvider,
            AllotmentPatchStateView allotmentPatchState,
            AllotmentProgress progress,
            AllotmentStepSupport support) {
        this.client = client;
        this.plugin = plugin;
        this.farmingHelperOverlay = farmingHelperOverlay;
        this.patchHighlighter = patchHighlighter;
        this.itemHighlighter = itemHighlighter;
        this.compostHighlighter = compostHighlighter;
        this.gameObjectHighlighter = gameObjectHighlighter;
        this.patchStateChecker = patchStateChecker;
        this.colorProvider = colorProvider;
        this.allotmentPatchState = allotmentPatchState;
        this.progress = progress;
        this.support = support;
    }

    /**
     * Facade entry matching {@code FarmingStepHandler.allotmentSteps}.
     * Orchestrates north then south patch handling for this frame.
     */
    public void run(Graphics2D graphics, Teleport teleport) {
        if (client.getLocalPlayer() == null) {
            return;
        }
        // Check if this location has allotment patches
        int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        String locationName = PatchLocationResolver.herbLocationName(currentRegionId);
        List<Integer> allotmentPatchIds = farmingHelperOverlay.getAllotmentPatchIdsForLocation(locationName);

        // If this location has no allotment patches, mark as done immediately
        if (allotmentPatchIds == null || allotmentPatchIds.isEmpty()) {
            progress.setDone(true);
            allotmentPatchState.reset();
            return;
        }

        // Handle north patch first
        if (allotmentPatchState.getCurrentIndex() == 0) {
            allotmentNorthSteps(graphics, teleport);
            // If north patch is done (GROWING + composted), move to south patch
            // Once we move to south patch, north patch is completely ignored for this run
            // Only transition if north patch is actually completed (GROWING + composted)
            if (allotmentPatchState.isPatchCompleted(0) && allotmentPatchState.isPatchComposted(0)) {
                allotmentPatchState.moveToNextPatch();
                plugin.clearLastMessage();
                // Don't process south patch in the same frame - let it happen on next frame
                return;
            }
        }

        // Handle south patch if north is done (and we're on index 1)
        // This block only executes when currentAllotmentPatchIndex == 1
        // Once we're here, we never go back to north patch
        if (allotmentPatchState.getCurrentIndex() == 1) {
            allotmentSouthSteps(graphics, teleport);
            // If south patch is done, mark all allotment patches as done
            if (allotmentPatchState.isPatchCompleted(1)) {
                progress.setDone(true);
                // Reset for next location
                allotmentPatchState.reset();
            }
        }
    }

    @Override
    public void process(PatchStepContext context) {
        run(context.getGraphics(), context.getTeleport());
    }

    /**
     * Handles north allotment patch farming steps.
     * Completely separate from south patch handling.
     * Once we move to south patch (index 1), this method should never be called.
     */
    private void allotmentNorthSteps(Graphics2D graphics, Teleport teleport) {
        // Safety check: If we're not on north patch (index 0), return immediately
        // This ensures we never process north patch once we've moved forward
        if (allotmentPatchState.getCurrentIndex() != 0) {
            return;
        }

        int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        Color leftColor = colorProvider.getLeftClickColorWithAlpha();
        Color useItemColor = colorProvider.getHighlightUseItemWithAlpha();

        // Get location name from region ID
        String locationName = PatchLocationResolver.herbLocationName(currentRegionId);

        // Get patch object IDs for this location
        List<Integer> allotmentPatchIds = farmingHelperOverlay.getAllotmentPatchIdsForLocation(locationName);

        // If no patches found for this location, return
        if (allotmentPatchIds.isEmpty() || allotmentPatchIds.get(0) == null) {
            return;
        }

        int patchObjectId = allotmentPatchIds.get(0); // North patch (index 0)

        // Get varbit ID from object composition
        int varbitIdFromObject = support.getAllotmentPatchVarbitId(patchObjectId);
        int varbitId = varbitIdFromObject;

        // Fallback: If object composition fails, use location-specific varbits
        if (varbitId == -1) {
            if (locationName.equals("Catherby")) {
                varbitId = Constants.VARBIT_ALLOTMENT_PATCH_NORTH_A1;
            } else {
                varbitId = Constants.VARBIT_ALLOTMENT_PATCH_NORTH_A2;
            }
        }

        // Check state for north patch
        AllotmentPatchChecker.PlantState plantState = AllotmentPatchChecker.PlantState.UNKNOWN;

        if (varbitId != -1) {
            plantState = AllotmentPatchChecker.checkAllotmentPatch(client, varbitId);
        }

        // Check completion status for north patch
        // HARVESTABLE is NOT completed - user still needs to harvest
        // Only GROWING + composted is considered completed (nothing more to do)
        boolean completed = plantState == AllotmentPatchChecker.PlantState.GROWING && allotmentPatchState.isPatchComposted(0);
        allotmentPatchState.setPatchCompleted(0, completed);

        // Handle early returns in a single place
        if (plantState == AllotmentPatchChecker.PlantState.UNKNOWN) {
            plugin.addTextToInfoBox(PatchInstructionText.allotmentUnknownNorth());
            return;
        }

        // If completed and not HARVESTABLE, return early (no need to show further instructions)
        // This prevents re-highlighting after other game actions
        if (completed && plantState != AllotmentPatchChecker.PlantState.HARVESTABLE) {
            return;
        }

        // Check if patch is visible in scene (more accurate than distance to teleport point)
        List<GameObject> patchObjects = gameObjectHighlighter.findGameObjectsByID(patchObjectId);
        boolean patchVisible = !patchObjects.isEmpty();

        // Handle north patch states
        if (!patchVisible) {
            plugin.addTextToInfoBox(PatchInstructionText.allotmentNavigateNorth());
            patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, leftColor);
        } else {
            // Clear hint arrow when patch is visible (player is near)
            support.clearHintArrow();
            switch (plantState) {
                case HARVESTABLE:
                    plugin.addTextToInfoBox(PatchInstructionText.allotmentHarvestNorth());
                    patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, leftColor);
                    break;
                case PLANT:
                    plugin.addTextToInfoBox(PatchInstructionText.allotmentPlantSeedNorth());
                    patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, useItemColor);
                    itemHighlighter.highlightAllotmentSeeds(graphics);
                    break;
                case DEAD:
                    plugin.addTextToInfoBox(PatchInstructionText.allotmentClearDeadNorth());
                    patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, leftColor);
                    break;
                case DISEASED:
                    plugin.addTextToInfoBox(PatchInstructionText.allotmentPlantCureNorth());
                    patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, leftColor);
                    itemHighlighter.itemHighlight(graphics, ItemID.PLANT_CURE, useItemColor);
                    break;
                case WEEDS:
                    plugin.addTextToInfoBox(PatchInstructionText.allotmentRakeNorth());
                    patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, leftColor);
                    break;
                case GROWING:
                    // Check if compost was just applied (from chat message)
                    if (patchStateChecker.patchIsCompostedForAllotmentPatch()) {
                        // Mark as composted (persistent)
                        allotmentPatchState.markComposted(0);
                        // Clear hint arrow when patch is composted
                        support.clearHintArrow();
                        return;
                    }
                    // Patch is GROWING but not composted yet - show compost instruction
                    plugin.addTextToInfoBox(PatchInstructionText.allotmentCompostNorth());
                    patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, useItemColor);
                    Integer compostId = itemHighlighter.selectedCompostID();
                    if (compostId != null && itemHighlighter.isItemInInventory(compostId)) {
                        itemHighlighter.highlightCompost(graphics, compostId, useItemColor);
                        // Clear hint arrow if compost is in inventory (no NPC interaction needed)
                        support.clearHintArrow();
                    } else {
                        compostHighlighter.withdrawCompost(graphics);
                        // If compost is not in inventory, set hint arrow to Tool Leprechaun
                        if (compostId != null) {
                            support.setHintArrowToNPC(Constants.NPC_TOOL_LEPRECHAUN);
                        }
                    }
                    break;
                case UNKNOWN:
                    plugin.addTextToInfoBox(PatchInstructionText.allotmentUnknownActionNorth());
                    break;
            }
        }
    }

    /**
     * Handles south allotment patch farming steps.
     * Completely separate from north patch handling - only deals with south patch (index 1).
     * North patch is completely ignored once we reach this point.
     */
    private void allotmentSouthSteps(Graphics2D graphics, Teleport teleport) {
        // Safety check: If we're not on south patch (index 1), return immediately
        // This ensures we only process south patch when we're supposed to
        if (allotmentPatchState.getCurrentIndex() != 1) {
            return;
        }

        int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        Color leftColor = colorProvider.getLeftClickColorWithAlpha();
        Color useItemColor = colorProvider.getHighlightUseItemWithAlpha();

        // Get location name from region ID
        String locationName = PatchLocationResolver.herbLocationName(currentRegionId);

        // Get patch object IDs for this location
        List<Integer> allotmentPatchIds = farmingHelperOverlay.getAllotmentPatchIdsForLocation(locationName);

        // If no patches found or south patch doesn't exist, return
        if (allotmentPatchIds.size() < 2 || allotmentPatchIds.get(1) == null) {
            return;
        }

        int patchObjectId = allotmentPatchIds.get(1); // South patch (index 1)

        // Get varbit ID from object composition
        int varbitIdFromObject = support.getAllotmentPatchVarbitId(patchObjectId);
        int varbitId = varbitIdFromObject;

        // Fallback: If object composition fails, use location-specific varbits
        if (varbitId == -1) {
            if (locationName.equals("Catherby")) {
                varbitId = Constants.VARBIT_ALLOTMENT_PATCH_SOUTH_B1;
            } else {
                varbitId = Constants.VARBIT_ALLOTMENT_PATCH_SOUTH_B2;
            }
        }

        // Check state for south patch
        AllotmentPatchChecker.PlantState plantState = AllotmentPatchChecker.PlantState.UNKNOWN;

        if (varbitId != -1) {
            plantState = AllotmentPatchChecker.checkAllotmentPatch(client, varbitId);
        }

        // Check completion status for south patch
        // HARVESTABLE is NOT completed - user still needs to harvest
        // Only GROWING + composted is considered completed (nothing more to do)
        // Don't mark as completed if it's GROWING but not composted yet
        if (!allotmentPatchState.isPatchCompleted(1)) {
            if (plantState == AllotmentPatchChecker.PlantState.GROWING && allotmentPatchState.isPatchComposted(1)) {
                allotmentPatchState.setPatchCompleted(1, true);
            }
        }

        // If patch is GROWING and already composted, return early (no need to show compost instruction)
        // This prevents re-highlighting after other game actions
        if (plantState == AllotmentPatchChecker.PlantState.GROWING && allotmentPatchState.isPatchComposted(1)) {
            if (!allotmentPatchState.isPatchCompleted(1)) {
                allotmentPatchState.setPatchCompleted(1, true);
            }
            return; // Don't show compost instruction if already composted
        }

        // If patch is done (GROWING + composted) and not HARVESTABLE, return (transition handled by allotmentSteps)
        // Don't return early for HARVESTABLE - user still needs to harvest
        // Don't return early for GROWING if not composted - user still needs to compost
        if (allotmentPatchState.isPatchCompleted(1) &&
            allotmentPatchState.isPatchComposted(1) &&
            plantState != AllotmentPatchChecker.PlantState.HARVESTABLE &&
            plantState != AllotmentPatchChecker.PlantState.GROWING) {
            return;
        }

        // If state is unknown, show message and return
        if (plantState == AllotmentPatchChecker.PlantState.UNKNOWN) {
            plugin.addTextToInfoBox(PatchInstructionText.allotmentUnknownSouth());
            return;
        }

        // Check if patch is visible in scene (more accurate than distance to teleport point)
        List<GameObject> patchObjects = gameObjectHighlighter.findGameObjectsByID(patchObjectId);
        boolean patchVisible = !patchObjects.isEmpty();

        // Handle south patch states
        if (!patchVisible) {
            plugin.addTextToInfoBox(PatchInstructionText.allotmentNavigateSouth());
            patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, leftColor);
        } else {
            // Clear hint arrow when patch is visible (player is near)
            support.clearHintArrow();
            switch (plantState) {
                case HARVESTABLE:
                    plugin.addTextToInfoBox(PatchInstructionText.allotmentHarvestSouth());
                    patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, leftColor);
                    break;
                case PLANT:
                    plugin.addTextToInfoBox(PatchInstructionText.allotmentPlantSeedSouth());
                    patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, useItemColor);
                    itemHighlighter.highlightAllotmentSeeds(graphics);
                    break;
                case DEAD:
                    plugin.addTextToInfoBox(PatchInstructionText.allotmentClearDeadSouth());
                    patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, leftColor);
                    break;
                case DISEASED:
                    plugin.addTextToInfoBox(PatchInstructionText.allotmentPlantCureSouth());
                    patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, leftColor);
                    itemHighlighter.itemHighlight(graphics, ItemID.PLANT_CURE, useItemColor);
                    break;
                case WEEDS:
                    plugin.addTextToInfoBox(PatchInstructionText.allotmentRakeSouth());
                    patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, leftColor);
                    break;
                case GROWING:
                    // This case should only be reached if the patch is GROWING and NOT already composted
                    // (the early return above should catch GROWING + composted cases)
                    // Check if compost was just applied (from chat message)
                    boolean isComposted = patchStateChecker.patchIsCompostedForAllotmentPatch();
                    if (isComposted) {
                        // Mark as composted (persistent)
                        allotmentPatchState.markComposted(1);
                        // Clear hint arrow when patch is composted
                        support.clearHintArrow();
                        return;
                    }
                    // Safety check: If already composted (shouldn't reach here due to early return, but just in case)
                    if (allotmentPatchState.isPatchComposted(1)) {
                        // Patch is already composted, mark as completed and return
                        if (!allotmentPatchState.isPatchCompleted(1)) {
                            allotmentPatchState.setPatchCompleted(1, true);
                        }
                        return; // Don't show compost instruction if already composted
                    }
                    // Patch is GROWING but not composted yet - show compost instruction
                    plugin.addTextToInfoBox(PatchInstructionText.allotmentCompostSouth());
                    patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, useItemColor);
                    Integer compostId = itemHighlighter.selectedCompostID();
                    if (compostId != null && itemHighlighter.isItemInInventory(compostId)) {
                        itemHighlighter.highlightCompost(graphics, compostId, useItemColor);
                        // Clear hint arrow if compost is in inventory (no NPC interaction needed)
                        support.clearHintArrow();
                    } else {
                        compostHighlighter.withdrawCompost(graphics);
                        // If compost is not in inventory, set hint arrow to Tool Leprechaun
                        if (compostId != null) {
                            support.setHintArrowToNPC(Constants.NPC_TOOL_LEPRECHAUN);
                        }
                    }
                    break;
                case UNKNOWN:
                    plugin.addTextToInfoBox(PatchInstructionText.allotmentUnknownActionSouth());
                    break;
            }
        }
    }

    /**
     * Done flag historically stored on {@code FarmingStepHandler} ({@code allotmentPatchDone}).
     */
    public interface AllotmentProgress {
        boolean isDone();

        void setDone(boolean v);
    }

    /**
     * Dual-patch index / completion / compost tracking.
     * Wraps the handler's authoritative {@code AllotmentPatchState} instance — do not construct a new one here.
     */
    public interface AllotmentPatchStateView {
        int getCurrentIndex();

        boolean isPatchCompleted(int index);

        boolean isPatchComposted(int index);

        void markComposted(int index);

        void setPatchCompleted(int index, boolean value);

        void moveToNextPatch();

        void reset();
    }

    /**
     * Handler-owned helpers for varbit lookup and hint arrows.
     * {@code FarmingStepHandler} will implement or adapt these when wiring.
     */
    public interface AllotmentStepSupport {
        int getAllotmentPatchVarbitId(int objectId);

        void setHintArrowToNPC(String npcName);

        void clearHintArrow();
    }
}
