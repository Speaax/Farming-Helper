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
    private String overrideTeleportName;
    private List<String> customPatchOrder;
    private java.util.Map<String, Boolean> customPatchStates;
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
        String selectedEnumOption = overrideTeleportName != null ? overrideTeleportName : selectedTeleportFunction.apply(config).name();
        for (Teleport teleport : teleportOptions) {
            String nameToMatch = overrideTeleportName != null ? teleport.getEnumOption() : teleport.getEnumOption();
            // In CustomRuns we store the teleport.getEnumOption() directly, not the Enum string format
            if (nameToMatch.equalsIgnoreCase(selectedEnumOption) || teleport.getEnumOption().equalsIgnoreCase(selectedEnumOption)) {
                return teleport;
            }
        }
        return teleportOptions.isEmpty() ? null : teleportOptions.get(0);
    }

    // Getters
    public String getName() { return name; }
    public Boolean getFarmLimps() { return farmLimps; }
    public List<Teleport> getTeleportOptions() { return teleportOptions; }
    
    // Custom Run Getters/Setters
    public void setOverrideTeleportName(String name) { this.overrideTeleportName = name; }
    public void setCustomPatchOrder(List<String> order) { this.customPatchOrder = order; }
    public void setCustomPatchStates(java.util.Map<String, Boolean> states) { this.customPatchStates = states; }
    
    public List<String> getCustomPatchOrder() { return customPatchOrder; }
    public java.util.Map<String, Boolean> getCustomPatchStates() { return customPatchStates; }
}

