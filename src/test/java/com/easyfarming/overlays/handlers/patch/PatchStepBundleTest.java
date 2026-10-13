package com.easyfarming.overlays.handlers.patch;

import org.junit.Test;

import static org.junit.Assert.assertNull;

/**
 * Pure holder checks for {@link PatchStepBundle} (no client wiring).
 */
public class PatchStepBundleTest {
    @Test
    public void gettersReturnConstructorArguments() {
        PatchStepBundle bundle = new PatchStepBundle(null, null, null, null, null, null);
        assertNull(bundle.getHerb());
        assertNull(bundle.getHops());
        assertNull(bundle.getFlower());
        assertNull(bundle.getAllotment());
        assertNull(bundle.getTree());
        assertNull(bundle.getFruitTree());
    }
}
