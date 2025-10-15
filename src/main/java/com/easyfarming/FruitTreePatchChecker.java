package com.easyfarming;

import net.runelite.api.Client;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FruitTreePatchChecker {
    public enum FruitTree {
        //Order of lists is "growing, diseased, dead, healthy, remove"
        APPLE(Arrays.asList(8,9,10,11,12,13), Arrays.asList(21,22,23,24,25,26), Arrays.asList(27,28,29,30,31,32), Arrays.asList(34), Arrays.asList(14,15,16,17,18,19,20, 33)),
        BANANA(Arrays.asList(35,36,37,38,39,40), Arrays.asList(48,49,50,51,52,53), Arrays.asList(54,55,56,57,58,59), Arrays.asList(61), Arrays.asList(41,42,43,44,45,46,47, 60)),
        ORANGE(Arrays.asList(72,73,74,75,76,77), Arrays.asList(85,86,87,88,89, 90), Arrays.asList(91,92,93,94,95,96), Arrays.asList(98), Arrays.asList(78,79,80,81,82,83,84, 97)),
        CURRY(Arrays.asList(99,100,101,102,103,104), Arrays.asList(112,113,114,115,116,117), Arrays.asList(118,119,120,121,122,123), Arrays.asList(125), Arrays.asList(105,106,107,108,109,110,111, 124)),
        PINEAPPLE(Arrays.asList(136,137,138,139,140,141), Arrays.asList(149,150,151,152,153,154), Arrays.asList(155,156,157,158,159,160), Arrays.asList(162), Arrays.asList(142,143,144,145,146,147,148, 161)),
        PAPAYA(Arrays.asList(163,164,165,166,167,168), Arrays.asList(176,177,178,179,180,181), Arrays.asList(182,183,184,185,186,187), Arrays.asList(189), Arrays.asList(169,170,171,172,173,174,175, 188)),
        PALM(Arrays.asList(200,201,202,203,204,205), Arrays.asList(213,214,215,216,217,218), Arrays.asList(219,220,221,222,223,224), Arrays.asList(226), Arrays.asList(206,207,208,209,210,211,212, 225)),
        DRAGONFRUIT(Arrays.asList(227,228,229,230,231,232), Arrays.asList(240,241,242,243,244,245), Arrays.asList(246,247,248,249,250,251), Arrays.asList(253), Arrays.asList(233,234,235,236,237,238,239, 252));

        private final List<Integer> growing;
        private final List<Integer> diseased;
        private final List<Integer> dead;
        private final List<Integer> healthy;
        private final List<Integer> remove;

        FruitTree(List<Integer> growing, List<Integer> diseased, List<Integer> dead, List<Integer> healthy, List<Integer> remove) {
            this.growing = growing;
            this.diseased = diseased;
            this.dead = dead;
            this.healthy = healthy;
            this.remove = remove;
        }

        public List<Integer> getGrowing() {
            return growing;
        }

        public List<Integer> getDiseased() {
            return diseased;
        }

        public List<Integer> getDead() {
            return dead;
        }

        public List<Integer> getHealthy() {
            return healthy;
        }
        public List<Integer> getRemove() {
            return remove;
        }
    }

    // Combine all growing and dead varbit values into single lists
    private static final List<Integer> growing = Stream.of(FruitTree.values())
            .flatMap(fruitTree -> fruitTree.getGrowing().stream())
            .collect(Collectors.toList());

    private static final List<Integer> diseased = Stream.of(FruitTree.values())
            .flatMap(fruitTree -> fruitTree.getDiseased().stream())
            .collect(Collectors.toList());

    private static final List<Integer> dead = Stream.of(FruitTree.values())
            .flatMap(fruitTree -> fruitTree.getDead().stream())
            .collect(Collectors.toList());

    private static final List<Integer> healthy = Stream.of(FruitTree.values())
            .flatMap(fruitTree -> fruitTree.getHealthy().stream())
            .collect(Collectors.toList());
    private static final List<Integer> remove = Stream.of(FruitTree.values())
            .flatMap(fruitTree -> fruitTree.getRemove().stream())
            .collect(Collectors.toList());

    private static final List<Integer> weeds = Arrays.asList(0, 1, 2);

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

    public static PlantState checkFruitTreePatch(Client client, int varbitIndex) {
        int varbitValue = client.getVarbitValue(varbitIndex);
        if (growing.contains(varbitValue)) {
            return PlantState.GROWING;
        } else if (diseased.contains(varbitValue)) {
            return PlantState.DISEASED;
        } else if (dead.contains(varbitValue)) {
            return PlantState.DEAD;
        } else if (weeds.contains(varbitValue)) {
            return PlantState.WEEDS;
        }  else if (healthy.contains(varbitValue)) {
            return PlantState.HEALTHY;
        }else if (remove.contains(varbitValue)) {
            return PlantState.REMOVE;
        } else if (varbitValue == 3) {
            return PlantState.PLANT;
        } else {
            return PlantState.UNKNOWN;
        }
    }
}