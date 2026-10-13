package com.easyfarming.customrun;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ItemGatheringStateTest {

    @Test
    public void newRunInvalidatesPreviousCompletion() {
        ItemGatheringState state = new ItemGatheringState();
        assertFalse(state.isCollected());

        state.beginNewRun();
        assertFalse(state.isCollected());

        state.setCollected(true);
        assertTrue(state.isCollected());

        state.beginNewRun();
        assertFalse("starting run B must clear run A's collected flag", state.isCollected());
    }

    @Test
    public void resetClearsCompletionWithoutNewRun() {
        ItemGatheringState state = new ItemGatheringState();
        state.beginNewRun();
        state.setCollected(true);
        assertTrue(state.isCollected());

        state.reset();
        assertFalse(state.isCollected());

        state.setCollected(true);
        assertTrue(state.isCollected());
    }

    @Test
    public void setCollectedFalseClearsCurrentRun() {
        ItemGatheringState state = new ItemGatheringState();
        state.beginNewRun();
        state.setCollected(true);
        state.setCollected(false);
        assertFalse(state.isCollected());
    }

    @Test
    public void setCollectedBeforeBeginNewRunIsIgnored() {
        ItemGatheringState state = new ItemGatheringState();
        state.setCollected(true);
        assertFalse(state.isCollected());
    }
}
