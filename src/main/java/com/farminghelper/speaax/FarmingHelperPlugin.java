package com.farminghelper.speaax;

import com.farminghelper.speaax.ItemsAndLocations.FruitTreeRunItemAndLocation;
import com.farminghelper.speaax.ItemsAndLocations.HerbRunItemAndLocation;
import com.farminghelper.speaax.ItemsAndLocations.TreeRunItemAndLocation;
import com.farminghelper.speaax.Patch.Location;
import com.google.inject.Provides;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@PluginDescriptor(name = "Lazy Farming", description = "Show item requirements and highlights for farming runs.")

public class FarmingHelperPlugin extends Plugin
{
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private HerbRunItemAndLocation herbRunItemAndLocation;

    private TreeRunItemAndLocation treeRunItemAndLocation;

    private FruitTreeRunItemAndLocation fruitTreeRunItemAndLocation;

    private boolean isTeleportOverlayActive = false;

    private String lastMessage = "";

    private FarmingHelperPanel farmingHelperPanel;

    public FarmingHelperPanel panel;

    private NavigationButton navButton;

    private boolean itemsCollected = false;

    private int lastAnimationId = - 1;

    private int lastClickedGroupId;

    private int lastClickedChildId;

    private boolean clicked = false;

    private boolean isOverlayActive = true;

    private Map<Integer, Integer> herbItemsCache;

    private Map<Integer, Integer> treeItemsCache;

    private Map<Integer, Integer> fruitTreeItemsCache;

    @Inject
    private ItemManager itemManager;

    @Inject
    private Client client;

    @Inject
    private FarmingHelperOverlayInfoBox farmingHelperOverlayInfoBox;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private FarmingHelperConfig config;

    @Inject
    public OverlayManager overlayManager;

    @Inject
    private FarmingHelperOverlay farmingHelperOverlay;

    @Inject
    private EventBus eventBus;

    @Inject
    private ClientThread clientThread;

    @Inject
    private FarmingTeleportOverlay farmingTeleportOverlay;

    public void runOnClientThread(Runnable task)
    {
        clientThread.invokeLater(task);
    }

    public boolean isTeleportOverlayActive()
    {
        return isTeleportOverlayActive;
    }

    public void setTeleportOverlayActive(boolean isTeleportOverlayActive)
    {
        this.isTeleportOverlayActive = isTeleportOverlayActive;
    }

