package com.easyfarming.overlays.handlers;

import com.easyfarming.*;
import com.easyfarming.core.Teleport;
import com.easyfarming.overlays.highlighting.*;
import com.easyfarming.overlays.utils.ColorProvider;
import com.easyfarming.overlays.utils.PatchStateChecker;
import com.easyfarming.utils.Constants;
import net.runelite.api.Client;
import net.runelite.api.gameval.ItemID;

import javax.inject.Inject;
import java.awt.*;

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
    
    // State tracking
    public boolean herbPatchDone = false;
    public boolean flowerPatchDone = false;
    public boolean treePatchDone = false;
    public boolean fruitTreePatchDone = false;
    
    @Inject
    public FarmingStepHandler(Client client, EasyFarmingPlugin plugin, EasyFarmingConfig config,
                              AreaCheck areaCheck, PatchHighlighter patchHighlighter,
                              ItemHighlighter itemHighlighter, CompostHighlighter compostHighlighter,
                              FarmerHighlighter farmerHighlighter, PatchStateChecker patchStateChecker,
                              ColorProvider colorProvider) {
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
    }
    
    /**
     * Handles herb patch farming steps.
     */
    public void herbSteps(Graphics2D graphics, Teleport teleport) {
        int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        HerbPatchChecker.PlantState plantState;
        Color leftColor = colorProvider.getLeftClickColorWithAlpha();
        Color useItemColor = colorProvider.getHighlightUseItemWithAlpha();
        
        // Farming guild herb patch uses 4775
        if (currentRegionId == Constants.REGION_FARMING_GUILD) {
            plantState = HerbPatchChecker.checkHerbPatch(client, Constants.VARBIT_HERB_PATCH_FARMING_GUILD);
        }
        // Harmony herb patch uses 4772
        else if (currentRegionId == Constants.REGION_HARMONY) {
            plantState = HerbPatchChecker.checkHerbPatch(client, Constants.VARBIT_HERB_PATCH_HARMONY);
        }
        // Troll Stronghold and Weiss herb patch uses 4771
        else if (currentRegionId == Constants.REGION_TROLL_STRONGHOLD || currentRegionId == Constants.REGION_WEISS) {
            plantState = HerbPatchChecker.checkHerbPatch(client, Constants.VARBIT_HERB_PATCH_TROLL_WEISS);
        }
        // Rest uses 4774
        else {
            plantState = HerbPatchChecker.checkHerbPatch(client, Constants.VARBIT_HERB_PATCH_STANDARD);
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
                    plugin.addTextToInfoBox("Use Compost on patch.");
                    compostHighlighter.highlightCompost(graphics, true, false, false, 1);
                    if (patchStateChecker.patchIsComposted()) {
                        herbPatchDone = true;
                    }
                    break;
                case UNKNOWN:
                    plugin.addTextToInfoBox("UNKNOWN state: Try to do something with the herb patch to change its state.");
                    break;
            }
        }
    }
    
    /**
     * Handles flower patch farming steps.
     */
    public void flowerSteps(Graphics2D graphics, boolean farmLimps) {
        if (farmLimps) {
            int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();
            FlowerPatchChecker.PlantState plantState;
            Color leftColor = colorProvider.getLeftClickColorWithAlpha();
            Color useItemColor = colorProvider.getHighlightUseItemWithAlpha();
            
            if (currentRegionId == Constants.REGION_FARMING_GUILD) {
                plantState = FlowerPatchChecker.checkFlowerPatch(client, Constants.VARBIT_FLOWER_PATCH_FARMING_GUILD);
            } else {
                plantState = FlowerPatchChecker.checkFlowerPatch(client, Constants.VARBIT_FLOWER_PATCH_STANDARD);
            }
            switch (plantState) {
                case HARVESTABLE:
                    plugin.addTextToInfoBox("Harvest Limwurt root.");
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
     * Handles tree patch farming steps.
     */
    public void treeSteps(Graphics2D graphics, Teleport teleport) {
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
                    plugin.addTextToInfoBox("Prune the tree patch patch.");
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
    public void fruitTreeSteps(Graphics2D graphics, Teleport teleport) {
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
}

