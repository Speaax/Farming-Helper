package com.easyfarming.overlays.handlers;

import com.easyfarming.*;
import com.easyfarming.core.Teleport;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Handles farming step logic for herb, flower, tree, and fruit tree patches.
 */
public class FarmingStepHandler {
    /** Hint arrow toward the current patch tile when farther than this (tiles); does not gate instructions. */
    private static final int PATCH_HINT_ARROW_RANGE_TILES = 15;

    private static final List<String> ALLOTMENT_FARMER_NAMES = Arrays.asList(
        "Elstan", "Dantaera", "Kragen", "Marisi", "Lyra", "Alan", "Harminia"
    );
    private static final List<String> HOPS_FARMER_NAMES = Arrays.asList(
        "Vasquen", "Rhonen", "Selena", "Brother Althric", "Ercos"
    );

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
    
    // Persistent compost state tracking (persists until next location)
    private boolean herbPatchComposted = false;
    private boolean flowerPatchComposted = false;
    private boolean treePatchComposted = false;
    private boolean fruitTreePatchComposted = false;
    private boolean hopsPatchComposted = false;
    
    // Allotment patch tracking - which patch we're currently working on (0 = first patch, 1 = second patch)
    private final AllotmentPatchState allotmentPatchState = new AllotmentPatchState();
    private final EasyFarmingOverlay farmingHelperOverlay;
    
