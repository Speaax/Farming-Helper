package com.easyfarming;

import java.awt.*;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("farminghelper")
public interface EasyFarmingConfig extends Config
{
	@ConfigSection(
			name = "General",
			description = "The highlighted and hidden item lists",
			position = 0,
			closedByDefault = false
	)
	String generalList = "generalList";

	enum OptionEnumHouseTele
	{
		Law_air_earth_runes,
		Teleport_To_House,
		Construction_cape,
		Construction_cape_t,
		Max_cape
	}
	public interface OptionEnumTeleport {
		String name();
	}
	@ConfigItem(
			position = 10,
			keyName = "enumConfigHouseTele",
			name = "House teleport",
			description = "Desired way to teleport to house",
			section = generalList
	)
	default OptionEnumHouseTele enumConfigHouseTele() { return OptionEnumHouseTele.Law_air_earth_runes; }
	@ConfigItem(
			position = 1,
			keyName = "highlightLeftClickColor",
			name = "Left Click Color",
			description = "The color to use for highlighting objects",
			section = generalList
	)
	default Color highlightLeftClickColor() {return new Color(0, 191, 255, 128);}
	@ConfigItem(
			position = 2,
			keyName = "highlightRightClickColor",
			name = "Right Click Color",
			description = "The color to use for highlighting objects",
			section = generalList
	)
	default Color highlightRightClickColor() {return new Color(0, 191, 30, 128);}
	@ConfigItem(
			position = 3,
			keyName = "highlightUseItemColor",
			name = "'Use' item Color",
			description = "The color to use for highlighting objects",
			section = generalList
	)
	default Color highlightUseItemColor() {return new Color(255, 192, 203, 128);}
	@ConfigItem(
			position = 4,
			keyName = "highlightAlpha",
			name = "Transparency",
			description = "The transparency value for the highlight color (0-255)",
			section = generalList
	)
	default int highlightAlpha() {return 128;}

	enum OptionEnumCompost
	{
		Compost,
		Supercompost,
		Ultracompost,
		Bottomless
	}
	@ConfigItem(
			position = 5,
			keyName = "enumConfigCompost",
			name = "Compost",
			description = "Desired Compost",
			section = generalList
	)
	default OptionEnumCompost enumConfigCompost() { return OptionEnumCompost.Bottomless; }


	@ConfigItem(
			keyName = "booleanConfigRake",
			name = "Rake",
			description = "Include rake?",
			position = 6,
			section = generalList
	)
	default boolean generalRake() { return false; }
	@ConfigItem(
		keyName = "booleanConfigSeedDibber",
		name = "Seed dibber",
		description = "Include seed dibber?",
		position = 7,
		section = generalList
	)
	default boolean generalSeedDibber() { return true; }
	@ConfigItem(
			keyName = "booleanConfigSecateurs",
			name = "Magic secateurs",
			description = "Include magic secateurs?",
			position = 8,
			section = generalList
	)
	default boolean generalSecateurs() { return true; }
	@ConfigItem(
			keyName = "booleanConfigLimpwurt",
			name = "Limpwurt",
			description = "Want to include limpwurts in your herb run?",
			position = 9,
			section = generalList
	)
	default boolean generalLimpwurt() { return false; }
	@ConfigItem(
			keyName = "booleanConfigAllotment",
			name = "Allotments",
			description = "Want to include Allotments in your herb run?",
			position = 10,
			section = generalList
	)
	default boolean generalAllotment() { return false; }

	@ConfigItem(
		keyName = "booleanConfigPayForProtection",
		name = "Pay for protection",
		description = "Want a reminder to pay for protection? (This currently doesn't check for the required items, only prompts you to pay the farmer.)",
		position = 11,
		section = generalList
	)
	default boolean generalPayForProtection() { return false; }


	@ConfigSection(
		name = "Herbs",
		description = "The highlighted and hidden item lists",
		position = 1,
		closedByDefault = true
	)
	String herbList = "herbList";

	@ConfigItem(
		keyName = "booleanConfigArdougneHerb",
		name = "Ardougne",
		description = "Include Ardougne?",
		position = 1,
		section = herbList
	)
	default boolean ardougneHerb() { return true; }
	
