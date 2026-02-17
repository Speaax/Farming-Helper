package com.easyfarming;

import java.awt.*;
import javax.inject.Inject;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import com.easyfarming.core.Teleport;
import com.easyfarming.core.Location;
import com.easyfarming.ItemsAndLocations.HerbRunItemAndLocation;
import com.easyfarming.ItemsAndLocations.TreeRunItemAndLocation;
import com.easyfarming.ItemsAndLocations.FruitTreeRunItemAndLocation;
import com.easyfarming.ItemsAndLocations.HopsRunItemAndLocation;
import com.easyfarming.overlays.handlers.NavigationHandler;
import com.easyfarming.overlays.handlers.FarmingStepHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FarmingTeleportOverlay extends Overlay {
    private final Client client;
    private final EasyFarmingPlugin plugin;
    private final EasyFarmingConfig config;
    private final AreaCheck areaCheck;
    
    @Inject
    private EasyFarmingOverlay farmingHelperOverlay;
    @Inject
    private EasyFarmingOverlayInfoBox farmingHelperOverlayInfoBox;
    @Inject
    private NavigationHandler navigationHandler;
    @Inject
    private FarmingStepHandler farmingStepHandler;
    
    // Run state
    public Boolean herbRun = false;
    public Boolean treeRun = false;
    public Boolean fruitTreeRun = false;
    public Boolean hopsRun = false;
    
    // Location tracking
    private int currentLocationIndex = 0;
    private List<Location> enabledLocations = new ArrayList<>();
    
    // Farming state
    private int subCase = 1;
    private boolean startSubCases = false;
    private boolean isAtDestination = false;
    private boolean farmLimps = false;

    @Inject
    public FarmingTeleportOverlay(EasyFarmingPlugin plugin, Client client, AreaCheck areaCheck, 
                                   EasyFarmingConfig config) {
        this.areaCheck = areaCheck;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.plugin = plugin;
        this.client = client;
        this.config = config;
    }

    /**
     * Gets the list of enabled locations for the current run type in order.
     */
    private List<Location> getEnabledLocations() {
        List<Location> allLocations = new ArrayList<>();
        
        if (herbRun && plugin.herbRunItemAndLocation != null) {
            plugin.herbRunItemAndLocation.setupLocations();
            // Create a copy of the list to avoid reference issues
            allLocations = new ArrayList<>(plugin.herbRunItemAndLocation.locations);
        } else if (treeRun && plugin.treeRunItemAndLocation != null) {
            plugin.treeRunItemAndLocation.setupLocations();
            allLocations = new ArrayList<>(plugin.treeRunItemAndLocation.locations);
        } else if (fruitTreeRun && plugin.fruitTreeRunItemAndLocation != null) {
            plugin.fruitTreeRunItemAndLocation.setupLocations();
            allLocations = new ArrayList<>(plugin.fruitTreeRunItemAndLocation.locations);
        } else if (hopsRun && plugin.hopsRunItemAndLocation != null) {
            plugin.hopsRunItemAndLocation.setupLocations();
            allLocations = new ArrayList<>(plugin.hopsRunItemAndLocation.locations);
        }
        
        // Filter to only enabled locations
        return allLocations.stream()
            .filter(location -> isLocationEnabled(location))
            .collect(Collectors.toList());
    }
    
    /**
     * Checks if a location is enabled for the current run type.
     */
    private boolean isLocationEnabled(Location location) {
        String locationName = location.getName();
        
        if (herbRun) {
            return plugin.getHerbLocationEnabled(locationName);
        } else if (treeRun) {
            return plugin.getTreeLocationEnabled(locationName);
        } else if (fruitTreeRun) {
            return plugin.getFruitTreeLocationEnabled(locationName);
        } else if (hopsRun) {
            return plugin.getHopsLocationEnabled(locationName);
        }
        
        return false;
    }
    
    /**
     * Gets the patch point for the current location based on run type.
     */
    private WorldPoint getPatchPointForLocation(Location location) {
        String locationName = location.getName();
        
        if (herbRun) {
            return getHerbPatchPoint(locationName);
        } else if (treeRun) {
            return getTreePatchPoint(locationName);
        } else if (fruitTreeRun) {
            return getFruitTreePatchPoint(locationName);
        } else if (hopsRun) {
            return getHopsPatchPoint(locationName);
        }
        
        return null;
    }
    
    /**
     * Gets herb patch coordinates for a location.
     * TODO: Move these to Location objects or a constants class
     */
    private WorldPoint getHerbPatchPoint(String locationName) {
        switch (locationName) {
            case "Ardougne": return new WorldPoint(2670, 3374, 0);
            case "Catherby": return new WorldPoint(2813, 3463, 0);
            case "Falador": return new WorldPoint(3058, 3307, 0);
            case "Farming Guild": return new WorldPoint(1238, 3726, 0);
            case "Harmony Island": return new WorldPoint(3789, 2837, 0);
            case "Kourend": return new WorldPoint(1738, 3550, 0);
            case "Morytania": return new WorldPoint(3601, 3525, 0);
            case "Troll Stronghold": return new WorldPoint(2824, 3696, 0);
            case "Weiss": return new WorldPoint(2847, 3931, 0);
            case "Civitas illa Fortis": return new WorldPoint(1586, 3099, 0);
            default: return null;
        }
    }
    
    private WorldPoint getTreePatchPoint(String locationName) {
        switch (locationName) {
            case "Falador": return new WorldPoint(3000, 3373, 0);
            case "Farming Guild": return new WorldPoint(1232, 3736, 0);
            case "Gnome Stronghold": return new WorldPoint(2436, 3415, 0);
            case "Lumbridge": return new WorldPoint(3193, 3231, 0);
            case "Taverley": return new WorldPoint(2936, 3438, 0);
            case "Varrock": return new WorldPoint(3229, 3459, 0);
            default: return null;
        }
    }
    
    private WorldPoint getFruitTreePatchPoint(String locationName) {
        switch (locationName) {
            case "Brimhaven": return new WorldPoint(2764, 3212, 0);
            case "Catherby": return new WorldPoint(2860, 3433, 0);
            case "Farming Guild": return new WorldPoint(1243, 3759, 0);
            case "Gnome Stronghold": return new WorldPoint(2475, 3446, 0);
            case "Lletya": return new WorldPoint(2346, 3162, 0);
            case "Tree Gnome Village": return new WorldPoint(2490, 3180, 0);
            default: return null;
        }
    }
    
    private WorldPoint getHopsPatchPoint(String locationName) {
        switch (locationName) {
            case "Aldarin": return new WorldPoint(1365, 2939, 0);
            case "Entrana": return new WorldPoint(2811, 3337, 0);
            case "Lumbridge": return new WorldPoint(3229, 3315, 0);
            case "Seers Village": return new WorldPoint(2667, 3526, 0);
            case "Yanille": return new WorldPoint(2576, 3105, 0);
            default: return null;
        }
    }
    
    /**
     * Manages hint arrows for navigation.
     */
    private void updateHintArrow(Location location) {
        WorldPoint patchPoint = getPatchPointForLocation(location);
        if (patchPoint == null) {
            return;
        }

        if (client.getLocalPlayer() == null) {
            return;
        }

        Teleport teleport = location.getSelectedTeleport();
        int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        int clearDistance = "Morytania".equals(location.getName()) ? 10 : 5;
        
        // Special handling for Brimhaven
        if (fruitTreeRun && "Brimhaven".equals(location.getName())) {
            if (currentRegionId == 10547) {
                boolean nearBrimhavenPatch = areaCheck.isPlayerWithinArea(patchPoint, 20);
                if (!nearBrimhavenPatch) {
                    WorldPoint captainBarnabyLocation = new WorldPoint(2675, 3265, 0);
                    client.setHintArrow(captainBarnabyLocation);
                    return;
                }
            }
        }
        
        // Special handling for Entrana
        if (hopsRun && "Entrana".equals(location.getName())) {
            boolean nearEntranaPatch = areaCheck.isPlayerWithinArea(patchPoint, 20);
            if (!nearEntranaPatch) {
                WorldPoint entranaMonkLocation = new WorldPoint(3042, 3235, 0);
                client.setHintArrow(entranaMonkLocation);
                return;
            }
        }
        
        // Normal hint arrow handling
        boolean nearPatch = areaCheck.isPlayerWithinArea(patchPoint, clearDistance);
        if (nearPatch) {
            client.clearHintArrow();
        } else if (!isAtDestination) {
            client.setHintArrow(patchPoint);
        }
    }
    
    /**
     * Handles navigation to the current location.
     */
    private void navigateToCurrentLocation(Graphics2D graphics) {
        if (currentLocationIndex >= enabledLocations.size()) {
            removeOverlay();
            return;
        }
        
        Location location = enabledLocations.get(currentLocationIndex);
        
        // Update hint arrow
        updateHintArrow(location);
        
        // Use NavigationHandler for all navigation logic
        navigationHandler.gettingToLocation(graphics, location, herbRun, treeRun, fruitTreeRun, hopsRun);
        
        // Check if we've reached the destination
        if (navigationHandler.isAtDestination) {
            isAtDestination = true;
            startSubCases = true;
            if (location.getFarmLimps()) {
                farmLimps = true;
            }
        }
    }
    
    /**
     * Handles farming steps at the current location.
     */
    private void handleFarmingSteps(Graphics2D graphics) {
        if (!startSubCases) {
            return;
        }
        
        Location location = enabledLocations.get(currentLocationIndex);
        Teleport teleport = location.getSelectedTeleport();
        
        // Guard against null teleport when location has no valid selection (e.g. transitioning patches)
        if (teleport == null) {
            return;
        }

        if (herbRun) {
            handleHerbRunSteps(graphics, teleport);
        } else if (treeRun) {
            handleTreeRunSteps(graphics, teleport);
        } else if (fruitTreeRun) {
            handleFruitTreeRunSteps(graphics, teleport);
        } else if (hopsRun) {
            handleHopsRunSteps(graphics, teleport);
        }
    }
    
    private void handleHerbRunSteps(Graphics2D graphics, Teleport teleport) {
        if (subCase == 1) {
            farmingStepHandler.herbSteps(graphics, teleport);
            if (farmingStepHandler.herbPatchDone) {
                subCase = 2;
                farmingStepHandler.herbPatchDone = false;
            }
        } else if (subCase == 2) {
            if (config.generalLimpwurt()) {
                farmingStepHandler.flowerSteps(graphics, farmLimps);
                if (farmingStepHandler.flowerPatchDone) {
                    if (config.generalAllotment()) {
                        subCase = 3;
                        farmingStepHandler.flowerPatchDone = false;
                    } else {
                        moveToNextLocation();
                    }
                }
            } else if (config.generalAllotment()) {
                subCase = 3;
                farmingStepHandler.allotmentPatchDone = false;
            } else {
                moveToNextLocation();
            }
        } else if (subCase == 3) {
            if (config.generalAllotment()) {
                farmingStepHandler.allotmentSteps(graphics, teleport);
                if (farmingStepHandler.allotmentPatchDone) {
                    moveToNextLocation();
                }
            } else {
                moveToNextLocation();
            }
        }
    }
    
    private void handleTreeRunSteps(Graphics2D graphics, Teleport teleport) {
        farmingStepHandler.treeSteps(graphics, teleport);
        if (farmingStepHandler.treePatchDone) {
            moveToNextLocation();
        }
    }
    
    private void handleFruitTreeRunSteps(Graphics2D graphics, Teleport teleport) {
        farmingStepHandler.fruitTreeSteps(graphics, teleport);
        if (farmingStepHandler.fruitTreePatchDone) {
            moveToNextLocation();
        }
    }
    
    private void handleHopsRunSteps(Graphics2D graphics, Teleport teleport) {
        farmingStepHandler.hopsSteps(graphics, teleport);
        if (farmingStepHandler.hopsPatchDone) {
            moveToNextLocation();
        }
    }
    
    /**
     * Moves to the next location in the run.
     */
    private void moveToNextLocation() {
        subCase = 1;
        startSubCases = false;
        isAtDestination = false;
        currentLocationIndex++;
        farmLimps = false;
        
        // Reset farming step handler states
        farmingStepHandler.herbPatchDone = false;
        farmingStepHandler.flowerPatchDone = false;
        farmingStepHandler.allotmentPatchDone = false;
        farmingStepHandler.treePatchDone = false;
        farmingStepHandler.fruitTreePatchDone = false;
        farmingStepHandler.hopsPatchDone = false;
        
        // Reset persistent compost states
        farmingStepHandler.resetCompostStates();
        
        // Reset navigation handler state for the new location
        navigationHandler.currentTeleportCase = 1;
        navigationHandler.isAtDestination = false;
    }
    
    /**
     * Initializes a new run.
     */
    public void startRun(boolean herbRun, boolean treeRun, boolean fruitTreeRun, boolean hopsRun) {
        this.herbRun = herbRun;
        this.treeRun = treeRun;
        this.fruitTreeRun = fruitTreeRun;
        this.hopsRun = hopsRun;
        
        currentLocationIndex = 0;
        enabledLocations = getEnabledLocations();
        subCase = 1;
        startSubCases = false;
        isAtDestination = false;
        farmLimps = false;
        navigationHandler.currentTeleportCase = 1;
        navigationHandler.isAtDestination = false;
    }
    
    public void removeOverlay() {
        plugin.overlayManager.remove(farmingHelperOverlay);
        plugin.overlayManager.remove(this);
        plugin.overlayManager.remove(farmingHelperOverlayInfoBox);
        
        plugin.setOverlayActive(false);
        plugin.setTeleportOverlayActive(false);
        
        currentLocationIndex = 0;
        enabledLocations.clear();
        subCase = 1;
        startSubCases = false;
        isAtDestination = false;
        farmLimps = false;
        
        farmingStepHandler.flowerPatchDone = false;
        farmingStepHandler.treePatchDone = false;
        farmingStepHandler.fruitTreePatchDone = false;
        farmingStepHandler.clearHintArrow();
        
        // Reset persistent compost states
        farmingStepHandler.resetCompostStates();
        
        navigationHandler.currentTeleportCase = 1;
        navigationHandler.isAtDestination = false;
        
        plugin.setItemsCollected(false);
        
        plugin.getFarmingTeleportOverlay().herbRun = false;
        plugin.getFarmingTeleportOverlay().treeRun = false;
        plugin.getFarmingTeleportOverlay().fruitTreeRun = false;
        plugin.getFarmingTeleportOverlay().hopsRun = false;
        
        herbRun = false;
        treeRun = false;
        fruitTreeRun = false;
        hopsRun = false;
        
        plugin.panel.herbButton.setStartStopState(false);
        plugin.panel.treeButton.setStartStopState(false);
        plugin.panel.fruitTreeButton.setStartStopState(false);
        plugin.panel.hopsButton.setStartStopState(false);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!plugin.isTeleportOverlayActive()) {
            return null;
        }
        
        // Guard against rendering before player is fully loaded
        if (client.getLocalPlayer() == null) {
            return null;
        }
        
        // Initialize enabled locations if not already done
        if (enabledLocations.isEmpty()) {
            enabledLocations = getEnabledLocations();
        }
        
        if (enabledLocations.isEmpty()) {
            removeOverlay();
            return null;
        }
        
        if (isAtDestination) {
            handleFarmingSteps(graphics);
        } else {
            navigateToCurrentLocation(graphics);
        }
        
        return null;
    }
}