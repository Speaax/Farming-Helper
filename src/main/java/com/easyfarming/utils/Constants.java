package com.easyfarming.utils;

import net.runelite.api.gameval.ItemID;
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
    public static final int REGION_CATHERBY = 11061;
    public static final int REGION_FALADOR = 11828;
    public static final int REGION_FARMING_GUILD = 4922;
    public static final int REGION_HARMONY = 15148;
    public static final int REGION_KOUREND = 6967;
    public static final int REGION_MORYTANIA = 14647;
    public static final int REGION_TROLL_STRONGHOLD = 11321;
    public static final int REGION_WEISS = 11325;
    public static final int REGION_CIVITAS = 6192;
    public static final int REGION_GNOME_STRONGHOLD = 9782;
    public static final int REGION_GNOME_STRONGHOLD_ALT = 9781;
    
    // Varbit IDs for patch checking
    public static final int VARBIT_HERB_PATCH_STANDARD = 4774;
    public static final int VARBIT_HERB_PATCH_FARMING_GUILD = 4775;
    public static final int VARBIT_HERB_PATCH_HARMONY = 4772;
    public static final int VARBIT_HERB_PATCH_TROLL_WEISS = 4771;
    public static final int VARBIT_FLOWER_PATCH_STANDARD = 4773;
    public static final int VARBIT_FLOWER_PATCH_FARMING_GUILD = 7906;
    public static final int VARBIT_TREE_PATCH_STANDARD = 4771;
    public static final int VARBIT_TREE_PATCH_FARMING_GUILD = 7905;
    public static final int VARBIT_FRUIT_TREE_PATCH_STANDARD = 4771;
    public static final int VARBIT_FRUIT_TREE_PATCH_FARMING_GUILD = 7909;
    public static final int VARBIT_FRUIT_TREE_PATCH_GNOME_STRONGHOLD = 4772;
    
    // Tool Leprechaun varbits
    public static final int VARBIT_COMPOST_STORED = 1442;
    public static final int VARBIT_SUPERCOMPOST_STORED = 1443;
    public static final int VARBIT_ULTRACOMPOST_STORED = 5732;
    public static final int VARBIT_BOTTOMLESS_COMPOST = 7915;
    
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
        ItemID.MOURNING_TELEPORT_CRYSTAL_5
    ));
    
    public static final List<Integer> SKILLS_NECKLACE_IDS = Collections.unmodifiableList(Arrays.asList(
        ItemID.JEWL_NECKLACE_OF_SKILLS_1,
        ItemID.JEWL_NECKLACE_OF_SKILLS_2,
        ItemID.JEWL_NECKLACE_OF_SKILLS_3,
        ItemID.JEWL_NECKLACE_OF_SKILLS_4,
        ItemID.JEWL_NECKLACE_OF_SKILLS_5,
        ItemID.JEWL_NECKLACE_OF_SKILLS_6
    ));
    
    public static final List<Integer> HERB_PATCH_IDS = Collections.unmodifiableList(Arrays.asList(
        33176, 27115, 8152, 8150, 8153, 18816, 8151, 9372, 33979, 50697
    ));
    
    public static final List<Integer> FLOWER_PATCH_IDS = Collections.unmodifiableList(Arrays.asList(
        27111, 7849, 7847, 7850, 7848, 33649
    ));
    
    public static final List<Integer> TREE_PATCH_IDS = Collections.unmodifiableList(Arrays.asList(
        8389, 33732, 19147, 8391, 8388, 8390
    ));
    
    public static final List<Integer> FRUIT_TREE_PATCH_IDS = Collections.unmodifiableList(Arrays.asList(
        7964, 7965, 34007, 7962, 26579, 7963
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
    
    public static final List<Integer> JEWELLERY_BOX_IDS = Collections.unmodifiableList(Arrays.asList(
        29154, 29155, 29156
    ));
    
    public static final List<Integer> XERICS_TALISMAN_IDS = Collections.unmodifiableList(Arrays.asList(
        33411, 33412, 33413, 33414, 33415
    ));
    
    // Base item IDs (for variant handling)
    public static final int BASE_TELEPORT_CRYSTAL_ID = ItemID.MOURNING_TELEPORT_CRYSTAL_1;
    public static final int BASE_SKILLS_NECKLACE_ID = ItemID.JEWL_NECKLACE_OF_SKILLS_1;
    public static final int BASE_HERB_SEED_ID = ItemID.GUAM_SEED;
    public static final int BASE_TREE_SAPLING_ID = ItemID.PLANTPOT_OAK_SAPLING;
    public static final int BASE_FRUIT_TREE_SAPLING_ID = ItemID.PLANTPOT_APPLE_SAPLING;
    
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
    
    public static boolean isHerbSeed(int itemId) {
        return HERB_SEED_IDS.contains(itemId);
    }
    
    public static boolean isTreeSapling(int itemId) {
        return TREE_SAPLING_IDS.contains(itemId);
    }
    
    public static boolean isFruitTreeSapling(int itemId) {
        return FRUIT_TREE_SAPLING_IDS.contains(itemId);
    }
    
    public static boolean isQuetzalWhistle(int itemId) {
        return itemId == ItemID.HG_QUETZALWHISTLE_BASIC ||
               itemId == ItemID.HG_QUETZALWHISTLE_ENHANCED ||
               itemId == ItemID.HG_QUETZALWHISTLE_PERFECTED;
    }
}

