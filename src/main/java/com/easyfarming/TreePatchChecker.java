package com.easyfarming;

import net.runelite.api.Client;

/**
 * Tree patch state detection using range-based varbit value checks.
 *
 * This implementation uses the exact varbit ranges from RuneLite's PatchImplementation
 * (TREE section, lines 1835-2117) to determine crop state without hardcoding all varbit values.
 *
 * Varbit IDs: 
 * - Standard locations: 4771 (FARMING_TRANSMIT_A)
 * - Farming Guild: 7905
 */
public class TreePatchChecker {

    /**
     * Checks the state of a tree patch based on varbit value ranges.
     *
     * This method uses the exact varbit ranges from RuneLite's PatchImplementation
     * to determine crop state without hardcoding all varbit values.
     *
     * @param client The RuneLite client instance
     * @param varbitIndex The varbit ID for the patch
     * @return The current state of the patch
     */
    public static PlantState checkTreePatch(Client client, int varbitIndex) {
        int value = client.getVarbitValue(varbitIndex);

        // Check for empty patch ready to plant (value 3 = fully raked)
        if (value == 3) {
            return PlantState.PLANT;
        }

        // Check harvestable first (before other states, as harvest values might overlap)
        // Oak[Chop down,Inspect,Guide] 13
        if (value == 13) {
            return PlantState.REMOVE; // REMOVE = ready to chop down
        }
        // Oak tree stump[Clear,Inspect,Guide] 14
        if (value == 14) {
            return PlantState.REMOVE; // Stump = ready to clear
        }
        // Willow Tree[Chop down,Inspect,Guide] 22
        if (value == 22) {
            return PlantState.REMOVE;
        }
        // Willow tree stump[Clear,Inspect,Guide] 23
        if (value == 23) {
            return PlantState.REMOVE;
        }
        // Maple Tree[Chop down,Inspect,Guide] 33
        if (value == 33) {
            return PlantState.REMOVE;
        }
        // Tree stump[Clear,Inspect,Guide] 34
        if (value == 34) {
            return PlantState.REMOVE;
        }
        // Yew tree[Chop down,Inspect,Guide] 46
        if (value == 46) {
            return PlantState.REMOVE;
        }
        // Yew tree stump[Clear,Inspect,Guide] 47
        if (value == 47) {
            return PlantState.REMOVE;
        }
        // Magic Tree[Chop down,Inspect,Guide] 61
        if (value == 61) {
            return PlantState.REMOVE;
        }
        // Magic Tree Stump[Clear,Inspect,Guide] 62
        if (value == 62) {
            return PlantState.REMOVE;
        }
        // Willow Tree[Chop down,Inspect,Guide] 192-197
        if (value >= 192 && value <= 197) {
            return PlantState.REMOVE;
        }

        // Check dead before diseased, as dead is a more specific state
        // Dead Oak[Clear,Inspect,Guide] 137-139
        if (value >= 137 && value <= 139) {
            return PlantState.DEAD;
        }
        // Dead Oak[Clear,Inspect,Guide] 141
        if (value == 141) {
            return PlantState.DEAD;
        }
        // Dead Willow[Clear,Inspect,Guide] 144-148
        if (value >= 144 && value <= 148) {
            return PlantState.DEAD;
        }
        // Dead Willow[Clear,Inspect,Guide] 150
        if (value == 150) {
            return PlantState.DEAD;
        }
        // Dead Maple[Clear,Inspect,Guide] 153-159
        if (value >= 153 && value <= 159) {
            return PlantState.DEAD;
        }
        // Dead Maple[Clear,Inspect,Guide] 161
        if (value == 161) {
            return PlantState.DEAD;
        }
        // Dead Yew[Clear,Inspect,Guide] 164-172
        if (value >= 164 && value <= 172) {
            return PlantState.DEAD;
        }
        // Dead Yew[Clear,Inspect,Guide] 174
        if (value == 174) {
            return PlantState.DEAD;
        }
        // Dead Magic Tree[Clear,Inspect,Guide] 177-187
        if (value >= 177 && value <= 187) {
            return PlantState.DEAD;
        }
        // Dead Magic Tree[Clear,Inspect,Guide] 189
        if (value == 189) {
            return PlantState.DEAD;
        }

        // Check diseased state
        // Diseased Oak[Prune,Inspect,Guide] 73-75
        if (value >= 73 && value <= 75) {
            return PlantState.DISEASED;
        }
        // Diseased Oak[Prune,Inspect,Guide] 77
        if (value == 77) {
            return PlantState.DISEASED;
        }
        // Diseased Willow[Prune,Inspect,Guide] 80-84
        if (value >= 80 && value <= 84) {
            return PlantState.DISEASED;
        }
        // Diseased Willow[Prune,Inspect,Guide] 86
        if (value == 86) {
            return PlantState.DISEASED;
        }
        // Diseased Maple[Prune,Inspect,Guide] 89-95
        if (value >= 89 && value <= 95) {
            return PlantState.DISEASED;
        }
        // Diseased Maple[Prune,Inspect,Guide] 97
        if (value == 97) {
            return PlantState.DISEASED;
        }
        // Diseased Yew[Prune,Inspect,Guide] 100-108
        if (value >= 100 && value <= 108) {
            return PlantState.DISEASED;
        }
        // Diseased Yew[Prune,Inspect,Guide] 110
        if (value == 110) {
            return PlantState.DISEASED;
        }
        // Diseased Magic Tree[Prune,Inspect,Guide] 113-123
        if (value >= 113 && value <= 123) {
            return PlantState.DISEASED;
        }
        // Diseased Magic Tree[Prune,Inspect,Guide] 125
        if (value == 125) {
            return PlantState.DISEASED;
        }

        // Check healthy state (fully grown, check-health available)
        // Oak[Check-health,Inspect,Guide] 12
        if (value == 12) {
            return PlantState.HEALTHY;
        }
        // Willow Tree[Check-health,Inspect,Guide] 21
        if (value == 21) {
            return PlantState.HEALTHY;
        }
        // Maple Tree[Check-health,Inspect,Guide] 32
        if (value == 32) {
            return PlantState.HEALTHY;
        }
        // Yew tree[Check-health,Inspect,Guide] 45
        if (value == 45) {
            return PlantState.HEALTHY;
        }
        // Magic Tree[Check-health,Inspect,Guide] 60
        if (value == 60) {
            return PlantState.HEALTHY;
        }

        // Check weeds (all weed states)
        // Tree patch[Rake,Inspect,Guide] 0-3
        if (value >= 0 && value <= 3) {
            return PlantState.WEEDS;
        }
        // Tree patch[Rake,Inspect,Guide] 4-7
        if (value >= 4 && value <= 7) {
            return PlantState.WEEDS;
        }
        // Tree patch[Rake,Inspect,Guide] 63-72
        if (value >= 63 && value <= 72) {
            return PlantState.WEEDS;
        }
        // Tree patch[Rake,Inspect,Guide] 78-79
        if (value >= 78 && value <= 79) {
            return PlantState.WEEDS;
        }
        // Tree patch[Rake,Inspect,Guide] 87-88
        if (value >= 87 && value <= 88) {
            return PlantState.WEEDS;
        }
        // Tree patch[Rake,Inspect,Guide] 98-99
        if (value >= 98 && value <= 99) {
            return PlantState.WEEDS;
        }
        // Tree patch[Rake,Inspect,Guide] 111-112
        if (value >= 111 && value <= 112) {
            return PlantState.WEEDS;
        }
        // Tree patch[Rake,Inspect,Guide] 126-136
        if (value >= 126 && value <= 136) {
            return PlantState.WEEDS;
        }
        // Tree patch[Rake,Inspect,Guide] 142-143
        if (value >= 142 && value <= 143) {
            return PlantState.WEEDS;
        }
        // Tree patch[Rake,Inspect,Guide] 151-152
        if (value >= 151 && value <= 152) {
            return PlantState.WEEDS;
        }
        // Tree patch[Rake,Inspect,Guide] 162-163
        if (value >= 162 && value <= 163) {
            return PlantState.WEEDS;
        }
        // Tree patch[Rake,Inspect,Guide] 175-176
        if (value >= 175 && value <= 176) {
            return PlantState.WEEDS;
        }
        // Tree patch[Rake,Inspect,Guide] 190-191
        if (value >= 190 && value <= 191) {
            return PlantState.WEEDS;
        }
        // Tree patch[Rake,Inspect,Guide] 198-255
        if (value >= 198 && value <= 255) {
            return PlantState.WEEDS;
        }

        // Growing ranges (checked after other states)
        // Oak[Inspect,Guide] 8-11
        if (value >= 8 && value <= 11) {
            return PlantState.GROWING;
        }
        // Willow Tree[Inspect,Guide] 15-20
        if (value >= 15 && value <= 20) {
            return PlantState.GROWING;
        }
        // Maple Tree[Inspect,Guide] 24-31
        if (value >= 24 && value <= 31) {
            return PlantState.GROWING;
        }
        // Yew sapling,Yew tree[Inspect,Guide] 35-44
        if (value >= 35 && value <= 44) {
            return PlantState.GROWING;
        }
        // Magic Tree[Inspect,Guide] 48-59
        if (value >= 48 && value <= 59) {
            return PlantState.GROWING;
        }

        // Unknown state
        return PlantState.UNKNOWN;
    }

    public enum PlantState {
        GROWING,
        DISEASED,
        DEAD,
        WEEDS,
        HEALTHY,
        REMOVE,
        PLANT,
        UNKNOWN
    }
}
