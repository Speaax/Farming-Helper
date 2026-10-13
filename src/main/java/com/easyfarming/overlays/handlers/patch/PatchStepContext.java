package com.easyfarming.overlays.handlers.patch;

import com.easyfarming.core.Teleport;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Immutable-ish frame context for one patch-step processing pass.
 * Built by {@link com.easyfarming.overlays.handlers.FarmingStepHandler} (and later
 * dedicated step classes) before calling {@link PatchStep#process(PatchStepContext)}.
 */
public final class PatchStepContext {

    private final Graphics2D graphics;
    private final Teleport teleport;
    private final int regionId;
    private final String locationName;
    private final Color leftClickColor;
    private final Color useItemColor;
    private final Integer patchObjectId;
    private final int varbitId;
    private final boolean useSpecificPatch;

    private PatchStepContext(Builder builder) {
        this.graphics = builder.graphics;
        this.teleport = builder.teleport;
        this.regionId = builder.regionId;
        this.locationName = builder.locationName;
        this.leftClickColor = builder.leftClickColor;
        this.useItemColor = builder.useItemColor;
        this.patchObjectId = builder.patchObjectId;
        this.varbitId = builder.varbitId;
        this.useSpecificPatch = builder.useSpecificPatch;
    }

    public Graphics2D getGraphics() {
        return graphics;
    }

    public Teleport getTeleport() {
        return teleport;
    }

    public int getRegionId() {
        return regionId;
    }

    public String getLocationName() {
        return locationName;
    }

    public Color getLeftClickColor() {
        return leftClickColor;
    }

    public Color getUseItemColor() {
        return useItemColor;
    }

    public Integer getPatchObjectId() {
        return patchObjectId;
    }

    public int getVarbitId() {
        return varbitId;
    }

    public boolean isUseSpecificPatch() {
        return useSpecificPatch;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Graphics2D graphics;
        private Teleport teleport;
        private int regionId;
        private String locationName;
        private Color leftClickColor;
        private Color useItemColor;
        private Integer patchObjectId;
        private int varbitId = -1;
        private boolean useSpecificPatch;

        private Builder() {
        }

        public Builder graphics(Graphics2D graphics) {
            this.graphics = graphics;
            return this;
        }

        public Builder teleport(Teleport teleport) {
            this.teleport = teleport;
            return this;
        }

        public Builder regionId(int regionId) {
            this.regionId = regionId;
            return this;
        }

        public Builder locationName(String locationName) {
            this.locationName = locationName;
            return this;
        }

        public Builder leftClickColor(Color leftClickColor) {
            this.leftClickColor = leftClickColor;
            return this;
        }

        public Builder useItemColor(Color useItemColor) {
            this.useItemColor = useItemColor;
            return this;
        }

        public Builder patchObjectId(Integer patchObjectId) {
            this.patchObjectId = patchObjectId;
            return this;
        }

        public Builder varbitId(int varbitId) {
            this.varbitId = varbitId;
            return this;
        }

        public Builder useSpecificPatch(boolean useSpecificPatch) {
            this.useSpecificPatch = useSpecificPatch;
            return this;
        }

        public PatchStepContext build() {
            return new PatchStepContext(this);
        }
    }
}
