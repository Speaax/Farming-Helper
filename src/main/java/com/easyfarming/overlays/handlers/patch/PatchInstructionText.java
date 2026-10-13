package com.easyfarming.overlays.handlers.patch;

/**
 * Shared instruction strings for patch-step info boxes.
 * Keep herb/hops/flower (and later other types) wording here so handlers do not drift.
 */
public final class PatchInstructionText {

    public static final String USE_COMPOST_ON_PATCH = "Use Compost on patch.";

    private PatchInstructionText() {
    }

    // --- Herb ---

    public static String herbHarvest() {
        return "Harvest Herbs.";
    }

    public static String herbPlantSeed() {
        return "Use Herb seed on patch.";
    }

    public static String herbClearDead() {
        return "Clear the dead herb patch.";
    }

    public static String herbPlantCure() {
        return "Use Plant cure on herb patch. Buy at GE or in farming guild/catherby, and store at Tool Leprechaun for easy access.";
    }

    public static String herbRake() {
        return "Rake the herb patch.";
    }

    public static String herbUnknown() {
        return "UNKNOWN state: Try to do something with the herb patch to change its state.";
    }

    // --- Hops ---

    public static String hopsHarvest() {
        return "Harvest Hops.";
    }

    public static String hopsPlantSeed() {
        return "Use Hops seed on patch.";
    }

    public static String hopsClearDead() {
        return "Clear the dead hops patch.";
    }

    public static String hopsPlantCure() {
        return "Use Plant cure on hops patch.";
    }

    public static String hopsRake() {
        return "Rake the hops patch.";
    }

    public static String hopsUnknown() {
        return "UNKNOWN state: Try to do something with the hops patch to change its state.";
    }

    public static String hopsFlyRenuToAldarin() {
        return "Fly Renu to Aldarin.";
    }

    // --- Flower (limpwurt) ---

    public static String flowerHarvest() {
        return "Harvest Limwurt root.";
    }

    public static String flowerPlantSeed() {
        return "Use Limwurt seed on the patch.";
    }

    public static String flowerClearDead() {
        return "Clear the dead flower patch.";
    }

    public static String flowerPlantCure() {
        return "Cure the diseased Limwurt.";
    }

    public static String flowerRake() {
        return "Rake the flower patch.";
    }

    public static String flowerUnknown() {
        return "UNKNOWN state: Try to do something with the flower patch to change its state.";
    }

    // --- Tree ---

    public static String treeCheckHealth() {
        return "Check tree health.";
    }

    public static String treeRake() {
        return "Rake the tree patch.";
    }

    public static String treeClearDead() {
        return "Clear the dead tree patch.";
    }

    public static String treePlantSapling() {
        return "Use Sapling on the patch.";
    }

    public static String treePrune() {
        return "Prune the tree patch.";
    }

    public static String treeRemove() {
        return "Pay to remove tree, or cut it down and clear the patch.";
    }

    public static String treeUnknown() {
        return "UNKNOWN state: Try to do something with the tree patch to change its state.";
    }

    public static String treePayProtect() {
        return "Pay to protect the patch.";
    }

    public static String treeFlyRenuToAuburnvale() {
        return "Fly Renu to Auburnvale.";
    }

    // --- Fruit tree ---

    public static String fruitTreeCheckHealth() {
        return "Check Fruit tree health.";
    }

    public static String fruitTreePlantSapling() {
        return "Use Sapling on the patch.";
    }

    public static String fruitTreeRake() {
        return "Rake the fruit tree patch.";
    }

    public static String fruitTreeClearDead() {
        return "Clear the dead fruit tree patch.";
    }

    public static String fruitTreePrune() {
        return "Prune the fruit tree patch.";
    }

    public static String fruitTreePayToRemove() {
        return "Pay to remove fruit tree, or cut it down and clear the patch.";
    }

    public static String fruitTreePayToProtect() {
        return "Pay to protect the patch.";
    }

    public static String fruitTreeUnknown() {
        return "UNKNOWN state: Try to do something with the tree patch to change its state.";
    }

    public static String fruitTreeFlyQuetzalToKastori() {
        return "Use the Quetzal Transport System to fly to Kastori.";
    }

    // --- Allotment (north / south) — wording matches FarmingStepHandler allotment steps ---

    public static String allotmentUnknownNorth() {
        return "Allotment patch state unknown - north patch";
    }

    public static String allotmentNavigateNorth() {
        return "Navigate to north patch.";
    }

    public static String allotmentHarvestNorth() {
        return "Harvest Allotment (north patch).";
    }

    public static String allotmentPlantSeedNorth() {
        return "Use Allotment seed on north patch.";
    }

    public static String allotmentClearDeadNorth() {
        return "Clear the dead north patch.";
    }

    public static String allotmentPlantCureNorth() {
        return "Use Plant cure on north patch. Buy at GE or in farming guild/catherby, and store at Tool Leprechaun for easy access.";
    }

    public static String allotmentRakeNorth() {
        return "Rake the north patch.";
    }

    public static String allotmentCompostNorth() {
        return "Use Compost on north patch.";
    }

    public static String allotmentUnknownActionNorth() {
        return "UNKNOWN state: Try to do something with the north allotment patch to change its state.";
    }

    public static String allotmentUnknownSouth() {
        return "Allotment patch state unknown - south patch";
    }

    public static String allotmentNavigateSouth() {
        return "Navigate to south patch.";
    }

    public static String allotmentHarvestSouth() {
        return "Harvest Allotment (south patch).";
    }

    public static String allotmentPlantSeedSouth() {
        return "Use Allotment seed on south patch.";
    }

    public static String allotmentClearDeadSouth() {
        return "Clear the dead south patch.";
    }

    public static String allotmentPlantCureSouth() {
        return "Use Plant cure on south patch. Buy at GE or in farming guild/catherby, and store at Tool Leprechaun for easy access.";
    }

    public static String allotmentRakeSouth() {
        return "Rake the south patch.";
    }

    public static String allotmentCompostSouth() {
        return "Use Compost on south patch.";
    }

    public static String allotmentUnknownActionSouth() {
        return "UNKNOWN state: Try to do something with the south allotment patch to change its state.";
    }

    /** Shared compost instruction used by herb and hops (and other patch types). */
    public static String useCompost() {
        return USE_COMPOST_ON_PATCH;
    }
}
