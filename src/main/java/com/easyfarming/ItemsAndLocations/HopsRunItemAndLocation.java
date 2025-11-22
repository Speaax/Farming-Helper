package com.easyfarming.ItemsAndLocations;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.ItemRequirement;
import com.easyfarming.Location;
import com.easyfarming.utils.Constants;
import net.runelite.api.Client;
import net.runelite.api.gameval.ItemID;

import java.util.*;
import java.util.function.Supplier;

public class HopsRunItemAndLocation extends ItemAndLocation
{
    public Location lumbridgeHopsLocation;
    public Location seersVillageHopsLocation;
    public Location yanilleHopsLocation;
    public Location entranaHopsLocation;
    public Location aldarinHopsLocation;

    public HopsRunItemAndLocation()
    {
    }

    public HopsRunItemAndLocation(EasyFarmingConfig config, Client client, EasyFarmingPlugin plugin)
    {
        super(
            config,
            client,
            plugin
        );
    }

    public Map<Integer, Integer> getHopsItems()
    {
        return getAllItemRequirements(locations);
    }

    public Map<Integer, Integer> getAllItemRequirements(List<Location> locations)
    {
        Map<Integer, Integer> allRequirements = new HashMap<>();

        setupLocations();

        // Add other items and merge them with allRequirements
        for (Location location : locations) {
            if (plugin.getHopsLocationEnabled(location.getName())) {
                // Use BARLEY_SEED as default for hops seeds, code later will allow for any seed to be used
                allRequirements.merge(
                    ItemID.BARLEY_SEED,
                    4, // Hops patches require 4 seeds (except jute which requires 3)
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

                    if (itemId == ItemID.SKILLCAPE_CONSTRUCTION || itemId == ItemID.SKILLCAPE_CONSTRUCTION_TRIMMED || itemId == ItemID.SKILLCAPE_MAX) {
                        allRequirements.merge(
                            itemId,
                            quantity,
                            (oldValue, newValue) -> Math.min(
                                1,
                                oldValue + newValue
                            )
                        );
                    } else if (itemId == ItemID.HG_QUETZALWHISTLE_BASIC || itemId == ItemID.HG_QUETZALWHISTLE_ENHANCED || itemId == ItemID.HG_QUETZALWHISTLE_PERFECTED) {
                        allRequirements.merge(
                            ItemID.HG_QUETZALWHISTLE_BASIC,
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

        // Hops patches need watering can
        allRequirements.merge(
            Constants.WATERING_CAN_IDS.get(0),
            1,
            Integer::sum
        );

        return allRequirements;
    }

    public void setupLocations()
    {
        super.setupLocations();

        setupLumbridgeLocation();
        setupSeersVillageLocation();
        setupYanilleLocation();
        setupEntranaLocation();
        setupAldarinLocation();
    }

    private Supplier<List<ItemRequirement>> createHouseTeleportSupplier() {
        return () -> getHouseTeleportItemRequirements();
    }

    private void setupLumbridgeLocation()
    {
        com.easyfarming.locations.LocationData lumbridgeData = 
            com.easyfarming.locations.hops.LumbridgeHopsLocationData.create(createHouseTeleportSupplier());
        
        lumbridgeHopsLocation = com.easyfarming.locations.LocationFactory.createLocation(lumbridgeData, config);
        locations.add(lumbridgeHopsLocation);
    }

    private void setupSeersVillageLocation()
    {
        com.easyfarming.locations.LocationData seersVillageData = 
            com.easyfarming.locations.hops.SeersVillageHopsLocationData.create(createHouseTeleportSupplier());
        
        seersVillageHopsLocation = com.easyfarming.locations.LocationFactory.createLocation(seersVillageData, config);
        locations.add(seersVillageHopsLocation);
    }

    private void setupYanilleLocation()
    {
        com.easyfarming.locations.LocationData yanilleData = 
            com.easyfarming.locations.hops.YanilleHopsLocationData.create(createHouseTeleportSupplier());
        
        yanilleHopsLocation = com.easyfarming.locations.LocationFactory.createLocation(yanilleData, config);
        locations.add(yanilleHopsLocation);
    }

    private void setupEntranaLocation()
    {
        com.easyfarming.locations.LocationData entranaData = 
            com.easyfarming.locations.hops.EntranaHopsLocationData.create();
        
        entranaHopsLocation = com.easyfarming.locations.LocationFactory.createLocation(entranaData, config);
        locations.add(entranaHopsLocation);
    }

    private void setupAldarinLocation()
    {
        com.easyfarming.locations.LocationData aldarinData = 
            com.easyfarming.locations.hops.AldarinHopsLocationData.create(createHouseTeleportSupplier());
        
        aldarinHopsLocation = com.easyfarming.locations.LocationFactory.createLocation(aldarinData, config);
        locations.add(aldarinHopsLocation);
    }
}

