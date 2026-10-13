package com.easyfarming.overlays.handlers;

/**
 * Tracks per-patch-type done and composted flags for a custom farming run,
 * plus allotment north/south state. Owned by {@link FarmingStepHandler}; the
 * teleport overlay must use handler accessors rather than mutating fields.
 */
public class CustomRunPatchProgress {
    private boolean herbPatchDone;
    private boolean flowerPatchDone;
    private boolean allotmentPatchDone;
    private boolean treePatchDone;
    private boolean fruitTreePatchDone;
    private boolean hopsPatchDone;

    private boolean herbPatchComposted;
    private boolean flowerPatchComposted;
    private boolean treePatchComposted;
    private boolean fruitTreePatchComposted;
    private boolean hopsPatchComposted;

    private final AllotmentPatchState allotmentPatchState = new AllotmentPatchState();

    public boolean isHerbPatchDone() {
        return herbPatchDone;
    }

    public void setHerbPatchDone(boolean herbPatchDone) {
        this.herbPatchDone = herbPatchDone;
    }

    public boolean isFlowerPatchDone() {
        return flowerPatchDone;
    }

    public void setFlowerPatchDone(boolean flowerPatchDone) {
        this.flowerPatchDone = flowerPatchDone;
    }

    public boolean isAllotmentPatchDone() {
        return allotmentPatchDone;
    }

    public void setAllotmentPatchDone(boolean allotmentPatchDone) {
        this.allotmentPatchDone = allotmentPatchDone;
    }

    public boolean isTreePatchDone() {
        return treePatchDone;
    }

    public void setTreePatchDone(boolean treePatchDone) {
        this.treePatchDone = treePatchDone;
    }

    public boolean isFruitTreePatchDone() {
        return fruitTreePatchDone;
    }

    public void setFruitTreePatchDone(boolean fruitTreePatchDone) {
        this.fruitTreePatchDone = fruitTreePatchDone;
    }

    public boolean isHopsPatchDone() {
        return hopsPatchDone;
    }

    public void setHopsPatchDone(boolean hopsPatchDone) {
        this.hopsPatchDone = hopsPatchDone;
    }

    public boolean isHerbPatchComposted() {
        return herbPatchComposted;
    }

    public void setHerbPatchComposted(boolean herbPatchComposted) {
        this.herbPatchComposted = herbPatchComposted;
    }

    public boolean isFlowerPatchComposted() {
        return flowerPatchComposted;
    }

    public void setFlowerPatchComposted(boolean flowerPatchComposted) {
        this.flowerPatchComposted = flowerPatchComposted;
    }

    public boolean isTreePatchComposted() {
        return treePatchComposted;
    }

    public void setTreePatchComposted(boolean treePatchComposted) {
        this.treePatchComposted = treePatchComposted;
    }

    public boolean isFruitTreePatchComposted() {
        return fruitTreePatchComposted;
    }

    public void setFruitTreePatchComposted(boolean fruitTreePatchComposted) {
        this.fruitTreePatchComposted = fruitTreePatchComposted;
    }

    public boolean isHopsPatchComposted() {
        return hopsPatchComposted;
    }

    public void setHopsPatchComposted(boolean hopsPatchComposted) {
        this.hopsPatchComposted = hopsPatchComposted;
    }

    public AllotmentPatchState getAllotmentPatchState() {
        return allotmentPatchState;
    }

    /**
     * Clears all patch-type done flags (does not touch compost or allotment state).
     */
    public void resetAllPatchDoneFlags() {
        herbPatchDone = false;
        flowerPatchDone = false;
        allotmentPatchDone = false;
        treePatchDone = false;
        fruitTreePatchDone = false;
        hopsPatchDone = false;
    }

    /**
     * Clears sticky compost flags for non-allotment patch types.
     */
    public void resetCompostStates() {
        herbPatchComposted = false;
        flowerPatchComposted = false;
        treePatchComposted = false;
        fruitTreePatchComposted = false;
        hopsPatchComposted = false;
    }

    /**
     * Resets allotment north/south index, completion, and compost tracking.
     */
    public void resetAllotmentState() {
        allotmentPatchState.reset();
    }

    /**
     * Reset used when advancing to the next patch type or location in a custom run:
     * clears done flags and sticky compost states. Matches historical overlay behavior
     * (allotment state is left alone here; allotment steps reset themselves when finished).
     */
    public void resetForNextLocation() {
        resetAllPatchDoneFlags();
        resetCompostStates();
    }

    /**
     * Encapsulates allotment patch state tracking.
     * Manages current patch index, completion status, and compost state for both patches.
     */
    public static class AllotmentPatchState {
        private int currentIndex = 0;
        private final boolean[] completed = new boolean[2];
        private final boolean[] composted = new boolean[2];

        public int getCurrentIndex() {
            return currentIndex;
        }

        public boolean isPatchCompleted(int index) {
            if (index < 0 || index >= completed.length) {
                throw new IllegalArgumentException("Invalid patch index: " + index);
            }
            return completed[index];
        }

        public boolean isPatchComposted(int index) {
            if (index < 0 || index >= composted.length) {
                throw new IllegalArgumentException("Invalid patch index: " + index);
            }
            return composted[index];
        }

        public void markComposted(int index) {
            if (index < 0 || index >= composted.length) {
                throw new IllegalArgumentException("Invalid patch index: " + index);
            }
            composted[index] = true;
            completed[index] = true;
        }

        public void setPatchCompleted(int index, boolean value) {
            if (index < 0 || index >= completed.length) {
                throw new IllegalArgumentException("Invalid patch index: " + index);
            }
            completed[index] = value;
        }

        public void moveToNextPatch() {
            currentIndex = 1;
            completed[1] = false;
        }

        public void reset() {
            currentIndex = 0;
            completed[0] = false;
            completed[1] = false;
            composted[0] = false;
            composted[1] = false;
        }
    }
}
