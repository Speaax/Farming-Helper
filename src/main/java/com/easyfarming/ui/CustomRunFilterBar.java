package com.easyfarming.ui;

import com.easyfarming.customrun.PatchTypes;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.game.ItemManager;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * Filter bar: 2 rows of 3 patch-type icons. Each icon cycles: neutral (0) -> yellow / filter (1) -> green / enable all (2) -> neutral.
 * Yellow = show only locations that have this patch type; green = enable this patch type at all available locations.
 */
public class CustomRunFilterBar extends JPanel {
    private static final int PATCH_ICON_SIZE = 36;
    private static final int FILTER_STATES = 3;
    /** Neutral = off, 1 = yellow (filter), 2 = green (enable everywhere). */
    private final int[] filterStates = new int[PatchTypes.ALL.size()];
    private final JButton[] filterButtons = new JButton[PatchTypes.ALL.size()];
    private final ItemManager itemManager;
    /** patchType, previousState, newState */
    private FilterChangeListener onFilterChanged;

    private static final Color YELLOW_FILTER = new Color(180, 160, 40);
    private static final Color GREEN_ENABLE = new Color(30, 60, 30);
    /** OSRS item id for Grimy ranarr weed. */
    private static final int GRIMY_RANARR_WEED = 207;

    public CustomRunFilterBar(ItemManager itemManager) {
        this.itemManager = itemManager;
        setLayout(new GridLayout(2, 3, 6, 6));
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        List<String> all = PatchTypes.ALL;
        for (int i = 0; i < all.size(); i++) {
            final String patchType = all.get(i);
            final int index = i;
            JButton btn = makeFilterButton(patchType, index);
            filterButtons[index] = btn;
            add(btn);
        }
    }

    public interface FilterChangeListener {
        void onFilterChanged(String patchType, int fromState, int toState);
    }

    public void setOnFilterChanged(FilterChangeListener listener) {
        this.onFilterChanged = listener;
    }

    public int getFilterState(String patchType) {
        int idx = PatchTypes.ALL.indexOf(patchType);
        return idx >= 0 ? filterStates[idx] : 0;
    }

    public boolean isFilterActive(String patchType) {
        return getFilterState(patchType) == 1;
    }

    public boolean isEnableAllActive(String patchType) {
        return getFilterState(patchType) == 2;
    }

    /** True if any filter is yellow (view filter). */
    public boolean hasAnyYellowFilter() {
        for (int s : filterStates) {
            if (s == 1) return true;
        }
        return false;
    }

    /** True if any filter is yellow or green (show location list). */
    public boolean hasAnyYellowOrGreenFilter() {
        for (int s : filterStates) {
            if (s == 1 || s == 2) return true;
        }
        return false;
    }

    /** Patch types that are currently yellow (filter). */
    public java.util.Set<String> getYellowFilterTypes() {
        java.util.Set<String> set = new java.util.LinkedHashSet<>();
        List<String> all = PatchTypes.ALL;
        for (int i = 0; i < filterStates.length; i++) {
            if (filterStates[i] == 1) set.add(all.get(i));
        }
        return set;
    }

    /** Patch types that are yellow or green (for showing which locations to list). */
    public java.util.Set<String> getYellowOrGreenFilterTypes() {
        java.util.Set<String> set = new java.util.LinkedHashSet<>();
        List<String> all = PatchTypes.ALL;
        for (int i = 0; i < filterStates.length; i++) {
            if (filterStates[i] == 1 || filterStates[i] == 2) set.add(all.get(i));
        }
        return set;
    }

    public void setFilterState(String patchType, int state) {
        int idx = PatchTypes.ALL.indexOf(patchType);
        if (idx >= 0 && state >= 0 && state < FILTER_STATES) {
            filterStates[idx] = state;
            updateFilterButtonAppearance(filterButtons[idx], idx);
        }
    }

    private JButton makeFilterButton(String patchType, int index) {
        int itemId = itemIdForPatchType(patchType);
        String tooltip = displayName(patchType) + " (click: filter → enable all → off)";
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(PATCH_ICON_SIZE, PATCH_ICON_SIZE));
        btn.setFocusable(false);
        btn.setToolTipText(tooltip);
        updateFilterButtonAppearance(btn, index);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        if (itemManager != null) {
            itemManager.getImage(itemId).addTo(btn);
        } else {
            btn.setText(displayName(patchType));
        }
        btn.addActionListener(e -> {
            int fromState = filterStates[index];
            filterStates[index] = (filterStates[index] + 1) % FILTER_STATES;
            int toState = filterStates[index];
            updateFilterButtonAppearance(btn, index);
            if (onFilterChanged != null) onFilterChanged.onFilterChanged(patchType, fromState, toState);
        });
        return btn;
    }

    private void updateFilterButtonAppearance(JButton btn, int index) {
        int state = filterStates[index];
        if (state == 0) {
            btn.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        } else if (state == 1) {
            btn.setBackground(YELLOW_FILTER);
        } else {
            btn.setBackground(GREEN_ENABLE);
        }
    }

    private static int itemIdForPatchType(String patchType) {
        switch (patchType) {
            case PatchTypes.HERB: return GRIMY_RANARR_WEED;
            case PatchTypes.FLOWER: return net.runelite.api.gameval.ItemID.LIMPWURT_ROOT;
            case PatchTypes.ALLOTMENT: return net.runelite.api.gameval.ItemID.WATERMELON;
            case PatchTypes.TREE: return net.runelite.api.gameval.ItemID.PLANTPOT_OAK_SAPLING;
            case PatchTypes.FRUIT_TREE: return net.runelite.api.gameval.ItemID.PLANTPOT_APPLE_SAPLING;
            case PatchTypes.HOPS: return net.runelite.api.gameval.ItemID.BARLEY;
            default: return GRIMY_RANARR_WEED;
        }
    }

    private static String displayName(String patchType) {
        switch (patchType) {
            case PatchTypes.HERB: return "Herb";
            case PatchTypes.FLOWER: return "Flower";
            case PatchTypes.ALLOTMENT: return "Allotment";
            case PatchTypes.TREE: return "Tree";
            case PatchTypes.FRUIT_TREE: return "Fruit tree";
            case PatchTypes.HOPS: return "Hops";
            default: return patchType.replace("_", " ");
        }
    }
}
