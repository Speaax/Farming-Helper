package com.easyfarming;

import net.runelite.api.Client;

/**
 * Herb patch state detection using range-based varbit value checks.
 * 
 * This implementation uses the exact varbit ranges from RuneLite's PatchImplementation
 * (HERB section, lines 435-717) to determine crop state without hardcoding all varbit values.
 * 
 * Varbit IDs vary by location:
 * - Standard locations (Ardougne, Catherby, Falador, Kourend, Morytania, Civitas): 4774 (FARMING_TRANSMIT_D)
 * - Harmony Island: 4772 (FARMING_TRANSMIT_B)
 * - Troll Stronghold/Weiss: 4771 (FARMING_TRANSMIT_A)
 * - Farming Guild: 4775
 * 
 * Note: The varbit value ranges are the same regardless of which transmit varbit is used.
 */
public class HerbPatchChecker {
    
    /**
     * Checks the state of a herb patch based on varbit value ranges.
     * 
     * This method uses the exact varbit ranges from RuneLite's PatchImplementation
     * to determine crop state without hardcoding all varbit values.
     * 
     * @param client The RuneLite client instance
     * @param varbitIndex The varbit ID for the patch
     * @return The current state of the patch
     */
    public static PlantState checkHerbPatch(Client client, int varbitIndex) {
        int value = client.getVarbitValue(varbitIndex);
        
        // Check harvestable first (before other states, as harvest values might overlap)
        // Herbs[Pick,Inspect,Guide] GUAM 8-10
        if (value >= 8 && value <= 10) {
            return PlantState.HARVESTABLE;
        }
        // Herbs[Pick,Inspect,Guide] MARRENTILL 15-17
        if (value >= 15 && value <= 17) {
            return PlantState.HARVESTABLE;
        }
        // Herbs[Pick,Inspect,Guide] TARROMIN 22-24
        if (value >= 22 && value <= 24) {
            return PlantState.HARVESTABLE;
        }
        // Herbs[Pick,Inspect,Guide] HARRALANDER 29-31
        if (value >= 29 && value <= 31) {
            return PlantState.HARVESTABLE;
        }
        // Herbs[Pick,Inspect,Guide] RANARR 36-38
        if (value >= 36 && value <= 38) {
            return PlantState.HARVESTABLE;
        }
        // Herbs[Pick,Inspect,Guide] TOADFLAX 43-45
        if (value >= 43 && value <= 45) {
            return PlantState.HARVESTABLE;
        }
        // Herbs[Pick,Inspect,Guide] IRIT 50-52
        if (value >= 50 && value <= 52) {
            return PlantState.HARVESTABLE;
        }
        // Herbs[Pick,Inspect,Guide] AVANTOE 57-59
        if (value >= 57 && value <= 59) {
            return PlantState.HARVESTABLE;
        }
        // Herbs[Pick,Inspect,Guide] HUASCA 64-66
        if (value >= 64 && value <= 66) {
            return PlantState.HARVESTABLE;
        }
        // Herbs[Pick,Inspect,Guide] KWUARM 72-74
        if (value >= 72 && value <= 74) {
            return PlantState.HARVESTABLE;
        }
        // Herbs[Pick,Inspect,Guide] SNAPDRAGON 79-81
        if (value >= 79 && value <= 81) {
            return PlantState.HARVESTABLE;
        }
        // Herbs[Pick,Inspect,Guide] CADANTINE 86-88
        if (value >= 86 && value <= 88) {
            return PlantState.HARVESTABLE;
        }
        // Herbs[Pick,Inspect,Guide] LANTADYME 93-95
        if (value >= 93 && value <= 95) {
            return PlantState.HARVESTABLE;
        }
        // Herbs[Pick,Inspect,Guide] DWARF_WEED 100-102
        if (value >= 100 && value <= 102) {
            return PlantState.HARVESTABLE;
        }
        // Herbs[Pick,Inspect,Guide] TORSTOL 107-109
        if (value >= 107 && value <= 109) {
            return PlantState.HARVESTABLE;
        }
        // Goutweed[Pick,Inspect,Guide] 196-197
        if (value >= 196 && value <= 197) {
            return PlantState.HARVESTABLE;
        }
        
        // Check dead before diseased, as dead is a more specific state
        // Dead herbs[Clear,Inspect,Guide] 170-172
        if (value >= 170 && value <= 172) {
            return PlantState.DEAD;
        }
        // Dead goutweed[Clear,Inspect,Guide] 201-203
        if (value >= 201 && value <= 203) {
            return PlantState.DEAD;
        }
        
        // Check weeds BEFORE diseased to avoid false positives
        // Herb patch[Rake,Inspect,Guide] 0-3 (value 3 = fully raked, ready to plant)
        // Note: In RuneLite, value 3 is treated as WEEDS with stage 0 (fully raked)
        // We'll handle value 3 specially in the step handler to show "plant" instead of "rake"
        if (value >= 0 && value <= 3) {
            return PlantState.WEEDS;
        }
        // Herb patch[Rake,Inspect,Guide] 67
        if (value == 67) {
            return PlantState.WEEDS;
        }
        // Herb patch[Rake,Inspect,Guide] 176-191
        if (value >= 176 && value <= 191) {
            return PlantState.WEEDS;
        }
        // Herb patch[Rake,Inspect,Guide] 204-219
        if (value >= 204 && value <= 219) {
            return PlantState.WEEDS;
        }
        // Herb patch[Rake,Inspect,Guide] 221-255
        if (value >= 221 && value <= 255) {
            return PlantState.WEEDS;
        }
        
        // Values 110-127 are used for newer herb types (like Huaska)
        if (value >= 110 && value <= 114) {
            return PlantState.GROWING;
        }
        if (value >= 115 && value <= 117) {
            return PlantState.DISEASED;
        }
        if (value == 118) {
            return PlantState.DEAD;
        }
        if (value >= 119 && value <= 122) {
            return PlantState.HARVESTABLE;
        }
        if (value >= 123 && value <= 127) {
            return PlantState.GROWING;
        }
        
        // Check diseased state (only after ensuring value is not weeds or in gap range)
        // Diseased herbs[Cure,Inspect,Guide] GUAM 128-130
        if (value >= 128 && value <= 130) {
            return PlantState.DISEASED;
        }
        // Diseased herbs[Cure,Inspect,Guide] MARRENTILL 131-133
        if (value >= 131 && value <= 133) {
            return PlantState.DISEASED;
        }
        // Diseased herbs[Cure,Inspect,Guide] TARROMIN 134-136
        if (value >= 134 && value <= 136) {
            return PlantState.DISEASED;
        }
        // Diseased herbs[Cure,Inspect,Guide] HARRALANDER 137-139
        if (value >= 137 && value <= 139) {
            return PlantState.DISEASED;
        }
        // Diseased herbs[Cure,Inspect,Guide] RANARR 140-142
        if (value >= 140 && value <= 142) {
            return PlantState.DISEASED;
        }
        // Diseased herbs[Cure,Inspect,Guide] TOADFLAX 143-145
        if (value >= 143 && value <= 145) {
            return PlantState.DISEASED;
        }
        // Diseased herbs[Cure,Inspect,Guide] IRIT 146-148
        if (value >= 146 && value <= 148) {
            return PlantState.DISEASED;
        }
        // Diseased herbs[Cure,Inspect,Guide] AVANTOE 149-151
        if (value >= 149 && value <= 151) {
            return PlantState.DISEASED;
        }
        // Diseased herbs[Cure,Inspect,Guide] KWUARM 152-154
        if (value >= 152 && value <= 154) {
            return PlantState.DISEASED;
        }
        // Diseased herbs[Cure,Inspect,Guide] SNAPDRAGON 155-157
        if (value >= 155 && value <= 157) {
            return PlantState.DISEASED;
        }
        // Diseased herbs[Cure,Inspect,Guide] CADANTINE 158-160
        if (value >= 158 && value <= 160) {
            return PlantState.DISEASED;
        }
        // Diseased herbs[Cure,Inspect,Guide] LANTADYME 161-163
        if (value >= 161 && value <= 163) {
            return PlantState.DISEASED;
        }
        // Diseased herbs[Cure,Inspect,Guide] DWARF_WEED 164-166
        if (value >= 164 && value <= 166) {
            return PlantState.DISEASED;
        }
        // Diseased herbs[Cure,Inspect,Guide] TORSTOL 167-169
        if (value >= 167 && value <= 169) {
            return PlantState.DISEASED;
        }
        // Diseased herbs[Cure,Inspect,Guide] HUASCA 173-175
        if (value >= 173 && value <= 175) {
            return PlantState.DISEASED;
        }
        // Diseased goutweed[Cure,Inspect,Guide] 198-200
        if (value >= 198 && value <= 200) {
            return PlantState.DISEASED;
        }
        
        // Growing ranges (checked after other states)
        // Herbs[Inspect,Guide] GUAM 4-7
        if (value >= 4 && value <= 7) {
            return PlantState.GROWING;
        }
        // Herbs[Inspect,Guide] MARRENTILL 11-14
        if (value >= 11 && value <= 14) {
            return PlantState.GROWING;
        }
        // Herbs[Inspect,Guide] TARROMIN 18-21
        if (value >= 18 && value <= 21) {
            return PlantState.GROWING;
        }
        // Herbs[Inspect,Guide] HARRALANDER 25-28
        if (value >= 25 && value <= 28) {
            return PlantState.GROWING;
        }
        // Herbs[Inspect,Guide] RANARR 32-35
        if (value >= 32 && value <= 35) {
            return PlantState.GROWING;
        }
        // Herbs[Inspect,Guide] TOADFLAX 39-42
        if (value >= 39 && value <= 42) {
            return PlantState.GROWING;
        }
        // Herbs[Inspect,Guide] IRIT 46-49
        if (value >= 46 && value <= 49) {
            return PlantState.GROWING;
        }
        // Herbs[Inspect,Guide] AVANTOE 53-56
        if (value >= 53 && value <= 56) {
            return PlantState.GROWING;
        }
        // Herbs[Inspect,Guide] HUASCA 60-63
        if (value >= 60 && value <= 63) {
            return PlantState.GROWING;
        }
        // Herbs[Inspect,Guide] KWUARM 68-71
        if (value >= 68 && value <= 71) {
            return PlantState.GROWING;
        }
        // Herbs[Inspect,Guide] SNAPDRAGON 75-78
        if (value >= 75 && value <= 78) {
            return PlantState.GROWING;
        }
        // Herbs[Inspect,Guide] CADANTINE 82-85
        if (value >= 82 && value <= 85) {
            return PlantState.GROWING;
        }
        // Herbs[Inspect,Guide] LANTADYME 89-92
        if (value >= 89 && value <= 92) {
            return PlantState.GROWING;
        }
        // Herbs[Inspect,Guide] DWARF_WEED 96-99
        if (value >= 96 && value <= 99) {
            return PlantState.GROWING;
        }
        // Herbs[Inspect,Guide] TORSTOL 103-106
        if (value >= 103 && value <= 106) {
            return PlantState.GROWING;
        }
        // Goutweed[Inspect,Guide] 192-195
        if (value >= 192 && value <= 195) {
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
