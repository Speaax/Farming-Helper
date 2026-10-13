package com.easyfarming.customrun;

/**
 * Explicit custom-run phase. The item checklist is shown only during {@link #GATHERING};
 * navigation/farming run during {@link #NAVIGATING}. Using a phase (not a sticky boolean)
 * prevents a prior run's "items collected" from skipping the checklist on the next start.
 */
public enum CustomRunPhase {
    IDLE,
    GATHERING,
    NAVIGATING
}
