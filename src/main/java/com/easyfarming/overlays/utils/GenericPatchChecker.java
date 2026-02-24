package com.easyfarming.overlays.utils;

import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.runelite.farming.CropState;
import com.easyfarming.runelite.farming.FarmingPatch;
import com.easyfarming.runelite.farming.PatchPrediction;
import com.easyfarming.runelite.farming.Produce;

import java.util.Set;

public class GenericPatchChecker {
    public enum PlantState {
        UNKNOWN, HARVESTABLE, PLANT, DEAD, DISEASED, WEEDS, GROWING, EMPTY
    }

    public static PlantState checkPatch(EasyFarmingPlugin plugin, int varbitId) {
        if (plugin.getFarmingTracker() == null || plugin.getFarmingWorld() == null) {
            return PlantState.UNKNOWN;
        }

        for (Set<FarmingPatch> patchSet : plugin.getFarmingWorld().getTabs().values()) {
            for (FarmingPatch patch : patchSet) {
                if (patch.getVarbit() == varbitId) {
                    PatchPrediction prediction = plugin.getFarmingTracker().predictPatch(patch);
                    if (prediction == null) {
                        return PlantState.UNKNOWN;
                    }
                    if (prediction.getProduce() == Produce.WEEDS) {
                        return PlantState.WEEDS;
                    }
                    switch (prediction.getCropState()) {
                        case HARVESTABLE: return PlantState.HARVESTABLE;
                        case DEAD: return PlantState.DEAD;
                        case DISEASED: return PlantState.DISEASED;
                        case GROWING: return PlantState.GROWING;
                        case EMPTY: return PlantState.PLANT;
                        default: return PlantState.UNKNOWN;
                    }
                }
            }
        }
        return PlantState.UNKNOWN;
    }
}