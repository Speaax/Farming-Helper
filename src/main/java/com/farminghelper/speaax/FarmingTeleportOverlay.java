package com.farminghelper.speaax;

import java.awt.*;
import javax.inject.Inject;

import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.api.ItemID;
import net.runelite.client.util.ColorUtil;
import net.runelite.api.Tile;

import java.awt.Color;
import java.awt.Shape;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;


public class FarmingTeleportOverlay extends Overlay
{
    private final Client client;
    private final FarmingHelperPlugin plugin;
    private boolean clicked = false;
    @Inject
    private FarmingHelperConfig config;
    @Inject
    private FarmingHelperOverlay farmingHelperOverlay;
    @Inject
    private FarmingTeleportOverlay farmingTeleportOverlay;
    @Inject
    private FarmingHelperOverlayInfoBox farmingHelperOverlayInfoBox;
    @Inject
    private AreaCheck areaCheck;

    private final PanelComponent panelComponent = new PanelComponent();
    public boolean patchCleared = false;

    private Color leftClickColorWithAlpha;
    private Color rightClickColorWithAlpha;
    private Color highlightUseItemWithAlpha;

    public void updateColors()
    {
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


    public boolean patchIsComposted()
    {
        String regexCompost1 = "You treat the (herb patch|flower patch|tree patch|fruit tree patch) with (compost|supercompost|ultracompost)\\.";
        String regexCompost2 = "This (herb patch|flower patch|tree patch|fruit tree patch) has already been treated with (compost|supercompost|ultracompost)\\.";

        return Pattern.compile(regexCompost1 + "|" + regexCompost2).matcher(plugin.getLastMessage()).matches();
    }

    public boolean patchIsProtected()
    {
        String standardResponse = "You pay the gardener ([0-9A-Za-z\\ ]+) to protect the patch\\.";
        String faladorEliteResponse = "The gardener protects your tree for you, free of charge, as a token of gratitude for completing the ([A-Za-z\\ ]+)\\.";

        return Pattern.compile(standardResponse + "|" + faladorEliteResponse).matcher(plugin.getLastMessage()).matches();
    }

    @Inject
    public FarmingTeleportOverlay(FarmingHelperPlugin plugin, Client client, AreaCheck areaCheck)
    {
        this.areaCheck = areaCheck;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        this.plugin = plugin;
        this.client = client;
    }

    public Overlay interfaceOverlay(int groupId, int childId)
    {
        return new Overlay()
        {
            @Override
            public Dimension render(Graphics2D graphics)
            {
                Client client = plugin.getClient();
                if (client != null) {
                    Widget widget = client.getWidget(
                        groupId,
                        childId
                    );
                    if (widget != null) {
                        Rectangle bounds = widget.getBounds();
                        graphics.setColor(leftClickColorWithAlpha);

                        // Set the composite for transparency
                        AlphaComposite alphaComposite = AlphaComposite.getInstance(
                            AlphaComposite.SRC_OVER,
                            0.65f
                        );
                        graphics.setComposite(alphaComposite);

                        // Draw a rectangle over the widget
                        graphics.fillRect(
                            bounds.x,
                            bounds.y,
                            bounds.width,
                            bounds.height
                        );

                        // Reset the composite back to the original
                        graphics.setComposite(AlphaComposite.SrcOver);

                    }
                }
                return null;
            }
        };
    }

    public int getChildIndex(Widget parentWidget, String searchText)
    {
        if (parentWidget == null) {
            return - 1;
        }

        Widget[] children = parentWidget.getChildren();

        if (children == null) {
            return - 1;
        }

        for (int index = 0; index < children.length; index++) {
            Widget child = children[index];
            String text = child.getText();

            if (text != null) {
                int colonIndex = text.indexOf(':');

                if (colonIndex != - 1 && colonIndex + 1 < text.length()) {
                    String textAfterColon = text.substring(colonIndex + 1).trim();

                    if (textAfterColon.equals(searchText)) {
                        return index;
                    }
                }
            }
        }

        return - 1; // Return -1 if the specified text is not found
    }

    public void highlightDynamicComponent(Graphics2D graphics, Widget widget, int dynamicChildIndex)
    {
        if (widget != null) {
            Widget[] dynamicChildren = widget.getDynamicChildren();
            if (dynamicChildren != null && dynamicChildIndex >= 0 && dynamicChildIndex < dynamicChildren.length) {
                Widget dynamicChild = dynamicChildren[dynamicChildIndex];
                if (dynamicChild != null) {
                    Rectangle bounds = dynamicChild.getBounds();
                    graphics.setColor(leftClickColorWithAlpha);
                    //graphics.draw(bounds);
                    graphics.fill(bounds);
                }
            }
        }
    }

    public void itemHighlight(Graphics2D graphics, int itemID, Color color)
    {
        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);

        if (inventory != null) {
            Item[] items = inventory.getItems();
            Widget inventoryWidget = client.getWidget(WidgetInfo.INVENTORY);

            for (int i = 0; i < items.length; i++) {
                Item item = items[i];

                if (item.getId() == itemID) {
                    Widget itemWidget = inventoryWidget.getChild(i);
                    Rectangle bounds = itemWidget.getBounds();
                    graphics.setColor(color);
                    graphics.draw(bounds);
                    graphics.fill(bounds);
                }
            }
        }
    }

    private List<GameObject> findGameObjectsByID(int objectID)
    {
        List<GameObject> gameObjects = new ArrayList<>();
        for (int x = 0; x < Constants.SCENE_SIZE; x++) {
            for (int y = 0; y < Constants.SCENE_SIZE; y++) {
                Tile tile = client.getScene().getTiles()[client.getPlane()][x][y];
                if (tile == null) {
                    continue;
                }

                for (GameObject gameObject : tile.getGameObjects()) {
                    if (gameObject != null && gameObject.getId() == objectID) {
                        gameObjects.add(gameObject);
                    }
                }
            }
        }
        return gameObjects;
    }

    private void drawGameObjectClickbox(Graphics2D graphics, GameObject gameObject, Color color)
    {
        Shape objectClickbox = gameObject.getClickbox();
        if (objectClickbox != null) {
            graphics.setColor(color);
            graphics.draw(objectClickbox);
            graphics.setColor(new Color(
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                color.getAlpha() / 5
            ));
            graphics.fill(objectClickbox);
        }
    }

    public Overlay gameObjectOverlay(int objectId, Color color)
    {
        return new Overlay()
        {
            @Override
            public Dimension render(Graphics2D graphics)
            {
                Client client = plugin.getClient();
                if (client != null) {
                    List<GameObject> gameObjects = findGameObjectsByID(objectId);
                    for (GameObject gameObject : gameObjects) {
                        drawGameObjectClickbox(
                            graphics,
                            gameObject,
                            color
                        );
                    }
                }
                return null;
            }
        };
    }

