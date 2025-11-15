package com.easyfarming.overlays.handlers;

import com.easyfarming.*;
import com.easyfarming.core.Location;
import com.easyfarming.core.Teleport;
import com.easyfarming.overlays.highlighting.*;
import com.easyfarming.overlays.utils.ColorProvider;
import com.easyfarming.overlays.utils.GameObjectHelper;
import com.easyfarming.overlays.utils.WidgetHelper;
import com.easyfarming.utils.Constants;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarClientID;
import net.runelite.api.widgets.Widget;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;
import java.util.Objects;

/**
 * Handles teleport navigation logic for getting to locations and houses.
 */
public class NavigationHandler {
    private final Client client;
    private final EasyFarmingPlugin plugin;
    private final EasyFarmingConfig config;
    private final AreaCheck areaCheck;
    private final TeleportHighlighter teleportHighlighter;
    private final PatchHighlighter patchHighlighter;
    private final ItemHighlighter itemHighlighter;
    private final WidgetHighlighter widgetHighlighter;
    private final GameObjectHighlighter gameObjectHighlighter;
    private final DecorativeObjectHighlighter decorativeObjectHighlighter;
    private final MenuHighlighter menuHighlighter;
    private final WidgetHelper widgetHelper;
    private final GameObjectHelper gameObjectHelper;
    private final ColorProvider colorProvider;
    
    // State tracking
    public int currentTeleportCase = 1;
    public boolean isAtDestination = false;
    
