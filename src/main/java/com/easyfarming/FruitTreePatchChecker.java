package com.easyfarming;

import net.runelite.api.Client;

/**
 * Fruit tree patch state detection using range-based varbit value checks.
 *
 * This implementation uses the exact varbit ranges from RuneLite's PatchImplementation
 * (FRUIT_TREE section) to determine crop state without hardcoding all varbit values.
 *
 * Varbit IDs:
 * - Standard locations: 4771 (FARMING_TRANSMIT_A)
 * - Farming Guild: 7909
 * - Gnome Stronghold: 4772 (FARMING_TRANSMIT_B)
 */
public class FruitTreePatchChecker {

    /**
     * Checks the state of a fruit tree patch based on varbit value ranges.
     *
     * This method uses the exact varbit ranges from RuneLite's PatchImplementation
     * to determine crop state without hardcoding all varbit values.
     *
     * @param client The RuneLite client instance
     * @param varbitIndex The varbit ID for the patch
     * @return The current state of the patch
     */
    public static PlantState checkFruitTreePatch(Client client, int varbitIndex) {
        int value = client.getVarbitValue(varbitIndex);

        // Check harvestable/remove first (before other states, as harvest values might overlap)
        // Apple tree[Chop-down,Inspect,Guide,Pick-apple] 14-20
        if (value >= 14 && value <= 20) {
            return PlantState.REMOVE;
        }
        // Apple tree stump[Clear,Inspect,Guide] 33
        if (value == 33) {
            return PlantState.REMOVE;
        }
        // Banana tree[Chop-down,Inspect,Guide,Pick-banana] 41-47
        if (value >= 41 && value <= 47) {
            return PlantState.REMOVE;
        }
        // Banana tree stump[Clear,Inspect,Guide] 60
        if (value == 60) {
            return PlantState.REMOVE;
        }
        // Orange tree[Chop-down,Inspect,Guide,Pick-orange] 78-84
        if (value >= 78 && value <= 84) {
            return PlantState.REMOVE;
        }
        // Orange tree stump[Clear,Inspect,Guide] 97
        if (value == 97) {
            return PlantState.REMOVE;
        }
        // Curry tree[Chop-down,Inspect,Guide,Pick-leaf] 105-111
        if (value >= 105 && value <= 111) {
            return PlantState.REMOVE;
        }
        // Curry tree stump[Clear,Inspect,Guide] 124
        if (value == 124) {
            return PlantState.REMOVE;
        }
        // Pineapple plant[Chop down,Inspect,Guide,Pick-pineapple] 142-148
        if (value >= 142 && value <= 148) {
            return PlantState.REMOVE;
        }
        // Pineapple plant stump[Clear,Inspect,Guide] 161
        if (value == 161) {
            return PlantState.REMOVE;
        }
        // Papaya tree[Chop-down,Inspect,Guide,Pick-fruit] 169-175
        if (value >= 169 && value <= 175) {
            return PlantState.REMOVE;
        }
        // Papaya tree stump[Clear,Inspect,Guide] 188
        if (value == 188) {
            return PlantState.REMOVE;
        }
        // Palm tree[Chop-down,Inspect,Guide,Pick-coconut] 206-212
        if (value >= 206 && value <= 212) {
            return PlantState.REMOVE;
        }
        // Palm tree stump[Clear,Inspect,Guide] 225
        if (value == 225) {
            return PlantState.REMOVE;
        }
        // Dragonfruit tree[Chop down,Inspect,Guide,Pick-dragonfruit] 233-239
        if (value >= 233 && value <= 239) {
            return PlantState.REMOVE;
        }
        // Dragonfruit tree stump[Clear,Inspect,Guide] 252
        if (value == 252) {
            return PlantState.REMOVE;
        }

        // Check dead before diseased, as dead is a more specific state
        // Dead apple tree[Clear,Inspect,Guide] 27-32
        if (value >= 27 && value <= 32) {
            return PlantState.DEAD;
        }
        // Dead banana tree[Clear,Inspect,Guide] 54-59
        if (value >= 54 && value <= 59) {
            return PlantState.DEAD;
        }
        // Dead orange tree[Clear,Inspect,Guide] 91-96
        if (value >= 91 && value <= 96) {
            return PlantState.DEAD;
        }
        // Dead curry tree[Clear,Inspect,Guide] 118-123
        if (value >= 118 && value <= 123) {
            return PlantState.DEAD;
        }
        // Dead pineapple plant[Clear,Inspect,Guide] 155-160
        if (value >= 155 && value <= 160) {
            return PlantState.DEAD;
        }
        // Dead papaya tree[Clear,Inspect,Guide] 182-187
        if (value >= 182 && value <= 187) {
            return PlantState.DEAD;
        }
        // Dead palm tree[Clear,Inspect,Guide] 219-224
        if (value >= 219 && value <= 224) {
            return PlantState.DEAD;
        }
        // Dead dragonfruit plant[Clear,Inspect,Guide] 246-251
        if (value >= 246 && value <= 251) {
            return PlantState.DEAD;
        }

        // Check diseased state
        // Diseased apple tree[Prune,Inspect,Guide] 21-26
        if (value >= 21 && value <= 26) {
            return PlantState.DISEASED;
        }
        // Diseased banana tree[Prune,Inspect,Guide] 48-53
        if (value >= 48 && value <= 53) {
            return PlantState.DISEASED;
        }
        // Diseased orange tree[Prune,Inspect,Guide] 85-89
        if (value >= 85 && value <= 89) {
            return PlantState.DISEASED;
        }
        // Diseased orange tree[Chop-down,Inspect,Guide] 90
        if (value == 90) {
            return PlantState.DISEASED;
        }
        // Diseased curry tree[Prune,Inspect,Guide] 112-117
        if (value >= 112 && value <= 117) {
            return PlantState.DISEASED;
        }
        // Diseased pineapple plant[Prune,Inspect,Guide] 149-154
        if (value >= 149 && value <= 154) {
            return PlantState.DISEASED;
        }
        // Diseased papaya tree[Prune,Inspect,Guide] 176-181
        if (value >= 176 && value <= 181) {
            return PlantState.DISEASED;
        }
        // Diseased palm tree[Prune,Inspect,Guide] 213-218
        if (value >= 213 && value <= 218) {
            return PlantState.DISEASED;
        }
        // Diseased dragonfruit plant[Prune,Inspect,Guide] 240-245
        if (value >= 240 && value <= 245) {
            return PlantState.DISEASED;
        }

        // Check healthy state (fully grown, check-health available)
        // Apple tree[Check-health,Inspect,Guide] 34
        if (value == 34) {
            return PlantState.HEALTHY;
        }
        // Banana tree[Check-health,Inspect,Guide] 61
        if (value == 61) {
            return PlantState.HEALTHY;
        }
        // Orange tree[Check-health,Inspect,Guide] 98
        if (value == 98) {
            return PlantState.HEALTHY;
        }
        // Curry tree[Check-health,Inspect,Guide] 125
        if (value == 125) {
            return PlantState.HEALTHY;
        }
        // Pineapple plant[Check-health,Inspect,Guide] 162
        if (value == 162) {
            return PlantState.HEALTHY;
        }
        // Papaya tree[Check-health,Inspect,Guide] 189
        if (value == 189) {
            return PlantState.HEALTHY;
        }
        // Palm tree[Check-health,Inspect,Guide] 226
        if (value == 226) {
            return PlantState.HEALTHY;
        }
        // Dragonfruit tree[Check-health,Inspect,Guide] 253
        if (value == 253) {
            return PlantState.HEALTHY;
        }

        // Check weeds (all weed states)
        // Fruit Tree Patch[Rake,Inspect,Guide] 0-3
        if (value >= 0 && value <= 3) {
            return PlantState.WEEDS;
        }
        // Fruit Tree Patch[Rake,Inspect,Guide] 4-7
        if (value >= 4 && value <= 7) {
            return PlantState.WEEDS;
        }
        // Fruit Tree Patch[Rake,Inspect,Guide] 62-71
        if (value >= 62 && value <= 71) {
            return PlantState.WEEDS;
        }
        // Fruit Tree Patch[Rake,Inspect,Guide] 126-135
        if (value >= 126 && value <= 135) {
            return PlantState.WEEDS;
        }
        // Fruit Tree Patch[Rake,Inspect,Guide] 190-199
        if (value >= 190 && value <= 199) {
            return PlantState.WEEDS;
        }
        // Fruit Tree Patch[Rake,Inspect,Guide] 254-255
        if (value >= 254 && value <= 255) {
            return PlantState.WEEDS;
        }

        // Growing ranges (checked after other states)
        // Apple tree[Inspect,Guide] 8-13
        if (value >= 8 && value <= 13) {
            return PlantState.GROWING;
        }
        // Banana tree[Inspect,Guide] 35-40
        if (value >= 35 && value <= 40) {
            return PlantState.GROWING;
        }
        // Orange tree[Inspect,Guide] 72-77
        if (value >= 72 && value <= 77) {
            return PlantState.GROWING;
        }
        // Curry tree[Inspect,Guide] 99-104
        if (value >= 99 && value <= 104) {
            return PlantState.GROWING;
        }
        // Pineapple plant[Inspect,Guide] 136-141
        if (value >= 136 && value <= 141) {
            return PlantState.GROWING;
        }
        // Papaya tree[Inspect,Guide] 163-168
        if (value >= 163 && value <= 168) {
            return PlantState.GROWING;
        }
        // Palm tree[Inspect,Guide] 200-205
        if (value >= 200 && value <= 205) {
            return PlantState.GROWING;
        }
        // Dragonfruit tree[Inspect,Guide] 227-232
        if (value >= 227 && value <= 232) {
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
