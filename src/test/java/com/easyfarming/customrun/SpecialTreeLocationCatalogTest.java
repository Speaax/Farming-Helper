package com.easyfarming.customrun;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.core.Location;
import com.easyfarming.core.Teleport;
import com.easyfarming.utils.Constants;
import java.util.Arrays;
import java.util.List;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.ObjectID;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SpecialTreeLocationCatalogTest {

    private final LocationCatalog catalog = new LocationCatalog(new TestPlugin());

    @Test
    public void registersAllSpecialTreeLocationsAndPatchTypesInStableRows() {
        List<String> names = catalog.getAllLocationNames();

        assertTrue(names.containsAll(Arrays.asList(
                "Fossil Island", "Locus Oasis", "Anglers' Retreat", "Tai Bwo Wannai",
                "Kastori", "Great Conch", "Farming Guild", "Prifddinas")));
        assertEquals(8, names.stream().filter(name -> Arrays.asList(
                "Fossil Island", "Locus Oasis", "Anglers' Retreat", "Tai Bwo Wannai",
                "Kastori", "Great Conch", "Farming Guild", "Prifddinas").contains(name)).count());

        assertRegistered("Fossil Island", PatchTypes.HARDWOOD);
        assertRegistered("Locus Oasis", PatchTypes.HARDWOOD);
        assertRegistered("Anglers' Retreat", PatchTypes.HARDWOOD);
        assertRegistered("Tai Bwo Wannai", PatchTypes.CALQUAT);
        assertRegistered("Kastori", PatchTypes.CALQUAT);
        assertRegistered("Great Conch", PatchTypes.CALQUAT);
        assertRegistered("Farming Guild", PatchTypes.CELASTRUS);
        assertRegistered("Prifddinas", PatchTypes.CRYSTAL_TREE);
        assertRegistered("Farming Guild", PatchTypes.REDWOOD);

        assertEquals(0, names.indexOf("Farming Guild"));
        assertEquals(18, names.indexOf("Kastori"));
    }

    @Test
    public void specialLocationsProvidePracticalOptionsEndingInNone() {
        assertOptions("Fossil Island", PatchTypes.HARDWOOD, "Digsite_pendant", "Mounted_Digsite_pendant", "None");
        assertOptions("Locus Oasis", PatchTypes.HARDWOOD, "Colossal_Wyrm_Teleport", "Fairy_Ring", "Quetzal_whistle", "Quetzal_Transport", "None");
        assertOptions("Anglers' Retreat", PatchTypes.HARDWOOD, "Fishing_Trawler", "Rimmington_POH", "Sailors_amulet", "Mythical_cape", "Mounted_Mythical_cape", "Spirit_Tree", "None");
        assertOptions("Tai Bwo Wannai", PatchTypes.CALQUAT, "Tai_Bwo_Wannai_Teleport", "Brimhaven_POH_Tablet", "Fairy_Ring", "None");
        assertOptions("Kastori", PatchTypes.CALQUAT, "Quetzal_Transport", "Pendant_of_Ates", "None");
        assertOptions("Great Conch", PatchTypes.CALQUAT, "Fairy_Ring", "None");
        assertOptions("Farming Guild", PatchTypes.CELASTRUS, "Jewellery_box", "Skills_Necklace", "Spirit_Tree", "Fairy_Ring", "Farming_Skillcape", "None");
        assertOptions("Prifddinas", PatchTypes.CRYSTAL_TREE, "Teleport_crystal", "Spirit_Tree", "None");
        assertOptions("Farming Guild", PatchTypes.REDWOOD, "Jewellery_box", "Skills_Necklace", "Spirit_Tree", "Fairy_Ring", "Farming_Skillcape", "None");
    }

    @Test
    public void reusedLocationsKeepTheirExistingTransportOptionLists() {
        assertEquals(options("Farming Guild", PatchTypes.TREE), options("Farming Guild", PatchTypes.CELASTRUS));
        assertEquals(options("Farming Guild", PatchTypes.TREE), options("Farming Guild", PatchTypes.REDWOOD));
        assertEquals(options("Kastori", PatchTypes.FRUIT_TREE), options("Kastori", PatchTypes.CALQUAT));
    }

    @Test
    public void itemTeleportsExposeTheirRequiredInventoryItems() {
        assertItemRequirement("Fossil Island", PatchTypes.HARDWOOD, "Digsite_pendant", Constants.BASE_DIGSITE_PENDANT_ID);
        assertItemRequirement("Locus Oasis", PatchTypes.HARDWOOD, "Colossal_Wyrm_Teleport", ItemID.TELEPORTSCROLL_COLOSSAL_WYRM);
        assertItemRequirement("Locus Oasis", PatchTypes.HARDWOOD, "Quetzal_whistle", ItemID.HG_QUETZALWHISTLE_BASIC);
        assertItemRequirement("Anglers' Retreat", PatchTypes.HARDWOOD, "Sailors_amulet", ItemID.SAILORS_AMULET);
        assertItemRequirement("Anglers' Retreat", PatchTypes.HARDWOOD, "Mythical_cape", ItemID.MYTHICAL_CAPE);
        assertItemRequirement("Tai Bwo Wannai", PatchTypes.CALQUAT, "Tai_Bwo_Wannai_Teleport", ItemID.TELEPORTSCROLL_TAIBWO);
        assertItemRequirement("Tai Bwo Wannai", PatchTypes.CALQUAT, "Brimhaven_POH_Tablet", ItemID.NZONE_TELETAB_BRIMHAVEN);
        assertItemRequirement("Kastori", PatchTypes.CALQUAT, "Pendant_of_Ates", ItemID.PENDANT_OF_ATES);
        assertItemRequirement("Prifddinas", PatchTypes.CRYSTAL_TREE, "Teleport_crystal", Constants.BASE_TELEPORT_CRYSTAL_ID);
    }

    @Test
    public void reusedTransportOptionsTargetTheSpecialPatchPoints() {
        assertEquals(Constants.KASTORI_CALQUAT_PATCH_POINT,
                teleport("Kastori", PatchTypes.CALQUAT, "Quetzal_Transport").getPoint());
        assertEquals(Constants.KASTORI_CALQUAT_PATCH_POINT,
                teleport("Kastori", PatchTypes.CALQUAT, "Pendant_of_Ates").getPoint());
        assertEquals(Constants.FARMING_GUILD_CELASTRUS_PATCH_POINT,
                teleport("Farming Guild", PatchTypes.CELASTRUS, "Skills_Necklace").getPoint());
        assertEquals(Constants.FARMING_GUILD_REDWOOD_PATCH_POINT,
                teleport("Farming Guild", PatchTypes.REDWOOD, "Spirit_Tree").getPoint());
    }

    @Test
    public void kastoriCalquatTransportDescriptionsIdentifyTheCalquatPatch() {
        assertCalquatDescription("Quetzal_Transport");
        assertCalquatDescription("Pendant_of_Ates");
    }

    @Test
    public void hardwoodTransportDescriptionsNameTheActualRoute() {
        assertDescriptionContains("Fossil Island", "Digsite_pendant", "Barge guard");
        assertDescriptionContains("Locus Oasis", "Quetzal_Transport", "Colossal Wyrm Remains");
        assertDescriptionContains("Locus Oasis", "Quetzal_whistle", "Colossal Wyrm Remains");
        assertDescriptionContains("Anglers' Retreat", "Fishing_Trawler", "15 Fishing");
        assertDescriptionContains("Anglers' Retreat", "Fishing_Trawler", "Port Khazard");
        assertDescriptionContains("Anglers' Retreat", "Fishing_Trawler", "Corsair Cove");
        assertDescriptionContains("Anglers' Retreat", "Rimmington_POH", "house is in Rimmington");
        assertDescriptionContains("Anglers' Retreat", "Rimmington_POH", "Cabin Boy Colin");
        assertDescriptionContains("Anglers' Retreat", "Rimmington_POH", "Corsair Cove");
        assertDescriptionContains("Anglers' Retreat", "Rimmington_POH", "The Corsair Curse");
        assertDescriptionDoesNotContain("Anglers' Retreat", "Rimmington_POH", "charter");
        assertDescriptionDoesNotContain("Anglers' Retreat", "Rimmington_POH", "Port Sarim");
        assertDescriptionContains("Anglers' Retreat", "Sailors_amulet", "The Pandemonium");
        assertDescriptionContains("Anglers' Retreat", "Sailors_amulet", "Deepfin Point");
        assertDescriptionContains("Anglers' Retreat", "Spirit_Tree", "Feldip Hills");
    }

    @Test
    public void mountedPohOptionsUseTheConfiguredHouseTeleport() {
        Teleport mountedDigsite = teleport("Fossil Island", PatchTypes.HARDWOOD, "Mounted_Digsite_pendant");
        Teleport mountedMythical = teleport("Anglers' Retreat", PatchTypes.HARDWOOD, "Mounted_Mythical_cape");
        Teleport rimmingtonPoh = teleport("Anglers' Retreat", PatchTypes.HARDWOOD, "Rimmington_POH");

        assertEquals(Teleport.Category.MOUNTED_POH, mountedDigsite.getCategory());
        assertEquals(Teleport.Category.MOUNTED_POH, mountedMythical.getCategory());
        assertEquals(Teleport.Category.MOUNTED_POH, rimmingtonPoh.getCategory());
        assertEquals(ObjectID.POH_EXIT_PORTAL, rimmingtonPoh.getId());
        assertEquals(Integer.valueOf(1), mountedDigsite.getItemRequirements().get(ItemID.AIRRUNE));
        assertEquals(Integer.valueOf(1), mountedDigsite.getItemRequirements().get(ItemID.EARTHRUNE));
        assertEquals(Integer.valueOf(1), mountedDigsite.getItemRequirements().get(ItemID.LAWRUNE));
        assertFalse(mountedDigsite.getItemRequirements().containsKey(Constants.BASE_DIGSITE_PENDANT_ID));
        assertFalse(mountedMythical.getItemRequirements().containsKey(ItemID.MYTHICAL_CAPE));
        assertEquals(Integer.valueOf(1), rimmingtonPoh.getItemRequirements().get(ItemID.AIRRUNE));
        assertEquals(Integer.valueOf(1), rimmingtonPoh.getItemRequirements().get(ItemID.EARTHRUNE));
        assertEquals(Integer.valueOf(1), rimmingtonPoh.getItemRequirements().get(ItemID.LAWRUNE));
    }

    @Test
    public void fishingTrawlerUsesTheMinigameTeleportSpell() {
        Teleport fishingTrawler = teleport("Anglers' Retreat", PatchTypes.HARDWOOD, "Fishing_Trawler");

        assertEquals(Teleport.Category.SPELLBOOK, fishingTrawler.getCategory());
        assertEquals(Constants.INTERFACE_MAGIC_SPELLBOOK, fishingTrawler.getInterfaceGroupId());
        assertEquals(Constants.SPELL_CHILD_MINIGAME_TELEPORT, fishingTrawler.getInterfaceChildId());
        assertTrue(fishingTrawler.getItemRequirements().isEmpty());
    }

    private void assertRegistered(String locationName, String patchType) {
        assertNotNull(locationName + " " + patchType, catalog.getLocationForPatch(locationName, patchType));
    }

    private void assertOptions(String locationName, String patchType, String... expected) {
        List<String> options = options(locationName, patchType);
        assertEquals(Arrays.asList(expected), options);
        assertFalse(options.subList(0, options.size() - 1).contains("None"));
        assertEquals("None", options.get(options.size() - 1));
    }

    private List<String> options(String locationName, String patchType) {
        return catalog.getLocationForPatch(locationName, patchType).getTeleportOptions().stream()
                .map(Teleport::getEnumOption)
                .collect(java.util.stream.Collectors.toList());
    }

    private void assertItemRequirement(String locationName, String patchType, String option, int itemId) {
        assertEquals(Integer.valueOf(1), teleport(locationName, patchType, option).getItemRequirements().get(itemId));
    }

    private Teleport teleport(String locationName, String patchType, String option) {
        Location location = catalog.getLocationForPatch(locationName, patchType);
        return location.getTeleportOptions().stream()
                .filter(teleport -> option.equals(teleport.getEnumOption()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Missing " + option + " at " + locationName));
    }

    private void assertCalquatDescription(String option) {
        String description = teleport("Kastori", PatchTypes.CALQUAT, option).getDescription();
        assertTrue(description.contains("calquat patch"));
        assertFalse(description.contains("fruit tree patch"));
    }

    private void assertDescriptionContains(String locationName, String option, String expectedText) {
        assertTrue(teleport(locationName, PatchTypes.HARDWOOD, option).getDescription().contains(expectedText));
    }

    private void assertDescriptionDoesNotContain(String locationName, String option, String unexpectedText) {
        assertFalse(teleport(locationName, PatchTypes.HARDWOOD, option).getDescription().contains(unexpectedText));
    }

    private static class TestPlugin extends EasyFarmingPlugin {
        private final EasyFarmingConfig config = new EasyFarmingConfig() { };

        @Override
        public EasyFarmingConfig getConfig() {
            return config;
        }
    }
}
