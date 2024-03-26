package com.farminghelper.speaax.Patch;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.runelite.api.Client;

public class PatchState
{
    private static final List<Integer> weeds = Stream.of(Crop.values())
            .flatMap(crop -> crop.weeds.boxed())
            .collect(Collectors.toList());

    private static final List<Integer> plant = Stream.of(Crop.values())
            .flatMap(crop -> crop.plant.boxed())
            .collect(Collectors.toList());

    private static final List<Integer> growing = Stream.of(Crop.values())
            .flatMap(crop -> crop.growing.boxed())
            .collect(Collectors.toList());

    private static final List<Integer> harvested = Stream.of(Crop.values())
            .flatMap(crop -> crop.harvested.boxed())
            .collect(Collectors.toList());

    private static final List<Integer> harvestable = Stream.of(Crop.values())
            .flatMap(crop -> crop.harvestable.boxed())
            .collect(Collectors.toList());

    private static final List<Integer> diseased = Stream.of(Crop.values())
            .flatMap(crop -> crop.diseased.boxed())
            .collect(Collectors.toList());

    private static final List<Integer> dead = Stream.of(Crop.values())
            .flatMap(crop -> crop.dead.boxed())
            .collect(Collectors.toList());

    private static final List<Integer> remove = Stream.of(Crop.values())
            .flatMap(crop -> crop.remove.boxed())
            .collect(Collectors.toList());

    private static final List<Integer> grown = Stream.of(Crop.values())
            .flatMap(crop -> crop.grown.boxed())
            .collect(Collectors.toList());

    public static CropState check(Client client, int varbitIndex) {
        int varbitValue = client.getVarbitValue(varbitIndex);

        if (weeds.contains(varbitValue)) {
            return CropState.WEEDS;
        }
        else if (plant.contains(varbitValue)) {
            return CropState.PLANT;
        }
        else if (growing.contains(varbitValue)) {
            return CropState.GROWING;
        }
        else if (harvested.contains(varbitValue)) {
            return CropState.HARVESTED;
        }
        else if (harvestable.contains(varbitValue)) {
            return CropState.HARVESTABLE;
        }
        else if (diseased.contains(varbitValue)) {
            return CropState.DISEASED;
        }
        else if (dead.contains(varbitValue)) {
            return CropState.DEAD;
        }
        else if (remove.contains(varbitValue)) {
            return CropState.REMOVE;
        }
        else if (grown.contains(varbitValue)) {
            return CropState.GROWN;
        }
        else {
            return CropState.UNKNOWN;
        }
    }
}
