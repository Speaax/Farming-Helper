package com.easyfarming.overlays.handlers.patch;

import com.easyfarming.locations.RegionLocationRegistry;
import com.easyfarming.utils.Constants;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * Thin wrapper around {@link RegionLocationRegistry} for patch-step location names,
 * plus helpers for region resolution and herb varbit fallbacks.
 */
public final class PatchLocationResolver {

    private PatchLocationResolver() {
    }

    /**
     * Region ID of the local player, or empty if the client has no local player.
     */
    public static OptionalInt resolveRegionId(Client client) {
        if (client == null || client.getLocalPlayer() == null) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(client.getLocalPlayer().getWorldLocation().getRegionID());
    }

    /**
     * Local player world point, or empty if unavailable.
     */
    public static Optional<WorldPoint> resolvePlayerLocation(Client client) {
        if (client == null || client.getLocalPlayer() == null) {
            return Optional.empty();
        }
        return Optional.of(client.getLocalPlayer().getWorldLocation());
    }

    public static String herbLocationName(int regionId) {
        return RegionLocationRegistry.getHerbLocationName(regionId);
    }

    public static String hopsLocationName(int regionId) {
        return RegionLocationRegistry.getHopsLocationName(regionId);
    }

    public static String fruitTreeLocationName(int regionId, WorldPoint playerLocation) {
        return RegionLocationRegistry.getFruitTreeLocationName(regionId, playerLocation);
    }

    public static String fruitTreeLocationName(int regionId) {
        return RegionLocationRegistry.getFruitTreeLocationName(regionId);
    }

    public static String treeLocationName(int regionId) {
        return RegionLocationRegistry.getTreeLocationNameFromRegionId(regionId);
    }

    /**
     * Location-specific herb patch varbit when object composition lookup fails.
     * Mirrors the fallback block historically in {@code FarmingStepHandler.herbSteps}.
     */
    public static int herbVarbitFallbackByRegion(int regionId) {
        if (regionId == Constants.REGION_FARMING_GUILD) {
            return Constants.VARBIT_HERB_PATCH_FARMING_GUILD;
        }
        if (regionId == Constants.REGION_HARMONY) {
            return Constants.VARBIT_HERB_PATCH_HARMONY;
        }
        if (regionId == Constants.REGION_TROLL_STRONGHOLD || regionId == Constants.REGION_WEISS) {
            return Constants.VARBIT_HERB_PATCH_TROLL_WEISS;
        }
        return Constants.VARBIT_HERB_PATCH_STANDARD;
    }
}
