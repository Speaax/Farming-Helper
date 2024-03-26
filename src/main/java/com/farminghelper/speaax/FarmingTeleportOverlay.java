package com.farminghelper.speaax;

import com.farminghelper.speaax.Helpers.AreaCheck;
import com.farminghelper.speaax.Helpers.Inventory;
import com.farminghelper.speaax.Patch.*;
import net.runelite.api.*;
import net.runelite.api.Item;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.util.ColorUtil;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;


public class FarmingTeleportOverlay extends Overlay
{
    private final Client client;

    private final FarmingHelperPlugin plugin;

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

    public Boolean patchComplete = false;

    public static int teleportStep = 1;

    public boolean isAtDestination = false;

    public static boolean farmLimpwurts = false;

    public static boolean checkForLimpwurts = false;

    public static boolean startSubCases = false;

    public static int runStep = 0;

    public PatchType runPatchType = null;

    private Color leftClickColorWithAlpha;
    private Color rightClickColorWithAlpha;
    private Color highlightUseItemWithAlpha;

    @Inject
    public FarmingTeleportOverlay(FarmingHelperPlugin plugin, Client client, AreaCheck areaCheck)
    {
        this.areaCheck = areaCheck;

        setPosition(OverlayPosition.DYNAMIC);

        setLayer(OverlayLayer.ABOVE_WIDGETS);

        this.plugin = plugin;
        this.client = client;
    }

    public void updateColors()
    {
        leftClickColorWithAlpha = new Color(config.highlightLeftClickColor().getRed(),
                                            config.highlightLeftClickColor().getGreen(),
                                            config.highlightLeftClickColor().getBlue(),
                                            config.highlightAlpha()
        );

        rightClickColorWithAlpha = new Color(config.highlightRightClickColor().getRed(),
                                             config.highlightRightClickColor().getGreen(),
                                             config.highlightRightClickColor().getBlue(),
                                             config.highlightAlpha()
        );

        highlightUseItemWithAlpha = new Color(config.highlightUseItemColor().getRed(),
                                              config.highlightUseItemColor().getGreen(),
                                              config.highlightUseItemColor().getBlue(),
                                              config.highlightAlpha()
        );
    }

    public boolean patchIsComposted()
    {
        String regexCompost1 = "You treat the (herb patch|flower patch|tree patch|fruit tree patch) with (compost|supercompost|ultracompost)\\.";
        String regexCompost2 = "This (herb patch|flower patch|tree patch|fruit tree patch) has already been treated with (compost|supercompost|ultracompost)\\.";

        return Pattern.compile(regexCompost1 + "|" + regexCompost2).matcher(plugin.getLastMessage()).matches();
    }

