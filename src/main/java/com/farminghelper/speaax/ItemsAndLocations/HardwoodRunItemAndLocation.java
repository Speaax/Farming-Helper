package com.farminghelper.speaax.ItemsAndLocations;

import com.farminghelper.speaax.FarmingHelperConfig;
import com.farminghelper.speaax.FarmingHelperPlugin;
import com.farminghelper.speaax.ItemRequirement;
import com.farminghelper.speaax.Location;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.coords.WorldPoint;

import java.util.*;

public class HardwoodRunItemAndLocation extends ItemAndLocation
{
    public Location hardwoodFossilIsland;

    public HardwoodRunItemAndLocation()
    {
    }

    public HardwoodRunItemAndLocation(FarmingHelperConfig config, Client client, FarmingHelperPlugin plugin)
    {
        super(
            config,
            client,
            plugin
        );
    }

    public Map<Integer, Integer> getHardwoodItems()
    {
        return getAllItemRequirements(locations);
    }

    public Map<Integer, Integer> getAllItemRequirements(List<Location> locations)
    {
        Map<Integer, Integer> allRequirements = new HashMap<>();

        setupLocations();

        // Add other items and merge them with allRequirements
        for (Location location : locations) {
            if (plugin.getHardwoodLocationEnabled(location.getName())) {
                //ItemID.GUAM_SEED is default for herb seeds, code later will allow for any seed to be used, just needed a placeholder ID
                //allRequirements.merge(ItemID.GUAM_SEED, 1, Integer::sum);

                allRequirements.merge(
                    ItemID.COINS_995,
                    600,
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

        allRequirements.merge(
            ItemID.TEAK_SAPLING,
            3,
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

        setupFossilIsland();
    }


    private void setupFossilIsland()
    {
        WorldPoint fossilIslandTreePatchPoint = new WorldPoint(
            3715,
            3835,
            0
        );

        hardwoodFossilIsland = new Location(
            FarmingHelperConfig::optionEnumFossilIslandEastTeleport,
            config,
            "Fossil Island",
            false
        );

        hardwoodFossilIsland.addTeleportOption(hardwoodFossilIsland.new Teleport(
            "Digsite_pendant",
            Location.TeleportCategory.ITEM,
            "Teleport to the Digsite with the Digsite Pendant, then Quick-Travel on the easterly barge and run to the Fossil Island hardwood tree patches via the beach tunnel",
            ItemID.DIGSITE_PENDANT_1,
            "Rub",
            219,
            1,
            13365,
            fossilIslandTreePatchPoint,
            Collections.singletonList(new ItemRequirement(
                ItemID.DIGSITE_PENDANT_1,
                1
            ))
        ));

        hardwoodFossilIsland.addTeleportOption(hardwoodFossilIsland.new Teleport(
            "Mounted_Digsite_pendant",
            Location.TeleportCategory.MOUNTED_DIGSITE,
            "Teleport to the Digsite with the mounted Digsite Pendant in your PoH, then Quick-Travel on the easterly barge and run to the Fossil Island hardwood tree patches via the beach tunnel.",
            0,
            "null",
            187,
            3,
            13365,
            fossilIslandTreePatchPoint,
            getHouseTeleportItemRequirements()
        ));

        locations.add(hardwoodFossilIsland);
    }
}