    public List<DecorativeObject> findDecorativeObjectsByID(int objectId)
    {
        Client client = plugin.getClient();
        List<DecorativeObject> foundDecorativeObjects = new ArrayList<>();

        if (client != null) {
            Tile[][][] tiles = client.getScene().getTiles();
            for (int plane = 0; plane < tiles.length; plane++) {
                for (int x = 0; x < tiles[plane].length; x++) {
                    for (int y = 0; y < tiles[plane][x].length; y++) {
                        Tile tile = tiles[plane][x][y];
                        if (tile != null) {
                            DecorativeObject decorativeObject = tile.getDecorativeObject();
                            if (decorativeObject != null && decorativeObject.getId() == objectId) {
                                foundDecorativeObjects.add(decorativeObject);
                            }
                        }
                    }
                }
            }
        }

        return foundDecorativeObjects;
    }

    public Overlay decorativeObjectOverlay(int objectId)
    {
        return new Overlay()
        {
            @Override
            public Dimension render(Graphics2D graphics)
            {
                Client client = plugin.getClient();
                if (client != null) {
                    List<DecorativeObject> decorativeObjects = findDecorativeObjectsByID(objectId);
                    for (DecorativeObject decorativeObject : decorativeObjects) {
                        drawDecorativeObjectClickbox(
                            graphics,
                            decorativeObject,
                            leftClickColorWithAlpha
                        );
                    }
                }
                return null;
            }
        };
    }


    public void drawDecorativeObjectClickbox(Graphics2D graphics, DecorativeObject decorativeObject, Color color)
    {
        Shape clickbox = decorativeObject.getClickbox();
        if (clickbox != null) {
            graphics.setColor(color);
            graphics.draw(clickbox);
            graphics.setColor(new Color(
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                50
            ));
            graphics.fill(clickbox);
        }
    }

    public void highlightRightClickOption(Graphics2D graphics, String option)
    {
        // Get the menu entries
        MenuEntry[] menuEntries = client.getMenuEntries();

        for (int i = 0; i < menuEntries.length; i++) {
            MenuEntry entry = menuEntries[i];
            String optionText = entry.getOption();

            // Check if the option text matches the desired option
            if (optionText.equalsIgnoreCase(option)) {
                // Modify the menu entry to include a highlight
                String highlightedText = ColorUtil.prependColorTag(
                    ">>> " + optionText,
                    rightClickColorWithAlpha
                );
                entry.setOption(highlightedText);
                client.setMenuEntries(menuEntries);
                break;
            }
        }
    }

    public void highlightNpc(Graphics2D graphics, Polygon tilePolygon)
    {
        if (tilePolygon != null) {
            graphics.setColor(leftClickColorWithAlpha);
            graphics.draw(tilePolygon);
            graphics.fill(tilePolygon);
        }
    }

    public void highlightNpc(Graphics2D graphics, String npcName)
    {
        List<NPC> npcs = client.getNpcs();

        if (npcs != null) {
            for (NPC npc : npcs) {
                if (npc != null && npc.getName() != null && npc.getName().equals(npcName)) {
                    highlightNpc(
                        graphics,
                        npc.getCanvasTilePoly()
                    );
                }
            }
        }
    }

    public void highlightNpc(Graphics2D graphics, Integer npcId)
    {
        List<NPC> npcs = client.getNpcs();

        if (npcs != null) {
            for (NPC npc : npcs) {
                if (npc != null && npc.getId() == npcId) {
                    highlightNpc(
                        graphics,
                        npc.getCanvasTilePoly()
                    );
                }
            }
        }
    }


    private boolean isInterfaceOpen(int groupId, int childId)
    {
        Widget widget = client.getWidget(
            groupId,
            childId
        );
        return widget != null && ! widget.isHidden();
    }


    private void printWidgetText(int groupId, int childId)
    {
        Widget widget = client.getWidget(
            groupId,
            childId
        );
        if (widget != null) {
            String text = widget.getText();
            System.out.println("Widget text: " + text);
        } else {
            System.out.println("Widget not found for groupId: " + groupId + " and childId: " + childId);
        }
    }

    public void highlightHerbPatches(Graphics2D graphics, Color color)
    {
        for (Integer patchId : farmingHelperOverlay.getHerbPatchIds()) {
            gameObjectOverlay(
                patchId,
                color
            ).render(graphics);
        }
    }

    public void highlightFlowerPatches(Graphics2D graphics, Color color)
    {
        for (Integer patchId : farmingHelperOverlay.getFlowerPatchIds()) {
            gameObjectOverlay(
                patchId,
                color
            ).render(graphics);
        }
    }

    public void highlightTreePatches(Graphics2D graphics, Color color)
    {
        for (Integer patchId : farmingHelperOverlay.getTreePatchIds()) {
            gameObjectOverlay(
                patchId,
                color
            ).render(graphics);
        }
    }

    public void highlightFruitTreePatches(Graphics2D graphics, Color color)
    {
        for (Integer patchId : farmingHelperOverlay.getFruitTreePatchIds()) {
            gameObjectOverlay(
                patchId,
                color
            ).render(graphics);
        }
    }

    public void highlightHardwoodPatches(Graphics2D graphics, Color color)
    {
        for (Integer patchId : farmingHelperOverlay.getHardwoodPatchIds()) {
            gameObjectOverlay(
                patchId,
                color
            ).render(graphics);
        }
    }

    public void highlightCompost(Graphics2D graphics)
    {
        if (isItemInInventory(selectedCompostID())) {
            if (herbRun) {
                if (subCase == 1) {
                    highlightHerbPatches(
                        graphics,
                        highlightUseItemWithAlpha
                    );

                } else if (subCase == 2) {
                    highlightFlowerPatches(
                        graphics,
                        highlightUseItemWithAlpha
                    );
                }

            }

            if (treeRun) {
                highlightTreePatches(
                    graphics,
                    highlightUseItemWithAlpha
                );
            }

            if (fruitTreeRun) {
                highlightFruitTreePatches(
                    graphics,
                    highlightUseItemWithAlpha
                );
            }
            if (hardwoodRun) {
                highlightHardwoodPatches(
                    graphics,
                    highlightUseItemWithAlpha
                );
            }

            itemHighlight(
                graphics,
                selectedCompostID(),
                highlightUseItemWithAlpha
            );
        } else {
            withdrawCompost(graphics);
        }
    }

