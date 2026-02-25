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
import com.easyfarming.customrun.LocationCatalog;
import com.easyfarming.customrun.PatchTypes;
import com.easyfarming.customrun.CustomRun;
import com.easyfarming.customrun.RunLocation;
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
    
    // Custom run state
    private boolean customRunMode = false;
    private List<RunLocation> customRunLocations = new ArrayList<>();
    /** Name of the custom run currently active (so UI can show Stop on the correct run). */
    private String activeCustomRunName = null;

    public boolean isCustomRunMode() {
        return customRunMode;
    }

    public List<RunLocation> getCustomRunLocations() {
        return customRunLocations;
    }

    public String getActiveCustomRunName() {
        return activeCustomRunName;
    }
    private int currentPatchTypeIndex = 0;
    
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
        setLayer(OverlayLayer.ABOVE_WIDGETS);
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
     * Gets patch point for a location and patch type (used in custom run mode).
     */
    public WorldPoint getPatchPointForLocationAndType(String locationName, String patchType) {
        if (patchType == null) return null;
        switch (patchType) {
            case PatchTypes.HERB:
            case PatchTypes.FLOWER:
            case PatchTypes.ALLOTMENT:
                return getHerbPatchPoint(locationName);
            case PatchTypes.TREE:
                return getTreePatchPoint(locationName);
            case PatchTypes.FRUIT_TREE:
                return getFruitTreePatchPoint(locationName);
            case PatchTypes.HOPS:
                return getHopsPatchPoint(locationName);
            default:
                return null;
        }
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
     * Returns the current Location (with teleport override set) and patch type when in custom run mode; otherwise null.
     */
    private Location getCurrentLocationForCustomRun() {
        if (!customRunMode || customRunLocations.isEmpty() || currentLocationIndex >= customRunLocations.size()) {
            return null;
        }
        RunLocation rl = customRunLocations.get(currentLocationIndex);
        List<String> patchTypes = rl.getPatchTypes();
        if (patchTypes == null || patchTypes.isEmpty() || currentPatchTypeIndex >= patchTypes.size()) {
            return null;
        }
        String patchType = patchTypes.get(currentPatchTypeIndex);
        LocationCatalog catalog = plugin.getLocationCatalog();
        Location loc = catalog.getLocationForPatch(rl.getLocationName(), patchType);
        if (loc != null && rl.getTeleportOption() != null) {
            loc.setOverrideTeleportEnumOption(rl.getTeleportOption());
        }
        return loc;
    }
    
    private String getCurrentPatchTypeForCustomRun() {
        if (!customRunMode || customRunLocations.isEmpty() || currentLocationIndex >= customRunLocations.size()) {
            return null;
        }
        RunLocation rl = customRunLocations.get(currentLocationIndex);
        List<String> patchTypes = rl.getPatchTypes();
        if (patchTypes == null || currentPatchTypeIndex >= patchTypes.size()) return null;
        return patchTypes.get(currentPatchTypeIndex);
    }
    
    /**
     * Manages hint arrows for navigation.
     */
    private void updateHintArrow(Location location) {
        WorldPoint patchPoint;
        if (customRunMode) {
            String patchType = getCurrentPatchTypeForCustomRun();
            patchPoint = location != null && patchType != null
                    ? getPatchPointForLocationAndType(location.getName(), patchType)
                    : null;
        } else {
            patchPoint = getPatchPointForLocation(location);
        }
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
        boolean isBrimhavenFruitTree = (fruitTreeRun || (customRunMode && PatchTypes.FRUIT_TREE.equals(getCurrentPatchTypeForCustomRun()))) && "Brimhaven".equals(location.getName());
        if (isBrimhavenFruitTree) {
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
        boolean isEntranaHops = (hopsRun || (customRunMode && PatchTypes.HOPS.equals(getCurrentPatchTypeForCustomRun()))) && "Entrana".equals(location.getName());
        if (isEntranaHops) {
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
        if (customRunMode) {
            Location location = getCurrentLocationForCustomRun();
            if (location == null) {
                moveToNextLocation();
                return;
            }
            updateHintArrow(location);
            String patchType = getCurrentPatchTypeForCustomRun();
            boolean h = PatchTypes.HERB.equals(patchType) || PatchTypes.FLOWER.equals(patchType) || PatchTypes.ALLOTMENT.equals(patchType);
            boolean t = PatchTypes.TREE.equals(patchType);
            boolean f = PatchTypes.FRUIT_TREE.equals(patchType);
            boolean ho = PatchTypes.HOPS.equals(patchType);
            navigationHandler.gettingToLocation(graphics, location, h, t, f, ho, true);
            if (navigationHandler.isAtDestination) {
                isAtDestination = true;
                startSubCases = true;
                if (location.getFarmLimps()) farmLimps = true;
            }
            return;
        }
        
        if (currentLocationIndex >= enabledLocations.size()) {
            removeOverlay();
            return;
        }
        
        Location location = enabledLocations.get(currentLocationIndex);
        
        // Update hint arrow
        updateHintArrow(location);
        
        // Use NavigationHandler for all navigation logic
        navigationHandler.gettingToLocation(graphics, location, herbRun, treeRun, fruitTreeRun, hopsRun, false);
        
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
        
        if (customRunMode) {
            handleCustomRunSteps(graphics);
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
    
    private void handleCustomRunSteps(Graphics2D graphics) {
        Location location = getCurrentLocationForCustomRun();
        String patchType = getCurrentPatchTypeForCustomRun();
        if (location == null || patchType == null) {
            moveToNextLocation();
            return;
        }
        Teleport teleport = location.getSelectedTeleport();
        if (teleport == null) {
            moveToNextLocation();
            return;
        }
        switch (patchType) {
            case PatchTypes.HERB:
                farmingStepHandler.herbSteps(graphics, teleport);
                if (farmingStepHandler.herbPatchDone) {
                    farmingStepHandler.herbPatchDone = false;
                    moveToNextPatchOrLocation();
                }
                break;
            case PatchTypes.FLOWER:
                farmingStepHandler.flowerSteps(graphics, farmLimps);
                if (farmingStepHandler.flowerPatchDone) {
                    farmingStepHandler.flowerPatchDone = false;
                    moveToNextPatchOrLocation();
                }
                break;
            case PatchTypes.ALLOTMENT:
                farmingStepHandler.allotmentSteps(graphics, teleport);
                if (farmingStepHandler.allotmentPatchDone) {
                    farmingStepHandler.allotmentPatchDone = false;
                    moveToNextPatchOrLocation();
                }
                break;
            case PatchTypes.TREE:
                farmingStepHandler.treeSteps(graphics, teleport);
                if (farmingStepHandler.treePatchDone) {
                    farmingStepHandler.treePatchDone = false;
                    moveToNextPatchOrLocation();
                }
                break;
            case PatchTypes.FRUIT_TREE:
                farmingStepHandler.fruitTreeSteps(graphics, teleport);
                if (farmingStepHandler.fruitTreePatchDone) {
                    farmingStepHandler.fruitTreePatchDone = false;
                    moveToNextPatchOrLocation();
                }
                break;
            case PatchTypes.HOPS:
                farmingStepHandler.hopsSteps(graphics, teleport);
                if (farmingStepHandler.hopsPatchDone) {
                    farmingStepHandler.hopsPatchDone = false;
                    moveToNextPatchOrLocation();
                }
                break;
            default:
                moveToNextPatchOrLocation();
        }
    }
    
    private void moveToNextPatchOrLocation() {
        if (!customRunMode || customRunLocations.isEmpty()) {
            moveToNextLocation();
            return;
        }
        RunLocation rl = customRunLocations.get(currentLocationIndex);
        List<String> patchTypes = rl.getPatchTypes();
        if (patchTypes != null && currentPatchTypeIndex + 1 < patchTypes.size()) {
            currentPatchTypeIndex++;
            farmingStepHandler.herbPatchDone = false;
            farmingStepHandler.flowerPatchDone = false;
            farmingStepHandler.allotmentPatchDone = false;
            farmingStepHandler.treePatchDone = false;
            farmingStepHandler.fruitTreePatchDone = false;
            farmingStepHandler.hopsPatchDone = false;
            farmingStepHandler.resetCompostStates();
            return;
        }
        moveToNextLocation();
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
        currentPatchTypeIndex = 0;
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
        
        if (customRunMode && currentLocationIndex >= customRunLocations.size()) {
            removeOverlay();
        }
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
        
        customRunMode = false;
        customRunLocations.clear();
        activeCustomRunName = null;
        currentPatchTypeIndex = 0;
        currentLocationIndex = 0;
        enabledLocations.clear();
        subCase = 1;
        startSubCases = false;
        isAtDestination = false;
        farmLimps = false;
        
        farmingStepHandler.herbPatchDone = false;
        farmingStepHandler.flowerPatchDone = false;
        farmingStepHandler.allotmentPatchDone = false;
        farmingStepHandler.treePatchDone = false;
        farmingStepHandler.fruitTreePatchDone = false;
        farmingStepHandler.hopsPatchDone = false;
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
    }
    
    public void startCustomRun(CustomRun run) {
        if (run == null || run.getLocations() == null) {
            return;
        }
        herbRun = false;
        treeRun = false;
        fruitTreeRun = false;
        hopsRun = false;
        customRunMode = true;
        activeCustomRunName = run.getName();
        customRunLocations = new ArrayList<>(run.getLocations());
        currentLocationIndex = 0;
        currentPatchTypeIndex = 0;
        enabledLocations.clear();
        subCase = 1;
        startSubCases = false;
        isAtDestination = false;
        farmLimps = false;
        navigationHandler.currentTeleportCase = 1;
        navigationHandler.isAtDestination = false;
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
        
        if (customRunMode) {
            if (customRunLocations.isEmpty()) {
                removeOverlay();
                return null;
            }
        } else {
            // Initialize enabled locations if not already done
            if (enabledLocations.isEmpty()) {
                enabledLocations = getEnabledLocations();
            }
            if (enabledLocations.isEmpty()) {
                removeOverlay();
                return null;
            }
        }
        
        if (isAtDestination) {
            handleFarmingSteps(graphics);
        } else {
            navigateToCurrentLocation(graphics);
        }
        
        return null;
    }
}