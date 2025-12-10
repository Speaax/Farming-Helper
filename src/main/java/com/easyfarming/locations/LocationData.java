package com.easyfarming.locations;

import com.easyfarming.EasyFarmingConfig;
import net.runelite.api.coords.WorldPoint;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * NOTE: This class is part of an incomplete refactoring effort.
 * 
 * This class and related classes in the locations.* package were designed to replace
 * the current ItemsAndLocations.* package structure, but the migration was never completed.
 * 
 * Part of an incomplete refactor; do not modify without coordinating.
 * 
 * @deprecated This is part of an incomplete refactor. The data-driven approach using this class
 *             is still being used, but the architecture may change in future refactors.
 */
@Deprecated
public class LocationData {
    private final String name;
    private final boolean farmLimps;
    private final WorldPoint patchPoint;
    private final Function<EasyFarmingConfig, EasyFarmingConfig.OptionEnumTeleport> configFunction;
    private final List<TeleportData> teleportOptions;
    
    public LocationData(String name, boolean farmLimps, WorldPoint patchPoint,
                       Function<EasyFarmingConfig, EasyFarmingConfig.OptionEnumTeleport> configFunction) {
        this.name = name;
        this.farmLimps = farmLimps;
        this.patchPoint = patchPoint;
        this.configFunction = configFunction;
        this.teleportOptions = new ArrayList<>();
    }
    
    public LocationData addTeleport(TeleportData teleportData) {
        teleportOptions.add(teleportData);
        return this;
    }
    
    // Getters
    public String getName() { return name; }
    public boolean getFarmLimps() { return farmLimps; }
    public WorldPoint getPatchPoint() { return patchPoint; }
    public Function<EasyFarmingConfig, EasyFarmingConfig.OptionEnumTeleport> getConfigFunction() { return configFunction; }
    public List<TeleportData> getTeleportOptions() { return teleportOptions; }
}

