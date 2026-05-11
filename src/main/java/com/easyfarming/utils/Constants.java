package com.easyfarming.utils;

import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.gameval.VarbitID;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Constants {
    
    // Scene size for iterating over tiles
    public static final int SCENE_SIZE = 104;
    
    // Region IDs
    public static final int REGION_ARDOUGNE = 10547;
    public static final int REGION_ARDOUGNE_ALT = 10548; // Alternative region ID for Ardougne farming area
    public static final int REGION_CATHERBY = 11062;
    public static final int REGION_FALADOR = 12083;
    public static final int REGION_FARMING_GUILD = 4922;
    public static final int REGION_HARMONY = 15148;
    public static final int REGION_KOUREND = 6967;
    public static final int REGION_MORYTANIA = 14391;
    public static final int REGION_TROLL_STRONGHOLD = 11321;
    public static final int REGION_WEISS = 11325;
    public static final int REGION_CIVITAS = 6192;
    /** Kastori (fruit tree / calquat / flower); see RuneLite {@code FarmingWorld}. */
    public static final int REGION_KASTORI = 5423;
    public static final int REGION_KASTORI_ALT1 = 5167;
    public static final int REGION_KASTORI_ALT2 = 5424;
    /** Auburnvale (Nemus Retreat tree patch); see RuneLite {@code FarmingWorld}. */
    public static final int REGION_AUBURNVALE = 5427;
    public static final int REGION_AUBURNVALE_ALT1 = 5428;
    public static final int REGION_AUBURNVALE_ALT2 = 5684;
    public static final int REGION_GNOME_STRONGHOLD = 9782;
    public static final int REGION_GNOME_STRONGHOLD_ALT = 9781;
    
    // Patch state varbits. RuneLite names them generically (FARMING_TRANSMIT_*) - each transmit
    // letter is reused by multiple patches depending on the location/patch type.
    public static final int VARBIT_HERB_PATCH_STANDARD = VarbitID.FARMING_TRANSMIT_D;             // 4774
    public static final int VARBIT_HERB_PATCH_FARMING_GUILD = VarbitID.FARMING_TRANSMIT_E;        // 4775
    public static final int VARBIT_HERB_PATCH_HARMONY = VarbitID.FARMING_TRANSMIT_B;              // 4772
    public static final int VARBIT_HERB_PATCH_TROLL_WEISS = VarbitID.FARMING_TRANSMIT_A;          // 4771
    public static final int VARBIT_FLOWER_PATCH_STANDARD = VarbitID.FARMING_TRANSMIT_C;           // 4773
    public static final int VARBIT_FLOWER_PATCH_FARMING_GUILD = VarbitID.FARMING_TRANSMIT_H;      // 7906
    public static final int VARBIT_TREE_PATCH_STANDARD = VarbitID.FARMING_TRANSMIT_A;             // 4771
    public static final int VARBIT_TREE_PATCH_FARMING_GUILD = VarbitID.FARMING_TRANSMIT_G;        // 7905
    public static final int VARBIT_FRUIT_TREE_PATCH_STANDARD = VarbitID.FARMING_TRANSMIT_A;       // 4771
    public static final int VARBIT_FRUIT_TREE_PATCH_FARMING_GUILD = VarbitID.FARMING_TRANSMIT_K;  // 7909
    public static final int VARBIT_FRUIT_TREE_PATCH_GNOME_STRONGHOLD = VarbitID.FARMING_TRANSMIT_B; // 4772
    public static final int VARBIT_HOPS_PATCH_STANDARD = VarbitID.FARMING_TRANSMIT_A;             // 4771
    // Allotment patch varbits - fallback only (object composition is preferred)
    // These are only used if object composition doesn't provide a varbit ID
    // Different locations use different transmit varbits:
    // Catherby uses A1/B1, Ardougne uses A2/B2
    public static final int VARBIT_ALLOTMENT_PATCH_NORTH_A1 = VarbitID.FARMING_TRANSMIT_A1;  // North patch fallback (Catherby)
    public static final int VARBIT_ALLOTMENT_PATCH_SOUTH_B1 = VarbitID.FARMING_TRANSMIT_B1;  // South patch fallback (Catherby)
    public static final int VARBIT_ALLOTMENT_PATCH_NORTH_A2 = VarbitID.FARMING_TRANSMIT_A2;  // North patch fallback (Ardougne)
    public static final int VARBIT_ALLOTMENT_PATCH_SOUTH_B2 = VarbitID.FARMING_TRANSMIT_B2;  // South patch fallback (Ardougne)

    // Tool Leprechaun varbits (compost amounts stored at the leprechaun).
    public static final int VARBIT_COMPOST_STORED = VarbitID.FARMING_TOOLS_COMPOST;                       // 1442
    public static final int VARBIT_SUPERCOMPOST_STORED = VarbitID.FARMING_TOOLS_SUPERCOMPOST;             // 1443
    public static final int VARBIT_ULTRACOMPOST_STORED = VarbitID.FARMING_TOOLS_ULTRACOMPOST;             // 5732
    public static final int VARBIT_BOTTOMLESS_COMPOST = VarbitID.FARMING_TOOLS_BOTTOMLESS_BUCKET_TYPE;    // 7915
    
    // Interface IDs
    public static final int INTERFACE_SPELLBOOK_RESIZABLE = 161;
    public static final int INTERFACE_SPELLBOOK_FIXED = 164;
    public static final int INTERFACE_SPELLBOOK_TAB_RESIZABLE = 65;
    public static final int INTERFACE_SPELLBOOK_TAB_FIXED = 58;
    public static final int INTERFACE_PORTAL_NEXUS = 17;
    public static final int INTERFACE_PORTAL_NEXUS_CHILD = 13;
    public static final int INTERFACE_SPIRIT_TREE = 187;
    public static final int INTERFACE_SPIRIT_TREE_CHILD = 3;
    public static final int INTERFACE_JEWELLERY_BOX = 29155;
    public static final int INTERFACE_JEWELLERY_BOX_OPEN = 590;
    public static final int INTERFACE_TOOL_LEPRECHAUN = 125;
    public static final int INTERFACE_FARMER = 219;
    public static final int INTERFACE_INVENTORY = 149;
    public static final int INTERFACE_MAGIC_SPELLBOOK = 218;
    
    // Widget IDs
    public static final int WIDGET_PORTAL_NEXUS_PARENT = 17;
    public static final int WIDGET_PORTAL_NEXUS_CHILD = 12;
    public static final int WIDGET_JEWELLERY_BOX_WIDGET = 590;
    public static final int WIDGET_JEWELLERY_BOX_CHILD = 5;
    
    // Item ID Groups
    public static final List<Integer> TELEPORT_CRYSTAL_IDS = Collections.unmodifiableList(Arrays.asList(
        ItemID.MOURNING_TELEPORT_CRYSTAL_1,
        ItemID.MOURNING_TELEPORT_CRYSTAL_2,
        ItemID.MOURNING_TELEPORT_CRYSTAL_3,
        ItemID.MOURNING_TELEPORT_CRYSTAL_4,
        ItemID.MOURNING_TELEPORT_CRYSTAL_5,
        ItemID.PRIF_TELEPORT_CRYSTAL
    ));
    
    public static final List<Integer> SKILLS_NECKLACE_IDS = Collections.unmodifiableList(Arrays.asList(
        ItemID.JEWL_NECKLACE_OF_SKILLS_1,
        ItemID.JEWL_NECKLACE_OF_SKILLS_2,
        ItemID.JEWL_NECKLACE_OF_SKILLS_3,
        ItemID.JEWL_NECKLACE_OF_SKILLS_4,
        ItemID.JEWL_NECKLACE_OF_SKILLS_5,
        ItemID.JEWL_NECKLACE_OF_SKILLS_6
    ));

    /** Necklace of passage (1) through (5); teleports include The Outpost (near Gnome Stronghold). */
    public static final List<Integer> NECKLACE_OF_PASSAGE_IDS = Collections.unmodifiableList(Arrays.asList(
        ItemID.NECKLACE_OF_PASSAGE_1,
        ItemID.NECKLACE_OF_PASSAGE_2,
        ItemID.NECKLACE_OF_PASSAGE_3,
        ItemID.NECKLACE_OF_PASSAGE_4,
        ItemID.NECKLACE_OF_PASSAGE_5
    ));

    /** Combat bracelet (1) — canonical ID for teleports that list "combat bracelet". */
    public static final int BASE_COMBAT_BRACELET_ID = ItemID.JEWL_BRACELET_OF_COMBAT_1;
    /** All charged Combat bracelet variants (1) through (6). */
    public static final List<Integer> COMBAT_BRACELET_IDS = Collections.unmodifiableList(Arrays.asList(
        ItemID.JEWL_BRACELET_OF_COMBAT_1,
        ItemID.JEWL_BRACELET_OF_COMBAT_2,
        ItemID.JEWL_BRACELET_OF_COMBAT_3,
        ItemID.JEWL_BRACELET_OF_COMBAT_4,
        ItemID.JEWL_BRACELET_OF_COMBAT_5,
        ItemID.JEWL_BRACELET_OF_COMBAT_6
    ));

    /**
     * Bottomless compost bucket inventory variants (empty + filled tiers).
     * Gameval splits some IDs across {@link ItemID.Cert} and {@link ItemID.Placeholder}.
     */
    public static final List<Integer> BOTTOMLESS_COMPOST_BUCKET_ITEM_IDS = Collections.unmodifiableList(Arrays.asList(
        ItemID.BOTTOMLESS_COMPOST_BUCKET,
        ItemID.Cert.BOTTOMLESS_COMPOST_BUCKET,
        ItemID.Placeholder.BOTTOMLESS_COMPOST_BUCKET,
        ItemID.BOTTOMLESS_COMPOST_BUCKET_FILLED,
        ItemID.Placeholder.BOTTOMLESS_COMPOST_BUCKET_FILLED
    ));
    
    public static final List<Integer> HERB_PATCH_IDS = Collections.unmodifiableList(Arrays.asList(
        33176, 27115, 8152, 8150, 8153, 18816, 8151, 9372,
        33979,  // Farming Guild herb patch
        50697
    ));
    
    public static final List<Integer> FLOWER_PATCH_IDS = Collections.unmodifiableList(Arrays.asList(
        27111, 7849, 7847, 7850, 7848, 33649, 50693
    ));
    
    public static final List<Integer> TREE_PATCH_IDS = Collections.unmodifiableList(Arrays.asList(
        8389, 33732, 19147, 8391, 8388, 8390,
        // Standard tree patch rake/weeds cycle (RuneLite {@code PatchImplementation} TREE)
        8392, 8393, 8394, 8395
    ));
    
    public static final List<Integer> FRUIT_TREE_PATCH_IDS = Collections.unmodifiableList(Arrays.asList(
        7964, 7965, 34007, 7962, 26579, 7963,
        // Fruit tree patch rake/weeds cycle (RuneLite {@code PatchImplementation} FRUIT_TREE)
        8047, 8048, 8049, 8050
    ));
    
    public static final List<Integer> HOPS_PATCH_IDS = Collections.unmodifiableList(Arrays.asList(
        8175, 8174, 8173, 8176, 55341
    ));
    
    // Allotment patch IDs per location
    // Format: [north patch, south patch] for each location
    // Note: Troll Stronghold and Weiss have no allotment patches
    // Harmony is excluded for now
    public static final Map<String, List<Integer>> ALLOTMENT_PATCH_IDS_BY_LOCATION;
    
    static {
        Map<String, List<Integer>> patchMap = new HashMap<>();
        patchMap.put("Ardougne", Arrays.asList(8554, 8555));  // north, south
        patchMap.put("Catherby", Arrays.asList(8552, 8553));  // north, south
        patchMap.put("Falador", Arrays.asList(8550, 8551));  // north, south
        patchMap.put("Farming Guild", Arrays.asList(33694, 33693));  // north, south
        patchMap.put("Kourend", Arrays.asList(27113, 27114));  // north, south
        patchMap.put("Morytania", Arrays.asList(8556, 8557));  // north, south
        patchMap.put("Civitas illa Fortis", Arrays.asList(50696, 50695));  // north, south
        ALLOTMENT_PATCH_IDS_BY_LOCATION = Collections.unmodifiableMap(patchMap);
    }
    
    // Herb patch IDs per location
    // Format: single patch ID for each location
    public static final Map<String, Integer> HERB_PATCH_IDS_BY_LOCATION;
    
    static {
        Map<String, Integer> patchMap = new HashMap<>();
        patchMap.put("Ardougne", 8152);   // Ardougne herb patch
        patchMap.put("Catherby", 8151);   // Catherby herb patch
        patchMap.put("Falador", 8150);
        patchMap.put("Farming Guild", 33979);  // Farming Guild herb patch object ID
        patchMap.put("Harmony Island", 9372);
        patchMap.put("Kourend", 27115);
        patchMap.put("Morytania", 8153);
        patchMap.put("Troll Stronghold", 18816);
        patchMap.put("Weiss", 33176);
        patchMap.put("Civitas illa Fortis", 50697);
        HERB_PATCH_IDS_BY_LOCATION = Collections.unmodifiableMap(patchMap);
    }
    
    // Flower patch IDs per location
    // Format: single patch ID for each location
    public static final Map<String, Integer> FLOWER_PATCH_IDS_BY_LOCATION;
    
    static {
        Map<String, Integer> patchMap = new HashMap<>();
        patchMap.put("Ardougne", 7849);
        patchMap.put("Catherby", 7848);
        patchMap.put("Falador", 7847);
        patchMap.put("Farming Guild", 33649);
        patchMap.put("Kourend", 27111);
        patchMap.put("Morytania", 7850);
        patchMap.put("Civitas illa Fortis", 50693);
        FLOWER_PATCH_IDS_BY_LOCATION = Collections.unmodifiableMap(patchMap);
    }
    
    // Hops patch IDs per location
    // Format: single patch ID for each location
    public static final Map<String, Integer> HOPS_PATCH_IDS_BY_LOCATION;
    
    static {
        Map<String, Integer> patchMap = new HashMap<>();
        patchMap.put("Lumbridge", 8175);
        patchMap.put("Seers Village", 8176);
        patchMap.put("Yanille", 8173);
        patchMap.put("Entrana", 8174);
        patchMap.put("Aldarin", 55341);
        HOPS_PATCH_IDS_BY_LOCATION = Collections.unmodifiableMap(patchMap);
    }
    
    // Fruit tree patch IDs per location
    // Format: single patch ID for each location
    public static final Map<String, Integer> FRUIT_TREE_PATCH_IDS_BY_LOCATION;
    
    static {
        Map<String, Integer> patchMap = new HashMap<>();
        patchMap.put("Brimhaven", 7964);
        patchMap.put("Catherby", 7965);
        patchMap.put("Farming Guild", 34007);
        patchMap.put("Gnome Stronghold", 7962);
        patchMap.put("Tree Gnome Village", 7963);
        patchMap.put("Lletya", 26579);
        // Kastori uses transmit B; empty/weeded visuals follow global fruit tree patch ids (8050 etc.)
        patchMap.put("Kastori", 8050);
        FRUIT_TREE_PATCH_IDS_BY_LOCATION = Collections.unmodifiableMap(patchMap);
    }
    
    // Legacy support - returns Ardougne patches by default
    @Deprecated
    public static final List<Integer> ALLOTMENT_PATCH_IDS = Collections.unmodifiableList(Arrays.asList(
        8554, 8555  // Ardougne: north patch, south patch
    ));
    
    public static final List<Integer> HERB_SEED_IDS = Collections.unmodifiableList(Arrays.asList(
        ItemID.GUAM_SEED, ItemID.MARRENTILL_SEED, ItemID.TARROMIN_SEED, ItemID.HARRALANDER_SEED,
        ItemID.RANARR_SEED, ItemID.TOADFLAX_SEED, ItemID.IRIT_SEED, ItemID.AVANTOE_SEED,
        ItemID.KWUARM_SEED, ItemID.SNAPDRAGON_SEED, ItemID.CADANTINE_SEED, ItemID.LANTADYME_SEED,
        ItemID.DWARF_WEED_SEED, ItemID.TORSTOL_SEED, ItemID.HUASCA_SEED
    ));
    
    public static final List<Integer> TREE_SAPLING_IDS = Collections.unmodifiableList(Arrays.asList(
        ItemID.PLANTPOT_OAK_SAPLING, ItemID.PLANTPOT_WILLOW_SAPLING, ItemID.PLANTPOT_MAPLE_SAPLING,
        ItemID.PLANTPOT_YEW_SAPLING, ItemID.PLANTPOT_MAGIC_TREE_SAPLING
    ));
    
    public static final List<Integer> FRUIT_TREE_SAPLING_IDS = Collections.unmodifiableList(Arrays.asList(
        ItemID.PLANTPOT_APPLE_SAPLING, ItemID.PLANTPOT_BANANA_SAPLING, ItemID.PLANTPOT_ORANGE_SAPLING,
        ItemID.PLANTPOT_CURRY_SAPLING, ItemID.PLANTPOT_PINEAPPLE_SAPLING, ItemID.PLANTPOT_PAPAYA_SAPLING,
        ItemID.PLANTPOT_PALM_SAPLING, ItemID.PLANTPOT_DRAGONFRUIT_SAPLING
    ));
    
    public static final List<Integer> ALLOTMENT_SEED_IDS = Collections.unmodifiableList(Arrays.asList(
        ItemID.POTATO_SEED, ItemID.ONION_SEED, ItemID.CABBAGE_SEED, ItemID.TOMATO_SEED,
        ItemID.SWEETCORN_SEED, ItemID.STRAWBERRY_SEED, ItemID.WATERMELON_SEED, ItemID.SNAPE_GRASS_SEED
    ));
    
    public static final List<Integer> HOPS_SEED_IDS = Collections.unmodifiableList(Arrays.asList(
        ItemID.BARLEY_SEED, ItemID.JUTE_SEED, ItemID.HAMMERSTONE_HOP_SEED, ItemID.ASGARNIAN_HOP_SEED, ItemID.YANILLIAN_HOP_SEED, ItemID.FLAX_SEED, ItemID.KRANDORIAN_HOP_SEED, ItemID.WILDBLOOD_HOP_SEED, ItemID.HEMP_SEED, ItemID.COTTON_SEED
    ));

    /** All flower-patch seeds (stack variants per item) — any count toward flower patch requirements. */
    public static final List<Integer> FLOWER_SEED_IDS = Collections.unmodifiableList(Arrays.asList(
            ItemID.LIMPWURT_SEED,
            ItemID.WHITE_LILY_SEED,
            ItemID.WHITE_LILY_SEED_2,
            ItemID.WHITE_LILY_SEED_3,
            ItemID.WHITE_LILY_SEED_4,
            ItemID.WHITE_LILY_SEED_5,
            ItemID.MARIGOLD_SEED,
            ItemID.MARIGOLD_SEED_2,
            ItemID.MARIGOLD_SEED_3,
            ItemID.MARIGOLD_SEED_4,
            ItemID.MARIGOLD_SEED_5,
            ItemID.ROSEMARY_SEED,
            ItemID.ROSEMARY_SEED_2,
            ItemID.ROSEMARY_SEED_3,
            ItemID.ROSEMARY_SEED_4,
            ItemID.ROSEMARY_SEED_5,
            ItemID.NASTURTIUM_SEED,
            ItemID.NASTURTIUM_SEED_2,
            ItemID.NASTURTIUM_SEED_3,
            ItemID.NASTURTIUM_SEED_4,
            ItemID.NASTURTIUM_SEED_5,
            ItemID.WOAD_SEED,
            ItemID.WOAD_SEED_2,
            ItemID.WOAD_SEED_3,
            ItemID.WOAD_SEED_4,
            ItemID.WOAD_SEED_5));
    
    /** Standard watering cans (charges 1–10) plus Gricoller's ({@link ItemID#ZEAH_WATERINGCAN}). */
    public static final List<Integer> WATERING_CAN_IDS = Collections.unmodifiableList(Arrays.asList(
        ItemID.WATERING_CAN_0,
        ItemID.WATERING_CAN_1,
        ItemID.WATERING_CAN_2,
        ItemID.WATERING_CAN_3,
        ItemID.WATERING_CAN_4,
        ItemID.WATERING_CAN_5,
        ItemID.WATERING_CAN_6,
        ItemID.WATERING_CAN_7,
        ItemID.WATERING_CAN_8,
        ItemID.ZEAH_WATERINGCAN
    ));
    
    public static final List<Integer> RUNE_POUCH_IDS = Collections.unmodifiableList(Arrays.asList(
        ItemID.BH_RUNE_POUCH, ItemID.DIVINE_RUNE_POUCH
    ));
    
    public static final List<Integer> RUNE_POUCH_AMOUNT_VARBITS = Collections.unmodifiableList(Arrays.asList(
        VarbitID.RUNE_POUCH_QUANTITY_1, VarbitID.RUNE_POUCH_QUANTITY_2,
        VarbitID.RUNE_POUCH_QUANTITY_3, VarbitID.RUNE_POUCH_QUANTITY_4
    ));
    
    public static final List<Integer> RUNE_POUCH_RUNE_VARBITS = Collections.unmodifiableList(Arrays.asList(
        VarbitID.RUNE_POUCH_TYPE_1, VarbitID.RUNE_POUCH_TYPE_2,
        VarbitID.RUNE_POUCH_TYPE_3, VarbitID.RUNE_POUCH_TYPE_4
    ));
    
    public static final List<Integer> SPIRIT_TREE_IDS = Collections.unmodifiableList(Arrays.asList(
        1293, 1294, 1295, 8355, 29227, 29229, 37329, 40778
    ));
    
    public static final int FAIRY_RING_OBJECT_ID = 29495;
    
    // Lumbridge & Draynor Elite diary completion varbit
    // When value >= 1, the player does not need a Dramen/Lunar staff for fairy rings
    public static final int VARBIT_LUMBRIDGE_DIARY_ELITE = VarbitID.LUMBRIDGE_DIARY_ELITE_COMPLETE;
    
    public static final List<Integer> JEWELLERY_BOX_IDS = Collections.unmodifiableList(Arrays.asList(
        29154, 29155, 29156
    ));
    
    /**
     * Decorative object IDs for mounted Xeric's talisman in the POH (used with
     * {@code highlightDecorativeObject}). Defined in {@code gameval.ObjectID1} but inherited
     * by the public {@link ObjectID} class.
     */
    public static final List<Integer> XERICS_TALISMAN_IDS = Collections.unmodifiableList(Arrays.asList(
        ObjectID.POH_AMULET_XERIC_LOOKOUT,
        ObjectID.POH_AMULET_XERIC_GLADE,
        ObjectID.POH_AMULET_XERIC_INFERNO,
        ObjectID.POH_AMULET_XERIC_HEART,
        ObjectID.POH_AMULET_XERIC_HONOUR
    ));

    // Base item IDs (for variant handling)
    public static final int BASE_TELEPORT_CRYSTAL_ID = ItemID.MOURNING_TELEPORT_CRYSTAL_1;
    public static final int BASE_SKILLS_NECKLACE_ID = ItemID.JEWL_NECKLACE_OF_SKILLS_1;
    public static final int BASE_NECKLACE_OF_PASSAGE_ID = ItemID.NECKLACE_OF_PASSAGE_5;
    public static final int BASE_HERB_SEED_ID = ItemID.GUAM_SEED;
    public static final int BASE_TREE_SAPLING_ID = ItemID.PLANTPOT_OAK_SAPLING;
    public static final int BASE_FRUIT_TREE_SAPLING_ID = ItemID.PLANTPOT_APPLE_SAPLING;
    public static final int BASE_ALLOTMENT_SEED_ID = ItemID.SNAPE_GRASS_SEED;
    
    // Combination rune mapping
    public static final Map<Integer, List<Integer>> COMBINATION_RUNE_SUBRUNES_MAP;
    
    static {
        Map<Integer, List<Integer>> tempMap = new HashMap<>();
        tempMap.put(ItemID.DUSTRUNE, Arrays.asList(ItemID.AIRRUNE, ItemID.EARTHRUNE));
        tempMap.put(ItemID.MISTRUNE, Arrays.asList(ItemID.AIRRUNE, ItemID.WATERRUNE));
        tempMap.put(ItemID.MUDRUNE, Arrays.asList(ItemID.WATERRUNE, ItemID.EARTHRUNE));
        tempMap.put(ItemID.LAVARUNE, Arrays.asList(ItemID.FIRERUNE, ItemID.EARTHRUNE));
        tempMap.put(ItemID.STEAMRUNE, Arrays.asList(ItemID.FIRERUNE, ItemID.WATERRUNE));
        tempMap.put(ItemID.SMOKERUNE, Arrays.asList(ItemID.FIRERUNE, ItemID.AIRRUNE));
        COMBINATION_RUNE_SUBRUNES_MAP = Collections.unmodifiableMap(tempMap);
    }
    
    // Helper methods
    public static boolean isTeleportCrystal(int itemId) {
        return TELEPORT_CRYSTAL_IDS.contains(itemId);
    }
    
    public static boolean isSkillsNecklace(int itemId) {
        return SKILLS_NECKLACE_IDS.contains(itemId);
    }

    public static boolean isNecklaceOfPassage(int itemId) {
        return NECKLACE_OF_PASSAGE_IDS.contains(itemId);
    }

    public static boolean isCombatBracelet(int itemId) {
        return itemId == BASE_COMBAT_BRACELET_ID || COMBAT_BRACELET_IDS.contains(itemId);
    }

    /** Returns the number of charges for a Combat bracelet item ID, or 0 if not a charged bracelet. */
    public static int getCombatBraceletCharges(int itemId) {
        switch (itemId) {
            case ItemID.JEWL_BRACELET_OF_COMBAT_1:
                return 1;
            case ItemID.JEWL_BRACELET_OF_COMBAT_2:
                return 2;
            case ItemID.JEWL_BRACELET_OF_COMBAT_3:
                return 3;
            case ItemID.JEWL_BRACELET_OF_COMBAT_4:
                return 4;
            case ItemID.JEWL_BRACELET_OF_COMBAT_5:
                return 5;
            case ItemID.JEWL_BRACELET_OF_COMBAT_6:
                return 6;
            default:
                return 0;
        }
    }
    
    public static boolean isHerbSeed(int itemId) {
        return HERB_SEED_IDS.contains(itemId);
    }
    
    public static boolean isTreeSapling(int itemId) {
        return TREE_SAPLING_IDS.contains(itemId);
    }
    
    public static boolean isFruitTreeSapling(int itemId) {
        return FRUIT_TREE_SAPLING_IDS.contains(itemId);
    }
    
    public static boolean isAllotmentSeed(int itemId) {
        return ALLOTMENT_SEED_IDS.contains(itemId);
    }

    public static boolean isFlowerSeed(int itemId) {
        return FLOWER_SEED_IDS.contains(itemId);
    }
    
    public static boolean isQuetzalWhistle(int itemId) {
        return itemId == ItemID.HG_QUETZALWHISTLE_BASIC ||
               itemId == ItemID.HG_QUETZALWHISTLE_ENHANCED ||
               itemId == ItemID.HG_QUETZALWHISTLE_PERFECTED;
    }
}

