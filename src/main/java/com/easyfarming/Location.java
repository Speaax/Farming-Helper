package com.easyfarming;

import com.easyfarming.core.Teleport;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Location {
    private String name;

    public String getName() {
        return name;
    }
    private Boolean farmLimps;

    public Boolean getFarmLimps() {
        return farmLimps;
    }

    private List<Teleport> teleportOptions;
    private EasyFarmingConfig config;
    private final Function<EasyFarmingConfig, EasyFarmingConfig.OptionEnumTeleport> selectedTeleportFunction;

    public Location(Function<EasyFarmingConfig, EasyFarmingConfig.OptionEnumTeleport> selectedTeleportFunction, EasyFarmingConfig config, String name, Boolean farmLimps) {
        this.config = config;
        this.selectedTeleportFunction = selectedTeleportFunction;
        this.name = name;
        this.farmLimps = farmLimps;
        this.teleportOptions = new ArrayList<>();
    }

    public void addTeleportOption(Teleport teleport) {
        teleportOptions.add(teleport);
    }

    /**
     * Gets the selected teleport based on the configuration.
     * 
     * @return the selected core.Teleport instance, or null if no teleport is available
     */
    public Teleport getSelectedTeleport() {
        String selectedEnumOption = selectedTeleportFunction.apply(config).name();
        for (Teleport teleport : teleportOptions) {
            if (teleport.getEnumOption().equalsIgnoreCase(selectedEnumOption)) {
                return teleport;
            }
        }
        return teleportOptions.isEmpty() ? null : teleportOptions.get(0);
    }

    /**
     * Gets the selected teleport as a core.Teleport instance.
     * This method is kept for backward compatibility but now simply delegates to getSelectedTeleport().
     * 
     * @return a core.Teleport instance representing the selected teleport, or null if no teleport is selected
     * @deprecated Use getSelectedTeleport() instead, as it now returns core.Teleport directly
     */
    @Deprecated
    public Teleport getSelectedCoreTeleport() {
        return getSelectedTeleport();
    }

}