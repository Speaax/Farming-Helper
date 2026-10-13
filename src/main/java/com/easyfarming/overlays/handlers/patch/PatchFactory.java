package com.easyfarming.overlays.handlers.patch;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.EasyFarmingOverlay;
import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.overlays.handlers.CustomRunPatchProgress;
import com.easyfarming.overlays.highlighting.CompostHighlighter;
import com.easyfarming.overlays.highlighting.FarmerHighlighter;
import com.easyfarming.overlays.highlighting.GameObjectHighlighter;
import com.easyfarming.overlays.highlighting.ItemHighlighter;
import com.easyfarming.overlays.highlighting.PatchHighlighter;
import com.easyfarming.overlays.utils.ColorProvider;
import com.easyfarming.overlays.utils.PatchStateChecker;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;

import java.util.List;

/**
 * Builds the six {@link PatchStep} implementations and binds Progress/Support adapters
 * to a handler-owned {@link CustomRunPatchProgress} and {@link PatchStepHost}.
 */
public final class PatchFactory {
    private PatchFactory() {
    }

    public static PatchStepBundle create(
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
            CustomRunPatchProgress patchProgress,
            PatchStepHost host) {
        HerbPatchStep herb = new HerbPatchStep(
                client, plugin, farmingHelperOverlay, patchHighlighter, itemHighlighter,
                compostHighlighter, patchStateChecker, colorProvider,
                herbProgress(patchProgress), herbSupport(host));

        HopsPatchStep hops = new HopsPatchStep(
                client, plugin, farmingHelperOverlay, patchHighlighter, itemHighlighter,
                compostHighlighter, gameObjectHighlighter, patchStateChecker, colorProvider,
                hopsProgress(patchProgress), hopsSupport(host));

        FlowerPatchStep flower = new FlowerPatchStep(
                client, plugin, farmingHelperOverlay, patchHighlighter, itemHighlighter,
                compostHighlighter, patchStateChecker, colorProvider,
                flowerProgress(patchProgress), flowerSupport(host));

        AllotmentPatchStep allotment = new AllotmentPatchStep(
                client, plugin, farmingHelperOverlay, patchHighlighter, itemHighlighter,
                compostHighlighter, gameObjectHighlighter, patchStateChecker, colorProvider,
                allotmentPatchStateView(patchProgress), allotmentProgress(patchProgress),
                allotmentSupport(host));

        TreePatchStep tree = new TreePatchStep(
                client, plugin, farmingHelperOverlay, patchHighlighter, itemHighlighter,
                compostHighlighter, farmerHighlighter, gameObjectHighlighter, patchStateChecker,
                colorProvider, treeProgress(patchProgress), treeSupport(host));

        FruitTreePatchStep fruitTree = new FruitTreePatchStep(
                client, plugin, config, farmingHelperOverlay, patchHighlighter, itemHighlighter,
                compostHighlighter, farmerHighlighter, gameObjectHighlighter, patchStateChecker,
                colorProvider, fruitTreeProgress(patchProgress), fruitTreeSupport(host));

        return new PatchStepBundle(herb, hops, flower, allotment, tree, fruitTree);
    }

    private static HerbPatchStep.HerbProgress herbProgress(CustomRunPatchProgress patchProgress) {
        return new HerbPatchStep.HerbProgress() {
            @Override
            public boolean isComposted() {
                return patchProgress.isHerbPatchComposted();
            }

            @Override
            public void setComposted(boolean v) {
                patchProgress.setHerbPatchComposted(v);
            }

            @Override
            public boolean isDone() {
                return patchProgress.isHerbPatchDone();
            }

            @Override
            public void setDone(boolean v) {
                patchProgress.setHerbPatchDone(v);
            }
        };
    }

    private static HerbPatchStep.HerbStepSupport herbSupport(PatchStepHost host) {
        return new HerbPatchStep.HerbStepSupport() {
            @Override
            public int getHerbPatchVarbitId(Integer objectId) {
                return host.getHerbPatchVarbitId(objectId);
            }

            @Override
            public WorldPoint getHerbPatchWorldPoint(String locationName) {
                return host.getHerbPatchWorldPoint(locationName);
            }

            @Override
            public void applyPatchDirectionHintArrow(WorldPoint patchWorldPoint) {
                host.applyPatchDirectionHintArrow(patchWorldPoint);
            }

            @Override
            public void setHintArrowToNPC(String npcName) {
                host.setHintArrowToNPC(npcName);
            }

            @Override
            public void clearHintArrow() {
                host.clearHintArrow();
            }
        };
    }

