package com.easyfarming.ItemsAndLocations;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.ItemRequirement;
import com.easyfarming.Location;
import net.runelite.api.Client;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.coords.WorldPoint;

import java.util.*;

public class HerbRunItemAndLocation extends ItemAndLocation
{
    public Location ardougneLocation;
    public Location catherbyLocation;
    public Location faladorLocation;
    public Location farmingGuildLocation;
    public Location harmonyLocation;
    public Location kourendLocation;
    public Location morytaniaLocation;
    public Location trollStrongholdLocation;
    public Location weissLocation;

    public HerbRunItemAndLocation(EasyFarmingConfig config, Client client, EasyFarmingPlugin plugin)
    {
        super(
            config,
            client,
            plugin
        );
    }

    public Map<Integer, Integer> getHerbItems()
    {
        return getAllItemRequirements(locations);
    }

    public Map<Integer, Integer> getAllItemRequirements(List<Location> locations)
    {
        Map<Integer, Integer> allRequirements = new HashMap<>();

        setupLocations();

        // Add other items and merge them with allRequirements
        for (Location location : locations) {
            if (plugin.getHerbLocationEnabled(location.getName())) {
                //ItemID.GUAM_SEED is default for herb seeds, code later will allow for any seed to be used, just needed a placeholder ID
                allRequirements.merge(
                    ItemID.GUAM_SEED,
                    1,
                    Integer::sum
                );

                if (selectedCompostID() != - 1 && selectedCompostID() != ItemID.BOTTOMLESS_COMPOST_BUCKET) {
                    allRequirements.merge(
                        selectedCompostID(),
                        1,
                        Integer::sum
                    );
                }

                Location.Teleport teleport = location.getSelectedTeleport();

                Map<Integer, Integer> locationRequirements = teleport.getItemRequirements();

                for (Map.Entry<Integer, Integer> entry : locationRequirements.entrySet()) {
                    int itemId = entry.getKey();
                    int quantity = entry.getValue();

                    if (itemId == ItemID.SKILLCAPE_CONSTRUCTION || itemId == ItemID.SKILLCAPE_CONSTRUCTION_TRIMMED || itemId == ItemID.SKILLCAPE_MAX) {
                        allRequirements.merge(
                            itemId,
                            quantity,
                            (oldValue, newValue) -> Math.min(
                                1,
                                oldValue + newValue
                            )
                        );
                    } else {
                        allRequirements.merge(
                            itemId,
                            quantity,
                            Integer::sum
                        );
                    }
                }

                if (location.getFarmLimps() && config.generalLimpwurt()) {
                    allRequirements.merge(
                        ItemID.LIMPWURT_SEED,
                        1,
                        Integer::sum
                    );

                    if (selectedCompostID() != - 1 && selectedCompostID() != ItemID.BOTTOMLESS_COMPOST_BUCKET) {
                        allRequirements.merge(
                            selectedCompostID(),
                            1,
                            Integer::sum
                        );
                    }
                }
            }
        }
        if(config.generalSeedDibber()) {
            allRequirements.merge(
                ItemID.DIBBER,
                1,
                Integer::sum
            );
        }

        allRequirements.merge(
            ItemID.SPADE,
            1,
            Integer::sum
        );

        if (selectedCompostID() == ItemID.BOTTOMLESS_COMPOST_BUCKET) {
            allRequirements.merge(
                ItemID.BOTTOMLESS_COMPOST_BUCKET,
                1,
                Integer::sum
            );
        }

        allRequirements.merge(
            ItemID.FAIRY_ENCHANTED_SECATEURS,
            1,
            Integer::sum
        );

        if (config.generalRake()) {
            allRequirements.merge(
                ItemID.RAKE,
                1,
                Integer::sum
            );
        }

        return allRequirements;
    }

    public void setupLocations()
    {
        super.setupLocations();

        setupArdougneLocation();
        setupCatherbyLocation();
        setupFaladorLocation();
        setupFarmingGuildLocation();
        setupHarmonyLocation();
        setupKourendLocation();
        setupMorytaniaLocation();
        setupTrollStrongholdLocation();
        setupWeissLocation();
    }

