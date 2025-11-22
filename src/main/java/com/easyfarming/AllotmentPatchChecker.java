package com.easyfarming;

import net.runelite.api.Client;

/**
 * Allotment patch state detection using range-based varbit value checks.
 * 
 * This implementation uses the exact varbit ranges from RuneLite's PatchImplementation
 * (ALLOTMENT section, lines 102-434) to determine crop state without hardcoding all varbit values.
 * 
 * Varbit IDs: Determined from object composition (typically in 4771-4774 range)
 * - Standard locations: Varbit IDs in 4771-4774 range
 * - Farming Guild: Varbit IDs in 7904-7914 range
 */
public class AllotmentPatchChecker {
    
    /**
     * Checks the state of an allotment patch based on varbit value ranges.
     * 
     * This method uses the exact varbit ranges from RuneLite's PatchImplementation
     * to determine crop state without hardcoding all varbit values.
     * 
     * @param client The RuneLite client instance
     * @param varbitIndex The varbit ID for the patch
     * @return The current state of the patch
     */
    public static PlantState checkAllotmentPatch(Client client, int varbitIndex) {
        int value = client.getVarbitValue(varbitIndex);
        
        // Check for empty patch ready to plant (value 3 = fully raked)
        if (value == 3) {
            return PlantState.PLANT;
        }
        
        // Check harvestable first (before other states)
        // Potato[Harvest,Inspect,Guide] 10-12
        if (value >= 10 && value <= 12) {
            return PlantState.HARVESTABLE;
        }
        // Onion[Harvest,Inspect,Guide] 17-19
        if (value >= 17 && value <= 19) {
            return PlantState.HARVESTABLE;
        }
        // Cabbages[Harvest,Inspect,Guide] 24-26
        if (value >= 24 && value <= 26) {
            return PlantState.HARVESTABLE;
        }
        // Tomato[Harvest,Inspect,Guide] 31-33
        if (value >= 31 && value <= 33) {
            return PlantState.HARVESTABLE;
        }
        // Sweetcorn[Harvest,Inspect,Guide] 40-42
        if (value >= 40 && value <= 42) {
            return PlantState.HARVESTABLE;
        }
        // Strawberry[Harvest,Inspect,Guide] 49-51
        if (value >= 49 && value <= 51) {
            return PlantState.HARVESTABLE;
        }
        // Watermelon[Harvest,Inspect,Guide] 60-62
        if (value >= 60 && value <= 62) {
            return PlantState.HARVESTABLE;
        }
        // Snape grass plant[Harvest,Inspect,Guide] 138-140
        if (value >= 138 && value <= 140) {
            return PlantState.HARVESTABLE;
        }
        
        // Check dead before diseased, as dead is a more specific state
        // Dead potatoes[Clear,Inspect,Guide] 199-201
        if (value >= 199 && value <= 201) {
            return PlantState.DEAD;
        }
        // Dead onions[Clear,Inspect,Guide] 206-208
        if (value >= 206 && value <= 208) {
            return PlantState.DEAD;
        }
        // Dead cabbages[Clear,Inspect,Guide] 213-215
        if (value >= 213 && value <= 215) {
            return PlantState.DEAD;
        }
        // Dead tomato plant[Clear,Inspect,Guide] 220-222
        if (value >= 220 && value <= 222) {
            return PlantState.DEAD;
        }
        // Dead sweetcorn plant[Clear,Inspect,Guide] 227-231
        if (value >= 227 && value <= 231) {
            return PlantState.DEAD;
        }
        // Dead strawberry plant[Clear,Inspect,Guide] 236-240
        if (value >= 236 && value <= 240) {
            return PlantState.DEAD;
        }
        // Dead watermelons[Clear,Inspect,Guide] 245-251
        if (value >= 245 && value <= 251) {
            return PlantState.DEAD;
        }
        // Dead Snape grass[Clear,Inspect,Guide] 193-195, 209-211
        if (value >= 193 && value <= 195) {
            return PlantState.DEAD;
        }
        if (value >= 209 && value <= 211) {
            return PlantState.DEAD;
        }
        
        // Check diseased state
        // Diseased potatoes[Cure,Inspect,Guide] 135-137
        if (value >= 135 && value <= 137) {
            return PlantState.DISEASED;
        }
        // Diseased onions[Cure,Inspect,Guide] 142-144
        if (value >= 142 && value <= 144) {
            return PlantState.DISEASED;
        }
        // Diseased cabbages[Cure,Inspect,Guide] 149-151
        if (value >= 149 && value <= 151) {
            return PlantState.DISEASED;
        }
        // Diseased tomato plant[Cure,Inspect,Guide] 156-158
        if (value >= 156 && value <= 158) {
            return PlantState.DISEASED;
        }
        // Diseased sweetcorn plant[Cure,Inspect,Guide] 163-167
        if (value >= 163 && value <= 167) {
            return PlantState.DISEASED;
        }
        // Diseased strawberry plant[Cure,Inspect,Guide] 172-176
        if (value >= 172 && value <= 176) {
            return PlantState.DISEASED;
        }
        // Diseased watermelons[Cure,Inspect,Guide] 181-187
        if (value >= 181 && value <= 187) {
            return PlantState.DISEASED;
        }
        // Diseased Snape grass[Cure,Inspect,Guide] 196-198, 202-204
        if (value >= 196 && value <= 198) {
            return PlantState.DISEASED;
        }
        if (value >= 202 && value <= 204) {
            return PlantState.DISEASED;
        }
        
        // Check weeds (all weed states)
        // Allotment[Rake,Inspect,Guide] 0-2
        if (value >= 0 && value <= 2) {
            return PlantState.WEEDS;
        }
        // Allotment[Rake,Inspect,Guide] 4-5
        if (value >= 4 && value <= 5) {
            return PlantState.WEEDS;
        }
        // Allotment[Rake,Inspect,Guide] 74-76
        if (value >= 74 && value <= 76) {
            return PlantState.WEEDS;
        }
        // Allotment[Rake,Inspect,Guide] 81-83
        if (value >= 81 && value <= 83) {
            return PlantState.WEEDS;
        }
        // Allotment[Rake,Inspect,Guide] 88-90
        if (value >= 88 && value <= 90) {
            return PlantState.WEEDS;
        }
        // Allotment[Rake,Inspect,Guide] 95-97
        if (value >= 95 && value <= 97) {
            return PlantState.WEEDS;
        }
        // Allotment[Rake,Inspect,Guide] 104-106
        if (value >= 104 && value <= 106) {
            return PlantState.WEEDS;
        }
        // Allotment[Rake,Inspect,Guide] 113-115
        if (value >= 113 && value <= 115) {
            return PlantState.WEEDS;
        }
        // Allotment[Rake,Inspect,Guide] 124-127
        if (value >= 124 && value <= 127) {
            return PlantState.WEEDS;
        }
        // Allotment[Rake,Inspect,Guide] 141
        if (value == 141) {
            return PlantState.WEEDS;
        }
        // Allotment[Rake,Inspect,Guide] 145-148
        if (value >= 145 && value <= 148) {
            return PlantState.WEEDS;
        }
        // Allotment[Rake,Inspect,Guide] 152-155
        if (value >= 152 && value <= 155) {
            return PlantState.WEEDS;
        }
        // Allotment[Rake,Inspect,Guide] 159-162
        if (value >= 159 && value <= 162) {
            return PlantState.WEEDS;
        }
        // Allotment[Rake,Inspect,Guide] 168-171
        if (value >= 168 && value <= 171) {
            return PlantState.WEEDS;
        }
        // Allotment[Rake,Inspect,Guide] 177-180
        if (value >= 177 && value <= 180) {
            return PlantState.WEEDS;
        }
        // Allotment[Rake,Inspect,Guide] 188-192
        if (value >= 188 && value <= 192) {
            return PlantState.WEEDS;
        }
        // Allotment[Rake,Inspect,Guide] 205
        if (value == 205) {
            return PlantState.WEEDS;
        }
        // Allotment[Rake,Inspect,Guide] 212
        if (value == 212) {
            return PlantState.WEEDS;
        }
        // Allotment[Rake,Inspect,Guide] 216-219
        if (value >= 216 && value <= 219) {
            return PlantState.WEEDS;
        }
        // Allotment[Rake,Inspect,Guide] 223-226
        if (value >= 223 && value <= 226) {
            return PlantState.WEEDS;
        }
        // Allotment[Rake,Inspect,Guide] 232-235
        if (value >= 232 && value <= 235) {
            return PlantState.WEEDS;
        }
        // Allotment[Rake,Inspect,Guide] 241-244
        if (value >= 241 && value <= 244) {
            return PlantState.WEEDS;
        }
        // Allotment[Rake,Inspect,Guide] 252-255
        if (value >= 252 && value <= 255) {
            return PlantState.WEEDS;
        }
        
        // Growing ranges (checked after other states)
        // Potato seed,Potato plant[Inspect,Guide] 6-9, 70-73
        if (value >= 6 && value <= 9) {
            return PlantState.GROWING;
        }
        if (value >= 70 && value <= 73) {
            return PlantState.GROWING;
        }
        // Onion seeds,Onion plant[Inspect,Guide] 13-16, 77-80
        if (value >= 13 && value <= 16) {
            return PlantState.GROWING;
        }
        if (value >= 77 && value <= 80) {
            return PlantState.GROWING;
        }
        // Cabbages[Inspect,Guide] 20-23, 84-87
        if (value >= 20 && value <= 23) {
            return PlantState.GROWING;
        }
        if (value >= 84 && value <= 87) {
            return PlantState.GROWING;
        }
        // Tomato plant[Inspect,Guide] 27-30, 91-94
        if (value >= 27 && value <= 30) {
            return PlantState.GROWING;
        }
        if (value >= 91 && value <= 94) {
            return PlantState.GROWING;
        }
        // Sweetcorn seed,Sweetcorn plant[Inspect,Guide] 34-39, 98-103
        if (value >= 34 && value <= 39) {
            return PlantState.GROWING;
        }
        if (value >= 98 && value <= 103) {
            return PlantState.GROWING;
        }
        // Strawberry seed,Strawberry plant[Inspect,Guide] 43-48, 107-112
        if (value >= 43 && value <= 48) {
            return PlantState.GROWING;
        }
        if (value >= 107 && value <= 112) {
            return PlantState.GROWING;
        }
        // Watermelon seed,Watermelons[Inspect,Guide] 52-59, 116-123
        if (value >= 52 && value <= 59) {
            return PlantState.GROWING;
        }
        if (value >= 116 && value <= 123) {
            return PlantState.GROWING;
        }
        // Snape grass seedling,Snape grass plant[Inspect,Guide] 63-69, 128-134
        if (value >= 63 && value <= 69) {
            return PlantState.GROWING;
        }
        if (value >= 128 && value <= 134) {
            return PlantState.GROWING;
        }
        
        // Unknown state
        return PlantState.UNKNOWN;
    }
    
    public enum PlantState {
        GROWING,
        DISEASED,
        HARVESTABLE,
        WEEDS,
        DEAD,
        PLANT,
        UNKNOWN
    }
}
