package com.easyfarming;

import java.awt.*;
import javax.inject.Inject;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarClientID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.Tile;

import java.awt.Color;
import java.awt.Shape;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.*;
import java.util.List;
import com.easyfarming.core.Teleport;
import com.easyfarming.utils.Constants;


public class FarmingTeleportOverlay extends Overlay {
    private final Client client;
    private final EasyFarmingPlugin plugin;
    private boolean clicked = false;
    @Inject
    private EasyFarmingConfig config;
    @Inject
    private EasyFarmingOverlay farmingHelperOverlay;
    @Inject
    private EasyFarmingOverlayInfoBox farmingHelperOverlayInfoBox;
    @Inject
    private AreaCheck areaCheck;
    @Inject
    private com.easyfarming.overlays.handlers.FarmingStepHandler farmingStepHandler;
    @Inject
    private com.easyfarming.overlays.highlighting.PatchHighlighter patchHighlighter;
    @Inject
    private com.easyfarming.overlays.highlighting.ItemHighlighter itemHighlighter;
    @Inject
    private com.easyfarming.overlays.highlighting.WidgetHighlighter widgetHighlighter;
    @Inject
    private com.easyfarming.overlays.highlighting.MenuHighlighter menuHighlighter;
    @Inject
    private com.easyfarming.overlays.highlighting.GameObjectHighlighter gameObjectHighlighter;
    @Inject
    private com.easyfarming.overlays.highlighting.DecorativeObjectHighlighter decorativeObjectHighlighter;
    @Inject
    private com.easyfarming.overlays.utils.ColorProvider colorProvider;

    private final PanelComponent panelComponent = new PanelComponent();
    public boolean patchCleared = false;

    private Color leftClickColorWithAlpha;
    private Color rightClickColorWithAlpha;
    private Color highlightUseItemWithAlpha;

    public void updateColors() {
        leftClickColorWithAlpha = new Color(
                config.highlightLeftClickColor().getRed(),
                config.highlightLeftClickColor().getGreen(),
                config.highlightLeftClickColor().getBlue(),
                config.highlightAlpha()
        );
        rightClickColorWithAlpha = new Color(
                config.highlightRightClickColor().getRed(),
                config.highlightRightClickColor().getGreen(),
                config.highlightRightClickColor().getBlue(),
                config.highlightAlpha()
        );
        highlightUseItemWithAlpha = new Color(
                config.highlightUseItemColor().getRed(),
                config.highlightUseItemColor().getGreen(),
                config.highlightUseItemColor().getBlue(),
                config.highlightAlpha()
        );
    }


    public Map<String, Boolean> herbConfigMap = new HashMap<>();


    private int previousRegionId;
    public int inventoryTabValue = 0;




    @Inject
    public FarmingTeleportOverlay(EasyFarmingPlugin plugin, Client client, AreaCheck areaCheck) {
        this.areaCheck = areaCheck;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        this.plugin = plugin;
        this.client = client;
    }


    public int getChildIndex(String searchText, Widget parentWidget)
    {
        if (parentWidget == null) {
            return -1;
        }

        Widget[] children = parentWidget.getChildren();

        if (children == null) {
            return -1;
        }

        for (int index = 0; index < children.length; index++) {
            Widget child = children[index];
            String text = child.getText();

            if (text != null) {
                int colonIndex = text.indexOf(':');

                if (colonIndex != -1 && colonIndex + 1 < text.length()) {
                    String textAfterColon = text.substring(colonIndex + 1).trim();

                    if (textAfterColon.equals(searchText)) {
                        return index;
                    }
                }
            }
        }

        return -1; // Return -1 if the specified text is not found
    }

    public int getChildIndexPortalNexus(String searchText)
    {
        return getChildIndex(
            searchText,
            client.getWidget(17, 12)
        );
    }

    public int getChildIndexSpiritTree(String searchText)
    {
        return getChildIndex(
            searchText,
            client.getWidget(187, 3)
        );
    }





    private boolean isInterfaceOpen(int groupId, int childId) {
        Widget widget = client.getWidget(groupId, childId);
        return widget != null && !widget.isHidden();
    }

    /**
     * Dynamically detects the correct spellbook tab interface ID based on the current client mode
     * @return The child ID for the magic spellbook tab, or -1 if not found
     */
    private int getSpellbookTabChildId() {
        // Try resizable classic mode first (161.65)
        if (isInterfaceOpen(161, 65)) {
            return 65;
        }
        // Try pre-EOC mode (164.58)
        if (isInterfaceOpen(164, 58)) {
            return 58;
        }
        // Try other possible variations
        if (isInterfaceOpen(161, 58)) {
            return 58;
        }
        if (isInterfaceOpen(164, 65)) {
            return 65;
        }
        // Default fallback to resizable classic mode
        return 65;
    }

    /**
     * Gets the correct group ID for the spellbook tab based on the current client mode
     * @return The group ID for the spellbook tab
     */
    private int getSpellbookTabGroupId() {
        // Try resizable classic mode first (161.65)
        if (isInterfaceOpen(161, 65)) {
            return 161;
        }
        // Try pre-EOC mode (164.58)
        if (isInterfaceOpen(164, 58)) {
            return 164;
        }
        // Try other possible variations
        if (isInterfaceOpen(161, 58)) {
            return 161;
        }
        if (isInterfaceOpen(164, 65)) {
            return 164;
        }
        // Default fallback to resizable classic mode
        return 161;
    }
    
    /**
     * Gets the widget group ID for the spellbook icon in the tab bar.
     * Detects whether we're in fixed classic mode (548) or resizable mode (161/164).
     * @return The group ID for the tab bar containing the spellbook icon
     */
    private int getSpellbookIconGroupId() {
        // Check for fixed classic mode first (548)
        if (isInterfaceOpen(548, 63)) {
            return 548;
        }
        // Check for resizable mode (161 or 164)
        if (isInterfaceOpen(161, 65) || isInterfaceOpen(161, 58)) {
            return 161;
        }
        if (isInterfaceOpen(164, 58) || isInterfaceOpen(164, 65)) {
            return 164;
        }
        // Default to resizable mode
        return 161;
    }
    
    /**
     * Gets the child ID for the spellbook icon in the tab bar.
     * Based on screenshots:
     * - Resizable classic mode (161): child ID 72 (ICON6)
     * - Resizable modern mode (164): child ID 65 (ICON6)
     * - Fixed classic mode (548): child ID 77 (ICON6)
     * @return The child ID for the spellbook icon
     */
    private int getSpellbookIconChildId() {
        int groupId = getSpellbookIconGroupId();
        
        // Fixed classic mode uses widget 548, child 77
        if (groupId == 548) {
            return 77;
        }
        
        // Resizable modern mode (164) uses child ID 65
        if (groupId == 164) {
            return 65;
        }
        
        // Resizable classic mode (161) uses child ID 72
        return 72;
    }

