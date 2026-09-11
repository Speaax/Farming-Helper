package com.easyfarming.customrun;

import java.util.Arrays;
import java.util.List;

/** Patch type keys used in custom runs. */
public final class PatchTypes {
    public static final String HERB = "herb";
    public static final String FLOWER = "flower";
    public static final String ALLOTMENT = "allotment";
    public static final String TREE = "tree";
    public static final String FRUIT_TREE = "fruit_tree";
    public static final String HOPS = "hops";
    public static final String HARDWOOD = "hardwood";
    public static final String CALQUAT = "calquat";
    public static final String CELASTRUS = "celastrus";
    public static final String CRYSTAL_TREE = "crystal_tree";
    public static final String REDWOOD = "redwood";

    public static final List<String> ALL = Arrays.asList(
            HERB, FLOWER, ALLOTMENT, TREE, FRUIT_TREE, HOPS,
            HARDWOOD, CALQUAT, CELASTRUS, CRYSTAL_TREE, REDWOOD);

    private PatchTypes() {}
}
