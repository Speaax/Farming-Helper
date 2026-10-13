package com.easyfarming.overlays.handlers.patch;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.EasyFarmingOverlay;
import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.FruitTreePatchChecker;
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
import net.runelite.api.gameval.VarbitID;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * Fruit tree patch processing for one frame: highlight patch/items/farmers and set instruction text.
 * Extracted from {@code FarmingStepHandler.fruitTreeSteps} with identical behavior.
 */
public final class FruitTreePatchStep implements PatchStep {

    private final Client client;
    private final EasyFarmingPlugin plugin;
    private final EasyFarmingConfig config;
    private final EasyFarmingOverlay farmingHelperOverlay;
    private final PatchHighlighter patchHighlighter;
    private final ItemHighlighter itemHighlighter;
    private final CompostHighlighter compostHighlighter;
    private final FarmerHighlighter farmerHighlighter;
    private final GameObjectHighlighter gameObjectHighlighter;
    private final PatchStateChecker patchStateChecker;
    private final ColorProvider colorProvider;
    private final FruitTreeProgress progress;
    private final FruitTreeStepSupport support;

    public FruitTreePatchStep(
            Client client,
            EasyFarmingPlugin plugin,
            EasyFarmingConfig config,
            EasyFarmingOverlay farmingHelperOverlay,
            PatchHighlighter patchHighlighter,
            ItemHighlighter itemHighlighter,
            CompostHighlighter compostHighlighter,
            FarmerHighlighter farmerHighlighter,
            GameObjectHighlighter gameObjectHighlighter,
            PatchStateChecker patchStateChecker,
            ColorProvider colorProvider,
            FruitTreeProgress progress,
            FruitTreeStepSupport support) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
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
        Optional<WorldPoint> playerLocation = PatchLocationResolver.resolvePlayerLocation(client);
        String locationName = PatchLocationResolver.fruitTreeLocationName(
                currentRegionId, playerLocation.orElse(null));
        Integer patchObjectId = locationName != null
                ? farmingHelperOverlay.getFruitTreePatchIdForLocation(locationName)
                : null;

