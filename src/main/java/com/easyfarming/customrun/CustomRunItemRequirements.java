package com.easyfarming.customrun;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.core.Location;
import com.easyfarming.core.Teleport;
import com.easyfarming.utils.Constants;
import net.runelite.api.gameval.ItemID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds the item requirement map for a custom run from its locations and teleport choices,
 * so the overlay can block "proceed to patches" until the player has the required items.
 */
public final class CustomRunItemRequirements {

    private CustomRunItemRequirements() {}

    /**
     * Builds a single requirement map for the given custom run locations.
     * Uses catalog to resolve Location/Teleport; compost from config via selectedCompostId.
     * Tool inclusion: use only the provided flags (from run config or saved state); no fallbacks.
     */
    public static Map<Integer, Integer> buildRequirements(
            com.easyfarming.customrun.LocationCatalog catalog,
            EasyFarmingConfig config,
            List<RunLocation> runLocations,
            boolean includeSecateurs,
            boolean includeDibber,
            boolean includeRake) {
        Map<Integer, Integer> allRequirements = new HashMap<>();
        if (catalog == null || runLocations == null || runLocations.isEmpty()) {
            return allRequirements;
        }

        int herbPatchCount = 0;
        int flowerPatchCount = 0;
        int allotmentPatchCount = 0;
        int treePatchCount = 0;
        int fruitTreePatchCount = 0;
        int hopsPatchCount = 0;
        int compostPatchesTotal = 0;

        for (RunLocation rl : runLocations) {
            String name = rl.getLocationName();
            List<String> patchTypes = rl.getPatchTypes();
            if (name == null || patchTypes == null || patchTypes.isEmpty()) continue;

            Location loc = catalog.getLocationForPatch(name, patchTypes.get(0));
            if (loc == null) continue;
            String teleportOption = rl.getTeleportOption();
            if (teleportOption == null) continue;
            Teleport teleport = null;
            for (Teleport t : loc.getTeleportOptions()) {
                if (t != null && t.getEnumOption() != null && teleportOption.equalsIgnoreCase(t.getEnumOption())) {
                    teleport = t;
                    break;
                }
            }
            if (teleport == null) continue;

            Map<Integer, Integer> req = teleport.getItemRequirements();
            mergeTeleportRequirements(allRequirements, req);

            for (String patchType : patchTypes) {
                if (PatchTypes.HERB.equals(patchType)) {
                    herbPatchCount++;
                    compostPatchesTotal++;
                }
                if (PatchTypes.FLOWER.equals(patchType)) {
                    flowerPatchCount++;
                    compostPatchesTotal++;
                }
                if (PatchTypes.ALLOTMENT.equals(patchType)) {
                    List<Integer> patchIds = Constants.ALLOTMENT_PATCH_IDS_BY_LOCATION.get(name);
                    int n = (patchIds != null && !patchIds.isEmpty()) ? patchIds.size() : 2;
                    allotmentPatchCount += n;
                    compostPatchesTotal += n;
                }
                if (PatchTypes.TREE.equals(patchType)) {
                    treePatchCount++;
                    compostPatchesTotal++;
                }
                if (PatchTypes.FRUIT_TREE.equals(patchType)) {
                    fruitTreePatchCount++;
                    compostPatchesTotal++;
                }
                if (PatchTypes.HOPS.equals(patchType)) {
                    hopsPatchCount++;
                    compostPatchesTotal++;
                }
            }
        }

        Integer selectedCompost = selectedCompostId(config);
        int compostId = selectedCompost != null ? selectedCompost : -1;
        if (compostId != -1 && compostId != 0) {
            if (compostId == ItemID.BOTTOMLESS_COMPOST_BUCKET) {
                allRequirements.merge(ItemID.BOTTOMLESS_COMPOST_BUCKET, 1, Integer::sum);
            } else {
                allRequirements.merge(compostId, compostPatchesTotal, Integer::sum);
            }
        }

        if (herbPatchCount > 0) {
            allRequirements.merge(ItemID.GUAM_SEED, herbPatchCount, Integer::sum);
        }
        if (flowerPatchCount > 0) {
            allRequirements.merge(ItemID.LIMPWURT_SEED, flowerPatchCount, Integer::sum);
        }
        if (allotmentPatchCount > 0) {
            int seedsPerPatch = 3;
            allRequirements.merge(Constants.BASE_ALLOTMENT_SEED_ID, allotmentPatchCount * seedsPerPatch, Integer::sum);
        }
        if (treePatchCount > 0) {
            allRequirements.merge(Constants.BASE_TREE_SAPLING_ID, treePatchCount, Integer::sum);
        }
        if (fruitTreePatchCount > 0) {
            allRequirements.merge(Constants.BASE_FRUIT_TREE_SAPLING_ID, fruitTreePatchCount, Integer::sum);
        }
        if (hopsPatchCount > 0) {
            allRequirements.merge(ItemID.BARLEY_SEED, hopsPatchCount, Integer::sum);
        }

        // Tree/fruit tree runs instruct paying farmer to chop down after check-health (200gp per patch)
        int treeAndFruitCount = treePatchCount + fruitTreePatchCount;
        if (treeAndFruitCount > 0) {
            allRequirements.merge(ItemID.COINS, 200 * treeAndFruitCount, Integer::sum);
        }

        allRequirements.merge(ItemID.SPADE, 1, Integer::sum);
        if (includeDibber) {
            allRequirements.merge(ItemID.DIBBER, 1, Integer::sum);
        }
        if (includeRake) {
            allRequirements.merge(ItemID.RAKE, 1, Integer::sum);
        }
        if (includeSecateurs) {
            allRequirements.merge(ItemID.FAIRY_ENCHANTED_SECATEURS, 1, Integer::sum);
        }

        return allRequirements;
    }

