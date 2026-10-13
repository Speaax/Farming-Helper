package com.easyfarming.customrun;

/**
 * Tracks whether the item-checklist phase is complete for the <em>current</em> run only.
 * <p>
 * A plain boolean gets stuck {@code true} across runs when a reset is missed or a stale
 * overlay frame races with start. Bumping {@link #runId} on each start makes any prior
 * "collected" flag automatically invalid.
 */
public final class ItemGatheringState {

    private int runId = 0;
    /** {@link #runId} for which gathering was completed; {@code -1} = not complete. */
    private int collectedForRunId = -1;

    /** Starts a new gathering cycle; previous completion no longer counts. */
    public synchronized void beginNewRun() {
        runId++;
        collectedForRunId = -1;
    }

    /** Marks gathering incomplete without starting a new run (e.g. stop/remove overlay). */
    public synchronized void reset() {
        collectedForRunId = -1;
    }

    public synchronized boolean isCollected() {
        return collectedForRunId == runId && runId > 0;
    }

    /**
     * @param collected {@code true} completes gathering for the current run;
     *                  {@code false} clears completion for the current run
     */
    public synchronized void setCollected(boolean collected) {
        if (runId <= 0) {
            return;
        }
        collectedForRunId = collected ? runId : -1;
    }

    /** Test/helper: current run id (0 before first {@link #beginNewRun()}). */
    synchronized int getRunId() {
        return runId;
    }
}
