package com.easyfarming;

import net.runelite.api.Client;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AllotmentPatchChecker {
    public enum Allotment {
        //Order of lists is "growing, needsWater, diseased, harvest"
        // Note: Values 128-151 are "needs water" states (confirmed: 129 = needs water)
        // When watered, value changes from 129 to 64, so 64+ are watered growing states
        // Value 63 is also a watered growing state (observed after first watering)
        // TODO: Disease detection not yet implemented - diseased lists are empty pending verification of OSRS varbit mappings.
        //       Diseased allotment states require verified varbit IDs from OSRS. Once verified, populate each crop's
        //       diseased list with the appropriate varbit values. Disease detection is intentionally omitted until
        //       accurate mappings can be confirmed through testing or official OSRS documentation.
        POTATO(Arrays.asList(4,5,6,7,63,64,65,66,67), Arrays.asList(128,129,130), Arrays.asList() /* TODO: Add verified diseased varbit IDs */, Arrays.asList(8,9,10)),
        ONION(Arrays.asList(11,12,13,14,68,69,70,71), Arrays.asList(131,132,133), Arrays.asList() /* TODO: Add verified diseased varbit IDs */, Arrays.asList(15,16,17,18,19)),
        CABBAGE(Arrays.asList(20,21,72,73,74,75), Arrays.asList(134,135,136), Arrays.asList() /* TODO: Add verified diseased varbit IDs */, Arrays.asList(22,23,24)),
        TOMATO(Arrays.asList(25,26,27,28,76,77,78,79), Arrays.asList(137,138), Arrays.asList() /* TODO: Add verified diseased varbit IDs */, Arrays.asList(29,30,31)),
        SWEETCORN(Arrays.asList(32,33,34,35,80,81,82,83), Arrays.asList(141,142), Arrays.asList() /* TODO: Add verified diseased varbit IDs */, Arrays.asList(36,37,38)),
        STRAWBERRY(Arrays.asList(39,40,41,42,84,85,86,87), Arrays.asList(143,144,145), Arrays.asList() /* TODO: Add verified diseased varbit IDs */, Arrays.asList(43,44,45)),
        WATERMELON(Arrays.asList(46,47,48,49,88,89,90,91), Arrays.asList(146,147,148), Arrays.asList() /* TODO: Add verified diseased varbit IDs */, Arrays.asList(50,51,52)),
        SNAPE_GRASS(Arrays.asList(53,54,55,56,92,93,94,95), Arrays.asList(149,150,151), Arrays.asList() /* TODO: Add verified diseased varbit IDs */, Arrays.asList(57,58,59,138,139,140));

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

    public static PlantState checkAllotmentPatch(Client client, int varbitIndex) {
        int varbitValue = client.getVarbitValue(varbitIndex);

        // Check harvestable first (before weeds, since harvest values might overlap with other states)
        if (harvest.contains(varbitValue)) {
            return PlantState.HARVESTABLE;
        } else if (diseased.contains(varbitValue)) {
            // Check diseased after harvestable but before needsWater, as diseased patches need immediate attention.
            // NOTE: This check currently never matches because diseased lists are empty (see enum TODO comments).
            //       Once verified varbit IDs are added to the enum entries, this will properly detect diseased states.
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
        } else if (DEAD.contains(varbitValue)) {
            return PlantState.DEAD;
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

