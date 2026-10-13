package com.easyfarming.ui;

import com.easyfarming.EasyFarmingPanel;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.SwingUtilities;

/**
 * Schedules a UI refresh of the current-step editors on the Swing EDT.
 */
@Singleton
public class CurrentStepPanelRefresher
{
	@Inject
	public CurrentStepPanelRefresher()
	{
	}

	public void refreshCurrentStepEditors(EasyFarmingPanel panel)
	{
		if (panel != null)
		{
			SwingUtilities.invokeLater(panel::refreshCurrentStepEditors);
		}
	}
}