    public FarmingHelperOverlayInfoBox getFarmingHelperOverlayInfoBox()
    {
        return farmingHelperOverlayInfoBox;
    }

    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        if (event.getType() == ChatMessageType.GAMEMESSAGE) {
            lastMessage = event.getMessage();
            System.out.println("Last game message updated: " + lastMessage);
        } else if (event.getType() == ChatMessageType.SPAM) {
            lastMessage = event.getMessage();
            System.out.println("Last spam message updated: " + lastMessage);
        }
    }

    public String getLastMessage()
    {
        return lastMessage;
    }

    public boolean checkMessage(String targetMessage, String lastMessage)
    {
        return lastMessage.trim().equalsIgnoreCase(targetMessage.trim());
    }

    public FarmingTeleportOverlay getFarmingTeleportOverlay()
    {
        return farmingTeleportOverlay;
    }

    public boolean isClicked(int groupId, int childId)
    {
        return clicked && groupId == lastClickedGroupId && childId == lastClickedChildId;
    }

    // "no usage" but currently needed for spellbook check
    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event)
    {
        clientThread.invokeLater(() -> {
            int groupId = event.getWidgetId() >>> 16;
            int childId = event.getWidgetId() & 0xFFFF;
            clicked = true;
            lastClickedGroupId = groupId;
            lastClickedChildId = childId;
            System.out.printf("Clicked widget: groupId=%d, childId=%d%n", groupId, childId);
        });
    }

    public FarmingHelperOverlay getFarmingHelperOverlay()
    {
        return farmingHelperOverlay;
    }

    public boolean areItemsCollected()
    {
        return itemsCollected;
    }

    public void setItemsCollected(boolean itemsCollected)
    {
        this.itemsCollected = itemsCollected;
    }

    public Client getClient()
    {
        return client;
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event)
    {
        if (client.getGameState() != GameState.LOGGED_IN || event.getActor() != client.getLocalPlayer()) {
            return;
        }

        int currentAnimationId = event.getActor().getAnimation();
        if (currentAnimationId != lastAnimationId) {
            System.out.println("Animation ID: " + currentAnimationId);
            lastAnimationId = currentAnimationId;
        }
    }

    public void updateHerbOverlay(Map<Integer, Integer> herbItems)
    {
        this.herbItemsCache = herbItems;
    }

    public void updateTreeOverlay(Map<Integer, Integer> treeItems)
    {
        this.treeItemsCache = treeItems;
    }

    public void updateFruitTreeOverlay(Map<Integer, Integer> fruitTreeItems)
    {
        this.fruitTreeItemsCache = fruitTreeItems;
    }

    @Provides
    FarmingHelperConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(FarmingHelperConfig.class);
    }

    public boolean isOverlayActive()
    {
        return isOverlayActive;
    }

    public void setOverlayActive(boolean overlayActive)
    {
        isOverlayActive = overlayActive;
    }

    public void addTextToInfoBox(String text)
    {
        farmingHelperOverlayInfoBox.setText(text);
    }

    public boolean getHerbLocationEnabled(String locationName)
    {
        switch (locationName) {
            case "Ardougne":
                return config.ardougneHerb();
            case "Catherby":
                return config.catherbyHerb();
            case "Falador":
                return config.faladorHerb();
            case "Farming Guild":
                return config.farmingGuildHerb();
            case "Harmony Island":
                return config.harmonyHerb();
            case "Kourend":
                return config.kourendHerb();
            case "Morytania":
                return config.morytaniaHerb();
            case "Troll Stronghold":
                return config.trollStrongholdHerb();
            case "Weiss":
                return config.weissHerb();
            // Add cases for other locations as needed
            default:
                return false;
        }
    }

    public boolean getTreeLocationEnabled(String locationName)
    {
        switch (locationName) {
            case "Falador":
                return config.faladorTree();
            case "Farming Guild":
                return config.farmingGuildTree();
            case "Gnome Stronghold":
                return config.gnomeStrongholdTree();
            case "Lumbridge":
                return config.lumbridgeTree();
            case "Taverley":
                return config.taverleyTree();
            case "Varrock":
                return config.varrockTree();
            // Add cases for other locations as needed
            default:
                return false;
        }
    }

    public boolean getFruitTreeLocationEnabled(String locationName)
    {
        switch (locationName) {
            case "Brimhaven":
                return config.brimhavenFruitTree();
            case "Catherby":
                return config.catherbyFruitTree();
            case "Farming Guild":
                return config.farmingGuildFruitTree();
            case "Gnome Stronghold":
                return config.gnomeStrongholdFruitTree();
            case "Lletya":
                return config.lletyaFruitTree();
            case "Tree Gnome Village":
                return config.treeGnomeVillageFruitTree();
            // Add cases for other locations as needed
            default:
                return false;
        }
    }

    @Override
    protected void startUp()
    {
        herbRunItemAndLocation = new HerbRunItemAndLocation(config, this);
        treeRunItemAndLocation = new TreeRunItemAndLocation(config, this);
        fruitTreeRunItemAndLocation = new FruitTreeRunItemAndLocation(config, this);

        farmingHelperOverlay = new FarmingHelperOverlay(
            client,
            this,
            itemManager,
            herbRunItemAndLocation,
            treeRunItemAndLocation,
            fruitTreeRunItemAndLocation
        );

        panel = new FarmingHelperPanel(
            this,
            overlayManager,
            farmingTeleportOverlay,
            herbRunItemAndLocation,
            treeRunItemAndLocation,
            fruitTreeRunItemAndLocation
        );

        final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/com/farminghelper/speaax/icon.png");

        navButton = NavigationButton.builder().tooltip("Lazy Farming").icon(icon).priority(6).panel(panel).build();

        clientToolbar.addNavigation(navButton);

        overlayManager.add(farmingHelperOverlay);
        overlayManager.add(farmingTeleportOverlay);
        overlayManager.add(farmingHelperOverlayInfoBox);

        // set overlay to inactive
        isOverlayActive = false;

        eventBus.register(this);
    }

    @Override
    protected void shutDown()
    {
        clientToolbar.removeNavigation(navButton);

        overlayManager.remove(farmingHelperOverlay);
        overlayManager.remove(farmingTeleportOverlay);
        overlayManager.remove(farmingHelperOverlayInfoBox);

        eventBus.unregister(this);
    }
}