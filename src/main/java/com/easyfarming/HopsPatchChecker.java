package com.easyfarming;

import net.runelite.api.Client;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HopsPatchChecker {
    /**
     * Hops patch varbit values for different crop types and states.
     * 
     * Varbit ID: 4771 (FARMING_TRANSMIT_A) - used for all hops patches
     */
    public enum Hops {
        //Order of lists is "growing, needsWater, diseased, dead, harvest"
        // Hops have varying amounts of growth stages.
        BARLEY( // Varbit values confirmed
            Arrays.asList(202,203,204,205,206), // growing
            Arrays.asList(74,75,76,77), // needs water
            Arrays.asList(81), // diseased
            Arrays.asList(209), // dead
            Arrays.asList(78,79,80) // harvest
        ),

        HAMMERSTONE( // Varbit values confirmed
            Arrays.asList(132,133,134,135), 
            Arrays.asList(4,5,6,7), 
            Arrays.asList(11),
            Arrays.asList(139),
            Arrays.asList(8, 9, 10)
        ),

        ASGARNIAN( // Varbit values confirmed
            Arrays.asList(142,143,144,145,146),
            Arrays.asList(14,15,16,17,18), 
            Arrays.asList(22),
            Arrays.asList(149),
            Arrays.asList(19,20,21)
        ),

        JUTE( // Varbit values confirmed
            Arrays.asList(212,213,214,215,216), 
            Arrays.asList(84, 85, 86, 87, 88), 
            Arrays.asList(92,93),
            Arrays.asList(219),
            Arrays.asList(89, 90, 91)
        ),

        YANILLIAN(
            Arrays.asList(154,155,156,157,158,159), 
            Arrays.asList(26,27,28,29,30,31), 
            Arrays.asList(163,164,165),
            Arrays.asList(),
            Arrays.asList(32,33,34)
        ),

        KRANDORIAN(
            Arrays.asList(168,169,170,171,172,173,174), 
            Arrays.asList(40,41,42,43,44,45,46), 
            Arrays.asList(50,51,52,53,54,55,56),
            Arrays.asList(183),
            Arrays.asList(47,48,49)
        ),

        WILDBLOOD(
            Arrays.asList(184,185,186,187,188,189,190,191), 
            Arrays.asList(56,57,58,59,60,61,62,63), 
            Arrays.asList(),
            Arrays.asList(200),
            Arrays.asList(64,65,66)
        ),

        HEMP(
            Arrays.asList(232,233,234,235), 
            Arrays.asList(104,105,106,107), 
            Arrays.asList(),
            Arrays.asList(),
            Arrays.asList(108,109,110)
        ),

        FLAX(
            Arrays.asList(225,226,227), 
            Arrays.asList(97, 98), 
            Arrays.asList(),
            Arrays.asList(),
            Arrays.asList(99,100,101)
        ),

        COTTON(
            Arrays.asList(242,243,244,245,246), 
            Arrays.asList(114,115,116,117,118), 
            Arrays.asList(),
            Arrays.asList(),
            Arrays.asList(247,248,249,250,251)
        );

        private final List<Integer> growing;
        private final List<Integer> needsWater;
        private final List<Integer> diseased;
        private final List<Integer> dead;
        private final List<Integer> harvest;

        Hops(List<Integer> growing, List<Integer> needsWater, List<Integer> diseased, List<Integer> dead, List<Integer> harvest) {
            this.growing = growing;
            this.needsWater = needsWater;
            this.diseased = diseased;
            this.dead = dead;
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

        public List<Integer> getDead() {
            return dead;
        }

        public List<Integer> getHarvest() {
            return harvest;
        }
    }

    // Combine all growing, needsWater, diseased, dead, and harvest varbit values into single lists
    private static final List<Integer> growing = Stream.of(Hops.values())
            .flatMap(hops -> hops.getGrowing().stream())
            .collect(Collectors.toList());

    private static final List<Integer> needsWater = Stream.of(Hops.values())
            .flatMap(hops -> hops.getNeedsWater().stream())
            .collect(Collectors.toList());

    private static final List<Integer> diseased = Stream.of(Hops.values())
            .flatMap(hops -> hops.getDiseased().stream())
            .collect(Collectors.toList());

    private static final List<Integer> dead = Stream.of(Hops.values())
            .flatMap(hops -> hops.getDead().stream())
            .collect(Collectors.toList());

    private static final List<Integer> harvest = Stream.of(Hops.values())
            .flatMap(hops -> hops.getHarvest().stream())
            .collect(Collectors.toList());

    private static final List<Integer> WEEDS = Arrays.asList(0, 1, 2);

    public static PlantState checkHopsPatch(Client client, int varbitIndex) {
        int varbitValue = client.getVarbitValue(varbitIndex);

        // Check harvestable first (before weeds, since harvest values might overlap with other states)
        if (harvest.contains(varbitValue)) {
            return PlantState.HARVESTABLE;
        }
        
        // Check dead before diseased, as dead is a more specific state
        if (dead.contains(varbitValue)) {
            return PlantState.DEAD;
        }
        
        // Check diseased state
        if (diseased.contains(varbitValue)) {
            return PlantState.DISEASED;
        } else if (needsWater.contains(varbitValue)) {
            // Check needsWater after diseased but before growing, as it's urgent
            return PlantState.NEEDS_WATER;
        } else if (growing.contains(varbitValue)) {
            return PlantState.GROWING;
        } else if (WEEDS.contains(varbitValue)) {
            return PlantState.WEEDS;
        } else if (varbitValue == 3) {
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

