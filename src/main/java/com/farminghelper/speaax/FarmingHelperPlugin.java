package com.farminghelper.speaax;

import com.farminghelper.speaax.ItemsAndLocations.HerbRunItemAndLocation;
import com.farminghelper.speaax.ItemsAndLocations.TreeRunItemAndLocation;
import com.farminghelper.speaax.ItemsAndLocations.FruitTreeRunItemAndLocation;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.*;
import javax.inject.Inject;

import net.runelite.api.*;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.callback.ClientThread;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.eventbus.EventBus;

@PluginDescriptor(
		name = "Lazy Farming",
		description = "Show item requirements and highlights for farming runs."
)

public class FarmingHelperPlugin extends Plugin
{
	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	private HerbRunItemAndLocation herbRunItemAndLocation;
	private TreeRunItemAndLocation treeRunItemAndLocation;
	private FruitTreeRunItemAndLocation fruitTreeRunItemAndLocation;


	@Inject
	private ItemManager itemManager;
	@Inject
	private Client client;

	public void runOnClientThread(Runnable task) {
		clientThread.invokeLater(task);
	}

	public Location getArdougneLocation() {
		return herbRunItemAndLocation.ardougneLocation;
	}
	public Location getCatherbyLocation() {
		return herbRunItemAndLocation.catherbyLocation;
	}
	public Location getFaladorLocation() {
		return herbRunItemAndLocation.faladorLocation;
	}
	public Location getFarmingGuildLocation() {return herbRunItemAndLocation.farmingGuildLocation;}
	public Location getHarmonyLocation() {
		return herbRunItemAndLocation.harmonyLocation;
	}
	public Location getKourendLocation() {
		return herbRunItemAndLocation.kourendLocation;
	}
	public Location getMorytaniaLocation() {
		return herbRunItemAndLocation.morytaniaLocation;
	}
	public Location getTrollStrongholdLocation() {
		return herbRunItemAndLocation.trollStrongholdLocation;
	}

	public Location getWeissLocation() {
		return herbRunItemAndLocation.weissLocation;
	}

	//get Tree locations
	public Location getFaladorTreeLocation() {return treeRunItemAndLocation.faladorTreeLocation;}
	public Location getFarmingGuildTreeLocation() {
		return treeRunItemAndLocation.farmingGuildTreeLocation;
	}
	public Location getGnomeStrongholdTreeLocation() {return treeRunItemAndLocation.gnomeStrongholdTreeLocation;}
	public Location getLumbridgeTreeLocation() {return treeRunItemAndLocation.lumbridgeTreeLocation;}
	public Location getTaverleyTreeLocation() {
		return treeRunItemAndLocation.taverleyTreeLocation;
	}
	public Location getVarrockTreeLocation() {
		return treeRunItemAndLocation.varrockTreeLocation;
	}

	//get fruit tree locations
	public Location getBrimhavenFruitTreeLocation() {return fruitTreeRunItemAndLocation.brimhavenFruitTreeLocation;}
	public Location getCatherbyFruitTreeLocation() {return fruitTreeRunItemAndLocation.catherbyFruitTreeLocation;}
	public Location getFarmingGuildFruitTreeLocation() {return fruitTreeRunItemAndLocation.farmingGuildFruitTreeLocation;}
	public Location getGnomeStrongholdFruitTreeLocation() {return fruitTreeRunItemAndLocation.gnomeStrongholdFruitTreeLocation;}
	public Location getLletyaFruitTreeLocation() {return fruitTreeRunItemAndLocation.lletyaFruitTreeLocation;}
	public Location getTreeGnomeVillageTreeLocation() {return fruitTreeRunItemAndLocation.treeGnomeVillageFruitTreeLocation;}

	private boolean isTeleportOverlayActive = false;
	public boolean isTeleportOverlayActive() {
		return isTeleportOverlayActive;
	}
	public void setTeleportOverlayActive(boolean isTeleportOverlayActive) {
		this.isTeleportOverlayActive = isTeleportOverlayActive;
	}
	@Inject
	private FarmingHelperOverlayInfoBox farmingHelperOverlayInfoBox;
	public FarmingHelperOverlayInfoBox getFarmingHelperOverlayInfoBox()
	{
		return farmingHelperOverlayInfoBox;
	}

	private String lastMessage = "";
	@Subscribe
	public void onChatMessage(ChatMessage event) {
		if (event.getType() == ChatMessageType.GAMEMESSAGE) {
			lastMessage = event.getMessage();
			System.out.println("Last game message updated: " + lastMessage);
		}
		else if (event.getType() == ChatMessageType.SPAM) {
			lastMessage = event.getMessage();
			System.out.println("Last spam message updated: " + lastMessage);
		}
	}

