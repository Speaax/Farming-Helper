package com.easyfarming.ItemsAndLocations;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.ItemRequirement;
import com.easyfarming.Location;
import com.easyfarming.core.Teleport;
import com.easyfarming.utils.Constants;
import net.runelite.api.Client;
import net.runelite.api.gameval.ItemID;

import java.util.*;
import java.util.function.Supplier;

/**
 * Manages herb run locations and calculates item requirements for herb farming runs.
 * 
 * Uses the data-driven locations.* package to create Location instances:
 * - Creates Location instances directly using classes like ArdougneLocationData, CatherbyLocationData, etc.
 * - These classes now return Location instances directly using core.Teleport
 * - Calculates item requirements based on enabled locations and selected teleports
 */
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

                Teleport teleport = location.getSelectedTeleport();

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

                // Add allotment seed requirements if enabled
                // Only add requirements if this location actually has allotment patches
                if (config.generalAllotment()) {
                    List<Integer> allotmentPatchIds = Constants.ALLOTMENT_PATCH_IDS_BY_LOCATION.get(location.getName());
                    if (allotmentPatchIds != null && !allotmentPatchIds.isEmpty()) {
                        int allotmentPatches = allotmentPatchIds.size(); // Number of patches at this location
                        
                        // Each allotment patch requires 3 seeds
                        int seedsPerPatch = 3;
                        int totalAllotmentSeeds = allotmentPatches * seedsPerPatch;
                        
                        // Use SNAPE_GRASS_SEED as base ID (similar to GUAM_SEED for herbs)
                        allRequirements.merge(
                            Constants.BASE_ALLOTMENT_SEED_ID,
                            totalAllotmentSeeds,
                            Integer::sum
                        );

                        // Allotment patches also need compost (same as herb patches)
                        if (selectedCompostID() != - 1 && selectedCompostID() != ItemID.BOTTOMLESS_COMPOST_BUCKET) {
                            allRequirements.merge(
                                selectedCompostID(),
                                allotmentPatches,
                                Integer::sum
                            );
                        }
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

        setupFarmingGuildLocation();
        setupArdougneLocation();
        setupCatherbyLocation();
        setupFaladorLocation();
        setupHarmonyLocation();
        setupKourendLocation();
        setupMorytaniaLocation();
        setupTrollStrongholdLocation();
        setupWeissLocation();
        setupCivitasLocation();
    }

    private Supplier<List<ItemRequirement>> createHouseTeleportSupplier() {
        return () -> getHouseTeleportItemRequirements();
    }

    private void setupCivitasLocation()
    {
        civitasLocation = com.easyfarming.locations.CivitasLocationData.create(config, createHouseTeleportSupplier());
        locations.add(civitasLocation);
    }

    private void setupArdougneLocation()
    {
        ardougneLocation = com.easyfarming.locations.ArdougneLocationData.create(config, createHouseTeleportSupplier());
        locations.add(ardougneLocation);
    }

    private void setupCatherbyLocation()
    {
        catherbyLocation = com.easyfarming.locations.CatherbyLocationData.create(config, createHouseTeleportSupplier());
        locations.add(catherbyLocation);
    }

    private void setupFaladorLocation()
    {
        faladorLocation = com.easyfarming.locations.FaladorLocationData.create(config, createHouseTeleportSupplier());
        locations.add(faladorLocation);
    }

    private void setupFarmingGuildLocation()
    {
        farmingGuildLocation = com.easyfarming.locations.FarmingGuildLocationData.create(config, createHouseTeleportSupplier());
        locations.add(farmingGuildLocation);
    }

    private void setupHarmonyLocation()
    {
        harmonyLocation = com.easyfarming.locations.HarmonyLocationData.create(config, createHouseTeleportSupplier());
        locations.add(harmonyLocation);
    }

    private void setupKourendLocation()
    {
        kourendLocation = com.easyfarming.locations.KourendLocationData.create(config, createHouseTeleportSupplier());
        locations.add(kourendLocation);
    }

    private void setupMorytaniaLocation()
    {
        morytaniaLocation = com.easyfarming.locations.MorytaniaLocationData.create(config);
        locations.add(morytaniaLocation);
    }

    private void setupTrollStrongholdLocation()
    {
        trollStrongholdLocation = com.easyfarming.locations.TrollStrongholdLocationData.create(config, createHouseTeleportSupplier());
        locations.add(trollStrongholdLocation);
    }

    private void setupWeissLocation()
    {
        weissLocation = com.easyfarming.locations.WeissLocationData.create(config, createHouseTeleportSupplier());
        locations.add(weissLocation);
    }


}