package com.easyfarming.ItemsAndLocations;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.ItemRequirement;
import com.easyfarming.Location;
import net.runelite.api.Client;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.coords.WorldPoint;

import java.util.*;

public class FruitTreeRunItemAndLocation extends ItemAndLocation
{
    public Location brimhavenFruitTreeLocation;
    public Location catherbyFruitTreeLocation;
    public Location farmingGuildFruitTreeLocation;
    public Location gnomeStrongholdFruitTreeLocation;
    public Location lletyaFruitTreeLocation;
    public Location treeGnomeVillageFruitTreeLocation;

    public FruitTreeRunItemAndLocation()
    {
    }

    public FruitTreeRunItemAndLocation(EasyFarmingConfig config, Client client, EasyFarmingPlugin plugin)
    {
        super(
            config,
            client,
            plugin
        );
    }

    public Map<Integer, Integer> getFruitTreeItems()
    {
        return getAllItemRequirements(locations);
    }

    public Map<Integer, Integer> getAllItemRequirements(List<Location> locations)
    {
        Map<Integer, Integer> allRequirements = new HashMap<>();

        setupLocations();

        // Add other items and merge them with allRequirements
        for (Location location : locations) {
            if (plugin.getFruitTreeLocationEnabled(location.getName())) {
                //ItemID.GUAM_SEED is default for herb seeds, code later will allow for any seed to be used, just needed a placeholder ID
                //allRequirements.merge(ItemID.GUAM_SEED, 1, Integer::sum);
                allRequirements.merge(
                    ItemID.PLANTPOT_APPLE_SAPLING,
                    1,
                    Integer::sum
                );

                allRequirements.merge(
                    ItemID.COINS,
                    200,
                    Integer::sum
                );

                if (selectedCompostID() != -1 && selectedCompostID() != ItemID.BOTTOMLESS_COMPOST_BUCKET) {
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

                    if (itemId == ItemID.SKILLCAPE_CONSTRUCTION || itemId == ItemID.SKILLCAPE_CONSTRUCTION_TRIMMED || itemId == ItemID.SKILLCAPE_MAX || itemId == ItemID.MM2_ROYAL_SEED_POD) {
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

        // Only add bottomless compost bucket if it's selected in config
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

        setupBrimhavenLocations();
        setupCatherbyLocations();
        setupFarmingGuildLocation();
        setupGnomeStrongholdLocation();
        setupLletyaLocation();
        setupTreeGnomeVillage();
    }

    private void setupBrimhavenLocations()
    {
        WorldPoint brimhavenFruitTreePatchPoint = new WorldPoint(
            2764,
            3212,
            0
        );

        brimhavenFruitTreeLocation = new Location(
            EasyFarmingConfig::enumFruitTreeBrimhavenTeleport,
            config,
            "Brimhaven",
            false
        );

        brimhavenFruitTreeLocation.addTeleportOption(brimhavenFruitTreeLocation.new Teleport(
            "Portal_Nexus",
            Location.TeleportCategory.PORTAL_NEXUS,
            "Teleport to Ardougne with Portal Nexus and take the boat to Brimhaven.",
            0,
            "null",
            17,
            13,
            10547,
            brimhavenFruitTreePatchPoint,
            getHouseTeleportItemRequirements()
        ));

        brimhavenFruitTreeLocation.addTeleportOption(brimhavenFruitTreeLocation.new Teleport(
            "Ardougne_teleport",
            Location.TeleportCategory.SPELLBOOK,
            "Teleport to Ardougne with Spellbook and take the boat to Brimhaven.",
            0,
            "null",
            218,
            41,
            10547,
            brimhavenFruitTreePatchPoint,
            Arrays.asList(
                new ItemRequirement(
                    ItemID.COINS,
                    30
                ),
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

        locations.add(brimhavenFruitTreeLocation);
    }

    private void setupCatherbyLocations()
    {
        WorldPoint cathebyFruitTreePatchPoint = new WorldPoint(
            2860,
            3433,
            0
        );

        catherbyFruitTreeLocation = new Location(
            EasyFarmingConfig::enumFruitTreeCatherbyTeleport,
            config,
            "Catherby",
            false
        );

        catherbyFruitTreeLocation.addTeleportOption(catherbyFruitTreeLocation.new Teleport(
            "Portal_Nexus_Catherby",
            Location.TeleportCategory.PORTAL_NEXUS,
            "Teleport to Catherby with Portal Nexus.",
            0,
            "null",
            17,
            13,
            11061,
            cathebyFruitTreePatchPoint,
            getHouseTeleportItemRequirements()
        ));

        catherbyFruitTreeLocation.addTeleportOption(catherbyFruitTreeLocation.new Teleport(
            "Portal_Nexus_Camelot",
            Location.TeleportCategory.PORTAL_NEXUS,
            "Teleport to Camelot with Portal Nexus.",
            0,
            "null",
            17,
            13,
            11062,
            cathebyFruitTreePatchPoint,
            getHouseTeleportItemRequirements()
        ));

        catherbyFruitTreeLocation.addTeleportOption(catherbyFruitTreeLocation.new Teleport(
            "Camelot_Teleport",
            Location.TeleportCategory.SPELLBOOK,
            "Teleport to Camelot using the standard spellbook, and run east. (If you have configured the teleport to seers you need to right click and teleport to Camelot)",
            0,
            "null",
            218,
            34,
            11062,
            cathebyFruitTreePatchPoint,
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

        catherbyFruitTreeLocation.addTeleportOption(catherbyFruitTreeLocation.new Teleport(
            "Camelot_Tele_Tab",
            Location.TeleportCategory.ITEM,
            "Teleport to Camelot using a Camelot tele tab, and run east.(If you have configured the teleport to seers you need to right click and teleport to Camelot)",
            ItemID.POH_TABLET_CAMELOTTELEPORT,
            "null",
            0,
            0,
            11062,
            cathebyFruitTreePatchPoint,
            Collections.singletonList(new ItemRequirement(
                ItemID.POH_TABLET_CAMELOTTELEPORT,
                1
            ))
        ));

        catherbyFruitTreeLocation.addTeleportOption(catherbyFruitTreeLocation.new Teleport(
            "Catherby_Tele_Tab",
            Location.TeleportCategory.ITEM,
            "Teleport to Catherby using Catherby teleport tab.",
            ItemID.LUNAR_TABLET_CATHERBY_TELEPORT,
            "null",
            0,
            0,
            11061,
            cathebyFruitTreePatchPoint,
            Collections.singletonList(new ItemRequirement(
                ItemID.LUNAR_TABLET_CATHERBY_TELEPORT,
                1
            ))
        ));

        locations.add(catherbyFruitTreeLocation);
    }

    private void setupFarmingGuildLocation()
    {
        WorldPoint farmingGuildFruitTreePatchPoint = new WorldPoint(
            1243,
            3759,
            0
        );

        farmingGuildFruitTreeLocation = new Location(
            EasyFarmingConfig::enumFruitTreeFarmingGuildTeleport,
            config,
            "Farming Guild",
            false
        );

        farmingGuildFruitTreeLocation.addTeleportOption(farmingGuildFruitTreeLocation.new Teleport(
            "Jewellery_box",
            Location.TeleportCategory.JEWELLERY_BOX,
            "Teleport to Farming Guild with Jewellery box.",
            0,
            "null",
            17,
            13,
            4922,
            farmingGuildFruitTreePatchPoint,
            getHouseTeleportItemRequirements()
        ));

        farmingGuildFruitTreeLocation.addTeleportOption(farmingGuildFruitTreeLocation.new Teleport(
            "Skills_Necklace",
            Location.TeleportCategory.ITEM,
            "Teleport to Farming guild using Skills necklace.",
            ItemID.JEWL_NECKLACE_OF_SKILLS_1,
            "null",
            0,
            0,
            4922,
            farmingGuildFruitTreePatchPoint,
            Collections.singletonList(new ItemRequirement(
                ItemID.JEWL_NECKLACE_OF_SKILLS_1,
                1
            ))
        ));

        locations.add(farmingGuildFruitTreeLocation);
    }

    private void setupGnomeStrongholdLocation()
    {
        WorldPoint gnomeStrongholdFruitTreePatchPoint = new WorldPoint(
            2475,
            3446,
            0
        );

        gnomeStrongholdFruitTreeLocation = new Location(
            EasyFarmingConfig::enumFruitTreeGnomeStrongholdTeleport,
            config,
            "Gnome Stronghold",
            false
        );

        gnomeStrongholdFruitTreeLocation.addTeleportOption(gnomeStrongholdFruitTreeLocation.new Teleport(
            "Royal_seed_pod",
            Location.TeleportCategory.ITEM,
            "Teleport to Gnome Stronghold with Royal seed pod.",
            ItemID.MM2_ROYAL_SEED_POD,
            "null",
            0,
            0,
            9782,
            gnomeStrongholdFruitTreePatchPoint,
            Collections.singletonList(new ItemRequirement(
                ItemID.MM2_ROYAL_SEED_POD,
                1
            ))
        ));

        gnomeStrongholdFruitTreeLocation.addTeleportOption(gnomeStrongholdFruitTreeLocation.new Teleport(
            "Spirit_Tree",
            Location.TeleportCategory.SPIRIT_TREE,
            "Teleport to Gnome Stronghold via a Spirit Tree.",
            0,
            "null",
            187,
            3,
            9781,
            gnomeStrongholdFruitTreePatchPoint,
            Collections.<ItemRequirement> emptyList()
        ));

        locations.add(gnomeStrongholdFruitTreeLocation);
    }

    private void setupLletyaLocation()
    {
        WorldPoint lletyaFruitTreePatchPoint = new WorldPoint(
            2346,
            3162,
            0
        );

        lletyaFruitTreeLocation = new Location(
            EasyFarmingConfig::enumFruitTreeLletyaTeleport,
            config,
            "Lletya",
            false
        );

        lletyaFruitTreeLocation.addTeleportOption(lletyaFruitTreeLocation.new Teleport(
            "Teleport_crystal",
            Location.TeleportCategory.ITEM,
            "Teleport to Lletya with Teleport crystal.",
            ItemID.MOURNING_TELEPORT_CRYSTAL_1,
            "null",
            0,
            0,
            9265,
            lletyaFruitTreePatchPoint,
            Collections.singletonList(new ItemRequirement(
                ItemID.MOURNING_TELEPORT_CRYSTAL_1,
                1
            ))
        ));

        locations.add(lletyaFruitTreeLocation);
    }

    private void setupTreeGnomeVillage()
    {
        WorldPoint treeGnomeVillageFruitTreePatchPoint = new WorldPoint(
            2490,
            3180,
            0
        );

        treeGnomeVillageFruitTreeLocation = new Location(
            EasyFarmingConfig::enumFruitTreeTreeGnomeVillageTeleport,
            config,
            "Tree Gnome Village",
            false
        );

        treeGnomeVillageFruitTreeLocation.addTeleportOption(treeGnomeVillageFruitTreeLocation.new Teleport(
            "Royal_seed_pod",
            Location.TeleportCategory.ITEM,
            "Teleport to Tree Gnome Village with Royal seed pod and use Spirit tree to Tree Gnome Village.",
            ItemID.MM2_ROYAL_SEED_POD,
            "null",
            0,
            0,
            9782,
            treeGnomeVillageFruitTreePatchPoint,
            Collections.singletonList(new ItemRequirement(
                ItemID.MM2_ROYAL_SEED_POD,
                1
            ))
        ));

        treeGnomeVillageFruitTreeLocation.addTeleportOption(treeGnomeVillageFruitTreeLocation.new Teleport(
            "Spirit_Tree",
            Location.TeleportCategory.SPIRIT_TREE,
            "Teleport to Tree Gnome Village via a Spirit Tree.",
            0,
            "null",
            187,
            3,
            10033,
            treeGnomeVillageFruitTreePatchPoint,
            Collections.<ItemRequirement> emptyList()
        ));

        locations.add(treeGnomeVillageFruitTreeLocation);
    }
}