	@ConfigItem(
		position = 2,
		keyName = "booleanConfigCatherbyHerb",
		name = "Catherby",
		description = "Include Catherby?",
		section = herbList
	)
	default boolean catherbyHerb() { return true; }
	
	@ConfigItem(
		position = 3,
		keyName = "booleanConfigFaladorHerb",
		name = "Falador",
		description = "Include Falador?",
		section = herbList
	)
	default boolean faladorHerb() { return true; }
	
	@ConfigItem(
		position = 4,
		keyName = "booleanConfigFarmingGuildHerb",
		name = "Farming Guild",
		description = "Include Farming guild? (Requires level 65 farming and 60% Hosidious favour)",
		section = herbList
	)
	default boolean farmingGuildHerb() { return false; }
	
	@ConfigItem(
		position = 5,
		keyName = "booleanConfigHarmonyHerb",
		name = "Harmony",
		description = "Include Harmony? (Requires elite Morytania diary)",
		section = herbList
	)
	default boolean harmonyHerb() { return false; }
	
	@ConfigItem(
		position = 6,
		keyName = "booleanConfigKourendHerb",
		name = "Kourend",
		description = "Include Kourend?",
		section = herbList
	)
	default boolean kourendHerb() { return true; }
	
	@ConfigItem(
		position = 7,
		keyName = "booleanConfigMorytaniaHerb",
		name = "Morytania",
		description = "Include Morytania?",
		section = herbList
	)
	default boolean morytaniaHerb() { return true; }
	
	@ConfigItem(
		position = 8,
		keyName = "booleanConfigTrollStrongholdHerb",
		name = "Troll Stronghold",
		description = "Include Troll Stronghold(Requires completion of My Arm's Big Adventure)?",
		section = herbList
	)
	default boolean trollStrongholdHerb() { return false; }
	
	@ConfigItem(
		position = 9,
		keyName = "booleanConfigWeissHerb",
		name = "Weiss",
		description = "Include Weiss? (Requires completion of Making Friends with My Arm, and The Fire of Nourishment must be built)",
		section = herbList
	)
	default boolean weissHerb() { return false; }

	@ConfigItem(
		position = 10,
		keyName = "booleanConfigCivitasHerb",
		name = "Civitas illa Fortis",
		description = "Include Civitas illa Fortis?",
		section = herbList
	)
	default boolean civitasHerb() { return false; }

	@ConfigSection(
		name = "Tree",
		description = "The highlighted and hidden item lists",
		position = 2,
		closedByDefault = true
	)
	String treeList = "treeList";
	
	@ConfigItem(
		position = 10,
		keyName = "booleanConfigFaladorTree",
		name = "Falador",
		description = "Include Falador?",
		section = treeList
	)
	default boolean faladorTree() { return true; }
	
	@ConfigItem(
		position = 11,
		keyName = "booleanConfigFarmingGuildTree",
		name = "Farming guild",
		description = "Include Farming Guild? (Requires 65 farming)",
		section = treeList
	)
	default boolean farmingGuildTree() { return false; }
	
	@ConfigItem(
		position = 12,
		keyName = "booleanConfigGnomeStrongholdTree",
		name = "Gnome Stronghold",
		description = "Include Gnome Stronghold?",
		section = treeList
	)
	default boolean gnomeStrongholdTree() { return true; }
	
	@ConfigItem(
		position = 13,
		keyName = "booleanConfigLumbridgeTree",
		name = "Lumbridge",
		description = "Include Lumbridge?",
		section = treeList
	)
	default boolean lumbridgeTree() { return true; }
	
	@ConfigItem(
		position = 14,
		keyName = "booleanConfigTaverleyTree",
		name = "Taverley",
		description = "Include Taverley?",
		section = treeList
	)
	default boolean taverleyTree() { return true; }
	
	@ConfigItem(
		position = 15,
		keyName = "booleanConfigVarrockTree",
		name = "Varrock",
		description = "Include Varrock?",
		section = treeList
	)
	default boolean varrockTree() { return true; }
	
