package com.farminghelper.speaax.ItemsAndLocations;

import com.farminghelper.speaax.FarmingHelperConfig;
import com.farminghelper.speaax.FarmingHelperPlugin;
import com.farminghelper.speaax.Patch.Location;
import com.farminghelper.speaax.Patch.PatchType;
import com.farminghelper.speaax.Patch.Teleport;
import net.runelite.api.ItemID;

import java.util.*;

public class HerbRunItemAndLocation extends ItemAndLocation
{
    public HerbRunItemAndLocation()
    {
    }

    public HerbRunItemAndLocation(FarmingHelperConfig config, FarmingHelperPlugin plugin)
    {
        super(config, plugin);
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

                if (selectedCompostID() != - 1 && selectedCompostID() != ItemID.BOTTOMLESS_COMPOST_BUCKET_22997) {
                    allRequirements.merge(
                        selectedCompostID(),
                        1,
                        Integer::sum
                    );
                }

                Teleport teleport = location.desiredTeleport(PatchType.HERB, config);

                Map<Integer, Integer> locationRequirements = teleport.getItemRequirements(config);

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

                if (location.patch(PatchType.HERB).shouldFarmLimpwurts() && config.generalLimpwurt()) {
                    allRequirements.merge(
                        ItemID.LIMPWURT_SEED,
                        1,
                        Integer::sum
                    );

                    if (selectedCompostID() != - 1 && selectedCompostID() != ItemID.BOTTOMLESS_COMPOST_BUCKET_22997) {
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
                ItemID.SEED_DIBBER,
                1,
                Integer::sum
            );
        }

        allRequirements.merge(
            ItemID.SPADE,
            1,
            Integer::sum
        );

        if (selectedCompostID() == ItemID.BOTTOMLESS_COMPOST_BUCKET_22997) {
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

        locations.add(Location.ARDOUGNE);
        locations.add(Location.CATHERBY);
        locations.add(Location.FALADOR);
        locations.add(Location.FARMING_GUILD);
        locations.add(Location.HARMONY_ISLAND);
        locations.add(Location.KOUREND);
        locations.add(Location.MORYTANIA);
        locations.add(Location.TROLL_STRONGHOLD);
        locations.add(Location.WEISS);
    }
}