    public void highlightFarmersByName(Graphics2D graphics, List<String> farmers)
    {
        if (! isInterfaceOpen(
            219,
            1
        )) {
            for (String farmer : farmers) {
                highlightNpc(
                    graphics,
                    farmer
                );
            }
        } else {
            Widget widget = client.getWidget(
                219,
                1
            );
            highlightDynamicComponent(
                graphics,
                widget,
                1
            );
        }
    }

    public void highlightFarmersById(Graphics2D graphics, List<Integer> farmers)
    {
        if (! isInterfaceOpen(
            219,
            1
        )) {
            for (Integer farmer : farmers) {
                highlightNpc(
                    graphics,
                    farmer
                );
            }
        } else {
            Widget widget = client.getWidget(
                219,
                1
            );
            highlightDynamicComponent(
                graphics,
                widget,
                1
            );
        }
    }

    public void highlightTreeFarmers(Graphics2D graphics)
    {
        highlightFarmersByName(
            graphics,
            Arrays.asList(
                "Alain",
                // Taverly
                "Fayeth",
                // Lumbridge
                "Heskel",
                // Falador
                "Prissy Scilla",
                // Gnome Stronghold
                "Rosie",
                // Farming Guild
                "Treznor"
                // Varrock
            )
        );

        highlightFarmersById(
            graphics,
            Arrays.asList(
                7754,
                // Squirrel - East
                7755,
                // Squirrel - South
                7756
                // Squirrel - West
            )
        );
    }

    public void highlightFruitTreeFarmers(Graphics2D graphics)
    {
        highlightFarmersByName(
            graphics,
            Arrays.asList(
                "Bolongo",
                // Gnome Stronghold
                "Ellena",
                // Catherby
                "Garth",
                // Brimhaven
                "Gileth",
                // Tree Gnome Village
                "Liliwen",
                // Lletya
                "Nikkie"
                // Farming Guild
            )
        );
    }

    public void highlightHerbSeeds(Graphics2D graphics)
    {
        for (Integer seedId : farmingHelperOverlay.getHerbSeedIds()) {
            itemHighlight(
                graphics,
                seedId,
                highlightUseItemWithAlpha
            );
        }
    }

    public void highlightTreeSapling(Graphics2D graphics)
    {
        for (Integer seedId : farmingHelperOverlay.getTreeSaplingIds()) {
            itemHighlight(
                graphics,
                seedId,
                highlightUseItemWithAlpha
            );
        }
    }

    public void highlightFruitTreeSapling(Graphics2D graphics)
    {
        for (Integer seedId : farmingHelperOverlay.getFruitTreeSaplingIds()) {
            itemHighlight(
                graphics,
                seedId,
                highlightUseItemWithAlpha
            );
        }
    }

    public void highlightHardwoodSapling(Graphics2D graphics)
    {
        for (Integer seedId : farmingHelperOverlay.getHardwoodSaplingIds()) {
            itemHighlight(
                graphics,
                seedId,
                highlightUseItemWithAlpha
            );
        }
    }

    public void highlightTeleportCrystal(Graphics2D graphics)
    {
        for (Integer seedId : farmingHelperOverlay.getTeleportCrystalIdsIds()) {
            itemHighlight(
                graphics,
                seedId,
                leftClickColorWithAlpha
            );
        }
    }

    public void highlightSkillsNecklace(Graphics2D graphics)
    {
        for (Integer seedId : farmingHelperOverlay.getSkillsNecklaceIdsIds()) {
            itemHighlight(
                graphics,
                seedId,
                leftClickColorWithAlpha
            );
        }
    }

    public Integer selectedCompostID()
    {
        FarmingHelperConfig.OptionEnumCompost selectedCompost = config.enumConfigCompost();
        switch (selectedCompost) {
            case Compost:
                return ItemID.COMPOST;
            case Supercompost:
                return ItemID.SUPERCOMPOST;
            case Ultracompost:
                return ItemID.ULTRACOMPOST;
            case Bottomless:
                return ItemID.BOTTOMLESS_COMPOST_BUCKET_22997;
        }
        return - 1;
    }

    private boolean isItemInInventory(int itemId)
    {
        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);

        Item[] items;
        if (inventory == null || inventory.getItems() == null) {
            items = new Item[0];
        } else {
            items = inventory.getItems();
        }

        for (Item item : items) {
            if (item.getId() == itemId) {
                return true;
            }
        }

