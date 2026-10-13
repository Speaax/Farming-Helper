package com.easyfarming.overlays.handlers.patch;

/**
 * One unit of patch-run processing for a frame (highlight + instruction text).
 * Graphics and other frame data live on {@link PatchStepContext}.
 */
public interface PatchStep {
    void process(PatchStepContext context);
}