        int varbitId = -1;
        // Kastori: fruit tree is on FARMING_TRANSMIT_B (RuneLite FarmingWorld); object composition from 8050 would resolve to transmit A.
        if (Constants.isKastoriRegion(currentRegionId)) {
            varbitId = VarbitID.FARMING_TRANSMIT_B;
        } else if (patchObjectId != null) {
            varbitId = support.getFruitTreePatchVarbitId(patchObjectId);
        }
        if (varbitId == -1) {
            if (currentRegionId == Constants.REGION_FARMING_GUILD) {
                varbitId = Constants.VARBIT_FRUIT_TREE_PATCH_FARMING_GUILD;
            } else if (currentRegionId == Constants.REGION_GNOME_STRONGHOLD
                    || currentRegionId == Constants.REGION_GNOME_STRONGHOLD_ALT) {
                varbitId = Constants.VARBIT_FRUIT_TREE_PATCH_GNOME_STRONGHOLD;
            } else {
                varbitId = Constants.VARBIT_FRUIT_TREE_PATCH_STANDARD;
            }
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

        if (teleport != null && "Quetzal_Transport".equals(teleport.getEnumOption())
                && Constants.isCivitasQuetzalRegion(currentRegionId)) {
            plugin.addTextToInfoBox(PatchInstructionText.fruitTreeFlyQuetzalToKastori());
            gameObjectHighlighter.renderGameObjectHighlight(graphics, Constants.QUETZAL_TRANSPORT_OBJECT_ID, useItemColor);
            return;
        }

        FruitTreePatchChecker.PlantState plantState = FruitTreePatchChecker.PlantState.UNKNOWN;
        if (varbitId != -1) {
            plantState = FruitTreePatchChecker.checkFruitTreePatch(client, varbitId);
        }

        // Instructions follow the active run step whenever we have a teleport context — not player distance to the patch.
        if (teleport == null) {
            // Transitioning between locations; navigation overlay handles routing.
        } else {
            switch (plantState) {
                case HEALTHY:
                    plugin.addTextToInfoBox(PatchInstructionText.fruitTreeCheckHealth());
                    highlightFruitTreePatch(graphics, patchObjectId, leftColor);
                    break;
                case WEEDS:
                    // Check if value is 3 (fully raked, ready to plant)
                    int varbitValue = varbitId != -1 ? client.getVarbitValue(varbitId) : -1;
                    if (varbitValue == 3) {
                        // Fully raked patch, ready to plant
                        plugin.addTextToInfoBox(PatchInstructionText.fruitTreePlantSapling());
                        highlightFruitTreePatch(graphics, patchObjectId, useItemColor);
                        itemHighlighter.highlightFruitTreeSapling(graphics);
                    } else {
                        // Needs raking
                        plugin.addTextToInfoBox(PatchInstructionText.fruitTreeRake());
                        highlightFruitTreePatch(graphics, patchObjectId, leftColor);
                    }
                    break;
                case DEAD:
                    plugin.addTextToInfoBox(PatchInstructionText.fruitTreeClearDead());
                    highlightFruitTreePatch(graphics, patchObjectId, leftColor);
                    break;
                case PLANT:
                    plugin.addTextToInfoBox(PatchInstructionText.fruitTreePlantSapling());
                    highlightFruitTreePatch(graphics, patchObjectId, useItemColor);
                    itemHighlighter.highlightFruitTreeSapling(graphics);
                    break;
                case DISEASED:
                    plugin.addTextToInfoBox(PatchInstructionText.fruitTreePrune());
                    highlightFruitTreePatch(graphics, patchObjectId, leftColor);
                    break;
                case REMOVE:
                    plugin.addTextToInfoBox(PatchInstructionText.fruitTreePayToRemove());
                    farmerHighlighter.highlightFruitTreeFarmers(graphics);
                    support.setHintArrowToFirstAvailableNPC(Constants.FRUIT_TREE_FARMER_NAMES);
                    break;
                case UNKNOWN:
                    plugin.addTextToInfoBox(PatchInstructionText.fruitTreeUnknown());
                    break;
                case GROWING:
                    if (config.generalPayForProtection()) {
                        plugin.addTextToInfoBox(PatchInstructionText.fruitTreePayToProtect());
                        farmerHighlighter.highlightFruitTreeFarmers(graphics);
                        support.setHintArrowToFirstAvailableNPC(Constants.FRUIT_TREE_FARMER_NAMES);
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
                        boolean isComposted = patchStateChecker.patchIsCompostedForFruitTreePatch();
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
                        compostHighlighter.highlightCompost(graphics, false, false, true, 1);
                    }
                    break;
            }
            support.applyPatchDirectionHintArrow(support.getFruitTreePatchWorldPoint(locationName));
        }
    }

    private void highlightFruitTreePatch(Graphics2D graphics, Integer patchObjectId, Color color) {
        if (patchObjectId != null) {
            patchHighlighter.highlightSpecificFruitTreePatch(graphics, patchObjectId, color);
        } else {
            patchHighlighter.highlightFruitTreePatches(graphics, color);
        }
    }

    /**
     * Progress flags historically stored on {@code FarmingStepHandler}
     * ({@code fruitTreePatchComposted}, {@code fruitTreePatchDone}).
     */
    public interface FruitTreeProgress {
        boolean isComposted();

        void setComposted(boolean v);

        boolean isDone();

        void setDone(boolean v);
    }

    /**
     * Handler-owned helpers for varbit lookup, world points, and hint arrows.
     * {@code FarmingStepHandler} will implement or adapt these when wiring.
     */
    public interface FruitTreeStepSupport {
        int getFruitTreePatchVarbitId(Integer objectId);

        WorldPoint getFruitTreePatchWorldPoint(String locationName);

        void applyPatchDirectionHintArrow(WorldPoint patchWorldPoint);

        void setHintArrowToNPC(String npcName);

        void setHintArrowToFirstAvailableNPC(List<String> npcNames);

        void clearHintArrow();
    }
}
