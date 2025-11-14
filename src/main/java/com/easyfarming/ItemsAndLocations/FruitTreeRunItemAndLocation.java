package com.easyfarming.ItemsAndLocations;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.ItemRequirement;
import com.easyfarming.Location;
import net.runelite.api.Client;
import net.runelite.api.gameval.ItemID;

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
        // NEW APPROACH: Using LocationData pattern for data-driven setup
        com.easyfarming.locations.LocationData brimhavenData = com.easyfarming.locations.fruittree.BrimhavenFruitTreeLocationData.create(
            () -> {
                List<ItemRequirement> requirements = getHouseTeleportItemRequirements();
                return requirements.stream()
                    .map(ir -> new com.easyfarming.core.ItemRequirement(ir.getItemId(), ir.getQuantity()))
                    .collect(java.util.stream.Collectors.toList());
            }
        );
        
        brimhavenFruitTreeLocation = com.easyfarming.locations.LocationFactory.createLocation(brimhavenData, config);
        locations.add(brimhavenFruitTreeLocation);
    }

    private void setupCatherbyLocations()
    {
        // NEW APPROACH: Using LocationData pattern for data-driven setup
        com.easyfarming.locations.LocationData catherbyData = com.easyfarming.locations.fruittree.CatherbyFruitTreeLocationData.create(
            () -> {
                List<ItemRequirement> requirements = getHouseTeleportItemRequirements();
                return requirements.stream()
                    .map(ir -> new com.easyfarming.core.ItemRequirement(ir.getItemId(), ir.getQuantity()))
                    .collect(java.util.stream.Collectors.toList());
            }
        );
        
        catherbyFruitTreeLocation = com.easyfarming.locations.LocationFactory.createLocation(catherbyData, config);
        locations.add(catherbyFruitTreeLocation);
    }

    private void setupFarmingGuildLocation()
    {
        // NEW APPROACH: Using LocationData pattern for data-driven setup
        com.easyfarming.locations.LocationData farmingGuildData = com.easyfarming.locations.fruittree.FarmingGuildFruitTreeLocationData.create(
            () -> {
                List<ItemRequirement> requirements = getHouseTeleportItemRequirements();
                return requirements.stream()
                    .map(ir -> new com.easyfarming.core.ItemRequirement(ir.getItemId(), ir.getQuantity()))
                    .collect(java.util.stream.Collectors.toList());
            }
        );
        
        farmingGuildFruitTreeLocation = com.easyfarming.locations.LocationFactory.createLocation(farmingGuildData, config);
        locations.add(farmingGuildFruitTreeLocation);
    }

    private void setupGnomeStrongholdLocation()
    {
        // NEW APPROACH: Using LocationData pattern for data-driven setup
        com.easyfarming.locations.LocationData gnomeStrongholdData = com.easyfarming.locations.fruittree.GnomeStrongholdFruitTreeLocationData.create();
        
        gnomeStrongholdFruitTreeLocation = com.easyfarming.locations.LocationFactory.createLocation(gnomeStrongholdData, config);
        locations.add(gnomeStrongholdFruitTreeLocation);
    }

    private void setupLletyaLocation()
    {
        // NEW APPROACH: Using LocationData pattern for data-driven setup
        com.easyfarming.locations.LocationData lletyaData = com.easyfarming.locations.fruittree.LletyaFruitTreeLocationData.create();
        
        lletyaFruitTreeLocation = com.easyfarming.locations.LocationFactory.createLocation(lletyaData, config);
        locations.add(lletyaFruitTreeLocation);
    }

    private void setupTreeGnomeVillage()
    {
        // NEW APPROACH: Using LocationData pattern for data-driven setup
        com.easyfarming.locations.LocationData treeGnomeVillageData = com.easyfarming.locations.fruittree.TreeGnomeVillageFruitTreeLocationData.create();
        
        treeGnomeVillageFruitTreeLocation = com.easyfarming.locations.LocationFactory.createLocation(treeGnomeVillageData, config);
        locations.add(treeGnomeVillageFruitTreeLocation);
    }
}