    private void setupArdougneLocation()
    {
        WorldPoint ardougneHerbPatchPoint = new WorldPoint(
            2670,
            3374,
            0
        );

        ardougneLocation = new Location(
            EasyFarmingConfig::enumOptionEnumArdougneTeleport,
            config,
            "Ardougne",
            true
        );

        ardougneLocation.addTeleportOption(ardougneLocation.new Teleport(
            "Portal_Nexus",
            Location.TeleportCategory.PORTAL_NEXUS,
            "Teleport to Ardougne with Portal Nexus, and run north.",
            0,
            "null",
            17,
            13,
            10547,
            ardougneHerbPatchPoint,
            getHouseTeleportItemRequirements()
        ));

        ardougneLocation.addTeleportOption(ardougneLocation.new Teleport(
            "Ardougne_teleport",
            Location.TeleportCategory.SPELLBOOK,
            "Teleport to Ardougne with standard spellbook, and run north.",
            0,
            "null",
            218,
            41,
            10547,
            ardougneHerbPatchPoint,
            Arrays.asList(
                new ItemRequirement(
                    ItemID.LAWRUNE,
                    2
                ),
                new ItemRequirement(
                    ItemID.WATERRUNE,
                    2
                )
            )
        ));

        ardougneLocation.addTeleportOption(ardougneLocation.new Teleport(
            "Ardougne_Tele_Tab",
            Location.TeleportCategory.ITEM,
            "Teleport to Ardougne with Ardougne tele tab, and run north.",
            ItemID.POH_TABLET_ARDOUGNETELEPORT,
            "null",
            0,
            0,
            10547,
            ardougneHerbPatchPoint,
            Collections.singletonList(new ItemRequirement(
                ItemID.POH_TABLET_ARDOUGNETELEPORT,
                1
            ))
        ));

        ardougneLocation.addTeleportOption(ardougneLocation.new Teleport(
            "Ardy_Cloak_2",
            Location.TeleportCategory.ITEM,
            "Teleport to Ardougne Farm with Ardougne cloak.",
            ItemID.ARDY_CAPE_MEDIUM,
            "Farm Teleport",
            0,
            0,
            10548,
            ardougneHerbPatchPoint,
            Collections.singletonList(new ItemRequirement(
                ItemID.ARDY_CAPE_MEDIUM,
                1
            ))
        ));

        ardougneLocation.addTeleportOption(ardougneLocation.new Teleport(
            "Ardy_Cloak_3",
            Location.TeleportCategory.ITEM,
            "Teleport to Ardougne Farm with Ardougne cloak.",
            ItemID.ARDY_CAPE_HARD,
            "Farm Teleport",
            0,
            0,
            10548,
            ardougneHerbPatchPoint,
            Collections.singletonList(new ItemRequirement(
                ItemID.ARDY_CAPE_HARD,
                1
            ))
        ));

        ardougneLocation.addTeleportOption(ardougneLocation.new Teleport(
            "Ardy_Cloak_4",
            Location.TeleportCategory.ITEM,
            "Teleport to Ardougne Farm with Ardougne cloak.",
            ItemID.ARDY_CAPE_ELITE,
            "Farm Teleport",
            0,
            0,
            10548,
            ardougneHerbPatchPoint,
            Collections.singletonList(new ItemRequirement(
                ItemID.ARDY_CAPE_ELITE,
                1
            ))
        ));

        ardougneLocation.addTeleportOption(ardougneLocation.new Teleport(
            "Skills_Necklace",
            Location.TeleportCategory.ITEM,
            "Teleport to Fishing guild with Skills necklace, and run east.",
            ItemID.JEWL_NECKLACE_OF_SKILLS_1,
            "null",
            0,
            0,
            10292,
            ardougneHerbPatchPoint,
            Collections.singletonList(new ItemRequirement(
                ItemID.JEWL_NECKLACE_OF_SKILLS_1,
                1
            ))
        ));

        locations.add(ardougneLocation);
    }