	@ConfigSection(
		name = "Fruit Tree",
		description = "The highlighted and hidden item lists",
		position = 3,
		closedByDefault = true
	)
	String fruitTreeList = "fruitTreeList";
	
	@ConfigItem(
		position = 16,
		keyName = "booleanConfigBrimhavenFruitTree",
		name = "Brimhaven",
		description = "Include Brimhaven?",
		section = fruitTreeList
	)
	default boolean brimhavenFruitTree() { return true; }
	
	@ConfigItem(
		position = 17,
		keyName = "booleanConfigCatherbyFruitTree",
		name = "Catherby",
		description = "Include Catherby?",
		section = fruitTreeList
	)
	default boolean catherbyFruitTree() { return true; }
	
	@ConfigItem(
		position = 18,
		keyName = "booleanConfigFarmingGuildFruitTree",
		name = "Farming Guild",
		description = "Include Farming Guild? (Requires 85 farming)",
		section = fruitTreeList
	)
	default boolean farmingGuildFruitTree() { return false; }
	
	@ConfigItem(
		position = 19,
		keyName = "booleanConfigGnomeStrongholdFruitTree",
		name = "Gnome Stronghold",
		description = "Include Gnome Stronghold?",
		section = fruitTreeList
	)
	default boolean gnomeStrongholdFruitTree() { return true; }
	
	@ConfigItem(
		position = 20,
		keyName = "booleanConfigLletyaFruitTree",
		name = "Lletya",
		description = "Include Lletya? (Requires starting Mourning's End Part I)",
		section = fruitTreeList
	)
	default boolean lletyaFruitTree() { return false; }
	
	@ConfigItem(
		position = 21,
		keyName = "booleanConfigTreeGnomeVillageFruitTree",
		name = "Tree Gnome Village",
		description = "Include Tree Gnome Village?",
		section = fruitTreeList
	)
	default boolean treeGnomeVillageFruitTree() { return true; }

	@ConfigSection(
		name = "Hops",
		description = "The highlighted and hidden item lists",
		position = 4,
		closedByDefault = true
	)
	String hopsList = "hopsList";

	@ConfigItem(
		position = 22,
		keyName = "booleanConfigLumbridgeHops",
		name = "Lumbridge",
		description = "Include Lumbridge?",
		section = hopsList
	)
	default boolean lumbridgeHops() { return true; }

	@ConfigItem(
		position = 23,
		keyName = "booleanConfigSeersVillageHops",
		name = "Seers Village",
		description = "Include Seers Village?",
		section = hopsList
	)
	default boolean seersVillageHops() { return true; }

	@ConfigItem(
		position = 24,
		keyName = "booleanConfigYanilleHops",
		name = "Yanille",
		description = "Include Yanille?",
		section = hopsList
	)
	default boolean yanilleHops() { return true; }

	@ConfigItem(
		position = 25,
		keyName = "booleanConfigEntranaHops",
		name = "Entrana",
		description = "Include Entrana?",
		section = hopsList
	)
	default boolean entranaHops() { return true; }

	@ConfigItem(
		position = 26,
		keyName = "booleanConfigAldarinHops",
		name = "Aldarin",
		description = "Include Aldarin?",
		section = hopsList
	)
	default boolean aldarinHops() { return false; }


	@ConfigSection(
			name = "Herb teleport options",
			description = "Choose what teleport to use for each Herb patch",
			position = 5,
			closedByDefault = true
		)
	String teleportOptionList = "teleportOptionList";

	enum OptionEnumArdougneTeleport implements OptionEnumTeleport
	{
		Portal_Nexus,
		Ardougne_teleport,
		Ardougne_tele_tab,
		Ardy_cloak,
		Skills_Necklace,
		Fishing_Skillcape
	}
	@ConfigItem(
			position = 2,
			keyName = "enumOptionEnumArdougneTeleport",
			name = "Ardougne",
			description = "Desired way to teleport to Ardougne",
			section = teleportOptionList
	)
	default OptionEnumArdougneTeleport enumOptionEnumArdougneTeleport() { return OptionEnumArdougneTeleport.Ardy_cloak; }
	enum OptionEnumCatherbyTeleport implements OptionEnumTeleport
	{
		Portal_Nexus_Catherby,
		Portal_Nexus_Camelot,
		Camelot_Teleport,
		Camelot_Tele_Tab,
		Catherby_Tele_Tab
	}
	@ConfigItem(
			position = 3,
			keyName = "enumOptionEnumCatherbyTeleport",
			name = "Catherby",
			description = "Desired way to teleport to Catherby",
			section = teleportOptionList
	)
	default OptionEnumCatherbyTeleport enumOptionEnumCatherbyTeleport() { return OptionEnumCatherbyTeleport.Portal_Nexus_Catherby; }

