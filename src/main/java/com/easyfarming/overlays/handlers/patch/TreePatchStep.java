package com.easyfarming.overlays.handlers.patch;

import com.easyfarming.EasyFarmingOverlay;
import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.TreePatchChecker;
import com.easyfarming.core.Teleport;
import com.easyfarming.overlays.highlighting.CompostHighlighter;
import com.easyfarming.overlays.highlighting.FarmerHighlighter;
import com.easyfarming.overlays.highlighting.GameObjectHighlighter;
import com.easyfarming.overlays.highlighting.ItemHighlighter;
import com.easyfarming.overlays.highlighting.PatchHighlighter;
import com.easyfarming.overlays.utils.ColorProvider;
import com.easyfarming.overlays.utils.PatchStateChecker;
import com.easyfarming.utils.Constants;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import java.util.OptionalInt;

/**
 * Tree patch processing for one frame: highlight patch/items/farmers and set instruction text.
 * Extracted from {@code FarmingStepHandler.treeSteps} with identical behavior.
 */
public final class TreePatchStep implements PatchStep {

    private final Client client;
    private final EasyFarmingPlugin plugin;
    private final EasyFarmingOverlay farmingHelperOverlay;
    private final PatchHighlighter patchHighlighter;
    private final ItemHighlighter itemHighlighter;
    private final CompostHighlighter compostHighlighter;
    private final FarmerHighlighter farmerHighlighter;
    private final GameObjectHighlighter gameObjectHighlighter;
    private final PatchStateChecker patchStateChecker;
    private final ColorProvider colorProvider;
    private final TreeProgress progress;
    private final TreeStepSupport support;

    public TreePatchStep(
            Client client,
            EasyFarmingPlugin plugin,
            EasyFarmingOverlay farmingHelperOverlay,
            PatchHighlighter patchHighlighter,
            ItemHighlighter itemHighlighter,
            CompostHighlighter compostHighlighter,
            FarmerHighlighter farmerHighlighter,
            GameObjectHighlighter gameObjectHighlighter,
            PatchStateChecker patchStateChecker,
            ColorProvider colorProvider,
            TreeProgress progress,
            TreeStepSupport support) {
        this.client = client;
        this.plugin = plugin;
        this.farmingHelperOverlay = farmingHelperOverlay;
        this.patchHighlighter = patchHighlighter;
        this.itemHighlighter = itemHighlighter;
        this.compostHighlighter = compostHighlighter;
        this.farmerHighlighter = farmerHighlighter;
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
        String locationName = PatchLocationResolver.treeLocationName(currentRegionId);
        Integer patchObjectId = farmingHelperOverlay.getTreePatchIdForLocation(locationName);

        int varbitId;
        if (currentRegionId == Constants.REGION_FARMING_GUILD) {
            varbitId = Constants.VARBIT_TREE_PATCH_FARMING_GUILD;
        } else {
            varbitId = Constants.VARBIT_TREE_PATCH_STANDARD;
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
        String locationName = context.getLocationName();
        int currentRegionId = context.getRegionId();
        int varbitId = context.getVarbitId();

        if (teleport != null) {
            if ("Quetzal_Transport".equals(teleport.getEnumOption())) {
                if (Constants.isCivitasQuetzalRegion(currentRegionId)) {
                    plugin.addTextToInfoBox(PatchInstructionText.treeFlyRenuToAuburnvale());
                    gameObjectHighlighter.renderGameObjectHighlight(graphics, Constants.QUETZAL_TRANSPORT_OBJECT_ID, useItemColor);
                    return;
                }
            }
        }

        TreePatchChecker.PlantState plantState = TreePatchChecker.checkTreePatch(client, varbitId);

        // Instructions follow the active run step whenever we have a teleport context — not player distance to the patch.
        if (teleport == null) {
            // Transitioning between locations.
        } else {
            switch (plantState) {
                case HEALTHY:
                    plugin.addTextToInfoBox(PatchInstructionText.treeCheckHealth());
                    highlightTreePatchForLocation(graphics, locationName, leftColor);
                    break;
                case WEEDS:
                    plugin.addTextToInfoBox(PatchInstructionText.treeRake());
                    highlightTreePatchForLocation(graphics, locationName, leftColor);
                    break;
                case DEAD:
                    plugin.addTextToInfoBox(PatchInstructionText.treeClearDead());
                    highlightTreePatchForLocation(graphics, locationName, leftColor);
                    break;
                case PLANT:
                    plugin.addTextToInfoBox(PatchInstructionText.treePlantSapling());
                    highlightTreePatchForLocation(graphics, locationName, useItemColor);
                    itemHighlighter.highlightTreeSapling(graphics);
                    break;
                case DISEASED:
                    plugin.addTextToInfoBox(PatchInstructionText.treePrune());
                    highlightTreePatchForLocation(graphics, locationName, useItemColor);
                    break;
                case REMOVE:
                    plugin.addTextToInfoBox(PatchInstructionText.treeRemove());
                    farmerHighlighter.highlightTreeFarmers(graphics);
                    support.setHintArrowToFirstAvailableNPC(Constants.TREE_FARMER_NAMES);
                    break;
                case UNKNOWN:
                    plugin.addTextToInfoBox(PatchInstructionText.treeUnknown());
                    break;
                case GROWING:
                    if (support.payForProtection()) {
                        plugin.addTextToInfoBox(PatchInstructionText.treePayProtect());
                        farmerHighlighter.highlightTreeFarmers(graphics);
                        support.setHintArrowToFirstAvailableNPC(Constants.TREE_FARMER_NAMES);
                        if (patchStateChecker.patchIsProtected()) {
                            progress.setDone(true);
                            support.clearHintArrow();
                        }
                    } else {
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
                        boolean isComposted = patchStateChecker.patchIsCompostedForTreePatch();
                        if (isComposted) {
                            progress.setComposted(true);  // Set persistent state
                            progress.setDone(true);
                            support.clearHintArrow();
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
                        compostHighlighter.highlightCompost(graphics, false, true, false, 1);
                    }
                    break;
            }
            support.applyPatchDirectionHintArrow(support.getTreePatchWorldPoint(locationName));
        }
    }

    private void highlightTreePatchForLocation(Graphics2D graphics, String locationName, Color color) {
        Integer patchId = locationName != null ? farmingHelperOverlay.getTreePatchIdForLocation(locationName) : null;
        if (patchId != null) {
            patchHighlighter.highlightSpecificTreePatch(graphics, patchId, color);
        } else {
            patchHighlighter.highlightTreePatches(graphics, color);
        }
    }

    /**
     * Progress flags historically stored on {@code FarmingStepHandler}
     * ({@code treePatchComposted}, {@code treePatchDone}).
     */
    public interface TreeProgress {
        boolean isComposted();

        void setComposted(boolean v);

        boolean isDone();

        void setDone(boolean v);
    }

    /**
     * Handler-owned helpers for world points, hint arrows, and protection config.
     * {@code FarmingStepHandler} will implement or adapt these when wiring.
     */
    public interface TreeStepSupport {
        WorldPoint getTreePatchWorldPoint(String locationName);

        void applyPatchDirectionHintArrow(WorldPoint patchWorldPoint);

        void setHintArrowToNPC(String npcName);

        void setHintArrowToFirstAvailableNPC(List<String> npcNames);

        void clearHintArrow();

        boolean payForProtection();
    }
}
