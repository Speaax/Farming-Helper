package com.easyfarming.utils;

import net.runelite.api.gameval.ItemID;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FairyRingStaffRequirementsTest {

    @Test
    public void lunarStaffIdIs9084() {
        assertEquals(9084, FairyRingStaffRequirements.LUNAR_STAFF);
        assertEquals(ItemID.LUNAR_MOONCLAN_LIMINAL_STAFF, FairyRingStaffRequirements.LUNAR_STAFF);
    }

    @Test
    public void lunarStaffSatisfiesDramenRequirement() {
        Map<Integer, Integer> counts = new HashMap<>();
        counts.put(ItemID.LUNAR_MOONCLAN_LIMINAL_STAFF, 1);
        assertEquals(1, FairyRingStaffRequirements.countFairyRingStaffs(counts));
        assertTrue(FairyRingStaffRequirements.isFairyRingStaff(ItemID.LUNAR_MOONCLAN_LIMINAL_STAFF));
    }

    @Test
    public void unfinishedLunarStagesCount() {
        Map<Integer, Integer> counts = new HashMap<>();
        counts.put(ItemID.DRAMEN_STAFF_WATER, 1);
        assertEquals(1, FairyRingStaffRequirements.countFairyRingStaffs(counts));
    }

    @Test
    public void unrelatedStaffDoesNotCount() {
        Map<Integer, Integer> counts = new HashMap<>();
        counts.put(ItemID.STAFF_OF_AIR, 1);
        assertEquals(0, FairyRingStaffRequirements.countFairyRingStaffs(counts));
        assertFalse(FairyRingStaffRequirements.isFairyRingStaff(ItemID.STAFF_OF_AIR));
    }

    @Test
    public void nullOrEmptyMapIsZero() {
        assertEquals(0, FairyRingStaffRequirements.countFairyRingStaffs(null));
        assertEquals(0, FairyRingStaffRequirements.countFairyRingStaffs(new HashMap<>()));
    }
}