    private static HopsPatchStep.HopsProgress hopsProgress(CustomRunPatchProgress patchProgress) {
        return new HopsPatchStep.HopsProgress() {
            @Override
            public boolean isComposted() {
                return patchProgress.isHopsPatchComposted();
            }

            @Override
            public void setComposted(boolean v) {
                patchProgress.setHopsPatchComposted(v);
            }

            @Override
            public boolean isDone() {
                return patchProgress.isHopsPatchDone();
            }

            @Override
            public void setDone(boolean v) {
                patchProgress.setHopsPatchDone(v);
            }
        };
    }

    private static HopsPatchStep.HopsStepSupport hopsSupport(PatchStepHost host) {
        return new HopsPatchStep.HopsStepSupport() {
            @Override
            public int getHopsPatchVarbitId(Integer objectId) {
                return host.getHopsPatchVarbitId(objectId);
            }

            @Override
            public WorldPoint getHopsPatchWorldPoint(String locationName) {
                return host.getHopsPatchWorldPoint(locationName);
            }

            @Override
            public void applyPatchDirectionHintArrow(WorldPoint patchWorldPoint) {
                host.applyPatchDirectionHintArrow(patchWorldPoint);
            }

            @Override
            public void setHintArrowToNPC(String npcName) {
                host.setHintArrowToNPC(npcName);
            }

            @Override
            public void clearHintArrow() {
                host.clearHintArrow();
            }
        };
    }

    private static FlowerPatchStep.FlowerProgress flowerProgress(CustomRunPatchProgress patchProgress) {
        return new FlowerPatchStep.FlowerProgress() {
            @Override
            public boolean isComposted() {
                return patchProgress.isFlowerPatchComposted();
            }

            @Override
            public void setComposted(boolean v) {
                patchProgress.setFlowerPatchComposted(v);
            }

            @Override
            public boolean isDone() {
                return patchProgress.isFlowerPatchDone();
            }

            @Override
            public void setDone(boolean v) {
                patchProgress.setFlowerPatchDone(v);
            }
        };
    }

    private static FlowerPatchStep.FlowerStepSupport flowerSupport(PatchStepHost host) {
        return new FlowerPatchStep.FlowerStepSupport() {
            @Override
            public int getFlowerPatchVarbitId(Integer objectId) {
                return host.getFlowerPatchVarbitId(objectId);
            }

            @Override
            public WorldPoint getFlowerPatchWorldPoint(String locationName) {
                return host.getFlowerPatchWorldPoint(locationName);
            }

            @Override
            public void applyPatchDirectionHintArrow(WorldPoint patchWorldPoint) {
                host.applyPatchDirectionHintArrow(patchWorldPoint);
            }

            @Override
            public void setHintArrowToNPC(String npcName) {
                host.setHintArrowToNPC(npcName);
            }

            @Override
            public void clearHintArrow() {
                host.clearHintArrow();
            }
        };
    }

    private static AllotmentPatchStep.AllotmentProgress allotmentProgress(CustomRunPatchProgress patchProgress) {
        return new AllotmentPatchStep.AllotmentProgress() {
            @Override
            public boolean isDone() {
                return patchProgress.isAllotmentPatchDone();
            }

            @Override
            public void setDone(boolean v) {
                patchProgress.setAllotmentPatchDone(v);
            }
        };
    }

    private static AllotmentPatchStep.AllotmentPatchStateView allotmentPatchStateView(
            CustomRunPatchProgress patchProgress) {
        return new AllotmentPatchStep.AllotmentPatchStateView() {
            @Override
            public int getCurrentIndex() {
                return patchProgress.getAllotmentPatchState().getCurrentIndex();
            }

            @Override
            public boolean isPatchCompleted(int index) {
                return patchProgress.getAllotmentPatchState().isPatchCompleted(index);
            }

            @Override
            public boolean isPatchComposted(int index) {
                return patchProgress.getAllotmentPatchState().isPatchComposted(index);
            }

            @Override
            public void markComposted(int index) {
                patchProgress.getAllotmentPatchState().markComposted(index);
            }

            @Override
            public void setPatchCompleted(int index, boolean value) {
                patchProgress.getAllotmentPatchState().setPatchCompleted(index, value);
            }

            @Override
            public void moveToNextPatch() {
                patchProgress.getAllotmentPatchState().moveToNextPatch();
            }

            @Override
            public void reset() {
                patchProgress.resetAllotmentState();
            }
        };
    }

