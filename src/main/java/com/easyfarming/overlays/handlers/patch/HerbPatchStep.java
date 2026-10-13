package com.easyfarming.overlays.handlers.patch;

import com.easyfarming.EasyFarmingOverlay;
import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.HerbPatchChecker;
import com.easyfarming.core.Teleport;
import com.easyfarming.overlays.highlighting.CompostHighlighter;
import com.easyfarming.overlays.highlighting.ItemHighlighter;
import com.easyfarming.overlays.highlighting.PatchHighlighter;
import com.easyfarming.overlays.utils.ColorProvider;
import com.easyfarming.overlays.utils.PatchStateChecker;
import com.easyfarming.utils.Constants;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.OptionalInt;

/**
 * Herb patch processing for one frame: highlight patch/items and set instruction text.
 * Extracted from {@code FarmingStepHandler.herbSteps} with identical behavior.
 */
public final class HerbPatchStep implements PatchStep {

    private final Client client;
    private final EasyFarmingPlugin plugin;
    private final EasyFarmingOverlay farmingHelperOverlay;
    private final PatchHighlighter patchHighlighter;
    private final ItemHighlighter itemHighlighter;
    private final CompostHighlighter compostHighlighter;
    private final PatchStateChecker patchStateChecker;
    private final ColorProvider colorProvider;
    private final HerbProgress progress;
    private final HerbStepSupport support;

    public HerbPatchStep(
            Client client,
            EasyFarmingPlugin plugin,
            EasyFarmingOverlay farmingHelperOverlay,
            PatchHighlighter patchHighlighter,
            ItemHighlighter itemHighlighter,
            CompostHighlighter compostHighlighter,
            PatchStateChecker patchStateChecker,
            ColorProvider colorProvider,
            HerbProgress progress,
            HerbStepSupport support) {
        this.client = client;
        this.plugin = plugin;
        this.farmingHelperOverlay = farmingHelperOverlay;
        this.patchHighlighter = patchHighlighter;
        this.itemHighlighter = itemHighlighter;
        this.compostHighlighter = compostHighlighter;
        this.patchStateChecker = patchStateChecker;
        this.colorProvider = colorProvider;
        this.progress = progress;
        this.support = support;
    }

    /**
     * Builds a {@link PatchStepContext} from the live client/teleport/graphics, then
     * {@link #process(PatchStepContext)}. Intended as the facade entry point once
     * {@code FarmingStepHandler} wires this class.
     */
    public void run(Graphics2D graphics, Teleport teleport) {
        OptionalInt regionOpt = PatchLocationResolver.resolveRegionId(client);
        if (!regionOpt.isPresent()) {
            return;
        }
        int currentRegionId = regionOpt.getAsInt();
        String locationName = PatchLocationResolver.herbLocationName(currentRegionId);
        Integer patchObjectId = farmingHelperOverlay.getHerbPatchIdForLocation(locationName);

        int varbitId = -1;
        if (patchObjectId != null) {
            varbitId = support.getHerbPatchVarbitId(patchObjectId);
        }
        if (varbitId == -1) {
            varbitId = PatchLocationResolver.herbVarbitFallbackByRegion(currentRegionId);
        }

        PatchStepContext context = PatchStepContext.builder()
                .graphics(graphics)
                .teleport(teleport)
                .regionId(currentRegionId)
                .locationName(locationName)
                .leftClickColor(colorProvider.getLeftClickColorWithAlpha())
                .useItemColor(colorProvider.getHighlightUseItemWithAlpha())
                .patchObjectId(patchObjectId)
                .varbitId(varbitId)
                .useSpecificPatch(patchObjectId != null)
                .build();
        process(context);
    }

