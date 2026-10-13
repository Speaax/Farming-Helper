package com.easyfarming.overlays.handlers.patch;

import net.runelite.api.coords.WorldPoint;

import java.util.List;

/**
 * Handler-owned helpers used when {@link PatchFactory} wires {@code *StepSupport} adapters.
 * Implemented by {@code FarmingStepHandler}. Public because the factory lives in this package
 * while the handler lives in the parent package.
 */
public interface PatchStepHost {
    int getHerbPatchVarbitId(Integer objectId);

    int getHopsPatchVarbitId(Integer objectId);

    int getFlowerPatchVarbitId(Integer objectId);

    int getAllotmentPatchVarbitId(int objectId);

    int getFruitTreePatchVarbitId(Integer objectId);

    WorldPoint getHerbPatchWorldPoint(String locationName);

    WorldPoint getHopsPatchWorldPoint(String locationName);

    WorldPoint getFlowerPatchWorldPoint(String locationName);

    WorldPoint getTreePatchWorldPoint(String locationName);

    WorldPoint getFruitTreePatchWorldPoint(String locationName);

    void applyPatchDirectionHintArrow(WorldPoint patchWorldPoint);

    void setHintArrowToNPC(String npcName);

    void setHintArrowToFirstAvailableNPC(List<String> npcNames);

    void clearHintArrow();

    boolean payForProtection();
}
