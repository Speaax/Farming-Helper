package com.farminghelper.speaax.ItemsAndLocations;

import com.farminghelper.speaax.FarmingHelperConfig;
import com.farminghelper.speaax.FarmingHelperPlugin;
import com.farminghelper.speaax.Patch.Location;
import com.farminghelper.speaax.Patch.Teleport;
import net.runelite.api.ItemID;
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

    public FruitTreeRunItemAndLocation(FarmingHelperConfig config, FarmingHelperPlugin plugin)
    {
        super(config, plugin);
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
                    ItemID.APPLE_SAPLING,
                    1,
                    Integer::sum
                );

                allRequirements.merge(
                    ItemID.COINS_995,
                    200,
                    Integer::sum
                );

                Teleport teleport = location.getDesiredTeleport(config);

                Map<Integer, Integer> locationRequirements = teleport.getItemRequirements();

                for (Map.Entry<Integer, Integer> entry : locationRequirements.entrySet()) {
                    int itemId = entry.getKey();
                    int quantity = entry.getValue();

                    if (itemId == ItemID.CONSTRUCT_CAPE || itemId == ItemID.CONSTRUCT_CAPET || itemId == ItemID.MAX_CAPE || itemId == ItemID.ROYAL_SEED_POD) {
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

        allRequirements.merge(
            ItemID.BOTTOMLESS_COMPOST_BUCKET_22997,
            1,
            Integer::sum
        );

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

        brimhavenFruitTreeLocation = Location.BRIMHAVEN;

        brimhavenFruitTreeLocation.addTeleportOption(new Teleport(
            "Portal_Nexus",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Ardougne with Portal Nexus and take the boat to Brimhaven.",
            0,
            "null",
            17,
            13,
            10547,
            brimhavenFruitTreePatchPoint,
            getHouseTeleportItemRequirements()
        ).overrideLocationName("Ardougne"));

        brimhavenFruitTreeLocation.addTeleportOption(new Teleport(
            "Ardougne_teleport",
            Teleport.Category.SPELLBOOK,
            "Teleport to Ardougne with Spellbook and take the boat to Brimhaven.",
            0,
            "null",
            218,
            38,
            10547,
            brimhavenFruitTreePatchPoint,
            Arrays.asList(
                new ItemRequirement(
                    ItemID.COINS_995,
                    30
                ),
                new ItemRequirement(
                    ItemID.LAW_RUNE,
                    2
                ),
                new ItemRequirement(
                    ItemID.WATER_RUNE,
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

        catherbyFruitTreeLocation = Location.CATHERBY;

        catherbyFruitTreeLocation.addTeleportOption(new Teleport(
            "Portal_Nexus_Catherby",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Catherby with Portal Nexus.",
            0,
            "null",
            17,
            13,
            11061,
            cathebyFruitTreePatchPoint,
            getHouseTeleportItemRequirements()
        ));

        catherbyFruitTreeLocation.addTeleportOption(new Teleport(
            "Portal_Nexus_Camelot",
            Teleport.Category.PORTAL_NEXUS,
            "Teleport to Camelot with Portal Nexus and run to Catherby.",
            0,
            "null",
            17,
            13,
            11062,
            cathebyFruitTreePatchPoint,
            getHouseTeleportItemRequirements()
        ).overrideLocationName("Camelot"));

        locations.add(catherbyFruitTreeLocation);
    }

    private void setupFarmingGuildLocation()
    {
        WorldPoint farmingGuildFruitTreePatchPoint = new WorldPoint(
            1243,
            3759,
            0
        );

        farmingGuildFruitTreeLocation = Location.FARMING_GUILD;

        farmingGuildFruitTreeLocation.addTeleportOption(new Teleport(
            "Jewellery_box",
            Teleport.Category.JEWELLERY_BOX,
            "Teleport to Farming Guild with Jewellery box.",
            0,
            "null",
            17,
            13,
            4922,
            farmingGuildFruitTreePatchPoint,
            getHouseTeleportItemRequirements()
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

        gnomeStrongholdFruitTreeLocation = Location.GNOME_STRONGHOLD;

        gnomeStrongholdFruitTreeLocation.addTeleportOption(new Teleport(
            "Royal_seed_pod",
            Teleport.Category.ITEM,
            "Teleport to Gnome Stronghold with Royal seed pod.",
            ItemID.ROYAL_SEED_POD,
            "null",
            0,
            0,
            9782,
            gnomeStrongholdFruitTreePatchPoint,
            Collections.singletonList(new ItemRequirement(
                ItemID.ROYAL_SEED_POD,
                1
            ))
        ));

        gnomeStrongholdFruitTreeLocation.addTeleportOption(new Teleport(
            "Spirit_Tree",
            Teleport.Category.SPIRIT_TREE,
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

        lletyaFruitTreeLocation = Location.LLETYA;

        lletyaFruitTreeLocation.addTeleportOption(new Teleport(
            "Teleport_crystal",
            Teleport.Category.ITEM,
            "Teleport to Lletya with Teleport crystal.",
            ItemID.TELEPORT_CRYSTAL_1,
            "null",
            0,
            0,
            9265,
            lletyaFruitTreePatchPoint,
            Collections.singletonList(new ItemRequirement(
                ItemID.TELEPORT_CRYSTAL_1,
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

        treeGnomeVillageFruitTreeLocation = Location.TREE_GNOME_VILLAGE;

        treeGnomeVillageFruitTreeLocation.addTeleportOption(new Teleport(
            "Royal_seed_pod",
            Teleport.Category.ITEM,
            "Teleport to Tree Gnome Village with Royal seed pod and use Spirit tree to Tree Gnome Village.",
            ItemID.ROYAL_SEED_POD,
            "null",
            0,
            0,
            9782,
            treeGnomeVillageFruitTreePatchPoint,
            Collections.singletonList(new ItemRequirement(
                ItemID.ROYAL_SEED_POD,
                1
            ))
        ));

        treeGnomeVillageFruitTreeLocation.addTeleportOption(new Teleport(
            "Spirit_Tree",
            Teleport.Category.SPIRIT_TREE,
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