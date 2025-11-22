package com.easyfarming.overlays.handlers;

import com.easyfarming.*;
import com.easyfarming.overlays.highlighting.*;
import com.easyfarming.overlays.utils.ColorProvider;
import com.easyfarming.overlays.utils.PatchStateChecker;
import com.easyfarming.utils.Constants;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Tile;
import net.runelite.api.WorldView;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;

/**
 * Handles farming step logic for herb, flower, tree, and fruit tree patches.
 */
public class FarmingStepHandler {
    private final Client client;
    private final EasyFarmingPlugin plugin;
    private final EasyFarmingConfig config;
    private final AreaCheck areaCheck;
    private final PatchHighlighter patchHighlighter;
    private final ItemHighlighter itemHighlighter;
    private final CompostHighlighter compostHighlighter;
    private final FarmerHighlighter farmerHighlighter;
    private final PatchStateChecker patchStateChecker;
    private final ColorProvider colorProvider;
    private final GameObjectHighlighter gameObjectHighlighter;
    
    // State tracking
    public boolean herbPatchDone = false;
    public boolean flowerPatchDone = false;
    public boolean allotmentPatchDone = false;
    public boolean treePatchDone = false;
    public boolean fruitTreePatchDone = false;
    public boolean hopsPatchDone = false;
    
    // Allotment patch tracking - which patch we're currently working on (0 = first patch, 1 = second patch)
    private final AllotmentPatchState allotmentPatchState = new AllotmentPatchState();
    private final EasyFarmingOverlay farmingHelperOverlay;
    
