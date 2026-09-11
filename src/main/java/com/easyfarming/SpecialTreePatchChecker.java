package com.easyfarming;

import com.easyfarming.customrun.PatchTypes;
import net.runelite.api.Client;

/** State detection for the special tree patch implementations. */
public final class SpecialTreePatchChecker {

    private SpecialTreePatchChecker() {}

    public static PlantState checkPatch(Client client, String patchType, int varbitIndex) {
        return classify(patchType, client.getVarbitValue(varbitIndex));
    }

    public static PlantState classify(String patchType, int value) {
        if (PatchTypes.HARDWOOD.equals(patchType)) {
            return classifyHardwood(value);
        }
        if (PatchTypes.CALQUAT.equals(patchType)) {
            return classifyCalquat(value);
        }
        if (PatchTypes.CELASTRUS.equals(patchType)) {
            return classifyCelastrus(value);
        }
        if (PatchTypes.REDWOOD.equals(patchType)) {
            return classifyRedwood(value);
        }
        if (PatchTypes.CRYSTAL_TREE.equals(patchType)) {
            return classifyCrystalTree(value);
        }
        return PlantState.UNKNOWN;
    }

    private static PlantState classifyHardwood(int value) {
        if (inRange(value, 0, 2) || inRange(value, 4, 7) || inRange(value, 133, 255)) {
            return PlantState.WEEDS;
        }
        if (value == 3) {
            return PlantState.PLANT;
        }
        if (inRange(value, 8, 14) || inRange(value, 30, 37) || inRange(value, 55, 62)
                || inRange(value, 80, 87) || inRange(value, 105, 113)) {
            return PlantState.GROWING;
        }
        if (value == 15 || value == 38 || value == 63 || value == 88 || value == 114) {
            return PlantState.HEALTHY;
        }
        if (value == 16 || value == 39 || value == 64 || value == 89 || value == 115) {
            return PlantState.CHOP;
        }
        if (value == 17 || value == 40 || value == 65 || value == 90 || value == 116) {
            return PlantState.CLEAR;
        }
        if (inRange(value, 18, 23) || inRange(value, 41, 47) || inRange(value, 66, 72)
                || inRange(value, 91, 97) || inRange(value, 117, 124)) {
            return PlantState.DISEASED;
        }
        if (inRange(value, 24, 29) || inRange(value, 48, 54) || inRange(value, 73, 79)
                || inRange(value, 98, 104) || inRange(value, 125, 132)) {
            return PlantState.DEAD;
        }
        return PlantState.UNKNOWN;
    }

    private static PlantState classifyCalquat(int value) {
        if (inRange(value, 0, 2) || inRange(value, 35, 255)) {
            return PlantState.WEEDS;
        }
        if (value == 3) {
            return PlantState.PLANT;
        }
        if (inRange(value, 4, 11)) {
            return PlantState.GROWING;
        }
        if (inRange(value, 12, 18)) {
            return PlantState.HARVEST;
        }
        if (inRange(value, 19, 25)) {
            return PlantState.DISEASED;
        }
        if (inRange(value, 26, 33)) {
            return PlantState.DEAD;
        }
        if (value == 34) {
            return PlantState.HEALTHY;
        }
        return PlantState.UNKNOWN;
    }

    private static PlantState classifyCelastrus(int value) {
        if (inRange(value, 0, 2) || inRange(value, 4, 7) || inRange(value, 29, 255)) {
            return PlantState.WEEDS;
        }
        if (value == 3) {
            return PlantState.PLANT;
        }
        if (inRange(value, 8, 12)) {
            return PlantState.GROWING;
        }
        if (value == 13) {
            return PlantState.HEALTHY;
        }
        if (inRange(value, 14, 16)) {
            return PlantState.HARVEST;
        }
        if (value == 17) {
            return PlantState.CHOP;
        }
        if (inRange(value, 18, 22)) {
            return PlantState.DISEASED;
        }
        if (inRange(value, 23, 27)) {
            return PlantState.DEAD;
        }
        if (value == 28) {
            return PlantState.CLEAR;
        }
        return PlantState.UNKNOWN;
    }

    private static PlantState classifyRedwood(int value) {
        if (inRange(value, 0, 2) || inRange(value, 4, 7)) {
            return PlantState.WEEDS;
        }
        if (value == 3) {
            return PlantState.PLANT;
        }
        if (inRange(value, 8, 17)) {
            return PlantState.GROWING;
        }
        if (value == 18 || inRange(value, 41, 55)) {
            return PlantState.REMOVE;
        }
        if (inRange(value, 19, 27)) {
            return PlantState.DISEASED;
        }
        if (inRange(value, 28, 36)) {
            return PlantState.DEAD;
        }
        if (value == 37) {
            return PlantState.HEALTHY;
        }
        return PlantState.UNKNOWN;
    }

    private static PlantState classifyCrystalTree(int value) {
        if (inRange(value, 0, 2)) {
            return PlantState.WEEDS;
        }
        if (value == 3) {
            return PlantState.PLANT;
        }
        if (inRange(value, 8, 13)) {
            return PlantState.GROWING;
        }
        if (value == 14) {
            return PlantState.HEALTHY;
        }
        if (value == 15) {
            return PlantState.HARVEST;
        }
        return PlantState.UNKNOWN;
    }

    private static boolean inRange(int value, int minimum, int maximum) {
        return value >= minimum && value <= maximum;
    }

    public enum PlantState {
        WEEDS,
        PLANT,
        GROWING,
        HEALTHY,
        HARVEST,
        CHOP,
        CLEAR,
        REMOVE,
        DISEASED,
        DEAD,
        UNKNOWN
    }
}
