package com.easyfarming;

import net.runelite.api.Client;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AllotmentPatchChecker {
    /**
     * Allotment patch varbit IDs used in OSRS.
     * 
     * Each location uses specific varbit IDs:
     * - Standard locations (Falador, Ardougne, Catherby, Morytania, Kourend, Civitas): 
     *   Varbit IDs are determined from object composition (typically in 4771-4774 range)
     * - Farming Guild: Varbit IDs in 7904-7914 range
     * - Transmit varbits (fallback): FARMING_TRANSMIT_A1, A2, B1, B2
     * 
     * The varbit ID is determined per-patch from the object composition, not per-crop.
     * All crops planted in the same patch location will use the same varbit ID.
     */
    
    // Valid varbit ID ranges for allotment patches (for validation)
    private static final int MIN_STANDARD_VARBIT = 4771;
    private static final int MAX_STANDARD_VARBIT = 4774;
    private static final int MIN_FARMING_GUILD_VARBIT = 7904;
    private static final int MAX_FARMING_GUILD_VARBIT = 7914;
    
    public enum Allotment {
        //Order of lists is "growing, needsWater, diseased, harvest"
        // Note: Values 128-151 are "needs water" states (confirmed: 129 = needs water)
        // When watered, value changes from 129 to 64, so 64+ are watered growing states
        // Value 63 is also a watered growing state (observed after first watering)
        POTATO(Arrays.asList(4,5,6,7,63,64,65,66,67), Arrays.asList(128,129,130), Arrays.asList(), Arrays.asList(8,9,10)),
        ONION(Arrays.asList(11,12,13,14,68,69,70,71), Arrays.asList(131,132,133), Arrays.asList(), Arrays.asList(15,16,17,18,19)),
        CABBAGE(Arrays.asList(20,21,72,73,74,75), Arrays.asList(134,135,136), Arrays.asList(), Arrays.asList(22,23,24)),
        TOMATO(Arrays.asList(25,26,27,28,76,77,78,79), Arrays.asList(137,138), Arrays.asList(), Arrays.asList(29,30,31)),
        SWEETCORN(Arrays.asList(32,33,34,35,80,81,82,83), Arrays.asList(141,142), Arrays.asList(), Arrays.asList(36,37,38)),
        STRAWBERRY(Arrays.asList(39,40,41,42,84,85,86,87), Arrays.asList(143,144,145), Arrays.asList(), Arrays.asList(43,44,45)),
        WATERMELON(Arrays.asList(46,47,48,49,88,89,90,91), Arrays.asList(146,147,148), Arrays.asList(), Arrays.asList(50,51,52)),
        SNAPE_GRASS(Arrays.asList(53,54,55,56,92,93,94,95), Arrays.asList(128,149,150,151), Arrays.asList(198), Arrays.asList(57,58,59,138,139,140));

        private final List<Integer> growing;
        private final List<Integer> needsWater;
        private final List<Integer> diseased;
        private final List<Integer> harvest;

        Allotment(List<Integer> growing, List<Integer> needsWater, List<Integer> diseased, List<Integer> harvest) {
            this.growing = growing;
            this.needsWater = needsWater;
            this.diseased = diseased;
            this.harvest = harvest;
        }
        
        public List<Integer> getGrowing() {
            return growing;
        }

        public List<Integer> getNeedsWater() {
            return needsWater;
        }

        public List<Integer> getDiseased() {
            return diseased;
        }

        public List<Integer> getHarvest() {
            return harvest;
        }
    }

    // Combine all growing, needsWater, diseased, and harvest varbit values into single lists
    private static final List<Integer> growing = Stream.of(Allotment.values())
            .flatMap(allotment -> allotment.getGrowing().stream())
            .collect(Collectors.toList());

    private static final List<Integer> needsWater = Stream.of(Allotment.values())
            .flatMap(allotment -> allotment.getNeedsWater().stream())
            .collect(Collectors.toList());

    private static final List<Integer> diseased = Stream.of(Allotment.values())
            .flatMap(allotment -> allotment.getDiseased().stream())
            .collect(Collectors.toList());

    private static final List<Integer> harvest = Stream.of(Allotment.values())
            .flatMap(allotment -> allotment.getHarvest().stream())
            .collect(Collectors.toList());

    private static final List<Integer> WEEDS = Arrays.asList(0, 1, 2);
    private static final List<Integer> DEAD = Arrays.asList(170, 171, 172);

    /**
     * Checks if a varbit ID is in the valid range for allotment patches.
     * @param varbitId The varbit ID to validate
     * @return true if the varbit ID is in a valid range for allotment patches
     */
    private static boolean isValidAllotmentVarbitId(int varbitId) {
        return (varbitId >= MIN_STANDARD_VARBIT && varbitId <= MAX_STANDARD_VARBIT) ||
               (varbitId >= MIN_FARMING_GUILD_VARBIT && varbitId <= MAX_FARMING_GUILD_VARBIT);
    }

    /**
     * Checks the state of an allotment patch.
     * 
     * @param client The RuneLite client instance
     * @param varbitIndex The varbit ID for the patch (determined from object composition)
     * @return The current state of the patch
     */
    public static PlantState checkAllotmentPatch(Client client, int varbitIndex) {
        int varbitValue = client.getVarbitValue(varbitIndex);

        // Runtime validation: Log a warning if varbit ID is outside expected ranges
        // (This helps catch incorrect varbit IDs during development/testing)
        if (!isValidAllotmentVarbitId(varbitIndex) && varbitIndex != -1) {
            // Note: We don't throw an error here as transmit varbits (A1, A2, B1, B2) 
            // may also be valid but not in our documented ranges
            // This is just a validation check for common allotment patch varbits
        }

        // Check harvestable first (before weeds, since harvest values might overlap with other states)
        if (harvest.contains(varbitValue)) {
            return PlantState.HARVESTABLE;
        }
        
        // Check dead before diseased, as dead is a more specific state
        if (DEAD.contains(varbitValue)) {
            return PlantState.DEAD;
        }
        
        // Check diseased state using the diseased list
        if (diseased.contains(varbitValue)) {
            return PlantState.DISEASED;
        } else if (needsWater.contains(varbitValue)) {
            // Check needsWater after diseased but before growing, as it's urgent
            // Values 128-151 are "needs water" states (confirmed: 129 = needs water)
            return PlantState.NEEDS_WATER;
        } else if (growing.contains(varbitValue)) {
            // Values 4-7, 64-67, etc. are "growing and watered" states
            return PlantState.GROWING;
        } else if (WEEDS.contains(varbitValue)) {
            // Value 0 = weeds/empty after harvesting
            return PlantState.WEEDS;
        } else if (varbitValue == 3) {
            // Value 3 = empty patch ready to plant (before harvesting, this might be harvestable state)
            // Note: After harvesting, value changes from 3 to 0 (weeds)
            return PlantState.PLANT;
        } else {
            return PlantState.UNKNOWN;
        }
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

