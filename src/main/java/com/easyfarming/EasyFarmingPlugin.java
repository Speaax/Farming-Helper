package com.easyfarming;

import com.easyfarming.customrun.CustomRunStorage;
import com.easyfarming.customrun.CurrentStepInstruction;
import com.easyfarming.customrun.LocationCatalog;
import com.easyfarming.customrun.NavigationTextOverrides;
import com.easyfarming.core.Teleport;
import com.google.gson.Gson;
import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import javax.swing.SwingUtilities;

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
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;

@PluginDescriptor(
		name = "Easy Farming",
		description = "Show item requirements and highlights for farming runs."
)

public class EasyFarmingPlugin extends Plugin
{
	@Inject
	private ItemManager itemManager;
	@Inject
	private ConfigManager configManager;
	@Inject
	private Gson gson;
	@Getter
    @Inject
	private Client client;

	private LocationCatalog locationCatalog;
	private CustomRunStorage customRunStorage;
	private NavigationTextOverrides navigationTextOverrides;

	public LocationCatalog getLocationCatalog() {
		if (locationCatalog == null) {
			locationCatalog = new LocationCatalog(this);
		}
		return locationCatalog;
	}

	public CustomRunStorage getCustomRunStorage() {
		if (customRunStorage == null) {
			customRunStorage = new CustomRunStorage(configManager, gson);
		}
		return customRunStorage;
	}

	public NavigationTextOverrides getNavigationTextOverrides() {
		if (navigationTextOverrides == null) {
			navigationTextOverrides = new NavigationTextOverrides(configManager, gson);
		}
		return navigationTextOverrides;
	}

	@Getter
	private CurrentStepInstruction currentStepInstruction;
	private String lastNotifiedStepIdentity = "";

	/**
	 * Shows navigation instruction text (with overrides) and records it as the editable current step.
	 */
	public void setNavigationInstruction(String locationName, Teleport teleport) {
		NavigationTextOverrides overrides = getNavigationTextOverrides();
		String defaultText = teleport != null && teleport.getDescription() != null ? teleport.getDescription() : "";
		String resolved = overrides.resolve(locationName, teleport);
		if (farmingHelperOverlayInfoBox != null) {
			farmingHelperOverlayInfoBox.setText(resolved);
		}
		currentStepInstruction = CurrentStepInstruction.navigation(
				locationName,
				teleport != null ? teleport.getEnumOption() : null,
				defaultText);
		maybeNotifyCurrentStepUi();
	}

	public void clearCurrentStepInstruction() {
		currentStepInstruction = null;
		lastNotifiedStepIdentity = "";
		if (panel != null) {
			SwingUtilities.invokeLater(panel::refreshCurrentStepEditors);
		}
	}

	private void maybeNotifyCurrentStepUi() {
		String identity = currentStepInstruction != null ? currentStepInstruction.identity() : "";
		if (identity.equals(lastNotifiedStepIdentity)) {
			return;
		}
		lastNotifiedStepIdentity = identity;
		if (panel != null) {
			SwingUtilities.invokeLater(panel::refreshCurrentStepEditors);
		}
	}

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

	/**
	 * Clears the last game message used for compost/protection detection. Call when advancing to the
	 * next patch at the same location so a compost line from the previous patch is not applied to the next.
	 */
	public void clearLastMessage() {
		lastMessage = "";
	}

	/**
	 * Skip the current step of an active custom run (item checklist, navigation, or farming).
	 * Called by the "Skip current step" button. No-op when no custom run is active.
	 * Clears {@code lastMessage} so a chat line from a skipped step cannot poison the next
	 * step's compost detection (see {@link com.easyfarming.overlays.utils.PatchStateChecker}).
	 */
	public void skipCurrentStep() {
		if (farmingTeleportOverlay == null || !farmingTeleportOverlay.isCustomRunMode()) {
			return;
		}
		clearLastMessage();
		farmingTeleportOverlay.skipCurrentStep();
	}

	/**
	 * Marks the item-gathering phase complete and enables teleport/navigation overlays.
	 * Used when the run has "Skip item checklist" enabled, or when the user skips that step.
	 */
	public void completeItemGatheringPhase() {
		itemsCollected = true;
		isTeleportOverlayActive = true;
		if (farmingHelperOverlay != null) {
			farmingHelperOverlay.clearAllInfoBoxes();
		}
	}

	@Inject
	private EventBus eventBus;

	@Inject
	private ClientThread clientThread;


	@Getter
    @Inject
	private FarmingTeleportOverlay farmingTeleportOverlay;
	@Inject
	private FarmingTeleportSceneOverlay farmingTeleportSceneOverlay;

	private EasyFarmingPanel farmingHelperPanel;
	public EasyFarmingPanel panel;
	private NavigationButton navButton;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private EasyFarmingConfig config;

	public EasyFarmingConfig getConfig() {
		return config;
	}

	/** Custom run tool inclusion: set when starting a custom run from the detail panel. */
	private boolean customRunIncludeSecateurs = true;
	private boolean customRunIncludeDibber = true;
	private boolean customRunIncludeRake = true;

	public void setCustomRunToolInclusion(boolean secateurs, boolean dibber, boolean rake) {
		this.customRunIncludeSecateurs = secateurs;
		this.customRunIncludeDibber = dibber;
		this.customRunIncludeRake = rake;
	}

	public boolean getCustomRunIncludeSecateurs() { return customRunIncludeSecateurs; }
	public boolean getCustomRunIncludeDibber() { return customRunIncludeDibber; }
	public boolean getCustomRunIncludeRake() { return customRunIncludeRake; }

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
	EasyFarmingOverlay provideEasyFarmingOverlay(Client client, EasyFarmingPlugin plugin, ItemManager itemManager, InfoBoxManager infoBoxManager)
	{
		return new EasyFarmingOverlay(client, plugin, itemManager, infoBoxManager);
	}

    public void addTextToInfoBox(String text) {
		String defaultText = text != null ? text : "";
		String resolved = getNavigationTextOverrides().resolveStep(defaultText);
		if (farmingHelperOverlayInfoBox != null) {
			farmingHelperOverlayInfoBox.setText(resolved);
		}
		String locationName = null;
		if (farmingTeleportOverlay != null && farmingTeleportOverlay.isCustomRunMode()) {
			locationName = farmingTeleportOverlay.getActiveLocationName();
		}
		currentStepInstruction = CurrentStepInstruction.farming(locationName, defaultText);
		maybeNotifyCurrentStepUi();
	}

    public void addDebugTextToInfoBox(String debugText) {
		farmingHelperOverlayInfoBox.setDebugText(debugText);
	}

	@Override
	protected void startUp()
	{
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
		overlayManager.add(farmingTeleportSceneOverlay);
		overlayManager.add(farmingTeleportOverlay);
		overlayManager.add(farmingHelperOverlayInfoBox);

		// set overlay to inactive
		isOverlayActive = false;
		eventBus.register(this);
	}

	@Override
	protected void shutDown()
	{
		if (navButton != null) {
			clientToolbar.removeNavigation(navButton);
		}

		overlayManager.remove(farmingHelperOverlay);
		overlayManager.remove(farmingTeleportSceneOverlay);
		overlayManager.remove(farmingTeleportOverlay);
		overlayManager.remove(farmingHelperOverlayInfoBox);

		eventBus.unregister(this);
	}
}