	enum OptionEnumFaladorTeleport implements OptionEnumTeleport
	{
		Portal_Nexus,
		Explorers_ring,
		Falador_Teleport,
		Falador_Tele_Tab,
		Draynor_Tele_Tab,
		Amulet_of_Glory,
		Spirit_Tree_Port_Sarim
	}
	@ConfigItem(
			position = 4,
			keyName = "enumOptionEnumFaladorTeleport",
			name = "Falador",
			description = "Desired way to teleport to Falador",
			section = teleportOptionList
	)
	default OptionEnumFaladorTeleport enumOptionEnumFaladorTeleport() { return OptionEnumFaladorTeleport.Explorers_ring; }

	enum OptionEnumFarmingGuildTeleport implements OptionEnumTeleport
	{
		Jewellery_box,
		Skills_Necklace,
		Spirit_Tree,
		Fairy_Ring,
		Farming_Skillcape
	}
	@ConfigItem(
			position = 5,
			keyName = "enumOptionEnumFarmingGuildTeleport",
			name = "Farming Guild",
			description = "Desired way to teleport to Farming Guild",
			section = teleportOptionList
	)
	default OptionEnumFarmingGuildTeleport enumOptionEnumFarmingGuildTeleport() { return OptionEnumFarmingGuildTeleport.Jewellery_box; }

	enum OptionEnumHarmonyTeleport implements OptionEnumTeleport
	{
		Portal_Nexus,
		Harmony_Tele_tab
	}
	@ConfigItem(
			position = 6,
			keyName = "enumOptionEnumHarmonyTeleport",
			name = "Harmony",
			description = "Desired way to teleport to Harmony",
			section = teleportOptionList
	)
	default OptionEnumHarmonyTeleport enumOptionEnumHarmonyTeleport() { return OptionEnumHarmonyTeleport.Portal_Nexus; }

	enum OptionEnumKourendTeleport implements OptionEnumTeleport
	{
		Xerics_Talisman,
		Mounted_Xerics,
		Hosidius_POH_Tab,
		Normal_POH_Tab
	}
	@ConfigItem(
			position = 7,
			keyName = "enumOptionEnumKourendTeleport",
			name = "Kourend",
			description = "Desired way to teleport to Kourend",
			section = teleportOptionList
	)
	default OptionEnumKourendTeleport enumOptionEnumKourendTeleport() { return OptionEnumKourendTeleport.Xerics_Talisman; }

	enum OptionEnumMorytaniaTeleport implements OptionEnumTeleport
	{
		Ectophial,
		Fairy_Ring,
		Portal_Nexus_Fenkenstrain,
		Portal_Nexus_Canifis
	}
	@ConfigItem(
			position = 8,
			keyName = "enumOptionEnumMorytaniaTeleport",
			name = "Morytania",
			description = "Desired way to teleport to Morytania",
			section = teleportOptionList
	)
	default OptionEnumMorytaniaTeleport enumOptionEnumMorytaniaTeleport() { return OptionEnumMorytaniaTeleport.Ectophial; }

	enum OptionEnumTrollStrongholdTeleport implements OptionEnumTeleport
	{
		Portal_Nexus,
		Stony_Basalt
	}
	@ConfigItem(
			position = 9,
			keyName = "enumOptionEnumTrollStrongholdTeleport",
			name = "Troll Stronghold",
			description = "Desired way to teleport to Troll Stronghold",
			section = teleportOptionList
	)
	default OptionEnumTrollStrongholdTeleport enumOptionEnumTrollStrongholdTeleport() { return OptionEnumTrollStrongholdTeleport.Portal_Nexus; }

