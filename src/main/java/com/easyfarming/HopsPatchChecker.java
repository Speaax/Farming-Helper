package com.easyfarming;

import net.runelite.api.Client;

/**
 * Hops patch state detection using range-based varbit value checks.
 * 
 * This implementation follows the pattern used in RuneLite's PatchImplementation,
 * using range checks instead of hardcoded lists of varbit values.
 * 
 * Varbit ID: 4771 (FARMING_TRANSMIT_A) - used for all hops patches
 */
public class HopsPatchChecker {
    
    /**
     * Checks the state of a hops patch based on varbit value ranges.
     * 
     * This method uses the exact varbit ranges from RuneLite's PatchImplementation
     * (lines 1507-1834) to determine crop state without hardcoding all varbit values.
     * 
     * @param client The RuneLite client instance
     * @param varbitIndex The varbit ID for the patch
     * @return The current state of the patch
     */
    public static PlantState checkHopsPatch(Client client, int varbitIndex) {
        int value = client.getVarbitValue(varbitIndex);
        
        // Check harvestable first (before other states, as harvest values might overlap)
        // Hammerstone Hops[Harvest,Inspect,Guide] 8-10
        if (value >= 8 && value <= 10) {
            return PlantState.HARVESTABLE;
        }
        // Asgarnian Hops[Harvest,Inspect,Guide] 19-21
        if (value >= 19 && value <= 21) {
            return PlantState.HARVESTABLE;
        }
        // Yanillian Hops[Harvest,Inspect,Guide] 32-34
        if (value >= 32 && value <= 34) {
            return PlantState.HARVESTABLE;
        }
        // Krandorian Hops[Harvest,Inspect,Guide] 47-49
        if (value >= 47 && value <= 49) {
            return PlantState.HARVESTABLE;
        }
        // Wildblood Hops[Harvest,Inspect,Guide] 64-66
        if (value >= 64 && value <= 66) {
            return PlantState.HARVESTABLE;
        }
        // Barley[Harvest,Inspect,Guide] 78-80
        if (value >= 78 && value <= 80) {
            return PlantState.HARVESTABLE;
        }
        // Jute[Harvest,Inspect,Guide] 89-91
        if (value >= 89 && value <= 91) {
            return PlantState.HARVESTABLE;
        }
        // Flax[Harvest,Inspect,Guide] 99-101
        if (value >= 99 && value <= 101) {
            return PlantState.HARVESTABLE;
        }
        // Hemp[Harvest,Inspect,Guide] 108-110
        if (value >= 108 && value <= 110) {
            return PlantState.HARVESTABLE;
        }
        // Cotton[Harvest,Inspect,Guide] 119-121
        if (value >= 119 && value <= 121) {
            return PlantState.HARVESTABLE;
        }
        
        // Check dead before diseased, as dead is a more specific state
        // Dead Hammerstone Hops[Clear,Inspect,Guide] 139-141
        if (value >= 139 && value <= 141) {
            return PlantState.DEAD;
        }
        // Dead Asgarnian Hops[Clear,Inspect,Guide] 150-153
        if (value >= 150 && value <= 153) {
            return PlantState.DEAD;
        }
        // Dead Yanillian Hops[Clear,Inspect,Guide] 163-167
        if (value >= 163 && value <= 167) {
            return PlantState.DEAD;
        }
        // Dead Krandorian Hops[Clear,Inspect,Guide] 178-183
        if (value >= 178 && value <= 183) {
            return PlantState.DEAD;
        }
        // Dead Wildblood Hops[Clear,Inspect,Guide] 195-201
        if (value >= 195 && value <= 201) {
            return PlantState.DEAD;
        }
        // Dead Barley[Clear,Inspect,Guide] 209-211
        if (value >= 209 && value <= 211) {
            return PlantState.DEAD;
        }
        // Dead Jute[Clear,Inspect,Guide] 220-223
        if (value >= 220 && value <= 223) {
            return PlantState.DEAD;
        }
        // Dead Flax[Clear,Inspect,Guide] 230-231
        if (value >= 230 && value <= 231) {
            return PlantState.DEAD;
        }
        // Dead Hemp[Clear,Inspect,Guide] 239-241
        if (value >= 239 && value <= 241) {
            return PlantState.DEAD;
        }
        // Dead Cotton[Clear,Inspect,Guide] 250-253
        if (value >= 250 && value <= 253) {
            return PlantState.DEAD;
        }
        
        // Check diseased state
        // Diseased Hammerstone Hops[Cure,Inspect,Guide] 11-13
        if (value >= 11 && value <= 13) {
            return PlantState.DISEASED;
        }
        // Diseased Asgarnian Hops[Cure,Inspect,Guide] 22-25
        if (value >= 22 && value <= 25) {
            return PlantState.DISEASED;
        }
        // Diseased Yanillian Hops[Cure,Inspect,Guide] 35-39
        if (value >= 35 && value <= 39) {
            return PlantState.DISEASED;
        }
        // Diseased Krandorian Hops[Cure,Inspect,Guide] 50-55
        if (value >= 50 && value <= 55) {
            return PlantState.DISEASED;
        }
        // Diseased Wildblood Hops[Cure,Inspect,Guide] 67-73
        if (value >= 67 && value <= 73) {
            return PlantState.DISEASED;
        }
        // Diseased Barley[Cure,Inspect,Guide] 81-83
        if (value >= 81 && value <= 83) {
            return PlantState.DISEASED;
        }
        // Diseased Jute[Cure,Inspect,Guide] 92-95
        if (value >= 92 && value <= 95) {
            return PlantState.DISEASED;
        }
        // Diseased Flax[Cure,Inspect,Guide] 102-103
        if (value >= 102 && value <= 103) {
            return PlantState.DISEASED;
        }
        // Diseased Hemp[Cure,Inspect,Guide] 111-113
        if (value >= 111 && value <= 113) {
            return PlantState.DISEASED;
        }
        // Diseased Cotton[Cure,Inspect,Guide] 122-125
        if (value >= 122 && value <= 125) {
            return PlantState.DISEASED;
        }
        
        // Check for empty patch ready to plant (value 3 = fully raked)
        if (value == 3) {
            return PlantState.PLANT;
        }
        
        // Check weeds (all weed states)
        // Hops Patch[Rake,Inspect,Guide] 0-2 (needs raking)
        if (value >= 0 && value <= 2) {
            return PlantState.WEEDS;
        }
        // Hops Patch[Rake,Inspect,Guide] 126-131
        if (value >= 126 && value <= 131) {
            return PlantState.WEEDS;
        }
        // Hops Patch[Rake,Inspect,Guide] 136-138
        if (value >= 136 && value <= 138) {
            return PlantState.WEEDS;
        }
        // Hops Patch[Rake,Inspect,Guide] 147-149
        if (value >= 147 && value <= 149) {
            return PlantState.WEEDS;
        }
        // Hops Patch[Rake,Inspect,Guide] 160-162
        if (value >= 160 && value <= 162) {
            return PlantState.WEEDS;
        }
        // Hops Patch[Rake,Inspect,Guide] 175-177
        if (value >= 175 && value <= 177) {
            return PlantState.WEEDS;
        }
        // Hops Patch[Rake,Inspect,Guide] 192-194
        if (value >= 192 && value <= 194) {
            return PlantState.WEEDS;
        }
        // Hops Patch[Rake,Inspect,Guide] 206-208
        if (value >= 206 && value <= 208) {
            return PlantState.WEEDS;
        }
        // Hops Patch[Rake,Inspect,Guide] 217-219
        if (value >= 217 && value <= 219) {
            return PlantState.WEEDS;
        }
        // Hops Patch[Rake,Inspect,Guide] 227-229
        if (value >= 227 && value <= 229) {
            return PlantState.WEEDS;
        }
        // Hops Patch[Rake,Inspect,Guide] 236-238
        if (value >= 236 && value <= 238) {
            return PlantState.WEEDS;
        }
        // Hops Patch[Rake,Inspect,Guide] 247-249
        if (value >= 247 && value <= 249) {
            return PlantState.WEEDS;
        }
        // Hops Patch[Rake,Inspect,Guide] 254-255
        if (value >= 254 && value <= 255) {
            return PlantState.WEEDS;
        }
        
        // Check needsWater (growing states that need water)
        // Hammerstone Hops[Inspect,Guide] 4-7 (needs water)
        if (value >= 4 && value <= 7) {
            return PlantState.NEEDS_WATER;
        }
        // Asgarnian Hops[Inspect,Guide] 14-18 (needs water)
        if (value >= 14 && value <= 18) {
            return PlantState.NEEDS_WATER;
        }
        // Yanillian Hops[Inspect,Guide] 26-31 (needs water)
        if (value >= 26 && value <= 31) {
            return PlantState.NEEDS_WATER;
        }
        // Krandorian Hops[Inspect,Guide] 40-46 (needs water)
        if (value >= 40 && value <= 46) {
            return PlantState.NEEDS_WATER;
        }
        // Wildblood Hops[Inspect,Guide] 56-63 (needs water)
        if (value >= 56 && value <= 63) {
            return PlantState.NEEDS_WATER;
        }
        // Barley[Inspect,Guide] 74-77 (needs water)
        if (value >= 74 && value <= 77) {
            return PlantState.NEEDS_WATER;
        }
        // Jute[Inspect,Guide] 84-88 (needs water)
        if (value >= 84 && value <= 88) {
            return PlantState.NEEDS_WATER;
        }
        // Flax[Inspect,Guide] 96-98 (needs water)
        if (value >= 96 && value <= 98) {
            return PlantState.NEEDS_WATER;
        }
        // Hemp[Inspect,Guide] 104-107 (needs water)
        if (value >= 104 && value <= 107) {
            return PlantState.NEEDS_WATER;
        }
        // Cotton[Inspect,Guide] 114-118 (needs water)
        if (value >= 114 && value <= 118) {
            return PlantState.NEEDS_WATER;
        }
        
        // Growing ranges (watered and growing)
        // Hammerstone Hops[Inspect,Guide] 132-135
        if (value >= 132 && value <= 135) {
            return PlantState.GROWING;
        }
        // Asgarnian Hops[Inspect,Guide] 142-146
        if (value >= 142 && value <= 146) {
            return PlantState.GROWING;
        }
        // Yanillian Hops[Inspect,Guide] 154-159
        if (value >= 154 && value <= 159) {
            return PlantState.GROWING;
        }
        // Krandorian Hops[Inspect,Guide] 168-174
        if (value >= 168 && value <= 174) {
            return PlantState.GROWING;
        }
        // Wildblood Hops[Inspect,Guide] 184-191
        if (value >= 184 && value <= 191) {
            return PlantState.GROWING;
        }
        // Barley[Inspect,Guide] 202-205
        if (value >= 202 && value <= 205) {
            return PlantState.GROWING;
        }
        // Jute[Inspect,Guide] 212-216
        if (value >= 212 && value <= 216) {
            return PlantState.GROWING;
        }
        // Flax[Inspect,Guide] 224-226
        if (value >= 224 && value <= 226) {
            return PlantState.GROWING;
        }
        // Hemp[Inspect,Guide] 232-235
        if (value >= 232 && value <= 235) {
            return PlantState.GROWING;
        }
        // Cotton[Inspect,Guide] 242-246
        if (value >= 242 && value <= 246) {
            return PlantState.GROWING;
        }
        
        // Unknown state
        return PlantState.UNKNOWN;
    }
    public enum PlantState {
        GROWING,
        NEEDS_WATER,
        DISEASED,
        HARVESTABLE,
        WEEDS,
        DEAD,
        PLANT,
        UNKNOWN
    }

}

