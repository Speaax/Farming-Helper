package com.easyfarming.customrun;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.ItemsAndLocations.HerbRunItemAndLocation;
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
     * Uses catalog to resolve Location/Teleport, herbRun for compost.
     * Tool inclusion: when includeSecateurs/includeDibber/includeRake are non-null, use them;
     * otherwise fall back to config (generalSecateurs, generalSeedDibber, generalRake).
     */
    public static Map<Integer, Integer> buildRequirements(
            com.easyfarming.customrun.LocationCatalog catalog,
            HerbRunItemAndLocation herbRun,
            EasyFarmingConfig config,
            List<RunLocation> runLocations,
            Boolean includeSecateurs,
            Boolean includeDibber,
            Boolean includeRake) {
        Map<Integer, Integer> allRequirements = new HashMap<>();
        if (catalog == null || herbRun == null || runLocations == null || runLocations.isEmpty()) {
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

        int compostId = herbRun.selectedCompostID() != null ? herbRun.selectedCompostID() : -1;
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

        allRequirements.merge(ItemID.SPADE, 1, Integer::sum);
        boolean needDibber = includeDibber != null ? includeDibber : (config != null && config.generalSeedDibber());
        boolean needRake = includeRake != null ? includeRake : (config != null && config.generalRake());
        boolean needSecateurs = includeSecateurs != null ? includeSecateurs : (config != null && config.generalSecateurs());
        if (needDibber) {
            allRequirements.merge(ItemID.DIBBER, 1, Integer::sum);
        }
        if (needRake) {
            allRequirements.merge(ItemID.RAKE, 1, Integer::sum);
        }
        if (needSecateurs) {
            allRequirements.merge(ItemID.FAIRY_ENCHANTED_SECATEURS, 1, Integer::sum);
        }

        return allRequirements;
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