    private static AllotmentPatchStep.AllotmentStepSupport allotmentSupport(PatchStepHost host) {
        return new AllotmentPatchStep.AllotmentStepSupport() {
            @Override
            public int getAllotmentPatchVarbitId(int objectId) {
                return host.getAllotmentPatchVarbitId(objectId);
            }

            @Override
            public void setHintArrowToNPC(String npcName) {
                host.setHintArrowToNPC(npcName);
            }

            @Override
            public void clearHintArrow() {
                host.clearHintArrow();
            }
        };
    }

    private static TreePatchStep.TreeProgress treeProgress(CustomRunPatchProgress patchProgress) {
        return new TreePatchStep.TreeProgress() {
            @Override
            public boolean isComposted() {
                return patchProgress.isTreePatchComposted();
            }

            @Override
            public void setComposted(boolean v) {
                patchProgress.setTreePatchComposted(v);
            }

            @Override
            public boolean isDone() {
                return patchProgress.isTreePatchDone();
            }

            @Override
            public void setDone(boolean v) {
                patchProgress.setTreePatchDone(v);
            }
        };
    }

    private static TreePatchStep.TreeStepSupport treeSupport(PatchStepHost host) {
        return new TreePatchStep.TreeStepSupport() {
            @Override
            public WorldPoint getTreePatchWorldPoint(String locationName) {
                return host.getTreePatchWorldPoint(locationName);
            }

            @Override
            public void applyPatchDirectionHintArrow(WorldPoint patchWorldPoint) {
                host.applyPatchDirectionHintArrow(patchWorldPoint);
            }

            @Override
            public void setHintArrowToNPC(String npcName) {
                host.setHintArrowToNPC(npcName);
            }

            @Override
            public void setHintArrowToFirstAvailableNPC(List<String> npcNames) {
                host.setHintArrowToFirstAvailableNPC(npcNames);
            }

            @Override
            public void clearHintArrow() {
                host.clearHintArrow();
            }

            @Override
            public boolean payForProtection() {
                return host.payForProtection();
            }
        };
    }

    private static FruitTreePatchStep.FruitTreeProgress fruitTreeProgress(CustomRunPatchProgress patchProgress) {
        return new FruitTreePatchStep.FruitTreeProgress() {
            @Override
            public boolean isComposted() {
                return patchProgress.isFruitTreePatchComposted();
            }

            @Override
            public void setComposted(boolean v) {
                patchProgress.setFruitTreePatchComposted(v);
            }

            @Override
            public boolean isDone() {
                return patchProgress.isFruitTreePatchDone();
            }

            @Override
            public void setDone(boolean v) {
                patchProgress.setFruitTreePatchDone(v);
            }
        };
    }

    private static FruitTreePatchStep.FruitTreeStepSupport fruitTreeSupport(PatchStepHost host) {
        return new FruitTreePatchStep.FruitTreeStepSupport() {
            @Override
            public int getFruitTreePatchVarbitId(Integer objectId) {
                return host.getFruitTreePatchVarbitId(objectId);
            }

            @Override
            public WorldPoint getFruitTreePatchWorldPoint(String locationName) {
                return host.getFruitTreePatchWorldPoint(locationName);
            }

            @Override
            public void applyPatchDirectionHintArrow(WorldPoint patchWorldPoint) {
                host.applyPatchDirectionHintArrow(patchWorldPoint);
            }

            @Override
            public void setHintArrowToNPC(String npcName) {
                host.setHintArrowToNPC(npcName);
            }

            @Override
            public void setHintArrowToFirstAvailableNPC(List<String> npcNames) {
                host.setHintArrowToFirstAvailableNPC(npcNames);
            }

            @Override
            public void clearHintArrow() {
                host.clearHintArrow();
            }
        };
    }
}
