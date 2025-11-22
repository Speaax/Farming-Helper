package com.easyfarming;

import net.runelite.api.Client;

/**
 * Flower patch state detection using range-based varbit value checks.
 * 
 * This implementation uses the exact varbit ranges from RuneLite's PatchImplementation
 * (FLOWER section, lines 718-990) to determine crop state without hardcoding all varbit values.
 * 
 * Varbit ID: 4773 (FARMING_TRANSMIT_C) - used for all flower patches
 */
public class FlowerPatchChecker {
    
    /**
     * Checks the state of a flower patch based on varbit value ranges.
     * 
     * This method uses the exact varbit ranges from RuneLite's PatchImplementation
     * to determine crop state without hardcoding all varbit values.
     * 
     * @param client The RuneLite client instance
     * @param varbitIndex The varbit ID for the patch
     * @return The current state of the patch
     */
    public static PlantState checkFlowerPatch(Client client, int varbitIndex) {
        int value = client.getVarbitValue(varbitIndex);
        
        // Check for empty patch ready to plant (value 3 = fully raked)
        if (value == 3) {
            return PlantState.PLANT;
        }
        
        // Check harvestable first (before other states, as harvest values might overlap)
        // Marigold[Pick,Inspect,Guide] 12
        if (value == 12) {
            return PlantState.HARVESTABLE;
        }
        // Rosemary[Pick,Inspect,Guide] 17
        if (value == 17) {
            return PlantState.HARVESTABLE;
        }
        // Nasturtium[Pick,Inspect,Guide] 22
        if (value == 22) {
            return PlantState.HARVESTABLE;
        }
        // Woad plant[Pick,Inspect,Guide] 27
        if (value == 27) {
            return PlantState.HARVESTABLE;
        }
        // Limpwurt plant[Pick,Inspect,Guide] 32
        if (value == 32) {
            return PlantState.HARVESTABLE;
        }
        // White lily[Pick,Inspect,Guide] 41
        if (value == 41) {
            return PlantState.HARVESTABLE;
        }
        
        // Check dead before diseased, as dead is a more specific state
        // Dead marigold[Clear,Inspect,Guide] 201-204
        if (value >= 201 && value <= 204) {
            return PlantState.DEAD;
        }
        // Dead rosemary[Clear,Inspect,Guide] 206-209
        if (value >= 206 && value <= 209) {
            return PlantState.DEAD;
        }
        // Dead nasturtium[Clear,Inspect,Guide] 211-214
        if (value >= 211 && value <= 214) {
            return PlantState.DEAD;
        }
        // Dead woad plant[Clear,Inspect,Guide] 216-219
        if (value >= 216 && value <= 219) {
            return PlantState.DEAD;
        }
        // Dead limpwurt plant[Clear,Inspect,Guide] 221-224
        if (value >= 221 && value <= 224) {
            return PlantState.DEAD;
        }
        // Dead White lily[Clear,Inspect,Guide] 230-233
        if (value >= 230 && value <= 233) {
            return PlantState.DEAD;
        }
        
        // Check diseased state
        // Diseased marigold[Cure,Inspect,Guide] 137-139
        if (value >= 137 && value <= 139) {
            return PlantState.DISEASED;
        }
        // Diseased rosemary[Cure,Inspect,Guide] 142-144
        if (value >= 142 && value <= 144) {
            return PlantState.DISEASED;
        }
        // Diseased nasturtium[Cure,Inspect,Guide] 147-149
        if (value >= 147 && value <= 149) {
            return PlantState.DISEASED;
        }
        // Diseased woad plant[Cure,Inspect,Guide] 152-154
        if (value >= 152 && value <= 154) {
            return PlantState.DISEASED;
        }
        // Diseased limpwurt plant[Cure,Inspect,Guide] 157-159
        if (value >= 157 && value <= 159) {
            return PlantState.DISEASED;
        }
        // Diseased White lily[Cure,Inspect,Guide] 166-168
        if (value >= 166 && value <= 168) {
            return PlantState.DISEASED;
        }
        
        // Check weeds (all weed states)
        // Flower Patch[Rake,Inspect,Guide] 0-2
        if (value >= 0 && value <= 2) {
            return PlantState.WEEDS;
        }
        // Flower Patch[Rake,Inspect,Guide] 4-7
        if (value >= 4 && value <= 7) {
            return PlantState.WEEDS;
        }
        // Flower Patch[Rake,Inspect,Guide] 42-71
        if (value >= 42 && value <= 71) {
            return PlantState.WEEDS;
        }
        // Flower Patch[Rake,Inspect,Guide] 76
        if (value == 76) {
            return PlantState.WEEDS;
        }
        // Flower Patch[Rake,Inspect,Guide] 81
        if (value == 81) {
            return PlantState.WEEDS;
        }
        // Flower Patch[Rake,Inspect,Guide] 86
        if (value == 86) {
            return PlantState.WEEDS;
        }
        // Flower Patch[Rake,Inspect,Guide] 91
        if (value == 91) {
            return PlantState.WEEDS;
        }
        // Flower Patch[Rake,Inspect,Guide] 96-100
        if (value >= 96 && value <= 100) {
            return PlantState.WEEDS;
        }
        // Flower Patch[Rake,Inspect,Guide] 105-136
        if (value >= 105 && value <= 136) {
            return PlantState.WEEDS;
        }
        // Flower Patch[Rake,Inspect,Guide] 140-141
        if (value >= 140 && value <= 141) {
            return PlantState.WEEDS;
        }
        // Flower Patch[Rake,Inspect,Guide] 145-146
        if (value >= 145 && value <= 146) {
            return PlantState.WEEDS;
        }
        // Flower Patch[Rake,Inspect,Guide] 150-151
        if (value >= 150 && value <= 151) {
            return PlantState.WEEDS;
        }
        // Flower Patch[Rake,Inspect,Guide] 155-156
        if (value >= 155 && value <= 156) {
            return PlantState.WEEDS;
        }
        // Flower Patch[Rake,Inspect,Guide] 160-165
        if (value >= 160 && value <= 165) {
            return PlantState.WEEDS;
        }
        // Flower Patch[Rake,Inspect,Guide] 169-200
        if (value >= 169 && value <= 200) {
            return PlantState.WEEDS;
        }
        // Flower Patch[Rake,Inspect,Guide] 205
        if (value == 205) {
            return PlantState.WEEDS;
        }
        // Flower Patch[Rake,Inspect,Guide] 210
        if (value == 210) {
            return PlantState.WEEDS;
        }
        // Flower Patch[Rake,Inspect,Guide] 215
        if (value == 215) {
            return PlantState.WEEDS;
        }
        // Flower Patch[Rake,Inspect,Guide] 220
        if (value == 220) {
            return PlantState.WEEDS;
        }
        // Flower Patch[Rake,Inspect,Guide] 225-229
        if (value >= 225 && value <= 229) {
            return PlantState.WEEDS;
        }
        // Flower Patch[Rake,Inspect,Guide] 234-255
        if (value >= 234 && value <= 255) {
            return PlantState.WEEDS;
        }
        
        // Growing ranges (checked after other states)
        // Marigold[Inspect,Guide] 8-11
        if (value >= 8 && value <= 11) {
            return PlantState.GROWING;
        }
        // Rosemary[Inspect,Guide] 13-16
        if (value >= 13 && value <= 16) {
            return PlantState.GROWING;
        }
        // Nasturtium[Inspect,Guide] 18-21
        if (value >= 18 && value <= 21) {
            return PlantState.GROWING;
        }
        // Woad plant[Inspect,Guide] 23-26
        if (value >= 23 && value <= 26) {
            return PlantState.GROWING;
        }
        // Limpwurt plant[Inspect,Guide] 28-31
        if (value >= 28 && value <= 31) {
            return PlantState.GROWING;
        }
        // Scarecrow[Rake,Inspect,Guide,Remove] 33-36
        if (value >= 33 && value <= 36) {
            return PlantState.GROWING;
        }
        // White lily[Inspect,Guide] 37-40
        if (value >= 37 && value <= 40) {
            return PlantState.GROWING;
        }
        // Marigold[Inspect,Guide] 72-75
        if (value >= 72 && value <= 75) {
            return PlantState.GROWING;
        }
        // Rosemary[Inspect,Guide] 77-80
        if (value >= 77 && value <= 80) {
            return PlantState.GROWING;
        }
        // Nasturtium[Inspect,Guide] 82-85
        if (value >= 82 && value <= 85) {
            return PlantState.GROWING;
        }
        // Woad plant[Inspect,Guide] 87-90
        if (value >= 87 && value <= 90) {
            return PlantState.GROWING;
        }
        // Limpwurt plant[Inspect,Guide] 92-95
        if (value >= 92 && value <= 95) {
            return PlantState.GROWING;
        }
        // White lily[Inspect,Guide] 101-104
        if (value >= 101 && value <= 104) {
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
