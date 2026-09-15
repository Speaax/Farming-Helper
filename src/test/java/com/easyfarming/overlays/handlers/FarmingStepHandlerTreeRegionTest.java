package com.easyfarming.overlays.handlers;

import com.easyfarming.utils.Constants;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Region → tree location mapping used for patch anchors and hint arrows.
 * Wrong mapping at Falador Park previously pointed the REMOVE-step arrow at Taverley
 * instead of Heskel (issue #101).
 */
public class FarmingStepHandlerTreeRegionTest {

    @Test
    public void faladorParkRegionMapsToFaladorNotTaverley() {
        // RuneLite FarmingWorld: Falador TREE primary 11828, extra 12084
        assertEquals("Falador", FarmingStepHandler.getTreeLocationNameFromRegionId(Constants.REGION_FALADOR_PARK));
        assertEquals("Falador", FarmingStepHandler.getTreeLocationNameFromRegionId(Constants.REGION_FALADOR_PARK_ALT));
        assertEquals("Falador", FarmingStepHandler.getTreeLocationNameFromRegionId(11828));
        assertEquals("Falador", FarmingStepHandler.getTreeLocationNameFromRegionId(12084));
    }

    @Test
    public void taverleyRegionMapsToTaverley() {
        // RuneLite FarmingWorld: Taverley TREE primary 11573, extra 11829
        assertEquals("Taverley", FarmingStepHandler.getTreeLocationNameFromRegionId(Constants.REGION_TAVERLEY));
        assertEquals("Taverley", FarmingStepHandler.getTreeLocationNameFromRegionId(Constants.REGION_TAVERLEY_ALT));
        assertEquals("Taverley", FarmingStepHandler.getTreeLocationNameFromRegionId(11573));
        assertEquals("Taverley", FarmingStepHandler.getTreeLocationNameFromRegionId(11829));
    }

    @Test
    public void faladorHerbRegionStillMapsToFalador() {
        assertEquals("Falador", FarmingStepHandler.getTreeLocationNameFromRegionId(Constants.REGION_FALADOR));
    }

    @Test
    public void unknownRegionReturnsNull() {
        assertNull(FarmingStepHandler.getTreeLocationNameFromRegionId(0));
    }
}