    @Override
    public void process(PatchStepContext context) {
        if (client.getLocalPlayer() == null) {
            return;
        }

        HerbPatchChecker.PlantState plantState = HerbPatchChecker.PlantState.UNKNOWN;
        int varbitId = context.getVarbitId();
        if (varbitId != -1) {
            plantState = HerbPatchChecker.checkHerbPatch(client, varbitId);
        }

        Graphics2D graphics = context.getGraphics();
        Teleport teleport = context.getTeleport();
        Color leftColor = context.getLeftClickColor();
        Color useItemColor = context.getUseItemColor();
        Integer patchObjectId = context.getPatchObjectId();
        boolean useSpecificPatch = context.isUseSpecificPatch();
        String locationName = context.getLocationName();
        WorldPoint patchWorldPoint = support.getHerbPatchWorldPoint(locationName);

        // Instructions follow the active run step whenever we have a teleport context — not player distance to the patch.
        if (teleport == null) {
            // Transitioning between locations; navigation overlay handles routing.
        } else {
            switch (plantState) {
                case HARVESTABLE:
                    plugin.addTextToInfoBox(PatchInstructionText.herbHarvest());
                    highlightHerbPatch(graphics, useSpecificPatch, patchObjectId, leftColor, patchWorldPoint);
                    break;
                case PLANT:
                    plugin.addTextToInfoBox(PatchInstructionText.herbPlantSeed());
                    highlightHerbPatch(graphics, useSpecificPatch, patchObjectId, useItemColor, patchWorldPoint);
                    itemHighlighter.highlightHerbSeeds(graphics);
                    break;
                case DEAD:
                    plugin.addTextToInfoBox(PatchInstructionText.herbClearDead());
                    highlightHerbPatch(graphics, useSpecificPatch, patchObjectId, leftColor, patchWorldPoint);
                    break;
                case DISEASED:
                    plugin.addTextToInfoBox(PatchInstructionText.herbPlantCure());
                    highlightHerbPatch(graphics, useSpecificPatch, patchObjectId, leftColor, patchWorldPoint);
                    itemHighlighter.itemHighlight(graphics, ItemID.PLANT_CURE, useItemColor);
                    break;
                case WEEDS:
                    // Check if value is 3 (fully raked, ready to plant)
                    int varbitValue = varbitId != -1 ? client.getVarbitValue(varbitId) : -1;
                    if (varbitValue == 3) {
                        // Fully raked patch, ready to plant
                        plugin.addTextToInfoBox(PatchInstructionText.herbPlantSeed());
                        highlightHerbPatch(graphics, useSpecificPatch, patchObjectId, useItemColor, patchWorldPoint);
                        itemHighlighter.highlightHerbSeeds(graphics);
                    } else {
                        // Needs raking
                        plugin.addTextToInfoBox(PatchInstructionText.herbRake());
                        highlightHerbPatch(graphics, useSpecificPatch, patchObjectId, leftColor, patchWorldPoint);
                    }
                    break;
                case GROWING:
                    // Check persistent state FIRST - if already composted, mark as done and return
                    if (progress.isComposted()) {
                        progress.setDone(true);  // Set done flag so transition happens
                        support.clearHintArrow();
                        return;
                    }
                    // If already done (shouldn't happen, but safety check)
                    if (progress.isDone()) {
                        support.clearHintArrow();
                        return;
                    }
                    // Check if compost was just applied (from chat message) - this sets herbPatchDone
                    boolean isComposted = patchStateChecker.patchIsCompostedForHerbPatch();
                    if (isComposted) {
                        progress.setComposted(true);  // Set persistent state
                        progress.setDone(true);
                        // Clear hint arrow when patch is done
                        support.clearHintArrow();
                        // Don't show anything - transition will happen on next frame
                        return;
                    }
                    // Patch is GROWING but not composted yet - show compost instruction
                    plugin.addTextToInfoBox(PatchInstructionText.useCompost());
                    Integer compostId = itemHighlighter.selectedCompostID();
                    // If compost is not in inventory, set hint arrow to Tool Leprechaun
                    if (compostId != null && !itemHighlighter.isItemInInventory(compostId)) {
                        support.setHintArrowToNPC(Constants.NPC_TOOL_LEPRECHAUN);
                    } else {
                        // Clear hint arrow if compost is in inventory (no NPC interaction needed)
                        support.clearHintArrow();
                    }
                    highlightHerbPatch(graphics, useSpecificPatch, patchObjectId, useItemColor, patchWorldPoint);
                    compostHighlighter.highlightCompost(graphics, true, false, false, 1);
                    break;
                case UNKNOWN:
                    plugin.addTextToInfoBox(PatchInstructionText.herbUnknown());
                    break;
            }
            support.applyPatchDirectionHintArrow(patchWorldPoint);
        }
    }

    private void highlightHerbPatch(
            Graphics2D graphics,
            boolean useSpecificPatch,
            Integer patchObjectId,
            Color color,
            WorldPoint patchWorldPoint) {
        if (useSpecificPatch && patchObjectId != null) {
            patchHighlighter.highlightSpecificHerbPatch(graphics, patchObjectId, color, patchWorldPoint);
        } else {
            patchHighlighter.highlightHerbPatches(graphics, color);
        }
    }

    /**
     * Progress flags historically stored on {@code FarmingStepHandler}
     * ({@code herbPatchComposted}, {@code herbPatchDone}).
     */
    public interface HerbProgress {
        boolean isComposted();

        void setComposted(boolean v);

        boolean isDone();

        void setDone(boolean v);
    }

    /**
     * Handler-owned helpers for varbit lookup, world points, and hint arrows.
     * {@code FarmingStepHandler} will implement or adapt these when wiring.
     */
    public interface HerbStepSupport {
        int getHerbPatchVarbitId(Integer objectId);

        WorldPoint getHerbPatchWorldPoint(String locationName);

        void applyPatchDirectionHintArrow(WorldPoint patchWorldPoint);

        void setHintArrowToNPC(String npcName);

        void clearHintArrow();
    }
}
