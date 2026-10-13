package com.easyfarming.customrun;

import net.runelite.api.gameval.ItemID;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Regression: catalog lookup failure must not drop seed/compost requirements.
 */
public class CustomRunItemRequirementsTest {

    @Test
    public void countsSeedsEvenWhenCatalogCannotResolveLocation() {
        RunLocation rl = new RunLocation(
                "Farming Guild",
                "Fairy_Ring",
                Collections.singletonList(PatchTypes.HERB));

        Map<Integer, Integer> reqs = CustomRunItemRequirements.buildRequirements(
                null,
                null,
                null,
                Collections.singletonList(rl),
                true,
                true,
                true);

        assertEquals(Integer.valueOf(1), reqs.get(ItemID.GUAM_SEED));
        assertEquals(Integer.valueOf(1), reqs.get(ItemID.SPADE));
        assertTrue(reqs.size() >= 2);
    }

    @Test
    public void countsMultiplePatchTypesWithoutCatalog() {
        RunLocation rl = new RunLocation(
                "Falador",
                "None",
                Arrays.asList(PatchTypes.HERB, PatchTypes.FLOWER, PatchTypes.ALLOTMENT));

        Map<Integer, Integer> reqs = CustomRunItemRequirements.buildRequirements(
                null,
                null,
                null,
                Collections.singletonList(rl),
                false,
                false,
                false);

        assertEquals(Integer.valueOf(1), reqs.get(ItemID.GUAM_SEED));
        assertEquals(Integer.valueOf(1), reqs.get(ItemID.LIMPWURT_SEED));
        // Falador has 2 allotment patches × 3 seeds
        assertEquals(Integer.valueOf(6), reqs.get(com.easyfarming.utils.Constants.BASE_ALLOTMENT_SEED_ID));
    }
}
