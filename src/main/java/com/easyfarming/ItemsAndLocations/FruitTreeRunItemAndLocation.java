package com.easyfarming.ItemsAndLocations;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.ItemRequirement;
import com.easyfarming.core.Location;
import com.easyfarming.core.Teleport;
import net.runelite.api.Client;
import net.runelite.api.gameval.ItemID;
import com.easyfarming.locations.fruittree.BrimhavenFruitTreeLocationData;
import com.easyfarming.locations.fruittree.CatherbyFruitTreeLocationData;
import com.easyfarming.locations.fruittree.FarmingGuildFruitTreeLocationData;
import com.easyfarming.locations.fruittree.GnomeStrongholdFruitTreeLocationData;
import com.easyfarming.locations.fruittree.LletyaFruitTreeLocationData;
import com.easyfarming.locations.fruittree.TreeGnomeVillageFruitTreeLocationData;

import java.util.*;

public class FruitTreeRunItemAndLocation extends ItemAndLocation
{
    public Location brimhavenFruitTreeLocation;
    public Location catherbyFruitTreeLocation;
    public Location farmingGuildFruitTreeLocation;
    public Location gnomeStrongholdFruitTreeLocation;
    public Location lletyaFruitTreeLocation;
    public Location treeGnomeVillageFruitTreeLocation;

    private boolean locationsInitialized = false;

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
        setupLocations();
    }

    public Map<Integer, Integer> getFruitTreeItems()
    {
        return getAllItemRequirements(locations);
    }

    public Map<Integer, Integer> getAllItemRequirements(List<Location> locations)
    {
        Map<Integer, Integer> allRequirements = new HashMap<>();

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

                Teleport teleport = location.getSelectedTeleport();

                Map<Integer, Integer> locationRequirements = teleport.getItemRequirements();

                for (Map.Entry<Integer, Integer> entry : locationRequirements.entrySet()) {
                    int itemId = entry.getKey();
                    int quantity = entry.getValue();

                    if (itemId == ItemID.SKILLCAPE_CONSTRUCTION || itemId == ItemID.SKILLCAPE_CONSTRUCTION_TRIMMED || itemId == ItemID.SKILLCAPE_MAX || itemId == ItemID.MM2_ROYAL_SEED_POD) {
                        allRequirements.merge(
                            itemId,
                            quantity,
                            (oldValue, newValue) -> Math.max(oldValue, newValue)
                        );
                    } else if (itemId == ItemID.DRAMEN_STAFF) {
                        allRequirements.merge(
                            ItemID.DRAMEN_STAFF,
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

        if (config.generalSecateurs()) {
            allRequirements.merge(
                ItemID.FAIRY_ENCHANTED_SECATEURS,
                1,
                Integer::sum
            );
        }

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
        if (locationsInitialized) {
            return;
        }

        locations.clear();
        super.setupLocations();

        setupFarmingGuildLocation();
        setupBrimhavenLocations();
        setupCatherbyLocations();
        setupLletyaLocation();
        setupGnomeStrongholdLocation();
        setupTreeGnomeVillage();

        locationsInitialized = true;
    }

    private void setupBrimhavenLocations()
    {
        brimhavenFruitTreeLocation = BrimhavenFruitTreeLocationData.create(
            config,
            () -> getHouseTeleportItemRequirements()
        );
        locations.add(brimhavenFruitTreeLocation);
    }

    private void setupCatherbyLocations()
    {
        catherbyFruitTreeLocation = CatherbyFruitTreeLocationData.create(
            config,
            () -> getHouseTeleportItemRequirements()
        );
        locations.add(catherbyFruitTreeLocation);
    }

    private void setupFarmingGuildLocation()
    {
        farmingGuildFruitTreeLocation = FarmingGuildFruitTreeLocationData.create(
            config,
            () -> getHouseTeleportItemRequirements(),
            () -> getFairyRingItemRequirements()
        );
        locations.add(farmingGuildFruitTreeLocation);
    }

    private void setupGnomeStrongholdLocation()
    {
        gnomeStrongholdFruitTreeLocation = GnomeStrongholdFruitTreeLocationData.create(config);
        locations.add(gnomeStrongholdFruitTreeLocation);
    }

    private void setupLletyaLocation()
    {
        lletyaFruitTreeLocation = LletyaFruitTreeLocationData.create(config);
        locations.add(lletyaFruitTreeLocation);
    }

    private void setupTreeGnomeVillage()
    {
        treeGnomeVillageFruitTreeLocation = TreeGnomeVillageFruitTreeLocationData.create(config);
        locations.add(treeGnomeVillageFruitTreeLocation);
    }
}