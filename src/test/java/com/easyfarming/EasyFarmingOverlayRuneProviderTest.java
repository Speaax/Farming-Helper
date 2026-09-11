package com.easyfarming;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.runelite.api.gameval.ItemID;
import org.junit.Test;

public class EasyFarmingOverlayRuneProviderTest {

    @Test
    public void chargedTomeOfFireProvidesFireRunes() throws Exception {
        Field field = EasyFarmingOverlay.class.getDeclaredField("STAFF_RUNES_MAP");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Integer, List<Integer>> runeProviders = (Map<Integer, List<Integer>>) field.get(null);

        assertEquals(Collections.singletonList(ItemID.FIRERUNE), runeProviders.get(ItemID.TOME_OF_FIRE));
        assertFalse(runeProviders.containsKey(ItemID.TOME_OF_FIRE_UNCHARGED));
    }
}