    /**
     * Enhanced location detection that handles edge cases and adapts to player's current situation
     * @param location The target location
     * @param teleport The selected teleport method
     * @return true if player should proceed to farming phase, false if still navigating
     */
    private boolean shouldProceedToFarming(Location location, Teleport teleport) {
        int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        WorldPoint targetLocation = teleport.getPoint();
        
        // Special handling for Quetzal_Transport to Aldarin
        // The teleport point is in Civitas, but we need to check if player is in Aldarin near the patch
        if (teleport != null && "Quetzal_Transport".equals(teleport.getEnumOption()) && 
            "Aldarin".equals(location.getName())) {
            // If player is in Aldarin region (5421), check proximity to Aldarin patch point
            if (currentRegionId == 5421) {
                WorldPoint aldarinPatchPoint = new WorldPoint(1365, 2939, 0);
                boolean nearAldarinPatch = areaCheck.isPlayerWithinArea(aldarinPatchPoint, 20);
                if (nearAldarinPatch) {
                    return true;
                }
            }
        }
        
        // Special handling for Entrana - accepts both region 11060 and 11316
        if ("Entrana".equals(location.getName())) {
            // Entrana spans multiple regions (11060 and 11316)
            boolean inEntranaRegion = (currentRegionId == 11060 || currentRegionId == 11316);
            if (inEntranaRegion) {
                WorldPoint entranaPatchPoint = getHopsPatchPointForLocation("Entrana");
                if (entranaPatchPoint != null) {
                    boolean nearEntranaPatch = areaCheck.isPlayerWithinArea(entranaPatchPoint, 20);
                    if (nearEntranaPatch) {
                        return true;
                    }
                }
            }
        }
        
        // Check if player is in the correct region
        boolean inCorrectRegion = (currentRegionId == teleport.getRegionId());
        
        // Check if player is near the target location (within 20 tiles)
        boolean nearTarget = areaCheck.isPlayerWithinArea(targetLocation, 20);
        
        // Check if player is very close to the farming patch (within 5 tiles)
        boolean nearPatch = areaCheck.isPlayerWithinArea(targetLocation, 5);
        
        // Adaptive logic for different scenarios:
        
        // Scenario 1: Player is very close to the patch - proceed to farming regardless of teleport method
        if (nearPatch) {
            return true;
        }
        
        // Scenario 2: Player is in correct region and reasonably close - proceed to farming
        if (inCorrectRegion && nearTarget) {
            return true;
        }
        
        // Scenario 3: Player is in correct region but far from target - might have skipped teleport step
        // Check if there are any farming patches nearby that match this location type
        if (inCorrectRegion && !nearTarget) {
            // Check if player is near any farming patches of the same type
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
     * Gets the herb patch WorldPoint for a location by name.
     * @param locationName The name of the location
     * @return WorldPoint of the herb patch, or null if location not found
     */
    private WorldPoint getHerbPatchPointForLocation(String locationName) {
        if (locationName == null) {
            return null;
        }
        
        // Use hardcoded coordinates to ensure we get the correct herb patch point
        // These match the coordinates in the LocationData classes
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
    
    /**
     * Gets the tree patch WorldPoint for a location by name.
     * @param locationName The name of the location
     * @return WorldPoint of the tree patch, or null if location not found
     */
    private WorldPoint getTreePatchPointForLocation(String locationName) {
        if (locationName == null) {
            return null;
        }
        
        // Use hardcoded coordinates to ensure we get the correct tree patch point
        // These match the coordinates in the LocationData classes
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
    
    /**
     * Gets the fruit tree patch WorldPoint for a location by name.
     * @param locationName The name of the location
     * @return WorldPoint of the fruit tree patch, or null if location not found
     */
    private WorldPoint getFruitTreePatchPointForLocation(String locationName) {
        if (locationName == null) {
            return null;
        }
        
        // Use hardcoded coordinates to ensure we get the correct fruit tree patch point
        // These match the coordinates in the LocationData classes
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
    
    /**
     * Gets the hops patch WorldPoint for a location by name.
     * @param locationName The name of the location
     * @return WorldPoint of the hops patch, or null if location not found
     */
    private WorldPoint getHopsPatchPointForLocation(String locationName) {
        if (locationName == null) {
            return null;
        }
        
        // Use hardcoded coordinates to ensure we get the correct hops patch point
        // These match the coordinates in the LocationData classes
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
     * Sets hint arrow to an NPC location using a two-step approach:
     * 1. Points to a WorldPoint near the NPC when far away
     * 2. Switches to pointing at the NPC when within range and loaded
     * @param npcName The name of the NPC
     * @param npcLocation The WorldPoint near where the NPC is located
     * @param switchDistance The distance at which to switch from WorldPoint to NPC (default 15 tiles)
     */
    private void setHintArrowToNPCLocation(String npcName, WorldPoint npcLocation, int switchDistance) {
        if (client == null || npcName == null || npcLocation == null) {
            return;
        }
        
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        int distanceToLocation = playerLocation.distanceTo(npcLocation);
        
        // Try to find the NPC
        NPC targetNpc = null;
        try {
            IndexedObjectSet<? extends NPC> npcs = client.getTopLevelWorldView().npcs();
            if (npcs != null) {
                for (NPC npc : npcs) {
                    if (npc != null && npc.getName() != null && npc.getName().equals(npcName)) {
                        targetNpc = npc;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            // Silently handle any exceptions
        }
        
        // If NPC is found and player is within switch distance, point to NPC
        if (targetNpc != null && distanceToLocation <= switchDistance) {
            client.setHintArrow(targetNpc);
        } else {
            // Otherwise, point to the WorldPoint near the NPC
            client.setHintArrow(npcLocation);
        }
    }
    
    /**
     * Sets hint arrow to an NPC location using a two-step approach (with default 15 tile switch distance):
     * 1. Points to a WorldPoint near the NPC when far away
     * 2. Switches to pointing at the NPC when within range and loaded
     * @param npcName The name of the NPC
     * @param npcLocation The WorldPoint near where the NPC is located
     */
    private void setHintArrowToNPCLocation(String npcName, WorldPoint npcLocation) {
        setHintArrowToNPCLocation(npcName, npcLocation, 15);
    }
    
    /**
     * Sets hint arrow to an NPC location by ID using a two-step approach:
     * 1. Points to a WorldPoint near the NPC when far away
     * 2. Switches to pointing at the NPC when within range and loaded
     * @param npcId The ID of the NPC
     * @param npcLocation The WorldPoint near where the NPC is located
     * @param switchDistance The distance at which to switch from WorldPoint to NPC (default 15 tiles)
     */
    private void setHintArrowToNPCLocationById(int npcId, WorldPoint npcLocation, int switchDistance) {
        if (client == null || npcLocation == null) {
            return;
        }
        
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        int distanceToLocation = playerLocation.distanceTo(npcLocation);
        
        // Try to find the NPC
        NPC targetNpc = null;
        try {
            IndexedObjectSet<? extends NPC> npcs = client.getTopLevelWorldView().npcs();
            if (npcs != null) {
                for (NPC npc : npcs) {
                    if (npc != null) {
                        // Check both getId() and getComposition().getId() as NPC IDs can vary
                        int npcCompositionId = npc.getId();
                        if (npcCompositionId == npcId) {
                            targetNpc = npc;
                            break;
                        }
                        // Also check the composition ID in case it's different
                        if (npc.getComposition() != null) {
                            int compositionId = npc.getComposition().getId();
                            if (compositionId == npcId) {
                                targetNpc = npc;
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Silently handle any exceptions
        }
        
        // If NPC is found and player is within switch distance, point to NPC
        if (targetNpc != null && distanceToLocation <= switchDistance) {
            client.setHintArrow(targetNpc);
        } else {
            // Otherwise, point to the WorldPoint near the NPC
            client.setHintArrow(npcLocation);
        }
    }
    
    /**
     * Sets hint arrow to an NPC location by ID using a two-step approach (with default 15 tile switch distance):
     * 1. Points to a WorldPoint near the NPC when far away
     * 2. Switches to pointing at the NPC when within range and loaded
     * @param npcId The ID of the NPC
     * @param npcLocation The WorldPoint near where the NPC is located
     */
    private void setHintArrowToNPCLocationById(int npcId, WorldPoint npcLocation) {
        setHintArrowToNPCLocationById(npcId, npcLocation, 15);
    }
    
    /**
     * Gets the WorldPoint location for a specific NPC by name.
     * @param npcName The name of the NPC
     * @return WorldPoint near the NPC location, or null if not found
     */
    private WorldPoint getNPCLocation(String npcName) {
        if (npcName == null) {
            return null;
        }
        
        // NPC locations - these should be near where the NPCs spawn
        switch (npcName) {
            case "Captain Barnaby":
                // Captain Barnaby is at Ardougne docks, near the boat to Brimhaven
                // TODO: Verify exact coordinates
                return new WorldPoint(2675, 3265, 0);
            default:
                return null;
        }
    }
    
    /**
     * Gets the WorldPoint location for a specific NPC by ID.
     * @param npcId The ID of the NPC
     * @return WorldPoint near the NPC location, or null if not found
     */
    private WorldPoint getNPCLocationById(int npcId) {
        switch (npcId) {
            case 1165: // Entrana monk at Port Sarim
                // Entrana monks are at Port Sarim docks, near the boat to Entrana
                // TODO: Verify exact coordinates
                return new WorldPoint(3042, 3235, 0);
            default:
                return null;
        }
    }
    
    /**
     * Checks if player is near any farming patches of the specified type
     * @param locationName The name of the location to check for
     * @return true if player is near any farming patch of this type
     */
    private boolean isNearAnyFarmingPatch(String locationName) {
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        
        // Define farming patch locations for each area
        switch (locationName) {
            case "Ardougne":
                // Check if near Ardougne herb patch
                return areaCheck.isPlayerWithinArea(new WorldPoint(2670, 3374, 0), 10);
            case "Catherby":
                // Check if near Catherby herb patch
                return areaCheck.isPlayerWithinArea(new WorldPoint(2813, 3463, 0), 10);
            case "Falador":
                // Check if near Falador herb patch
                return areaCheck.isPlayerWithinArea(new WorldPoint(3058, 3307, 0), 10);
            case "Civitas illa Fortis":
                // Check if near Civitas herb patch
                return areaCheck.isPlayerWithinArea(new WorldPoint(1586, 3099, 0), 10);
            case "Farming Guild":
                // Check if near Farming Guild patches
                return areaCheck.isPlayerWithinArea(new WorldPoint(1238, 3726, 0), 15) ||
                       areaCheck.isPlayerWithinArea(new WorldPoint(1232, 3736, 0), 15) ||
                       areaCheck.isPlayerWithinArea(new WorldPoint(1243, 3759, 0), 15);
            case "Brimhaven":
                // Check if near Brimhaven fruit tree patch
                return areaCheck.isPlayerWithinArea(new WorldPoint(2764, 3212, 0), 10);
            case "Gnome Stronghold":
                // Check if near Gnome Stronghold patches
                return areaCheck.isPlayerWithinArea(new WorldPoint(2436, 3415, 0), 10) ||
                       areaCheck.isPlayerWithinArea(new WorldPoint(2475, 3446, 0), 10);
            case "Lumbridge":
                // Check if near Lumbridge tree patch
                return areaCheck.isPlayerWithinArea(new WorldPoint(3193, 3231, 0), 10);
            case "Taverley":
                // Check if near Taverley tree patch
                return areaCheck.isPlayerWithinArea(new WorldPoint(2936, 3438, 0), 10);
            case "Varrock":
                // Check if near Varrock tree patch
                return areaCheck.isPlayerWithinArea(new WorldPoint(3229, 3459, 0), 10);
            default:
                return false;
        }
    }
    
    /**
     * Gets the appropriate highlighting based on current situation
     * @param location The target location
     * @param teleport The selected teleport method
     * @param graphics Graphics context for highlighting
     */
    private void adaptiveHighlighting(Location location, Teleport teleport, Graphics2D graphics) {
        int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        WorldPoint targetLocation = teleport.getPoint();
        
        boolean inCorrectRegion = (currentRegionId == teleport.getRegionId());
        boolean nearTarget = areaCheck.isPlayerWithinArea(targetLocation, 20);
        boolean nearPatch = areaCheck.isPlayerWithinArea(targetLocation, 5);
        
        // If player is very close to patch, highlight the patch directly
        if (nearPatch) {
            highlightFarmingPatchesForLocation(location, graphics);
            return;
        }
        
        // If player is in correct region but not near target, they might be near a different patch
        if (inCorrectRegion && !nearTarget) {
            if (isNearAnyFarmingPatch(location.getName())) {
                highlightFarmingPatchesForLocation(location, graphics);
                return;
            }
        }
        
        // Default to normal teleport highlighting
        highlightTeleportMethod(teleport, graphics);
    }
    
    /**
     * Highlights farming patches for a specific location
     * @param location The location object (to check farmLimps and run type)
     * @param graphics Graphics context for highlighting
     */
    private void highlightFarmingPatchesForLocation(Location location, Graphics2D graphics) {
        Color leftColor = colorProvider.getLeftClickColorWithAlpha();
        String locationName = location.getName();
        
        switch (locationName) {
            case "Ardougne":
            case "Weiss":
            case "Civitas illa Fortis":
                patchHighlighter.highlightHerbPatches(graphics, leftColor);
                // Also highlight flower patches if this location supports limpwurt and it's a herb run
                if (herbRun && location.getFarmLimps() && config.generalLimpwurt()) {
                    patchHighlighter.highlightFlowerPatches(graphics, leftColor);
                }
                // Allotment patches are highlighted in allotmentSteps() based on state detection
                // No unconditional highlighting here - only when state is detected
                // highlightHerbPatches(graphics, leftClickColorWithAlpha);
                break;
            case "Catherby":
                // Catherby has both herb and fruit tree patches
                patchHighlighter.highlightHerbPatches(graphics, leftColor);
                patchHighlighter.highlightFruitTreePatches(graphics, leftColor);
                // highlightHerbPatches(graphics, leftClickColorWithAlpha);
                // highlightFruitTreePatches(graphics, leftClickColorWithAlpha);
                break;
            case "Falador":
                // Falador has both herb and tree patches
                patchHighlighter.highlightHerbPatches(graphics, leftColor);
                patchHighlighter.highlightTreePatches(graphics, leftColor);
                // highlightHerbPatches(graphics, leftClickColorWithAlpha);
                // highlightTreePatches(graphics, leftClickColorWithAlpha);
                break;
            case "Farming Guild":
                // Farming Guild has all patch types
                patchHighlighter.highlightHerbPatches(graphics, leftColor);
                patchHighlighter.highlightTreePatches(graphics, leftColor);
                patchHighlighter.highlightFruitTreePatches(graphics, leftColor);
                // highlightHerbPatches(graphics, leftClickColorWithAlpha);
                // highlightTreePatches(graphics, leftClickColorWithAlpha);
                // highlightFruitTreePatches(graphics, leftClickColorWithAlpha);
                break;
            case "Lumbridge":
            case "Taverley":
            case "Varrock":
                patchHighlighter.highlightTreePatches(graphics, leftColor);
                // highlightTreePatches(graphics, leftClickColorWithAlpha);
                break;
            case "Brimhaven":
            case "Gnome Stronghold":
            case "Lletya":
            case "Tree Gnome Village":
                patchHighlighter.highlightFruitTreePatches(graphics, leftColor);
                // highlightFruitTreePatches(graphics, leftClickColorWithAlpha);
                break;
        }
    }
    
    /**
     * Highlights the appropriate teleport method based on category
     * @param teleport The teleport method to highlight
     * @param graphics Graphics context for highlighting
     */
    private void highlightTeleportMethod(Teleport teleport, Graphics2D graphics) {
        Color leftColor = colorProvider.getLeftClickColorWithAlpha();
        Color rightColor = colorProvider.getRightClickColorWithAlpha();
        switch (teleport.getCategory()) {
            case ITEM:
                // Check if it's a Quetzal whistle, Royal seed pod, or Ectophial (left-click teleport)
                if(plugin.getEasyFarmingOverlay().isQuetzalWhistle(teleport.getId()) || 
                   plugin.getEasyFarmingOverlay().isRoyalSeedPod(teleport.getId()) ||
                   plugin.getEasyFarmingOverlay().isEctophial(teleport.getId())) {
                    itemHighlighter.itemHighlight(graphics, teleport.getId(), leftColor);
                    // itemHighlight(graphics, teleport.getId(), leftClickColorWithAlpha);
                } else if (plugin.getEasyFarmingOverlay().isTeleportCrystal(teleport.getId())) {
                    // Teleport crystals need special handling - highlight all crystal variants
                    itemHighlighter.highlightTeleportCrystal(graphics);
                } else {
                    itemHighlighter.itemHighlight(graphics, teleport.getId(), rightColor);
                    // itemHighlight(graphics, teleport.getId(), rightClickColorWithAlpha);
                    if (!teleport.getRightClickOption().equals("")) {
                        menuHighlighter.highlightRightClickOption(graphics, teleport.getRightClickOption());
                        // highlightRightClickOption(graphics, teleport.getRightClickOption());
                    }
                }
                break;
            case SPELLBOOK:
                InventoryTabChecker.TabState tabState = InventoryTabChecker.checkTab(client, VarClientID.TOPLEVEL_PANEL);
                if (tabState == InventoryTabChecker.TabState.SPELLBOOK) {
                    widgetHighlighter.interfaceOverlay(teleport.getInterfaceGroupId(), teleport.getInterfaceChildId()).render(graphics);
                    // interfaceOverlay(teleport.getInterfaceGroupId(), teleport.getInterfaceChildId()).render(graphics);
                } else {
                    // Highlight the spellbook icon in the tab bar (widget group 161/164, child 6)
                    widgetHighlighter.interfaceOverlay(getSpellbookIconGroupId(), getSpellbookIconChildId()).render(graphics);
                    // interfaceOverlay(getSpellbookIconGroupId(), getSpellbookIconChildId()).render(graphics);
                }
                break;
            case PORTAL_NEXUS:
                if (!isInterfaceOpen(17, 0)) {
                    List<Integer> portalNexusIds = getGameObjectIdsByName("Portal Nexus");
                    for (Integer objectId : portalNexusIds) {
                        gameObjectHighlighter.highlightGameObject(objectId, leftColor).render(graphics);
                        // gameObjectOverlay(objectId, leftClickColorWithAlpha).render(graphics);
                    }
                } else {
                    Widget widget = client.getWidget(17, 13);
                    int index = getChildIndexPortalNexus(teleport.getPoint().toString());
                    widgetHighlighter.highlightDynamicComponent(graphics, widget, index);
                    // highlightDynamicComponent(graphics, widget, index);
                }
                break;
            case SPIRIT_TREE:
                if (!isInterfaceOpen(187, 3)) {
                    List<Integer> spiritTreeIds = Arrays.asList(1293, 1294, 1295, 8355, 29227, 29229, 37329, 40778);
                    for (Integer objectId : spiritTreeIds) {
                        gameObjectHighlighter.highlightGameObject(objectId, leftColor).render(graphics);
                        // gameObjectOverlay(objectId, leftClickColorWithAlpha).render(graphics);
                    }
                } else {
                    Widget widget = client.getWidget(187, 3);
                    int index = getChildIndexSpiritTree(teleport.getPoint().toString());
                    widgetHighlighter.highlightDynamicComponent(graphics, widget, index);
                    // highlightDynamicComponent(graphics, widget, index);
                }
                break;
            case JEWELLERY_BOX:
                if (!isInterfaceOpen(29155, 0)) {
                    List<Integer> jewelleryBoxIds = getGameObjectIdsByName("Jewellery Box");
                    for (Integer objectId : jewelleryBoxIds) {
                        gameObjectHighlighter.highlightGameObject(objectId, leftColor).render(graphics);
                        // gameObjectOverlay(objectId, leftClickColorWithAlpha).render(graphics);
                    }
                } else {
                    Widget widget = client.getWidget(29155, 0);
                    widgetHighlighter.highlightDynamicComponent(graphics, widget, 0);
                    // highlightDynamicComponent(graphics, widget, 0);
                }
                break;
        }
    }



    private List<Integer> getGameObjectIdsByName(String name) {
        List<Integer> foundObjectIds = new ArrayList<>();
        WorldView top_wv = client.getTopLevelWorldView();
        Scene scene = top_wv.getScene();
        Tile[][][] tiles = scene.getTiles();

        for (int x = 0; x < Constants.SCENE_SIZE; x++) {
            for (int y = 0; y < Constants.SCENE_SIZE; y++) {
                Tile tile = tiles[top_wv.getPlane()][x][y];
                if (tile == null) {
                    continue;
                }

                for (GameObject gameObject : tile.getGameObjects()) {
                    if (gameObject != null) {
                        ObjectComposition objectComposition = client.getObjectDefinition(gameObject.getId());
                        if (objectComposition != null && objectComposition.getName().equals(name)) {
                            foundObjectIds.add(gameObject.getId());
                        }
                    }
                }
            }
        }

        return foundObjectIds;
    }

    public void inHouseCheck() {
        if(getGameObjectIdsByName("Portal").contains(4525))
        {
            this.currentTeleportCase = 2;
        }
    }

    public void gettingToHouse(Graphics2D graphics) {
        EasyFarmingConfig.OptionEnumHouseTele teleportOption = config.enumConfigHouseTele();
        switch (teleportOption) {
            case Law_air_earth_runes:
                InventoryTabChecker.TabState tabState;
                tabState = InventoryTabChecker.checkTab(client, VarClientID.TOPLEVEL_PANEL);
                switch (tabState) {
                            case INVENTORY:
                            case REST:
                                // Highlight the spellbook icon in the tab bar (widget group 161/164, child 6)
                                widgetHighlighter.interfaceOverlay(getSpellbookIconGroupId(), getSpellbookIconChildId()).render(graphics);
                                // interfaceOverlay(getSpellbookIconGroupId(), getSpellbookIconChildId()).render(graphics);
                                break;
                    case SPELLBOOK:
                        // Highlight the "Teleport to House" spell using correct child ID from widget inspector
                        widgetHighlighter.interfaceOverlay(InterfaceID.MAGIC_SPELLBOOK, 31).render(graphics);
                        // interfaceOverlay(InterfaceID.MAGIC_SPELLBOOK, 31).render(graphics);
                        inHouseCheck();
                        break;
                }
                break;
            case Teleport_To_House:
                inHouseCheck();
                Color leftColor = colorProvider.getLeftClickColorWithAlpha();
                itemHighlighter.itemHighlight(graphics, ItemID.POH_TABLET_TELEPORTTOHOUSE, leftColor);
                // itemHighlight(graphics, ItemID.POH_TABLET_TELEPORTTOHOUSE, leftClickColorWithAlpha);
                break;
            case Construction_cape:
                inHouseCheck();
                Color rightColor = colorProvider.getRightClickColorWithAlpha();
                itemHighlighter.itemHighlight(graphics, ItemID.SKILLCAPE_CONSTRUCTION, rightColor);
                // itemHighlight(graphics, ItemID.SKILLCAPE_CONSTRUCTION, rightClickColorWithAlpha);
                break;
            case Construction_cape_t:
                inHouseCheck();
                Color rightColor2 = colorProvider.getRightClickColorWithAlpha();
                itemHighlighter.itemHighlight(graphics, ItemID.SKILLCAPE_CONSTRUCTION_TRIMMED, rightColor2);
                // itemHighlight(graphics, ItemID.SKILLCAPE_CONSTRUCTION_TRIMMED, rightClickColorWithAlpha);
                break;
            case Max_cape:
                inHouseCheck();
                Color rightColor3 = colorProvider.getRightClickColorWithAlpha();
                itemHighlighter.itemHighlight(graphics, ItemID.SKILLCAPE_MAX, rightColor3);
                // itemHighlight(graphics, ItemID.SKILLCAPE_MAX, rightClickColorWithAlpha);
                break;
        }
    }

    private int currentTeleportCase = 1;

    public boolean isAtDestination = false;


    public void gettingToLocation(Graphics2D graphics, Location location) {
        updateColors();
        Teleport teleport = location.getSelectedTeleport();
        Boolean locationEnabledBool = false;
        if (plugin.getFarmingTeleportOverlay().herbRun) {
            locationEnabledBool = plugin.getHerbLocationEnabled(location.getName());
        }
        if (plugin.getFarmingTeleportOverlay().treeRun) {
            locationEnabledBool = plugin.getTreeLocationEnabled(location.getName());
        }
        if (plugin.getFarmingTeleportOverlay().fruitTreeRun) {
            locationEnabledBool = plugin.getFruitTreeLocationEnabled(location.getName());
        }
        if (plugin.getFarmingTeleportOverlay().hopsRun) {
            locationEnabledBool = plugin.getHopsLocationEnabled(location.getName());
        }
        if (locationEnabledBool) {
            int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();
            
            // Get patch point directly from location name to ensure correct coordinates
            WorldPoint patchPoint = null;
            int clearDistance = 5; // Default distance
            
            if (herbRun) {
                patchPoint = getHerbPatchPointForLocation(location.getName());
                // Morytania uses 10 tile threshold, others use 5 tiles
                if (patchPoint != null) {
                    clearDistance = "Morytania".equals(location.getName()) ? 10 : 5;
                }
            } else if (treeRun) {
                patchPoint = getTreePatchPointForLocation(location.getName());
            } else if (fruitTreeRun) {
                patchPoint = getFruitTreePatchPointForLocation(location.getName());
            } else if (hopsRun) {
                patchPoint = getHopsPatchPointForLocation(location.getName());
            }
            
            // Special handling for Brimhaven: If in Ardougne region, point to Captain Barnaby
            // Brimhaven fruit tree patch is in region 10547 (same as Ardougne), but we need to check
            // if player is in Ardougne city area (not yet in Brimhaven)
            if (fruitTreeRun && "Brimhaven".equals(location.getName())) {
                if (currentRegionId == 10547) {
                    // Check if player is near the Brimhaven patch (already in Brimhaven area)
                    boolean nearBrimhavenPatch = patchPoint != null && areaCheck.isPlayerWithinArea(patchPoint, 20);
                    if (!nearBrimhavenPatch) {
                        // Player is in Ardougne region but not near Brimhaven patch - needs to take boat
                        // Set hint arrow to Captain Barnaby location (switches to NPC when close)
                        WorldPoint captainBarnabyLocation = getNPCLocation("Captain Barnaby");
                        if (captainBarnabyLocation != null) {
                            setHintArrowToNPCLocation("Captain Barnaby", captainBarnabyLocation);
                        }
                    } else {
                        // Player is near Brimhaven patch, clear arrow
                        client.clearHintArrow();
                    }
                } else if (patchPoint != null) {
                    // Player is in a different region, use normal patch navigation
                    boolean nearPatch = areaCheck.isPlayerWithinArea(patchPoint, clearDistance);
                    if (nearPatch) {
                        client.clearHintArrow();
                    } else if (!isAtDestination) {
                        client.setHintArrow(patchPoint);
                    }
                }
            } else if (hopsRun && "Entrana".equals(location.getName())) {
                // Special handling for Entrana: Point to Entrana monks at Port Sarim
                // Once on Entrana (near patch), point to patch instead
                WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
                boolean nearEntranaPatch = patchPoint != null && areaCheck.isPlayerWithinArea(patchPoint, 20);
                
                if (nearEntranaPatch) {
                    // Player is on Entrana near the patch - point to patch
                    boolean nearPatch = areaCheck.isPlayerWithinArea(patchPoint, clearDistance);
                    if (nearPatch) {
                        client.clearHintArrow();
                    } else if (!isAtDestination) {
                        client.setHintArrow(patchPoint);
                    }
                } else {
                    // Player is not on Entrana - point to Entrana monks at Port Sarim
                    // This works whether player is at Port Sarim or navigating to it
                    // Arrow will point to WorldPoint when far, switch to NPC when within range
                    WorldPoint entranaMonkLocation = getNPCLocationById(1165);
                    if (entranaMonkLocation != null) {
                        setHintArrowToNPCLocationById(1165, entranaMonkLocation);
                    }
                }
            } else if (patchPoint != null) {
                // Handle hint arrows for all farming run types
                // Clear when within threshold, set when further away
                boolean nearPatch = areaCheck.isPlayerWithinArea(patchPoint, clearDistance);
                if (nearPatch) {
                    // Clear hint arrow when within threshold - step handlers will handle NPC interactions if needed
                    client.clearHintArrow();
                } else if (!isAtDestination) {
                    // Set hint arrow pointing to patch when navigating (and not at destination)
                    client.setHintArrow(patchPoint);
                }
            }
            
            if (!isAtDestination) {
                // Use adaptive detection to determine if we should proceed to farming
                if (shouldProceedToFarming(location, teleport)) {
                    this.currentTeleportCase = 1;
                    isAtDestination = true;
                    this.startSubCases = true;
                    if (location.getFarmLimps()) {
                        this.farmLimps = true;
                    }
                    plugin.addTextToInfoBox(teleport.getDescription());
                } else {
                    // Use adaptive highlighting based on current situation
                    adaptiveHighlighting(location, teleport, graphics);
                    plugin.addTextToInfoBox(teleport.getDescription());
                    return;
                }
                
                switch (teleport.getCategory()) {
                    case ITEM:
                        if (teleport.getInterfaceGroupId() != 0) {
                            Color rightColor = colorProvider.getRightClickColorWithAlpha();
                            if (!isInterfaceOpen(teleport.getInterfaceGroupId(), teleport.getInterfaceChildId())) {
                                itemHighlighter.itemHighlight(graphics, teleport.getId(), rightColor);
                                // itemHighlight(graphics, teleport.getId(), rightClickColorWithAlpha);
                                if (!teleport.getRightClickOption().equals("")) {
                                    menuHighlighter.highlightRightClickOption(graphics, teleport.getRightClickOption());
                                    // highlightRightClickOption(graphics, teleport.getRightClickOption());
                                }
                            } else {
                                Widget widget = client.getWidget(teleport.getInterfaceGroupId(), teleport.getInterfaceChildId());
                                widgetHighlighter.highlightDynamicComponent(graphics, widget, 1);
                                // highlightDynamicComponent(graphics, widget, 1);
                            }
                            if (currentRegionId == teleport.getRegionId()) {
                                this.currentTeleportCase = 1;
                                isAtDestination = true;
                                this.startSubCases = true;
                                if (location.getFarmLimps()) {
                                    this.farmLimps = true;
                                }
                            }
                        } else {
                            Color rightColor = colorProvider.getRightClickColorWithAlpha();
                            if (!teleport.getRightClickOption().equals("")) {
                                itemHighlighter.itemHighlight(graphics, teleport.getId(), rightColor);
                                menuHighlighter.highlightRightClickOption(graphics, teleport.getRightClickOption());
                                // itemHighlight(graphics, teleport.getId(), rightClickColorWithAlpha);
                                // highlightRightClickOption(graphics, teleport.getRightClickOption());
                            } else {
                                Color leftColor = colorProvider.getLeftClickColorWithAlpha();
                                if(plugin.getEasyFarmingOverlay().isTeleportCrystal(teleport.getId())) {
                                    itemHighlighter.highlightTeleportCrystal(graphics);
                                    // highlightTeleportCrystal(graphics);
                                }
                                if(plugin.getEasyFarmingOverlay().isSkillsNecklace(teleport.getId())) {
                                    String index = location.getName();
                                    List<Integer> skillsNecklaceIds = Constants.SKILLS_NECKLACE_IDS;
                                    if(Objects.equals(index, "Ardougne")) {
                                        for (int id : skillsNecklaceIds) {
                                            itemHighlighter.itemHighlight(graphics, id, rightColor);
                                        }
                                        Widget widget = client.getWidget(187, 3);
                                        if (widget != null && !widget.isHidden()) {
                                            widgetHighlighter.highlightDynamicComponent(graphics, widget, 0);
                                        }
                                        // highlightSkillsNecklace(graphics);
                                        // highlightRightClickOption(graphics, "Rub");
                                        // highlightDynamicComponent(graphics, widget, 0);
                                    }
                                    if(Objects.equals(index, "Farming Guild")) {
                                        for (int id : skillsNecklaceIds) {
                                            itemHighlighter.itemHighlight(graphics, id, rightColor);
                                        }
                                        Widget widget = client.getWidget(187, 3);
                                        if (widget != null && !widget.isHidden()) {
                                            widgetHighlighter.highlightDynamicComponent(graphics, widget, 5);
                                        }
                                        // highlightSkillsNecklace(graphics);
                                        // highlightRightClickOption(graphics, "Rub");
                                        // highlightDynamicComponent(graphics, widget, 5);
                                    }
                                }
                                else if(plugin.getEasyFarmingOverlay().isQuetzalWhistle(teleport.getId()) || 
                                        plugin.getEasyFarmingOverlay().isRoyalSeedPod(teleport.getId()) ||
                                        plugin.getEasyFarmingOverlay().isEctophial(teleport.getId())) {
                                    itemHighlighter.itemHighlight(graphics, teleport.getId(), leftColor);
                                    // itemHighlight(graphics, teleport.getId(), leftClickColorWithAlpha);
                                }
                                else {
                                    itemHighlighter.itemHighlight(graphics, teleport.getId(), leftColor);
                                    // itemHighlight(graphics, teleport.getId(), leftClickColorWithAlpha);
                                }
                            }
                            if (currentRegionId == teleport.getRegionId()) {
                                this.currentTeleportCase = 1;
                                isAtDestination = true;
                                this.startSubCases = true;
                                if (location.getFarmLimps()) {
                                    this.farmLimps = true;
                                }
                            }
                        }
                        break;
                    case PORTAL_NEXUS:
                        switch (this.currentTeleportCase) {
                            case 1:
                                gettingToHouse(graphics);
                                break;
                            case 2:
                                Color leftColor = colorProvider.getLeftClickColorWithAlpha();
                                if (!isInterfaceOpen(17, 0)) {
                                    List<Integer> portalNexusIds = getGameObjectIdsByName("Portal Nexus");
                                    for (Integer objectId : portalNexusIds) {
                                        gameObjectHighlighter.highlightGameObject(objectId, leftColor).render(graphics);
                                        // gameObjectOverlay(objectId, leftClickColorWithAlpha).render(graphics);
                                    }
                                } else {
                                    Widget widget = client.getWidget(17, 13);
                                    int index = getChildIndexPortalNexus(location.getName());
                                    widgetHighlighter.highlightDynamicComponent(graphics, widget, index);
                                    // highlightDynamicComponent(graphics, widget, index);
                                }
                                if (currentRegionId == teleport.getRegionId()) {
                                    this.currentTeleportCase = 1;
                                    isAtDestination = true;
                                    this.startSubCases = true;
                                    if (location.getFarmLimps()) {
                                        this.farmLimps = true;
                                    }
                                }
                                break;
                        }
                        break;
                    case SPIRIT_TREE:
                        Color leftColor = colorProvider.getLeftClickColorWithAlpha();
                        if (!isInterfaceOpen(187, 3)) {
                            List<Integer> spiritTreeIds = Arrays.asList(1293, 1294, 1295, 8355, 29227, 29229, 37329, 40778);

                            for (Integer objectId : spiritTreeIds) {
                                gameObjectHighlighter.highlightGameObject(objectId, leftColor).render(graphics);
                                // gameObjectOverlay(objectId, leftClickColorWithAlpha).render(graphics);
                            }
                        } else {
                            Widget widget = client.getWidget(187, 3);

                            switch (location.getName()) {
                                case "Gnome Stronghold":
                                    widgetHighlighter.highlightDynamicComponent(graphics, widget, getChildIndexSpiritTree("Gnome Stronghold"));
                                    // highlightDynamicComponent(graphics, widget, getChildIndexSpiritTree("Gnome Stronghold"));
                                    break;

                                case "Tree Gnome Village":
                                    widgetHighlighter.highlightDynamicComponent(graphics, widget, getChildIndexSpiritTree("Tree Gnome Village"));
                                    // highlightDynamicComponent(graphics, widget, getChildIndexSpiritTree("Tree Gnome Village"));
                                    break;

                                case "Falador":
                                    widgetHighlighter.highlightDynamicComponent(graphics, widget, getChildIndexSpiritTree("Port Sarim"));
                                    // highlightDynamicComponent(graphics, widget, getChildIndexSpiritTree("Port Sarim"));
                                    break;

                                case "Kourend":
                                    widgetHighlighter.highlightDynamicComponent(graphics, widget, getChildIndexSpiritTree("Hosidius"));
                                    // highlightDynamicComponent(graphics, widget, getChildIndexSpiritTree("Hosidius"));
                                    break;
                            }
                        }
                        if (currentRegionId == teleport.getRegionId()) {
                            this.currentTeleportCase = 1;
                            isAtDestination = true;
                            this.startSubCases = true;

                            if (location.getFarmLimps()) {
                                this.farmLimps = true;
                            }
                        }
                        break;                    
                    case JEWELLERY_BOX:
                        switch (this.currentTeleportCase) {
                            case 1:
                                gettingToHouse(graphics);
                                break;
                            case 2:
                                Color leftColorJewelry = colorProvider.getLeftClickColorWithAlpha();
                                List<Integer> jewelleryBoxIds = Arrays.asList(29154, 29155, 29156);

                                if (!isInterfaceOpen(590, 0)) {
                                    for (int id : jewelleryBoxIds) {
                                        gameObjectHighlighter.highlightGameObject(id, leftColorJewelry).render(graphics);
                                        // gameObjectOverlay(id, leftClickColorWithAlpha).render(graphics);
                                    }
                                    gameObjectHighlighter.highlightGameObject(teleport.getId(), leftColorJewelry).render(graphics);
                                    // gameObjectOverlay(teleport.getId(), leftClickColorWithAlpha).render(graphics);
                                } else {
                                    Widget widget = client.getWidget(590, 5);
                                    widgetHighlighter.highlightDynamicComponent(graphics, widget, 10);
                                    // highlightDynamicComponent(graphics, widget, 10);
                                }
                                if (currentRegionId == teleport.getRegionId()) {
                                    this.currentTeleportCase = 1;
                                    isAtDestination = true;
                                    this.startSubCases = true;
                                    if (location.getFarmLimps()) {
                                        this.farmLimps = true;
                                    }
                                }
                                break;
                        }
                        break;
                    case MOUNTED_XERICS:
                        switch (this.currentTeleportCase) {
                            case 1:
                                gettingToHouse(graphics);
                                break;
                            case 2:
                                List<Integer> xericsTalismanIds = Arrays.asList(33411, 33412, 33413, 33414, 33415);

                                if (!isInterfaceOpen(teleport.getInterfaceGroupId(), teleport.getInterfaceChildId())) {
                                    for (int id : xericsTalismanIds) {
                                        decorativeObjectHighlighter.highlightDecorativeObject(id).render(graphics);
                                        // Overlay decorativeObjectHighlight = decorativeObjectOverlay(id);
                                        // decorativeObjectHighlight.render(graphics);
                                    }
                                } else {
                                    Widget widget = client.getWidget(teleport.getInterfaceGroupId(), teleport.getInterfaceChildId());
                                    widgetHighlighter.highlightDynamicComponent(graphics, widget, 1);
                                    // highlightDynamicComponent(graphics, widget, 1);
                                    if (currentRegionId == teleport.getRegionId()) {
                                        this.currentTeleportCase = 1;
                                        isAtDestination = true;
                                        this.startSubCases = true;
                                        if (location.getFarmLimps()) {
                                            this.farmLimps = true;
                                        }
                                    }
                                }
                                break;
                        }
                        break;
                    case SPELLBOOK:
                        InventoryTabChecker.TabState tabState;
                        tabState = InventoryTabChecker.checkTab(client, VarClientID.TOPLEVEL_PANEL);
                        switch (tabState) {
                            case REST:
                            case INVENTORY:
                                // Highlight the spellbook icon in the tab bar (widget group 161/164, child 6)
                                widgetHighlighter.interfaceOverlay(getSpellbookIconGroupId(), getSpellbookIconChildId()).render(graphics);
                                // interfaceOverlay(getSpellbookIconGroupId(), getSpellbookIconChildId()).render(graphics);
                                if (currentRegionId == teleport.getRegionId()) {
                                    this.currentTeleportCase = 1;
                                    isAtDestination = true;
                                    this.startSubCases = true;
                                }
                                break;
                            case SPELLBOOK:
                                widgetHighlighter.interfaceOverlay(teleport.getInterfaceGroupId(), teleport.getInterfaceChildId()).render(graphics);
                                // interfaceOverlay(teleport.getInterfaceGroupId(), teleport.getInterfaceChildId()).render(graphics);
                                if (currentRegionId == teleport.getRegionId()) {
                                    this.currentTeleportCase = 1;
                                    isAtDestination = true;
                                    this.startSubCases = true;
                                }
                                break;
                        }
                        break;
                    default:
                        // Optional: Code for handling unexpected values
                        break;
                }

            } else {
                farming(graphics, teleport);
            }
        } else {
            this.currentLocationIndex++;
        }
    }
    //}


    private boolean farmLimps = false;

    public void farming(Graphics2D graphics, Teleport teleport) {
        if (this.startSubCases) {
            if (herbRun) {
                if (this.subCase == 1) {
                    farmingStepHandler.herbSteps(graphics, teleport);
                    if (farmingStepHandler.herbPatchDone) {
                        this.subCase = 2;
                        farmingStepHandler.herbPatchDone = false;
                    }
                } else if (this.subCase == 2) {
                    if (config.generalLimpwurt()) {
                        farmingStepHandler.flowerSteps(graphics, this.farmLimps);
                        if (farmingStepHandler.flowerPatchDone) {
                            if (config.generalAllotment()) {
                                this.subCase = 3;
                                farmingStepHandler.flowerPatchDone = false;
                            } else {
                                this.subCase = 1;
                                this.startSubCases = false;
                                isAtDestination = false;
                                this.currentLocationIndex++;
                                this.farmLimps = false;
                                farmingStepHandler.flowerPatchDone = false;
                            }
                        }
                    } else if (config.generalAllotment()) {
                        // Transition directly to allotment steps
                        this.subCase = 3;
                        // Reset allotment patch tracking for new location
                        farmingStepHandler.allotmentPatchDone = false;
                    } else {
                        this.subCase = 1;
                        this.startSubCases = false;
                        isAtDestination = false;
                        this.currentLocationIndex++;
                        this.farmLimps = false;
                        farmingStepHandler.flowerPatchDone = false;
                    }
                } else if (this.subCase == 3) {
                    if (config.generalAllotment()) {
                        farmingStepHandler.allotmentSteps(graphics, teleport);
                        if (farmingStepHandler.allotmentPatchDone) {
                            this.subCase = 1;
                            this.startSubCases = false;
                            isAtDestination = false;
                            this.currentLocationIndex++;
                            this.farmLimps = false;
                            farmingStepHandler.allotmentPatchDone = false;
                        }
                        // If allotmentPatchDone is false, continue to next frame - allotmentSteps() will handle instructions/highlights
                    } else {
                        this.subCase = 1;
                        this.startSubCases = false;
                        isAtDestination = false;
                        this.currentLocationIndex++;
                        this.farmLimps = false;
                    }
                }
            }
            if (treeRun) {
                farmingStepHandler.treeSteps(graphics, teleport);
                if (farmingStepHandler.treePatchDone) {
                    this.startSubCases = false;
                    isAtDestination = false;
                    this.currentLocationIndex++;
                    farmingStepHandler.treePatchDone = false;
                }
            }
            if (fruitTreeRun) {
                farmingStepHandler.fruitTreeSteps(graphics, teleport);
                if (farmingStepHandler.fruitTreePatchDone) {
                    this.startSubCases = false;
                    isAtDestination = false;
                    this.currentLocationIndex++;
                    farmingStepHandler.fruitTreePatchDone = false;
                }
            }
            if (hopsRun) {
                farmingStepHandler.hopsSteps(graphics, teleport);
                if (farmingStepHandler.hopsPatchDone) {
                    this.startSubCases = false;
                    isAtDestination = false;
                    this.currentLocationIndex++;
                    farmingStepHandler.hopsPatchDone = false;
                }
            }
        }
    }

    private int subCase = 1;
    private boolean startSubCases = false;
    private int currentLocationIndex = 0;

    public void removeOverlay() {
        plugin.overlayManager.remove(farmingHelperOverlay);
        plugin.overlayManager.remove(this);
        plugin.overlayManager.remove(farmingHelperOverlayInfoBox);

        plugin.setOverlayActive(false);
        plugin.setTeleportOverlayActive(false);

        this.currentLocationIndex = 0;
        this.currentTeleportCase = 1;
        this.subCase = 1;
        this.startSubCases = false;
        isAtDestination = false;
        this.farmLimps = false;
        farmingStepHandler.flowerPatchDone = false;
        farmingStepHandler.treePatchDone = false;
        farmingStepHandler.fruitTreePatchDone = false;
        farmingStepHandler.clearHintArrow();

        plugin.setItemsCollected(false);

        plugin.getFarmingTeleportOverlay().herbRun = false;
        plugin.getFarmingTeleportOverlay().treeRun = false;
        plugin.getFarmingTeleportOverlay().fruitTreeRun = false;
        plugin.getFarmingTeleportOverlay().hopsRun = false;

        fruitTreeRun = false;
        herbRun = false;
        treeRun = false;
        hopsRun = false;

        plugin.panel.herbButton.setStartStopState(false);
        plugin.panel.treeButton.setStartStopState(false);
        plugin.panel.fruitTreeButton.setStartStopState(false);
        plugin.panel.hopsButton.setStartStopState(false);
    }

    public Boolean herbRun = false;

    public Boolean treeRun = false;

    public Boolean fruitTreeRun = false;

    public Boolean hopsRun = false;

    @Override
    public Dimension render(Graphics2D graphics) {
        if (plugin.isTeleportOverlayActive()) {
            if (herbRun) {
                switch (this.currentLocationIndex) {
                    case 0:
                        gettingToLocation(graphics, plugin.getArdougneLocation());
                        break;
                    case 1:
                        gettingToLocation(graphics, plugin.getCatherbyLocation());
                        break;
                    case 2:
                        gettingToLocation(graphics, plugin.getFaladorLocation());
                        break;
                    case 3:
                        gettingToLocation(graphics, plugin.getFarmingGuildLocation());
                        break;
                    case 4:
                        gettingToLocation(graphics, plugin.getHarmonyLocation());
                        break;
                    case 5:
                        gettingToLocation(graphics, plugin.getKourendLocation());
                        break;
                    case 6:
                        gettingToLocation(graphics, plugin.getMorytaniaLocation());
                        break;
                    case 7:
                        gettingToLocation(graphics, plugin.getTrollStrongholdLocation());
                        break;
                    case 8:
                        gettingToLocation(graphics, plugin.getWeissLocation());
                        break;
                    case 9:
                        gettingToLocation(graphics, plugin.getCivitasLocation());
                        break;
                    case 10:
                        removeOverlay();
                        // add more cases for each location in the array
                    default:
                        removeOverlay();
                        // Add any other actions you want to perform when the herb run is complete
                        break;
                }
            } else if (treeRun) {
                switch (this.currentLocationIndex) {
                    case 0:
                        gettingToLocation(graphics, plugin.getFaladorTreeLocation());
                        break;
                    case 1:
                        gettingToLocation(graphics, plugin.getFarmingGuildTreeLocation());
                        break;
                    case 2:
                        gettingToLocation(graphics, plugin.getGnomeStrongholdTreeLocation());
                        break;
                    case 3:
                        gettingToLocation(graphics, plugin.getLumbridgeTreeLocation());
                        break;
                    case 4:
                        gettingToLocation(graphics, plugin.getTaverleyTreeLocation());
                        break;
                    case 5:
                        gettingToLocation(graphics, plugin.getVarrockTreeLocation());
                        break;
                    case 6:
                        removeOverlay();
                        // add more cases for each location in the array
                    default:
                        removeOverlay();
                        // Add any other actions you want to perform when the herb run is complete
                        break;
                }
            } else if (fruitTreeRun) {
                switch (this.currentLocationIndex) {
                    case 0:
                        gettingToLocation(graphics, plugin.getBrimhavenFruitTreeLocation());
                        break;
                    case 1:
                        gettingToLocation(graphics, plugin.getCatherbyFruitTreeLocation());
                        break;
                    case 2:
                        gettingToLocation(graphics, plugin.getFarmingGuildFruitTreeLocation());
                        break;
                    case 3:
                        gettingToLocation(graphics, plugin.getGnomeStrongholdFruitTreeLocation());
                        break;
                    case 4:
                        gettingToLocation(graphics, plugin.getLletyaFruitTreeLocation());
                        break;
                    case 5:
                        gettingToLocation(graphics, plugin.getTreeGnomeVillageTreeLocation());
                        break;
                    case 6:
                        removeOverlay();
                        // add more cases for each location in the array
                    default:
                        removeOverlay();
                        // Add any other actions you want to perform when the herb run is complete
                        break;
                }
            } else if (hopsRun) {
                switch (this.currentLocationIndex) {
                    case 0:
                        gettingToLocation(graphics, plugin.getLumbridgeHopsLocation());
                        break;
                    case 1:
                        gettingToLocation(graphics, plugin.getSeersVillageHopsLocation());
                        break;
                    case 2:
                        gettingToLocation(graphics, plugin.getYanilleHopsLocation());
                        break;
                    case 3:
                        gettingToLocation(graphics, plugin.getEntranaHopsLocation());
                        break;
                    case 4:
                        gettingToLocation(graphics, plugin.getAldarinHopsLocation());
                        break;
                    case 5:
                        removeOverlay();
                        // add more cases for each location in the array
                    default:
                        removeOverlay();
                        // Add any other actions you want to perform when the hops run is complete
                        break;
                }
            }
        }
        return null;
    }
}
