package com.easyfarming.customrun;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.utils.Constants;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.runelite.api.gameval.ItemID;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SpecialTreeItemRequirementsTest {

    private final LocationCatalog catalog = new LocationCatalog(new TestPlugin());

    @Test
    public void countsEveryPhysicalSpecialTreePatchAndNormalizesHardwoodSaplings() {
        Map<Integer, Integer> requirements = requirements(false);

        assertEquals(Integer.valueOf(5), requirements.get(Constants.BASE_HARDWOOD_SAPLING_ID));
        assertEquals(Integer.valueOf(3), requirements.get(Constants.CALQUAT_SAPLING_ID));
        assertEquals(Integer.valueOf(1), requirements.get(Constants.CELASTRUS_SAPLING_ID));
        assertEquals(Integer.valueOf(1), requirements.get(Constants.CRYSTAL_TREE_SAPLING_ID));
        assertEquals(Integer.valueOf(1), requirements.get(Constants.REDWOOD_SAPLING_ID));
        assertEquals(Integer.valueOf(11), requirements.get(ItemID.BUCKET_COMPOST));
        assertEquals(Integer.valueOf(3000), requirements.get(ItemID.COINS));

        assertEquals(Arrays.asList(
                ItemID.PLANTPOT_TEAK_SAPLING,
                ItemID.PLANTPOT_MAHOGANY_SAPLING,
                ItemID.PLANTPOT_CAMPHOR_SAPLING,
                ItemID.PLANTPOT_IRONWOOD_SAPLING,
                ItemID.PLANTPOT_ROSEWOOD_SAPLING), Constants.HARDWOOD_SAPLING_IDS);
        assertEquals(ItemID.PLANTPOT_TEAK_SAPLING, Constants.BASE_HARDWOOD_SAPLING_ID);
        assertEquals(Arrays.asList(
                ItemID.NECKLACE_OF_DIGSITE_1,
                ItemID.NECKLACE_OF_DIGSITE_2,
                ItemID.NECKLACE_OF_DIGSITE_3,
                ItemID.NECKLACE_OF_DIGSITE_4,
                ItemID.NECKLACE_OF_DIGSITE_5), Constants.DIGSITE_PENDANT_IDS);
        assertEquals(Integer.valueOf(1), requirements.get(Constants.BASE_DIGSITE_PENDANT_ID));
    }

    @Test
    public void keepsCompostOnlyForCrystalWhenProtectionIsEnabled() {
        Map<Integer, Integer> requirements = requirements(true);

        assertEquals(Integer.valueOf(1), requirements.get(ItemID.BUCKET_COMPOST));
        assertEquals(Integer.valueOf(3000), requirements.get(ItemID.COINS));
    }

    @Test
    public void normalizesEveryQuetzalWhistleVariant() {
        assertTrue(Constants.isQuetzalWhistle(ItemID.HG_QUETZALWHISTLE_PERFECTED_INFINITE));

        List<RunLocation> locations = Arrays.asList(
                location("Locus Oasis", "Quetzal_whistle", PatchTypes.HARDWOOD));
        Map<Integer, Integer> requirements = CustomRunItemRequirements.buildRequirements(
                catalog, new EasyFarmingConfig() { }, null, locations, false, false, false);

        assertEquals(Integer.valueOf(1), requirements.get(ItemID.HG_QUETZALWHISTLE_BASIC));
    }

    private Map<Integer, Integer> requirements(boolean payForProtection) {
        EasyFarmingConfig config = new EasyFarmingConfig() {
            @Override
            public OptionEnumCompost enumConfigCompost() {
                return OptionEnumCompost.Compost;
            }

            @Override
            public boolean generalPayForProtection() {
                return payForProtection;
            }
        };
        return CustomRunItemRequirements.buildRequirements(catalog, config, null, allSpecialLocations(), false, false, false);
    }

    private static List<RunLocation> allSpecialLocations() {
        return Arrays.asList(
                location("Fossil Island", "Digsite_pendant", PatchTypes.HARDWOOD),
                location("Locus Oasis", "None", PatchTypes.HARDWOOD),
                location("Anglers' Retreat", "None", PatchTypes.HARDWOOD),
                location("Tai Bwo Wannai", "None", PatchTypes.CALQUAT),
                location("Kastori", "None", PatchTypes.CALQUAT),
                location("Great Conch", "None", PatchTypes.CALQUAT),
                location("Farming Guild", "None", PatchTypes.CELASTRUS),
                location("Prifddinas", "None", PatchTypes.CRYSTAL_TREE),
                location("Farming Guild", "None", PatchTypes.REDWOOD));
    }

    private static RunLocation location(String name, String teleport, String patchType) {
        return new RunLocation(name, teleport, Arrays.asList(patchType));
    }

    private static class TestPlugin extends EasyFarmingPlugin {
        private final EasyFarmingConfig config = new EasyFarmingConfig() { };

        @Override
        public EasyFarmingConfig getConfig() {
            return config;
        }
    }
}
