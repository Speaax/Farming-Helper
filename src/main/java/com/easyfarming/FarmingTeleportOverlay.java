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
    
    // Removed legacy run state flags    
    // Location tracking
    private int currentLocationIndex = 0;
    private List<Location> enabledLocations = new ArrayList<>();
    
    // Custom Run Support
    private com.easyfarming.models.CustomRun activeCustomRun;
    private int currentPatchIndex = 0;
    
    public com.easyfarming.models.CustomRun getActiveCustomRun() {
        return activeCustomRun;
    }

    public List<Location> getEnabledLocations() {
        return enabledLocations;
    }

    public int getCurrentLocationIndex() {
        return currentLocationIndex;
    }

    public int getCurrentPatchIndex() {
        return currentPatchIndex;
    }
    
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
     * Gets the patch point for the current location based on current custom patch.
     */
    private WorldPoint getPatchPointForLocation(Location location) {
        String locationName = location.getName();
        
        if (activeCustomRun != null) {
        java.util.List<String> order = location.getCustomPatchOrder();
            if (order != null && currentPatchIndex < order.size()) {
                return com.easyfarming.overlays.utils.PatchLocationProvider.getPatchPoint(locationName, order.get(currentPatchIndex));
            } else if (order != null && !order.isEmpty()) {
                return com.easyfarming.overlays.utils.PatchLocationProvider.getPatchPoint(locationName, order.get(0));
            }
        }
        
        return null;
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
        navigationHandler.gettingToLocation(graphics, location, false, false, false, false);
        
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

        handleCustomRunSteps(graphics, teleport, location);
    }
    
    private void handleCustomRunSteps(Graphics2D graphics, Teleport teleport, Location location) {
        List<String> order = location.getCustomPatchOrder();
        java.util.Map<String, Boolean> states = location.getCustomPatchStates();
        
        if (order == null || currentPatchIndex >= order.size()) {
            moveToNextLocation();
            return;
        }

        String currentPatchName = order.get(currentPatchIndex);
        boolean isActive = states != null && states.getOrDefault(currentPatchName, false);
        
        if (!isActive) {
            advancePatch();
            return;
        }
        
        String lowerName = currentPatchName.toLowerCase();
        
        if (lowerName.contains("herb")) {
            farmingStepHandler.herbSteps(graphics, teleport);
            if (farmingStepHandler.herbPatchDone) advancePatch();
        } else if (lowerName.contains("flower")) {
            farmingStepHandler.flowerSteps(graphics, true); // Always assume true for custom
            if (farmingStepHandler.flowerPatchDone) advancePatch();
        } else if (lowerName.contains("allotment")) {
            farmingStepHandler.allotmentSteps(graphics, teleport);
            if (farmingStepHandler.allotmentPatchDone) advancePatch();
        } else if (lowerName.contains("tree")) {
            farmingStepHandler.treeSteps(graphics, teleport);
            if (farmingStepHandler.treePatchDone) advancePatch();
        } else if (lowerName.contains("fruit tree")) {
            farmingStepHandler.fruitTreeSteps(graphics, teleport);
            if (farmingStepHandler.fruitTreePatchDone) advancePatch();
        } else if (lowerName.contains("hops")) {
            farmingStepHandler.hopsSteps(graphics, teleport);
            if (farmingStepHandler.hopsPatchDone) advancePatch();
        } else {
            // Unknown patch type
            advancePatch();
        }
    }
    
    private void advancePatch() {
        currentPatchIndex++;
        // Reset flags
        farmingStepHandler.herbPatchDone = false;
        farmingStepHandler.flowerPatchDone = false;
        farmingStepHandler.allotmentPatchDone = false;
        farmingStepHandler.treePatchDone = false;
        farmingStepHandler.fruitTreePatchDone = false;
        farmingStepHandler.hopsPatchDone = false;
    }
    
    public java.util.Map<Integer, Integer> getActiveCustomRunItemRequirements() {
        java.util.Map<Integer, Integer> allRequirements = new java.util.HashMap<>();
        if (activeCustomRun == null || enabledLocations.isEmpty()) {
            return allRequirements;
        }

        boolean addedSpade = false;
        boolean addedDibber = false;
        boolean addedWateringCan = false;

        for (Location location : enabledLocations) {
            // Add teleport requirements
            Teleport teleport = location.getSelectedTeleport();
            if (teleport != null && teleport.getItemRequirements() != null) {
                for (java.util.Map.Entry<Integer, Integer> entry : teleport.getItemRequirements().entrySet()) {
                    int itemId = entry.getKey();
                    int quantity = entry.getValue();
                    
                    if (itemId == ItemID.CONSTRUCT_CAPE || itemId == ItemID.CONSTRUCT_CAPET || itemId == ItemID.MAX_CAPE || itemId == ItemID.ROYAL_SEED_POD || itemId == net.runelite.api.ItemID.SLAYER_RING_8) {
                        allRequirements.merge(itemId, quantity, Math::max);
                    } else if (itemId == com.easyfarming.utils.Constants.BASE_TELEPORT_CRYSTAL_ID) {
                        allRequirements.merge(itemId, quantity, Math::max);
                    } else if (itemId == ItemID.BASIC_QUETZAL_WHISTLE || itemId == ItemID.ENHANCED_QUETZAL_WHISTLE || itemId == ItemID.PERFECTED_QUETZAL_WHISTLE) {
                        allRequirements.merge(net.runelite.api.ItemID.BASIC_QUETZAL_WHISTLE, quantity, (oldValue, newValue) -> Math.min(1, oldValue + newValue));
                    } else if (itemId == ItemID.HUNTER_CAPE || itemId == ItemID.HUNTER_CAPET) {
                        allRequirements.merge(ItemID.HUNTER_CAPE, quantity, (oldValue, newValue) -> Math.min(1, oldValue + newValue));
                    } else if (itemId == ItemID.EXPLORERS_RING_2 || itemId == ItemID.EXPLORERS_RING_3 || itemId == ItemID.EXPLORERS_RING_4) {
                        allRequirements.merge(ItemID.EXPLORERS_RING_2, quantity, (oldValue, newValue) -> Math.min(1, oldValue + newValue));
                    } else if (itemId == ItemID.ARDOUGNE_CLOAK_2 || itemId == ItemID.ARDOUGNE_CLOAK_3 || itemId == ItemID.ARDOUGNE_CLOAK_4) {
                        allRequirements.merge(ItemID.ARDOUGNE_CLOAK_2, quantity, (oldValue, newValue) -> Math.min(1, oldValue + newValue));
                    } else if (itemId == net.runelite.api.ItemID.DRAMEN_STAFF) {
                        allRequirements.merge(net.runelite.api.ItemID.DRAMEN_STAFF, quantity, (oldValue, newValue) -> Math.min(1, oldValue + newValue));
                    } else {
                        allRequirements.merge(itemId, quantity, Integer::sum);
                    }
                }
            }
            
            // Add patch requirements (seeds, compost, payments)
            java.util.List<String> order = location.getCustomPatchOrder();
            java.util.Map<String, Boolean> states = location.getCustomPatchStates();
            if (order != null && states != null) {
                for (String patch : order) {
                    if (states.getOrDefault(patch, false)) {
                        String lowerName = patch.toLowerCase();
                        addedSpade = true;
                        
                        if (lowerName.contains("herb")) {
                            allRequirements.merge(net.runelite.api.ItemID.GUAM_SEED, 1, Integer::sum);
                            addedDibber = true;
                            addCompost(allRequirements);
                        } else if (lowerName.contains("flower")) {
                            allRequirements.merge(net.runelite.api.ItemID.LIMPWURT_SEED, 1, Integer::sum);
                            addedDibber = true;
                            addCompost(allRequirements);
                        } else if (lowerName.contains("allotment")) {
                            allRequirements.merge(net.runelite.api.ItemID.WATERMELON_SEED, 3, Integer::sum);
                            addedDibber = true;
                            addCompost(allRequirements);
                        } else if (lowerName.contains("tree") && !lowerName.contains("fruit tree")) {
                            allRequirements.merge(ItemID.OAK_SAPLING, 1, Integer::sum);
                            allRequirements.merge(net.runelite.api.ItemID.COINS, 200, Integer::sum);
                            addCompost(allRequirements);
                        } else if (lowerName.contains("fruit tree")) {
                            allRequirements.merge(ItemID.APPLE_SAPLING, 1, Integer::sum);
                            allRequirements.merge(net.runelite.api.ItemID.COINS, 200, Integer::sum);
                            addCompost(allRequirements);
                        } else if (lowerName.contains("hops")) {
                            allRequirements.merge(net.runelite.api.ItemID.BARLEY_SEED, 4, Integer::sum);
                            addedDibber = true;
                            addedWateringCan = true;
                            addCompost(allRequirements);
                        }
                    }
                }
            }
        }
        
        // Add generic tools
        if (addedSpade) {
            allRequirements.merge(net.runelite.api.ItemID.SPADE, 1, Integer::sum);
        }
        if (addedDibber && config.generalSeedDibber()) {
            allRequirements.merge(net.runelite.api.ItemID.SEED_DIBBER, 1, Integer::sum);
        }
        if (config.generalSecateurs()) {
            allRequirements.merge(net.runelite.api.ItemID.MAGIC_SECATEURS, 1, Integer::sum);
        }
        if (config.generalRake()) {
            allRequirements.merge(net.runelite.api.ItemID.RAKE, 1, Integer::sum);
        }
        if (addedWateringCan) {
            allRequirements.merge(com.easyfarming.utils.Constants.WATERING_CAN_IDS.get(0), 1, Integer::sum);
        }
        
        // Handle bottomless compost if they have it selected
        int compostId = plugin.getCompostId();
        if (compostId == net.runelite.api.gameval.ItemID.BOTTOMLESS_COMPOST_BUCKET) {
            allRequirements.put(net.runelite.api.gameval.ItemID.BOTTOMLESS_COMPOST_BUCKET, 1);
        }

        return allRequirements;
    }
    
    private void addCompost(java.util.Map<Integer, Integer> allRequirements) {
        int compostId = plugin.getCompostId();
        if (compostId != -1 && compostId != net.runelite.api.gameval.ItemID.BOTTOMLESS_COMPOST_BUCKET) {
            allRequirements.merge(compostId, 1, Integer::sum);
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
        currentPatchIndex = 0;
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
     * Initializes a new Custom Run sequence.
     */
    public void startCustomRun(com.easyfarming.models.CustomRun run) {
        this.activeCustomRun = run;

        List<Location> allBaseLocations = buildAllBaseLocations();

        List<Location> newEnabledLocations = new ArrayList<>();
        if (run.getLocations() != null) {
            for (com.easyfarming.models.CustomLocation customLoc : run.getLocations()) {
                boolean hasActivePatch = false;
                if (customLoc.getEnabledPatches() != null && customLoc.getPatchActiveStates() != null) {
                    for (String patch : customLoc.getEnabledPatches()) {
                        if (customLoc.getPatchActiveStates().getOrDefault(patch, false)) {
                            hasActivePatch = true;
                            break;
                        }
                    }
                }
                if (!hasActivePatch) continue;

                Location matchedBase = null;
                for (Location baseLoc : allBaseLocations) {
                    if (baseLoc.getName().equals(customLoc.getName())) {
                        matchedBase = baseLoc;
                        break;
                    }
                }
                
                if (matchedBase != null) {
                    // Clone it so we don't pollute base configuration states across duplicate loops
                    Location activeInstance = new Location(conf -> null, config, matchedBase.getName(), matchedBase.getFarmLimps());
                    for (Teleport t : matchedBase.getTeleportOptions()) activeInstance.addTeleportOption(t);

                    activeInstance.setOverrideTeleportName(customLoc.getTeleportOption());
                    activeInstance.setCustomPatchOrder(customLoc.getEnabledPatches());
                    activeInstance.setCustomPatchStates(customLoc.getPatchActiveStates());
                    newEnabledLocations.add(activeInstance);
                }
            }
        }
        
        this.enabledLocations = newEnabledLocations;
        this.currentLocationIndex = 0;
        this.subCase = 1;
        this.currentPatchIndex = 0;
        this.startSubCases = false;
        this.isAtDestination = false;
        this.farmLimps = false;
        navigationHandler.currentTeleportCase = 1;
        navigationHandler.isAtDestination = false;
        
        plugin.setTeleportOverlayActive(true);
    }

    /**
     * Builds the full list of all known Location templates from the LocationData registry.
     * Used as a lookup table in startCustomRun to resolve base configurations by name.
     * Supplier args are empty lists here because we only need location names and teleport
     * structure, not house-teleport item requirements, for the name→config match.
     */
    private List<Location> buildAllBaseLocations() {
        java.util.function.Supplier<java.util.List<com.easyfarming.ItemRequirement>> empty =
                java.util.Collections::emptyList;
        List<Location> all = new ArrayList<>();

        // ── Herb locations ──────────────────────────────────────────────────
        all.add(com.easyfarming.locations.ArdougneLocationData.create(config, empty));
        all.add(com.easyfarming.locations.CatherbyLocationData.create(config, empty));
        all.add(com.easyfarming.locations.CivitasLocationData.create(config, empty));
        all.add(com.easyfarming.locations.FaladorLocationData.create(config, empty));
        all.add(com.easyfarming.locations.FarmingGuildLocationData.create(config, empty, empty));
        all.add(com.easyfarming.locations.HarmonyLocationData.create(config, empty));
        all.add(com.easyfarming.locations.KourendLocationData.create(config, empty));
        all.add(com.easyfarming.locations.MorytaniaLocationData.create(config, empty, empty));
        all.add(com.easyfarming.locations.TrollStrongholdLocationData.create(config, empty));
        all.add(com.easyfarming.locations.WeissLocationData.create(config, empty));

        // ── Tree locations ──────────────────────────────────────────────────
        all.add(com.easyfarming.locations.tree.FaladorTreeLocationData.create(config, empty));
        all.add(com.easyfarming.locations.tree.FarmingGuildTreeLocationData.create(config, empty, empty));
        all.add(com.easyfarming.locations.tree.GnomeStrongholdTreeLocationData.create(config));
        all.add(com.easyfarming.locations.tree.LumbridgeTreeLocationData.create(config, empty));
        all.add(com.easyfarming.locations.tree.TaverleyTreeLocationData.create(config, empty));
        all.add(com.easyfarming.locations.tree.VarrockTreeLocationData.create(config, empty));

        // ── Fruit tree locations ────────────────────────────────────────────
        all.add(com.easyfarming.locations.fruittree.BrimhavenFruitTreeLocationData.create(config, empty));
        all.add(com.easyfarming.locations.fruittree.CatherbyFruitTreeLocationData.create(config, empty));
        all.add(com.easyfarming.locations.fruittree.FarmingGuildFruitTreeLocationData.create(config, empty, empty));
        all.add(com.easyfarming.locations.fruittree.GnomeStrongholdFruitTreeLocationData.create(config));
        all.add(com.easyfarming.locations.fruittree.LletyaFruitTreeLocationData.create(config));
        all.add(com.easyfarming.locations.fruittree.TreeGnomeVillageFruitTreeLocationData.create(config));

        // ── Hops locations ──────────────────────────────────────────────────
        all.add(com.easyfarming.locations.hops.AldarinHopsLocationData.create(config, empty, empty));
        all.add(com.easyfarming.locations.hops.EntranaHopsLocationData.create(config));
        all.add(com.easyfarming.locations.hops.LumbridgeHopsLocationData.create(config, empty));
        all.add(com.easyfarming.locations.hops.SeersVillageHopsLocationData.create(config, empty, empty));
        all.add(com.easyfarming.locations.hops.YanilleHopsLocationData.create(config, empty));

        return all;
    }

    public void removeOverlay() {
        plugin.overlayManager.remove(farmingHelperOverlay);
        plugin.overlayManager.remove(this);
        plugin.overlayManager.remove(farmingHelperOverlayInfoBox);

        
        plugin.setOverlayActive(false);
        plugin.setTeleportOverlayActive(false);
        
        activeCustomRun = null;
        currentLocationIndex = 0;
        enabledLocations.clear();
        subCase = 1;
        currentPatchIndex = 0;
        startSubCases = false;
        isAtDestination = false;
        farmLimps = false;
        
        farmingStepHandler.flowerPatchDone = false;
        farmingStepHandler.treePatchDone = false;
        farmingStepHandler.fruitTreePatchDone = false;
        farmingStepHandler.clearHintArrow();
        farmingStepHandler.resetCompostStates();
        
        navigationHandler.currentTeleportCase = 1;
        navigationHandler.isAtDestination = false;
        
        plugin.setItemsCollected(false);
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