    @Inject
    public FarmingStepHandler(Client client, EasyFarmingPlugin plugin, EasyFarmingConfig config,
                              AreaCheck areaCheck,
                              PatchHighlighter patchHighlighter,
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
    public void herbSteps(Graphics2D graphics, Teleport teleport) {
        if (client.getLocalPlayer() == null) {
            return;
        }
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
        
        boolean useSpecificPatch = patchObjectId != null;
        // Instructions follow the active run step whenever we have a teleport context — not player distance to the patch.
        if (teleport == null) {
            // Transitioning between locations; navigation overlay handles routing.
        } else {
            switch (plantState) {
                case HARVESTABLE:
                    plugin.addTextToInfoBox("Harvest Herbs.");
                    if (useSpecificPatch) {
                        patchHighlighter.highlightSpecificHerbPatch(graphics, patchObjectId, leftColor);
                    } else {
                        patchHighlighter.highlightHerbPatches(graphics, leftColor);
                    }
                    break;
                case PLANT:
                    plugin.addTextToInfoBox("Use Herb seed on patch.");
                    if (useSpecificPatch) {
                        patchHighlighter.highlightSpecificHerbPatch(graphics, patchObjectId, useItemColor);
                    } else {
                        patchHighlighter.highlightHerbPatches(graphics, useItemColor);
                    }
                    itemHighlighter.highlightHerbSeeds(graphics);
                    break;
                case DEAD:
                    plugin.addTextToInfoBox("Clear the dead herb patch.");
                    if (useSpecificPatch) {
                        patchHighlighter.highlightSpecificHerbPatch(graphics, patchObjectId, leftColor);
                    } else {
                        patchHighlighter.highlightHerbPatches(graphics, leftColor);
                    }
                    break;
                case DISEASED:
                    plugin.addTextToInfoBox("Use Plant cure on herb patch. Buy at GE or in farming guild/catherby, and store at Tool Leprechaun for easy access.");
                    if (useSpecificPatch) {
                        patchHighlighter.highlightSpecificHerbPatch(graphics, patchObjectId, leftColor);
                    } else {
                        patchHighlighter.highlightHerbPatches(graphics, leftColor);
                    }
                    itemHighlighter.itemHighlight(graphics, ItemID.PLANT_CURE, useItemColor);
                    break;
                case WEEDS:
                    // Check if value is 3 (fully raked, ready to plant)
                    int varbitValue = varbitId != -1 ? client.getVarbitValue(varbitId) : -1;
                    if (varbitValue == 3) {
                        // Fully raked patch, ready to plant
                        plugin.addTextToInfoBox("Use Herb seed on patch.");
                        if (useSpecificPatch) {
                            patchHighlighter.highlightSpecificHerbPatch(graphics, patchObjectId, useItemColor);
                        } else {
                            patchHighlighter.highlightHerbPatches(graphics, useItemColor);
                        }
                        itemHighlighter.highlightHerbSeeds(graphics);
                    } else {
                        // Needs raking
                        plugin.addTextToInfoBox("Rake the herb patch.");
                        if (useSpecificPatch) {
                            patchHighlighter.highlightSpecificHerbPatch(graphics, patchObjectId, leftColor);
                        } else {
                            patchHighlighter.highlightHerbPatches(graphics, leftColor);
                        }
                    }
                    break;
                case GROWING:
                    if (config.generalPayForProtection() && locationHasAllotmentFarmer(locationName)) {
                        plugin.addTextToInfoBox("Pay to protect the patch.");
                        farmerHighlighter.highlightAllotmentFarmers(graphics);
                        setHintArrowToFirstAvailableNPC(ALLOTMENT_FARMER_NAMES);
                        if (patchStateChecker.patchIsProtected()) {
                            herbPatchDone = true;
                            clearHintArrow();
                        }
                    } else {
                        // Check persistent state FIRST - if already composted, mark as done and return
                        if (herbPatchComposted) {
                            herbPatchDone = true;  // Set done flag so transition happens
                            clearHintArrow();
                            return;
                        }
                        // If already done (shouldn't happen, but safety check)
                        if (herbPatchDone) {
                            clearHintArrow();
                            return;
                        }
                        // Check if compost was just applied (from chat message) - this sets herbPatchDone
                        boolean isComposted = patchStateChecker.patchIsCompostedForHerbPatch();
                        if (isComposted) {
                            herbPatchComposted = true;  // Set persistent state
                            herbPatchDone = true;
                            // Clear hint arrow when patch is done
                            clearHintArrow();
                            // Don't show anything - transition will happen on next frame
                            return;
                        }
                        // Patch is GROWING but not composted yet - show compost instruction
                        plugin.addTextToInfoBox("Use Compost on patch.");
                        Integer compostId = itemHighlighter.selectedCompostID();
                        // If compost is not in inventory, set hint arrow to Tool Leprechaun
                        if (compostId != null && !itemHighlighter.isItemInInventory(compostId)) {
                            setHintArrowToNPC("Tool Leprechaun");
                        } else {
                            // Clear hint arrow if compost is in inventory (no NPC interaction needed)
                            clearHintArrow();
                        }
                        if (useSpecificPatch) {
                            patchHighlighter.highlightSpecificHerbPatch(graphics, patchObjectId, useItemColor);
                        } else {
                            patchHighlighter.highlightHerbPatches(graphics, useItemColor);
                        }
                        compostHighlighter.highlightCompost(graphics, true, false, false, 1);
                    }
                    break;
                case UNKNOWN:
                    plugin.addTextToInfoBox("UNKNOWN state: Try to do something with the herb patch to change its state.");
                    break;
            }
            applyPatchDirectionHintArrow(getHerbPatchWorldPoint(locationName));
        }
    }
    
    /**
     * Handles hops patch farming steps.
     */
    public void hopsSteps(Graphics2D graphics, Teleport teleport) {
        if (client.getLocalPlayer() == null) {
            return;
        }
        int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        HopsPatchChecker.PlantState plantState = HopsPatchChecker.PlantState.UNKNOWN;
        Color leftColor = colorProvider.getLeftClickColorWithAlpha();
        Color useItemColor = colorProvider.getHighlightUseItemWithAlpha();
        
        // Check if player is in Civitas and using Quetzal_Transport for Aldarin
        // Civitas teleport can land in either region 6704 or 6705
        // If so, show instructions to use Renu to fly to Aldarin
        if ((currentRegionId == 6704 || currentRegionId == 6705) && teleport != null && 
            "Quetzal_Transport".equals(teleport.getEnumOption())) {
            plugin.addTextToInfoBox("Fly Renu to Aldarin.");
            gameObjectHighlighter.highlightGameObject(52815, useItemColor).render(graphics);
            return;
        }
        
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
        
        // Instructions follow the active run step whenever we have a teleport context — not distance to the patch.
        boolean shouldShowInstructions = teleport != null;
        
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
                        break;
                    case NEEDS_WATER:
                        if (config.generalPayForProtection() && locationHasHopsFarmer(locationName)) {
                            // Pay-for-protection replaces watering
                            plugin.addTextToInfoBox("Pay to protect the patch.");
                            farmerHighlighter.highlightHopsFarmers(graphics);
                            setHintArrowToFirstAvailableNPC(HOPS_FARMER_NAMES);
                            if (patchStateChecker.patchIsProtected()) {
                                hopsPatchDone = true;
                                clearHintArrow();
                            }
                        } else {
                            plugin.addTextToInfoBox("Water the hops patch.");
                            patchHighlighter.highlightSpecificHopsPatch(graphics, patchObjectId, useItemColor);
                            // Highlight all watering can variants
                            for (int canId : Constants.WATERING_CAN_IDS) {
                                itemHighlighter.itemHighlight(graphics, canId, useItemColor);
                            }
                        }
                        break;
                    case GROWING:
                        if (config.generalPayForProtection() && locationHasHopsFarmer(locationName)) {
                            plugin.addTextToInfoBox("Pay to protect the patch.");
                            farmerHighlighter.highlightHopsFarmers(graphics);
                            setHintArrowToFirstAvailableNPC(HOPS_FARMER_NAMES);
                            if (patchStateChecker.patchIsProtected()) {
                                hopsPatchDone = true;
                                clearHintArrow();
                            }
                        } else {
                            // Check persistent state FIRST - if already composted, mark as done and return
                            if (hopsPatchComposted) {
                                hopsPatchDone = true;  // Set done flag so transition happens
                                clearHintArrow();
                                return;
                            }
                            // If already done (shouldn't happen, but safety check)
                            if (hopsPatchDone) {
                                clearHintArrow();
                                return;
                            }
                            // Check if compost was just applied (from chat message)
                            boolean isComposted = patchStateChecker.patchIsCompostedForHopsPatch();
                            if (isComposted) {
                                hopsPatchComposted = true;  // Set persistent state
                                hopsPatchDone = true;
                                clearHintArrow();
                                return;
                            }
                            // Patch is GROWING but not composted yet - show compost instruction
                            plugin.addTextToInfoBox("Use Compost on patch.");
                            patchHighlighter.highlightSpecificHopsPatch(graphics, patchObjectId, useItemColor);
                            Integer compostId = itemHighlighter.selectedCompostID();
                            // If compost is not in inventory, set hint arrow to Tool Leprechaun
                            if (compostId != null && !itemHighlighter.isItemInInventory(compostId)) {
                                setHintArrowToNPC("Tool Leprechaun");
                            } else {
                                // Clear hint arrow if compost is in inventory (no NPC interaction needed)
                                clearHintArrow();
                            }
                            compostHighlighter.highlightCompost(graphics, true, false, false, 1);
                        }
                        break;
                    case UNKNOWN:
                        plugin.addTextToInfoBox("UNKNOWN state: Try to do something with the hops patch to change its state.");
                        patchHighlighter.highlightSpecificHopsPatch(graphics, patchObjectId, leftColor);
                        break;
                }
            } else {
                // Fallback: highlight all patches if patchObjectId is null
                patchHighlighter.highlightHopsPatches(graphics, leftColor);
            }
            applyPatchDirectionHintArrow(getHopsPatchWorldPoint(locationName));
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
        if (client.getLocalPlayer() == null) {
            return;
        }
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
            // Always highlight specific patch if patchObjectId is available, similar to herb patches
            if (patchObjectId != null) {
                switch (plantState) {
                    case HARVESTABLE:
                        plugin.addTextToInfoBox("Harvest Limwurt root.");
                        patchHighlighter.highlightSpecificFlowerPatch(graphics, patchObjectId, leftColor);
                        break;
                    case DISEASED:
                        plugin.addTextToInfoBox("Cure the diseased Limwurt.");
                        patchHighlighter.highlightSpecificFlowerPatch(graphics, patchObjectId, leftColor);
                        itemHighlighter.itemHighlight(graphics, ItemID.PLANT_CURE, useItemColor);
                        break;
                    case WEEDS:
                        // Check if value is 3 (fully raked, ready to plant) - similar to herb patches
                        int varbitValue = varbitId != -1 ? client.getVarbitValue(varbitId) : -1;
                        if (varbitValue == 3) {
                            // Fully raked patch, ready to plant
                            plugin.addTextToInfoBox("Use Limwurt seed on the patch.");
                            patchHighlighter.highlightSpecificFlowerPatch(graphics, patchObjectId, useItemColor);
                            itemHighlighter.highlightFlowerSeeds(graphics);
                        } else {
                            // Needs raking - always highlight when WEEDS state is detected
                            plugin.addTextToInfoBox("Rake the flower patch.");
                            patchHighlighter.highlightSpecificFlowerPatch(graphics, patchObjectId, leftColor);
                        }
                        break;
                    case DEAD:
                        plugin.addTextToInfoBox("Clear the dead flower patch.");
                        patchHighlighter.highlightSpecificFlowerPatch(graphics, patchObjectId, leftColor);
                        break;
                    case PLANT:
                        plugin.addTextToInfoBox("Use Limwurt seed on the patch.");
                        patchHighlighter.highlightSpecificFlowerPatch(graphics, patchObjectId, useItemColor);
                        itemHighlighter.highlightFlowerSeeds(graphics);
                        break;
                    case GROWING:
                        if (config.generalPayForProtection() && locationHasAllotmentFarmer(locationName)) {
                            plugin.addTextToInfoBox("Pay to protect the patch.");
                            farmerHighlighter.highlightAllotmentFarmers(graphics);
                            setHintArrowToFirstAvailableNPC(ALLOTMENT_FARMER_NAMES);
                            if (patchStateChecker.patchIsProtected()) {
                                flowerPatchDone = true;
                                clearHintArrow();
                            }
                        } else {
                            // Check persistent state FIRST - if already composted, mark as done and return
                            if (flowerPatchComposted) {
                                flowerPatchDone = true;  // Set done flag so transition happens
                                clearHintArrow();
                                return;
                            }
                            // If already done (shouldn't happen, but safety check)
                            if (flowerPatchDone) {
                                clearHintArrow();
                                return;
                            }
                            // Check if compost was just applied (from chat message)
                            boolean isComposted = patchStateChecker.patchIsCompostedForFlowerPatch();
                            if (isComposted) {
                                flowerPatchComposted = true;  // Set persistent state
                                flowerPatchDone = true;
                                clearHintArrow();
                                return;
                            }
                            // Patch is GROWING but not composted yet - show compost instruction
                            plugin.addTextToInfoBox("Use Compost on patch.");
                            patchHighlighter.highlightSpecificFlowerPatch(graphics, patchObjectId, useItemColor);
                            compostHighlighter.highlightCompost(graphics, false, false, false, 2);
                        }
                        break;
                    case UNKNOWN:
                        plugin.addTextToInfoBox("UNKNOWN state: Try to do something with the flower patch to change its state.");
                        patchHighlighter.highlightSpecificFlowerPatch(graphics, patchObjectId, leftColor);
                        break;
                }
                applyPatchDirectionHintArrow(getFlowerPatchWorldPoint(locationName));
            } else {
                // Fallback: highlight all flower patches if patch ID not found
                patchHighlighter.highlightFlowerPatches(graphics, leftColor);
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
            case Constants.REGION_MORYTANIA_ALQ:
                return "Morytania";
            case Constants.REGION_CIVITAS:
                return "Civitas illa Fortis";
            case Constants.REGION_HARMONY:
                return "Harmony Island";
            case Constants.REGION_TROLL_STRONGHOLD:
                return "Troll Stronghold";
            case Constants.REGION_WEISS:
                return "Weiss";
            case Constants.REGION_GNOME_STRONGHOLD:
            case Constants.REGION_GNOME_STRONGHOLD_ALT:
                return "Gnome Stronghold";
            default:
                return "Unknown";
        }
    }
    
    private boolean locationHasAllotmentFarmer(String locationName) {
        switch (locationName) {
            case "Harmony Island":
            case "Troll Stronghold":
            case "Weiss":
                return false;
            default:
                return true;
        }
    }

    private boolean locationHasHopsFarmer(String locationName) {
        switch (locationName) {
            case "Unknown":
                return false;
            default:
                return true;
        }
    }

    /**
     * Gets the fruit tree location name from a region ID.
     * @param regionId The region ID
     * @return The location name, or null if not found
     */
    private String getFruitTreeLocationNameFromRegionId(int regionId) {
        switch (regionId) {
            case Constants.REGION_ARDOUGNE:
            case Constants.REGION_ARDOUGNE_ALT:
                return "Brimhaven";  // Brimhaven is in Ardougne region
            case Constants.REGION_CATHERBY:
                return "Catherby";
            case Constants.REGION_FARMING_GUILD:
                return "Farming Guild";
            case 9265:  // Lletya region
                return "Lletya";
            case 10033:  // Tree Gnome Village region (spirit tree)
                return "Tree Gnome Village";
            case Constants.REGION_GNOME_STRONGHOLD:
            case Constants.REGION_GNOME_STRONGHOLD_ALT:
                // Region 9782 is shared between Gnome Stronghold and Tree Gnome Village
                // Need to distinguish by checking distance to patch points
                WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
                WorldPoint treeGnomeVillagePoint = new WorldPoint(2490, 3180, 0);
                WorldPoint gnomeStrongholdPoint = new WorldPoint(2436, 3415, 0);
                if (playerLocation.distanceTo(treeGnomeVillagePoint) < playerLocation.distanceTo(gnomeStrongholdPoint)) {
                    return "Tree Gnome Village";
                }
                return "Gnome Stronghold";
            default:
                return null;
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
     * Gets the varbit ID for a fruit tree patch by checking the object composition.
     * @param objectId The object ID of the fruit tree patch
     * @return The varbit ID, or -1 if not found
     */
    private int getFruitTreePatchVarbitId(Integer objectId) {
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
    public void allotmentSteps(Graphics2D graphics, Teleport teleport) {
        if (client.getLocalPlayer() == null) {
            return;
        }
        // Check if this location has allotment patches
        int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        String locationName = getLocationNameFromRegionId(currentRegionId);
        List<Integer> allotmentPatchIds = farmingHelperOverlay.getAllotmentPatchIdsForLocation(locationName);
        
        // If this location has no allotment patches, mark as done immediately
        if (allotmentPatchIds == null || allotmentPatchIds.isEmpty()) {
            plugin.addTextToInfoBox("No allotment patches found (region=" + currentRegionId + " loc=" + locationName + ")");
            this.allotmentPatchDone = true;
            allotmentPatchState.reset();
            return;
        }
        
        // Handle north patch first
        if (allotmentPatchState.getCurrentIndex() == 0) {
            allotmentNorthSteps(graphics, teleport);
            // If north patch is done (GROWING + composted), move to south patch
            // Once we move to south patch, north patch is completely ignored for this run
            // Only transition if north patch is actually completed (GROWING + composted)
            if (allotmentPatchState.isPatchCompleted(0) && allotmentPatchState.isPatchComposted(0)) {
                allotmentPatchState.moveToNextPatch();
                plugin.clearLastMessage();
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
    private void allotmentNorthSteps(Graphics2D graphics, Teleport teleport) {
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
            int debugValue = varbitId != -1 ? client.getVarbitValue(varbitId) : -1;
            plugin.addTextToInfoBox("Allotment patch state unknown - north patch (obj=" + patchObjectId + " varbit=" + varbitId + " val=" + debugValue + ")");
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
            // Clear hint arrow when patch is visible (player is near)
            clearHintArrow();
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
                case WEEDS:
                    plugin.addTextToInfoBox("Rake the north patch.");
                    patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, leftColor);
                    break;
                case GROWING:
                    if (config.generalPayForProtection() && locationHasAllotmentFarmer(locationName)) {
                        plugin.addTextToInfoBox("Pay to protect the north patch.");
                        farmerHighlighter.highlightAllotmentFarmers(graphics);
                        setHintArrowToFirstAvailableNPC(ALLOTMENT_FARMER_NAMES);
                        if (patchStateChecker.patchIsProtected()) {
                            allotmentPatchState.markComposted(0);
                            clearHintArrow();
                        }
                    } else {
                        // Check if compost was just applied (from chat message)
                        if (patchStateChecker.patchIsCompostedForAllotmentPatch()) {
                            // Mark as composted (persistent)
                            allotmentPatchState.markComposted(0);
                            // Clear hint arrow when patch is composted
                            clearHintArrow();
                            return;
                        }
                        // Patch is GROWING but not composted yet - show compost instruction
                        plugin.addTextToInfoBox("Use Compost on north patch.");
                        patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, useItemColor);
                        Integer compostId = itemHighlighter.selectedCompostID();
                        if (compostId != null && itemHighlighter.isItemInInventory(compostId)) {
                            itemHighlighter.highlightCompost(graphics, compostId, useItemColor);
                            // Clear hint arrow if compost is in inventory (no NPC interaction needed)
                            clearHintArrow();
                        } else {
                            compostHighlighter.withdrawCompost(graphics);
                            // If compost is not in inventory, set hint arrow to Tool Leprechaun
                            if (compostId != null) {
                                setHintArrowToNPC("Tool Leprechaun");
                            }
                        }
                    }
                    break;
                case UNKNOWN:
                    plugin.addTextToInfoBox("UNKNOWN state: Try to do something with the north allotment patch to change its state.");
                    break;
            }
        }
    }
    
    /**
     * Handles south allotment patch farming steps.
     * Completely separate from north patch handling - only deals with south patch (index 1).
     * North patch is completely ignored once we reach this point.
     */
    private void allotmentSouthSteps(Graphics2D graphics, Teleport teleport) {
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
            plugin.addTextToInfoBox("Allotment patch state unknown - south patch");
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
            // Clear hint arrow when patch is visible (player is near)
            clearHintArrow();
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
                case WEEDS:
                    plugin.addTextToInfoBox("Rake the south patch.");
                    patchHighlighter.highlightSpecificAllotmentPatch(graphics, patchObjectId, leftColor);
                    break;
                case GROWING:
                    if (config.generalPayForProtection() && locationHasAllotmentFarmer(locationName)) {
                        plugin.addTextToInfoBox("Pay to protect the south patch.");
                        farmerHighlighter.highlightAllotmentFarmers(graphics);
                        setHintArrowToFirstAvailableNPC(ALLOTMENT_FARMER_NAMES);
                        if (patchStateChecker.patchIsProtected()) {
                            allotmentPatchState.markComposted(1);
                            clearHintArrow();
                        }
                    } else {
                        // This case should only be reached if the patch is GROWING and NOT already composted
                        // (the early return above should catch GROWING + composted cases)
                        // Check if compost was just applied (from chat message)
                        boolean isComposted = patchStateChecker.patchIsCompostedForAllotmentPatch();
                        if (isComposted) {
                            // Mark as composted (persistent)
                            allotmentPatchState.markComposted(1);
                            // Clear hint arrow when patch is composted
                            clearHintArrow();
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
                            itemHighlighter.highlightCompost(graphics, compostId, useItemColor);
                            // Clear hint arrow if compost is in inventory (no NPC interaction needed)
                            clearHintArrow();
                        } else {
                            compostHighlighter.withdrawCompost(graphics);
                            // If compost is not in inventory, set hint arrow to Tool Leprechaun
                            if (compostId != null) {
                                setHintArrowToNPC("Tool Leprechaun");
                            }
                        }
                    }
                    break;
                case UNKNOWN:
                    int debugValue = varbitId != -1 ? client.getVarbitValue(varbitId) : -1;
                    plugin.addTextToInfoBox("UNKNOWN state: south patch (obj=" + patchObjectId + " varbit=" + varbitId + " val=" + debugValue + ")");
                    break;
            }
        }
    }
    
    /**
     * Handles tree patch farming steps.
     */
    public void treeSteps(Graphics2D graphics, Teleport teleport) {
        if (client.getLocalPlayer() == null) {
            return;
        }
        int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        TreePatchChecker.PlantState plantState;
        Color leftColor = colorProvider.getLeftClickColorWithAlpha();
        Color useItemColor = colorProvider.getHighlightUseItemWithAlpha();
        
        // 4771 falador, gnome stronghold, lumbridge, taverley, Varrock
        // 7905 farming guild
        if (currentRegionId == Constants.REGION_FARMING_GUILD) {
            plantState = TreePatchChecker.checkTreePatch(client, Constants.VARBIT_TREE_PATCH_FARMING_GUILD);
        } else {
            plantState = TreePatchChecker.checkTreePatch(client, Constants.VARBIT_TREE_PATCH_STANDARD);
        }

        // Instructions follow the active run step whenever we have a teleport context — not player distance to the patch.
        if (teleport == null) {
            // Transitioning between locations.
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
                    // Set hint arrow to first available tree farmer
                    setHintArrowToFirstAvailableNPC(Arrays.asList(
                        "Alain", "Fayeth", "Heskel", "Prissy Scilla", "Rosie", "Treznor"
                    ));
                    break;
                case UNKNOWN:
                    plugin.addTextToInfoBox("UNKNOWN state: Try to do something with the tree patch to change its state.");
                    break;
                case GROWING:
                    if (config.generalPayForProtection()) {
                        plugin.addTextToInfoBox("Pay to protect the patch.");
                        farmerHighlighter.highlightTreeFarmers(graphics);
                        // Set hint arrow to first available tree farmer
                        setHintArrowToFirstAvailableNPC(Arrays.asList(
                            "Alain", "Fayeth", "Heskel", "Prissy Scilla", "Rosie", "Treznor"
                        ));
                        if (patchStateChecker.patchIsProtected()) {
                            treePatchDone = true;
                            clearHintArrow();
                        }
                    } else {
                        // Check persistent state FIRST - if already composted, mark as done and return
                        if (treePatchComposted) {
                            treePatchDone = true;  // Set done flag so transition happens
                            clearHintArrow();
                            return;
                        }
                        // If already done (shouldn't happen, but safety check)
                        if (treePatchDone) {
                            clearHintArrow();
                            return;
                        }
                        // Check if compost was just applied (from chat message)
                        boolean isComposted = patchStateChecker.patchIsCompostedForTreePatch();
                        if (isComposted) {
                            treePatchComposted = true;  // Set persistent state
                            treePatchDone = true;
                            clearHintArrow();
                            return;
                        }
                        // Patch is GROWING but not composted yet - show compost instruction
                        plugin.addTextToInfoBox("Use Compost on patch.");
                        Integer compostId = itemHighlighter.selectedCompostID();
                        // If compost is not in inventory, set hint arrow to Tool Leprechaun
                        if (compostId != null && !itemHighlighter.isItemInInventory(compostId)) {
                            setHintArrowToNPC("Tool Leprechaun");
                        } else {
                            // Clear hint arrow if compost is in inventory (no NPC interaction needed)
                            clearHintArrow();
                        }
                        compostHighlighter.highlightCompost(graphics, false, true, false, 1);
                    }
                    break;
            }
            applyPatchDirectionHintArrow(getTreePatchWorldPoint(getTreeLocationNameFromRegionId(currentRegionId)));
        }
    }
    
    /**
     * Handles fruit tree patch farming steps.
     */
    public void fruitTreeSteps(Graphics2D graphics, Teleport teleport) {
        if (client.getLocalPlayer() == null) {
            return;
        }
        int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        FruitTreePatchChecker.PlantState plantState = FruitTreePatchChecker.PlantState.UNKNOWN;
        Color leftColor = colorProvider.getLeftClickColorWithAlpha();
        Color useItemColor = colorProvider.getHighlightUseItemWithAlpha();
        
        // Get location name from region ID
        String locationName = getFruitTreeLocationNameFromRegionId(currentRegionId);
        
        // Get patch object ID for this location
        Integer patchObjectId = locationName != null ? farmingHelperOverlay.getFruitTreePatchIdForLocation(locationName) : null;
        
        int varbitId = -1;
        
        // Try to get varbit from object composition
        if (patchObjectId != null) {
            varbitId = getFruitTreePatchVarbitId(patchObjectId);
        }
        
        // Fallback: If object composition fails, use location-specific varbits
        if (varbitId == -1) {
            if (currentRegionId == Constants.REGION_FARMING_GUILD) {
                varbitId = Constants.VARBIT_FRUIT_TREE_PATCH_FARMING_GUILD;
            } else if (currentRegionId == Constants.REGION_GNOME_STRONGHOLD || currentRegionId == Constants.REGION_GNOME_STRONGHOLD_ALT) {
                varbitId = Constants.VARBIT_FRUIT_TREE_PATCH_GNOME_STRONGHOLD;
            } else {
                varbitId = Constants.VARBIT_FRUIT_TREE_PATCH_STANDARD;
            }
        }
        
        // Check state for fruit tree patch
        if (varbitId != -1) {
            plantState = FruitTreePatchChecker.checkFruitTreePatch(client, varbitId);
        }

        // Instructions follow the active run step whenever we have a teleport context — not player distance to the patch.
        if (teleport == null) {
            // Transitioning between locations.
        } else {
            switch (plantState) {
                case HEALTHY:
                    plugin.addTextToInfoBox("Check Fruit tree health.");
                    patchHighlighter.highlightFruitTreePatches(graphics, leftColor);
                    break;
                case WEEDS:
                    // Check if value is 3 (fully raked, ready to plant)
                    int varbitValue = varbitId != -1 ? client.getVarbitValue(varbitId) : -1;
                    if (varbitValue == 3) {
                        // Fully raked patch, ready to plant
                        plugin.addTextToInfoBox("Use Sapling on the patch.");
                        patchHighlighter.highlightFruitTreePatches(graphics, useItemColor);
                        itemHighlighter.highlightFruitTreeSapling(graphics);
                    } else {
                        // Needs raking
                        plugin.addTextToInfoBox("Rake the fruit tree patch.");
                        patchHighlighter.highlightFruitTreePatches(graphics, leftColor);
                    }
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
                    // Set hint arrow to first available fruit tree farmer
                    setHintArrowToFirstAvailableNPC(Arrays.asList(
                        "Bolongo", "Ellena", "Garth", "Gileth", "Liliwen", "Nikkie"
                    ));
                    break;
                case UNKNOWN:
                    plugin.addTextToInfoBox("UNKNOWN state: Try to do something with the tree patch to change its state.");
                    break;
                case GROWING:
                    if (config.generalPayForProtection()) {
                        plugin.addTextToInfoBox("Pay to protect the patch.");
                        farmerHighlighter.highlightFruitTreeFarmers(graphics);
                        // Set hint arrow to first available fruit tree farmer
                        setHintArrowToFirstAvailableNPC(Arrays.asList(
                            "Bolongo", "Ellena", "Garth", "Gileth", "Liliwen", "Nikkie"
                        ));
                        if (patchStateChecker.patchIsProtected()) {
                            fruitTreePatchDone = true;
                            clearHintArrow();
                        }
                    } else {
                        // Check persistent state FIRST - if already composted, mark as done and return
                        if (fruitTreePatchComposted) {
                            fruitTreePatchDone = true;  // Set done flag so transition happens
                            clearHintArrow();
                            return;
                        }
                        // If already done (shouldn't happen, but safety check)
                        if (fruitTreePatchDone) {
                            clearHintArrow();
                            return;
                        }
                        // Check if compost was just applied (from chat message)
                        boolean isComposted = patchStateChecker.patchIsCompostedForFruitTreePatch();
                        if (isComposted) {
                            fruitTreePatchComposted = true;  // Set persistent state
                            fruitTreePatchDone = true;
                            clearHintArrow();
                            return;
                        }
                        // Patch is GROWING but not composted yet - show compost instruction
                        plugin.addTextToInfoBox("Use Compost on patch.");
                        Integer compostId = itemHighlighter.selectedCompostID();
                        // If compost is not in inventory, set hint arrow to Tool Leprechaun
                        if (compostId != null && !itemHighlighter.isItemInInventory(compostId)) {
                            setHintArrowToNPC("Tool Leprechaun");
                        } else {
                            // Clear hint arrow if compost is in inventory (no NPC interaction needed)
                            clearHintArrow();
                        }
                        compostHighlighter.highlightCompost(graphics, false, false, true, 1);
                    }
                    break;
            }
            applyPatchDirectionHintArrow(getFruitTreePatchWorldPoint(locationName));
        }
    }

    /**
     * Points the hint arrow at the patch when the player is farther than {@link #PATCH_HINT_ARROW_RANGE_TILES}
     * from the patch anchor. Does not change instruction text. NPC/farmer arrows set in the same frame may be
     * replaced only while still outside that range (direction toward the patch).
     */
    private void applyPatchDirectionHintArrow(WorldPoint patchWorldPoint) {
        if (patchWorldPoint == null || client.getLocalPlayer() == null) {
            return;
        }
        if (areaCheck.isPlayerWithinArea(patchWorldPoint, PATCH_HINT_ARROW_RANGE_TILES)) {
            return;
        }
        setHintArrow(patchWorldPoint);
    }

    /** Herb patch anchor (same tile basis as {@link com.easyfarming.FarmingTeleportOverlay#getHerbPatchPoint}). */
    private static WorldPoint getHerbPatchWorldPoint(String locationName) {
        if (locationName == null) {
            return null;
        }
        switch (locationName) {
            case "Ardougne":
                return new WorldPoint(2670, 3374, 0);
            case "Catherby":
                return new WorldPoint(2813, 3463, 0);
            case "Falador":
                return new WorldPoint(3058, 3307, 0);
            case "Farming Guild":
                return new WorldPoint(1238, 3726, 0);
            case "Harmony Island":
                return new WorldPoint(3789, 2837, 0);
            case "Kourend":
                return new WorldPoint(1738, 3550, 0);
            case "Morytania":
                return new WorldPoint(3601, 3525, 0);
            case "Troll Stronghold":
                return new WorldPoint(2824, 3696, 0);
            case "Weiss":
                return new WorldPoint(2847, 3931, 0);
            case "Civitas illa Fortis":
                return new WorldPoint(1586, 3099, 0);
            default:
                return null;
        }
    }

    private static WorldPoint getFlowerPatchWorldPoint(String locationName) {
        return getHerbPatchWorldPoint(locationName);
    }

    private static WorldPoint getTreePatchWorldPoint(String locationName) {
        if (locationName == null) {
            return null;
        }
        switch (locationName) {
            case "Falador":
                return new WorldPoint(3000, 3373, 0);
            case "Farming Guild":
                return new WorldPoint(1232, 3736, 0);
            case "Gnome Stronghold":
                return new WorldPoint(2436, 3415, 0);
            case "Lumbridge":
                return new WorldPoint(3193, 3231, 0);
            case "Taverley":
                return new WorldPoint(2936, 3438, 0);
            case "Varrock":
                return new WorldPoint(3229, 3459, 0);
            default:
                return null;
        }
    }

    private static String getTreeLocationNameFromRegionId(int regionId) {
        switch (regionId) {
            case Constants.REGION_FALADOR:
                return "Falador";
            case Constants.REGION_FARMING_GUILD:
                return "Farming Guild";
            case Constants.REGION_GNOME_STRONGHOLD:
            case Constants.REGION_GNOME_STRONGHOLD_ALT:
                return "Gnome Stronghold";
            case 12850:
                return "Lumbridge";
            case 12853:
                return "Varrock";
            case 11828:
                return "Taverley";
            default:
                return null;
        }
    }

    private static WorldPoint getFruitTreePatchWorldPoint(String locationName) {
        if (locationName == null) {
            return null;
        }
        switch (locationName) {
            case "Brimhaven":
                return new WorldPoint(2764, 3212, 0);
            case "Catherby":
                return new WorldPoint(2860, 3433, 0);
            case "Farming Guild":
                return new WorldPoint(1243, 3759, 0);
            case "Gnome Stronghold":
                return new WorldPoint(2475, 3446, 0);
            case "Lletya":
                return new WorldPoint(2346, 3162, 0);
            case "Tree Gnome Village":
                return new WorldPoint(2490, 3180, 0);
            default:
                return null;
        }
    }

    private static WorldPoint getHopsPatchWorldPoint(String locationName) {
        if (locationName == null || "Unknown".equals(locationName)) {
            return null;
        }
        switch (locationName) {
            case "Aldarin":
                return new WorldPoint(1365, 2939, 0);
            case "Entrana":
                return new WorldPoint(2811, 3337, 0);
            case "Lumbridge":
                return new WorldPoint(3229, 3315, 0);
            case "Seers Village":
                return new WorldPoint(2667, 3526, 0);
            case "Yanille":
                return new WorldPoint(2576, 3105, 0);
            default:
                return null;
        }
    }
    
    /**
     * Sets a hint arrow pointing to the specified WorldPoint.
     * @param point The WorldPoint to point the hint arrow to
     */
    private void setHintArrow(WorldPoint point) {
        if (point != null && client != null) {
            try {
                client.setHintArrow(point);
            } catch (Exception e) {
                // Silently handle any exceptions (e.g., if called from wrong thread)
                // Hint arrows will be set on next frame
            }
        }
    }
    
    /**
     * Sets a hint arrow pointing to an NPC by name.
     * @param npcName The name of the NPC to point the hint arrow to
     */
    private void setHintArrowToNPC(String npcName) {
        if (npcName == null || client == null) {
            return;
        }
        
        try {
            net.runelite.api.IndexedObjectSet<? extends net.runelite.api.NPC> npcs = client.getTopLevelWorldView().npcs();
            if (npcs != null) {
                for (net.runelite.api.NPC npc : npcs) {
                    if (npc != null && npc.getName() != null && npc.getName().equals(npcName)) {
                        client.setHintArrow(npc);
                        return; // Found the NPC, set arrow and return
                    }
                }
            }
        } catch (Exception e) {
            // Silently handle any exceptions
        }
    }
    
    /**
     * Sets hint arrow to the first available NPC from a list of NPC names.
     * @param npcNames List of NPC names to search for
     */
    private void setHintArrowToFirstAvailableNPC(List<String> npcNames) {
        if (client != null && npcNames != null) {
            try {
                net.runelite.api.IndexedObjectSet<? extends net.runelite.api.NPC> npcs = client.getTopLevelWorldView().npcs();
                if (npcs != null) {
                    for (String npcName : npcNames) {
                        for (net.runelite.api.NPC npc : npcs) {
                            if (npc != null && npc.getName() != null && npc.getName().equals(npcName)) {
                                client.setHintArrow(npc);
                                return; // Found an NPC, set arrow and return
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // Silently handle any exceptions
            }
        }
        // If no NPC found, clear any existing hint arrow
        if (client != null) {
            client.clearHintArrow();
        }
    }
    
    /**
     * Clears the hint arrow using the client's clearHintArrow method.
     * Public method to allow clearing from other classes (e.g., when overlay is removed).
     */
    public void clearHintArrow() {
        client.clearHintArrow();
    }
    
    /**
     * Encapsulates allotment patch state tracking.
     * Manages current patch index, completion status, compost state, and crop type for both patches.
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
         * Sets current index to 0 and clears all completion, compost, and crop type flags.
         */
        public void reset() {
            currentIndex = 0;
            completed[0] = false;
            completed[1] = false;
            composted[0] = false;
            composted[1] = false;
        }
    }

    /**
     * Resets all persistent compost states (called when moving to next location).
     */
    public void resetCompostStates() {
        herbPatchComposted = false;
        flowerPatchComposted = false;
        treePatchComposted = false;
        fruitTreePatchComposted = false;
        hopsPatchComposted = false;
    }
}