	enum OptionEnumWeissTeleport implements OptionEnumTeleport
	{
		Portal_Nexus,
		Icy_basalt
	}
	@ConfigItem(
			position = 10,
			keyName = "enumOptionEnumWeissTeleport",
			name = "Weiss",
			description = "Desired way to teleport to Weiss",
			section = teleportOptionList
	)
	default OptionEnumWeissTeleport enumOptionEnumWeissTeleport() { return OptionEnumWeissTeleport.Portal_Nexus; }

	enum OptionEnumCivitasTeleport implements OptionEnumTeleport
	{
		Portal_Nexus,
		Civitas_Teleport,
		Civitas_Tele_Tab,
		Quetzal_whistle,
		Hunter_Skillcape
	}
	@ConfigItem(
		position = 11,
		keyName = "enumOptionEnumCivitasTeleport",
		name = "Civitas illa Fortis",
		description = "Desired way to teleport to Civitas illa Fortis",
		section = teleportOptionList
	)
	default OptionEnumCivitasTeleport enumOptionEnumCivitasTeleport() { return OptionEnumCivitasTeleport.Portal_Nexus; }

	@ConfigSection(
			name = "Tree teleport options",
			description = "Choose what teleport to use for each Herb patch",
			position = 6,
			closedByDefault = true
	)
	String treeTeleportOptionList = "treeTeleportOptionList";
	enum TreeOptionEnumFaladorTeleport implements OptionEnumTeleport
	{
		Portal_Nexus,
		Teleport,
		Falador_Tele_Tab
	}
	@ConfigItem(
			position = 1,
			keyName = "enumTreeFaladorTeleport",
			name = "Falador",
			description = "Desired way to teleport to Falador",
			section = treeTeleportOptionList
	)
	default TreeOptionEnumFaladorTeleport enumTreeFaladorTeleport() { return TreeOptionEnumFaladorTeleport.Teleport; }

	enum TreeOptionEnumFarmingGuildTeleport implements OptionEnumTeleport
	{
		Jewellery_box,
		Skills_Necklace,
		Spirit_Tree,
		Fairy_Ring,
		Farming_Skillcape
	}
	@ConfigItem(
			position = 1,
			keyName = "enumTreeFarmingGuildTeleport",
			name = "Farming Guild",
			description = "Desired way to teleport to Farming Guild",
			section = treeTeleportOptionList
	)
	default TreeOptionEnumFarmingGuildTeleport enumTreeFarmingGuildTeleport() { return TreeOptionEnumFarmingGuildTeleport.Jewellery_box; }

	enum TreeOptionEnumGnomeStrongholdTeleport implements OptionEnumTeleport
	{
		Royal_seed_pod,
		Spirit_Tree,
		Slayer_Ring
	}
	@ConfigItem(
			position = 2,
			keyName = "enumTreeGnomeStrongoldTeleport",
			name = "Gnome Stronghold",
			description = "Desired way to teleport to Gnome Stronghold",
			section = treeTeleportOptionList
	)
	default TreeOptionEnumGnomeStrongholdTeleport enumTreeGnomeStrongoldTeleport() { return TreeOptionEnumGnomeStrongholdTeleport.Royal_seed_pod; }

	enum TreeOptionEnumLumbridgeTeleport implements OptionEnumTeleport
	{
		Portal_Nexus,
		Teleport,
		Lumbridge_Tele_Tab
	}
	@ConfigItem(
			position = 3,
			keyName = "enumTreeLumbridgeTeleport",
			name = "Lumbridge",
			description = "Desired way to teleport to Lumbridge",
			section = treeTeleportOptionList
	)
	default TreeOptionEnumLumbridgeTeleport enumTreeLumbridgeTeleport() { return TreeOptionEnumLumbridgeTeleport.Teleport; }

