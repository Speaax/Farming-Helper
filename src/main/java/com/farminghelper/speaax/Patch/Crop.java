package com.farminghelper.speaax.Patch;

import java.util.stream.IntStream;

public enum Crop
{
    // TODO: Herbs, Flowers, Trees, Hardwood Trees

    // ----- ----- ----- ----- ----- ----- -----
    // Fruit Trees

    APPLE(
        IntStream.rangeClosed(  8, 13), // growing     - tree[Inspect,Guide]
        IntStream.rangeClosed( 14, 14), // harvested   - tree[Inspect,Guide,Chop-down]
        IntStream.rangeClosed( 15, 20), // harvestable - tree[Inspect,Guide,Chop-down,Pick-{fruit}]
        IntStream.rangeClosed( 21, 26), // diseased    - tree[Inspect,Guide,Prune]
        IntStream.rangeClosed( 27, 32), // dead        - tree[Inspect,Guide,Clear]
        IntStream.rangeClosed( 33, 33), // remove      - tree[Inspect,Guide,Clear] (stump)
        IntStream.rangeClosed( 34, 34)  // grown       - tree[Inspect,Guide,Check-health]
    ),
    BANANA(
        IntStream.rangeClosed( 35, 40), // growing     - tree[Inspect,Guide]
        IntStream.rangeClosed( 41, 41), // harvested   - tree[Inspect,Guide,Chop-down]
        IntStream.rangeClosed( 42, 47), // harvestable - tree[Inspect,Guide,Chop-down,Pick-{fruit}]
        IntStream.rangeClosed( 48, 53), // diseased    - tree[Inspect,Guide,Prune]
        IntStream.rangeClosed( 54, 59), // dead        - tree[Inspect,Guide,Clear]
        IntStream.rangeClosed( 60, 60), // remove      - tree[Inspect,Guide,Clear] (stump)
        IntStream.rangeClosed( 61, 61)  // grown       - tree[Inspect,Guide,Check-health]
    ),
    ORANGE(
        IntStream.rangeClosed( 72, 77), // growing     - tree[Inspect,Guide]
        IntStream.rangeClosed( 78, 78), // harvested   - tree[Inspect,Guide,Chop-down]
        IntStream.rangeClosed( 79, 84), // harvestable - tree[Inspect,Guide,Chop-down,Pick-{fruit}]
        IntStream.rangeClosed( 85, 90), // diseased    - tree[Inspect,Guide,Prune]
        IntStream.rangeClosed( 91, 96), // dead        - tree[Inspect,Guide,Clear]
        IntStream.rangeClosed( 97, 97), // remove      - tree[Inspect,Guide,Clear] (stump)
        IntStream.rangeClosed( 98, 98)  // grown       - tree[Inspect,Guide,Check-health]
    ),
    CURRY(
        IntStream.rangeClosed( 99,104), // growing     - tree[Inspect,Guide]
        IntStream.rangeClosed(105,105), // harvested   - tree[Inspect,Guide,Chop-down]
        IntStream.rangeClosed(106,111), // harvestable - tree[Inspect,Guide,Chop-down,Pick-{fruit}]
        IntStream.rangeClosed(112,117), // diseased    - tree[Inspect,Guide,Prune]
        IntStream.rangeClosed(118,123), // dead        - tree[Inspect,Guide,Clear]
        IntStream.rangeClosed(124,124), // remove      - tree[Inspect,Guide,Clear] (stump)
        IntStream.rangeClosed(125,125)  // grown       - tree[Inspect,Guide,Check-health]
    ),
    PINEAPPLE(
        IntStream.rangeClosed(136,141), // growing     - tree[Inspect,Guide]
        IntStream.rangeClosed(142,142), // harvested   - tree[Inspect,Guide,Chop-down]
        IntStream.rangeClosed(143,148), // harvestable - tree[Inspect,Guide,Chop-down,Pick-{fruit}]
        IntStream.rangeClosed(149,154), // diseased    - tree[Inspect,Guide,Prune]
        IntStream.rangeClosed(155,160), // dead        - tree[Inspect,Guide,Clear]
        IntStream.rangeClosed(161,161), // remove      - tree[Inspect,Guide,Clear] (stump)
        IntStream.rangeClosed(162,162)  // grown       - tree[Inspect,Guide,Check-health]
    ),
    PAPAYA(
        IntStream.rangeClosed(163,168), // growing     - tree[Inspect,Guide]
        IntStream.rangeClosed(169,169), // harvested   - tree[Inspect,Guide,Chop-down]
        IntStream.rangeClosed(170,175), // harvestable - tree[Inspect,Guide,Chop-down,Pick-{fruit}]
        IntStream.rangeClosed(176,181), // diseased    - tree[Inspect,Guide,Prune]
        IntStream.rangeClosed(182,187), // dead        - tree[Inspect,Guide,Clear]
        IntStream.rangeClosed(188,188), // remove      - tree[Inspect,Guide,Clear] (stump)
        IntStream.rangeClosed(189,189)  // grown       - tree[Inspect,Guide,Check-health]
    ),
    PALM(
        IntStream.rangeClosed(200,205), // growing     - tree[Inspect,Guide]
        IntStream.rangeClosed(206,206), // harvested   - tree[Inspect,Guide,Chop-down]
        IntStream.rangeClosed(207,212), // harvestable - tree[Inspect,Guide,Chop-down,Pick-{fruit}]
        IntStream.rangeClosed(213,218), // diseased    - tree[Inspect,Guide,Prune]
        IntStream.rangeClosed(219,224), // dead        - tree[Inspect,Guide,Clear]
        IntStream.rangeClosed(225,225), // remove      - tree[Inspect,Guide,Clear] (stump)
        IntStream.rangeClosed(226,226)  // grown       - tree[Inspect,Guide,Check-health]
    ),
    DRAGONFRUIT(
        IntStream.rangeClosed(227,232), // growing     - tree[Inspect,Guide]
        IntStream.rangeClosed(233,233), // harvested   - tree[Inspect,Guide,Chop-down]
        IntStream.rangeClosed(234,239), // harvestable - tree[Inspect,Guide,Chop-down,Pick-{fruit}]
        IntStream.rangeClosed(240,245), // diseased    - tree[Inspect,Guide,Prune]
        IntStream.rangeClosed(246,251), // dead        - tree[Inspect,Guide,Clear]
        IntStream.rangeClosed(252,252), // remove      - tree[Inspect,Guide,Clear] (stump)
        IntStream.rangeClosed(253,253)  // grown       - tree[Inspect,Guide,Check-health]
    );

    public final IntStream weeds = IntStream.rangeClosed(0, 2);

    public final IntStream plant = IntStream.rangeClosed(3, 3);

    public final IntStream growing;

    public final IntStream harvested;

    public final IntStream harvestable;

    public final IntStream diseased;

    public final IntStream dead;

    public final IntStream remove;

    public final IntStream grown;

    Crop(
        IntStream growing,
        IntStream harvested,
        IntStream harvestable,
        IntStream diseased,
        IntStream dead,
        IntStream remove,
        IntStream grown
    ) {
        this.growing = growing;
        this.harvested = harvested;
        this.harvestable = harvestable;
        this.diseased = diseased;
        this.dead = dead;
        this.remove = remove;
        this.grown = grown;
    }
}