        return false;
    }

    public void withdrawCompost(Graphics2D graphics)
    {
        plugin.addTextToInfoBox("Withdraw compost from Tool Leprechaun");
        if (! isInterfaceOpen(125,
                              0)) {
            highlightNpc(
                graphics,
                "Tool Leprechaun"
            );
        } else {
            if (selectedCompostID() == ItemID.COMPOST) {
                interfaceOverlay(
                    125,
                    17
                ).render(graphics);
            } else if (selectedCompostID() == ItemID.SUPERCOMPOST) {
                interfaceOverlay(
                    125,
                    18
                ).render(graphics);
            } else if (selectedCompostID() == ItemID.ULTRACOMPOST) {
                interfaceOverlay(
                    125,
                    19
                ).render(graphics);
            } else if (selectedCompostID() == ItemID.BOTTOMLESS_COMPOST_BUCKET_22997) {
                interfaceOverlay(
                    125,
                    15
                ).render(graphics);
            }
        }
    }

    public Boolean herbPatchDone = false;

    public void herbSteps(Graphics2D graphics, Location.Teleport teleport)
    {
        int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        HerbPatchChecker.PlantState plantState;

        //Farming guild herb patch uses Varbits.FARMING_4775
        if (currentRegionId == 4922) {
            plantState = HerbPatchChecker.checkHerbPatch(
                client,
                Varbits.FARMING_4775
            );
        }
        //Harmony herb patch uses Varbits.FARMING_4772
        else if (currentRegionId == 15148) {
            plantState = HerbPatchChecker.checkHerbPatch(
                client,
                Varbits.FARMING_4772
            );
        }
        //Troll Stronghold and Weiss herb patch uses Varbits.FARMING_4771
        else if (currentRegionId == 11321 || currentRegionId == 11325) {
            plantState = HerbPatchChecker.checkHerbPatch(
                client,
                Varbits.FARMING_4771
            );
        }
        //Rest uses Varbits.FARMING_4774
        else {
            plantState = HerbPatchChecker.checkHerbPatch(
                client,
                Varbits.FARMING_4774
            );
        }
        if (! areaCheck.isPlayerWithinArea(
            teleport.getPoint(),
            15
        )) {
            //should be replaced with a pathing system, pointing arrow or something else eventually
            highlightHerbPatches(
                graphics,
                leftClickColorWithAlpha
            );
        } else {
            switch (plantState) {
                case HARVESTABLE:
                    plugin.addTextToInfoBox("Harvest Herbs.");
                    highlightHerbPatches(
                        graphics,
                        leftClickColorWithAlpha
                    );
                    break;
                case PLANT:
                    plugin.addTextToInfoBox("Use Herb seed on patch.");
                    highlightHerbPatches(
                        graphics,
                        highlightUseItemWithAlpha
                    );
                    highlightHerbSeeds(graphics);
                    break;
                case DEAD:
                    plugin.addTextToInfoBox("Clear the dead herb patch.");
                    highlightHerbPatches(
                        graphics,
                        leftClickColorWithAlpha
                    );
                    break;
                case DISEASED:
                    plugin.addTextToInfoBox("Use Plant cure on herb patch. Buy at GE or in farming guild/catherby, and store at Tool Leprechaun for easy access.");
                    highlightHerbPatches(
                        graphics,
                        leftClickColorWithAlpha
                    );
                    itemHighlight(
                        graphics,
                        ItemID.PLANT_CURE,
                        highlightUseItemWithAlpha
                    );
                    break;
                case WEEDS:
                    plugin.addTextToInfoBox("Rake the herb patch.");
                    highlightHerbPatches(
                        graphics,
                        leftClickColorWithAlpha
                    );
                    break;
                case GROWING:
                    plugin.addTextToInfoBox("Use Compost on patch.");

                    highlightCompost(graphics);

                    if (patchIsComposted()) {
                        herbPatchDone = true;
                    }
                    break;
                case UNKNOWN:
                    plugin.addTextToInfoBox("UNKNOWN state: Try to do something with the herb patch to change its state.");
                    break;
            }
        }
    }

    public static boolean flowerPatchDone = false;

    public void flowerSteps(Graphics2D graphics)
    {
        if (farmLimps) {
            int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();
            FlowerPatchChecker.PlantState plantState;
            if (currentRegionId == 4922) {
                plantState = FlowerPatchChecker.checkFlowerPatch(
                    client,
                    Varbits.FARMING_7906
                );
            } else {
                plantState = FlowerPatchChecker.checkFlowerPatch(
                    client,
                    Varbits.FARMING_4773
                );
            }
            switch (plantState) {
                case HARVESTABLE:
                    plugin.addTextToInfoBox("Harvest Limwurt root.");
                    highlightFlowerPatches(
                        graphics,
                        leftClickColorWithAlpha
                    );
                    break;
                case WEEDS:
                    plugin.addTextToInfoBox("Rake the flower patch.");
                    highlightFlowerPatches(
                        graphics,
                        leftClickColorWithAlpha
                    );
                    break;
                case DEAD:
                    plugin.addTextToInfoBox("Clear the dead flower patch.");
                    highlightFlowerPatches(
                        graphics,
                        leftClickColorWithAlpha
                    );
                    break;
                case PLANT:
                    plugin.addTextToInfoBox("Use Limwurt seed on the patch.");
                    highlightFlowerPatches(
                        graphics,
                        highlightUseItemWithAlpha
                    );
                    itemHighlight(
                        graphics,
                        ItemID.LIMPWURT_SEED,
                        highlightUseItemWithAlpha
                    );
                    break;
                case GROWING:
                    plugin.addTextToInfoBox("Use Compost on patch.");

                    highlightCompost(graphics);

                    if (patchIsComposted()) {
                        flowerPatchDone = true;
                    }
                    break;
            }
        } else {
            flowerPatchDone = true;
        }
    }

    public Boolean treePatchDone = false;

    public void treeSteps(Graphics2D graphics, Location.Teleport teleport)
    {
        int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        TreePatchChecker.PlantState plantState;
        //4771 falador, gnome stronghold, lumbridge, Taverly, Varrock
        //7905 farming guild
        if (currentRegionId == 4922) {
            plantState = TreePatchChecker.checkTreePatch(
                client,
                Varbits.FARMING_7905
            );
        } else {
            plantState = TreePatchChecker.checkTreePatch(
                client,
                Varbits.FARMING_4771
            );
        }
        if (! areaCheck.isPlayerWithinArea(
            teleport.getPoint(),
            15
        )) {
            //should be replaced with a pathing system, pointing arrow or something else eventually
            highlightTreePatches(
                graphics,
                leftClickColorWithAlpha
            );
        } else {
            switch (plantState) {
                case HEALTHY:
                    plugin.addTextToInfoBox("Check tree health.");
                    highlightTreePatches(
                        graphics,
                        leftClickColorWithAlpha
                    );
                    break;
                case WEEDS:
                    plugin.addTextToInfoBox("Rake the tree patch.");
                    highlightTreePatches(
                        graphics,
                        leftClickColorWithAlpha
                    );
                    break;
                case DEAD:
                    plugin.addTextToInfoBox("Clear the dead tree patch.");
                    highlightTreePatches(
                        graphics,
                        leftClickColorWithAlpha
                    );
                    break;
                case PLANT:
                    plugin.addTextToInfoBox("Use Sapling on the patch.");
                    highlightTreePatches(
                        graphics,
                        highlightUseItemWithAlpha
                    );
                    highlightTreeSapling(graphics);
                    break;
                case DISEASED:
                    plugin.addTextToInfoBox("Prune the tree patch patch.");
                    highlightTreePatches(
                        graphics,
                        highlightUseItemWithAlpha
                    );
                    break;
                case REMOVE:
                    plugin.addTextToInfoBox("Pay to remove tree, or cut it down and clear the patch.");

                    highlightTreeFarmers(graphics);

                    break;
                case UNKNOWN:
                    plugin.addTextToInfoBox("UNKNOWN state: Try to do something with the tree patch to change its state.");
                    break;
                case GROWING:
                    if (config.generalPayForProtection()) {
                        plugin.addTextToInfoBox("Pay to protect the patch.");

                        highlightTreeFarmers(graphics);

                        if (patchIsProtected()) {
                            treePatchDone = true;
                        }
                    } else {
                        plugin.addTextToInfoBox("Use Compost on patch.");

                        highlightCompost(graphics);

                        if (patchIsComposted()) {
                            treePatchDone = true;
                        }
                    }

                    break;
            }
        }
    }

    public Boolean fruitTreePatchDone = false;

    public void fruitTreeSteps(Graphics2D graphics, Location.Teleport teleport)
    {
        int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        FruitTreePatchChecker.PlantState plantState;
        //Varbits.FARMING_4771 brimhaven, catherby, Lletya, tree gnome village
        //Varbits.FARMING_7909 farming guild
        //Varbits.FARMING_4772 gnome stronghold
        if (currentRegionId == 4922) {
            plantState = FruitTreePatchChecker.checkFruitTreePatch(
                client,
                Varbits.FARMING_7909
            );
        } else if (currentRegionId == 9782 || currentRegionId == 9781) {
            plantState = FruitTreePatchChecker.checkFruitTreePatch(
                client,
                Varbits.FARMING_4772
            );
        } else {
            plantState = FruitTreePatchChecker.checkFruitTreePatch(
                client,
                Varbits.FARMING_4771
            );
        }
        if (! areaCheck.isPlayerWithinArea(
            teleport.getPoint(),
            15
        )) {
            //should be replaced with a pathing system, point arrow or something else eventually
            highlightFruitTreePatches(
                graphics,
                leftClickColorWithAlpha
            );
        } else {
            switch (plantState) {
                case HEALTHY:
                    plugin.addTextToInfoBox("Check Fruit tree health.");
                    highlightFruitTreePatches(
                        graphics,
                        leftClickColorWithAlpha
                    );
                    break;
                case WEEDS:
                    plugin.addTextToInfoBox("Rake the fruit tree patch.");
                    highlightFruitTreePatches(
                        graphics,
                        leftClickColorWithAlpha
                    );
                    break;
                case DEAD:
                    plugin.addTextToInfoBox("Clear the dead fruit tree patch.");
                    highlightFruitTreePatches(
                        graphics,
                        leftClickColorWithAlpha
                    );
                    break;
                case PLANT:
                    plugin.addTextToInfoBox("Use Sapling on the patch.");
                    highlightFruitTreePatches(
                        graphics,
                        highlightUseItemWithAlpha
                    );
                    highlightFruitTreeSapling(graphics);
                    break;
                case DISEASED:
                    plugin.addTextToInfoBox("Prune the fruit tree patch.");
                    highlightFruitTreePatches(
                        graphics,
                        leftClickColorWithAlpha
                    );
                    break;
                case REMOVE:
                    plugin.addTextToInfoBox("Pay to remove fruit tree, or cut it down and clear the patch.");

                    highlightFruitTreeFarmers(graphics);

                    break;
                case UNKNOWN:
                    plugin.addTextToInfoBox("UNKNOWN state: Try to do something with the tree patch to change its state.");
                    break;
                case GROWING:
                    if (config.generalPayForProtection()) {
                        plugin.addTextToInfoBox("Pay to protect the patch.");

                        highlightFruitTreeFarmers(graphics);

                        if (patchIsProtected()) {
                            fruitTreePatchDone = true;
                        }
                    } else {
                        plugin.addTextToInfoBox("Use Compost on patch.");

                        highlightCompost(graphics);

                        if (patchIsComposted()) {
                            fruitTreePatchDone = true;
                        }
                    }

                    break;
            }
        }
    }

    public Boolean hardwoodPatchDone = false;

    public void hardwoodSteps(Graphics2D graphics, Location.Teleport teleport)
    {
        System.out.print("hardwoodSteps");
        int test = 1;
        int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        HardwoodPatchChecker.PlantState plantState;
        if (currentRegionId == 14651 || currentRegionId == 14907) {
            plantState = HardwoodPatchChecker.checkHardwoodPatch(
                client,
                Varbits.FARMING_4771
            );
        } else {
            plantState = HardwoodPatchChecker.checkHardwoodPatch(
                client,
                Varbits.FARMING_4772
            );
        }
        if (! areaCheck.isPlayerWithinArea(
            teleport.getPoint(),
            15
        )) {
            //should be replaced with a pathing system, pointing arrow or something else eventually
            highlightHardwoodPatches(
                graphics,
                leftClickColorWithAlpha
            );
        } else {
            switch (plantState) {
                case HEALTHY:
                    plugin.addTextToInfoBox("Check tree health.");
                    highlightHardwoodPatches(
                        graphics,
                        leftClickColorWithAlpha
                    );
                    break;
                case WEEDS:
                    plugin.addTextToInfoBox("Rake the tree patch.");
                    highlightHardwoodPatches(
                        graphics,
                        leftClickColorWithAlpha
                    );
                    break;
                case DEAD:
                    plugin.addTextToInfoBox("Clear the dead tree patch.");
                    highlightHardwoodPatches(
                        graphics,
                        leftClickColorWithAlpha
                    );
                    break;
                case PLANT:
                    plugin.addTextToInfoBox("Use Sapling on the patch.");
                    highlightHardwoodPatches(
                        graphics,
                        highlightUseItemWithAlpha
                    );
                    highlightHardwoodSapling(graphics);
                    break;
                case DISEASED:
                    plugin.addTextToInfoBox("Prune the tree patch patch.");
                    highlightHardwoodPatches(
                        graphics,
                        highlightUseItemWithAlpha
                    );
                    break;
                case REMOVE:
                    plugin.addTextToInfoBox("Pay to remove tree, or cut it down and clear the patch.");

                    highlightTreeFarmers(graphics);

                    break;
                case UNKNOWN:
                    plugin.addTextToInfoBox("UNKNOWN state: Try to do something with the tree patch to change its state.");
                    break;
                case GROWING:
                    if (config.generalPayForProtection()) {
                        plugin.addTextToInfoBox("Pay to protect the patch.");

                        highlightTreeFarmers(graphics);

                        if (patchIsProtected()) {
                            treePatchDone = true;
                        }
                    } else {
                        plugin.addTextToInfoBox("Use Compost on patch.");

                        highlightCompost(graphics);

                        if (patchIsComposted()) {
                            treePatchDone = true;
                        }
                    }

                    break;
            }
        }
    }


    private List<Integer> getGameObjectIdsByName(String name)
    {
        List<Integer> foundObjectIds = new ArrayList<>();
        Scene scene = client.getScene();
        Tile[][][] tiles = scene.getTiles();

        for (int x = 0; x < Constants.SCENE_SIZE; x++) {
            for (int y = 0; y < Constants.SCENE_SIZE; y++) {
                Tile tile = tiles[client.getPlane()][x][y];
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

    public void inHouseCheck()
    {
        if (getGameObjectIdsByName("Portal").contains(4525)) {
            currentTeleportCase = 2;
        }
    }

    public void gettingToHouse(Graphics2D graphics)
    {
        FarmingHelperConfig.OptionEnumHouseTele teleportOption = config.enumConfigHouseTele();
        switch (teleportOption) {
            case Law_air_earth_runes:
                InventoryTabChecker.TabState tabState;
                tabState = InventoryTabChecker.checkTab(
                    client,
                    VarClientInt.INVENTORY_TAB
                );
                switch (tabState) {
                    case INVENTORY:
                    case REST:
                        interfaceOverlay(
                            161,
                            64
                        ).render(graphics);
                        break;
                    case SPELLBOOK:
                        interfaceOverlay(
                            218,
                            29
                        ).render(graphics);
                        inHouseCheck();
                        break;
                }
            case Teleport_To_House:
                inHouseCheck();
                itemHighlight(
                    graphics,
                    ItemID.TELEPORT_TO_HOUSE,
                    leftClickColorWithAlpha
                );
                break;
            case Construction_cape:
                inHouseCheck();
                itemHighlight(
                    graphics,
                    ItemID.CONSTRUCT_CAPE,
                    rightClickColorWithAlpha
                );
                break;
            case Construction_cape_t:
                inHouseCheck();
                itemHighlight(
                    graphics,
                    ItemID.CONSTRUCT_CAPET,
                    rightClickColorWithAlpha
                );
                break;
            case Max_cape:
                inHouseCheck();
                itemHighlight(
                    graphics,
                    ItemID.MAX_CAPE,
                    rightClickColorWithAlpha
                );
                break;
        }
    }

    public static int currentTeleportCase = 1;

    public boolean isAtDestination = false;


    public void gettingToLocation(Graphics2D graphics, Location location)
    {
        updateColors();
        Location.Teleport teleport = location.getSelectedTeleport();
        Boolean locationEnabledBool = false;
        if (plugin.getFarmingTeleportOverlay().herbRun) {
            locationEnabledBool = plugin.getHerbLocationEnabled(location.getName());
        }
        if (plugin.getFarmingTeleportOverlay().treeRun) {
            locationEnabledBool = plugin.getTreeLocationEnabled(location.getName());
        }
        if (plugin.getFarmingTeleportOverlay().hardwoodRun) {
            locationEnabledBool = plugin.getHardwoodLocationEnabled(location.getName());
        }
        if (plugin.getFarmingTeleportOverlay().fruitTreeRun) {
            locationEnabledBool = plugin.getFruitTreeLocationEnabled(location.getName());
        }
        if (locationEnabledBool) {
            if (! isAtDestination) {
                int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();
                plugin.addTextToInfoBox(teleport.getDescription());
                switch (teleport.getCategory()) {
                    case ITEM:
                        if (teleport.getInterfaceGroupId() != 0) {
                            if (! isInterfaceOpen(
                                teleport.getInterfaceGroupId(),
                                teleport.getInterfaceChildId()
                            )) {
                                itemHighlight(
                                    graphics,
                                    teleport.getId(),
                                    rightClickColorWithAlpha
                                );
                                if (! teleport.getRightClickOption().equals("null")) {
                                    highlightRightClickOption(
                                        graphics,
                                        teleport.getRightClickOption()
                                    );
                                }
                            } else {
                                Widget widget = client.getWidget(
                                    teleport.getInterfaceGroupId(),
                                    teleport.getInterfaceChildId()
                                );
                                highlightDynamicComponent(
                                    graphics,
                                    widget,
                                    1
                                );
                            }
                            if (currentRegionId == teleport.getRegionId()) {
                                currentTeleportCase = 1;
                                isAtDestination = true;
                                startSubCases = true;
                                if (location.getFarmLimps()) {
                                    farmLimps = true;
                                }
                            }
                        } else {
                            if (! teleport.getRightClickOption().equals("null")) {
                                itemHighlight(
                                    graphics,
                                    teleport.getId(),
                                    rightClickColorWithAlpha
                                );
                                highlightRightClickOption(
                                    graphics,
                                    teleport.getRightClickOption()
                                );
                            } else {
                                if (teleport.getId() == ItemID.TELEPORT_CRYSTAL_1) {
                                    highlightTeleportCrystal(graphics);
                                }
                                if (teleport.getId() == ItemID.SKILLS_NECKLACE1) {
                                    String index = location.getName();
                                    if (Objects.equals(
                                        index,
                                        "Ardougne"
                                    )) {
                                        highlightSkillsNecklace(graphics);
                                        highlightRightClickOption(
                                            graphics,
                                            "Rub"
                                        );
                                        Widget widget = client.getWidget(
                                            187,
                                            3
                                        );
                                        highlightDynamicComponent(
                                            graphics,
                                            widget,
                                            0
                                        );
                                    }
                                    if (Objects.equals(
                                        index,
                                        "Farming Guild"
                                    )) {
                                        highlightSkillsNecklace(graphics);
                                        highlightRightClickOption(
                                            graphics,
                                            "Rub"
                                        );
                                        Widget widget = client.getWidget(
                                            187,
                                            3
                                        );
                                        highlightDynamicComponent(
                                            graphics,
                                            widget,
                                            5
                                        );
                                    }
                                } else {
                                    itemHighlight(
                                        graphics,
                                        teleport.getId(),
                                        leftClickColorWithAlpha
                                    );
                                }
                            }
                            if (currentRegionId == teleport.getRegionId()) {
                                currentTeleportCase = 1;
                                isAtDestination = true;
                                startSubCases = true;
                                if (location.getFarmLimps()) {
                                    farmLimps = true;
                                }
                            }
                        }
                        break;
                    case PORTAL_NEXUS:
                        switch (currentTeleportCase) {
                            case 1:
                                gettingToHouse(graphics);
                                break;
                            case 2:
                                if (! isInterfaceOpen(
                                    17,
                                    0
                                )) {
                                    List<Integer> portalNexusIds = getGameObjectIdsByName("Portal Nexus");

                                    for (Integer objectId : portalNexusIds) {
                                        gameObjectOverlay(
                                            objectId,
                                            leftClickColorWithAlpha
                                        ).render(graphics);
                                    }
                                } else {
                                    Widget widget = client.getWidget(
                                        17,
                                        13
                                    );

                                    highlightDynamicComponent(
                                        graphics,
                                        widget,
                                        getChildIndex(
                                            client.getWidget(
                                                17,
                                                12
                                            ),
                                            teleport.overrideLocationName() == "" ? location.getName() : teleport.overrideLocationName()
                                        )
                                    );
                                }

                                if (currentRegionId == teleport.getRegionId()) {
                                    currentTeleportCase = 1;
                                    isAtDestination = true;
                                    startSubCases = true;

                                    if (location.getFarmLimps()) {
                                        farmLimps = true;
                                    }
                                }

                                break;
                        }
                        break;
                    case SPIRIT_TREE:
                        if (! isInterfaceOpen(
                            187,
                            3
                        )) {
                            List<Integer> spiritTreeIds = Arrays.asList(
                                1293,
                                1294,
                                1295,
                                8355,
                                29227,
                                29229,
                                37329,
                                40778
                            );

                            for (Integer objectId : spiritTreeIds) {
                                gameObjectOverlay(
                                    objectId,
                                    leftClickColorWithAlpha
                                ).render(graphics);
                            }
                        } else {
                            Widget widget = client.getWidget(
                                187,
                                3
                            );

                            highlightDynamicComponent(
                                graphics,
                                widget,
                                getChildIndex(
                                    widget,
                                    teleport.overrideLocationName() == "" ? location.getName() : teleport.overrideLocationName()
                                )
                            );
                        }

                        if (currentRegionId == teleport.getRegionId()) {
                            currentTeleportCase = 1;
                            isAtDestination = true;
                            startSubCases = true;

                            if (location.getFarmLimps()) {
                                farmLimps = true;
                            }
                        }
                        break;
                    case JEWELLERY_BOX:
                        switch (currentTeleportCase) {
                            case 1:
                                gettingToHouse(graphics);
                                break;
                            case 2:
                                List<Integer> jewelleryBoxIds = Arrays.asList(
                                    29154,
                                    29155,
                                    29156
                                );

                                if (! isInterfaceOpen(
                                    590,
                                    0
                                )) {
                                    for (int id : jewelleryBoxIds) {
                                        gameObjectOverlay(
                                            id,
                                            leftClickColorWithAlpha
                                        ).render(graphics);
                                    }
                                    gameObjectOverlay(
                                        teleport.getId(),
                                        leftClickColorWithAlpha
                                    ).render(graphics);
                                } else {
                                    Widget widget = client.getWidget(
                                        590,
                                        5
                                    );
                                    highlightDynamicComponent(
                                        graphics,
                                        widget,
                                        10
                                    );
                                }
                                if (currentRegionId == teleport.getRegionId()) {
                                    currentTeleportCase = 1;
                                    isAtDestination = true;
                                    startSubCases = true;
                                    if (location.getFarmLimps()) {
                                        farmLimps = true;
                                    }
                                }
                                break;
                        }
                        break;
                    case MOUNTED_XERICS:
                        switch (currentTeleportCase) {
                            case 1:
                                gettingToHouse(graphics);
                                break;
                            case 2:
                                List<Integer> xericsTalismanIds = Arrays.asList(
                                    33411,
                                    33412,
                                    33413,
                                    33414,
                                    33415
                                );

                                if (! isInterfaceOpen(
                                    teleport.getInterfaceGroupId(),
                                    teleport.getInterfaceChildId()
                                )) {
                                    for (int id : xericsTalismanIds) {
                                        Overlay decorativeObjectHighlight = decorativeObjectOverlay(id);
                                        decorativeObjectHighlight.render(graphics);
                                    }
                                } else {
                                    Widget widget = client.getWidget(
                                        teleport.getInterfaceGroupId(),
                                        teleport.getInterfaceChildId()
                                    );
                                    highlightDynamicComponent(
                                        graphics,
                                        widget,
                                        1
                                    );
                                    if (currentRegionId == teleport.getRegionId()) {
                                        currentTeleportCase = 1;
                                        isAtDestination = true;
                                        startSubCases = true;
                                        if (location.getFarmLimps()) {
                                            farmLimps = true;
                                        }
                                    }
                                }
                                break;
                        }
                    case MOUNTED_DIGSITE:
                        switch (currentTeleportCase) {
                            case 1:
                                gettingToHouse(graphics);
                                break;
                            case 2:
                                List<Integer> digsitePendantIds = Arrays.asList(33416);

                                if (! isInterfaceOpen(
                                    teleport.getInterfaceGroupId(),
                                    teleport.getInterfaceChildId()
                                )) {
                                    for (int id : digsitePendantIds) {
                                        Overlay decorativeObjectHighlight = decorativeObjectOverlay(id);

                                        decorativeObjectHighlight.render(graphics);
                                    }
                                } else {
                                    Widget widget = client.getWidget(
                                        teleport.getInterfaceGroupId(),
                                        teleport.getInterfaceChildId()
                                    );

                                    highlightDynamicComponent(
                                        graphics,
                                        widget,
                                        1
                                    );

                                    if (currentRegionId == teleport.getRegionId()) {
                                        currentTeleportCase = 1;
                                        isAtDestination = true;
                                        startSubCases = true;

                                        if (location.getFarmLimps()) {
                                            farmLimps = true;
                                        }
                                    }
                                }

                                break;
                        }
                    case SPELLBOOK:
                        InventoryTabChecker.TabState tabState;
                        tabState = InventoryTabChecker.checkTab(
                            client,
                            VarClientInt.INVENTORY_TAB
                        );
                        switch (tabState) {
                            case REST:
                                if (currentRegionId == teleport.getRegionId()) {
                                    currentTeleportCase = 1;
                                    isAtDestination = true;
                                    startSubCases = true;
                                }
                            case INVENTORY:
                                interfaceOverlay(
                                    161,
                                    64
                                ).render(graphics);
                                if (currentRegionId == teleport.getRegionId()) {
                                    currentTeleportCase = 1;
                                    isAtDestination = true;
                                    startSubCases = true;
                                }
                                break;
                            case SPELLBOOK:
                                interfaceOverlay(
                                    teleport.getInterfaceGroupId(),
                                    teleport.getInterfaceChildId()
                                ).render(graphics);
                                if (currentRegionId == teleport.getRegionId()) {
                                    currentTeleportCase = 1;
                                    isAtDestination = true;
                                    startSubCases = true;
                                }
                                break;
                        }

                        break;
                    default:
                        // Optional: Code for handling unexpected values
                        break;
                }

            } else {
                farming(
                    graphics,
                    teleport
                );
            }
        } else {
            runIndex++;
        }
    }
    //}


    public static boolean farmLimps = false;

    public void farming(Graphics2D graphics, Location.Teleport teleport)
    {
        if (startSubCases) {
            if (herbRun) {
                if (subCase == 1) {
                    herbSteps(
                        graphics,
                        teleport
                    );
                    if (herbPatchDone) {
                        subCase = 2;
                        herbPatchDone = false;
                    }
                } else if (subCase == 2) {
                    if (config.generalLimpwurt()) {
                        flowerSteps(graphics);
                        if (flowerPatchDone) {
                            subCase = 1;
                            startSubCases = false;
                            isAtDestination = false;
                            runIndex++;
                            farmLimps = false;
                            flowerPatchDone = false;

                        }
                    } else {
                        subCase = 1;
                        startSubCases = false;
                        isAtDestination = false;
                        runIndex++;
                        farmLimps = false;
                        flowerPatchDone = false;
                    }
                }
            }
            if (treeRun) {
                treeSteps(
                    graphics,
                    teleport
                );
                if (treePatchDone) {
                    startSubCases = false;
                    isAtDestination = false;
                    runIndex++;
                    treePatchDone = false;
                }
            }
            if (fruitTreeRun) {
                fruitTreeSteps(
                    graphics,
                    teleport
                );
                if (fruitTreePatchDone) {
                    startSubCases = false;
                    isAtDestination = false;
                    runIndex++;
                    fruitTreePatchDone = false;
                }
            }
            if (hardwoodRun) {
                hardwoodSteps(graphics, teleport);
                if (hardwoodPatchDone) {
                    startSubCases = false;
                    isAtDestination = false;
                    runIndex++;
                    hardwoodPatchDone = false;
                }
            }
        }
    }

    public static int subCase = 1;
    public static boolean startSubCases = false;
    public static int runIndex = 0;

    public void RemoveOverlay()
    {
        plugin.overlayManager.remove(farmingHelperOverlay);
        plugin.overlayManager.remove(farmingTeleportOverlay);
        plugin.overlayManager.remove(farmingHelperOverlayInfoBox);

        plugin.setOverlayActive(false);
        plugin.setTeleportOverlayActive(false);

        runIndex = 0;
        currentTeleportCase = 1;
        subCase = 1;
        startSubCases = false;
        isAtDestination = false;
        farmLimps = false;
        flowerPatchDone = false;

        plugin.setItemsCollected(false);

        plugin.getFarmingTeleportOverlay().herbRun = false;
        plugin.getFarmingTeleportOverlay().treeRun = false;
        plugin.getFarmingTeleportOverlay().fruitTreeRun = false;
        plugin.getFarmingTeleportOverlay().hardwoodRun = false;

        fruitTreeRun = false;
        herbRun = false;
        treeRun = false;
        hardwoodRun = false;

        plugin.panel.herbButton.setStartStopState(false);
        plugin.panel.treeButton.setStartStopState(false);
        plugin.panel.fruitTreeButton.setStartStopState(false);
        plugin.panel.hardwoodButton.setStartStopState(false);
    }

    public Boolean herbRun = false;

    public Boolean treeRun = false;

    public Boolean fruitTreeRun = false;
    public Boolean hardwoodRun = false;

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (plugin.isTeleportOverlayActive()) {
            Client client = plugin.getClient();
            if (herbRun) {
                switch (runIndex) {
                    case 0:
                        gettingToLocation(
                            graphics,
                            plugin.getArdougneLocation()
                        );
                        break;
                    case 1:
                        gettingToLocation(
                            graphics,
                            plugin.getCatherbyLocation()
                        );
                        break;
                    case 2:
                        gettingToLocation(
                            graphics,
                            plugin.getFaladorLocation()
                        );
                        break;
                    case 3:
                        gettingToLocation(
                            graphics,
                            plugin.getFarmingGuildLocation()
                        );
                        break;
                    case 4:
                        gettingToLocation(
                            graphics,
                            plugin.getHarmonyLocation()
                        );
                        break;
                    case 5:
                        gettingToLocation(
                            graphics,
                            plugin.getKourendLocation()
                        );
                        break;
                    case 6:
                        gettingToLocation(
                            graphics,
                            plugin.getMorytaniaLocation()
                        );
                        break;
                    case 7:
                        gettingToLocation(
                            graphics,
                            plugin.getTrollStrongholdLocation()
                        );
                        break;
                    case 8:
                        gettingToLocation(
                            graphics,
                            plugin.getWeissLocation()
                        );
                        break;
                    case 9:
                        RemoveOverlay();
                        // add more cases for each location in the array
                    default:
                        RemoveOverlay();
                        // Add any other actions you want to perform when the herb run is complete
                        break;
                }
            } else if (treeRun) {
                switch (runIndex) {
                    case 0:
                        gettingToLocation(
                            graphics,
                            plugin.getFaladorTreeLocation()
                        );
                        break;
                    case 1:
                        gettingToLocation(
                            graphics,
                            plugin.getFarmingGuildTreeLocation()
                        );
                        break;
                    case 2:
                        gettingToLocation(
                            graphics,
                            plugin.getGnomeStrongholdTreeLocation()
                        );
                        break;
                    case 3:
                        gettingToLocation(
                            graphics,
                            plugin.getLumbridgeTreeLocation()
                        );
                        break;
                    case 4:
                        gettingToLocation(
                            graphics,
                            plugin.getTaverleyTreeLocation()
                        );
                        break;
                    case 5:
                        gettingToLocation(
                            graphics,
                            plugin.getVarrockTreeLocation()
                        );
                        break;
                    case 6:
                        gettingToLocation(
                            graphics,
                            plugin.getFossilIslandEastTreeLocation()
                        );
                        break;
                    case 7:
                        gettingToLocation(
                            graphics,
                            plugin.getFossilIslandSouthTreeLocation()
                        );
                        break;
                    case 8:
                        gettingToLocation(
                            graphics,
                            plugin.getFossilIslandWestTreeLocation()
                        );
                        break;
                    case 9:
                        RemoveOverlay();
                        // add more cases for each location in the array
                    default:
                        RemoveOverlay();
                        // Add any other actions you want to perform when the herb run is complete
                        break;
                }
            } else if (fruitTreeRun) {
                switch (runIndex) {
                    case 0:
                        gettingToLocation(
                            graphics,
                            plugin.getBrimhavenFruitTreeLocation()
                        );
                        break;
                    case 1:
                        gettingToLocation(
                            graphics,
                            plugin.getCatherbyFruitTreeLocation()
                        );
                        break;
                    case 2:
                        gettingToLocation(
                            graphics,
                            plugin.getFarmingGuildFruitTreeLocation()
                        );
                        break;
                    case 3:
                        gettingToLocation(
                            graphics,
                            plugin.getGnomeStrongholdFruitTreeLocation()
                        );
                        break;
                    case 4:
                        gettingToLocation(
                            graphics,
                            plugin.getLletyaFruitTreeLocation()
                        );
                        break;
                    case 5:
                        gettingToLocation(
                            graphics,
                            plugin.getTreeGnomeVillageTreeLocation()
                        );
                        break;
                    case 6:
                        RemoveOverlay();
                        // add more cases for each location in the array
                    default:
                        RemoveOverlay();
                        // Add any other actions you want to perform when the herb run is complete
                        break;
                }
            } else if (hardwoodRun) {
                switch (runIndex) {
                    case 0:
                        gettingToLocation(
                            graphics,
                            plugin.getHardwoodFossilIslandLocation()
                        );
                        break;
                    case 1:
                        RemoveOverlay();
                        // add more cases for each location in the array
                    default:
                        RemoveOverlay();
                        // Add any other actions you want to perform when the herb run is complete
                        break;
                }
            }
        }
        return null;
    }
}
