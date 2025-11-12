package com.easyfarming;

import com.easyfarming.ItemsAndLocations.HerbRunItemAndLocation;
import com.easyfarming.ItemsAndLocations.TreeRunItemAndLocation;
import com.easyfarming.ItemsAndLocations.FruitTreeRunItemAndLocation;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.*;
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
import net.runelite.client.eventbus.EventBus;

@PluginDescriptor(
		name = "Easy Farming",
		description = "Show item requirements and highlights for farming runs."
)

public class EasyFarmingPlugin extends Plugin
{
	private HerbRunItemAndLocation herbRunItemAndLocation;
	private TreeRunItemAndLocation treeRunItemAndLocation;
	private FruitTreeRunItemAndLocation fruitTreeRunItemAndLocation;


	@Inject
	private ItemManager itemManager;
	@Getter
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

	public Location getCivitasLocation() {
		return herbRunItemAndLocation.civitasLocation;
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

	@Getter
    @Setter
    private boolean isTeleportOverlayActive = false;

    @Inject
	private EasyFarmingOverlayInfoBox farmingHelperOverlayInfoBox;
	public EasyFarmingOverlayInfoBox getEasyFarmingOverlayInfoBox()
	{
		return farmingHelperOverlayInfoBox;
	}

	@Getter
    private String lastMessage = "";
    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getType() == ChatMessageType.GAMEMESSAGE) {
            lastMessage = event.getMessage();
        }
        else if (event.getType() == ChatMessageType.SPAM) {
            lastMessage = event.getMessage();
        }
    }

    public boolean checkMessage(String targetMessage, String lastMessage) {
		return lastMessage.trim().equalsIgnoreCase(targetMessage.trim());
	}

	@Inject
	private EventBus eventBus;

	@Inject
	private ClientThread clientThread;


	@Getter
    @Inject
	private FarmingTeleportOverlay farmingTeleportOverlay;

	private EasyFarmingPanel farmingHelperPanel;
	public EasyFarmingPanel panel;
	private NavigationButton navButton;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private EasyFarmingConfig config;
	@Inject
	public OverlayManager overlayManager;

	@Getter
    @Setter
    private boolean isOverlayActive = true;

	@Inject
	private EasyFarmingOverlay farmingHelperOverlay;

	public EasyFarmingOverlay getEasyFarmingOverlay()
	{
		return farmingHelperOverlay;
	}

	@Setter
    private boolean itemsCollected = false;
	public boolean areItemsCollected() {
		return itemsCollected;
	}

	@Provides
	EasyFarmingConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(EasyFarmingConfig.class);
	}
	
	@Provides
	com.easyfarming.overlays.utils.ColorProvider provideColorProvider(EasyFarmingConfig config)
	{
		return new com.easyfarming.overlays.utils.ColorProvider(config);
	}
	
	@Provides
	HerbRunItemAndLocation provideHerbRunItemAndLocation(EasyFarmingConfig config, Client client, EasyFarmingPlugin plugin)
	{
		return new HerbRunItemAndLocation(config, client, plugin);
	}
	
	@Provides
	TreeRunItemAndLocation provideTreeRunItemAndLocation(EasyFarmingConfig config, Client client, EasyFarmingPlugin plugin)
	{
		return new TreeRunItemAndLocation(config, client, plugin);
	}
	
	@Provides
	FruitTreeRunItemAndLocation provideFruitTreeRunItemAndLocation(EasyFarmingConfig config, Client client, EasyFarmingPlugin plugin)
	{
		return new FruitTreeRunItemAndLocation(config, client, plugin);
	}
	
	@Provides
	EasyFarmingOverlay provideEasyFarmingOverlay(Client client, EasyFarmingPlugin plugin, ItemManager itemManager,
	                                             HerbRunItemAndLocation herbRunItemAndLocation,
	                                             TreeRunItemAndLocation treeRunItemAndLocation,
	                                             FruitTreeRunItemAndLocation fruitTreeRunItemAndLocation)
	{
		return new EasyFarmingOverlay(client, plugin, itemManager, herbRunItemAndLocation, treeRunItemAndLocation, fruitTreeRunItemAndLocation);
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
			case "Civitas illa Fortis":
				return config.civitasHerb();
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
		farmingHelperOverlay = new EasyFarmingOverlay(client, this, itemManager, herbRunItemAndLocation, treeRunItemAndLocation, fruitTreeRunItemAndLocation);

		panel = new EasyFarmingPanel(this, overlayManager, farmingTeleportOverlay, herbRunItemAndLocation, treeRunItemAndLocation, fruitTreeRunItemAndLocation);
		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/icon.png");

		navButton = NavigationButton.builder()
				.tooltip("Easy Farming")
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
		if (navButton != null) {
			clientToolbar.removeNavigation(navButton);
		}

		overlayManager.remove(farmingHelperOverlay);
		overlayManager.remove(farmingTeleportOverlay);
		overlayManager.remove(farmingHelperOverlayInfoBox);

		eventBus.unregister(this);
	}
}