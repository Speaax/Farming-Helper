package com.easyfarming.core;

import com.easyfarming.EasyFarmingConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Location {
    private String name;
    private Boolean farmLimps;
    private List<Teleport> teleportOptions;
    private EasyFarmingConfig config;
    private final Function<EasyFarmingConfig, EasyFarmingConfig.OptionEnumTeleport> selectedTeleportFunction;

    public Location(Function<EasyFarmingConfig, EasyFarmingConfig.OptionEnumTeleport> selectedTeleportFunction,
                   EasyFarmingConfig config, String name, Boolean farmLimps) {
        this.config = config;
        this.selectedTeleportFunction = selectedTeleportFunction;
        this.name = name;
        this.farmLimps = farmLimps;
        this.teleportOptions = new ArrayList<>();
    }

    public void addTeleportOption(Teleport teleport) {
        teleportOptions.add(teleport);
    }

    public Teleport getSelectedTeleport() {
        String selectedEnumOption = selectedTeleportFunction.apply(config).name();
        for (Teleport teleport : teleportOptions) {
            if (teleport.getEnumOption().equalsIgnoreCase(selectedEnumOption)) {
                return teleport;
            }
        }
        return teleportOptions.isEmpty() ? null : teleportOptions.get(0);
    }

    // Getters
    public String getName() { return name; }
    public Boolean getFarmLimps() { return farmLimps; }
    public List<Teleport> getTeleportOptions() { return teleportOptions; }
}

