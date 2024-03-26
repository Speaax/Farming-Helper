package com.farminghelper.speaax.Patch;

import com.farminghelper.speaax.FarmingHelperConfig;

import net.runelite.api.ItemID;
import net.runelite.api.coords.WorldPoint;

import javax.inject.Inject;
import java.util.*;

public enum Location
{
    ARDOUGNE(
        "Ardougne",
        Map.of(
            PatchType.HERB, new Patch(
                new WorldPoint(2670, 3374, 0),
                FarmingHelperConfig::enumOptionEnumArdougneTeleport,
                true
            )
        ),
        Arrays.asList(
            new Teleport(
                "Portal_Nexus",
                Teleport.Category.PORTAL_NEXUS,
                "Teleport to Ardougne with Portal Nexus.",
                0,
                "null",
                17,
                13,
                10547,
                true
            ),
            new Teleport(
                "Ardougne_teleport",
                Teleport.Category.SPELLBOOK,
                "Teleport to Ardougne with standard spellbook, and run north.",
                0,
                "null",
                218,
                38,
                10547,
                Arrays.asList(
                    new ItemRequirement(
                        ItemID.LAW_RUNE,
                        2
                    ),
                    new ItemRequirement(
                        ItemID.WATER_RUNE,
                        2
                    )
                )
            ),
            new Teleport(
                "Ardougne_Tele_Tab",
                Teleport.Category.ITEM,
                "Teleport to Ardougne with Ardougne tele tab, and run north.",
                ItemID.ARDOUGNE_TELEPORT,
                "null",
                0,
                0,
                10547,
                Arrays.asList(
                    new ItemRequirement(
                        ItemID.ARDOUGNE_TELEPORT,
                        1
                    )
                )
            ),
            new Teleport(
                "Ardy_Cloak_2",
                Teleport.Category.ITEM,
                "Teleport to Ardougne with Ardougne cloak.",
                ItemID.ARDOUGNE_CLOAK_2,
                "Farm Teleport",
                0,
                0,
                10548,
                Arrays.asList(
                    new ItemRequirement(
                        ItemID.ARDOUGNE_CLOAK_2,
                        1
                    )
                )
            ),
            new Teleport(
                "Ardy_Cloak_3",
                Teleport.Category.ITEM,
                "Teleport to Ardougne with Ardougne cloak.",
                ItemID.ARDOUGNE_CLOAK_3,
                "Farm Teleport",
                0,
                0,
                10548,
                Arrays.asList(
                    new ItemRequirement(
                        ItemID.ARDOUGNE_CLOAK_3,
                        1
                    )
                )
            ),
            new Teleport(
                "Ardy_Cloak_4",
                Teleport.Category.ITEM,
                "Teleport to Ardougne with Ardougne cloak.",
                ItemID.ARDOUGNE_CLOAK_4,
                "Farm Teleport",
                0,
                0,
                10548,
                Arrays.asList(
                    new ItemRequirement(
                        ItemID.ARDOUGNE_CLOAK_4,
                        1
                    )
                )
            ),
            new Teleport(
                "Skills_Necklace",
                Teleport.Category.ITEM,
                "Teleport to Fishing guild with Skills necklace, and run east.",
                ItemID.SKILLS_NECKLACE1,
                "null",
                0,
                0,
                10292,
                Arrays.asList(
                    new ItemRequirement(
                        ItemID.SKILLS_NECKLACE1,
                        1
                    )
                )
            )
        )
    ),
    BRIMHAVEN(
        "Brimhaven",
        Map.of(
            PatchType.FRUIT_TREE, new Patch(
                new WorldPoint(2764, 3212, 0),
                FarmingHelperConfig::enumFruitTreeBrimhavenTeleport,
                false
            )
        ),
        Arrays.asList(
            new Teleport(
                "Portal_Nexus",
                Teleport.Category.PORTAL_NEXUS,
                "Teleport to Ardougne with Portal Nexus and take the boat to Brimhaven.",
                0,
                "null",
                17,
                13,
                10547,
                true
            ).overrideLocationName("Ardougne"),
            new Teleport(
                "Ardougne_teleport",
                Teleport.Category.SPELLBOOK,
                "Teleport to Ardougne with Spellbook and take the boat to Brimhaven.",
                0,
                "null",
                218,
                38,
                10547,
                Arrays.asList(
                    new ItemRequirement(
                        ItemID.COINS_995,
                        30
                    ),
                    new ItemRequirement(
                        ItemID.LAW_RUNE,
                        2
                    ),
                    new ItemRequirement(
                        ItemID.WATER_RUNE,
                        2
                    )
                )
            )
        )
    ),
    CATHERBY(
        "Catherby",
        Map.of(
            PatchType.HERB, new Patch(
                new WorldPoint(2813, 3463, 0),
                FarmingHelperConfig::enumOptionEnumCatherbyTeleport,
                true
            ),
            PatchType.FRUIT_TREE, new Patch(
                new WorldPoint(2860, 3433, 0),
                FarmingHelperConfig::enumFruitTreeCatherbyTeleport,
                false
            )
        ),
        Arrays.asList(
            new Teleport(
                "Portal_Nexus_Catherby",
                Teleport.Category.PORTAL_NEXUS,
                "Teleport to Catherby with Portal Nexus.",
                0,
                "null",
                17,
                13,
                11061,
                true
            ),
            new Teleport(
                "Portal_Nexus_Camelot",
                Teleport.Category.PORTAL_NEXUS,
                "Teleport to Camelot with Portal Nexus and run to Catherby.",
                0,
                "null",
                17,
                13,
                11062,
                true
            ).overrideLocationName("Camelot"),
            new Teleport(
                "Camelot_Teleport",
                Teleport.Category.SPELLBOOK,
                "Teleport to Camelot using the standard spellbook, and run east. (If you have configured the teleport to seers you need to right click and teleport to Camelot)",
                0,
                "null",
                218,
                32,
                11062,
                Arrays.asList(
                    new ItemRequirement(
                        ItemID.AIR_RUNE,
                        5
                    ),
                    new ItemRequirement(
                        ItemID.LAW_RUNE,
                        1
                    )
                )
            ),
            new Teleport(
                "Camelot_Tele_Tab",
                Teleport.Category.ITEM,
                "Teleport to Camelot using a Camelot tele tab, and run east.(If you have configured the teleport to seers you need to right click and teleport to Camelot)",
                ItemID.CAMELOT_TELEPORT,
                "null",
                0,
                0,
                11062,
                Arrays.asList(
                    new ItemRequirement(
                        ItemID.CAMELOT_TELEPORT,
                        1
                    )
                )
            ),
            new Teleport(
                "Catherby_Tele_Tab",
                Teleport.Category.ITEM,
                "Teleport to Catherby using Catherby teleport tab.",
                ItemID.CATHERBY_TELEPORT,
                "null",
                0,
                0,
                11061,
                Arrays.asList(
                    new ItemRequirement(
                        ItemID.CATHERBY_TELEPORT,
                        1
                    )
                )
            )
        )
    ),
    FALADOR(
        "Falador",
        Map.of(
            PatchType.HERB, new Patch(
                new WorldPoint(3058, 3307, 0),
                FarmingHelperConfig::enumOptionEnumFaladorTeleport,
                true
            ),
            PatchType.TREE, new Patch(
                new WorldPoint(3000, 3373, 0),
                FarmingHelperConfig::enumTreeFaladorTeleport,
                false
            )
        ),
        Arrays.asList(
            new Teleport(
                "Portal_Nexus",
                Teleport.Category.PORTAL_NEXUS,
                "Teleport to Falador with Portal Nexus.",
                0,
                "null",
                17,
                13,
                11828,
                true
            ),
            new Teleport(
                "Falador_teleport",
                Teleport.Category.SPELLBOOK,
                "Teleport to Falador with Spellbook and run to Falador park.",
                0,
                "null",
                218,
                27,
                11828,
                Arrays.asList(
                    new ItemRequirement(
                        ItemID.AIR_RUNE,
                        3
                    ),
                    new ItemRequirement(
                        ItemID.LAW_RUNE,
                        1
                    ),
                    new ItemRequirement(
                        ItemID.WATER_RUNE,
                        1
                    )
                )
            ),
            new Teleport(
                "Explorers_ring_2",
                Teleport.Category.ITEM,
                "Teleport to Falador with Explorers ring.",
                ItemID.EXPLORERS_RING_2,
                "Teleport",
                0,
                0,
                12083,
                Arrays.asList(
                    new ItemRequirement(
                        ItemID.EXPLORERS_RING_2,
                        1
                    )
                )
            ),
            new Teleport(
                "Explorers_ring_3",
                Teleport.Category.ITEM,
                "Teleport to Falador with Explorers ring.",
                ItemID.EXPLORERS_RING_3,
                "Teleport",
                0,
                0,
                12083,
                Arrays.asList(
                    new ItemRequirement(
                        ItemID.EXPLORERS_RING_3,
                        1
                    )
                )
            ),
            new Teleport(
                "Explorers_ring_4",
                Teleport.Category.ITEM,
                "Teleport to Falador with Explorers ring.",
                ItemID.EXPLORERS_RING_4,
                "Teleport",
                0,
                0,
                12083,
                Arrays.asList(
                    new ItemRequirement(
                        ItemID.EXPLORERS_RING_4,
                        1
                    )
                )
            ),
            new Teleport(
                "Falador_Tele_Tab",
                Teleport.Category.ITEM,
                "Teleport to Falador with Falador Tele Tab, and run south-east.",
                ItemID.FALADOR_TELEPORT,
                "null",
                0,
                0,
                11828,
                Arrays.asList(
                    new ItemRequirement(
                        ItemID.FALADOR_TELEPORT,
                        1
                    )
                )
            ),
            new Teleport(
                "Draynor_Tele_Tab",
                Teleport.Category.ITEM,
                "Teleport to Draynor Manor with Draynor Manor Tele Tab, and run south-west.",
                ItemID.DRAYNOR_MANOR_TELEPORT,
                "null",
                0,
                0,
                12340,
                Arrays.asList(
                    new ItemRequirement(
                        ItemID.DRAYNOR_MANOR_TELEPORT,
                        1
                    )
                )
            )
        )
    ),
    FARMING_GUILD(
        "Farming Guild",
        Map.of(
            PatchType.HERB, new Patch(
                new WorldPoint(1238, 3726, 0),
                FarmingHelperConfig::enumOptionEnumFarmingGuildTeleport,
                true
            ),
            PatchType.TREE, new Patch(
                new WorldPoint(1232, 3736, 0),
                FarmingHelperConfig::enumTreeFarmingGuildTeleport,
                false
            ),
            PatchType.FRUIT_TREE, new Patch(
                new WorldPoint(1243, 3759, 0),
                FarmingHelperConfig::enumFruitTreeFarmingGuildTeleport,
                false
            )
        ),
        Arrays.asList(
            new Teleport(
                "Jewellery_box",
                Teleport.Category.JEWELLERY_BOX,
                "Teleport to Farming Guild with Jewellery box.",
                0,
                "null",
                17,
                13,
                4922,
                true
            ),
            new Teleport(
                "Skills_Necklace",
                Teleport.Category.ITEM,
                "Teleport to Farming guild using Skills necklace.",
                ItemID.SKILLS_NECKLACE1,
                "null",
                0,
                0,
                4922,
                Arrays.asList(
                    new ItemRequirement(
                        ItemID.SKILLS_NECKLACE1,
                        1
                    )
                )
            )
        )
    ),
    GNOME_STRONGHOLD(
        "Gnome Stronghold",
        Map.of(
            PatchType.TREE, new Patch(
                new WorldPoint(2436, 3415, 0),
                FarmingHelperConfig::enumTreeGnomeStrongoldTeleport,
                false
            ),
            PatchType.FRUIT_TREE, new Patch(
                new WorldPoint(2475, 3446, 0),
                FarmingHelperConfig::enumFruitTreeGnomeStrongholdTeleport,
                false
            )
        ),
        Arrays.asList(
            new Teleport(
                "Royal_seed_pod",
                Teleport.Category.ITEM,
                "Teleport to Gnome Stronghold with Royal seed pod.",
                ItemID.ROYAL_SEED_POD,
                "null",
                0,
                0,
                9782,
                Arrays.asList(
                    new ItemRequirement(
                        ItemID.ROYAL_SEED_POD,
                        1
                    )
                )
            ),
            new Teleport(
                "Spirit_Tree",
                Teleport.Category.SPIRIT_TREE,
                "Teleport to Gnome Stronghold via a Spirit Tree.",
                0,
                "null",
                187,
                3,
                9781,
                Collections.<ItemRequirement> emptyList()
            )
        )
    ),
    HARMONY_ISLAND(
        "Harmony Island",
        Map.of(
            PatchType.HERB, new Patch(
                new WorldPoint(3789, 2837, 0),
                FarmingHelperConfig::enumOptionEnumHarmonyTeleport,
                false
            )
        ),
        Arrays.asList(
            new Teleport(
                "Portal_Nexus",
                Teleport.Category.PORTAL_NEXUS,
                "Teleport to Harmony with Portal Nexus.",
                0,
                "null",
                17,
                13,
                15148,
                true
            ),
            new Teleport(
                "Harmony_Tele_Tab",
                Teleport.Category.ITEM,
                "Teleport to Harmony with Harmony Tele Tab.",
                ItemID.HARMONY_ISLAND_TELEPORT,
                "null",
                0,
                0,
                15148,
                Arrays.asList(
                    new ItemRequirement(
                        ItemID.HARMONY_ISLAND_TELEPORT,
                        1
                    )
                )
            )
        )
    ),
    KOUREND(
        "Kourend",
        Map.of(
            PatchType.HERB, new Patch(
                new WorldPoint(1738, 3550, 0),
                FarmingHelperConfig::enumOptionEnumKourendTeleport,
                true
            )
        ),
        Arrays.asList(
            new Teleport(
                "Xerics_Talisman",
                Teleport.Category.ITEM,
                "Teleport to Kourend with Xeric's Talisman.",
                ItemID.XERICS_TALISMAN,
                "Rub",
                187,
                3,
                6967,
                Arrays.asList(
                    new ItemRequirement(
                        ItemID.XERICS_TALISMAN,
                        1
                    )
                )
            ),
            new Teleport(
                "Mounted_Xerics",
                Teleport.Category.MOUNTED_XERICS,
                "Teleport to Kourend with Xeric's Talisman in PoH.",
                0,
                "null",
                187,
                3,
                6967,
                true
            )
        )
    ),
    LLETYA(
        "Lletya",
        Map.of(
            PatchType.FRUIT_TREE, new Patch(
                new WorldPoint(2346, 3162, 0),
                FarmingHelperConfig::enumFruitTreeLletyaTeleport,
                false
            )
        ),
        Arrays.asList(
            new Teleport(
                "Teleport_crystal",
                Teleport.Category.ITEM,
                "Teleport to Lletya with Teleport crystal.",
                ItemID.TELEPORT_CRYSTAL_1,
                "null",
                0,
                0,
                9265,
                Arrays.asList(
                    new ItemRequirement(
                        ItemID.TELEPORT_CRYSTAL_1,
                        1
                    )
                )
            )
        )
    ),
    LUMBRIDGE(
        "Lumbridge",
        Map.of(
            PatchType.TREE, new Patch(
                new WorldPoint(3193, 3231, 0),
                FarmingHelperConfig::enumTreeLumbridgeTeleport,
                false
            )
        ),
        Arrays.asList(
            new Teleport(
                "Portal_Nexus",
                Teleport.Category.PORTAL_NEXUS,
                "Teleport to Lumbridge with Portal Nexus.",
                0,
                "null",
                17,
                13,
                12850,
                true
            ),
            new Teleport(
                "Lumbridge_teleport",
                Teleport.Category.SPELLBOOK,
                "Teleport to Lumbridge with spellbook.",
                0,
                "null",
                218,
                24,
                12850,
                Arrays.asList(
                    new ItemRequirement(
                        ItemID.AIR_RUNE,
                        3
                    ),
                    new ItemRequirement(
                        ItemID.LAW_RUNE,
                        1
                    ),
                    new ItemRequirement(
                        ItemID.EARTH_RUNE,
                        1
                    )
                )
            )
        )
    ),
    MORYTANIA(
        "Morytania",
        Map.of(
            PatchType.HERB, new Patch(
                new WorldPoint(3601, 3525, 0),
                FarmingHelperConfig::enumOptionEnumMorytaniaTeleport,
                true
            )
        ),
        Arrays.asList(
            new Teleport(
                "Ectophial",
                Teleport.Category.ITEM,
                "Teleport to Morytania with Ectophial.",
                ItemID.ECTOPHIAL,
                "null",
                0,
                0,
                14647,
                Arrays.asList(
                    new ItemRequirement(
                        ItemID.ECTOPHIAL,
                        1
                    )
                )
            )
        )
    ),
    TAVERLY(
        "Taverley",
        Map.of(
            PatchType.TREE, new Patch(
                new WorldPoint(2936, 3438, 0),
                FarmingHelperConfig::enumTreeTaverleyTeleport,
                false
            )
        ),
        Arrays.asList(
            new Teleport(
                "Portal_Nexus",
                Teleport.Category.PORTAL_NEXUS,
                "Teleport to Falador with Portal Nexus and run to Taverly.",
                0,
                "null",
                17,
                13,
                11828,
                true
            ).overrideLocationName("Falador"),
            new Teleport(
                "Falador_teleport",
                Teleport.Category.SPELLBOOK,
                "Teleport to Falador with spellbook and run to Taverly.",
                0,
                "null",
                218,
                27,
                11828,
                Arrays.asList(
                    new ItemRequirement(
                        ItemID.AIR_RUNE,
                        3
                    ),
                    new ItemRequirement(
                        ItemID.LAW_RUNE,
                        1
                    ),
                    new ItemRequirement(
                        ItemID.WATER_RUNE,
                        1
                    )
                )
            ),
            new Teleport(
                "Teleport_to_house",
                Teleport.Category.ITEM,
                "Teleport to Taverly with the House Tele Tab's 'Outside' right click option, then run east to the tree patch.",
                ItemID.TELEPORT_TO_HOUSE,
                "Outside",
                0,
                0,
                11574,
                Arrays.asList(
                    new ItemRequirement(
                        ItemID.TELEPORT_TO_HOUSE,
                        1
                    )
                )
            )
        )
    ),
    TREE_GNOME_VILLAGE(
        "Tree Gnome Village",
        Map.of(
            PatchType.FRUIT_TREE, new Patch(
                new WorldPoint(2490, 3180, 0),
                FarmingHelperConfig::enumFruitTreeTreeGnomeVillageTeleport,
                false
            )
        ),
        Arrays.asList(
            new Teleport(
                "Royal_seed_pod",
                Teleport.Category.ITEM,
                "Teleport to Tree Gnome Village with Royal seed pod and use Spirit tree to Tree Gnome Village.",
                ItemID.ROYAL_SEED_POD,
                "null",
                0,
                0,
                9782,
                Arrays.asList(
                    new ItemRequirement(
                        ItemID.ROYAL_SEED_POD,
                        1
                    )
                )
            ),
            new Teleport(
                "Spirit_Tree",
                Teleport.Category.SPIRIT_TREE,
                "Teleport to Tree Gnome Village via a Spirit Tree.",
                0,
                "null",
                187,
                3,
                10033,
                Collections.<ItemRequirement> emptyList()
            )
        )
    ),
    TROLL_STRONGHOLD(
        "Troll Stronghold",
        Map.of(
            PatchType.HERB, new Patch(
                new WorldPoint(2824, 3696, 0),
                FarmingHelperConfig::enumOptionEnumTrollStrongholdTeleport,
                false
            )
        ),
        Arrays.asList(
            new Teleport(
                "Stony_Basalt",
                Teleport.Category.ITEM,
                "Teleport to Troll Stronghold with Stony Basalt.",
                ItemID.STONY_BASALT,
                "null",
                0,
                0,
                11321,
                Arrays.asList(
                    new ItemRequirement(
                        ItemID.STONY_BASALT,
                        1
                    )
                )
            ),
            new Teleport(
                "Portal_Nexus",
                Teleport.Category.PORTAL_NEXUS,
                "Teleport to Troll Stronghold with Portal Nexus.",
                0,
                "null",
                17,
                13,
                11321,
                true
            )
        )
    ),
    VARROCK(
        "Varrock",
        Map.of(
            PatchType.TREE, new Patch(
                new WorldPoint(3229, 3459, 0),
                FarmingHelperConfig::enumTreeVarrockTeleport,
                false
            )
        ),
        Arrays.asList(
            new Teleport(
                "Portal_Nexus",
                Teleport.Category.PORTAL_NEXUS,
                "Teleport to Varrock with Portal Nexus.",
                0,
                "null",
                17,
                13,
                12853,
                true
            ),
            new Teleport(
                "Varrock_teleport",
                Teleport.Category.SPELLBOOK,
                "Teleport to Varrock with spellbook.",
                0,
                "null",
                218,
                21,
                12853,
                Arrays.asList(
                    new ItemRequirement(
                        ItemID.AIR_RUNE,
                        3
                    ),
                    new ItemRequirement(
                        ItemID.LAW_RUNE,
                        1
                    ),
                    new ItemRequirement(
                        ItemID.FIRE_RUNE,
                        1
                    )
                )
            )
        )
    ),
    WEISS(
        "Weiss",
        Map.of(
            PatchType.HERB, new Patch(
                new WorldPoint(2847, 3931, 0),
                FarmingHelperConfig::enumOptionEnumWeissTeleport,
                false
            )
        ),
        Arrays.asList(
            new Teleport(
                "Icy_Basalt",
                Teleport.Category.ITEM,
                "Teleport to Weiss with Icy Basalt.",
                ItemID.ICY_BASALT,
                "null",
                0,
                0,
                11325,
                Arrays.asList(
                    new ItemRequirement(
                        ItemID.ICY_BASALT,
                        1
                    )
                )
            ),
            new Teleport(
                "Portal_Nexus",
                Teleport.Category.PORTAL_NEXUS,
                "Teleport to Weiss with Portal Nexus.",
                0,
                "null",
                17,
                13,
                11325,
                true
            )
        )
    );

    private String name;

    private Map<PatchType, Patch> patches;

    private List<Teleport> teleports;

    Location(
        String name,
        Map<PatchType, Patch> patches,
        List<Teleport> teleports
    ) {
        this.name = name;
        this.patches = patches;
        this.teleports = teleports;
    }

    public String getName()
    {
        return name;
    }

    public Patch patch(PatchType patchType)
    {
        return patches != null && patches.get(patchType) != null
            ? patches.get(patchType)
            : null;
    }

    public Teleport desiredTeleport(PatchType patchType, FarmingHelperConfig config)
    {
        Patch patch = patch(patchType);

        if (patch == null) {
            return teleports.isEmpty() ? null : teleports.get(0);
        }

        String selectedEnumOption = patch.getDesiredTeleportFunction().apply(config).name();

        for (Teleport teleport : teleports) {
            if (teleport.getEnumOption().equalsIgnoreCase(selectedEnumOption)) {
                return teleport;
            }
        }

        return teleports.isEmpty() ? null : teleports.get(0);
    }
}