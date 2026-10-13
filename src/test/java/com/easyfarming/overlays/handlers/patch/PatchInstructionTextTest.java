package com.easyfarming.overlays.handlers.patch;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Shared patch instruction strings must stay non-empty and herb/hops-aligned.
 */
public class PatchInstructionTextTest {

    @Test
    public void herbInstructionsAreNonEmpty() {
        assertNonEmpty(PatchInstructionText.herbHarvest());
        assertNonEmpty(PatchInstructionText.herbPlantSeed());
        assertNonEmpty(PatchInstructionText.herbClearDead());
        assertNonEmpty(PatchInstructionText.herbPlantCure());
        assertNonEmpty(PatchInstructionText.herbRake());
        assertNonEmpty(PatchInstructionText.herbUnknown());
    }

    @Test
    public void hopsInstructionsAreNonEmpty() {
        assertNonEmpty(PatchInstructionText.hopsHarvest());
        assertNonEmpty(PatchInstructionText.hopsPlantSeed());
        assertNonEmpty(PatchInstructionText.hopsClearDead());
        assertNonEmpty(PatchInstructionText.hopsPlantCure());
        assertNonEmpty(PatchInstructionText.hopsRake());
        assertNonEmpty(PatchInstructionText.hopsUnknown());
        assertNonEmpty(PatchInstructionText.hopsFlyRenuToAldarin());
    }

    @Test
    public void compostInstructionIsShared() {
        assertEquals("Use Compost on patch.", PatchInstructionText.useCompost());
        assertEquals(PatchInstructionText.USE_COMPOST_ON_PATCH, PatchInstructionText.useCompost());
    }

    @Test
    public void herbAndHopsUnknownMentionTheirPatchType() {
        assertTrue(PatchInstructionText.herbUnknown().contains("herb"));
        assertTrue(PatchInstructionText.hopsUnknown().contains("hops"));
    }

    private static void assertNonEmpty(String text) {
        assertFalse(text == null || text.trim().isEmpty());
    }
}
