package com.easyfarming.overlays.handlers.patch;

import com.easyfarming.EasyFarmingOverlay;
import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.FlowerPatchChecker;
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
 * Flower (limpwurt) patch processing for one frame: highlight patch/items and set instruction text.
 * Extracted from {@code FarmingStepHandler.flowerSteps} with identical behavior.
 */
public final class FlowerPatchStep implements PatchStep {

    private final Client client;
    private final EasyFarmingPlugin plugin;
    private final EasyFarmingOverlay farmingHelperOverlay;
    private final PatchHighlighter patchHighlighter;
    private final ItemHighlighter itemHighlighter;
    private final CompostHighlighter compostHighlighter;
    private final PatchStateChecker patchStateChecker;
    private final ColorProvider colorProvider;
    private final FlowerProgress progress;
    private final FlowerStepSupport support;

    public FlowerPatchStep(
            Client client,
            EasyFarmingPlugin plugin,
            EasyFarmingOverlay farmingHelperOverlay,
            PatchHighlighter patchHighlighter,
            ItemHighlighter itemHighlighter,
            CompostHighlighter compostHighlighter,
            PatchStateChecker patchStateChecker,
            ColorProvider colorProvider,
            FlowerProgress progress,
            FlowerStepSupport support) {
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
     * Builds a {@link PatchStepContext} from the live client/graphics (teleport is null), then
     * {@link #process(PatchStepContext)}. When {@code farmLimps} is false, marks the flower patch
     * done and returns without processing — matching {@code FarmingStepHandler.flowerSteps}.
     */
    public void run(Graphics2D graphics, boolean farmLimps) {
        if (!farmLimps) {
            progress.setDone(true);
            return;
        }

        OptionalInt regionOpt = PatchLocationResolver.resolveRegionId(client);
        if (!regionOpt.isPresent()) {
            return;
        }
        int currentRegionId = regionOpt.getAsInt();
        String locationName = PatchLocationResolver.herbLocationName(currentRegionId);
        Integer patchObjectId = farmingHelperOverlay.getFlowerPatchIdForLocation(locationName);

        int varbitId = -1;
        if (patchObjectId != null) {
            varbitId = support.getFlowerPatchVarbitId(patchObjectId);
        }
        // Fallback: If object composition fails, use location-specific varbits
        if (varbitId == -1) {
            if (currentRegionId == Constants.REGION_FARMING_GUILD) {
                varbitId = Constants.VARBIT_FLOWER_PATCH_FARMING_GUILD;
            } else {
                varbitId = Constants.VARBIT_FLOWER_PATCH_STANDARD;
            }
        }

        PatchStepContext context = PatchStepContext.builder()
                .graphics(graphics)
                .teleport(null)
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

        FlowerPatchChecker.PlantState plantState = FlowerPatchChecker.PlantState.UNKNOWN;
        int varbitId = context.getVarbitId();
        if (varbitId != -1) {
            plantState = FlowerPatchChecker.checkFlowerPatch(client, varbitId);
        }

        Graphics2D graphics = context.getGraphics();
        Color leftColor = context.getLeftClickColor();
        Color useItemColor = context.getUseItemColor();
        Integer patchObjectId = context.getPatchObjectId();
        String locationName = context.getLocationName();

        // Always highlight specific patch if patchObjectId is available, similar to herb patches
        if (patchObjectId != null) {
            switch (plantState) {
                case HARVESTABLE:
                    plugin.addTextToInfoBox(PatchInstructionText.flowerHarvest());
                    patchHighlighter.highlightSpecificFlowerPatch(graphics, patchObjectId, leftColor);
                    break;
                case DISEASED:
                    plugin.addTextToInfoBox(PatchInstructionText.flowerPlantCure());
                    patchHighlighter.highlightSpecificFlowerPatch(graphics, patchObjectId, leftColor);
                    itemHighlighter.itemHighlight(graphics, ItemID.PLANT_CURE, useItemColor);
                    break;
                case WEEDS:
                    // Check if value is 3 (fully raked, ready to plant) - similar to herb patches
                    int varbitValue = varbitId != -1 ? client.getVarbitValue(varbitId) : -1;
                    if (varbitValue == 3) {
                        // Fully raked patch, ready to plant
                        plugin.addTextToInfoBox(PatchInstructionText.flowerPlantSeed());
                        patchHighlighter.highlightSpecificFlowerPatch(graphics, patchObjectId, useItemColor);
                        itemHighlighter.highlightFlowerSeeds(graphics);
                    } else {
                        // Needs raking - always highlight when WEEDS state is detected
                        plugin.addTextToInfoBox(PatchInstructionText.flowerRake());
                        patchHighlighter.highlightSpecificFlowerPatch(graphics, patchObjectId, leftColor);
                    }
                    break;
                case DEAD:
                    plugin.addTextToInfoBox(PatchInstructionText.flowerClearDead());
                    patchHighlighter.highlightSpecificFlowerPatch(graphics, patchObjectId, leftColor);
                    break;
                case PLANT:
                    plugin.addTextToInfoBox(PatchInstructionText.flowerPlantSeed());
                    patchHighlighter.highlightSpecificFlowerPatch(graphics, patchObjectId, useItemColor);
                    itemHighlighter.highlightFlowerSeeds(graphics);
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
                    // Check if compost was just applied (from chat message)
                    boolean isComposted = patchStateChecker.patchIsCompostedForFlowerPatch();
                    if (isComposted) {
                        progress.setComposted(true);  // Set persistent state
                        progress.setDone(true);
                        support.clearHintArrow();
                        return;
                    }
                    // Patch is GROWING but not composted yet - show compost instruction
                    plugin.addTextToInfoBox(PatchInstructionText.useCompost());
                    patchHighlighter.highlightSpecificFlowerPatch(graphics, patchObjectId, useItemColor);
                    compostHighlighter.highlightCompost(graphics, false, false, false, 2);
                    break;
                case UNKNOWN:
                    plugin.addTextToInfoBox(PatchInstructionText.flowerUnknown());
                    patchHighlighter.highlightSpecificFlowerPatch(graphics, patchObjectId, leftColor);
                    break;
            }
            support.applyPatchDirectionHintArrow(support.getFlowerPatchWorldPoint(locationName));
        } else {
            // Fallback: highlight all flower patches if patch ID not found
            patchHighlighter.highlightFlowerPatches(graphics, leftColor);
        }
    }

    /**
     * Progress flags historically stored on {@code FarmingStepHandler}
     * ({@code flowerPatchComposted}, {@code flowerPatchDone}).
     */
    public interface FlowerProgress {
        boolean isComposted();

        void setComposted(boolean v);

        boolean isDone();

        void setDone(boolean v);
    }

    /**
     * Handler-owned helpers for varbit lookup, world points, and hint arrows.
     * {@code FarmingStepHandler} will implement or adapt these when wiring.
     */
    public interface FlowerStepSupport {
        int getFlowerPatchVarbitId(Integer objectId);

        WorldPoint getFlowerPatchWorldPoint(String locationName);

        void applyPatchDirectionHintArrow(WorldPoint patchWorldPoint);

        void setHintArrowToNPC(String npcName);

        void clearHintArrow();
    }
}
