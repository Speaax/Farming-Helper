package com.easyfarming.ui;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StepEditPencilVisibilityTest {

    @Test
    public void hiddenWhenNoRunActive() {
        assertFalse(CustomRunLocationSubPanel.shouldShowStepEditPencil(
                false, "Farming Guild", "Farming Guild"));
    }

    @Test
    public void shownOnlyOnActiveLocation() {
        assertTrue(CustomRunLocationSubPanel.shouldShowStepEditPencil(
                true, "Farming Guild", "Farming Guild"));
        assertFalse(CustomRunLocationSubPanel.shouldShowStepEditPencil(
                true, "Farming Guild", "Ardougne"));
    }

    @Test
    public void hiddenWhenActiveLocationUnknown() {
        assertFalse(CustomRunLocationSubPanel.shouldShowStepEditPencil(
                true, null, "Farming Guild"));
    }
}
