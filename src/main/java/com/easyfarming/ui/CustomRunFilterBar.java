package com.easyfarming.ui;

import com.easyfarming.customrun.PatchTypes;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.game.ItemManager;
import net.runelite.api.gameval.ItemID;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Filter bar: tool row (secateurs, dibber, rake) then 2 rows of 3 patch-type icons.
 * Tool icons: grey = not included in run, green = included. Same logic flow as patch filters.
 * Patch icons cycle: neutral (0) -> yellow / filter (1) -> green / enable all (2) -> neutral.
 */
public class CustomRunFilterBar extends JPanel {
    private static final String TOOL_SECATEURS = "secateurs";
    private static final String TOOL_DIBBER = "dibber";
    private static final String TOOL_RAKE = "rake";
    private static final List<String> TOOL_KEYS = java.util.Arrays.asList(TOOL_SECATEURS, TOOL_DIBBER, TOOL_RAKE);
    private static final int PATCH_ICON_SIZE = 36;
    private static final int FILTER_STATES = 3;
    private static final int TOOL_STATES = 2; // 0 = grey (not included), 1 = green (included)
    /** Neutral = off, 1 = yellow (filter), 2 = green (enable everywhere). */
    private final int[] filterStates = new int[PatchTypes.ALL.size()];
    private final JButton[] filterButtons = new JButton[PatchTypes.ALL.size()];
    /** Tool filter: 0 = not included, 1 = included. */
    private final int[] toolStates = new int[TOOL_KEYS.size()];
    private final JButton[] toolButtons = new JButton[TOOL_KEYS.size()];
    private final ItemManager itemManager;
    private FilterChangeListener onFilterChanged;
    private ToolFilterChangeListener onToolFilterChanged;

    private static final Color YELLOW_FILTER = new Color(180, 160, 40);
    private static final Color GREEN_ENABLE = new Color(30, 60, 30);
    private static final int GRIMY_RANARR_WEED = 207;

    public CustomRunFilterBar(ItemManager itemManager) {
        this.itemManager = itemManager;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JPanel toolRow = new JPanel(new GridLayout(1, 3, 6, 6));
        toolRow.setBackground(ColorScheme.DARK_GRAY_COLOR);
        for (int i = 0; i < TOOL_KEYS.size(); i++) {
            final String toolKey = TOOL_KEYS.get(i);
            final int index = i;
            JButton btn = makeToolFilterButton(toolKey, index);
            toolButtons[index] = btn;
            toolRow.add(btn);
        }
        add(toolRow);
        add(Box.createRigidArea(new Dimension(0, 4)));
        JSeparator divider = new JSeparator(SwingConstants.HORIZONTAL);
        divider.setForeground(ColorScheme.MEDIUM_GRAY_COLOR);
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        add(divider);
        add(Box.createRigidArea(new Dimension(0, 4)));

        JPanel patchGrid = new JPanel(new GridLayout(2, 3, 6, 6));
        patchGrid.setBackground(ColorScheme.DARK_GRAY_COLOR);
        List<String> all = PatchTypes.ALL;
        for (int i = 0; i < all.size(); i++) {
            final String patchType = all.get(i);
            final int index = i;
            JButton btn = makeFilterButton(patchType, index);
            filterButtons[index] = btn;
            patchGrid.add(btn);
        }
        add(patchGrid);
    }

    public interface FilterChangeListener {
        void onFilterChanged(String patchType, int fromState, int toState);
    }

    public interface ToolFilterChangeListener {
        void onToolFilterChanged(String toolKey, int fromState, int toState);
    }

    public void setOnFilterChanged(FilterChangeListener listener) {
        this.onFilterChanged = listener;
    }

    public void setOnToolFilterChanged(ToolFilterChangeListener listener) {
        this.onToolFilterChanged = listener;
    }

    public boolean isSecateursIncluded() { return toolStates[TOOL_KEYS.indexOf(TOOL_SECATEURS)] == 1; }
    public boolean isDibberIncluded() { return toolStates[TOOL_KEYS.indexOf(TOOL_DIBBER)] == 1; }
    public boolean isRakeIncluded() { return toolStates[TOOL_KEYS.indexOf(TOOL_RAKE)] == 1; }

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

    private JButton makeToolFilterButton(String toolKey, int index) {
        int itemId = itemIdForTool(toolKey);
        String label = toolDisplayName(toolKey);
        String tooltip = label + " (click: grey = not included, green = included)";
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(PATCH_ICON_SIZE, PATCH_ICON_SIZE));
        btn.setFocusable(false);
        btn.setToolTipText(tooltip);
        updateToolFilterButtonAppearance(btn, index);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        if (itemManager != null) {
            itemManager.getImage(itemId).addTo(btn);
        } else {
            btn.setText(label);
        }
        btn.addActionListener(e -> {
            int fromState = toolStates[index];
            toolStates[index] = (toolStates[index] + 1) % TOOL_STATES;
            int toState = toolStates[index];
            updateToolFilterButtonAppearance(btn, index);
            if (onToolFilterChanged != null) onToolFilterChanged.onToolFilterChanged(toolKey, fromState, toState);
        });
        return btn;
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

    private void updateToolFilterButtonAppearance(JButton btn, int index) {
        int state = toolStates[index];
        if (state == 0) {
            btn.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        } else {
            btn.setBackground(GREEN_ENABLE);
        }
    }

    private static int itemIdForTool(String toolKey) {
        if (TOOL_SECATEURS.equals(toolKey)) return ItemID.FAIRY_ENCHANTED_SECATEURS;
        if (TOOL_DIBBER.equals(toolKey)) return ItemID.DIBBER;
        if (TOOL_RAKE.equals(toolKey)) return ItemID.RAKE;
        return ItemID.RAKE;
    }

    private static String toolDisplayName(String toolKey) {
        if (TOOL_SECATEURS.equals(toolKey)) return "Secateurs";
        if (TOOL_DIBBER.equals(toolKey)) return "Seed dibber";
        if (TOOL_RAKE.equals(toolKey)) return "Rake";
        return toolKey;
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
            case PatchTypes.TREE: return net.runelite.api.gameval.ItemID.YEW_LOGS;
            case PatchTypes.FRUIT_TREE: return net.runelite.api.gameval.ItemID.PINEAPPLE;
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
