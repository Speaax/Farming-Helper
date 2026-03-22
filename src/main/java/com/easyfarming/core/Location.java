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
    /** When set (e.g. by a custom run), this overrides config-based selection. */
    private String overrideTeleportEnumOption;

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
        String configEnumOption = selectedTeleportFunction != null && config != null
                ? selectedTeleportFunction.apply(config).name()
                : null;
        String selectedEnumOption = overrideTeleportEnumOption != null
                ? overrideTeleportEnumOption
                : configEnumOption;
        if (selectedEnumOption != null) {
            for (Teleport teleport : teleportOptions) {
                String opt = teleport.getEnumOption();
                if (opt != null && selectedEnumOption.equalsIgnoreCase(opt)) {
                    return teleport;
                }
            }
            // Custom run may store a teleport id from another patch type at the same location (e.g. tree
            // spellbook was "Teleport" while herb uses "Falador_Teleport"). If override does not match this
            // Location's list, fall back to the configured default for this Location.
            if (overrideTeleportEnumOption != null && configEnumOption != null) {
                for (Teleport teleport : teleportOptions) {
                    String opt = teleport.getEnumOption();
                    if (opt != null && configEnumOption.equalsIgnoreCase(opt)) {
                        return teleport;
                    }
                }
            }
        }
        return teleportOptions.isEmpty() ? null : teleportOptions.get(0);
    }

    public void setOverrideTeleportEnumOption(String enumOption) {
        this.overrideTeleportEnumOption = enumOption;
    }

    public String getOverrideTeleportEnumOption() {
        return overrideTeleportEnumOption;
    }

    // Getters
    public String getName() { return name; }
    public Boolean getFarmLimps() { return farmLimps; }
    public List<Teleport> getTeleportOptions() { return teleportOptions; }
}