    private void setupCatherbyLocation()
    {
        WorldPoint catherbyHerbPatchPoint = new WorldPoint(
            2813,
            3463,
            0
        );

        catherbyLocation = new Location(
            EasyFarmingConfig::enumOptionEnumCatherbyTeleport,
            config,
            "Catherby",
            true
        );

        catherbyLocation.addTeleportOption(catherbyLocation.new Teleport(
            "Portal_Nexus_Catherby",
            Location.TeleportCategory.PORTAL_NEXUS,
            "Teleport to Catherby with Portal Nexus.",
            0,
            "null",
            17,
            13,
            11061,
            catherbyHerbPatchPoint,
            getHouseTeleportItemRequirements()
        ));

        catherbyLocation.addTeleportOption(catherbyLocation.new Teleport(
            "Portal_Nexus_Camelot",
            Location.TeleportCategory.PORTAL_NEXUS,
            "Teleport to Camelot with Portal Nexus.",
            0,
            "null",
            17,
            13,
            11062,
            catherbyHerbPatchPoint,
            getHouseTeleportItemRequirements()
        ));

        catherbyLocation.addTeleportOption(catherbyLocation.new Teleport(
            "Camelot_Teleport",
            Location.TeleportCategory.SPELLBOOK,
            "Teleport to Camelot using the standard spellbook, and run east to Catherby herb patch.",
            0,
            "null",
            218,
            34,
            11062,
            catherbyHerbPatchPoint,
            Arrays.asList(
                new ItemRequirement(
                    ItemID.AIRRUNE,
                    5
                ),
                new ItemRequirement(
                    ItemID.LAWRUNE,
                    1
                )
            )
        ));

        catherbyLocation.addTeleportOption(catherbyLocation.new Teleport(
            "Camelot_Tele_Tab",
            Location.TeleportCategory.ITEM,
            "Teleport to Camelot using a Camelot tele tab, and run east to Catherby herb patch.",
            ItemID.POH_TABLET_CAMELOTTELEPORT,
            "null",
            0,
            0,
            11062,
            catherbyHerbPatchPoint,
            Collections.singletonList(new ItemRequirement(
                ItemID.POH_TABLET_CAMELOTTELEPORT,
                1
            ))
        ));

        catherbyLocation.addTeleportOption(catherbyLocation.new Teleport(
            "Catherby_Tele_Tab",
            Location.TeleportCategory.ITEM,
            "Teleport to Catherby using Catherby teleport tab.",
            ItemID.LUNAR_TABLET_CATHERBY_TELEPORT,
            "null",
            0,
            0,
            11061,
            catherbyHerbPatchPoint,
            Collections.singletonList(new ItemRequirement(
                ItemID.LUNAR_TABLET_CATHERBY_TELEPORT,
                1
            ))
        ));

        locations.add(catherbyLocation);
    }

    private void setupFaladorLocation()
    {
        WorldPoint faladorHerbPatchPoint = new WorldPoint(
            3058,
            3307,
            0
        );

        faladorLocation = new Location(
            EasyFarmingConfig::enumOptionEnumFaladorTeleport,
            config,
            "Falador",
            true
        );

        faladorLocation.addTeleportOption(faladorLocation.new Teleport(
            "Portal_Nexus",
            Location.TeleportCategory.PORTAL_NEXUS,
            "Teleport to Falador with Portal Nexus, and run south-east.",
            0,
            "null",
            17,
            13,
            11828,
            faladorHerbPatchPoint,
            getHouseTeleportItemRequirements()
        ));

        faladorLocation.addTeleportOption(faladorLocation.new Teleport(
            "Explorers_ring_2",
            Location.TeleportCategory.ITEM,
            "Teleport to Falador with Explorers ring, and run slightly north.",
            ItemID.LUMBRIDGE_RING_MEDIUM,
            "Teleport",
            0,
            0,
            12083,
            faladorHerbPatchPoint,
            Collections.singletonList(new ItemRequirement(
                ItemID.LUMBRIDGE_RING_MEDIUM,
                1
            ))
        ));

        faladorLocation.addTeleportOption(faladorLocation.new Teleport(
            "Explorers_ring_3",
            Location.TeleportCategory.ITEM,
            "Teleport to Falador with Explorers ring, and run slightly north.",
            ItemID.LUMBRIDGE_RING_HARD,
            "Teleport",
            0,
            0,
            12083,
            faladorHerbPatchPoint,
            Collections.singletonList(new ItemRequirement(
                ItemID.LUMBRIDGE_RING_HARD,
                1
            ))
        ));

        faladorLocation.addTeleportOption(faladorLocation.new Teleport(
            "Explorers_ring_4",
            Location.TeleportCategory.ITEM,
            "Teleport to Falador with Explorers ring, and run slightly north.",
            ItemID.LUMBRIDGE_RING_ELITE,
            "Teleport",
            0,
            0,
            12083,
            faladorHerbPatchPoint,
            Collections.singletonList(new ItemRequirement(
                ItemID.LUMBRIDGE_RING_ELITE,
                1
            ))
        ));

        faladorLocation.addTeleportOption(faladorLocation.new Teleport(
            "Falador_Teleport",
            Location.TeleportCategory.SPELLBOOK,
            "Teleport to Falador with standard spellbook, and run south-east.",
            0,
            "null",
            218,
            29,
            11828,
            faladorHerbPatchPoint,
            Arrays.asList(
                new ItemRequirement(
                    ItemID.AIRRUNE,
                    3
                ),
                new ItemRequirement(
                    ItemID.LAWRUNE,
                    1
                ),
                new ItemRequirement(
                    ItemID.WATERRUNE,
                    1
                )
            )
        ));

        faladorLocation.addTeleportOption(faladorLocation.new Teleport(
            "Falador_Tele_Tab",
            Location.TeleportCategory.ITEM,
            "Teleport to Falador with Falador Tele Tab, and run south-east.",
            ItemID.POH_TABLET_FALADORTELEPORT,
            "null",
            0,
            0,
            11828,
            faladorHerbPatchPoint,
            Collections.singletonList(new ItemRequirement(
                ItemID.POH_TABLET_FALADORTELEPORT,
                1
            ))
        ));

        faladorLocation.addTeleportOption(faladorLocation.new Teleport(
            "Draynor_Tele_Tab",
            Location.TeleportCategory.ITEM,
            "Teleport to Draynor Manor with Draynor Manor Tele Tab, and run south-west.",
            ItemID.TELETAB_DRAYNOR,
            "null",
            0,
            0,
            12340,
            faladorHerbPatchPoint,
            Collections.singletonList(new ItemRequirement(
                ItemID.TELETAB_DRAYNOR,
                1
            ))
        ));

        locations.add(faladorLocation);
    }