    public boolean patchIsProtected()
    {
        String standardResponse = "You pay the gardener ([0-9A-Za-z ]+) to protect the patch\\.";
        String faladorEliteResponse = "The gardener protects your tree for you, free of charge, as a token of gratitude for completing the ([A-Za-z ]+)\\.";

        return Pattern.compile(standardResponse + "|" + faladorEliteResponse).matcher(plugin.getLastMessage()).matches();
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
                    Widget widget = client.getWidget(groupId, childId);

                    if (widget != null) {
                        Rectangle bounds = widget.getBounds();

                        graphics.setColor(leftClickColorWithAlpha);

                        // Set the composite for transparency
                        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.65f);

                        graphics.setComposite(alphaComposite);

                        // Draw a rectangle over the widget
                        graphics.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

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

            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() / 5));

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
                        drawGameObjectClickbox(graphics, gameObject, color);
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

            for (Tile[][] value : tiles) {
                for (Tile[] item : value) {
                    for (Tile tile : item) {
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
                        drawDecorativeObjectClickbox(graphics, decorativeObject, leftClickColorWithAlpha);
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

            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));

            graphics.fill(clickbox);
        }
    }

    public void highlightRightClickOption(String option)
    {
        // Get the menu entries
        MenuEntry[] menuEntries = client.getMenuEntries();

        for (MenuEntry entry : menuEntries) {
            String optionText = entry.getOption();

            // Check if the option text matches the desired option
            if (optionText.equalsIgnoreCase(option)) {
                // Modify the menu entry to include a highlight
                String highlightedText = ColorUtil.prependColorTag(">>> " + optionText, rightClickColorWithAlpha);

                entry.setOption(highlightedText);
                client.setMenuEntries(menuEntries);

                break;
            }
        }
    }

    public void highlightNpc(Graphics2D graphics, String npcName)
    {
        List<NPC> npcs = client.getNpcs();

        if (npcs != null) {
            for (NPC npc : npcs) {
                if (npc != null && npc.getName() != null && npc.getName().equals(npcName)) {
                    Polygon tilePolygon = npc.getCanvasTilePoly();

                    if (tilePolygon != null) {
                        graphics.setColor(leftClickColorWithAlpha);
                        graphics.draw(tilePolygon);
                        //graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue()));
                        graphics.fill(tilePolygon);
                    }
                }
            }
        }
    }

    private boolean isInterfaceClosed(int groupId, int childId)
    {
        Widget widget = client.getWidget(groupId, childId);

        return widget == null || widget.isHidden();
    }

    public void highlightHerbPatches(Graphics2D graphics, Color color)
    {
        for (Integer patchId : farmingHelperOverlay.getHerbPatchIds()) {
            gameObjectOverlay(patchId, color).render(graphics);
        }
    }

    public void highlightFlowerPatches(Graphics2D graphics, Color color)
    {
        for (Integer patchId : farmingHelperOverlay.getFlowerPatchIds()) {
            gameObjectOverlay(patchId, color).render(graphics);
        }
    }

    public void highlightTreePatches(Graphics2D graphics, Color color)
    {
        for (Integer patchId : farmingHelperOverlay.getTreePatchIds()) {
            gameObjectOverlay(patchId, color).render(graphics);
        }
    }

    public void highlightFruitTreePatches(Graphics2D graphics, Color color)
    {
        for (Integer patchId : farmingHelperOverlay.getFruitTreePatchIds()) {
            gameObjectOverlay(patchId, color).render(graphics);
        }
    }

    public void highlightCompost(Graphics2D graphics)
    {
        if (isItemInInventory(selectedCompostID())) {
            switch (runPatchType) {
                case HERB:
                    if (checkForLimpwurts) {
                        highlightFlowerPatches(graphics, highlightUseItemWithAlpha);
                    } else {
                        highlightHerbPatches(graphics, highlightUseItemWithAlpha);
                    }
                    break;

                case TREE:
                    highlightTreePatches(graphics, highlightUseItemWithAlpha);
                    break;

                case FRUIT_TREE:
                    highlightFruitTreePatches(graphics, highlightUseItemWithAlpha);
                    break;
            }

            itemHighlight(graphics, selectedCompostID(), highlightUseItemWithAlpha);
        } else {
            withdrawCompost(graphics);
        }
    }

    public void highlightFarmers(Graphics2D graphics, List<String> farmers)
    {
        if (isInterfaceClosed(219, 1)) {
            for (String farmer : farmers) {
                highlightNpc(graphics, farmer);
            }
        } else {
            highlightDynamicComponent(graphics, client.getWidget(219, 1), 1);
        }
    }

    public void highlightTreeFarmers(Graphics2D graphics)
    {
        highlightFarmers(graphics, Arrays.asList(
            "Alain",         // Taverly
            "Fayeth",        // Lumbridge
            "Heskel",        // Falador
            "Prissy Scilla", // Gnome Stronghold
            "Rosie",         // Farming Guild
            "Treznor"        // Varrock
        ));
    }

    public void highlightFruitTreeFarmers(Graphics2D graphics)
    {
        highlightFarmers(graphics, Arrays.asList(
            "Bolongo", // Gnome Stronghold
            "Ellena",  // Catherby
            "Garth",   // Brimhaven
            "Gileth",  // Tree Gnome Village
            "Liliwen", // Lletya
            "Nikkie"   // Farming Guild
        ));
    }

    public void highlightHerbSeeds(Graphics2D graphics)
    {
        for (Integer seedId : farmingHelperOverlay.getHerbSeedIds()) {
            itemHighlight(graphics, seedId, highlightUseItemWithAlpha);
        }
    }

    public void highlightTreeSapling(Graphics2D graphics)
    {
        for (Integer seedId : farmingHelperOverlay.getTreeSaplingIds()) {
            itemHighlight(graphics, seedId, highlightUseItemWithAlpha);
        }
    }

    public void highlightFruitTreeSapling(Graphics2D graphics)
    {
        for (Integer seedId : farmingHelperOverlay.getFruitTreeSaplingIds()) {
            itemHighlight(graphics, seedId, highlightUseItemWithAlpha);
        }
    }

    public void highlightTeleportCrystal(Graphics2D graphics)
    {
        for (Integer seedId : farmingHelperOverlay.getTeleportCrystalIdsIds()) {
            itemHighlight(graphics, seedId, leftClickColorWithAlpha);
        }
    }

    public void highlightSkillsNecklace(Graphics2D graphics)
    {
        for (Integer seedId : farmingHelperOverlay.getSkillsNecklaceIdsIds()) {
            itemHighlight(graphics, seedId, leftClickColorWithAlpha);
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

        if (inventory == null) {
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

        if (isInterfaceClosed(125, 0)) {
            highlightNpc(graphics, "Tool Leprechaun");
        } else {
            switch (selectedCompostID()) {
                case ItemID.COMPOST:
                    interfaceOverlay(125, 17).render(graphics);
                    break;

                case ItemID.SUPERCOMPOST:
                    interfaceOverlay(125, 18).render(graphics);
                    break;

                case ItemID.ULTRACOMPOST:
                    interfaceOverlay(125, 19).render(graphics);
                    break;

                case ItemID.BOTTOMLESS_COMPOST_BUCKET_22997:
                    interfaceOverlay(125, 15).render(graphics);
                    break;
            }
        }
    }

    public void herbSteps(Graphics2D graphics, Location location)
    {
        HerbPatchChecker.PlantState plantState;

        switch (client.getLocalPlayer().getWorldLocation().getRegionID()) {
            case 4922:
                // Farming guild herb patch uses Varbits.FARMING_4775
                plantState = HerbPatchChecker.checkHerbPatch(client, Varbits.FARMING_4775);
                break;

            case 15148:
                // Harmony herb patch uses Varbits.FARMING_4772
                plantState = HerbPatchChecker.checkHerbPatch(client, Varbits.FARMING_4772);
                break;

            case 11321:
            case 11325:
                // Troll Stronghold and Weiss herb patch uses Varbits.FARMING_4771
                plantState = HerbPatchChecker.checkHerbPatch(client, Varbits.FARMING_4771);
                break;

            default:
                // Rest uses Varbits.FARMING_4774
                plantState = HerbPatchChecker.checkHerbPatch(client, Varbits.FARMING_4774);
                break;
        }

        if (! areaCheck.isPlayerWithinArea(location.patch(runPatchType).worldPoint(), 15)) {
            //should be replaced with a pathing system, pointing arrow or something else eventually
            highlightHerbPatches(graphics, leftClickColorWithAlpha);
        } else {
            switch (plantState) {
                case HARVESTABLE:
                    plugin.addTextToInfoBox("Harvest Herbs.");
                    highlightHerbPatches(graphics, leftClickColorWithAlpha);
                    break;

                case PLANT:
                    plugin.addTextToInfoBox("Use Herb seed on patch.");
                    highlightHerbPatches(graphics, highlightUseItemWithAlpha);
                    highlightHerbSeeds(graphics);
                    break;

                case DEAD:
                    plugin.addTextToInfoBox("Clear the dead herb patch.");
                    highlightHerbPatches(graphics, leftClickColorWithAlpha);
                    break;

                case DISEASED:
                    plugin.addTextToInfoBox(
                        "Use Plant cure on herb patch. Buy at GE or in farming guild/catherby, and store at Tool Leprechaun for easy access.");
                    highlightHerbPatches(graphics, leftClickColorWithAlpha);
                    itemHighlight(graphics, ItemID.PLANT_CURE, highlightUseItemWithAlpha);
                    break;

                case WEEDS:
                    plugin.addTextToInfoBox("Rake the herb patch.");
                    highlightHerbPatches(graphics, leftClickColorWithAlpha);
                    break;

                case GROWING:
                    plugin.addTextToInfoBox("Use Compost on patch.");

                    highlightCompost(graphics);

                    if (patchIsComposted()) {
                        patchComplete = true;
                    }

                    break;

                case UNKNOWN:
                    plugin.addTextToInfoBox(
                        "UNKNOWN state: Try to do something with the herb patch to change its state.");
                    break;
            }
        }
    }

    public void flowerSteps(Graphics2D graphics)
    {
        if (farmLimpwurts) {
            FlowerPatchChecker.PlantState plantState;

            if (client.getLocalPlayer().getWorldLocation().getRegionID() == 4922) {
                plantState = FlowerPatchChecker.checkFlowerPatch(client, Varbits.FARMING_7906);
            } else {
                plantState = FlowerPatchChecker.checkFlowerPatch(client, Varbits.FARMING_4773);
            }

            switch (plantState) {
                case HARVESTABLE:
                    plugin.addTextToInfoBox("Harvest Limpwurt root.");
                    highlightFlowerPatches(graphics, leftClickColorWithAlpha);
                    break;

                case WEEDS:
                    plugin.addTextToInfoBox("Rake the flower patch.");
                    highlightFlowerPatches(graphics, leftClickColorWithAlpha);
                    break;

                case DEAD:
                    plugin.addTextToInfoBox("Clear the dead flower patch.");
                    highlightFlowerPatches(graphics, leftClickColorWithAlpha);
                    break;

                case PLANT:
                    plugin.addTextToInfoBox("Use Limpwurt seed on the patch.");
                    highlightFlowerPatches(graphics, highlightUseItemWithAlpha);
                    itemHighlight(graphics, ItemID.LIMPWURT_SEED, highlightUseItemWithAlpha);
                    break;

                case GROWING:
                    plugin.addTextToInfoBox("Use Compost on patch.");

                    highlightCompost(graphics);

                    if (patchIsComposted()) {
                        patchComplete = true;
                    }

                    break;
            }
        } else {
            patchComplete = true;
        }
    }

    public void treeSteps(Graphics2D graphics, Location location)
    {
        TreePatchChecker.PlantState plantState;

        if (client.getLocalPlayer().getWorldLocation().getRegionID() == 4922) {
            // 7905 farming guild
            plantState = TreePatchChecker.checkTreePatch(client, Varbits.FARMING_7905);
        } else {
            // 4771 falador, gnome stronghold, lumbridge, Taverly, Varrock
            plantState = TreePatchChecker.checkTreePatch(client, Varbits.FARMING_4771);
        }

        System.out.println("Tree plant state: " + plantState);

        if (! areaCheck.isPlayerWithinArea(location.patch(runPatchType).worldPoint(), 15)) {
            System.out.println("Not there yet! " + location.patch(runPatchType).worldPoint());

            //should be replaced with a pathing system, pointing arrow or something else eventually
            highlightTreePatches(graphics, leftClickColorWithAlpha);
        } else {
            System.out.println("Arrived! Tree plant state: " + plantState);

            switch (plantState) {
                case HEALTHY:
                    plugin.addTextToInfoBox("Check tree health.");
                    highlightTreePatches(graphics, leftClickColorWithAlpha);
                    break;

                case WEEDS:
                    plugin.addTextToInfoBox("Rake the tree patch.");
                    highlightTreePatches(graphics, leftClickColorWithAlpha);
                    break;

                case DEAD:
                    plugin.addTextToInfoBox("Clear the dead tree patch.");
                    highlightTreePatches(graphics, leftClickColorWithAlpha);
                    break;

                case PLANT:
                    plugin.addTextToInfoBox("Use Sapling on the patch.");
                    highlightTreePatches(graphics, highlightUseItemWithAlpha);
                    highlightTreeSapling(graphics);
                    break;

                case DISEASED:
                    plugin.addTextToInfoBox("Prune the tree patch patch.");
                    highlightTreePatches(graphics, highlightUseItemWithAlpha);
                    break;

                case REMOVE:
                    plugin.addTextToInfoBox("Pay to remove tree, or cut it down and clear the patch.");
                    highlightTreeFarmers(graphics);
                    break;

                case UNKNOWN:
                    plugin.addTextToInfoBox(
                        "UNKNOWN state: Try to do something with the tree patch to change its state.");
                    break;

                case GROWING:
                    if (config.generalPayForProtection()) {
                        plugin.addTextToInfoBox("Pay to protect the patch.");

                        highlightTreeFarmers(graphics);

                        if (patchIsProtected()) {
                            patchComplete = true;
                        }
                    } else {
                        plugin.addTextToInfoBox("Use Compost on patch.");

                        highlightCompost(graphics);

                        if (patchIsComposted()) {
                            patchComplete = true;
                        }
                    }

                    break;
            }
        }
    }

    public void fruitTreeSteps(Graphics2D graphics, Location location)
    {
        // ABSTRACT: -- abstract this one level above and pass in the cropState
        // TODO: Also apply the cropState change to Herbs, Flowers and Trees
        CropState cropState;

        switch (client.getLocalPlayer().getWorldLocation().getRegionID()) {
            case 4922:
                // Varbits.FARMING_7909 farming guild
                cropState = PatchState.check(client, Varbits.FARMING_7909);
                break;

            case 9781:
            case 9782:
                // Varbits.FARMING_4772 gnome stronghold
                cropState = PatchState.check(client, Varbits.FARMING_4772);
                break;

            default:
                // Varbits.FARMING_4771 brimhaven, catherby, lletya, tree gnome village
                cropState = PatchState.check(client, Varbits.FARMING_4771);
                break;
        }
        // END ABSTRACT

        if (! areaCheck.isPlayerWithinArea(location.patch(runPatchType).worldPoint(), 15)) {
            //should be replaced with a pathing system, point arrow or something else eventually
            highlightFruitTreePatches(graphics, leftClickColorWithAlpha);
        } else {
            switch (cropState) {
                case WEEDS:
                    plugin.addTextToInfoBox("Rake the fruit tree patch.");
                    highlightFruitTreePatches(graphics, leftClickColorWithAlpha);
                    break;

                case PLANT:
                    plugin.addTextToInfoBox("Use Sapling on the patch.");
                    highlightFruitTreePatches(graphics, highlightUseItemWithAlpha);
                    highlightFruitTreeSapling(graphics);
                    break;

                case GROWING:
                    if (config.generalPayForProtection()) {
                        plugin.addTextToInfoBox("Pay to protect the patch.");
                        highlightFruitTreeFarmers(graphics);

                        if (patchIsProtected()) {
                            patchComplete = true;
                        }
                    } else {
                        plugin.addTextToInfoBox("Use Compost on patch.");
                        highlightCompost(graphics);

                        if (patchIsComposted()) {
                            patchComplete = true;
                        }
                    }

                    break;

                case DISEASED:
                    plugin.addTextToInfoBox("Prune the fruit tree patch.");
                    highlightFruitTreePatches(graphics, leftClickColorWithAlpha);
                    break;

                case DEAD:
                    plugin.addTextToInfoBox("Clear the dead fruit tree patch.");
                    highlightFruitTreePatches(graphics, leftClickColorWithAlpha);
                    break;

                case HARVESTABLE:
                    if (config.generalPickFruit()) {
                        plugin.addTextToInfoBox("Pick the produce from the fruit tree.");
                        highlightFruitTreePatches(graphics, leftClickColorWithAlpha);
                    } else {
                        plugin.addTextToInfoBox("Pay to remove fruit tree, or cut it down and clear the patch.");
                        highlightFruitTreeFarmers(graphics);
                    }
                    break;

                case HARVESTED:
                case REMOVE:
                    plugin.addTextToInfoBox("Pay to remove fruit tree, or cut it down and clear the patch.");
                    highlightFruitTreeFarmers(graphics);
                    break;

                case GROWN:
                    plugin.addTextToInfoBox("Check Fruit tree health.");
                    highlightFruitTreePatches(graphics, leftClickColorWithAlpha);
                    break;

                case UNKNOWN:
                    plugin.addTextToInfoBox(
                        "UNKNOWN state: Try to do something with the tree patch to change its state.");
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
            teleportStep = 2;
        }
    }

    public void gettingToHouse(Graphics2D graphics)
    {
        FarmingHelperConfig.OptionEnumHouseTele teleportOption = config.enumConfigHouseTele();

        switch (teleportOption) {
            case Law_air_earth_runes:
                Inventory.Tab tab = Inventory.getCurrentTab(client);

                switch (tab) {
                    case INVENTORY:
                    case REST:
                        interfaceOverlay(161, 64).render(graphics);
                        break;

                    case SPELLBOOK:
                        interfaceOverlay(218, 29).render(graphics);
                        inHouseCheck();
                        break;
                }

            case Teleport_To_House:
                inHouseCheck();
                itemHighlight(graphics, ItemID.TELEPORT_TO_HOUSE, leftClickColorWithAlpha);
                break;

            case Construction_cape:
                inHouseCheck();
                itemHighlight(graphics, ItemID.CONSTRUCT_CAPE, rightClickColorWithAlpha);
                break;

            case Construction_cape_t:
                inHouseCheck();
                itemHighlight(graphics, ItemID.CONSTRUCT_CAPET, rightClickColorWithAlpha);
                break;

            case Max_cape:
                inHouseCheck();
                itemHighlight(graphics, ItemID.MAX_CAPE, rightClickColorWithAlpha);
                break;
        }
    }

    // TODO: Migrate this method to Location.<LOCATION>.navigate(...) ???
    public void gettingToLocation(Graphics2D graphics, Location location)
    {
        updateColors();

        Teleport teleport = location.desiredTeleport(runPatchType, config);

        boolean locationEnabled = false;

        switch (runPatchType) {
            case HERB:
                locationEnabled = plugin.getHerbLocationEnabled(location.getName());
                break;

            case TREE:
                locationEnabled = plugin.getTreeLocationEnabled(location.getName());
                break;

            case FRUIT_TREE:
                locationEnabled = plugin.getFruitTreeLocationEnabled(location.getName());
                break;
        }

        if (locationEnabled) {
            if (! isAtDestination) {
                int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();

                plugin.addTextToInfoBox(teleport.getDescription());

                switch (teleport.getCategory()) {
                    case ITEM:
                        if (teleport.getInterfaceGroupId() != 0) {
                            if (isInterfaceClosed(teleport.getInterfaceGroupId(), teleport.getInterfaceChildId())) {
                                itemHighlight(graphics, teleport.getId(), rightClickColorWithAlpha);

                                if (! teleport.getRightClickOption().equals("null")) {
                                    highlightRightClickOption(teleport.getRightClickOption());
                                }
                            } else {
                                Widget widget = client.getWidget(teleport.getInterfaceGroupId(),
                                                                 teleport.getInterfaceChildId()
                                );

                                highlightDynamicComponent(graphics, widget, 1);
                            }

                        } else {
                            if (! teleport.getRightClickOption().equals("null")) {
                                itemHighlight(graphics, teleport.getId(), rightClickColorWithAlpha);
                                highlightRightClickOption(teleport.getRightClickOption());
                            } else {
                                if (teleport.getId() == ItemID.TELEPORT_CRYSTAL_1) {
                                    highlightTeleportCrystal(graphics);
                                }

                                if (teleport.getId() == ItemID.SKILLS_NECKLACE1) {
                                    String index = location.getName();

                                    if (Objects.equals(index, "Ardougne")) {
                                        highlightSkillsNecklace(graphics);
                                        highlightRightClickOption("Rub");
                                        Widget widget = client.getWidget(187, 3);
                                        highlightDynamicComponent(graphics, widget, 0);
                                    }

                                    if (Objects.equals(index, "Farming Guild")) {
                                        highlightSkillsNecklace(graphics);
                                        highlightRightClickOption("Rub");
                                        Widget widget = client.getWidget(187, 3);
                                        highlightDynamicComponent(graphics, widget, 5);
                                    }
                                } else {
                                    itemHighlight(graphics, teleport.getId(), leftClickColorWithAlpha);
                                }
                            }

                        }

                        if (currentRegionId == teleport.getRegionId()) {
                            destinationReached();

                            if (location.patch(runPatchType).shouldFarmLimpwurts()) {
                                farmLimpwurts = true;
                            }
                        }

                        break;

                    case PORTAL_NEXUS:
                        switch (teleportStep) {
                            case 1:
                                gettingToHouse(graphics);
                                break;

                            case 2:
                                if (isInterfaceClosed(17, 0)) {
                                    List<Integer> portalNexusIds = getGameObjectIdsByName("Portal Nexus");

                                    for (Integer objectId : portalNexusIds) {
                                        gameObjectOverlay(objectId, leftClickColorWithAlpha).render(graphics);
                                    }
                                } else {
                                    Widget widget = client.getWidget(17, 13);

                                    highlightDynamicComponent(
                                        graphics,
                                        widget,
                                        getChildIndex(
                                            client.getWidget(17, 12),
                                            teleport.overrideLocationName().equals("") ? location.getName() : teleport.overrideLocationName()
                                        )
                                    );
                                }

                                if (currentRegionId == teleport.getRegionId()) {
                                    destinationReached();

                                    if (location.patch(runPatchType).shouldFarmLimpwurts()) {
                                        farmLimpwurts = true;
                                    }
                                }

                                break;
                        }

                        break;

                    case SPIRIT_TREE:
                        if (isInterfaceClosed(187, 3)) {
                            List<Integer> spiritTreeIds = Arrays.asList(1293,
                                                                        1294,
                                                                        1295,
                                                                        8355,
                                                                        29227,
                                                                        29229,
                                                                        37329,
                                                                        40778
                            );

                            for (Integer objectId : spiritTreeIds) {
                                gameObjectOverlay(objectId, leftClickColorWithAlpha).render(graphics);
                            }
                        } else {
                            Widget widget = client.getWidget(187, 3);

                            highlightDynamicComponent(
                                graphics,
                                widget,
                                getChildIndex(
                                    widget,
                                    teleport.overrideLocationName().equals("") ? location.getName() : teleport.overrideLocationName()
                                )
                            );
                        }

                        if (currentRegionId == teleport.getRegionId()) {
                            destinationReached();

                            if (location.patch(runPatchType).shouldFarmLimpwurts()) {
                                farmLimpwurts = true;
                            }
                        }

                        break;

                    case JEWELLERY_BOX:
                        switch (teleportStep) {
                            case 1:
                                gettingToHouse(graphics);
                                break;

                            case 2:
                                List<Integer> jewelleryBoxIds = Arrays.asList(29154, 29155, 29156);

                                if (isInterfaceClosed(590, 0)) {
                                    for (int id : jewelleryBoxIds) {
                                        gameObjectOverlay(id, leftClickColorWithAlpha).render(graphics);
                                    }

                                    gameObjectOverlay(teleport.getId(), leftClickColorWithAlpha).render(graphics);
                                } else {
                                    highlightDynamicComponent(graphics, client.getWidget(590, 5), 10);
                                }

                                if (currentRegionId == teleport.getRegionId()) {
                                    destinationReached();

                                    if (location.patch(runPatchType).shouldFarmLimpwurts()) {
                                        farmLimpwurts = true;
                                    }
                                }

                                break;
                        }

                        break;

                    case MOUNTED_XERICS:
                        switch (teleportStep) {
                            case 1:
                                gettingToHouse(graphics);
                                break;

                            case 2:
                                List<Integer> xericsTalismanIds = Arrays.asList(33411, 33412, 33413, 33414, 33415);

                                if (isInterfaceClosed(teleport.getInterfaceGroupId(), teleport.getInterfaceChildId())) {
                                    for (int id : xericsTalismanIds) {
                                        Overlay decorativeObjectHighlight = decorativeObjectOverlay(id);
                                        decorativeObjectHighlight.render(graphics);
                                    }
                                } else {
                                    Widget widget = client.getWidget(
                                        teleport.getInterfaceGroupId(),
                                        teleport.getInterfaceChildId()
                                    );

                                    highlightDynamicComponent(graphics, widget, 1);

                                    if (currentRegionId == teleport.getRegionId()) {
                                        destinationReached();

                                        if (location.patch(runPatchType).shouldFarmLimpwurts()) {
                                            farmLimpwurts = true;
                                        }
                                    }
                                }

                                break;
                        }

                    case SPELLBOOK:
                        Inventory.Tab tab = Inventory.getCurrentTab(client);

                        switch (tab) {
                            case REST:

                            case INVENTORY:
                                interfaceOverlay(161, 64).render(graphics);

                                break;

                            case SPELLBOOK:
                                interfaceOverlay(
                                    teleport.getInterfaceGroupId(),
                                    teleport.getInterfaceChildId()
                                ).render(graphics);

                                break;
                        }

                        if (currentRegionId == teleport.getRegionId()) {
                            destinationReached();
                        }

                        break;

                    default:
                        // Optional: Code for handling unexpected values
                        break;
                }

            } else {
                farming(graphics, location);
            }
        } else {
            runStep++;
        }
    }

    public void destinationReached()
    {
        teleportStep = 1;
        isAtDestination = true;
        startSubCases = true;
    }

    public void nextPatch()
    {
        patchComplete = false;
        isAtDestination = false;
        startSubCases = false;
        farmLimpwurts = false;

        runStep++;
    }

    public void farming(Graphics2D graphics, Location location)
    {
        if (startSubCases) {
            switch (runPatchType) {
                case HERB:
                    if (checkForLimpwurts) {
                        if (config.generalLimpwurt()) {
                            flowerSteps(graphics);

                            if (patchComplete) {
                                checkForLimpwurts = false;

                                nextPatch();
                            }
                        } else {
                            checkForLimpwurts = false;

                            nextPatch();
                        }
                    } else {
                        herbSteps(graphics, location);

                        if (patchComplete) {
                            checkForLimpwurts = true;
                            patchComplete = false;
                        }
                    }
                    break;

                case TREE:
                    treeSteps(graphics, location);

                    if (patchComplete) {
                        nextPatch();
                    }
                    break;

                case FRUIT_TREE:
                    fruitTreeSteps(graphics, location);

                    if (patchComplete) {
                        nextPatch();
                    }
                    break;
            }
        }
    }

    public void RemoveOverlay()
    {
        plugin.overlayManager.remove(farmingHelperOverlay);
        plugin.overlayManager.remove(farmingTeleportOverlay);
        plugin.overlayManager.remove(farmingHelperOverlayInfoBox);

        plugin.setOverlayActive(false);
        plugin.setTeleportOverlayActive(false);

        nextPatch();

        runStep = 0;
        teleportStep = 1;

        checkForLimpwurts = false;

        plugin.setItemsCollected(false);

        runPatchType = null;

        plugin.panel.herbButton.setStartStopState(false);
        plugin.panel.treeButton.setStartStopState(false);
        plugin.panel.fruitTreeButton.setStartStopState(false);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        // TODO: Pull the navigateTo() from the Location itself, passing in the runPatchType

        if (plugin.isTeleportOverlayActive()) {
            if (runPatchType == PatchType.HERB) {
                switch (runStep) {
                    case 0:
                        gettingToLocation(graphics, Location.ARDOUGNE);
                        break;

                    case 1:
                        gettingToLocation(graphics, Location.CATHERBY);
                        break;

                    case 2:
                        gettingToLocation(graphics, Location.FALADOR);
                        break;

                    case 3:
                        gettingToLocation(graphics, Location.FARMING_GUILD);
                        break;

                    case 4:
                        gettingToLocation(graphics, Location.HARMONY_ISLAND);
                        break;

                    case 5:
                        gettingToLocation(graphics, Location.KOUREND);
                        break;

                    case 6:
                        gettingToLocation(graphics, Location.MORYTANIA);
                        break;

                    case 7:
                        gettingToLocation(graphics, Location.TROLL_STRONGHOLD);
                        break;

                    case 8:
                        gettingToLocation(graphics, Location.WEISS);
                        break;

                    case 9:
                    default:
                        RemoveOverlay();
                        // Add any other actions you want to perform when the herb run is complete
                        break;
                }
            }
            else if (runPatchType == PatchType.TREE) {
                switch (runStep) {
                    case 0:
                        gettingToLocation(graphics, Location.FALADOR);
                        break;

                    case 1:
                        gettingToLocation(graphics, Location.TAVERLY);
                        break;

                    case 2:
                        gettingToLocation(graphics, Location.GNOME_STRONGHOLD);
                        break;

                    case 3:
                        gettingToLocation(graphics, Location.LUMBRIDGE);
                        break;

                    case 4:
                        gettingToLocation(graphics, Location.VARROCK);
                        break;

                    case 5:
                        gettingToLocation(graphics, Location.FARMING_GUILD);
                        break;

                    case 6:
                    default:
                        RemoveOverlay();
                        // Add any other actions you want to perform when the herb run is complete
                        break;
                }
            }
            else if (runPatchType == PatchType.FRUIT_TREE) {
                switch (runStep) {
                    case 0:
                        gettingToLocation(graphics, Location.BRIMHAVEN);
                        break;

                    case 1:
                        gettingToLocation(graphics, Location.CATHERBY);
                        break;

                    case 2:
                        gettingToLocation(graphics, Location.GNOME_STRONGHOLD);
                        break;

                    case 3:
                        gettingToLocation(graphics, Location.TREE_GNOME_VILLAGE);
                        break;

                    case 4:
                        gettingToLocation(graphics, Location.LLETYA);
                        break;

                    case 5:
                        gettingToLocation(graphics, Location.FARMING_GUILD);
                        break;

                    case 6:
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
