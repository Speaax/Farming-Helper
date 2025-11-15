package com.easyfarming.locations;

import com.easyfarming.Location;
import com.easyfarming.core.Teleport;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for LocationFactory.
 */
public class LocationFactoryTest {
    
    /**
     * Tests that every enum value in com.easyfarming.core.Teleport.Category
     * has a corresponding mapping in CATEGORY_MAP.
     * This prevents regressions when new enum constants are added.
     */
    @Test
    public void testAllCategoryEnumValuesHaveMapping() {
        for (Teleport.Category category : Teleport.Category.values()) {
            assertNotNull(
                "Category " + category + " does not have a mapping in CATEGORY_MAP. " +
                "Please add a mapping for this enum value.",
                LocationFactory.CATEGORY_MAP.get(category)
            );
        }
    }
    
    /**
     * Tests that all mappings in CATEGORY_MAP are valid and non-null.
     */
    @Test
    public void testAllMappingsAreValid() {
        for (Teleport.Category category : LocationFactory.CATEGORY_MAP.keySet()) {
            Location.TeleportCategory mappedCategory = LocationFactory.CATEGORY_MAP.get(category);
            assertNotNull(
                "Mapping for " + category + " is null",
                mappedCategory
            );
        }
    }
    
    /**
     * Tests that the number of mappings matches the number of enum values.
     * This ensures no enum values are missing.
     */
    @Test
    public void testMappingCountMatchesEnumCount() {
        int enumCount = Teleport.Category.values().length;
        int mappingCount = LocationFactory.CATEGORY_MAP.size();
        assertEquals(
            "Number of mappings (" + mappingCount + ") does not match number of enum values (" + enumCount + "). " +
            "Some enum values may be missing mappings.",
            enumCount,
            mappingCount
        );
    }
}

