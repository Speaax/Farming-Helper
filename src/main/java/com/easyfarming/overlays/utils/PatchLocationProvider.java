package com.easyfarming.overlays.utils;

import net.runelite.api.coords.WorldPoint;

public class PatchLocationProvider {
    
    public static WorldPoint getPatchPoint(String locationName, String patchType) {
        if (locationName == null || patchType == null) return null;
        String lowerPatchType = patchType.toLowerCase();
        
        if (lowerPatchType.contains("herb") || lowerPatchType.contains("flower") || lowerPatchType.contains("allotment")) {
            return getHerbPatchPoint(locationName);
        } else if (lowerPatchType.contains("fruit tree")) {
            return getFruitTreePatchPoint(locationName);
        } else if (lowerPatchType.contains("tree")) {
            return getTreePatchPoint(locationName);
        } else if (lowerPatchType.contains("hops")) {
            return getHopsPatchPoint(locationName);
        }
        
        return null;
    }

    private static WorldPoint getHerbPatchPoint(String locationName) {
        switch (locationName) {
            case "Ardougne": return new WorldPoint(2670, 3374, 0);
            case "Catherby": return new WorldPoint(2813, 3463, 0);
            case "Falador": return new WorldPoint(3058, 3307, 0);
            case "Farming Guild": return new WorldPoint(1238, 3726, 0);
            case "Harmony Island": return new WorldPoint(3789, 2837, 0);
            case "Kourend": return new WorldPoint(1738, 3550, 0);
            case "Morytania": return new WorldPoint(3601, 3525, 0);
            case "Troll Stronghold": return new WorldPoint(2824, 3696, 0);
            case "Weiss": return new WorldPoint(2847, 3931, 0);
            case "Civitas illa Fortis": return new WorldPoint(1586, 3099, 0);
            default: return null;
        }
    }

    private static WorldPoint getTreePatchPoint(String locationName) {
        switch (locationName) {
            case "Falador": return new WorldPoint(3000, 3373, 0);
            case "Farming Guild": return new WorldPoint(1232, 3736, 0);
            case "Gnome Stronghold": return new WorldPoint(2436, 3415, 0);
            case "Lumbridge": return new WorldPoint(3193, 3231, 0);
            case "Taverley": return new WorldPoint(2936, 3438, 0);
            case "Varrock": return new WorldPoint(3229, 3459, 0);
            default: return null;
        }
    }

    private static WorldPoint getFruitTreePatchPoint(String locationName) {
        switch (locationName) {
            case "Brimhaven": return new WorldPoint(2764, 3212, 0);
            case "Catherby": return new WorldPoint(2860, 3433, 0);
            case "Farming Guild": return new WorldPoint(1243, 3759, 0);
            case "Gnome Stronghold": return new WorldPoint(2475, 3446, 0);
            case "Lletya": return new WorldPoint(2346, 3162, 0);
            case "Tree Gnome Village": return new WorldPoint(2490, 3180, 0);
            default: return null;
        }
    }

    private static WorldPoint getHopsPatchPoint(String locationName) {
        switch (locationName) {
            case "Aldarin": return new WorldPoint(1365, 2939, 0);
            case "Entrana": return new WorldPoint(2811, 3337, 0);
            case "Lumbridge": return new WorldPoint(3229, 3315, 0);
            case "Seers Village": return new WorldPoint(2667, 3526, 0);
            case "Yanille": return new WorldPoint(2576, 3105, 0);
            default: return null;
        }
    }
}
