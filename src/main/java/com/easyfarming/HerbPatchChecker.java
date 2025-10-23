package com.easyfarming;

import net.runelite.api.Client;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HerbPatchChecker {
    public enum Herb {
        //Order of lists is "growing, diseased, harvest"
        GUAM(Arrays.asList(4,5,6,7), Arrays.asList(128,129,130), Arrays.asList(8,9,10)),
        MARRENTILL(Arrays.asList(11,12,13,14), Arrays.asList(131,132,133), Arrays.asList(15,16,17)),
        TARROMIN(Arrays.asList(18,19,20,21), Arrays.asList(134,135,136), Arrays.asList(22,23,24)),
        HARRALANDER(Arrays.asList(25,26,27,28), Arrays.asList(137,138,139), Arrays.asList(29,30,31)),
        RANARR(Arrays.asList(32,33,34,35), Arrays.asList(140,141,142), Arrays.asList(36,37,38)),
        TOADFLAX(Arrays.asList(39,40,41,42), Arrays.asList(143,144,145), Arrays.asList(43,44,45)),
        IRIT(Arrays.asList(46,47,48,49), Arrays.asList(146,147,148), Arrays.asList(50,51,52)),
        AVANTOE(Arrays.asList(53,54,55,56), Arrays.asList(149,159,151), Arrays.asList(57,58,59)),
        KWUARM(Arrays.asList(68,69,70,71), Arrays.asList(152,153,154), Arrays.asList(72,73,74)),
        SNAPDRAGON(Arrays.asList(75,76,77,78), Arrays.asList(155,156,157), Arrays.asList(79,80,81)),
        CADANTINE(Arrays.asList(82,83,84,85), Arrays.asList(158,159,160), Arrays.asList(86,87,88)),
        LANTADYME(Arrays.asList(89,90,91,92), Arrays.asList(161,162,163), Arrays.asList(93,94,95)),
        DWARF_WEED(Arrays.asList(96,97,98,99), Arrays.asList(164,165,166), Arrays.asList(100,101,102)),
        TORSTOL(Arrays.asList(103,104,105,106), Arrays.asList(167,168,169), Arrays.asList(107,108,109));

        private final List<Integer> growing;
        private final List<Integer> diseased;
        private final List<Integer> harvest;

        Herb(List<Integer> growing, List<Integer> diseased, List<Integer> harvest) {
            this.growing = growing;
            this.diseased = diseased;
            this.harvest = harvest;
        }
        public List<Integer> getGrowing() {
            return growing;
        }

        public List<Integer> getDead() {
            return diseased;
        }

        public List<Integer> getHarvest() {
            return harvest;
        }
    }

    // Combine all growing and dead varbit values into single lists
    private static final List<Integer> growing = Stream.of(Herb.values())
            .flatMap(herb -> herb.getGrowing().stream())
            .collect(Collectors.toList());

    private static final List<Integer> diseased = Stream.of(Herb.values())
            .flatMap(herb -> herb.getDead().stream())
            .collect(Collectors.toList());

    private static final List<Integer> harvest = Stream.of(Herb.values())
            .flatMap(herb -> herb.getHarvest().stream())
            .collect(Collectors.toList());

    private static final List<Integer> WEEDS = Arrays.asList(0, 1, 2);
    private static final List<Integer> DEAD = Arrays.asList(170, 171, 172);

    public static PlantState checkHerbPatch(Client client, int varbitIndex) {
        int varbitValue = client.getVarbitValue(varbitIndex);

        if (growing.contains(varbitValue)) {
            return PlantState.GROWING;
        } else if (diseased.contains(varbitValue)) {
            return PlantState.DISEASED;
        } else if (harvest.contains(varbitValue)) {
            return PlantState.HARVESTABLE;
        } else if (WEEDS.contains(varbitValue)) {
            return PlantState.WEEDS;
        } else if (DEAD.contains(varbitValue)) {
            return PlantState.DEAD;
        } else if (varbitValue == 3) {
            return PlantState.PLANT;
        } else {
            return PlantState.UNKNOWN;
        }
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
