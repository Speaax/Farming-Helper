package com.easyfarming.overlays.handlers.patch;

import com.easyfarming.EasyFarmingOverlay;
import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.HopsPatchChecker;
import com.easyfarming.core.Teleport;
import com.easyfarming.overlays.highlighting.CompostHighlighter;
import com.easyfarming.overlays.highlighting.GameObjectHighlighter;
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
 * Hops patch processing for one frame: highlight patch/items and set instruction text.
 * Extracted from {@code FarmingStepHandler.hopsSteps} with identical behavior.
 */
public final class HopsPatchStep implements PatchStep {

    private final Client client;
    private final EasyFarmingPlugin plugin;
    private final EasyFarmingOverlay farmingHelperOverlay;
    private final PatchHighlighter patchHighlighter;
    private final ItemHighlighter itemHighlighter;
    private final CompostHighlighter compostHighlighter;
    private final GameObjectHighlighter gameObjectHighlighter;
    private final PatchStateChecker patchStateChecker;
    private final ColorProvider colorProvider;
    private final HopsProgress progress;
    private final HopsStepSupport support;

    public HopsPatchStep(
            Client client,
            EasyFarmingPlugin plugin,
            EasyFarmingOverlay farmingHelperOverlay,
            PatchHighlighter patchHighlighter,
            ItemHighlighter itemHighlighter,
            CompostHighlighter compostHighlighter,
            GameObjectHighlighter gameObjectHighlighter,
            PatchStateChecker patchStateChecker,
            ColorProvider colorProvider,
            HopsProgress progress,
            HopsStepSupport support) {
        this.client = client;
        this.plugin = plugin;
        this.farmingHelperOverlay = farmingHelperOverlay;
        this.patchHighlighter = patchHighlighter;
        this.itemHighlighter = itemHighlighter;
        this.compostHighlighter = compostHighlighter;
        this.gameObjectHighlighter = gameObjectHighlighter;
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
        String locationName = PatchLocationResolver.hopsLocationName(currentRegionId);
        Integer patchObjectId = farmingHelperOverlay.getHopsPatchIdForLocation(locationName);

        int varbitId = -1;
        if (patchObjectId != null) {
            varbitId = support.getHopsPatchVarbitId(patchObjectId);
        }
        // Fallback: Use standard hops patch varbit if object composition fails
        // Hops patches use FARMING_TRANSMIT_A (4771)
        if (varbitId == -1) {
            varbitId = Constants.VARBIT_HOPS_PATCH_STANDARD;
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

        Graphics2D graphics = context.getGraphics();
        Teleport teleport = context.getTeleport();
        Color leftColor = context.getLeftClickColor();
        Color useItemColor = context.getUseItemColor();
        Integer patchObjectId = context.getPatchObjectId();
        String locationName = context.getLocationName();
        int currentRegionId = context.getRegionId();
        int varbitId = context.getVarbitId();

        // Check if player is in Civitas and using Quetzal_Transport for Aldarin
        // Civitas teleport can land in either region 6704 or 6705
        // If so, show instructions to use Renu to fly to Aldarin
        if (Constants.isCivitasQuetzalRegion(currentRegionId) && teleport != null
                && "Quetzal_Transport".equals(teleport.getEnumOption())) {
            plugin.addTextToInfoBox(PatchInstructionText.hopsFlyRenuToAldarin());
            gameObjectHighlighter.renderGameObjectHighlight(graphics, Constants.QUETZAL_TRANSPORT_OBJECT_ID, useItemColor);
            return;
        }

        HopsPatchChecker.PlantState plantState = HopsPatchChecker.PlantState.UNKNOWN;
        if (varbitId != -1) {
            plantState = HopsPatchChecker.checkHopsPatch(client, varbitId);
        }

        // Instructions follow the active run step whenever we have a teleport context — not distance to the patch.
        boolean shouldShowInstructions = teleport != null;

        if (!shouldShowInstructions) {
            // Highlight all hops patches when far from patch and no state detected
            patchHighlighter.highlightHopsPatches(graphics, leftColor);
        } else {
            // Highlight specific patch for current location
            if (patchObjectId != null) {
                switch (plantState) {
                    case HARVESTABLE:
                        plugin.addTextToInfoBox(PatchInstructionText.hopsHarvest());
                        patchHighlighter.highlightSpecificHopsPatch(graphics, patchObjectId, leftColor);
                        break;
                    case PLANT:
                        plugin.addTextToInfoBox(PatchInstructionText.hopsPlantSeed());
                        patchHighlighter.highlightSpecificHopsPatch(graphics, patchObjectId, useItemColor);
                        itemHighlighter.highlightHopsSeeds(graphics);
                        break;
                    case DEAD:
                        plugin.addTextToInfoBox(PatchInstructionText.hopsClearDead());
                        patchHighlighter.highlightSpecificHopsPatch(graphics, patchObjectId, leftColor);
                        break;
                    case DISEASED:
                        plugin.addTextToInfoBox(PatchInstructionText.hopsPlantCure());
                        patchHighlighter.highlightSpecificHopsPatch(graphics, patchObjectId, leftColor);
                        itemHighlighter.itemHighlight(graphics, ItemID.PLANT_CURE, useItemColor);
                        break;
                    case WEEDS:
                        plugin.addTextToInfoBox(PatchInstructionText.hopsRake());
                        patchHighlighter.highlightSpecificHopsPatch(graphics, patchObjectId, leftColor);
                        break;
                    case NEEDS_WATER:
                        // First growth cycle cannot disease; skip watering and proceed to compost (#101).
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
                        boolean isComposted = patchStateChecker.patchIsCompostedForHopsPatch();
                        if (isComposted) {
                            progress.setComposted(true);  // Set persistent state
                            progress.setDone(true);
                            support.clearHintArrow();
                            return;
                        }
                        // Patch is GROWING/NEEDS_WATER but not composted yet - show compost instruction
                        plugin.addTextToInfoBox(PatchInstructionText.useCompost());
                        patchHighlighter.highlightSpecificHopsPatch(graphics, patchObjectId, useItemColor);
                        Integer compostId = itemHighlighter.selectedCompostID();
                        // If compost is not in inventory, set hint arrow to Tool Leprechaun
                        if (compostId != null && !itemHighlighter.isItemInInventory(compostId)) {
                            support.setHintArrowToNPC(Constants.NPC_TOOL_LEPRECHAUN);
                        } else {
                            // Clear hint arrow if compost is in inventory (no NPC interaction needed)
                            support.clearHintArrow();
                        }
                        compostHighlighter.highlightCompost(graphics, true, false, false, 1);
                        break;
                    case UNKNOWN:
                        plugin.addTextToInfoBox(PatchInstructionText.hopsUnknown());
                        patchHighlighter.highlightSpecificHopsPatch(graphics, patchObjectId, leftColor);
                        break;
                }
            } else {
                // Fallback: highlight all patches if patchObjectId is null
                patchHighlighter.highlightHopsPatches(graphics, leftColor);
            }
            support.applyPatchDirectionHintArrow(support.getHopsPatchWorldPoint(locationName));
        }
    }

    /**
     * Progress flags historically stored on {@code FarmingStepHandler}
     * ({@code hopsPatchComposted}, {@code hopsPatchDone}).
     */
    public interface HopsProgress {
        boolean isComposted();

        void setComposted(boolean v);

        boolean isDone();

        void setDone(boolean v);
    }

    /**
     * Handler-owned helpers for varbit lookup, world points, and hint arrows.
     * {@code FarmingStepHandler} will implement or adapt these when wiring.
     */
    public interface HopsStepSupport {
        int getHopsPatchVarbitId(Integer objectId);

        WorldPoint getHopsPatchWorldPoint(String locationName);

        void applyPatchDirectionHintArrow(WorldPoint patchWorldPoint);

        void setHintArrowToNPC(String npcName);

        void clearHintArrow();
    }
}
