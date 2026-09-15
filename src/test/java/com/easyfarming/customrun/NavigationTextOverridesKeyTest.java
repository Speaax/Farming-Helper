package com.easyfarming.customrun;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NavigationTextOverridesKeyTest {

    @Test
    public void keySeparatesLocationAndTeleport() {
        assertEquals(
                "Seers Village|Portal_Nexus_Camelot",
                NavigationTextOverrides.key("Seers Village", "Portal_Nexus_Camelot"));
        assertEquals(
                "Catherby|Portal_Nexus_Camelot",
                NavigationTextOverrides.key("Catherby", "Portal_Nexus_Camelot"));
    }

    @Test
    public void keyHandlesNulls() {
        assertEquals("|", NavigationTextOverrides.key(null, null));
        assertEquals("Falador|", NavigationTextOverrides.key("Falador", null));
    }
}
