package com.easyfarming.utils;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemRequirement;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.gameval.ItemID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Static utility for house teleport and fairy ring item requirements,
 * extracted from ItemAndLocation for use by LocationCatalog and other callers.
 */
public final class TeleportItemRequirements {

    private TeleportItemRequirements() {}

    public static List<ItemRequirement> getHouseTeleportItemRequirements(EasyFarmingConfig config) {
        EasyFarmingConfig.OptionEnumHouseTele selectedOption = config.enumConfigHouseTele();
        List<ItemRequirement> itemRequirements = new ArrayList<>();

        switch (selectedOption) {
            case Law_air_earth_runes:
                itemRequirements.add(new ItemRequirement(ItemID.AIRRUNE, 1));
                itemRequirements.add(new ItemRequirement(ItemID.EARTHRUNE, 1));
                itemRequirements.add(new ItemRequirement(ItemID.LAWRUNE, 1));
                break;

            case Teleport_To_House:
                itemRequirements.add(new ItemRequirement(ItemID.POH_TABLET_TELEPORTTOHOUSE, 1));
                break;

            case Construction_cape:
                itemRequirements.add(new ItemRequirement(ItemID.SKILLCAPE_CONSTRUCTION, 1));
                break;

            case Construction_cape_t:
                itemRequirements.add(new ItemRequirement(ItemID.SKILLCAPE_CONSTRUCTION_TRIMMED, 1));
                break;

            case Max_cape:
                itemRequirements.add(new ItemRequirement(ItemID.SKILLCAPE_MAX, 1));
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
    public static List<ItemRequirement> getFairyRingItemRequirements(EasyFarmingConfig config, Client client) {
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
        return Collections.singletonList(new ItemRequirement(ItemID.DRAMEN_STAFF, 1));
    }
}
