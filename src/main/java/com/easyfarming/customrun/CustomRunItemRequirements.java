package com.easyfarming.customrun;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.core.Location;
import com.easyfarming.core.Teleport;
import com.easyfarming.utils.Constants;
import com.easyfarming.utils.TeleportItemRequirements;
import net.runelite.api.Client;
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
            Client client,
            List<RunLocation> runLocations,
            boolean includeSecateurs,
            boolean includeDibber,
            boolean includeRake) {
        Map<Integer, Integer> allRequirements = new HashMap<>();
        if (runLocations == null || runLocations.isEmpty()) {
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
            if (name == null || patchTypes == null || patchTypes.isEmpty()) {
                continue;
            }

            // Always count patch supplies from the run definition. Do not gate this on catalog
            // lookup — a missed Location used to `continue` and drop all seeds/compost for the
            // location, so the checklist only required a spade and immediately "completed".
            for (String patchType : patchTypes) {
                if (PatchTypes.HERB.equals(patchType)) {
                    herbPatchCount++;
                    compostPatchesTotal++;
                } else if (PatchTypes.FLOWER.equals(patchType)) {
                    flowerPatchCount++;
                    compostPatchesTotal++;
                } else if (PatchTypes.ALLOTMENT.equals(patchType)) {
                    List<Integer> patchIds = Constants.ALLOTMENT_PATCH_IDS_BY_LOCATION.get(name);
                    int n = (patchIds != null && !patchIds.isEmpty()) ? patchIds.size() : 2;
                    allotmentPatchCount += n;
                    compostPatchesTotal += n;
                } else if (PatchTypes.TREE.equals(patchType)) {
                    treePatchCount++;
                    compostPatchesTotal++;
                } else if (PatchTypes.FRUIT_TREE.equals(patchType)) {
                    fruitTreePatchCount++;
                    compostPatchesTotal++;
                } else if (PatchTypes.HOPS.equals(patchType)) {
                    hopsPatchCount++;
                    compostPatchesTotal++;
                }
            }

            // Teleport items: resolve Location from any enabled patch type at this name.
            if (catalog != null) {
                Location loc = null;
                for (String patchType : patchTypes) {
                    loc = catalog.getLocationForPatch(name, patchType);
                    if (loc != null) {
                        break;
                    }
                }
                String teleportOption = rl.getTeleportOption();
                if (loc != null && teleportOption != null) {
                    Teleport teleport = null;
                    for (Teleport t : loc.getTeleportOptions()) {
                        if (t != null && t.getEnumOption() != null
                                && teleportOption.equalsIgnoreCase(t.getEnumOption())) {
                            teleport = t;
                            break;
                        }
                    }
                    if (teleport != null) {
                        mergeTeleportRequirements(allRequirements, teleport.getItemRequirements());
                    }
                }
            }
        }

        boolean payForProtection = config != null && config.generalPayForProtection();
        if (!payForProtection) {
            Integer selectedCompost = selectedCompostId(config);
            int compostId = selectedCompost != null ? selectedCompost : -1;
            if (compostId != -1 && compostId != 0 && compostPatchesTotal > 0) {
                if (compostId == ItemID.BOTTOMLESS_COMPOST_BUCKET) {
                    allRequirements.merge(ItemID.BOTTOMLESS_COMPOST_BUCKET, 1, Integer::sum);
                } else {
                    allRequirements.merge(compostId, compostPatchesTotal, Integer::sum);
                }
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
            // All hops-patch crops use 3 or 4 seeds in-game; we use 4 everywhere to err on the side of caution.
            int seedsPerHopsPatch = 4;
            allRequirements.merge(ItemID.BARLEY_SEED, hopsPatchCount * seedsPerHopsPatch, Integer::sum);
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

        // Teleport objects snapshot fairy ring staff requirements when the catalog is built; re-check live
        if (!TeleportItemRequirements.needsDramenStaffForFairyRings(client)) {
            allRequirements.remove(ItemID.DRAMEN_STAFF);
        }

        return allRequirements;
    }

    /**
     * Returns the selected compost item ID from config.
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
            } else if (itemId == ItemID.SKILLCAPE_FARMING || itemId == ItemID.SKILLCAPE_FARMING_TRIMMED || itemId == ItemID.SKILLCAPE_MAX){
                into.merge(itemId, quantity, (a, b) -> Math.min(1, a + b));
            } else {
                into.merge(itemId, quantity, Integer::sum);
            }
        }
    }
}