    private void setupFarmingGuildLocation()
    {
        WorldPoint farmingGuildHerbPatchPoint = new WorldPoint(
            1238,
            3726,
            0
        );

        farmingGuildLocation = new Location(
            EasyFarmingConfig::enumOptionEnumFarmingGuildTeleport,
            config,
            "Farming Guild",
            true
        );

        farmingGuildLocation.addTeleportOption(farmingGuildLocation.new Teleport(
            "Jewellery_box",
            Location.TeleportCategory.JEWELLERY_BOX,
            "Teleport to Farming guild with Jewellery box.",
            29155,
            "null",
            0,
            0,
            4922,
            farmingGuildHerbPatchPoint,
            getHouseTeleportItemRequirements()
        ));

        farmingGuildLocation.addTeleportOption(farmingGuildLocation.new Teleport(
            "Skills_Necklace",
            Location.TeleportCategory.ITEM,
            "Teleport to Farming guild using Skills necklace.",
            ItemID.JEWL_NECKLACE_OF_SKILLS_1,
            "null",
            0,
            0,
            4922,
            farmingGuildHerbPatchPoint,
            Collections.singletonList(new ItemRequirement(
                ItemID.JEWL_NECKLACE_OF_SKILLS_1,
                1
            ))
        ));

        locations.add(farmingGuildLocation);
    }

    private void setupHarmonyLocation()
    {
        WorldPoint harmonyHerbPatchPoint = new WorldPoint(
            3789,
            2837,
            0
        );

        harmonyLocation = new Location(
            EasyFarmingConfig::enumOptionEnumHarmonyTeleport,
            config,
            "Harmony Island",
            false
        );

        harmonyLocation.addTeleportOption(harmonyLocation.new Teleport(
            "Portal_Nexus",
            Location.TeleportCategory.PORTAL_NEXUS,
            "Teleport to Harmony with Portal Nexus.",
            0,
            "null",
            17,
            13,
            15148,
            harmonyHerbPatchPoint,
            getHouseTeleportItemRequirements()
        ));

        harmonyLocation.addTeleportOption(harmonyLocation.new Teleport(
            "Harmony_Tele_Tab",
            Location.TeleportCategory.ITEM,
            "Teleport to Harmony with Harmony Tele Tab.",
            ItemID.TELETAB_HARMONY,
            "null",
            0,
            0,
            15148,
            harmonyHerbPatchPoint,
            Collections.singletonList(new ItemRequirement(
                ItemID.TELETAB_HARMONY,
                1
            ))
        ));

        locations.add(harmonyLocation);
    }

