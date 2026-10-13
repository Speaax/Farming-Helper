package com.easyfarming.overlays.handlers.patch;

import com.easyfarming.utils.Constants;
import org.junit.Test;

import java.util.OptionalInt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

/**
 * Location / region helpers and herb varbit fallback mapping.
 */
public class PatchLocationResolverTest {

    @Test
    public void resolveRegionIdNullClientIsEmpty() {
        OptionalInt region = PatchLocationResolver.resolveRegionId(null);
        assertFalse(region.isPresent());
    }

    @Test
    public void herbLocationDelegatesToRegistry() {
        assertEquals("Farming Guild", PatchLocationResolver.herbLocationName(Constants.REGION_FARMING_GUILD));
        assertEquals("Unknown", PatchLocationResolver.herbLocationName(0));
    }

    @Test
    public void hopsLocationDelegatesToRegistry() {
        assertEquals("Yanille", PatchLocationResolver.hopsLocationName(Constants.REGION_YANILLE));
        assertEquals("Unknown", PatchLocationResolver.hopsLocationName(0));
    }

    @Test
    public void treeLocationDelegatesToRegistry() {
        assertEquals("Falador", PatchLocationResolver.treeLocationName(Constants.REGION_FALADOR_PARK));
        assertNull(PatchLocationResolver.treeLocationName(0));
    }

    @Test
    public void herbVarbitFallbackMapsSpecialRegions() {
        assertEquals(Constants.VARBIT_HERB_PATCH_FARMING_GUILD,
                PatchLocationResolver.herbVarbitFallbackByRegion(Constants.REGION_FARMING_GUILD));
        assertEquals(Constants.VARBIT_HERB_PATCH_HARMONY,
                PatchLocationResolver.herbVarbitFallbackByRegion(Constants.REGION_HARMONY));
        assertEquals(Constants.VARBIT_HERB_PATCH_TROLL_WEISS,
                PatchLocationResolver.herbVarbitFallbackByRegion(Constants.REGION_TROLL_STRONGHOLD));
        assertEquals(Constants.VARBIT_HERB_PATCH_TROLL_WEISS,
                PatchLocationResolver.herbVarbitFallbackByRegion(Constants.REGION_WEISS));
        assertEquals(Constants.VARBIT_HERB_PATCH_STANDARD,
                PatchLocationResolver.herbVarbitFallbackByRegion(Constants.REGION_FALADOR));
        assertEquals(Constants.VARBIT_HERB_PATCH_STANDARD,
                PatchLocationResolver.herbVarbitFallbackByRegion(Constants.REGION_ARDOUGNE));
    }
}
