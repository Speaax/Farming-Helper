package com.easyfarming.ItemsAndLocations;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.ItemRequirement;
import com.easyfarming.Location;
import net.runelite.api.Client;
import net.runelite.api.gameval.ItemID;

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
    public Location civitasLocation;

    public HerbRunItemAndLocation()
    {
    }

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
                    } else if (itemId == ItemID.HG_QUETZALWHISTLE_BASIC || itemId == ItemID.HG_QUETZALWHISTLE_ENHANCED || itemId == ItemID.HG_QUETZALWHISTLE_PERFECTED) {
                        // Handle Quetzal whistle variants - only show the basic one in requirements
                        allRequirements.merge(
                            ItemID.HG_QUETZALWHISTLE_BASIC,  // Always show the basic variant
                            quantity,
                            (oldValue, newValue) -> Math.min(
                                1,
                                oldValue + newValue
                            )
                        );
                    } else if (itemId == ItemID.SKILLCAPE_HUNTING || itemId == ItemID.SKILLCAPE_HUNTING_TRIMMED) {
                        // Handle Hunter skillcape variants - only show the regular one in requirements
                        allRequirements.merge(
                            ItemID.SKILLCAPE_HUNTING,  // Always show the regular variant
                            quantity,
                            (oldValue, newValue) -> Math.min(
                                1,
                                oldValue + newValue
                            )
                        );
                    } else if (itemId == ItemID.LUMBRIDGE_RING_MEDIUM || itemId == ItemID.LUMBRIDGE_RING_HARD || itemId == ItemID.LUMBRIDGE_RING_ELITE) {
                        // Handle Explorer's Ring variants - normalize to base ID
                        allRequirements.merge(
                            ItemID.LUMBRIDGE_RING_MEDIUM,  // Always show the base variant
                            quantity,
                            (oldValue, newValue) -> Math.min(
                                1,
                                oldValue + newValue
                            )
                        );
                    } else if (itemId == ItemID.ARDY_CAPE_MEDIUM || itemId == ItemID.ARDY_CAPE_HARD || itemId == ItemID.ARDY_CAPE_ELITE) {
                        // Handle Ardougne Cloak variants - normalize to base ID
                        allRequirements.merge(
                            ItemID.ARDY_CAPE_MEDIUM,  // Always show the base variant
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
        setupCivitasLocation();
    }

    private void setupCivitasLocation()
    {
        // NEW APPROACH: Using LocationData pattern for data-driven setup
        com.easyfarming.locations.LocationData civitasData = com.easyfarming.locations.CivitasLocationData.create(
            () -> {
                List<ItemRequirement> requirements = getHouseTeleportItemRequirements();
                return requirements.stream()
                    .map(ir -> new com.easyfarming.core.ItemRequirement(ir.getItemId(), ir.getQuantity()))
                    .collect(java.util.stream.Collectors.toList());
            }
        );
        
        civitasLocation = com.easyfarming.locations.LocationFactory.createLocation(civitasData, config);
        locations.add(civitasLocation);
    }

    private void setupArdougneLocation()
    {
        // NEW APPROACH: Using LocationData pattern for data-driven setup
        com.easyfarming.locations.LocationData ardougneData = com.easyfarming.locations.ArdougneLocationData.create(
            () -> {
                List<ItemRequirement> requirements = getHouseTeleportItemRequirements();
                return requirements.stream()
                    .map(ir -> new com.easyfarming.core.ItemRequirement(ir.getItemId(), ir.getQuantity()))
                    .collect(java.util.stream.Collectors.toList());
            }
        );
        
        ardougneLocation = com.easyfarming.locations.LocationFactory.createLocation(ardougneData, config);
        locations.add(ardougneLocation);
    }

    private void setupCatherbyLocation()
    {
        // NEW APPROACH: Using LocationData pattern for data-driven setup
        com.easyfarming.locations.LocationData catherbyData = com.easyfarming.locations.CatherbyLocationData.create(
            () -> {
                List<ItemRequirement> requirements = getHouseTeleportItemRequirements();
                return requirements.stream()
                    .map(ir -> new com.easyfarming.core.ItemRequirement(ir.getItemId(), ir.getQuantity()))
                    .collect(java.util.stream.Collectors.toList());
            }
        );
        
        catherbyLocation = com.easyfarming.locations.LocationFactory.createLocation(catherbyData, config);
        locations.add(catherbyLocation);
    }

    private void setupFaladorLocation()
    {
        // NEW APPROACH: Using LocationData pattern for data-driven setup
        com.easyfarming.locations.LocationData faladorData = com.easyfarming.locations.FaladorLocationData.create(
            () -> {
                List<ItemRequirement> requirements = getHouseTeleportItemRequirements();
                return requirements.stream()
                    .map(ir -> new com.easyfarming.core.ItemRequirement(ir.getItemId(), ir.getQuantity()))
                    .collect(java.util.stream.Collectors.toList());
            }
        );
        
        faladorLocation = com.easyfarming.locations.LocationFactory.createLocation(faladorData, config);
        locations.add(faladorLocation);
    }

    private void setupFarmingGuildLocation()
    {
        // NEW APPROACH: Using LocationData pattern for data-driven setup
        com.easyfarming.locations.LocationData farmingGuildData = com.easyfarming.locations.FarmingGuildLocationData.create(
            () -> {
                List<ItemRequirement> requirements = getHouseTeleportItemRequirements();
                return requirements.stream()
                    .map(ir -> new com.easyfarming.core.ItemRequirement(ir.getItemId(), ir.getQuantity()))
                    .collect(java.util.stream.Collectors.toList());
            }
        );
        
        farmingGuildLocation = com.easyfarming.locations.LocationFactory.createLocation(farmingGuildData, config);
        locations.add(farmingGuildLocation);
    }

    private void setupHarmonyLocation()
    {
        // NEW APPROACH: Using LocationData pattern for data-driven setup
        com.easyfarming.locations.LocationData harmonyData = com.easyfarming.locations.HarmonyLocationData.create(
            () -> {
                List<ItemRequirement> requirements = getHouseTeleportItemRequirements();
                return requirements.stream()
                    .map(ir -> new com.easyfarming.core.ItemRequirement(ir.getItemId(), ir.getQuantity()))
                    .collect(java.util.stream.Collectors.toList());
            }
        );
        
        harmonyLocation = com.easyfarming.locations.LocationFactory.createLocation(harmonyData, config);
        locations.add(harmonyLocation);
    }

    private void setupKourendLocation()
    {
        // NEW APPROACH: Using LocationData pattern for data-driven setup
        com.easyfarming.locations.LocationData kourendData = com.easyfarming.locations.KourendLocationData.create(
            () -> {
                List<ItemRequirement> requirements = getHouseTeleportItemRequirements();
                return requirements.stream()
                    .map(ir -> new com.easyfarming.core.ItemRequirement(ir.getItemId(), ir.getQuantity()))
                    .collect(java.util.stream.Collectors.toList());
            }
        );
        
        kourendLocation = com.easyfarming.locations.LocationFactory.createLocation(kourendData, config);
        locations.add(kourendLocation);
    }

    private void setupMorytaniaLocation()
    {
        // NEW APPROACH: Using LocationData pattern for data-driven setup
        com.easyfarming.locations.LocationData morytaniaData = com.easyfarming.locations.MorytaniaLocationData.create();
        
        morytaniaLocation = com.easyfarming.locations.LocationFactory.createLocation(morytaniaData, config);
        locations.add(morytaniaLocation);
    }

    private void setupTrollStrongholdLocation()
    {
        // NEW APPROACH: Using LocationData pattern for data-driven setup
        com.easyfarming.locations.LocationData trollStrongholdData = com.easyfarming.locations.TrollStrongholdLocationData.create(
            () -> {
                List<ItemRequirement> requirements = getHouseTeleportItemRequirements();
                return requirements.stream()
                    .map(ir -> new com.easyfarming.core.ItemRequirement(ir.getItemId(), ir.getQuantity()))
                    .collect(java.util.stream.Collectors.toList());
            }
        );
        
        trollStrongholdLocation = com.easyfarming.locations.LocationFactory.createLocation(trollStrongholdData, config);
        locations.add(trollStrongholdLocation);
    }

    private void setupWeissLocation()
    {
        // NEW APPROACH: Using LocationData pattern for data-driven setup
        com.easyfarming.locations.LocationData weissData = com.easyfarming.locations.WeissLocationData.create(
            () -> {
                List<ItemRequirement> requirements = getHouseTeleportItemRequirements();
                return requirements.stream()
                    .map(ir -> new com.easyfarming.core.ItemRequirement(ir.getItemId(), ir.getQuantity()))
                    .collect(java.util.stream.Collectors.toList());
            }
        );
        
        weissLocation = com.easyfarming.locations.LocationFactory.createLocation(weissData, config);
        locations.add(weissLocation);
    }


}