package com.farminghelper.speaax;

import net.runelite.api.Client;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HardwoodPatchChecker
{

    public enum Hardwood
    {
        //Order of lists is "growing, diseased, dead, healthy, remove"
        TEAK(Arrays.asList(8,9,10,11,12,13,14), Arrays.asList(18,19,20,21,22,23), Arrays.asList(24,25,26,27,28,29), Arrays.asList(15), Arrays.asList(16, 17)),
        MAHOGANY(Arrays.asList(30,31,32,33,34,35,36,37), Arrays.asList(41,42,43,44,45,46,47), Arrays.asList(48,49,50,51,52,53,54), Arrays.asList(38), Arrays.asList(39,40));
        // TODO: Add TEAK
        // TODO: Add MAHOGANY

        private final List<Integer> growing;
        private final List<Integer> diseased;
        private final List<Integer> dead;
        private final List<Integer> healthy;
        private final List<Integer> remove;

        Hardwood(List<Integer> growing, List<Integer> diseased, List<Integer> dead, List<Integer> healthy, List<Integer> remove) {
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
    private static final List<Integer> growing = Stream.of(Hardwood.values())
            .flatMap(hardwood -> hardwood.getGrowing().stream())
            .collect(Collectors.toList());

    private static final List<Integer> diseased = Stream.of(Hardwood.values())
            .flatMap(hardwood -> hardwood.getDiseased().stream())
            .collect(Collectors.toList());

    private static final List<Integer> dead = Stream.of(Hardwood.values())
            .flatMap(hardwood -> hardwood.getDead().stream())
            .collect(Collectors.toList());

    private static final List<Integer> healthy = Stream.of(Hardwood.values())
            .flatMap(hardwood -> hardwood.getHealthy().stream())
            .collect(Collectors.toList());
    private static final List<Integer> remove = Stream.of(Hardwood.values())
            .flatMap(hardwood -> hardwood.getRemove().stream())
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

    public static PlantState checkHardwoodPatch(Client client, int varbitIndex) {
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