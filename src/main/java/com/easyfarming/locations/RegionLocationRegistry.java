package com.easyfarming.locations;

import com.easyfarming.utils.Constants;
import net.runelite.api.coords.WorldPoint;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Central maps from region ID to farming location display names.
 * Used by {@link com.easyfarming.overlays.handlers.FarmingStepHandler} for patch-run steps.
 */
public final class RegionLocationRegistry {

    private static final WorldPoint TREE_GNOME_VILLAGE_POINT = new WorldPoint(2490, 3180, 0);
    private static final WorldPoint GNOME_STRONGHOLD_FRUIT_TREE_POINT = new WorldPoint(2436, 3415, 0);

    private static final Map<Integer, String> HERB_ALLOTMENT_FLOWER_BY_REGION;
    private static final Map<Integer, String> FRUIT_TREE_BY_REGION;
    private static final Map<Integer, String> TREE_BY_REGION;
    private static final Map<Integer, String> HOPS_BY_REGION;

    static {
        Map<Integer, String> herb = new HashMap<>();
        herb.put(Constants.REGION_ARDOUGNE, "Ardougne");
        herb.put(Constants.REGION_ARDOUGNE_ALT, "Ardougne");
        herb.put(Constants.REGION_CATHERBY, "Catherby");
        herb.put(Constants.REGION_FALADOR, "Falador");
        herb.put(Constants.REGION_FARMING_GUILD, "Farming Guild");
        herb.put(Constants.REGION_KOUREND, "Kourend");
        herb.put(Constants.REGION_MORYTANIA, "Morytania");
        herb.put(Constants.REGION_CIVITAS, "Civitas illa Fortis");
        herb.put(Constants.REGION_HARMONY, "Harmony Island");
        herb.put(Constants.REGION_TROLL_STRONGHOLD, "Troll Stronghold");
        herb.put(Constants.REGION_WEISS, "Weiss");
        herb.put(Constants.REGION_GNOME_STRONGHOLD, "Gnome Stronghold");
        herb.put(Constants.REGION_GNOME_STRONGHOLD_ALT, "Gnome Stronghold");
        HERB_ALLOTMENT_FLOWER_BY_REGION = Collections.unmodifiableMap(herb);

        Map<Integer, String> fruit = new HashMap<>();
        fruit.put(Constants.REGION_ARDOUGNE, "Brimhaven");
        fruit.put(Constants.REGION_ARDOUGNE_ALT, "Brimhaven");
        fruit.put(Constants.REGION_CATHERBY, "Catherby");
        fruit.put(Constants.REGION_FARMING_GUILD, "Farming Guild");
        fruit.put(Constants.REGION_LLETYA, "Lletya");
        fruit.put(Constants.REGION_TREE_GNOME_VILLAGE, "Tree Gnome Village");
        fruit.put(Constants.REGION_KASTORI, "Kastori");
        fruit.put(Constants.REGION_KASTORI_ALT1, "Kastori");
        fruit.put(Constants.REGION_KASTORI_ALT2, "Kastori");
        FRUIT_TREE_BY_REGION = Collections.unmodifiableMap(fruit);

        Map<Integer, String> tree = new HashMap<>();
        tree.put(Constants.REGION_FALADOR, "Falador");
        tree.put(Constants.REGION_FALADOR_PARK, "Falador");
        tree.put(Constants.REGION_FALADOR_PARK_ALT, "Falador");
        tree.put(Constants.REGION_FARMING_GUILD, "Farming Guild");
        tree.put(Constants.REGION_GNOME_STRONGHOLD, "Gnome Stronghold");
        tree.put(Constants.REGION_GNOME_STRONGHOLD_ALT, "Gnome Stronghold");
        tree.put(Constants.REGION_LUMBRIDGE, "Lumbridge");
        tree.put(Constants.REGION_VARROCK, "Varrock");
        tree.put(Constants.REGION_TAVERLEY, "Taverley");
        tree.put(Constants.REGION_TAVERLEY_ALT, "Taverley");
        tree.put(Constants.REGION_AUBURNVALE, "Nemus Retreat");
        tree.put(Constants.REGION_AUBURNVALE_ALT1, "Nemus Retreat");
        tree.put(Constants.REGION_AUBURNVALE_ALT2, "Nemus Retreat");
        TREE_BY_REGION = Collections.unmodifiableMap(tree);

        Map<Integer, String> hops = new HashMap<>();
        hops.put(Constants.REGION_LUMBRIDGE_HOPS, "Lumbridge");
        hops.put(Constants.REGION_SEERS_VILLAGE, "Seers Village");
        hops.put(Constants.REGION_YANILLE, "Yanille");
        hops.put(Constants.REGION_ENTRANA, "Entrana");
        hops.put(Constants.REGION_ALDARIN, "Aldarin");
        HOPS_BY_REGION = Collections.unmodifiableMap(hops);
    }

    private RegionLocationRegistry() {
    }

    /**
     * Herb / allotment / flower run location name for a region.
     * @return location name, or {@code "Unknown"} if unmapped
     */
    public static String getHerbLocationName(int regionId) {
        return HERB_ALLOTMENT_FLOWER_BY_REGION.getOrDefault(regionId, "Unknown");
    }

    /**
     * Fruit tree location name for a region.
     * Gnome Stronghold region IDs are shared with Tree Gnome Village; when
     * {@code playerLocation} is non-null, the closer patch point wins.
     * @return location name, or {@code null} if unmapped
     */
    public static String getFruitTreeLocationName(int regionId, WorldPoint playerLocation) {
        if (regionId == Constants.REGION_GNOME_STRONGHOLD || regionId == Constants.REGION_GNOME_STRONGHOLD_ALT) {
            if (playerLocation != null
                    && playerLocation.distanceTo(TREE_GNOME_VILLAGE_POINT)
                    < playerLocation.distanceTo(GNOME_STRONGHOLD_FRUIT_TREE_POINT)) {
                return "Tree Gnome Village";
            }
            return "Gnome Stronghold";
        }
        return FRUIT_TREE_BY_REGION.get(regionId);
    }

    /**
     * Fruit tree location without player-position disambiguation.
     * Gnome Stronghold regions resolve to {@code "Gnome Stronghold"}.
     */
    public static String getFruitTreeLocationName(int regionId) {
        return getFruitTreeLocationName(regionId, null);
    }

    /**
     * Tree-run location name for a region.
     * @return location name, or {@code null} if unmapped
     */
    public static String getTreeLocationNameFromRegionId(int regionId) {
        return TREE_BY_REGION.get(regionId);
    }

    /**
     * Hops-run location name for a region.
     * @return location name, or {@code "Unknown"} if unmapped
     */
    public static String getHopsLocationName(int regionId) {
        return HOPS_BY_REGION.getOrDefault(regionId, "Unknown");
    }
}