	enum TreeOptionEnumTaverleyTeleport implements OptionEnumTeleport
	{
		Portal_Nexus,
		Teleport,
		Falador_Tele_Tab,
		Normal_POH_Tab,
		Games_Necklace_Burthorpe
	}
	@ConfigItem(
			position = 4,
			keyName = "enumTreeTaverleyTeleport",
			name = "Taverley",
			description = "Desired way to teleport to Taverley",
			section = treeTeleportOptionList
	)
	default TreeOptionEnumTaverleyTeleport enumTreeTaverleyTeleport() { return TreeOptionEnumTaverleyTeleport.Teleport; }

	enum TreeOptionEnumVarrockTeleport implements OptionEnumTeleport
	{
		Portal_Nexus,
		Teleport,
		Varrock_Tele_Tab
	}
	@ConfigItem(
			position = 5,
			keyName = "enumTreeVarrockTeleport",
			name = "Varrock",
			description = "Desired way to teleport to Varrock",
			section = treeTeleportOptionList
	)
	default TreeOptionEnumVarrockTeleport enumTreeVarrockTeleport() { return TreeOptionEnumVarrockTeleport.Teleport; }

	@ConfigSection(
			name = "Fruit tree teleport options",
			description = "Choose what teleport to use for each fruit tree",
			position = 7,
			closedByDefault = true
	)
	String fruitTreeTeleportOptionList = "fruitTreeTeleportOptionList";

	enum FruitTreeOptionEnumBrimhavenTeleport implements OptionEnumTeleport
	{
		Portal_Nexus,
		Ardougne_teleport,
		Ardougne_Tele_Tab,
		POH_Tele_Tab
	}
	@ConfigItem(
			position = 1,
			keyName = "enumFruitTreeBrimhavenTeleport",
			name = "Brimhaven",
			description = "Desired way to teleport to Brimhaven",
			section = fruitTreeTeleportOptionList
	)
	default FruitTreeOptionEnumBrimhavenTeleport enumFruitTreeBrimhavenTeleport() { return FruitTreeOptionEnumBrimhavenTeleport.Ardougne_teleport; }

	enum FruitTreeOptionEnumCatherbyTeleport implements OptionEnumTeleport
	{
		Portal_Nexus_Catherby,
		Portal_Nexus_Camelot,
		Camelot_Teleport,
		Camelot_Tele_Tab,
		Catherby_Tele_Tab
	}
	@ConfigItem(
			position = 1,
			keyName = "enumFruitTreeCatherbyTeleport",
			name = "Catherby",
			description = "Desired way to teleport to Catherby",
			section = fruitTreeTeleportOptionList
	)
	default FruitTreeOptionEnumCatherbyTeleport enumFruitTreeCatherbyTeleport() { return FruitTreeOptionEnumCatherbyTeleport.Portal_Nexus_Catherby; }

	enum FruitTreeOptionEnumFarmingGuildTeleport implements OptionEnumTeleport
	{
		Jewellery_box,
		Skills_Necklace,
		Spirit_Tree,
		Fairy_Ring
	}
	@ConfigItem(
			position = 1,
			keyName = "enumFruitTreeFarmingGuildTeleport",
			name = "Farming Guild",
			description = "Desired way to teleport to Farming Guild",
			section = fruitTreeTeleportOptionList
	)
	default FruitTreeOptionEnumFarmingGuildTeleport enumFruitTreeFarmingGuildTeleport() { return FruitTreeOptionEnumFarmingGuildTeleport.Jewellery_box; }

	enum FruitTreeOptionEnumGnomeStrongholdTeleport implements OptionEnumTeleport
	{
		Royal_seed_pod,
		Spirit_Tree
	}
	@ConfigItem(
			position = 1,
			keyName = "enumFruitTreeGnomeStrongholdTeleport",
			name = "Gnome Stronghold",
			description = "Desired way to teleport to Gnome Stronghold",
			section = fruitTreeTeleportOptionList
	)
	default FruitTreeOptionEnumGnomeStrongholdTeleport enumFruitTreeGnomeStrongholdTeleport() { return FruitTreeOptionEnumGnomeStrongholdTeleport.Royal_seed_pod; }