    private void setupKourendLocation()
    {
        WorldPoint kourendHerbPatchPoint = new WorldPoint(
            1738,
            3550,
            0
        );

        kourendLocation = new Location(
            EasyFarmingConfig::enumOptionEnumKourendTeleport,
            config,
            "Kourend",
            true
        );

        kourendLocation.addTeleportOption(kourendLocation.new Teleport(
            "Xerics_Talisman",
            Location.TeleportCategory.ITEM,
            "Teleport to Kourend with Xeric's Talisman.",
            ItemID.XERIC_TALISMAN,
            "Rub",
            187,
            3,
            6967,
            kourendHerbPatchPoint,
            Collections.singletonList(new ItemRequirement(
                ItemID.XERIC_TALISMAN,
                1
            ))
        ));

        kourendLocation.addTeleportOption(kourendLocation.new Teleport(
            "Mounted_Xerics",
            Location.TeleportCategory.MOUNTED_XERICS,
            "Teleport to Kourend with Xeric's Talisman in PoH.",
            0,
            "null",
            187,
            3,
            6967,
            kourendHerbPatchPoint,
            getHouseTeleportItemRequirements()
        ));

        locations.add(kourendLocation);
    }

    private void setupMorytaniaLocation()
    {
        WorldPoint morytaniaHerbPatchPoint = new WorldPoint(
            3601,
            3525,
            0
        );

        morytaniaLocation = new Location(
            EasyFarmingConfig::enumOptionEnumMorytaniaTeleport,
            config,
            "Morytania",
            true
        );

        morytaniaLocation.addTeleportOption(morytaniaLocation.new Teleport(
            "Ectophial",
            Location.TeleportCategory.ITEM,
            "Teleport to Morytania with Ectophial.",
            ItemID.ECTOPHIAL,
            "null",
            0,
            0,
            14647,
            morytaniaHerbPatchPoint,
            Collections.singletonList(new ItemRequirement(
                ItemID.ECTOPHIAL,
                1
            ))
        ));

        locations.add(morytaniaLocation);
    }

    private void setupTrollStrongholdLocation()
    {
        WorldPoint trollStrongholdHerbPatchPoint = new WorldPoint(
            2824,
            3696,
            0
        );

        trollStrongholdLocation = new Location(
            EasyFarmingConfig::enumOptionEnumTrollStrongholdTeleport,
            config,
            "Troll Stronghold",
            false
        );

        trollStrongholdLocation.addTeleportOption(trollStrongholdLocation.new Teleport(
            "Stony_Basalt",
            Location.TeleportCategory.ITEM,
            "Teleport to Troll Stronghold with Stony Basalt.",
            ItemID.STRONGHOLD_TELEPORT_BASALT,
            "null",
            0,
            0,
            11321,
            trollStrongholdHerbPatchPoint,
            Collections.singletonList(new ItemRequirement(
                ItemID.STRONGHOLD_TELEPORT_BASALT,
                1
            ))
        ));

        trollStrongholdLocation.addTeleportOption(trollStrongholdLocation.new Teleport(
            "Portal_Nexus",
            Location.TeleportCategory.PORTAL_NEXUS,
            "Teleport to Troll Stronghold with Portal Nexus.",
            0,
            "null",
            17,
            13,
            11321,
            trollStrongholdHerbPatchPoint,
            getHouseTeleportItemRequirements()
        ));

        locations.add(trollStrongholdLocation);
    }

    private void setupWeissLocation()
    {
        WorldPoint weissHerbPatchPoint = new WorldPoint(
            2847,
            3931,
            0
        );

        weissLocation = new Location(
            EasyFarmingConfig::enumOptionEnumWeissTeleport,
            config,
            "Weiss",
            false
        );

        weissLocation.addTeleportOption(weissLocation.new Teleport(
            "Icy_Basalt",
            Location.TeleportCategory.ITEM,
            "Teleport to Weiss with Icy Basalt.",
            ItemID.WEISS_TELEPORT_BASALT,
            "null",
            0,
            0,
            11325,
            weissHerbPatchPoint,
            Collections.singletonList(new ItemRequirement(
                ItemID.WEISS_TELEPORT_BASALT,
                1
            ))
        ));

        weissLocation.addTeleportOption(weissLocation.new Teleport(
            "Portal_Nexus",
            Location.TeleportCategory.PORTAL_NEXUS,
            "Teleport to Weiss with Portal Nexus.",
            0,
            "null",
            17,
            13,
            11325,
            weissHerbPatchPoint,
            getHouseTeleportItemRequirements()
        ));

        locations.add(weissLocation);
    }
}