    @Inject
    public NavigationHandler(Client client, EasyFarmingPlugin plugin, EasyFarmingConfig config,
                            AreaCheck areaCheck, TeleportHighlighter teleportHighlighter,
                            PatchHighlighter patchHighlighter, ItemHighlighter itemHighlighter,
                            WidgetHighlighter widgetHighlighter, GameObjectHighlighter gameObjectHighlighter,
                            DecorativeObjectHighlighter decorativeObjectHighlighter, MenuHighlighter menuHighlighter,
                            WidgetHelper widgetHelper, GameObjectHelper gameObjectHelper,
                            ColorProvider colorProvider) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.areaCheck = areaCheck;
        this.teleportHighlighter = teleportHighlighter;
        this.patchHighlighter = patchHighlighter;
        this.itemHighlighter = itemHighlighter;
        this.widgetHighlighter = widgetHighlighter;
        this.gameObjectHighlighter = gameObjectHighlighter;
        this.decorativeObjectHighlighter = decorativeObjectHighlighter;
        this.menuHighlighter = menuHighlighter;
        this.widgetHelper = widgetHelper;
        this.gameObjectHelper = gameObjectHelper;
        this.colorProvider = colorProvider;
    }
    
    /**
     * Checks if player is in their house (has Portal object).
     */
    public void inHouseCheck() {
        if (gameObjectHelper.getGameObjectIdsByName("Portal").contains(4525)) {
            this.currentTeleportCase = 2;
        }
    }
    
    /**
     * Handles navigation to player's house.
     */
    public void gettingToHouse(Graphics2D graphics) {
        EasyFarmingConfig.OptionEnumHouseTele teleportOption = config.enumConfigHouseTele();
        Color leftColor = colorProvider.getLeftClickColorWithAlpha();
        Color rightColor = colorProvider.getRightClickColorWithAlpha();
        
        switch (teleportOption) {
            case Law_air_earth_runes:
                InventoryTabChecker.TabState tabState;
                tabState = InventoryTabChecker.checkTab(client, VarClientID.TOPLEVEL_PANEL);
                switch (tabState) {
                    case INVENTORY:
                    case REST:
                        widgetHighlighter.interfaceOverlay(widgetHelper.getSpellbookTabGroupId(), widgetHelper.getSpellbookTabChildId()).render(graphics);
                        break;
                    case SPELLBOOK:
                        // Highlight the "Teleport to House" spell using correct child ID from widget inspector
                        widgetHighlighter.interfaceOverlay(InterfaceID.MAGIC_SPELLBOOK, 31).render(graphics);
                        inHouseCheck();
                        break;
                }
                break;
            case Teleport_To_House:
                inHouseCheck();
                itemHighlighter.itemHighlight(graphics, ItemID.POH_TABLET_TELEPORTTOHOUSE, leftColor);
                break;
            case Construction_cape:
                inHouseCheck();
                itemHighlighter.itemHighlight(graphics, ItemID.SKILLCAPE_CONSTRUCTION, rightColor);
                break;
            case Construction_cape_t:
                inHouseCheck();
                itemHighlighter.itemHighlight(graphics, ItemID.SKILLCAPE_CONSTRUCTION_TRIMMED, rightColor);
                break;
            case Max_cape:
                inHouseCheck();
                itemHighlighter.itemHighlight(graphics, ItemID.SKILLCAPE_MAX, rightColor);
                break;
        }
    }
    
    /**
     * Determines if player should proceed to farming phase based on location and teleport.
     */
    public boolean shouldProceedToFarming(Location location, Teleport teleport) {
        int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        WorldPoint targetLocation = teleport.getPoint();
        
        // Check if player is in the correct region
        boolean inCorrectRegion = (currentRegionId == teleport.getRegionId());
        
        // Check if player is near the target location (within 20 tiles)
        boolean nearTarget = areaCheck.isPlayerWithinArea(targetLocation, 20);
        
        // Check if player is very close to the farming patch (within 5 tiles)
        boolean nearPatch = areaCheck.isPlayerWithinArea(targetLocation, 5);
        
        // Scenario 1: Player is very close to the patch - proceed to farming regardless of teleport method
        if (nearPatch) {
            return true;
        }
        
        // Scenario 2: Player is in correct region and reasonably close - proceed to farming
        if (inCorrectRegion && nearTarget) {
            return true;
        }
        
        // Scenario 3: Player is in correct region but far from target - might have skipped teleport step
        if (inCorrectRegion && !nearTarget) {
            if (isNearAnyFarmingPatch(location.getName())) {
                return true;
            }
        }
        
        // Scenario 4: Player is in wrong region but very close to target - might have used different teleport
        if (!inCorrectRegion && nearTarget) {
            return true;
        }
        
        // Default: Continue with normal navigation
        return false;
    }
    
    /**
     * Checks if player is near any farming patches of the specified type.
     */
    private boolean isNearAnyFarmingPatch(String locationName) {
        // Define farming patch locations for each area
        switch (locationName) {
            case "Ardougne":
                return areaCheck.isPlayerWithinArea(new WorldPoint(2670, 3374, 0), 10);
            case "Catherby":
                return areaCheck.isPlayerWithinArea(new WorldPoint(2813, 3463, 0), 10);
            case "Falador":
                return areaCheck.isPlayerWithinArea(new WorldPoint(3058, 3307, 0), 10);
            case "Civitas illa Fortis":
                return areaCheck.isPlayerWithinArea(new WorldPoint(1586, 3099, 0), 10);
            case "Farming Guild":
                return areaCheck.isPlayerWithinArea(new WorldPoint(1238, 3726, 0), 15) ||
                       areaCheck.isPlayerWithinArea(new WorldPoint(1232, 3736, 0), 15) ||
                       areaCheck.isPlayerWithinArea(new WorldPoint(1243, 3759, 0), 15);
            case "Brimhaven":
                return areaCheck.isPlayerWithinArea(new WorldPoint(2764, 3212, 0), 10);
            case "Gnome Stronghold":
                return areaCheck.isPlayerWithinArea(new WorldPoint(2436, 3415, 0), 10) ||
                       areaCheck.isPlayerWithinArea(new WorldPoint(2475, 3446, 0), 10);
            case "Lumbridge":
                return areaCheck.isPlayerWithinArea(new WorldPoint(3193, 3231, 0), 10);
            case "Taverley":
                return areaCheck.isPlayerWithinArea(new WorldPoint(2936, 3438, 0), 10);
            case "Varrock":
                return areaCheck.isPlayerWithinArea(new WorldPoint(3229, 3459, 0), 10);
            default:
                return false;
        }
    }
    
    /**
     * Gets the appropriate highlighting based on current situation.
     */
    public void adaptiveHighlighting(Location location, Teleport teleport, Graphics2D graphics,
                                     boolean herbRun, boolean treeRun, boolean fruitTreeRun) {
        int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        WorldPoint targetLocation = teleport.getPoint();
        Color leftColor = colorProvider.getLeftClickColorWithAlpha();
        
        boolean inCorrectRegion = (currentRegionId == teleport.getRegionId());
        boolean nearTarget = areaCheck.isPlayerWithinArea(targetLocation, 20);
        boolean nearPatch = areaCheck.isPlayerWithinArea(targetLocation, 5);
        
        // If player is very close to patch, highlight the patch directly
        if (nearPatch) {
            patchHighlighter.highlightFarmingPatchesForLocation(location.getName(), graphics,
                    herbRun, treeRun, fruitTreeRun, leftColor, leftColor);
            return;
        }
        
        // If player is in correct region but not near target, they might be near a different patch
        if (inCorrectRegion && !nearTarget) {
            if (isNearAnyFarmingPatch(location.getName())) {
                patchHighlighter.highlightFarmingPatchesForLocation(location.getName(), graphics,
                        herbRun, treeRun, fruitTreeRun, leftColor, leftColor);
                return;
            }
        }
        
        // Default to normal teleport highlighting
        teleportHighlighter.highlightTeleportMethod(teleport, graphics);
    }
    
    /**
     * Handles navigation to a specific location.
     */
    public void gettingToLocation(Graphics2D graphics, Location location, boolean herbRun,
                                  boolean treeRun, boolean fruitTreeRun) {
        Teleport teleport = location.getSelectedTeleport();
        boolean locationEnabledBool = false;
        
        if (herbRun) {
            locationEnabledBool = plugin.getHerbLocationEnabled(location.getName());
        } else if (treeRun) {
            locationEnabledBool = plugin.getTreeLocationEnabled(location.getName());
        } else if (fruitTreeRun) {
            locationEnabledBool = plugin.getFruitTreeLocationEnabled(location.getName());
        }
        
        if (locationEnabledBool) {
            if (!isAtDestination) {
                int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();
                
                // Use adaptive detection to determine if we should proceed to farming
                if (shouldProceedToFarming(location, teleport)) {
                    this.currentTeleportCase = 1;
                    isAtDestination = true;
                    if (location.getFarmLimps()) {
                        // This will be handled by the calling code
                    }
                    plugin.addTextToInfoBox(teleport.getDescription());
                } else {
                    // Use adaptive highlighting based on current situation
                    adaptiveHighlighting(location, teleport, graphics, herbRun, treeRun, fruitTreeRun);
                    plugin.addTextToInfoBox(teleport.getDescription());
                    return;
                }
                
                // Handle different teleport categories
                switch (teleport.getCategory()) {
                    case ITEM:
                        handleItemTeleport(graphics, teleport, location, currentRegionId);
                        break;
                    case PORTAL_NEXUS:
                        handlePortalNexusTeleport(graphics, teleport, location, currentRegionId);
                        break;
                    case SPIRIT_TREE:
                        handleSpiritTreeTeleport(graphics, teleport, location, currentRegionId);
                        break;
                    case JEWELLERY_BOX:
                        handleJewelleryBoxTeleport(graphics, teleport, location, currentRegionId);
                        break;
                    case MOUNTED_XERICS:
                        handleMountedXericsTeleport(graphics, teleport, location, currentRegionId);
                        break;
                    case SPELLBOOK:
                        handleSpellbookTeleport(graphics, teleport, currentRegionId);
                        break;
                }
            }
        } else {
            // Location not enabled, skip to next
        }
    }
    
    private void handleItemTeleport(Graphics2D graphics, Teleport teleport, Location location, int currentRegionId) {
        Color leftColor = colorProvider.getLeftClickColorWithAlpha();
        Color rightColor = colorProvider.getRightClickColorWithAlpha();
        
        if (teleport.getInterfaceGroupId() != 0) {
            if (!widgetHelper.isInterfaceOpen(teleport.getInterfaceGroupId(), teleport.getInterfaceChildId())) {
                itemHighlighter.itemHighlight(graphics, teleport.getId(), rightColor);
                if (!teleport.getRightClickOption().equals("")) {
                    menuHighlighter.highlightRightClickOption(graphics, teleport.getRightClickOption());
                }
            } else {
                Widget widget = client.getWidget(teleport.getInterfaceGroupId(), teleport.getInterfaceChildId());
                widgetHighlighter.highlightDynamicComponent(graphics, widget, 1);
            }
            if (currentRegionId == teleport.getRegionId()) {
                this.currentTeleportCase = 1;
                isAtDestination = true;
                if (location.getFarmLimps()) {
                    // This will be handled by the calling code
                }
            }
        } else {
            if (!teleport.getRightClickOption().equals("")) {
                itemHighlighter.itemHighlight(graphics, teleport.getId(), rightColor);
                menuHighlighter.highlightRightClickOption(graphics, teleport.getRightClickOption());
            } else {
                if (plugin.getEasyFarmingOverlay().isTeleportCrystal(teleport.getId())) {
                    itemHighlighter.highlightTeleportCrystal(graphics);
                }
                if (plugin.getEasyFarmingOverlay().isSkillsNecklace(teleport.getId())) {
                    String index = location.getName();
                    List<Integer> skillsNecklaceIds = Constants.SKILLS_NECKLACE_IDS;
                    if (Objects.equals(index, "Ardougne")) {
                        for (int id : skillsNecklaceIds) {
                            itemHighlighter.itemHighlight(graphics, id, rightColor);
                        }
                        Widget widget = client.getWidget(Constants.INTERFACE_SPIRIT_TREE, Constants.INTERFACE_SPIRIT_TREE_CHILD);
                        if (widget != null && !widget.isHidden()) {
                            widgetHighlighter.highlightDynamicComponent(graphics, widget, 0);
                        }
                    }
                    if (Objects.equals(index, "Farming Guild")) {
                        for (int id : skillsNecklaceIds) {
                            itemHighlighter.itemHighlight(graphics, id, rightColor);
                        }
                        Widget widget = client.getWidget(Constants.INTERFACE_SPIRIT_TREE, Constants.INTERFACE_SPIRIT_TREE_CHILD);
                        if (widget != null && !widget.isHidden()) {
                            widgetHighlighter.highlightDynamicComponent(graphics, widget, 5);
                        }
                    }
                } else if (plugin.getEasyFarmingOverlay().isQuetzalWhistle(teleport.getId()) || 
                           plugin.getEasyFarmingOverlay().isRoyalSeedPod(teleport.getId()) ||
                           plugin.getEasyFarmingOverlay().isEctophial(teleport.getId())) {
                    itemHighlighter.itemHighlight(graphics, teleport.getId(), leftColor);
                } else {
                    itemHighlighter.itemHighlight(graphics, teleport.getId(), leftColor);
                }
            }
            if (currentRegionId == teleport.getRegionId()) {
                this.currentTeleportCase = 1;
                isAtDestination = true;
                if (location.getFarmLimps()) {
                    // This will be handled by the calling code
                }
            }
        }
    }
    
    private void handlePortalNexusTeleport(Graphics2D graphics, Teleport teleport, Location location, int currentRegionId) {
        Color leftColor = colorProvider.getLeftClickColorWithAlpha();
        
        switch (this.currentTeleportCase) {
            case 1:
                gettingToHouse(graphics);
                break;
            case 2:
                if (!widgetHelper.isInterfaceOpen(17, 0)) {
                    List<Integer> portalNexusIds = gameObjectHelper.getGameObjectIdsByName("Portal Nexus");
                    for (Integer objectId : portalNexusIds) {
                        gameObjectHighlighter.highlightGameObject(objectId, leftColor).render(graphics);
                    }
                } else {
                    Widget widget = client.getWidget(Constants.INTERFACE_PORTAL_NEXUS, Constants.INTERFACE_PORTAL_NEXUS_CHILD);
                    int index = widgetHelper.getChildIndexPortalNexus(location.getName());
                    widgetHighlighter.highlightDynamicComponent(graphics, widget, index);
                }
                if (currentRegionId == teleport.getRegionId()) {
                    this.currentTeleportCase = 1;
                    isAtDestination = true;
                    if (location.getFarmLimps()) {
                        // This will be handled by the calling code
                    }
                }
                break;
        }
    }
    
    private void handleSpiritTreeTeleport(Graphics2D graphics, Teleport teleport, Location location, int currentRegionId) {
        Color leftColor = colorProvider.getLeftClickColorWithAlpha();
        
        if (!widgetHelper.isInterfaceOpen(187, 3)) {
            List<Integer> spiritTreeIds = Constants.SPIRIT_TREE_IDS;
            for (Integer objectId : spiritTreeIds) {
                gameObjectHighlighter.highlightGameObject(objectId, leftColor).render(graphics);
            }
        } else {
            Widget widget = client.getWidget(Constants.INTERFACE_SPIRIT_TREE, Constants.INTERFACE_SPIRIT_TREE_CHILD);
            switch (location.getName()) {
                case "Gnome Stronghold":
                    widgetHighlighter.highlightDynamicComponent(graphics, widget, widgetHelper.getChildIndexSpiritTree("Gnome Stronghold"));
                    break;
                case "Tree Gnome Village":
                    widgetHighlighter.highlightDynamicComponent(graphics, widget, widgetHelper.getChildIndexSpiritTree("Tree Gnome Village"));
                    break;
                case "Falador":
                    widgetHighlighter.highlightDynamicComponent(graphics, widget, widgetHelper.getChildIndexSpiritTree("Port Sarim"));
                    break;
                case "Kourend":
                    widgetHighlighter.highlightDynamicComponent(graphics, widget, widgetHelper.getChildIndexSpiritTree("Hosidius"));
                    break;
            }
        }
        if (currentRegionId == teleport.getRegionId()) {
            this.currentTeleportCase = 1;
            isAtDestination = true;
            if (location.getFarmLimps()) {
                // This will be handled by the calling code
            }
        }
    }
    
    private void handleJewelleryBoxTeleport(Graphics2D graphics, Teleport teleport, Location location, int currentRegionId) {
        Color leftColor = colorProvider.getLeftClickColorWithAlpha();
        
        switch (this.currentTeleportCase) {
            case 1:
                gettingToHouse(graphics);
                break;
            case 2:
                List<Integer> jewelleryBoxIds = Constants.JEWELLERY_BOX_IDS;
                if (!widgetHelper.isInterfaceOpen(Constants.INTERFACE_JEWELLERY_BOX_OPEN, 0)) {
                    for (int id : jewelleryBoxIds) {
                        gameObjectHighlighter.highlightGameObject(id, leftColor).render(graphics);
                    }
                    gameObjectHighlighter.highlightGameObject(teleport.getId(), leftColor).render(graphics);
                } else {
                    Widget widget = client.getWidget(Constants.INTERFACE_JEWELLERY_BOX_OPEN, Constants.WIDGET_JEWELLERY_BOX_CHILD);
                    widgetHighlighter.highlightDynamicComponent(graphics, widget, 10);
                }
                if (currentRegionId == teleport.getRegionId()) {
                    this.currentTeleportCase = 1;
                    isAtDestination = true;
                    if (location.getFarmLimps()) {
                        // This will be handled by the calling code
                    }
                }
                break;
        }
    }
    
    private void handleMountedXericsTeleport(Graphics2D graphics, Teleport teleport, Location location, int currentRegionId) {
        switch (this.currentTeleportCase) {
            case 1:
                gettingToHouse(graphics);
                break;
            case 2:
                List<Integer> xericsTalismanIds = Constants.XERICS_TALISMAN_IDS;
                Color leftColor = colorProvider.getLeftClickColorWithAlpha();
                if (!widgetHelper.isInterfaceOpen(teleport.getInterfaceGroupId(), teleport.getInterfaceChildId())) {
                    for (int id : xericsTalismanIds) {
                        decorativeObjectHighlighter.highlightDecorativeObject(id, leftColor).render(graphics);
                    }
                } else {
                    Widget widget = client.getWidget(teleport.getInterfaceGroupId(), teleport.getInterfaceChildId());
                    widgetHighlighter.highlightDynamicComponent(graphics, widget, 1);
                    if (currentRegionId == teleport.getRegionId()) {
                        this.currentTeleportCase = 1;
                        isAtDestination = true;
                        if (location.getFarmLimps()) {
                            // This will be handled by the calling code
                        }
                    }
                }
                break;
        }
    }
    
    private void handleSpellbookTeleport(Graphics2D graphics, Teleport teleport, int currentRegionId) {
        InventoryTabChecker.TabState tabState;
        tabState = InventoryTabChecker.checkTab(client, VarClientID.TOPLEVEL_PANEL);
        switch (tabState) {
            case REST:
            case INVENTORY:
                widgetHighlighter.interfaceOverlay(widgetHelper.getSpellbookTabGroupId(), widgetHelper.getSpellbookTabChildId()).render(graphics);
                if (currentRegionId == teleport.getRegionId()) {
                    this.currentTeleportCase = 1;
                    isAtDestination = true;
                }
                break;
            case SPELLBOOK:
                widgetHighlighter.interfaceOverlay(teleport.getInterfaceGroupId(), teleport.getInterfaceChildId()).render(graphics);
                if (currentRegionId == teleport.getRegionId()) {
                    this.currentTeleportCase = 1;
                    isAtDestination = true;
                }
                break;
        }
    }
}

