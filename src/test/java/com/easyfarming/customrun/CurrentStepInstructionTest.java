package com.easyfarming.customrun;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class CurrentStepInstructionTest {

    @Test
    public void equalsMatchesSameFields() {
        CurrentStepInstruction a = CurrentStepInstruction.navigation(
                "Seers Village", "Portal_Nexus_Camelot", "Teleport to Camelot.");
        CurrentStepInstruction b = CurrentStepInstruction.navigation(
                "Seers Village", "Portal_Nexus_Camelot", "Teleport to Camelot.");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void farmingStepsWithDifferentDefaultTextAreNotEqual() {
        CurrentStepInstruction harvest = CurrentStepInstruction.farming("Ardougne", "Harvest Herbs.");
        CurrentStepInstruction note = CurrentStepInstruction.farming("Ardougne", "Note produce.");

        assertNotEquals(harvest, note);
        assertFalse(harvest.equals(note));
    }

    @Test
    public void navigationIdentityFormatUnchanged() {
        CurrentStepInstruction step = CurrentStepInstruction.navigation(
                "Catherby", "Portal_Nexus_Camelot", "Teleport to Camelot.");

        assertEquals("nav|Catherby|Portal_Nexus_Camelot", step.identity());
    }

    @Test
    public void navigationIdentityOmitsDefaultText() {
        CurrentStepInstruction a = CurrentStepInstruction.navigation(
                "Falador", "Skills_Necklace", "Original text.");
        CurrentStepInstruction b = CurrentStepInstruction.navigation(
                "Falador", "Skills_Necklace", "Edited text.");

        assertEquals(a.identity(), b.identity());
        assertNotEquals(a, b);
    }

    @Test
    public void farmingIdentityIncludesDefaultText() {
        CurrentStepInstruction step = CurrentStepInstruction.farming("Farming Guild", "Harvest Herbs.");

        assertEquals("farm|Farming Guild|Harvest Herbs.", step.identity());
    }

    @Test
    public void nullDefaultTextNormalizedForEquality() {
        CurrentStepInstruction a = CurrentStepInstruction.farming("Lumbridge", null);
        CurrentStepInstruction b = CurrentStepInstruction.farming("Lumbridge", "");

        assertTrue(a.equals(b));
        assertEquals(a.hashCode(), b.hashCode());
    }
}
