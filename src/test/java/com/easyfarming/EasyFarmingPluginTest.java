package com.easyfarming;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class EasyFarmingPluginTest
{
	public static void main(String[] args) throws Exception
	{
		// Check if assertions are enabled
		boolean assertionsEnabled = false;
		try {
			assert assertionsEnabled = true;
		} catch (AssertionError e) {
			// Assertions are not enabled
		}
		
		if (!assertionsEnabled) {
			System.err.println("Assertions are not enabled. Please run with -ea flag:");
			System.err.println("java -ea -cp <classpath> com.easyfarming.EasyFarmingPluginTest");
			System.exit(1);
		}
		
		ExternalPluginManager.loadBuiltin(EasyFarmingPlugin.class);
		RuneLite.main(args);
	}
}