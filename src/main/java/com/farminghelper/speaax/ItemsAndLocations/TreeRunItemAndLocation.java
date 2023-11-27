package com.farminghelper.speaax.ItemsAndLocations;

import com.farminghelper.speaax.FarmingHelperConfig;
import com.farminghelper.speaax.FarmingHelperPlugin;
import com.farminghelper.speaax.ItemRequirement;
import com.farminghelper.speaax.Location;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.coords.WorldPoint;

import java.util.*;

public class TreeRunItemAndLocation extends ItemAndLocation
{
    public Location faladorTreeLocation;
    public Location farmingGuildTreeLocation;
    public Location gnomeStrongholdTreeLocation;
    public Location lumbridgeTreeLocation;
    public Location taverleyTreeLocation;
    public Location varrockTreeLocation;

    public TreeRunItemAndLocation()
    {
    }

    public TreeRunItemAndLocation(FarmingHelperConfig config, Client client, FarmingHelperPlugin plugin)
    {
        super(
            config,
            client,
            plugin
        );
    }

    public Map<Integer, Integer> getTreeItems()
    {
        return getAllItemRequirements(locations);
    }

    public Map<Integer, Integer> getAllItemRequirements(List<Location> locations)
    {
        Map<Integer, Integer> allRequirements = new HashMap<>();

        setupLocations();

        // Add other items and merge them with allRequirements
        for (Location location : locations) {
            if (plugin.getTreeLocationEnabled(location.getName())) {
                //ItemID.GUAM_SEED is default for herb seeds, code later will allow for any seed to be used, just needed a placeholder ID
                //allRequirements.merge(ItemID.GUAM_SEED, 1, Integer::sum);
                allRequirements.merge(
                    ItemID.OAK_SAPLING,
                    1,
                    Integer::sum
                );

                allRequirements.merge(
                    ItemID.COINS_995,
                    200,
                    Integer::sum
                );

                Location.Teleport teleport = location.getSelectedTeleport();

                Map<Integer, Integer> locationRequirements = teleport.getItemRequirements();

                for (Map.Entry<Integer, Integer> entry : locationRequirements.entrySet()) {
                    int itemId = entry.getKey();
                    int quantity = entry.getValue();

                    if (itemId == ItemID.CONSTRUCT_CAPE || itemId == ItemID.CONSTRUCT_CAPET || itemId == ItemID.MAX_CAPE) {
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
            }
        }

        //allRequirements.merge(ItemID.SEED_DIBBER, 1, Integer::sum);
        allRequirements.merge(
            ItemID.SPADE,
            1,
            Integer::sum
        );

        // TODO: Will be tricky, but a reminder to take the required items for protection?
        //       It will rely on knowing what saplings are in the inventory, and their quantity
        if (!config.generalPayForProtection()) {
            allRequirements.merge(
                ItemID.BOTTOMLESS_COMPOST_BUCKET_22997,
                1,
                Integer::sum
            );
        }

        allRequirements.merge(
            ItemID.MAGIC_SECATEURS,
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

        setupFaladorLocation();
        setupFarmingGuildLocation();
        setupGnomeStrongholdLocation();
        setupLumbridgeLocation();
        setupTaverleyLocation();
        setupVarrockLocation();
    }

    private void setupFaladorLocation()
    {
        WorldPoint faladorTreePatchPoint = new WorldPoint(
            3000,
            3373,
            0
        );

        faladorTreeLocation = new Location(
            FarmingHelperConfig::enumTreeFaladorTeleport,
            config,
            "Falador",
            false
        );

        faladorTreeLocation.addTeleportOption(faladorTreeLocation.new Teleport(
            "Portal_Nexus",
            Location.TeleportCategory.PORTAL_NEXUS,
            "Teleport to Falador with Portal Nexus.",
            0,
            "null",
            17,
            13,
            11828,
            faladorTreePatchPoint,
            getHouseTeleportItemRequirements()
        ));

        faladorTreeLocation.addTeleportOption(faladorTreeLocation.new Teleport(
            "Falador_teleport",
            Location.TeleportCategory.SPELLBOOK,
            "Teleport to Falador with Spellbook and run to Falador park.",
            0,
            "null",
            218,
            27,
            11828,
            faladorTreePatchPoint,
            Arrays.asList(
                new ItemRequirement(
                    ItemID.AIR_RUNE,
                    3
                ),
                new ItemRequirement(
                    ItemID.LAW_RUNE,
                    1
                ),
                new ItemRequirement(
                    ItemID.WATER_RUNE,
                    1
                )
            )
        ));

        locations.add(faladorTreeLocation);
    }

    private void setupFarmingGuildLocation()
    {
        WorldPoint farmingGuildTreePatchPoint = new WorldPoint(
            1232,
            3736,
            0
        );

        farmingGuildTreeLocation = new Location(
            FarmingHelperConfig::enumTreeFarmingGuildTeleport,
            config,
            "Farming Guild",
            false
        );

        farmingGuildTreeLocation.addTeleportOption(farmingGuildTreeLocation.new Teleport(
            "Jewellery_box",
            Location.TeleportCategory.JEWELLERY_BOX,
            "Teleport to Farming Guild with Jewellery box.",
            0,
            "null",
            0,
            0,
            4922,
            farmingGuildTreePatchPoint,
            getHouseTeleportItemRequirements()
        ));

        locations.add(farmingGuildTreeLocation);
    }

    private void setupGnomeStrongholdLocation()
    {
        WorldPoint gnomeStrongholdTreePatchPoint = new WorldPoint(
            2436,
            3415,
            0
        );

        gnomeStrongholdTreeLocation = new Location(
            FarmingHelperConfig::enumTreeGnomeStrongoldTeleport,
            config,
            "Gnome Stronghold",
            false
        );

        gnomeStrongholdTreeLocation.addTeleportOption(gnomeStrongholdTreeLocation.new Teleport(
            "Royal_seed_pod",
            Location.TeleportCategory.ITEM,
            "Teleport to Gnome Stronghold with Royal seed pod.",
            ItemID.ROYAL_SEED_POD,
            "null",
            0,
            0,
            9782,
            gnomeStrongholdTreePatchPoint,
            Collections.singletonList(new ItemRequirement(
                ItemID.ROYAL_SEED_POD,
                1
            ))
        ));

        gnomeStrongholdTreeLocation.addTeleportOption(gnomeStrongholdTreeLocation.new Teleport(
            "Spirit_Tree",
            Location.TeleportCategory.SPIRIT_TREE,
            "Teleport to Gnome Stronghold via a Spirit Tree.",
            0,
            "null",
            187,
            3,
            9781,
            gnomeStrongholdTreePatchPoint,
            Collections.<ItemRequirement> emptyList()
        ));

        locations.add(gnomeStrongholdTreeLocation);
    }

    private void setupLumbridgeLocation()
    {
        WorldPoint lumbridgeTreePatchPoint = new WorldPoint(
            3193,
            3231,
            0
        );

        lumbridgeTreeLocation = new Location(
            FarmingHelperConfig::enumTreeLumbridgeTeleport,
            config,
            "Lumbridge",
            false
        );

        lumbridgeTreeLocation.addTeleportOption(lumbridgeTreeLocation.new Teleport(
            "Portal_Nexus",
            Location.TeleportCategory.PORTAL_NEXUS,
            "Teleport to Lumbridge with Portal Nexus.",
            0,
            "null",
            17,
            13,
            12850,
            lumbridgeTreePatchPoint,
            getHouseTeleportItemRequirements()
        ));

        lumbridgeTreeLocation.addTeleportOption(lumbridgeTreeLocation.new Teleport(
            "Lumbridge_teleport",
            Location.TeleportCategory.SPELLBOOK,
            "Teleport to Lumbridge with spellbook.",
            0,
            "null",
            218,
            24,
            12850,
            lumbridgeTreePatchPoint,
            Arrays.asList(
                new ItemRequirement(
                    ItemID.AIR_RUNE,
                    3
                ),
                new ItemRequirement(
                    ItemID.LAW_RUNE,
                    1
                ),
                new ItemRequirement(
                    ItemID.EARTH_RUNE,
                    1
                )
            )
        ));

        locations.add(lumbridgeTreeLocation);
    }

    private void setupTaverleyLocation()
    {
        WorldPoint taverlyPatchPoint = new WorldPoint(
            2936,
            3438,
            0
        );

        taverleyTreeLocation = new Location(
            FarmingHelperConfig::enumTreeTaverleyTeleport,
            config,
            "Taverley",
            false
        );

        taverleyTreeLocation.addTeleportOption(taverleyTreeLocation.new Teleport(
            "Portal_Nexus",
            Location.TeleportCategory.PORTAL_NEXUS,
            "Teleport to Falador with Portal Nexus and run to Taverly.",
            0,
            "null",
            17,
            13,
            11828,
            taverlyPatchPoint,
            getHouseTeleportItemRequirements()
        ).overrideLocationName("Falador"));

        taverleyTreeLocation.addTeleportOption(taverleyTreeLocation.new Teleport(
            "Falador_teleport",
            Location.TeleportCategory.SPELLBOOK,
            "Teleport to Falador with spellbook and run to Taverly.",
            0,
            "null",
            218,
            27,
            11828,
            taverlyPatchPoint,
            Arrays.asList(
                new ItemRequirement(
                    ItemID.AIR_RUNE,
                    3
                ),
                new ItemRequirement(
                    ItemID.LAW_RUNE,
                    1
                ),
                new ItemRequirement(
                    ItemID.WATER_RUNE,
                    1
                )
            )
        ));

        locations.add(taverleyTreeLocation);
    }

    private void setupVarrockLocation()
    {
        WorldPoint varrockTreePatchPoint = new WorldPoint(
            3229,
            3459,
            0
        );

        varrockTreeLocation = new Location(
            FarmingHelperConfig::enumTreeVarrockTeleport,
            config,
            "Varrock",
            false
        );

        varrockTreeLocation.addTeleportOption(varrockTreeLocation.new Teleport(
            "Portal_Nexus",
            Location.TeleportCategory.PORTAL_NEXUS,
            "Teleport to Varrock with Portal Nexus.",
            0,
            "null",
            17,
            13,
            12853,
            varrockTreePatchPoint,
            getHouseTeleportItemRequirements()
        ));

        varrockTreeLocation.addTeleportOption(varrockTreeLocation.new Teleport(
            "Varrock_teleport",
            Location.TeleportCategory.SPELLBOOK,
            "Teleport to Varrock with spellbook.",
            0,
            "null",
            218,
            21,
            12853,
            varrockTreePatchPoint,
            Arrays.asList(
                new ItemRequirement(
                    ItemID.AIR_RUNE,
                    3
                ),
                new ItemRequirement(
                    ItemID.LAW_RUNE,
                    1
                ),
                new ItemRequirement(
                    ItemID.FIRE_RUNE,
                    1
                )
            )
        ));

        locations.add(varrockTreeLocation);
    }
}