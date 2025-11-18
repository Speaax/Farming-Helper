package com.easyfarming;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for AllotmentPatchChecker.
 * Validates varbit ID mappings and bit 7 disease detection logic.
 */
public class AllotmentPatchCheckerTest {
    
    /**
     * Tests that the DISEASED_BIT_MASK constant is correctly set to bit 7 (0x80).
     */
    @Test
    public void testDiseasedBitMask() {
        assertEquals("DISEASED_BIT_MASK should be 0x80 (bit 7)", 0x80, AllotmentPatchChecker.DISEASED_BIT_MASK);
        assertEquals("DISEASED_BIT_MASK should be 128 in decimal", 128, AllotmentPatchChecker.DISEASED_BIT_MASK);
    }
    
    /**
     * Tests the bit 7 disease detection logic directly.
     * Validates that values with bit 7 set are correctly identified as diseased.
     */
    @Test
    public void testDiseasedBitDetection() {
        // Test various varbit values with bit 7 set (diseased)
        int[] diseasedValues = {
            128,  // 0x80 - bit 7 only
            129,  // 0x81 - bit 7 + bit 0
            130,  // 0x82 - bit 7 + bit 1
            192,  // 0xC0 - bit 7 + bit 6
            255   // 0xFF - all bits set
        };
        
        for (int diseasedValue : diseasedValues) {
            boolean isDiseased = (diseasedValue & AllotmentPatchChecker.DISEASED_BIT_MASK) != 0;
            assertTrue(
                "Varbit value " + diseasedValue + " (0x" + Integer.toHexString(diseasedValue) + 
                ") with bit 7 set should be detected as diseased",
                isDiseased
            );
        }
    }
    
    /**
     * Tests that non-diseased states are not incorrectly detected as diseased.
     * Tests various varbit values without bit 7 set.
     */
    @Test
    public void testNonDiseasedBitDetection() {
        // Test various varbit values without bit 7 set (not diseased)
        int[] nonDiseasedValues = {
            0,    // 0x00 - no bits set
            1,    // 0x01 - bit 0 only
            64,   // 0x40 - bit 6 only (needs water state)
            127   // 0x7F - all bits except bit 7
        };
        
        for (int nonDiseasedValue : nonDiseasedValues) {
            boolean isDiseased = (nonDiseasedValue & AllotmentPatchChecker.DISEASED_BIT_MASK) != 0;
            assertFalse(
                "Varbit value " + nonDiseasedValue + " (0x" + Integer.toHexString(nonDiseasedValue) + 
                ") without bit 7 set should NOT be detected as diseased",
                isDiseased
            );
        }
    }
    
    /**
     * Tests that all allotment enum entries have empty diseased lists
     * (since disease detection now uses bit 7 instead of value lists).
     */
    @Test
    public void testAllAllotmentEnumsHaveEmptyDiseasedLists() {
        for (AllotmentPatchChecker.Allotment allotment : AllotmentPatchChecker.Allotment.values()) {
            assertTrue(
                "Allotment " + allotment + " should have an empty diseased list " +
                "(disease detection now uses bit 7 of varbit value)",
                allotment.getDiseased().isEmpty()
            );
        }
    }
    
    /**
     * Tests that the placeholder value 197 has been removed from all allotment entries.
     * This validates that the incorrect placeholder has been replaced.
     */
    @Test
    public void testPlaceholder197Removed() {
        for (AllotmentPatchChecker.Allotment allotment : AllotmentPatchChecker.Allotment.values()) {
            assertFalse(
                "Allotment " + allotment + " should not contain the placeholder value 197",
                allotment.getDiseased().contains(197)
            );
        }
    }
    
    /**
     * Tests that disease detection logic correctly identifies bit 7 across different value ranges.
     * This validates the bitwise AND operation works correctly.
     */
    @Test
    public void testDiseaseDetectionBitwiseLogic() {
        // Test edge cases for bit 7 detection
        int[] testCases = {
            // Value, Expected diseased
            0,      // false - no bits set
            127,    // false - all bits except bit 7
            128,    // true - bit 7 only
            129,    // true - bit 7 + bit 0
            255     // true - all bits set
        };
        
        boolean[] expectedResults = {false, false, true, true, true};
        
        for (int i = 0; i < testCases.length; i++) {
            int value = testCases[i];
            boolean expected = expectedResults[i];
            boolean actual = (value & AllotmentPatchChecker.DISEASED_BIT_MASK) != 0;
            
            assertEquals(
                "Value " + value + " (0x" + Integer.toHexString(value) + 
                ") should " + (expected ? "" : "NOT ") + "be detected as diseased",
                expected,
                actual
            );
        }
    }
}