    @Inject
    public FarmingStepHandler(Client client, EasyFarmingPlugin plugin, EasyFarmingConfig config,
                              AreaCheck areaCheck, PatchHighlighter patchHighlighter,
                              ItemHighlighter itemHighlighter, CompostHighlighter compostHighlighter,
                              FarmerHighlighter farmerHighlighter, PatchStateChecker patchStateChecker,
                              ColorProvider colorProvider, EasyFarmingOverlay farmingHelperOverlay,
                              GameObjectHighlighter gameObjectHighlighter) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.areaCheck = areaCheck;
        this.patchHighlighter = patchHighlighter;
        this.itemHighlighter = itemHighlighter;
        this.compostHighlighter = compostHighlighter;
        this.farmerHighlighter = farmerHighlighter;
        this.patchStateChecker = patchStateChecker;
        this.colorProvider = colorProvider;
        this.farmingHelperOverlay = farmingHelperOverlay;
        this.gameObjectHighlighter = gameObjectHighlighter;
    }
    
    /**
     * Handles herb patch farming steps.
     */
    public void herbSteps(Graphics2D graphics, Location.Teleport teleport) {
        int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        HerbPatchChecker.PlantState plantState = HerbPatchChecker.PlantState.UNKNOWN;
        Color leftColor = colorProvider.getLeftClickColorWithAlpha();
        Color useItemColor = colorProvider.getHighlightUseItemWithAlpha();
        
        // Get location name from region ID
        String locationName = getLocationNameFromRegionId(currentRegionId);
        
        // Get patch object ID for this location
        Integer patchObjectId = farmingHelperOverlay.getHerbPatchIdForLocation(locationName);
        
        int varbitId = -1;
        
        // Try to get varbit from object composition
        if (patchObjectId != null) {
            varbitId = getHerbPatchVarbitId(patchObjectId);
        }
        
        // Fallback: If object composition fails, use location-specific varbits
        if (varbitId == -1) {
            if (currentRegionId == Constants.REGION_FARMING_GUILD) {
                varbitId = Constants.VARBIT_HERB_PATCH_FARMING_GUILD;
            } else if (currentRegionId == Constants.REGION_HARMONY) {
                varbitId = Constants.VARBIT_HERB_PATCH_HARMONY;
            } else if (currentRegionId == Constants.REGION_TROLL_STRONGHOLD || currentRegionId == Constants.REGION_WEISS) {
                varbitId = Constants.VARBIT_HERB_PATCH_TROLL_WEISS;
            } else {
                varbitId = Constants.VARBIT_HERB_PATCH_STANDARD;
            }
        }
        
        // Check state for herb patch
        if (varbitId != -1) {
            plantState = HerbPatchChecker.checkHerbPatch(client, varbitId);
        }
        
        if (!areaCheck.isPlayerWithinArea(teleport.getPoint(), 15)) {
            // Should be replaced with a pathing system, pointing arrow or something else eventually
            patchHighlighter.highlightHerbPatches(graphics, leftColor);
        } else {
            switch (plantState) {
                case HARVESTABLE:
                    plugin.addTextToInfoBox("Harvest Herbs.");
                    patchHighlighter.highlightHerbPatches(graphics, leftColor);
                    break;
                case PLANT:
                    plugin.addTextToInfoBox("Use Herb seed on patch.");
                    patchHighlighter.highlightHerbPatches(graphics, useItemColor);
                    itemHighlighter.highlightHerbSeeds(graphics);
                    break;
                case DEAD:
                    plugin.addTextToInfoBox("Clear the dead herb patch.");
                    patchHighlighter.highlightHerbPatches(graphics, leftColor);
                    break;
                case DISEASED:
                    plugin.addTextToInfoBox("Use Plant cure on herb patch. Buy at GE or in farming guild/catherby, and store at Tool Leprechaun for easy access.");
                    patchHighlighter.highlightHerbPatches(graphics, leftColor);
                    itemHighlighter.itemHighlight(graphics, ItemID.PLANT_CURE, useItemColor);
                    break;
                case WEEDS:
                    plugin.addTextToInfoBox("Rake the herb patch.");
                    patchHighlighter.highlightHerbPatches(graphics, leftColor);
                    break;
                case GROWING:
                    boolean isComposted = patchStateChecker.patchIsComposted();
                    if (isComposted) {
                        herbPatchDone = true;
                        // Don't show anything - transition will happen on next frame
                        return;
                    }
                    if (!herbPatchDone) {
                        plugin.addTextToInfoBox("Use Compost on patch.");
                        compostHighlighter.highlightCompost(graphics, true, false, false, 1);
                    }
                    break;
                case UNKNOWN:
                    plugin.addTextToInfoBox("UNKNOWN state: Try to do something with the herb patch to change its state.");
                    break;
            }
        }
    }
    
    /**
     * Handles hops patch farming steps.
     */
    public void hopsSteps(Graphics2D graphics, Location.Teleport teleport) {
        int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        HopsPatchChecker.PlantState plantState = HopsPatchChecker.PlantState.UNKNOWN;
        Color leftColor = colorProvider.getLeftClickColorWithAlpha();
        Color useItemColor = colorProvider.getHighlightUseItemWithAlpha();
        
        // Get location name from region ID
        String locationName = getHopsLocationNameFromRegionId(currentRegionId);
        
        // Get patch object ID for this location
        Integer patchObjectId = farmingHelperOverlay.getHopsPatchIdForLocation(locationName);
        
        int varbitId = -1;
        
        // Try to get varbit from object composition
        if (patchObjectId != null) {
            varbitId = getHopsPatchVarbitId(patchObjectId);
        }
        
        // Fallback: Use standard hops patch varbit if object composition fails
        // Hops patches use FARMING_TRANSMIT_A (4771)
        if (varbitId == -1) {
            varbitId = Constants.VARBIT_HOPS_PATCH_STANDARD;
        }
        
        // Check state for hops patch
        if (varbitId != -1) {
            plantState = HopsPatchChecker.checkHopsPatch(client, varbitId);
        }
        
        // Get patch location for this hops location
        WorldPoint patchPoint = getHopsPatchPoint(locationName);
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        
        // Check if player is near the patch location (not the teleport point)
        // Use a larger range (20 tiles) to account for patches that may be spread out
        boolean nearPatch = patchPoint != null && areaCheck.isPlayerWithinArea(patchPoint, 20);
        boolean inCorrectRegion = currentRegionId == teleport.getRegionId();
        
        // If we have a valid plant state (not UNKNOWN), show instructions
        // This ensures instructions show when player is at the patch, even if proximity/region checks fail
        // The varbit detection is the most reliable indicator that we're at the correct patch
        // Show instructions if: near patch OR (valid state detected AND we're in a hops region)
        boolean isHopsRegion = locationName != null && !locationName.equals("Unknown");
        boolean shouldShowInstructions = nearPatch || (plantState != HopsPatchChecker.PlantState.UNKNOWN && isHopsRegion);
        
        // Always show debug output to help diagnose issues
        int distanceX = patchPoint != null ? Math.abs(playerLocation.getX() - patchPoint.getX()) : -1;
        int distanceY = patchPoint != null ? Math.abs(playerLocation.getY() - patchPoint.getY()) : -1;
        plugin.addDebugTextToInfoBox("[HOPS] Location: " + locationName + 
            ", RegionID: " + currentRegionId +
            ", TeleportRegionID: " + teleport.getRegionId() +
            (patchPoint != null ? ", Patch: (" + patchPoint.getX() + "," + patchPoint.getY() + ")" : ", Patch: null") +
            ", Player: (" + playerLocation.getX() + "," + playerLocation.getY() + ")" +
            (patchPoint != null ? ", Distance: (" + distanceX + "," + distanceY + ")" : "") +
            ", NearPatch: " + nearPatch +
            ", InRegion: " + inCorrectRegion +
            ", State: " + plantState +
            ", PatchObjID: " + patchObjectId +
            ", Varbit: " + varbitId + "=" + (varbitId != -1 ? client.getVarbitValue(varbitId) : "N/A") +
            ", ShowInstructions: " + shouldShowInstructions);
        
        if (!shouldShowInstructions) {
            // Highlight all hops patches when far from patch and no state detected
            patchHighlighter.highlightHopsPatches(graphics, leftColor);
        } else {
            // Highlight specific patch for current location
            if (patchObjectId != null) {
                switch (plantState) {
                    case HARVESTABLE:
                        plugin.addTextToInfoBox("Harvest Hops.");
                        patchHighlighter.highlightSpecificHopsPatch(graphics, patchObjectId, leftColor);
                        break;
                    case PLANT:
                        plugin.addTextToInfoBox("Use Hops seed on patch.");
                        patchHighlighter.highlightSpecificHopsPatch(graphics, patchObjectId, useItemColor);
                        itemHighlighter.highlightHopsSeeds(graphics);
                        // Debug: show varbit info
                        int plantVarbitValue = varbitId != -1 ? client.getVarbitValue(varbitId) : -1;
                        plugin.addDebugTextToInfoBox("[HOPS PLANT] Varbit=" + varbitId + " Value=" + plantVarbitValue);
                        break;
                    case DEAD:
                        plugin.addTextToInfoBox("Clear the dead hops patch.");
                        patchHighlighter.highlightSpecificHopsPatch(graphics, patchObjectId, leftColor);
                        break;
                    case DISEASED:
                        plugin.addTextToInfoBox("Use Plant cure on hops patch.");
                        patchHighlighter.highlightSpecificHopsPatch(graphics, patchObjectId, leftColor);
                        itemHighlighter.itemHighlight(graphics, ItemID.PLANT_CURE, useItemColor);
                        break;
                    case WEEDS:
                        plugin.addTextToInfoBox("Rake the hops patch.");
                        patchHighlighter.highlightSpecificHopsPatch(graphics, patchObjectId, leftColor);
                        // Debug: show varbit info
                        int weedsVarbitValue = varbitId != -1 ? client.getVarbitValue(varbitId) : -1;
                        plugin.addDebugTextToInfoBox("[HOPS WEEDS] Varbit=" + varbitId + " Value=" + weedsVarbitValue);
                        break;
                    case NEEDS_WATER:
                        plugin.addTextToInfoBox("Water the hops patch.");
                        patchHighlighter.highlightSpecificHopsPatch(graphics, patchObjectId, useItemColor);
                        // Highlight all watering can variants
                        for (int canId : Constants.WATERING_CAN_IDS) {
                            itemHighlighter.itemHighlight(graphics, canId, useItemColor);
                        }
                        // Debug: show varbit info
                        int needsWaterVarbitValue = varbitId != -1 ? client.getVarbitValue(varbitId) : -1;
                        plugin.addDebugTextToInfoBox("[HOPS NEEDS_WATER] Varbit=" + varbitId + " Value=" + needsWaterVarbitValue);
                        break;
                    case GROWING:
                        boolean isComposted = patchStateChecker.patchIsComposted();
                        if (isComposted) {
                            hopsPatchDone = true;
                            return;
                        }
                        if (!hopsPatchDone) {
                            plugin.addTextToInfoBox("Use Compost on patch.");
                            patchHighlighter.highlightSpecificHopsPatch(graphics, patchObjectId, useItemColor);
                            compostHighlighter.highlightCompost(graphics, true, false, false, 1);
                        }
                        break;
                    case UNKNOWN:
                        int varbitValue = varbitId != -1 ? client.getVarbitValue(varbitId) : -1;
                        plugin.addTextToInfoBox("UNKNOWN state: Try to do something with the hops patch to change its state.");
                        plugin.addDebugTextToInfoBox("[HOPS] Varbit=" + varbitId + " Value=" + varbitValue + " ObjID=" + patchObjectId);
                        patchHighlighter.highlightSpecificHopsPatch(graphics, patchObjectId, leftColor);
                        break;
                }
            } else {
                // Fallback: highlight all patches if patchObjectId is null
                patchHighlighter.highlightHopsPatches(graphics, leftColor);
            }
        }
    }
    
    /**
     * Gets the location name from a region ID for hops patches.
     * @param regionId The region ID
     * @return The location name, or "Unknown" if not found
     */
    private String getHopsLocationNameFromRegionId(int regionId) {
        switch (regionId) {
            case 12851: // Lumbridge
                return "Lumbridge";
            case 10551: // Seers Village/Camelot
                return "Seers Village";
            case 10288: // Yanille
                return "Yanille";
            case 11060: // Entrana
                return "Entrana";
            case 5421: // Aldarin
                return "Aldarin";
            default:
                return "Unknown";
        }
    }
    
    /**
     * Gets the WorldPoint for a hops patch location.
     * @param locationName The name of the hops location
     * @return WorldPoint of the patch, or null if location not found
     */
    private WorldPoint getHopsPatchPoint(String locationName) {
        switch (locationName) {
            case "Lumbridge":
                return new WorldPoint(3229, 3315, 0);
            case "Seers Village":
                return new WorldPoint(2667, 3526, 0);
            case "Yanille":
                return new WorldPoint(2576, 3105, 0);
            case "Entrana":
                return new WorldPoint(2811, 3337, 0);
            case "Aldarin":
                return new WorldPoint(1365, 2939, 0);
            default:
                return null;
        }
    }
    
    /**
     * Gets the varbit ID for a hops patch by checking the object composition.
     * @param objectId The object ID of the hops patch
     * @return The varbit ID, or -1 if not found
     */
    private int getHopsPatchVarbitId(Integer objectId) {
        if (objectId == null || objectId == -1) {
            return -1;
        }
        ObjectComposition objectComposition = client.getObjectDefinition(objectId);
        if (objectComposition != null) {
            return objectComposition.getVarbitId();
        }
        return -1;
    }
    
    /**
     * Handles flower patch farming steps.
     */
    public void flowerSteps(Graphics2D graphics, boolean farmLimps) {
        if (farmLimps) {
            int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();
            FlowerPatchChecker.PlantState plantState = FlowerPatchChecker.PlantState.UNKNOWN;
            Color leftColor = colorProvider.getLeftClickColorWithAlpha();
            Color useItemColor = colorProvider.getHighlightUseItemWithAlpha();
            
            // Get location name from region ID
            String locationName = getLocationNameFromRegionId(currentRegionId);
            
            // Get patch object ID for this location
            Integer patchObjectId = farmingHelperOverlay.getFlowerPatchIdForLocation(locationName);
            
            int varbitId = -1;
            
            // Try to get varbit from object composition
            if (patchObjectId != null) {
                varbitId = getFlowerPatchVarbitId(patchObjectId);
            }
            
            // Fallback: If object composition fails, use location-specific varbits
            if (varbitId == -1) {
                if (currentRegionId == Constants.REGION_FARMING_GUILD) {
                    varbitId = Constants.VARBIT_FLOWER_PATCH_FARMING_GUILD;
                } else {
                    varbitId = Constants.VARBIT_FLOWER_PATCH_STANDARD;
                }
            }
            
            // Check state for flower patch
            if (varbitId != -1) {
                plantState = FlowerPatchChecker.checkFlowerPatch(client, varbitId);
            }
            switch (plantState) {
                case HARVESTABLE:
                    plugin.addTextToInfoBox("Harvest Limwurt root.");
                    patchHighlighter.highlightFlowerPatches(graphics, leftColor);
                    break;
                case DISEASED:
                    plugin.addTextToInfoBox("Cure the diseased Limwurt.");
                    patchHighlighter.highlightFlowerPatches(graphics, leftColor);
                    break;
                case WEEDS:
                    plugin.addTextToInfoBox("Rake the flower patch.");
                    patchHighlighter.highlightFlowerPatches(graphics, leftColor);
                    break;
                case DEAD:
                    plugin.addTextToInfoBox("Clear the dead flower patch.");
                    patchHighlighter.highlightFlowerPatches(graphics, leftColor);
                    break;
                case PLANT:
                    plugin.addTextToInfoBox("Use Limwurt seed on the patch.");
                    patchHighlighter.highlightFlowerPatches(graphics, useItemColor);
                    itemHighlighter.itemHighlight(graphics, ItemID.LIMPWURT_SEED, useItemColor);
                    break;
                case GROWING:
                    plugin.addTextToInfoBox("Use Compost on patch.");
                    compostHighlighter.highlightCompost(graphics, false, false, false, 2);
                    if (patchStateChecker.patchIsComposted()) {
                        flowerPatchDone = true;
                    }
                    break;
                case UNKNOWN:
                    // Handle unknown state if needed
                    break;
            }
        } else {
            flowerPatchDone = true;
        }
    }
    
    /**
     * Gets the location name from a region ID.
     * @param regionId The region ID
     * @return The location name, or "Unknown" if not found
     */
    private String getLocationNameFromRegionId(int regionId) {
        switch (regionId) {
            case Constants.REGION_ARDOUGNE:
            case Constants.REGION_ARDOUGNE_ALT:
                return "Ardougne";
            case Constants.REGION_CATHERBY:
                return "Catherby";
            case Constants.REGION_FALADOR:
                return "Falador";
            case Constants.REGION_FARMING_GUILD:
                return "Farming Guild";
            case Constants.REGION_KOUREND:
                return "Kourend";
            case Constants.REGION_MORYTANIA:
                return "Morytania";
            case Constants.REGION_CIVITAS:
                return "Civitas illa Fortis";
            case Constants.REGION_HARMONY:
                return "Harmony Island";
            case Constants.REGION_TROLL_STRONGHOLD:
                return "Troll Stronghold";
            case Constants.REGION_WEISS:
                return "Weiss";
            default:
                return "Unknown";
        }
    }
    
    /**
     * Gets the varbit ID for an allotment patch by checking the object composition.
     * @param objectId The object ID of the allotment patch
     * @return The varbit ID, or -1 if not found
     */
    private int getAllotmentPatchVarbitId(int objectId) {
        if (objectId == -1) {
            return -1;
        }
        ObjectComposition objectComposition = client.getObjectDefinition(objectId);
        if (objectComposition != null) {
            return objectComposition.getVarbitId();
        }
        return -1;
    }
    
    /**
     * Gets the varbit ID for a herb patch by checking the object composition.
     * @param objectId The object ID of the herb patch
     * @return The varbit ID, or -1 if not found
     */
    private int getHerbPatchVarbitId(Integer objectId) {
        if (objectId == null || objectId == -1) {
            return -1;
        }
        ObjectComposition objectComposition = client.getObjectDefinition(objectId);
        if (objectComposition != null) {
            return objectComposition.getVarbitId();
        }
        return -1;
    }
    
    /**
     * Gets the varbit ID for a flower patch by checking the object composition.
     * @param objectId The object ID of the flower patch
     * @return The varbit ID, or -1 if not found
     */
    private int getFlowerPatchVarbitId(Integer objectId) {
        if (objectId == null || objectId == -1) {
            return -1;
        }
        ObjectComposition objectComposition = client.getObjectDefinition(objectId);
        if (objectComposition != null) {
            return objectComposition.getVarbitId();
        }
        return -1;
    }
    
    /**
     * Gets the priority of a plant state for determining which patch to handle first.
     * Higher priority = handle first.
     */
    private int getStatePriority(AllotmentPatchChecker.PlantState state) {
        switch (state) {
            case HARVESTABLE: return 7;
            case DEAD: return 6;
            case DISEASED: return 5;
            case NEEDS_WATER: return 4;
            case WEEDS: return 3;
            case PLANT: return 2;
            case GROWING: return 1;
            case UNKNOWN: return 0;
            default: return 0;
        }
    }
    
    /**
     * Handles allotment patch farming steps.
     * Calls north patch handler first, then south patch handler when north is done.
     */
    public void allotmentSteps(Graphics2D graphics, Location.Teleport teleport) {
        // Handle north patch first
        if (allotmentPatchState.getCurrentIndex() == 0) {
            allotmentNorthSteps(graphics, teleport);
            // If north patch is done (GROWING + composted), move to south patch
            // Once we move to south patch, north patch is completely ignored for this run
            // Only transition if north patch is actually completed (GROWING + composted)
            if (allotmentPatchState.isPatchCompleted(0) && allotmentPatchState.isPatchComposted(0)) {
                allotmentPatchState.moveToNextPatch();
                // Don't process south patch in the same frame - let it happen on next frame
                return;
            }
        }
        
        // Handle south patch if north is done (and we're on index 1)
        // This block only executes when currentAllotmentPatchIndex == 1
        // Once we're here, we never go back to north patch
        if (allotmentPatchState.getCurrentIndex() == 1) {
            allotmentSouthSteps(graphics, teleport);
            // If south patch is done, mark all allotment patches as done
            if (allotmentPatchState.isPatchCompleted(1)) {
                this.allotmentPatchDone = true;
                // Reset for next location
                allotmentPatchState.reset();
            }
        }
    }
    
    /**
     * Handles north allotment patch farming steps.
     * Completely separate from south patch handling.
     * Once we move to south patch (index 1), this method should never be called.
     */
    private void allotmentNorthSteps(Graphics2D graphics, Location.Teleport teleport) {
        // Safety check: If we're not on north patch (index 0), return immediately
        // This ensures we never process north patch once we've moved forward
        if (allotmentPatchState.getCurrentIndex() != 0) {
            return;
        }
        
        int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        Color leftColor = colorProvider.getLeftClickColorWithAlpha();
        Color useItemColor = colorProvider.getHighlightUseItemWithAlpha();
        
        // Get location name from region ID
        String locationName = getLocationNameFromRegionId(currentRegionId);
        
        // Get patch object IDs for this location
        List<Integer> allotmentPatchIds = farmingHelperOverlay.getAllotmentPatchIdsForLocation(locationName);
        
        // If no patches found for this location, return
        if (allotmentPatchIds.isEmpty() || allotmentPatchIds.get(0) == null) {
            return;
        }
        
        int patchObjectId = allotmentPatchIds.get(0); // North patch (index 0)
        
        // Get varbit ID from object composition
        int varbitIdFromObject = getAllotmentPatchVarbitId(patchObjectId);
        int varbitId = varbitIdFromObject;
        
        // Fallback: If object composition fails, use location-specific varbits
        if (varbitId == -1) {
            if (locationName.equals("Catherby")) {
                varbitId = Constants.VARBIT_ALLOTMENT_PATCH_NORTH_A1;
            } else {
                varbitId = Constants.VARBIT_ALLOTMENT_PATCH_NORTH_A2;
            }
        }
        
        // Check state for north patch
        AllotmentPatchChecker.PlantState plantState = AllotmentPatchChecker.PlantState.UNKNOWN;
        
        if (varbitId != -1) {
            plantState = AllotmentPatchChecker.checkAllotmentPatch(client, varbitId);
        }

        // Check completion status for north patch
        // HARVESTABLE is NOT completed - user still needs to harvest
        // Only GROWING + composted is considered completed (nothing more to do)
        boolean completed = plantState == AllotmentPatchChecker.PlantState.GROWING && allotmentPatchState.isPatchComposted(0);
        allotmentPatchState.setPatchCompleted(0, completed);
        
        // Handle early returns in a single place
        if (plantState == AllotmentPatchChecker.PlantState.UNKNOWN) {
            int varbitValue = varbitId != -1 ? client.getVarbitValue(varbitId) : -1;
            plugin.addTextToInfoBox("Allotment patch state unknown - north patch");
            plugin.addDebugTextToInfoBox("[ALLOTMENT NORTH] Varbit=" + varbitId + " Value=" + varbitValue);
            return;
        }
        
        // If completed and not HARVESTABLE, return early (no need to show further instructions)
        // This prevents re-highlighting after other game actions
        if (completed && plantState != AllotmentPatchChecker.PlantState.HARVESTABLE) {
            return;
        }
        
        // Check if patch is visible in scene (more accurate than distance to teleport point)
        List<GameObject> patchObjects = gameObjectHighlighter.findGameObjectsByID(patchObjectId);
        boolean patchVisible = !patchObjects.isEmpty();
        
        // Handle north patch states
        if (!patchVisible) {
            plugin.addTextToInfoBox("Navigate to north patch.");
            patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, leftColor);
        } else {
            switch (plantState) {
                case HARVESTABLE:
                    plugin.addTextToInfoBox("Harvest Allotment (north patch).");
                    patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, leftColor);
                    break;
                case PLANT:
                    plugin.addTextToInfoBox("Use Allotment seed on north patch.");
                    patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, useItemColor);
                    itemHighlighter.highlightAllotmentSeeds(graphics);
                    break;
                case DEAD:
                    plugin.addTextToInfoBox("Clear the dead north patch.");
                    patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, leftColor);
                    break;
                case DISEASED:
                    plugin.addTextToInfoBox("Use Plant cure on north patch. Buy at GE or in farming guild/catherby, and store at Tool Leprechaun for easy access.");
                    patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, leftColor);
                    itemHighlighter.itemHighlight(graphics, ItemID.PLANT_CURE, useItemColor);
                    break;
                case NEEDS_WATER:
                    plugin.addTextToInfoBox("Water the north patch.");
                    patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, useItemColor);
                    for (int canId : Constants.WATERING_CAN_IDS) {
                        itemHighlighter.itemHighlight(graphics, canId, useItemColor);
                    }
                    break;
                case WEEDS:
                    plugin.addTextToInfoBox("Rake the north patch.");
                    patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, leftColor);
                    break;
                case GROWING:
                    // Check if compost was just applied (from chat message)
                    if (patchStateChecker.patchIsComposted()) {
                        // Mark as composted (persistent)
                        allotmentPatchState.markComposted(0);
                        return;
                    }
                    // Patch is GROWING but not composted yet - show compost instruction
                    plugin.addTextToInfoBox("Use Compost on north patch.");
                    patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, useItemColor);
                    Integer compostId = itemHighlighter.selectedCompostID();
                    if (compostId != null && itemHighlighter.isItemInInventory(compostId)) {
                        itemHighlighter.itemHighlight(graphics, compostId, useItemColor);
                    } else {
                        compostHighlighter.withdrawCompost(graphics);
                    }
                    break;
                case UNKNOWN:
                    int varbitValueNorth = varbitId != -1 ? client.getVarbitValue(varbitId) : -1;
                    plugin.addTextToInfoBox("UNKNOWN state: Try to do something with the north allotment patch to change its state.");
                    plugin.addDebugTextToInfoBox("[ALLOTMENT NORTH] Varbit=" + varbitId + " Value=" + varbitValueNorth);
                    break;
            }
        }
    }
    
    /**
     * Handles south allotment patch farming steps.
     * Completely separate from north patch handling - only deals with south patch (index 1).
     * North patch is completely ignored once we reach this point.
     */
    private void allotmentSouthSteps(Graphics2D graphics, Location.Teleport teleport) {
        // Safety check: If we're not on south patch (index 1), return immediately
        // This ensures we only process south patch when we're supposed to
        if (allotmentPatchState.getCurrentIndex() != 1) {
            return;
        }
        
        int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        Color leftColor = colorProvider.getLeftClickColorWithAlpha();
        Color useItemColor = colorProvider.getHighlightUseItemWithAlpha();
        
        // Get location name from region ID
        String locationName = getLocationNameFromRegionId(currentRegionId);
        
        // Get patch object IDs for this location
        List<Integer> allotmentPatchIds = farmingHelperOverlay.getAllotmentPatchIdsForLocation(locationName);
        
        // If no patches found or south patch doesn't exist, return
        if (allotmentPatchIds.size() < 2 || allotmentPatchIds.get(1) == null) {
            return;
        }
        
        int patchObjectId = allotmentPatchIds.get(1); // South patch (index 1)
        
        // Get varbit ID from object composition
        int varbitIdFromObject = getAllotmentPatchVarbitId(patchObjectId);
        int varbitId = varbitIdFromObject;
        
        // Fallback: If object composition fails, use location-specific varbits
        if (varbitId == -1) {
            if (locationName.equals("Catherby")) {
                varbitId = Constants.VARBIT_ALLOTMENT_PATCH_SOUTH_B1;
            } else {
                varbitId = Constants.VARBIT_ALLOTMENT_PATCH_SOUTH_B2;
            }
        }
        
        // Check state for south patch
        AllotmentPatchChecker.PlantState plantState = AllotmentPatchChecker.PlantState.UNKNOWN;
        
        if (varbitId != -1) {
            plantState = AllotmentPatchChecker.checkAllotmentPatch(client, varbitId);
        }

        // Check completion status for south patch
        // HARVESTABLE is NOT completed - user still needs to harvest
        // Only GROWING + composted is considered completed (nothing more to do)
        // Don't mark as completed if it's GROWING but not composted yet
        if (!allotmentPatchState.isPatchCompleted(1)) {
            if (plantState == AllotmentPatchChecker.PlantState.GROWING && allotmentPatchState.isPatchComposted(1)) {
                allotmentPatchState.setPatchCompleted(1, true);
            }
        }
        
        // If patch is GROWING and already composted, return early (no need to show compost instruction)
        // This prevents re-highlighting after other game actions
        if (plantState == AllotmentPatchChecker.PlantState.GROWING && allotmentPatchState.isPatchComposted(1)) {
            if (!allotmentPatchState.isPatchCompleted(1)) {
                allotmentPatchState.setPatchCompleted(1, true);
            }
            return; // Don't show compost instruction if already composted
        }
        
        // If patch is done (GROWING + composted) and not HARVESTABLE, return (transition handled by allotmentSteps)
        // Don't return early for HARVESTABLE - user still needs to harvest
        // Don't return early for GROWING if not composted - user still needs to compost
        if (allotmentPatchState.isPatchCompleted(1) && 
            allotmentPatchState.isPatchComposted(1) && 
            plantState != AllotmentPatchChecker.PlantState.HARVESTABLE &&
            plantState != AllotmentPatchChecker.PlantState.GROWING) {
            return;
        }
        
        // If state is unknown, show message and return
        if (plantState == AllotmentPatchChecker.PlantState.UNKNOWN) {
            int varbitValue = varbitId != -1 ? client.getVarbitValue(varbitId) : -1;
            plugin.addTextToInfoBox("Allotment patch state unknown - south patch");
            plugin.addDebugTextToInfoBox("[ALLOTMENT SOUTH] Varbit=" + varbitId + " Value=" + varbitValue);
            return;
        }
        
        // Check if patch is visible in scene (more accurate than distance to teleport point)
        List<GameObject> patchObjects = gameObjectHighlighter.findGameObjectsByID(patchObjectId);
        boolean patchVisible = !patchObjects.isEmpty();
        
        // Handle south patch states
        if (!patchVisible) {
            plugin.addTextToInfoBox("Navigate to south patch.");
            patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, leftColor);
        } else {
            switch (plantState) {
                case HARVESTABLE:
                    plugin.addTextToInfoBox("Harvest Allotment (south patch).");
                    patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, leftColor);
                    break;
                case PLANT:
                    plugin.addTextToInfoBox("Use Allotment seed on south patch.");
                    patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, useItemColor);
                    itemHighlighter.highlightAllotmentSeeds(graphics);
                    break;
                case DEAD:
                    plugin.addTextToInfoBox("Clear the dead south patch.");
                    patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, leftColor);
                    break;
                case DISEASED:
                    plugin.addTextToInfoBox("Use Plant cure on south patch. Buy at GE or in farming guild/catherby, and store at Tool Leprechaun for easy access.");
                    patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, leftColor);
                    itemHighlighter.itemHighlight(graphics, ItemID.PLANT_CURE, useItemColor);
                    break;
                case NEEDS_WATER:
                    plugin.addTextToInfoBox("Water the south patch.");
                    patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, useItemColor);
                    for (int canId : Constants.WATERING_CAN_IDS) {
                        itemHighlighter.itemHighlight(graphics, canId, useItemColor);
                    }
                    break;
                case WEEDS:
                    plugin.addTextToInfoBox("Rake the south patch.");
                    patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, leftColor);
                    break;
                case GROWING:
                    // This case should only be reached if the patch is GROWING and NOT already composted
                    // (the early return above should catch GROWING + composted cases)
                    // Check if compost was just applied (from chat message)
                    boolean isComposted = patchStateChecker.patchIsComposted();
                    if (isComposted) {
                        // Mark as composted (persistent)
                        allotmentPatchState.markComposted(1);
                        return;
                    }
                    // Safety check: If already composted (shouldn't reach here due to early return, but just in case)
                    if (allotmentPatchState.isPatchComposted(1)) {
                        // Patch is already composted, mark as completed and return
                        if (!allotmentPatchState.isPatchCompleted(1)) {
                            allotmentPatchState.setPatchCompleted(1, true);
                        }
                        return; // Don't show compost instruction if already composted
                    }
                    // Patch is GROWING but not composted yet - show compost instruction
                    plugin.addTextToInfoBox("Use Compost on south patch.");
                    patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, useItemColor);
                    Integer compostId = itemHighlighter.selectedCompostID();
                    if (compostId != null && itemHighlighter.isItemInInventory(compostId)) {
                        itemHighlighter.itemHighlight(graphics, compostId, useItemColor);
                    } else {
                        compostHighlighter.withdrawCompost(graphics);
                    }
                    break;
                case UNKNOWN:
                    int varbitValueSouth = varbitId != -1 ? client.getVarbitValue(varbitId) : -1;
                    plugin.addTextToInfoBox("UNKNOWN state: Try to do something with the south allotment patch to change its state.");
                    plugin.addDebugTextToInfoBox("[ALLOTMENT SOUTH] Varbit=" + varbitId + " Value=" + varbitValueSouth);
                    break;
            }
        }
    }
    
    /**
     * Handles tree patch farming steps.
     */
    public void treeSteps(Graphics2D graphics, Location.Teleport teleport) {
        int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        TreePatchChecker.PlantState plantState;
        Color leftColor = colorProvider.getLeftClickColorWithAlpha();
        Color useItemColor = colorProvider.getHighlightUseItemWithAlpha();
        
        // 4771 falador, gnome stronghold, lumbridge, Taverly, Varrock
        // 7905 farming guild
        if (currentRegionId == Constants.REGION_FARMING_GUILD) {
            plantState = TreePatchChecker.checkTreePatch(client, Constants.VARBIT_TREE_PATCH_FARMING_GUILD);
        } else {
            plantState = TreePatchChecker.checkTreePatch(client, Constants.VARBIT_TREE_PATCH_STANDARD);
        }
        
        if (!areaCheck.isPlayerWithinArea(teleport.getPoint(), 15)) {
            // Should be replaced with a pathing system, pointing arrow or something else eventually
            patchHighlighter.highlightTreePatches(graphics, leftColor);
        } else {
            switch (plantState) {
                case HEALTHY:
                    plugin.addTextToInfoBox("Check tree health.");
                    patchHighlighter.highlightTreePatches(graphics, leftColor);
                    break;
                case WEEDS:
                    plugin.addTextToInfoBox("Rake the tree patch.");
                    patchHighlighter.highlightTreePatches(graphics, leftColor);
                    break;
                case DEAD:
                    plugin.addTextToInfoBox("Clear the dead tree patch.");
                    patchHighlighter.highlightTreePatches(graphics, leftColor);
                    break;
                case PLANT:
                    plugin.addTextToInfoBox("Use Sapling on the patch.");
                    patchHighlighter.highlightTreePatches(graphics, useItemColor);
                    itemHighlighter.highlightTreeSapling(graphics);
                    break;
                case DISEASED:
                    plugin.addTextToInfoBox("Prune the tree patch.");                    
                    patchHighlighter.highlightTreePatches(graphics, useItemColor);
                    break;
                case REMOVE:
                    plugin.addTextToInfoBox("Pay to remove tree, or cut it down and clear the patch.");
                    farmerHighlighter.highlightTreeFarmers(graphics);
                    break;
                case UNKNOWN:
                    plugin.addTextToInfoBox("UNKNOWN state: Try to do something with the tree patch to change its state.");
                    break;
                case GROWING:
                    if (config.generalPayForProtection()) {
                        plugin.addTextToInfoBox("Pay to protect the patch.");
                        farmerHighlighter.highlightTreeFarmers(graphics);
                        if (patchStateChecker.patchIsProtected()) {
                            treePatchDone = true;
                        }
                    } else {
                        plugin.addTextToInfoBox("Use Compost on patch.");
                        compostHighlighter.highlightCompost(graphics, false, true, false, 1);
                        if (patchStateChecker.patchIsComposted()) {
                            treePatchDone = true;
                        }
                    }
                    break;
            }
        }
    }
    
    /**
     * Handles fruit tree patch farming steps.
     */
    public void fruitTreeSteps(Graphics2D graphics, Location.Teleport teleport) {
        int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        FruitTreePatchChecker.PlantState plantState;
        Color leftColor = colorProvider.getLeftClickColorWithAlpha();
        Color useItemColor = colorProvider.getHighlightUseItemWithAlpha();
        
        // 4771 brimhaven, catherby, Lletya, tree gnome village
        // 7909 farming guild
        // 4772 gnome stronghold
        if (currentRegionId == Constants.REGION_FARMING_GUILD) {
            plantState = FruitTreePatchChecker.checkFruitTreePatch(client, Constants.VARBIT_FRUIT_TREE_PATCH_FARMING_GUILD);
        } else if (currentRegionId == Constants.REGION_GNOME_STRONGHOLD || currentRegionId == Constants.REGION_GNOME_STRONGHOLD_ALT) {
            plantState = FruitTreePatchChecker.checkFruitTreePatch(client, Constants.VARBIT_FRUIT_TREE_PATCH_GNOME_STRONGHOLD);
        } else {
            plantState = FruitTreePatchChecker.checkFruitTreePatch(client, Constants.VARBIT_FRUIT_TREE_PATCH_STANDARD);
        }
        
        if (!areaCheck.isPlayerWithinArea(teleport.getPoint(), 15)) {
            // Should be replaced with a pathing system, point arrow or something else eventually
            patchHighlighter.highlightFruitTreePatches(graphics, leftColor);
        } else {
            switch (plantState) {
                case HEALTHY:
                    plugin.addTextToInfoBox("Check Fruit tree health.");
                    patchHighlighter.highlightFruitTreePatches(graphics, leftColor);
                    break;
                case WEEDS:
                    plugin.addTextToInfoBox("Rake the fruit tree patch.");
                    patchHighlighter.highlightFruitTreePatches(graphics, leftColor);
                    break;
                case DEAD:
                    plugin.addTextToInfoBox("Clear the dead fruit tree patch.");
                    patchHighlighter.highlightFruitTreePatches(graphics, leftColor);
                    break;
                case PLANT:
                    plugin.addTextToInfoBox("Use Sapling on the patch.");
                    patchHighlighter.highlightFruitTreePatches(graphics, useItemColor);
                    itemHighlighter.highlightFruitTreeSapling(graphics);
                    break;
                case DISEASED:
                    plugin.addTextToInfoBox("Prune the fruit tree patch.");
                    patchHighlighter.highlightFruitTreePatches(graphics, leftColor);
                    break;
                case REMOVE:
                    plugin.addTextToInfoBox("Pay to remove fruit tree, or cut it down and clear the patch.");
                    farmerHighlighter.highlightFruitTreeFarmers(graphics);
                    break;
                case UNKNOWN:
                    plugin.addTextToInfoBox("UNKNOWN state: Try to do something with the tree patch to change its state.");
                    break;
                case GROWING:
                    if (config.generalPayForProtection()) {
                        plugin.addTextToInfoBox("Pay to protect the patch.");
                        farmerHighlighter.highlightFruitTreeFarmers(graphics);
                        if (patchStateChecker.patchIsProtected()) {
                            fruitTreePatchDone = true;
                        }
                    } else {
                        plugin.addTextToInfoBox("Use Compost on patch.");
                        compostHighlighter.highlightCompost(graphics, false, false, true, 1);
                        if (patchStateChecker.patchIsComposted()) {
                            fruitTreePatchDone = true;
                        }
                    }
                    break;
            }
        }
    }
    
    /**
     * Encapsulates allotment patch state tracking.
     * Manages current patch index, completion status, and compost state for both patches.
     */
    private static class AllotmentPatchState {
        private int currentIndex = 0;
        private final boolean[] completed = new boolean[2]; // Track completion of each patch
        private final boolean[] composted = new boolean[2]; // Track compost state per patch independently
        
        /**
         * Gets the current patch index (0 = north patch, 1 = south patch).
         * @return The current patch index
         */
        public int getCurrentIndex() {
            return currentIndex;
        }
        
        /**
         * Checks if a patch at the given index is completed.
         * @param index The patch index (0 = north, 1 = south)
         * @return true if the patch is completed, false otherwise
         */
        public boolean isPatchCompleted(int index) {
            if (index < 0 || index >= completed.length) {
                throw new IllegalArgumentException("Invalid patch index: " + index);
            }
            return completed[index];
        }
        
        /**
         * Checks if a patch at the given index is composted.
         * @param index The patch index (0 = north, 1 = south)
         * @return true if the patch is composted, false otherwise
         */
        public boolean isPatchComposted(int index) {
            if (index < 0 || index >= composted.length) {
                throw new IllegalArgumentException("Invalid patch index: " + index);
            }
            return composted[index];
        }
        
        /**
         * Marks a patch as composted and completed.
         * @param index The patch index (0 = north, 1 = south)
         */
        public void markComposted(int index) {
            if (index < 0 || index >= composted.length) {
                throw new IllegalArgumentException("Invalid patch index: " + index);
            }
            composted[index] = true;
            completed[index] = true;
        }
        
        /**
         * Sets the completion status of a patch.
         * @param index The patch index (0 = north, 1 = south)
         * @param value The completion status to set
         */
        public void setPatchCompleted(int index, boolean value) {
            if (index < 0 || index >= completed.length) {
                throw new IllegalArgumentException("Invalid patch index: " + index);
            }
            completed[index] = value;
        }
        
        /**
         * Moves to the next patch (from north to south).
         * Resets the south patch completion status.
         */
        public void moveToNextPatch() {
            currentIndex = 1;
            completed[1] = false; // Reset for south patch check
        }
        
        /**
         * Resets all state to initial values.
         * Sets current index to 0 and clears all completion and compost flags.
         */
        public void reset() {
            currentIndex = 0;
            completed[0] = false;
            completed[1] = false;
            composted[0] = false;
            composted[1] = false;
        }
    }
}

