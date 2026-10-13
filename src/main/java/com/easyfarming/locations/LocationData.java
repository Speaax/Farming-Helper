package com.easyfarming.locations;

import com.easyfarming.EasyFarmingConfig;
import net.runelite.api.coords.WorldPoint;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Canonical declarative definition of a farming location and its teleport options.
 *
 * <p>Location definitions in {@code locations.*} are built as {@code LocationData}
 * (or via per-site {@code *LocationData} helpers), converted to runtime
 * {@link com.easyfarming.core.Location} instances by {@link LocationFactory},
 * and consumed by {@link com.easyfarming.customrun.LocationCatalog}.
 *
 * @see LocationFactory
 * @see TeleportData
 * @see com.easyfarming.core.Location
 */
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
