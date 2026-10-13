package com.easyfarming.overlays.handlers;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CustomRunPatchProgressTest {

    @Test
    public void resetAllPatchDoneFlagsClearsOnlyDoneFlags() {
        CustomRunPatchProgress progress = new CustomRunPatchProgress();
        progress.setHerbPatchDone(true);
        progress.setFlowerPatchDone(true);
        progress.setAllotmentPatchDone(true);
        progress.setTreePatchDone(true);
        progress.setFruitTreePatchDone(true);
        progress.setHopsPatchDone(true);
        progress.setHerbPatchComposted(true);
        progress.getAllotmentPatchState().markComposted(0);

        progress.resetAllPatchDoneFlags();

        assertFalse(progress.isHerbPatchDone());
        assertFalse(progress.isFlowerPatchDone());
        assertFalse(progress.isAllotmentPatchDone());
        assertFalse(progress.isTreePatchDone());
        assertFalse(progress.isFruitTreePatchDone());
        assertFalse(progress.isHopsPatchDone());
        assertTrue(progress.isHerbPatchComposted());
        assertTrue(progress.getAllotmentPatchState().isPatchComposted(0));
    }

    @Test
    public void resetForNextLocationClearsDoneAndCompostButNotAllotment() {
        CustomRunPatchProgress progress = new CustomRunPatchProgress();
        progress.setHerbPatchDone(true);
        progress.setHopsPatchDone(true);
        progress.setHerbPatchComposted(true);
        progress.setHopsPatchComposted(true);
        progress.getAllotmentPatchState().moveToNextPatch();
        progress.getAllotmentPatchState().markComposted(1);

        progress.resetForNextLocation();

        assertFalse(progress.isHerbPatchDone());
        assertFalse(progress.isHopsPatchDone());
        assertFalse(progress.isHerbPatchComposted());
        assertFalse(progress.isHopsPatchComposted());
        assertEquals(1, progress.getAllotmentPatchState().getCurrentIndex());
        assertTrue(progress.getAllotmentPatchState().isPatchComposted(1));
    }

    @Test
    public void resetAllotmentStateRestoresNorthPatch() {
        CustomRunPatchProgress progress = new CustomRunPatchProgress();
        progress.getAllotmentPatchState().markComposted(0);
        progress.getAllotmentPatchState().moveToNextPatch();
        progress.getAllotmentPatchState().markComposted(1);

        progress.resetAllotmentState();

        assertEquals(0, progress.getAllotmentPatchState().getCurrentIndex());
        assertFalse(progress.getAllotmentPatchState().isPatchCompleted(0));
        assertFalse(progress.getAllotmentPatchState().isPatchCompleted(1));
        assertFalse(progress.getAllotmentPatchState().isPatchComposted(0));
        assertFalse(progress.getAllotmentPatchState().isPatchComposted(1));
    }
}
