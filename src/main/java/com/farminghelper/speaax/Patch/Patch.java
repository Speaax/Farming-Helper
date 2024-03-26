package com.farminghelper.speaax.Patch;

import com.farminghelper.speaax.FarmingHelperConfig;

import net.runelite.api.coords.WorldPoint;

import java.util.function.Function;

public class Patch
{
    private WorldPoint worldPoint;

    private final Function<FarmingHelperConfig, FarmingHelperConfig.OptionEnumTeleport> desiredTeleportFunction;

    private Boolean requiresLimpwurt;

    public Patch(
        WorldPoint worldPoint,
        Function<FarmingHelperConfig, FarmingHelperConfig.OptionEnumTeleport> desiredTeleportFunction,
        Boolean requiresLimpwurt
    ) {
        this.worldPoint = worldPoint;
        this.desiredTeleportFunction = desiredTeleportFunction;
        this.requiresLimpwurt = requiresLimpwurt;
    }

    public WorldPoint worldPoint()
    {
        return this.worldPoint;
    }

    public Function<FarmingHelperConfig, FarmingHelperConfig.OptionEnumTeleport> getDesiredTeleportFunction()
    {
        return this.desiredTeleportFunction;
    }

    public Boolean shouldFarmLimpwurts()
    {
        return this.requiresLimpwurt;
    }
}
