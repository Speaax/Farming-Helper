package com.easyfarming.overlays.handlers.patch;

/**
 * Immutable hold of the six patch-step instances built by {@link PatchFactory}.
 */
public final class PatchStepBundle {
    private final HerbPatchStep herb;
    private final HopsPatchStep hops;
    private final FlowerPatchStep flower;
    private final AllotmentPatchStep allotment;
    private final TreePatchStep tree;
    private final FruitTreePatchStep fruitTree;

    public PatchStepBundle(
            HerbPatchStep herb,
            HopsPatchStep hops,
            FlowerPatchStep flower,
            AllotmentPatchStep allotment,
            TreePatchStep tree,
            FruitTreePatchStep fruitTree) {
        this.herb = herb;
        this.hops = hops;
        this.flower = flower;
        this.allotment = allotment;
        this.tree = tree;
        this.fruitTree = fruitTree;
    }

    public HerbPatchStep getHerb() {
        return herb;
    }

    public HopsPatchStep getHops() {
        return hops;
    }

    public FlowerPatchStep getFlower() {
        return flower;
    }

    public AllotmentPatchStep getAllotment() {
        return allotment;
    }

    public TreePatchStep getTree() {
        return tree;
    }

    public FruitTreePatchStep getFruitTree() {
        return fruitTree;
    }
}
