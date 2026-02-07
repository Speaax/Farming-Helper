package com.easyfarming.ItemsAndLocations;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.ItemRequirement;
import com.easyfarming.core.Location;
import com.easyfarming.utils.Constants;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.gameval.ItemID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for farming run type implementations (Herb, Tree, FruitTree, Hops).
 * 
 * Architecture:
 * - This class and its subclasses (HerbRunItemAndLocation, TreeRunItemAndLocation, etc.) 
 *   represent the "business logic" layer for calculating item requirements per run type.
 * - They use the data-driven locations.* package to create Location instances,
 *   which provides a clean separation between data and logic.
 * - Subclasses create Location instances directly using LocationData classes (e.g., ArdougneLocationData.create()),
 *   which return Location instances using core.Teleport directly, then calculate item 
 *   requirements based on enabled locations.
 * 
 * Relationship to locations.* package:
 * - locations.* contains LocationData classes that define location data (teleports, coordinates, etc.)
 * - These classes now return Location instances directly using core.Teleport
 * - This allows locations to be defined as data rather than hardcoded in business logic
 * - The canonical teleport model is core.Teleport, used throughout the codebase
 */
public class ItemAndLocation
{
    protected EasyFarmingConfig config;

    protected Client client;

    protected EasyFarmingPlugin plugin;

    public List<Location> locations = new ArrayList<>();

    public ItemAndLocation()
    {
    }

    public ItemAndLocation(EasyFarmingConfig config, Client client, EasyFarmingPlugin plugin)
    {
        this.config = config;
        this.client = client;
        this.plugin = plugin;
    }

    public List<ItemRequirement> getHouseTeleportItemRequirements()
    {
        EasyFarmingConfig.OptionEnumHouseTele selectedOption = config.enumConfigHouseTele();

        List<ItemRequirement> itemRequirements = new ArrayList<>();

        switch (selectedOption) {
            case Law_air_earth_runes:
                itemRequirements.add(new ItemRequirement(
                    ItemID.AIRRUNE,
                    1
                ));

                itemRequirements.add(new ItemRequirement(
                    ItemID.EARTHRUNE,
                    1
                ));

                itemRequirements.add(new ItemRequirement(
                    ItemID.LAWRUNE,
                    1
                ));

                break;

            case Teleport_To_House:
                itemRequirements.add(new ItemRequirement(
                    ItemID.POH_TABLET_TELEPORTTOHOUSE,
                    1
                ));

                break;

            case Construction_cape:
                itemRequirements.add(new ItemRequirement(
                    ItemID.SKILLCAPE_CONSTRUCTION,
                    1
                ));

                break;

            case Construction_cape_t:
                itemRequirements.add(new ItemRequirement(
                    ItemID.SKILLCAPE_CONSTRUCTION_TRIMMED,
                    1
                ));

                break;
            case Max_cape:
                itemRequirements.add(new ItemRequirement(
                    ItemID.SKILLCAPE_MAX,
                    1
                ));

                break;

            default:
                throw new IllegalStateException("Unexpected value: " + selectedOption);
        }

        return itemRequirements;
    }

    /**
     * Returns the item requirements for using a fairy ring teleport.
     * Checks the Lumbridge & Draynor Elite diary completion varbit to determine
     * whether a Dramen staff is needed. If the diary is complete, no staff is required.
     *
     * @return List of item requirements (Dramen staff if diary incomplete, empty if complete)
     */
    public List<ItemRequirement> getFairyRingItemRequirements()
    {
        try {
            if (client != null && client.getGameState() == GameState.LOGGED_IN) {
                int diaryValue = client.getVarbitValue(Constants.VARBIT_LUMBRIDGE_DIARY_ELITE);
                if (diaryValue >= 1) {
                    return Collections.emptyList();
                }
            }
        } catch (Throwable t) {
            // Varbit may not be available before login or API may differ — default to requiring Dramen staff
        }
        return Collections.singletonList(
            new ItemRequirement(ItemID.DRAMEN_STAFF, 1)
        );
    }

    public Integer selectedCompostID()
    {
        EasyFarmingConfig.OptionEnumCompost selectedCompost = config.enumConfigCompost();

        switch (selectedCompost) {
            case Compost:
                return ItemID.BUCKET_COMPOST;

            case Supercompost:
                return ItemID.BUCKET_SUPERCOMPOST;

            case Ultracompost:
                return ItemID.BUCKET_ULTRACOMPOST;

            case Bottomless:
                return ItemID.BOTTOMLESS_COMPOST_BUCKET;
            default:
                return 0;
        }
    }    public void setupLocations()
    {
        locations.clear();
    }
}
