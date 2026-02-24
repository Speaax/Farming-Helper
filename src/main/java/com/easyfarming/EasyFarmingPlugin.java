package com.easyfarming;

import com.easyfarming.core.Location;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;

import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.timetracking.TimeTrackingPlugin;
import net.runelite.client.plugins.timetracking.TimeTrackingConfig;

@PluginDescriptor(
		name = "Easy Farming",
		description = "Show item requirements and highlights for farming runs."
)
@PluginDependency(TimeTrackingPlugin.class)
public class EasyFarmingPlugin extends Plugin
{



	@Inject
	private ItemManager itemManager;
	@Getter
    @Inject
	private Client client;

	@Inject
	private com.google.gson.Gson gson;

	@Inject
	private ConfigManager configManager;

	@Getter
	private com.easyfarming.managers.CustomRunManager customRunManager;

	@Inject
	@Getter
	private com.easyfarming.runelite.farming.FarmingTracker farmingTracker;

	@Inject
	@Getter
	private com.easyfarming.runelite.farming.FarmingWorld farmingWorld;

	@Inject
	@Getter
	private com.easyfarming.runelite.farming.CompostTracker compostTracker;

	public void runOnClientThread(Runnable task) {
		clientThread.invokeLater(task);
	}



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
        String message = event.getMessage();
        
        // Store last message for other purposes (compost detection, etc.)
        if (event.getType() == ChatMessageType.GAMEMESSAGE) {
            lastMessage = message;
        }
        else if (event.getType() == ChatMessageType.SPAM) {
            lastMessage = message;
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
	@Inject
	public InfoBoxManager infoBoxManager;

	@Getter
    @Setter
    private boolean isOverlayActive = true;

	@Inject
	private EasyFarmingOverlay farmingHelperOverlay;

	public EasyFarmingOverlay getEasyFarmingOverlay()
	{
		return farmingHelperOverlay;
	}

	public void startCustomRun(String runName) {
		com.easyfarming.models.CustomRun run = getCustomRunManager().getCustomRun(runName);
		if (run != null) {
			farmingTeleportOverlay.startCustomRun(run);
			isOverlayActive = true;
		}
	}

	@Setter
    private boolean itemsCollected = false;
	public boolean areItemsCollected() {
		return itemsCollected;
	}

	// ── Tool toggle state (driven by OverviewPanel buttons) ─────────────────
	@Getter private boolean toolSpade     = true;  // always required
	@Getter private boolean toolSecateurs = true;
	@Getter private boolean toolDibber    = true;
	@Getter private boolean toolRake      = false;

	public void setToolSpade(boolean v)     { toolSpade = v; }
	public void setToolSecateurs(boolean v) { toolSecateurs = v; }
	public void setToolDibber(boolean v)    { toolDibber = v; }
	public void setToolRake(boolean v)      { toolRake = v; }

	/** Returns the compost item ID based on current config, or -1 for none. */
	public int getCompostId() {
		if (config == null) return -1;
		switch (config.enumConfigCompost()) {
			case Compost:       return ItemID.BUCKET_COMPOST;
			case Supercompost:  return ItemID.BUCKET_SUPERCOMPOST;
			case Ultracompost:  return ItemID.BUCKET_ULTRACOMPOST;
			case Bottomless:    return ItemID.BOTTOMLESS_COMPOST_BUCKET;
			default:            return -1;
		}
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
	

    public void addTextToInfoBox(String text) {
		farmingHelperOverlayInfoBox.setText(text);
	}

    public void addDebugTextToInfoBox(String debugText) {
		farmingHelperOverlayInfoBox.setDebugText(debugText);
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

	public boolean getHopsLocationEnabled(String locationName) {
		switch (locationName) {
			case "Lumbridge":
				return config.lumbridgeHops();
			case "Seers Village":
				return config.seersVillageHops();
			case "Yanille":
				return config.yanilleHops();
			case "Entrana":
				return config.entranaHops();
			case "Aldarin":
				return config.aldarinHops();
			default:
				return false;
		}
	}

	@Override
	protected void startUp()
	{
		customRunManager = new com.easyfarming.managers.CustomRunManager(configManager, gson);

		farmingHelperOverlay = new EasyFarmingOverlay(client, this, itemManager, infoBoxManager);

		panel = new EasyFarmingPanel(this, overlayManager, farmingTeleportOverlay, itemManager);
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
		eventBus.register(farmingTracker);
		eventBus.register(compostTracker);
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
		eventBus.unregister(farmingTracker);
		eventBus.unregister(compostTracker);
	}
}