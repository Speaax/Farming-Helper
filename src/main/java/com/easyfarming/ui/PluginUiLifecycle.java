package com.easyfarming.ui;

import com.easyfarming.EasyFarmingOverlay;
import com.easyfarming.EasyFarmingOverlayInfoBox;
import com.easyfarming.EasyFarmingPanel;
import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.FarmingTeleportOverlay;
import com.easyfarming.FarmingTeleportSceneOverlay;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;

/**
 * Owns sidebar panel / nav-button construction and overlay registration for plugin start/stop.
 */
@Singleton
public class PluginUiLifecycle
{
	@Inject
	private ClientToolbar clientToolbar;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private ItemManager itemManager;
	@Inject
	private FarmingTeleportOverlay farmingTeleportOverlay;
	@Inject
	private EasyFarmingOverlay farmingHelperOverlay;
	@Inject
	private FarmingTeleportSceneOverlay farmingTeleportSceneOverlay;
	@Inject
	private EasyFarmingOverlayInfoBox farmingHelperOverlayInfoBox;

	private NavigationButton navButton;

	/**
	 * Creates the sidebar panel, adds the nav button, and registers overlays.
	 *
	 * @return the created {@link EasyFarmingPanel} for assignment to {@code plugin.panel}
	 */
	public EasyFarmingPanel start(EasyFarmingPlugin plugin)
	{
		EasyFarmingPanel panel = new EasyFarmingPanel(plugin, overlayManager, farmingTeleportOverlay, itemManager);
		final BufferedImage icon = ImageUtil.loadImageResource(EasyFarmingPlugin.class, "/icon.png");

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

		return panel;
	}

	/** Removes the nav button and unregisters overlays. */
	public void stop()
	{
		if (navButton != null) {
			clientToolbar.removeNavigation(navButton);
			navButton = null;
		}

		overlayManager.remove(farmingHelperOverlay);
		overlayManager.remove(farmingTeleportSceneOverlay);
		overlayManager.remove(farmingTeleportOverlay);
		overlayManager.remove(farmingHelperOverlayInfoBox);
	}
}
