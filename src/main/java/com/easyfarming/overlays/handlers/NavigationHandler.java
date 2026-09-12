package com.easyfarming.overlays.handlers;

import com.easyfarming.*;
import com.easyfarming.core.Location;
import com.easyfarming.core.Teleport;
import com.easyfarming.overlays.highlighting.*;
import com.easyfarming.overlays.utils.ColorProvider;
import com.easyfarming.overlays.utils.GameObjectHelper;
import com.easyfarming.overlays.utils.WidgetHelper;
import com.easyfarming.utils.Constants;
import com.easyfarming.utils.FertileSoilHelper;
import net.runelite.api.Client;
import net.runelite.api.DecorativeObject;
import net.runelite.api.GameObject;
import net.runelite.api.GroundObject;
import net.runelite.api.Player;
import net.runelite.api.Tile;
import net.runelite.api.WorldView;
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
    private final FarmingTeleportSceneOverlay farmingTeleportSceneOverlay;
    
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
                            ColorProvider colorProvider, FarmingTeleportSceneOverlay farmingTeleportSceneOverlay) {
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
        this.farmingTeleportSceneOverlay = farmingTeleportSceneOverlay;
    }
    
    /**
     * Checks if player is in their house.
     */
    public void inHouseCheck() {
        if (isPlayerInHouse()) {
            this.currentTeleportCase = 2;
        }
    }

    private boolean isPlayerInHouse() {
        return !gameObjectHelper.getGameObjectIdsByName("Portal").isEmpty()
                || !gameObjectHelper.getGameObjectIdsByName("Portal Nexus").isEmpty()
                || !gameObjectHelper.getGameObjectIdsByName("Jewellery Box").isEmpty()
                || !gameObjectHelper.getGameObjectIdsByName("Jewellery box").isEmpty()
                || !gameObjectHighlighter.findGameObjectsByID(Constants.POH_EXIT_PORTAL_OBJECT_ID).isEmpty()
                || hasAnyGameObject(Constants.POH_PORTAL_NEXUS_IDS)
                || hasAnyGameObject(Constants.JEWELLERY_BOX_IDS)
                || hasAnyDecorativeObject(Constants.XERICS_TALISMAN_IDS)
                || liveSceneHasPohObject();
    }

    private boolean hasAnyGameObject(List<Integer> objectIds) {
        for (int objectId : objectIds) {
            if (!gameObjectHighlighter.findGameObjectsByID(objectId).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasAnyDecorativeObject(List<Integer> objectIds) {
        for (int objectId : objectIds) {
            if (!gameObjectHighlighter.findDecorativeObjectsByID(objectId).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean liveSceneHasPohObject() {
        WorldView worldView = getPlayerWorldView();
        if (worldView == null || worldView.getScene() == null) {
            return false;
        }

        Tile[][][] tiles = worldView.getScene().getTiles();
        for (int plane = 0; plane < tiles.length; plane++) {
            for (int x = 0; x < Constants.SCENE_SIZE; x++) {
                for (int y = 0; y < Constants.SCENE_SIZE; y++) {
                    Tile tile = tiles[plane][x][y];
                    if (tile == null) {
                        continue;
                    }

                    for (GameObject gameObject : tile.getGameObjects()) {
                        if (gameObject != null && isPohGameObject(gameObject.getId())) {
                            return true;
                        }
                    }

                    GroundObject groundObject = tile.getGroundObject();
                    if (groundObject != null && isPohGameObject(groundObject.getId())) {
                        return true;
                    }

                    DecorativeObject decorativeObject = tile.getDecorativeObject();
                    if (decorativeObject != null && Constants.XERICS_TALISMAN_IDS.contains(decorativeObject.getId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isPohGameObject(int objectId) {
        return objectId == Constants.POH_EXIT_PORTAL_OBJECT_ID
                || Constants.POH_PORTAL_NEXUS_IDS.contains(objectId)
                || Constants.JEWELLERY_BOX_IDS.contains(objectId);
    }

    private WorldView getPlayerWorldView() {
        Player localPlayer = client.getLocalPlayer();
        if (localPlayer != null && localPlayer.getWorldView() != null) {
            return localPlayer.getWorldView();
        }
        return client.getTopLevelWorldView();
    }
    
    /**
     * Handles navigation to player's house.
     */
    public void gettingToHouse(Graphics2D graphics) {
        EasyFarmingConfig.OptionEnumHouseTele teleportOption = FertileSoilHelper.effectiveHouseTeleport(config);
        Color leftColor = colorProvider.getLeftClickColorWithAlpha();
        Color rightColor = colorProvider.getRightClickColorWithAlpha();

        inHouseCheck();
        if (currentTeleportCase == 2) {
            return;
        }

        plugin.addTextToInfoBox(getHouseTeleportInstruction());
        
        switch (teleportOption) {
            case Law_air_earth_runes:
                InventoryTabChecker.TabState tabState;
                tabState = InventoryTabChecker.checkTab(client, VarClientID.TOPLEVEL_PANEL);
                switch (tabState) {
                    case INVENTORY:
                    case REST:
                        // Highlight the spellbook icon in the tab bar (widget group 161/164, child 6)
                        widgetHighlighter.interfaceOverlay(widgetHelper.getSpellbookIconGroupId(), widgetHelper.getSpellbookIconChildId()).render(graphics);
                        break;
                    case SPELLBOOK:
                        if (FertileSoilHelper.useSpellbookSwap(config)
                                && !widgetHelper.isInterfaceOpen(InterfaceID.MAGIC_SPELLBOOK, Constants.SPELL_CHILD_TELEPORT_TO_HOUSE)) {
                            widgetHighlighter.interfaceOverlay(InterfaceID.MAGIC_SPELLBOOK, Constants.SPELL_CHILD_SPELLBOOK_SWAP).render(graphics);
                        } else {
                            // Highlight the "Teleport to House" spell using correct child ID from widget inspector
                            widgetHighlighter.interfaceOverlay(InterfaceID.MAGIC_SPELLBOOK, Constants.SPELL_CHILD_TELEPORT_TO_HOUSE).render(graphics);
                        }
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
        Player localPlayer = client.getLocalPlayer();
        if (localPlayer == null) {
            return false;
        }
        int currentRegionId = localPlayer.getWorldLocation().getRegionID();
        WorldPoint targetLocation = teleport.getPoint();

        if (teleport != null && "Quetzal_Transport".equals(teleport.getEnumOption())
                && Constants.isCivitasQuetzalRegion(currentRegionId)
                && requiresQuetzalFromCivitas(location.getName())) {
            return false;
        }

        // Check if player is in the correct region
        boolean inCorrectRegion = isInTeleportRegion(location.getName(), teleport.getRegionId(), currentRegionId);
        
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

    private static boolean requiresQuetzalFromCivitas(String locationName) {
        return "Kastori".equals(locationName)
                || "Nemus Retreat".equals(locationName)
                || "Aldarin".equals(locationName);
    }

    private static boolean isInTeleportRegion(String locationName, int teleportRegionId, int currentRegionId) {
        if (currentRegionId == teleportRegionId) {
            return true;
        }
        if ("Kastori".equals(locationName) && Constants.isKastoriRegion(currentRegionId)) {
            return true;
        }
        return false;
    }

    private static boolean hasReachedItemTeleportDestination(String locationName, int teleportRegionId, int currentRegionId) {
        return isInTeleportRegion(locationName, teleportRegionId, currentRegionId);
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
            // Hops locations
            case "Seers Village":
                return areaCheck.isPlayerWithinArea(new WorldPoint(2667, 3526, 0), 10);
            case "Yanille":
                return areaCheck.isPlayerWithinArea(new WorldPoint(2576, 3104, 0), 10);
            case "Entrana":
                return areaCheck.isPlayerWithinArea(new WorldPoint(2811, 3337, 0), 10);
            case "Aldarin":
                return areaCheck.isPlayerWithinArea(new WorldPoint(1365, 2937, 0), 10);
            case "Kastori":
                return areaCheck.isPlayerWithinArea(new WorldPoint(1350, 3057, 0), 10);
            case "Nemus Retreat":
                return areaCheck.isPlayerWithinArea(new WorldPoint(1366, 3321, 0), 10);
            default:
                return false;
        }
    }
    
    /**
     * Gets the appropriate highlighting based on current situation.
     */
    public void adaptiveHighlighting(Location location, Teleport teleport, Graphics2D graphics, String patchType) {
        if (client.getLocalPlayer() == null) {
            return;
        }
        int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        WorldPoint targetLocation = teleport.getPoint();
        Color leftColor = colorProvider.getLeftClickColorWithAlpha();
        
        boolean inCorrectRegion = isInTeleportRegion(location.getName(), teleport.getRegionId(), currentRegionId);
        boolean nearTarget = areaCheck.isPlayerWithinArea(targetLocation, 20);
        boolean nearPatch = areaCheck.isPlayerWithinArea(targetLocation, 5);
        
        // If player is very close to patch, highlight the patch directly
        if (nearPatch) {
            patchHighlighter.highlightFarmingPatchesForLocation(location.getName(), graphics,
                    patchType, leftColor, leftColor);
            return;
        }

        if (teleport != null && "Quetzal_Transport".equals(teleport.getEnumOption())
                && Constants.isCivitasQuetzalRegion(currentRegionId)
                && requiresQuetzalFromCivitas(location.getName())) {
            gameObjectHighlighter.renderGameObjectHighlight(graphics, Constants.QUETZAL_TRANSPORT_OBJECT_ID, leftColor);
            return;
        }
        
        // If player is in correct region but not near target, they might be near a different patch
        if (inCorrectRegion && !nearTarget) {
            if (isNearAnyFarmingPatch(location.getName())) {
                patchHighlighter.highlightFarmingPatchesForLocation(location.getName(), graphics,
                        patchType, leftColor, leftColor);
                return;
            }
        }
        
        // Default to normal teleport highlighting
        if (requiresHouseNavigation(teleport) && currentTeleportCase == 1) {
            inHouseCheck();
            if (currentTeleportCase == 2) {
                handleHouseDestinationTeleport(graphics, teleport, location, currentRegionId);
                return;
            }
            gettingToHouse(graphics);
            return;
        }

        teleportHighlighter.highlightTeleportMethod(teleport, graphics);
    }
    
    /**
     * Handles navigation to a specific location.
     * Caller is responsible for passing only enabled locations.
     */
    public void gettingToLocation(Graphics2D graphics, Location location, String patchType) {
        Teleport teleport = location.getSelectedTeleport();
        if (teleport == null) {
            return;
        }
        if (!isAtDestination) {
            if (client.getLocalPlayer() == null) {
                return;
            }
            int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();

            if (requiresHouseNavigation(teleport)) {
                inHouseCheck();
            }
            
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
                adaptiveHighlighting(location, teleport, graphics, patchType);
                plugin.addTextToInfoBox(getNavigationInstruction(teleport));
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
                case FAIRY_RING:
                    handleFairyRingTeleport(graphics, teleport, location, currentRegionId);
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
                case NONE:
                    if (currentRegionId == teleport.getRegionId()) {
                        this.currentTeleportCase = 1;
                        isAtDestination = true;
                    }
                    break;
            }
        }
    }

    private boolean requiresHouseNavigation(Teleport teleport) {
        if (teleport == null) {
            return false;
        }
        switch (teleport.getCategory()) {
            case PORTAL_NEXUS:
            case JEWELLERY_BOX:
            case MOUNTED_XERICS:
                return true;
            default:
                return false;
        }
    }

    private String getNavigationInstruction(Teleport teleport) {
        if (requiresHouseNavigation(teleport)) {
            inHouseCheck();
        }
        if (requiresHouseNavigation(teleport) && currentTeleportCase == 1) {
            return getHouseTeleportInstruction();
        }
        return teleport.getDescription();
    }

    private void handleHouseDestinationTeleport(Graphics2D graphics, Teleport teleport, Location location, int currentRegionId) {
        switch (teleport.getCategory()) {
            case PORTAL_NEXUS:
                handlePortalNexusTeleport(graphics, teleport, location, currentRegionId);
                break;
            case JEWELLERY_BOX:
                handleJewelleryBoxTeleport(graphics, teleport, location, currentRegionId);
                break;
            case MOUNTED_XERICS:
                handleMountedXericsTeleport(graphics, teleport, location, currentRegionId);
                break;
            default:
                teleportHighlighter.highlightTeleportMethod(teleport, graphics);
                break;
        }
    }

    private String getHouseTeleportInstruction() {
        EasyFarmingConfig.OptionEnumHouseTele teleportOption = FertileSoilHelper.effectiveHouseTeleport(config);
        switch (teleportOption) {
            case Law_air_earth_runes:
                InventoryTabChecker.TabState tabState = InventoryTabChecker.checkTab(client, VarClientID.TOPLEVEL_PANEL);
                switch (tabState) {
                    case INVENTORY:
                    case REST:
                        return "Open spellbook for Teleport to House.";
                    case SPELLBOOK:
                        if (FertileSoilHelper.useSpellbookSwap(config)
                                && !widgetHelper.isInterfaceOpen(InterfaceID.MAGIC_SPELLBOOK, Constants.SPELL_CHILD_TELEPORT_TO_HOUSE)) {
                            return "Cast Spellbook Swap for Teleport to House.";
                        }
                        return "Cast Teleport to House.";
                    default:
                        return "Cast Teleport to House.";
                }
            case Teleport_To_House:
                return "Use Teleport to House tablet.";
            case Construction_cape:
                return "Use Construction cape to teleport home.";
            case Construction_cape_t:
                return "Use trimmed Construction cape to teleport home.";
            case Max_cape:
                return "Use Max cape to teleport home.";
            default:
                return "Teleport to your house.";
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
            if (hasReachedItemTeleportDestination(location.getName(), teleport.getRegionId(), currentRegionId)) {
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
                } else if (plugin.getEasyFarmingOverlay().isNecklaceOfPassage(teleport.getId())) {
                    if (Objects.equals(location.getName(), "Gnome Stronghold")) {
                        for (int id : Constants.NECKLACE_OF_PASSAGE_IDS) {
                            itemHighlighter.itemHighlight(graphics, id, rightColor);
                        }
                        Widget widget = client.getWidget(Constants.INTERFACE_SPIRIT_TREE, Constants.INTERFACE_SPIRIT_TREE_CHILD);
                        if (widget != null && !widget.isHidden()) {
                            int outpostIndex = widgetHelper.getChildIndexSpiritTree("The Outpost");
                            if (outpostIndex >= 0) {
                                widgetHighlighter.highlightDynamicComponent(graphics, widget, outpostIndex);
                            }
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
            if (hasReachedItemTeleportDestination(location.getName(), teleport.getRegionId(), currentRegionId)) {
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
                    gameObjectHighlighter.renderGameObjectHighlights(graphics, portalNexusIds, leftColor);
                    gameObjectHighlighter.renderGameObjectHighlights(graphics, Constants.POH_PORTAL_NEXUS_IDS, leftColor);
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
            farmingTeleportSceneOverlay.requestSpiritTreeHighlight(leftColor);
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
                case "Farming Guild":
                    widgetHighlighter.highlightDynamicComponent(graphics, widget, widgetHelper.getChildIndexSpiritTree("Farming Guild"));
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
                    gameObjectHighlighter.renderGameObjectHighlights(graphics, jewelleryBoxIds, leftColor);
                    gameObjectHighlighter.renderGameObjectHighlight(graphics, teleport.getId(), leftColor);
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
    
    private void handleFairyRingTeleport(Graphics2D graphics, Teleport teleport, Location location, int currentRegionId) {
        Color leftColor = colorProvider.getLeftClickColorWithAlpha();
        
        gameObjectHighlighter.renderGameObjectHighlight(graphics, Constants.FAIRY_RING_OBJECT_ID, leftColor);
        
        if (currentRegionId == teleport.getRegionId()) {
            this.currentTeleportCase = 1;
            isAtDestination = true;
        }
    }
    
    private void handleSpellbookTeleport(Graphics2D graphics, Teleport teleport, int currentRegionId) {
        InventoryTabChecker.TabState tabState;
        tabState = InventoryTabChecker.checkTab(client, VarClientID.TOPLEVEL_PANEL);
        switch (tabState) {
            case REST:
            case INVENTORY:
                // Highlight the spellbook icon in the tab bar (widget group 161/164, child 6)
                widgetHighlighter.interfaceOverlay(widgetHelper.getSpellbookIconGroupId(), widgetHelper.getSpellbookIconChildId()).render(graphics);
                if (currentRegionId == teleport.getRegionId()) {
                    this.currentTeleportCase = 1;
                    isAtDestination = true;
                }
                break;
            case SPELLBOOK:
                if (FertileSoilHelper.useSpellbookSwap(config)
                        && !widgetHelper.isInterfaceOpen(teleport.getInterfaceGroupId(), teleport.getInterfaceChildId())) {
                    widgetHighlighter.interfaceOverlay(InterfaceID.MAGIC_SPELLBOOK, Constants.SPELL_CHILD_SPELLBOOK_SWAP).render(graphics);
                } else {
                    widgetHighlighter.interfaceOverlay(teleport.getInterfaceGroupId(), teleport.getInterfaceChildId()).render(graphics);
                }
                if (currentRegionId == teleport.getRegionId()) {
                    this.currentTeleportCase = 1;
                    isAtDestination = true;
                }
                break;
        }
    }
}

