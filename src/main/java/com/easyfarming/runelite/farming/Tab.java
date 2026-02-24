package com.easyfarming.runelite.farming;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;

@RequiredArgsConstructor
@Getter
public enum Tab {
    OVERVIEW("Overview", ItemID.OLD_NOTES),
    CLOCK("Timers & Stopwatches", ItemID.WATCH),
    BIRD_HOUSE("Bird Houses", ItemID.OAK_BIRD_HOUSE),
    ALLOTMENT("Allotment Patches", ItemID.CABBAGE),
    FLOWER("Flower Patches", ItemID.RED_FLOWERS),
    HERB("Herb Patches", ItemID.GRIMY_RANARR_WEED),
    TREE("Tree Patches", ItemID.YEW_LOGS),
    FRUIT_TREE("Fruit Tree Patches", ItemID.PINEAPPLE),
    HOPS("Hops Patches", ItemID.BARLEY),
    BUSH("Bush Patches", ItemID.POISON_IVY_BERRIES),
    GRAPE("Grape Patches", ItemID.GRAPES),
    SPECIAL("Special Patches", ItemID.MUSHROOM),
    MUSHROOM("Mushroom Patch", ItemID.MUSHROOM),
    BELLADONNA("Belladonna Patch", ItemID.CAVE_NIGHTSHADE),
    BIG_COMPOST("Giant Compost Bin", ItemID.ULTRACOMPOST),
    CORAL("Coral Patches", ItemID.UMBRAL_CORAL),
    SEAWEED("Seaweed Patches", ItemID.GIANT_SEAWEED),
    CALQUAT("Calquat Patches", ItemID.CALQUAT_FRUIT),
    CELASTRUS("Celastrus Patch", ItemID.BATTLESTAFF),
    HARDWOOD("Hardwood Patches", ItemID.TEAK_LOGS),
    REDWOOD("Redwood Patch", ItemID.REDWOOD_LOGS),
    CACTUS("Cactus Patches", ItemID.POTATO_CACTUS),
    HESPORI("Hespori Patch", ItemID.TANGLEROOT),
    CRYSTAL("Crystal Patch", ItemID.CRYSTAL_SHARD),
    TIME_OFFSET("Farming Tick Offset", ItemID.WATERING_CAN),

    // Custom Tabs
    ANIMA("Anima Patch", ItemID.ANIMAINFUSED_BARK)
    ;

    public static final Tab[] FARMING_TABS = {HERB, TREE, FRUIT_TREE, SPECIAL, FLOWER, ALLOTMENT, BUSH, GRAPE, HOPS, CRYSTAL};

    private final String name;
    private final int itemID;
}
