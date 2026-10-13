package com.easyfarming.locations;

import com.easyfarming.utils.Constants;
import net.runelite.api.coords.WorldPoint;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Region → location name maps for herb/allotment/flower, fruit tree, tree, and hops runs.
 */
public class RegionLocationRegistryTest {

    @Test
    public void herbLocationMapsKnownRegions() {
        assertEquals("Ardougne", RegionLocationRegistry.getHerbLocationName(Constants.REGION_ARDOUGNE));
        assertEquals("Ardougne", RegionLocationRegistry.getHerbLocationName(Constants.REGION_ARDOUGNE_ALT));
        assertEquals("Falador", RegionLocationRegistry.getHerbLocationName(Constants.REGION_FALADOR));
        assertEquals("Farming Guild", RegionLocationRegistry.getHerbLocationName(Constants.REGION_FARMING_GUILD));
        assertEquals("Civitas illa Fortis", RegionLocationRegistry.getHerbLocationName(Constants.REGION_CIVITAS));
    }

    @Test
    public void herbLocationUnknownReturnsUnknown() {
        assertEquals("Unknown", RegionLocationRegistry.getHerbLocationName(0));
    }

    @Test
    public void hopsLocationMapsAllPatches() {
        assertEquals("Lumbridge", RegionLocationRegistry.getHopsLocationName(Constants.REGION_LUMBRIDGE_HOPS));
        assertEquals("Seers Village", RegionLocationRegistry.getHopsLocationName(Constants.REGION_SEERS_VILLAGE));
        assertEquals("Yanille", RegionLocationRegistry.getHopsLocationName(Constants.REGION_YANILLE));
        assertEquals("Entrana", RegionLocationRegistry.getHopsLocationName(Constants.REGION_ENTRANA));
        assertEquals("Aldarin", RegionLocationRegistry.getHopsLocationName(Constants.REGION_ALDARIN));
        assertEquals("Unknown", RegionLocationRegistry.getHopsLocationName(0));
    }

    @Test
    public void fruitTreeLocationMapsKnownRegions() {
        assertEquals("Brimhaven", RegionLocationRegistry.getFruitTreeLocationName(Constants.REGION_ARDOUGNE));
        assertEquals("Catherby", RegionLocationRegistry.getFruitTreeLocationName(Constants.REGION_CATHERBY));
        assertEquals("Lletya", RegionLocationRegistry.getFruitTreeLocationName(Constants.REGION_LLETYA));
        assertEquals("Tree Gnome Village",
                RegionLocationRegistry.getFruitTreeLocationName(Constants.REGION_TREE_GNOME_VILLAGE));
        assertEquals("Kastori", RegionLocationRegistry.getFruitTreeLocationName(Constants.REGION_KASTORI));
        assertNull(RegionLocationRegistry.getFruitTreeLocationName(0));
    }

    @Test
    public void fruitTreeGnomeStrongholdDisambiguatesByDistance() {
        WorldPoint nearVillage = new WorldPoint(2490, 3180, 0);
        WorldPoint nearStronghold = new WorldPoint(2436, 3415, 0);

        assertEquals("Tree Gnome Village",
                RegionLocationRegistry.getFruitTreeLocationName(Constants.REGION_GNOME_STRONGHOLD, nearVillage));
        assertEquals("Gnome Stronghold",
                RegionLocationRegistry.getFruitTreeLocationName(Constants.REGION_GNOME_STRONGHOLD, nearStronghold));
        assertEquals("Gnome Stronghold",
                RegionLocationRegistry.getFruitTreeLocationName(Constants.REGION_GNOME_STRONGHOLD_ALT, nearStronghold));
        // Without player position, default to Gnome Stronghold (same as farther-from-village case)
        assertEquals("Gnome Stronghold",
                RegionLocationRegistry.getFruitTreeLocationName(Constants.REGION_GNOME_STRONGHOLD));
    }

    @Test
    public void treeLocationFaladorParkMapsToFaladorNotTaverley() {
        assertEquals("Falador",
                RegionLocationRegistry.getTreeLocationNameFromRegionId(Constants.REGION_FALADOR_PARK));
        assertEquals("Falador",
                RegionLocationRegistry.getTreeLocationNameFromRegionId(Constants.REGION_FALADOR_PARK_ALT));
        assertEquals("Taverley",
                RegionLocationRegistry.getTreeLocationNameFromRegionId(Constants.REGION_TAVERLEY));
        assertEquals("Taverley",
                RegionLocationRegistry.getTreeLocationNameFromRegionId(Constants.REGION_TAVERLEY_ALT));
    }

    @Test
    public void treeLocationMapsLumbridgeVarrockAndNemus() {
        assertEquals("Lumbridge",
                RegionLocationRegistry.getTreeLocationNameFromRegionId(Constants.REGION_LUMBRIDGE));
        assertEquals("Varrock",
                RegionLocationRegistry.getTreeLocationNameFromRegionId(Constants.REGION_VARROCK));
        assertEquals("Nemus Retreat",
                RegionLocationRegistry.getTreeLocationNameFromRegionId(Constants.REGION_AUBURNVALE));
        assertNull(RegionLocationRegistry.getTreeLocationNameFromRegionId(0));
    }
}