    /**
     * Returns the selected compost item ID from config. Logic matches ItemAndLocation.selectedCompostID().
     */
    private static Integer selectedCompostId(EasyFarmingConfig config) {
        if (config == null) {
            return null;
        }
        EasyFarmingConfig.OptionEnumCompost selectedCompost = config.enumConfigCompost();
        switch (selectedCompost) {
            case Compost:
                return ItemID.BUCKET_COMPOST;
            case Supercompost:
                return ItemID.BUCKET_SUPERCOMPOST;
            case Ultracompost:
                return ItemID.BUCKET_ULTRACOMPOST;
            case Bottomless:
                return ItemID.BOTTOMLESS_COMPOST_BUCKET;
            default:
                return 0;
        }
    }

    private static void mergeTeleportRequirements(Map<Integer, Integer> into, Map<Integer, Integer> from) {
        if (from == null) return;
        for (Map.Entry<Integer, Integer> entry : from.entrySet()) {
            int itemId = entry.getKey();
            int quantity = entry.getValue();
            if (itemId == ItemID.SKILLCAPE_CONSTRUCTION || itemId == ItemID.SKILLCAPE_CONSTRUCTION_TRIMMED || itemId == ItemID.SKILLCAPE_MAX) {
                into.merge(itemId, quantity, (a, b) -> Math.min(1, a + b));
            } else if (itemId == ItemID.HG_QUETZALWHISTLE_BASIC || itemId == ItemID.HG_QUETZALWHISTLE_ENHANCED || itemId == ItemID.HG_QUETZALWHISTLE_PERFECTED) {
                into.merge(ItemID.HG_QUETZALWHISTLE_BASIC, quantity, (a, b) -> Math.min(1, a + b));
            } else if (itemId == ItemID.SKILLCAPE_HUNTING || itemId == ItemID.SKILLCAPE_HUNTING_TRIMMED) {
                into.merge(ItemID.SKILLCAPE_HUNTING, quantity, (a, b) -> Math.min(1, a + b));
            } else if (itemId == ItemID.LUMBRIDGE_RING_MEDIUM || itemId == ItemID.LUMBRIDGE_RING_HARD || itemId == ItemID.LUMBRIDGE_RING_ELITE) {
                into.merge(ItemID.LUMBRIDGE_RING_MEDIUM, quantity, (a, b) -> Math.min(1, a + b));
            } else if (itemId == ItemID.ARDY_CAPE_MEDIUM || itemId == ItemID.ARDY_CAPE_HARD || itemId == ItemID.ARDY_CAPE_ELITE) {
                into.merge(ItemID.ARDY_CAPE_MEDIUM, quantity, (a, b) -> Math.min(1, a + b));
            } else if (itemId == ItemID.DRAMEN_STAFF) {
                into.merge(ItemID.DRAMEN_STAFF, quantity, (a, b) -> Math.min(1, a + b));
            } else {
                into.merge(itemId, quantity, Integer::sum);
            }
        }
    }
}
