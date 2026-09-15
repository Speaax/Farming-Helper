package com.easyfarming.ui;

import com.easyfarming.customrun.PatchTypes;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Visit order for multi-patch locations is {@code RunLocation.patchTypes} list order (#101).
 */
public class CustomRunPatchReorderTest {

    @Test
    public void reorderMovesPatchBeforeDropTarget() {
        List<String> types = new ArrayList<>(Arrays.asList(PatchTypes.TREE, PatchTypes.FRUIT_TREE));
        CustomRunLocationSubPanel.reorderPatchTypes(types, PatchTypes.FRUIT_TREE, PatchTypes.TREE);
        assertEquals(Arrays.asList(PatchTypes.FRUIT_TREE, PatchTypes.TREE), types);
    }

    @Test
    public void reorderNoopsWhenTypesMissingOrSame() {
        List<String> types = new ArrayList<>(Arrays.asList(PatchTypes.TREE, PatchTypes.FRUIT_TREE, PatchTypes.HERB));
        CustomRunLocationSubPanel.reorderPatchTypes(types, PatchTypes.TREE, PatchTypes.TREE);
        assertEquals(Arrays.asList(PatchTypes.TREE, PatchTypes.FRUIT_TREE, PatchTypes.HERB), types);

        CustomRunLocationSubPanel.reorderPatchTypes(types, PatchTypes.HOPS, PatchTypes.TREE);
        assertEquals(Arrays.asList(PatchTypes.TREE, PatchTypes.FRUIT_TREE, PatchTypes.HERB), types);
    }

    @Test
    public void reorderMiddlePatchBeforeEarlier() {
        List<String> types = new ArrayList<>(Arrays.asList(
                PatchTypes.HERB, PatchTypes.FLOWER, PatchTypes.ALLOTMENT));
        CustomRunLocationSubPanel.reorderPatchTypes(types, PatchTypes.ALLOTMENT, PatchTypes.HERB);
        assertEquals(Arrays.asList(PatchTypes.ALLOTMENT, PatchTypes.HERB, PatchTypes.FLOWER), types);
    }
}