	public String getLastMessage() {
		return lastMessage;
	}
	public boolean checkMessage(String targetMessage, String lastMessage) {
		return lastMessage.trim().equalsIgnoreCase(targetMessage.trim());
	}

	@Inject
	private EventBus eventBus;
	/*
	@Inject
	private Client client;

	 */
	@Inject
	private ClientThread clientThread;


	@Inject
	private FarmingTeleportOverlay farmingTeleportOverlay;
	public FarmingTeleportOverlay getFarmingTeleportOverlay()
	{
		return farmingTeleportOverlay;
	}
	private int lastClickedGroupId;
	private int lastClickedChildId;
	private boolean clicked = false;

	public boolean isClicked(int groupId, int childId) {
		return clicked && groupId == lastClickedGroupId && childId == lastClickedChildId;
	}

	//"no usage" but currently needed for spellbook check
	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event) {
		clientThread.invokeLater(() -> {
			int groupId = event.getWidgetId() >>> 16;
			int childId = event.getWidgetId() & 0xFFFF;
			clicked = true;
			lastClickedGroupId = groupId;
			lastClickedChildId = childId;
			System.out.printf("Clicked widget: groupId=%d, childId=%d%n", groupId, childId);
		});
	}


	private FarmingHelperPanel farmingHelperPanel;
	public FarmingHelperPanel panel;
	private NavigationButton navButton;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private FarmingHelperConfig config;
	@Inject
	public OverlayManager overlayManager;

	private boolean isOverlayActive = true;

	@Inject
	private FarmingHelperOverlay farmingHelperOverlay;

	public FarmingHelperOverlay getFarmingHelperOverlay()
	{
		return farmingHelperOverlay;
	}

	private boolean itemsCollected = false;
	public boolean areItemsCollected() {
		return itemsCollected;
	}

	public void setItemsCollected(boolean itemsCollected) {
		this.itemsCollected = itemsCollected;
	}
	public Client getClient() {
		return client;
	}

	private int lastAnimationId = -1;


	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		if (client.getGameState() != GameState.LOGGED_IN
				|| event.getActor() != client.getLocalPlayer())
		{
			return;
		}

		int currentAnimationId = event.getActor().getAnimation();
		if (currentAnimationId != lastAnimationId)
		{
			System.out.println("Animation ID: " + currentAnimationId);
			lastAnimationId = currentAnimationId;
		}
	}

	//update item list
	private Map<Integer, Integer> herbItemsCache;
	public void updateHerbOverlay(Map<Integer, Integer> herbItems)
	{
		this.herbItemsCache = herbItems;
	}
	private Map<Integer, Integer> treeItemsCache;
	public void updateTreeOverlay(Map<Integer, Integer> treeItems)
	{
		this.treeItemsCache = treeItems;
	}

	private Map<Integer, Integer> fruitTreeItemsCache;
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
	public void addTextToInfoBox(String text) {
		farmingHelperOverlayInfoBox.setText(text);
	}
	public boolean getHerbLocationEnabled(String locationName) {
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
			case "Varlamore":
				return config.varlamoreHerb();
			// Add cases for other locations as needed
			default:
				return false;
		}
	}

	public boolean getTreeLocationEnabled(String locationName) {
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

	public boolean getFruitTreeLocationEnabled(String locationName) {
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
		herbRunItemAndLocation = new HerbRunItemAndLocation(config, client, this);
		treeRunItemAndLocation = new TreeRunItemAndLocation(config, client, this);
		fruitTreeRunItemAndLocation = new FruitTreeRunItemAndLocation(config, client, this);
		farmingHelperOverlay = new FarmingHelperOverlay(client, this, itemManager, herbRunItemAndLocation, treeRunItemAndLocation, fruitTreeRunItemAndLocation);

		panel = new FarmingHelperPanel(this, overlayManager, farmingTeleportOverlay, herbRunItemAndLocation, treeRunItemAndLocation, fruitTreeRunItemAndLocation);
		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/com/farminghelper/speaax/icon.png");

		navButton = NavigationButton.builder()
				.tooltip("Lazy Farming")
				.icon(icon)
				.priority(6)
				.panel(panel)
				.build();
		clientToolbar.addNavigation(navButton);

		overlayManager.add(farmingHelperOverlay);
		overlayManager.add(farmingTeleportOverlay);
		overlayManager.add(farmingHelperOverlayInfoBox);

		// set overlay to inactive
		isOverlayActive = false;
		eventBus.register(this);

		herbRunItemAndLocation.setupLocations();
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