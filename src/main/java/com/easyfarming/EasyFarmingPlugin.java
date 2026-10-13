package com.easyfarming;

import com.easyfarming.customrun.CustomRunStorage;
import com.easyfarming.customrun.CurrentStepInstruction;
import com.easyfarming.customrun.GameMessageBuffer;
import com.easyfarming.customrun.ItemGatheringState;
import com.easyfarming.customrun.LocationCatalog;
import com.easyfarming.customrun.NavigationTextOverrides;
import com.easyfarming.core.Teleport;
import com.easyfarming.ui.CurrentStepPanelRefresher;
import com.easyfarming.ui.PluginUiLifecycle;
import com.google.gson.Gson;
import com.google.inject.Provides;
import java.util.Objects;
import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
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
	private ConfigManager configManager;
	@Inject
	private Gson gson;
	@Getter
    @Inject
	private Client client;

	@Inject
	private LocationCatalog locationCatalog;
	@Inject
	private CustomRunStorage customRunStorage;
	@Inject
	private NavigationTextOverrides navigationTextOverrides;
	@Inject
	private GameMessageBuffer gameMessageBuffer;
	@Inject
	private CurrentStepPanelRefresher currentStepPanelRefresher;

	public LocationCatalog getLocationCatalog() {
		return locationCatalog;
	}

	public CustomRunStorage getCustomRunStorage() {
		return customRunStorage;
	}

	public NavigationTextOverrides getNavigationTextOverrides() {
		return navigationTextOverrides;
	}

	@Getter
	private CurrentStepInstruction currentStepInstruction;
	private CurrentStepInstruction lastNotifiedStepInstruction;

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
		lastNotifiedStepInstruction = null;
		currentStepPanelRefresher.refreshCurrentStepEditors(panel);
	}

	private void maybeNotifyCurrentStepUi() {
		if (Objects.equals(currentStepInstruction, lastNotifiedStepInstruction)) {
			return;
		}
		lastNotifiedStepInstruction = currentStepInstruction;
		currentStepPanelRefresher.refreshCurrentStepEditors(panel);
	}

	public void runOnClientThread(Runnable task) {
		clientThread.invokeLater(task);
	}

	@Getter
    @Setter
    private volatile boolean isTeleportOverlayActive = false;

    @Inject
	private EasyFarmingOverlayInfoBox farmingHelperOverlayInfoBox;
	public EasyFarmingOverlayInfoBox getEasyFarmingOverlayInfoBox()
	{
		return farmingHelperOverlayInfoBox;
	}

    @Subscribe
    public void onChatMessage(ChatMessage event) {
		gameMessageBuffer.onChatMessage(event);
    }

	public String getLastMessage() {
		return gameMessageBuffer.getLastMessage();
	}

    public boolean checkMessage(String targetMessage, String lastMessage) {
		return gameMessageBuffer.checkMessage(targetMessage, lastMessage);
	}

	/**
	 * Clears the last game message used for compost/protection detection. Call when advancing to the
	 * next patch at the same location so a compost line from the previous patch is not applied to the next.
	 */
	public void clearLastMessage() {
		gameMessageBuffer.clearLastMessage();
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
	 * Clears item-checklist / teleport-active state so the next run rebuilds requirements
	 * from the current inventory instead of inheriting the previous run's "all collected" flag.
	 */
	public void resetItemGatheringState() {
		itemGatheringState.reset();
		isTeleportOverlayActive = false;
		if (farmingHelperOverlay != null) {
			farmingHelperOverlay.clearAllInfoBoxes();
		}
	}

	/**
	 * Begins a new run's gathering cycle. Invalidates any prior "items collected" completion
	 * even if a stale overlay frame still holds the old flag.
	 */
	public void beginItemGatheringForNewRun() {
		itemGatheringState.beginNewRun();
		isTeleportOverlayActive = false;
		if (farmingHelperOverlay != null) {
			farmingHelperOverlay.clearAllInfoBoxes();
		}
	}

	/**
	 * Marks the item-gathering phase complete and enables teleport/navigation overlays.
	 * Used when the run has "Skip item checklist" enabled, or when the user skips that step.
	 */
	public void completeItemGatheringPhase() {
		itemGatheringState.setCollected(true);
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

	public EasyFarmingPanel panel;

	@Inject
	private PluginUiLifecycle pluginUiLifecycle;

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
    private volatile boolean isOverlayActive = false;

	@Inject
	private EasyFarmingOverlay farmingHelperOverlay;

	public EasyFarmingOverlay getEasyFarmingOverlay()
	{
		return farmingHelperOverlay;
	}

	/**
	 * Gathering completion is scoped to the current run id so a stuck {@code true} from run A
	 * cannot skip the checklist on run B.
	 */
	private final ItemGatheringState itemGatheringState = new ItemGatheringState();

	public boolean areItemsCollected() {
		return itemGatheringState.isCollected();
	}

	public void setItemsCollected(boolean collected) {
		itemGatheringState.setCollected(collected);
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
	@javax.inject.Singleton
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
		panel = pluginUiLifecycle.start(this);

		// set overlay to inactive
		isOverlayActive = false;
		eventBus.register(this);
	}

	@Override
	protected void shutDown()
	{
		pluginUiLifecycle.stop();
		eventBus.unregister(this);
	}
}
