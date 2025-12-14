package com.easyfarming.ItemsAndLocations;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.Location;
import com.easyfarming.core.Teleport;
import net.runelite.api.Client;
import net.runelite.api.gameval.ItemID;

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

    public TreeRunItemAndLocation(EasyFarmingConfig config, Client client, EasyFarmingPlugin plugin)
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
                    ItemID.PLANTPOT_OAK_SAPLING,
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

                    if (itemId == ItemID.SKILLCAPE_CONSTRUCTION || itemId == ItemID.SKILLCAPE_CONSTRUCTION_TRIMMED || itemId == ItemID.SKILLCAPE_MAX) {
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

        setupFarmingGuildLocation();
        setupFaladorLocation();
        setupTaverleyLocation();
        setupLumbridgeLocation();
        setupVarrockLocation();
        setupGnomeStrongholdLocation();
    }

    private void setupFaladorLocation()
    {
        faladorTreeLocation = com.easyfarming.locations.tree.FaladorTreeLocationData.create(
            config,
            () -> getHouseTeleportItemRequirements()
        );
        locations.add(faladorTreeLocation);
    }

    private void setupFarmingGuildLocation()
    {
        farmingGuildTreeLocation = com.easyfarming.locations.tree.FarmingGuildTreeLocationData.create(
            config,
            () -> getHouseTeleportItemRequirements()
        );
        locations.add(farmingGuildTreeLocation);
    }

    private void setupGnomeStrongholdLocation()
    {
        gnomeStrongholdTreeLocation = com.easyfarming.locations.tree.GnomeStrongholdTreeLocationData.create(config);
        locations.add(gnomeStrongholdTreeLocation);
    }

    private void setupLumbridgeLocation()
    {
        lumbridgeTreeLocation = com.easyfarming.locations.tree.LumbridgeTreeLocationData.create(
            config,
            () -> getHouseTeleportItemRequirements()
        );
        locations.add(lumbridgeTreeLocation);
    }

    private void setupTaverleyLocation()
    {
        taverleyTreeLocation = com.easyfarming.locations.tree.TaverleyTreeLocationData.create(
            config,
            () -> getHouseTeleportItemRequirements()
        );
        locations.add(taverleyTreeLocation);
    }

    private void setupVarrockLocation()
    {
        varrockTreeLocation = com.easyfarming.locations.tree.VarrockTreeLocationData.create(
            config,
            () -> getHouseTeleportItemRequirements()
        );
        locations.add(varrockTreeLocation);
    }
}