	enum FruitTreeOptionEnumLletyaTeleport implements OptionEnumTeleport
	{
		Teleport_crystal
	}
	@ConfigItem(
			position = 1,
			keyName = "enumFruitTreeLletyaTeleport",
			name = "Lletya",
			description = "Desired way to teleport to Lletya",
			section = fruitTreeTeleportOptionList
	)
	default FruitTreeOptionEnumLletyaTeleport enumFruitTreeLletyaTeleport() { return FruitTreeOptionEnumLletyaTeleport.Teleport_crystal; }

	enum FruitTreeOptionEnumTreeGnomeVillageTeleport implements OptionEnumTeleport
	{
		Royal_seed_pod,
		Spirit_Tree
	}
	@ConfigItem(
			position = 1,
			keyName = "enumFruitTreeTreeGnomeVillageTeleport",
			name = "Tree Gnome Village",
			description = "Desired way to teleport to Tree Gnome Village",
			section = fruitTreeTeleportOptionList
	)
	default FruitTreeOptionEnumTreeGnomeVillageTeleport enumFruitTreeTreeGnomeVillageTeleport() { return FruitTreeOptionEnumTreeGnomeVillageTeleport.Royal_seed_pod; }

	@ConfigSection(
			name = "Hops teleport options",
			description = "Choose what teleport to use for each Hops patch",
			position = 8,
			closedByDefault = true
	)
	String hopsTeleportOptionList = "hopsTeleportOptionList";

	enum HopsOptionEnumLumbridgeTeleport implements OptionEnumTeleport
	{
		Portal_Nexus,
		Teleport,
		Lumbridge_Tele_Tab,
		Chronicle
	}
	@ConfigItem(
			position = 1,
			keyName = "enumHopsLumbridgeTeleport",
			name = "Lumbridge",
			description = "Desired way to teleport to Lumbridge",
			section = hopsTeleportOptionList
	)
	default HopsOptionEnumLumbridgeTeleport enumHopsLumbridgeTeleport() { return HopsOptionEnumLumbridgeTeleport.Teleport; }

	enum HopsOptionEnumSeersVillageTeleport implements OptionEnumTeleport
	{
		Portal_Nexus_Camelot,
		Camelot_Teleport,
		Camelot_Tele_Tab,
		Seers_Village,
		Fairy_Ring
	}
	@ConfigItem(
			position = 2,
			keyName = "enumHopsSeersVillageTeleport",
			name = "Seers Village",
			description = "Desired way to teleport to Seers Village",
			section = hopsTeleportOptionList
	)
	default HopsOptionEnumSeersVillageTeleport enumHopsSeersVillageTeleport() { return HopsOptionEnumSeersVillageTeleport.Portal_Nexus_Camelot; }

	enum HopsOptionEnumYanilleTeleport implements OptionEnumTeleport
	{
		Portal_Nexus,
		Watchtower_Teleport,
		Yanille
	}
	@ConfigItem(
			position = 3,
			keyName = "enumHopsYanilleTeleport",
			name = "Yanille",
			description = "Desired way to teleport to Yanille",
			section = hopsTeleportOptionList
	)
	default HopsOptionEnumYanilleTeleport enumHopsYanilleTeleport() { return HopsOptionEnumYanilleTeleport.Portal_Nexus; }

	enum HopsOptionEnumEntranaTeleport implements OptionEnumTeleport
	{
		Explorers_Ring
	}
	@ConfigItem(
			position = 4,
			keyName = "enumHopsEntranaTeleport",
			name = "Entrana",
			description = "Desired way to teleport to Entrana",
			section = hopsTeleportOptionList
	)
	default HopsOptionEnumEntranaTeleport enumHopsEntranaTeleport() { return HopsOptionEnumEntranaTeleport.Explorers_Ring; }

	enum HopsOptionEnumAldarinTeleport implements OptionEnumTeleport
	{
		Portal_Nexus,
		Quetzal_Transport,
		Fairy_Ring
	}
	@ConfigItem(
			position = 5,
			keyName = "enumHopsAldarinTeleport",
			name = "Aldarin",
			description = "Desired way to teleport to Aldarin",
			section = hopsTeleportOptionList
	)
	default HopsOptionEnumAldarinTeleport enumHopsAldarinTeleport() { return HopsOptionEnumAldarinTeleport.Portal_Nexus; }
}