package com.easyfarming;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.easyfarming.SpecialTreePatchChecker.PlantState;
import com.easyfarming.customrun.PatchTypes;
import com.easyfarming.utils.Constants;
import java.util.Arrays;
import org.junit.Test;

public class SpecialTreePatchCheckerTest {

    @Test
    public void specialPatchTypesAreAvailableToCustomRuns() {
        assertTrue(PatchTypes.ALL.contains("hardwood"));
        assertTrue(PatchTypes.ALL.contains("calquat"));
        assertTrue(PatchTypes.ALL.contains("celastrus"));
        assertTrue(PatchTypes.ALL.contains("crystal_tree"));
        assertTrue(PatchTypes.ALL.contains("redwood"));
    }

    @Test
    public void exposesKastoriCalquatRegionAliases() {
        assertEquals(Arrays.asList(5423, 5167, 5424), Constants.KASTORI_CALQUAT_REGION_IDS);
    }

    @Test
    public void classifiesHardwoodStateRangeBoundaries() {
        assertStates(PatchTypes.HARDWOOD, PlantState.WEEDS, 0, 2, 4, 7, 133, 255);
        assertStates(PatchTypes.HARDWOOD, PlantState.PLANT, 3);
        assertStates(PatchTypes.HARDWOOD, PlantState.GROWING, 8, 14, 30, 37, 55, 62, 80, 87, 105, 113);
        assertStates(PatchTypes.HARDWOOD, PlantState.HEALTHY, 15, 38, 63, 88, 114);
        assertStates(PatchTypes.HARDWOOD, PlantState.CHOP, 16, 39, 64, 89, 115);
        assertStates(PatchTypes.HARDWOOD, PlantState.CLEAR, 17, 40, 65, 90, 116);
        assertStates(PatchTypes.HARDWOOD, PlantState.DISEASED, 18, 23, 41, 47, 66, 72, 91, 97, 117, 124);
        assertStates(PatchTypes.HARDWOOD, PlantState.DEAD, 24, 29, 48, 54, 73, 79, 98, 104, 125, 132);
    }

    @Test
    public void classifiesCalquatStateRangeBoundaries() {
        assertStates(PatchTypes.CALQUAT, PlantState.WEEDS, 0, 2, 35, 255);
        assertStates(PatchTypes.CALQUAT, PlantState.PLANT, 3);
        assertStates(PatchTypes.CALQUAT, PlantState.GROWING, 4, 11);
        assertStates(PatchTypes.CALQUAT, PlantState.HARVEST, 12, 18);
        assertStates(PatchTypes.CALQUAT, PlantState.DISEASED, 19, 25);
        assertStates(PatchTypes.CALQUAT, PlantState.DEAD, 26, 33);
        assertStates(PatchTypes.CALQUAT, PlantState.HEALTHY, 34);
    }

    @Test
    public void classifiesCelastrusStateRangeBoundaries() {
        assertStates(PatchTypes.CELASTRUS, PlantState.WEEDS, 0, 2, 4, 7, 29, 255);
        assertStates(PatchTypes.CELASTRUS, PlantState.PLANT, 3);
        assertStates(PatchTypes.CELASTRUS, PlantState.GROWING, 8, 12);
        assertStates(PatchTypes.CELASTRUS, PlantState.HEALTHY, 13);
        assertStates(PatchTypes.CELASTRUS, PlantState.HARVEST, 14, 16);
        assertStates(PatchTypes.CELASTRUS, PlantState.CHOP, 17);
        assertStates(PatchTypes.CELASTRUS, PlantState.DISEASED, 18, 22);
        assertStates(PatchTypes.CELASTRUS, PlantState.DEAD, 23, 27);
        assertStates(PatchTypes.CELASTRUS, PlantState.CLEAR, 28);
    }

    @Test
    public void classifiesRedwoodStateRangeBoundaries() {
        assertStates(PatchTypes.REDWOOD, PlantState.WEEDS, 0, 2, 4, 7);
        assertStates(PatchTypes.REDWOOD, PlantState.PLANT, 3);
        assertStates(PatchTypes.REDWOOD, PlantState.GROWING, 8, 17);
        assertStates(PatchTypes.REDWOOD, PlantState.REMOVE, 18, 41, 55);
        assertStates(PatchTypes.REDWOOD, PlantState.DISEASED, 19, 27);
        assertStates(PatchTypes.REDWOOD, PlantState.DEAD, 28, 36);
        assertStates(PatchTypes.REDWOOD, PlantState.HEALTHY, 37);
        assertStates(PatchTypes.REDWOOD, PlantState.UNKNOWN, 38, 40, 56, 255);
    }

    @Test
    public void classifiesCrystalTreeStateRangeBoundaries() {
        assertStates(PatchTypes.CRYSTAL_TREE, PlantState.WEEDS, 0, 2);
        assertStates(PatchTypes.CRYSTAL_TREE, PlantState.PLANT, 3);
        assertStates(PatchTypes.CRYSTAL_TREE, PlantState.GROWING, 8, 13);
        assertStates(PatchTypes.CRYSTAL_TREE, PlantState.HEALTHY, 14);
        assertStates(PatchTypes.CRYSTAL_TREE, PlantState.HARVEST, 15);
        assertStates(PatchTypes.CRYSTAL_TREE, PlantState.UNKNOWN, 4, 7, 16, 255);
    }

    @Test
    public void civitasQuetzalTransportTargetsRenuByName() {
        assertEquals("Renu", Constants.QUETZAL_RENU_NPC_NAME);
    }

    private static void assertStates(String patchType, PlantState expected, int... values) {
        for (int value : values) {
            assertEquals(patchType + " value " + value, expected, SpecialTreePatchChecker.classify(patchType, value));
        }
    }
}
