package com.easyfarming;

import net.runelite.api.Client;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TreePatchChecker {

    public enum Tree {
        //Order of lists is "growing, diseased, dead, healthy, remove"
        OAK(Arrays.asList(8,9,10,11), Arrays.asList(73,74,75), Arrays.asList(137, 138, 139, 141), Arrays.asList(12), Arrays.asList(13,14)),
        WILLOW(Arrays.asList(15,16,17,18,19,20), Arrays.asList(80,81,82,83,84,86), Arrays.asList(144,145,146,147,148, 150), Arrays.asList(21), Arrays.asList(22,23)),
        MAPLE(Arrays.asList(24,25,26,27,28,29,30,31), Arrays.asList(89,90,91,92,93,94,95,97), Arrays.asList(153,154,155,156,157,158,159, 161), Arrays.asList(32), Arrays.asList(33,34)),
        YEW(Arrays.asList(35,36,37,38,39,40,41,42,43,44), Arrays.asList(100,101,102,103,104,105,106,107,108, 110), Arrays.asList(164,165,166,167,168,169,170,171,172, 174), Arrays.asList(45), Arrays.asList(46,47)),
        MAGIC(Arrays.asList(48,49,50,51,52,53,54,55,56,57,58,59), Arrays.asList(113,114,115,116,117,118,119,120,121,122,123, 125), Arrays.asList(177,178,179,180,181,182,183,184,185,186,187, 189), Arrays.asList(60), Arrays.asList(61,62));

        private final List<Integer> growing;
        private final List<Integer> diseased;
        private final List<Integer> dead;
        private final List<Integer> healthy;
        private final List<Integer> remove;

        Tree(List<Integer> growing, List<Integer> diseased, List<Integer> dead, List<Integer> healthy, List<Integer> remove) {
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
    private static final List<Integer> growing = Stream.of(Tree.values())
            .flatMap(tree -> tree.getGrowing().stream())
            .collect(Collectors.toList());

    private static final List<Integer> diseased = Stream.of(Tree.values())
            .flatMap(tree -> tree.getDiseased().stream())
            .collect(Collectors.toList());

    private static final List<Integer> dead = Stream.of(Tree.values())
            .flatMap(tree -> tree.getDead().stream())
            .collect(Collectors.toList());

    private static final List<Integer> healthy = Stream.of(Tree.values())
            .flatMap(tree -> tree.getHealthy().stream())
            .collect(Collectors.toList());
    private static final List<Integer> remove = Stream.of(Tree.values())
            .flatMap(tree -> tree.getRemove().stream())
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

    public static PlantState checkTreePatch(Client client, int varbitIndex) {
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