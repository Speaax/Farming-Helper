package com.easyfarming.ui;

import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.customrun.LocationCatalog;
import com.easyfarming.customrun.PatchTypes;
import com.easyfarming.customrun.RunLocation;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.game.ItemManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * One location as its own sub-panel: location name header (draggable for reorder), patch icons, teleport dropdown below.
 * Header can be collapsed to minimize the panel.
 */
public class CustomRunLocationSubPanel extends JPanel {
    private static final int PATCH_ICON_SIZE = 36;
    private static final int GRIMY_RANARR_WEED = 207;

    private final EasyFarmingPlugin plugin;
    private final ItemManager itemManager;
    private final String locationName;
    private final RunLocation runLocation;
    private final Runnable onChanged;

    private final JComboBox<String> teleportCombo;
    private final JPanel patchIconsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
    private final JPanel contentPanel = new JPanel(new BorderLayout());
    private boolean expanded = false;
    private final JLabel expandCollapseLabel = new JLabel("\u25B6");
    /** When true, programmatic refresh is in progress; do not fire onChanged to avoid re-entry loop. */
    private boolean refreshingFromRun = false;

    public CustomRunLocationSubPanel(EasyFarmingPlugin plugin, ItemManager itemManager, String locationName,
                                    RunLocation runLocation, Runnable onChanged) {
        this(plugin, itemManager, locationName, runLocation, onChanged, null);
    }

    public CustomRunLocationSubPanel(EasyFarmingPlugin plugin, ItemManager itemManager, String locationName,
                                    RunLocation runLocation, Runnable onChanged,
                                    java.util.function.Consumer<String> onDragStart) {
        this.plugin = plugin;
        this.itemManager = itemManager;
        this.locationName = locationName;
        this.runLocation = runLocation;
        this.onChanged = onChanged;

        runLocation.setLocationName(locationName);

        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(ColorScheme.DARK_GRAY_COLOR, 1),
                new EmptyBorder(8, 10, 8, 10)));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        headerPanel.setOpaque(false);

        expandCollapseLabel.setForeground(Color.WHITE);
        expandCollapseLabel.setFont(expandCollapseLabel.getFont().deriveFont(Font.BOLD, 14f));
        expandCollapseLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        expandCollapseLabel.setToolTipText("Expand");
        expandCollapseLabel.setBorder(new EmptyBorder(0, 0, 0, 6));
        expandCollapseLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                toggleExpanded();
            }
        });
        headerPanel.add(expandCollapseLabel);

        if (onDragStart != null) {
            JLabel gripLabel = new JLabel("\u22EE");
            gripLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
            gripLabel.setCursor(new Cursor(Cursor.MOVE_CURSOR));
            gripLabel.setToolTipText("Drag to reorder");
            gripLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mousePressed(java.awt.event.MouseEvent e) {
                    onDragStart.accept(locationName);
                }
            });
            headerPanel.add(gripLabel);
        }
        JLabel nameLabel = new JLabel(locationName);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(FontManager.getRunescapeBoldFont());
        if (onDragStart != null) {
            nameLabel.setCursor(new Cursor(Cursor.MOVE_CURSOR));
            nameLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mousePressed(java.awt.event.MouseEvent e) {
                    onDragStart.accept(locationName);
                }
            });
        }
        headerPanel.add(nameLabel);
        add(headerPanel, BorderLayout.NORTH);

        contentPanel.setOpaque(false);
        patchIconsPanel.setOpaque(false);
        contentPanel.add(patchIconsPanel, BorderLayout.CENTER);

        JPanel teleportRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        teleportRow.setOpaque(false);
        JLabel teleLabel = new JLabel("Teleport");
        teleLabel.setForeground(Color.WHITE);
        teleportRow.add(teleLabel);
        teleportCombo = new JComboBox<>();
        refreshTeleportOptions();
        teleportCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) ((JLabel) c).setText(((String) value).replace("_", " "));
                return c;
            }
        });
        teleportCombo.addActionListener(e -> {
            if (refreshingFromRun) return;
            Object sel = teleportCombo.getSelectedItem();
            if (sel != null) runLocation.setTeleportOption((String) sel);
            if (onChanged != null) onChanged.run();
        });
        teleportRow.add(teleportCombo);
        contentPanel.add(teleportRow, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);

        contentPanel.setVisible(expanded);
        refreshPatchIcons();
    }

    private void toggleExpanded() {
        expanded = !expanded;
        contentPanel.setVisible(expanded);
        expandCollapseLabel.setText(expanded ? "\u25BC" : "\u25B6");
        expandCollapseLabel.setToolTipText(expanded ? "Collapse" : "Expand");
        revalidate();
        repaint();
    }

    public String getLocationName() {
        return locationName;
    }

    private void refreshTeleportOptions() {
        LocationCatalog catalog = plugin.getLocationCatalog();
        List<String> opts = catalog.getTeleportOptionsForLocation(locationName);
        teleportCombo.removeAllItems();
        for (String o : opts) teleportCombo.addItem(o);
        String current = runLocation.getTeleportOption();
        if (current != null && opts.contains(current)) {
            teleportCombo.setSelectedItem(current);
        } else if (!opts.isEmpty()) {
            teleportCombo.setSelectedIndex(0);
            runLocation.setTeleportOption(opts.get(0));
        }
    }

    private void refreshPatchIcons() {
        patchIconsPanel.removeAll();
        LocationCatalog catalog = plugin.getLocationCatalog();
        List<String> available = catalog.getPatchTypesAtLocation(locationName);
        List<String> selected = runLocation.getPatchTypes() != null ? runLocation.getPatchTypes() : new ArrayList<>();
        for (String patchType : available) {
            JButton iconBtn = makePatchIconButton(patchType, selected.contains(patchType));
            patchIconsPanel.add(iconBtn);
        }
        patchIconsPanel.revalidate();
        patchIconsPanel.repaint();
    }

    public void refreshFromRunLocation() {
        refreshingFromRun = true;
        try {
            refreshTeleportOptions();
            refreshPatchIcons();
        } finally {
            refreshingFromRun = false;
        }
    }

    private JButton makePatchIconButton(String patchType, boolean selected) {
        int itemId = itemIdForPatchType(patchType);
        String tooltip = displayName(patchType);
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(PATCH_ICON_SIZE, PATCH_ICON_SIZE));
        btn.setFocusable(false);
        btn.setToolTipText(tooltip);
        btn.setBackground(selected ? new Color(30, 60, 30) : ColorScheme.DARKER_GRAY_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        if (itemManager != null) {
            itemManager.getImage(itemId).addTo(btn);
        } else {
            btn.setText(tooltip);
        }
        btn.addActionListener(e -> {
            List<String> types = runLocation.getPatchTypes();
            if (types.contains(patchType)) {
                types.remove(patchType);
            } else {
                types.add(patchType);
            }
            btn.setBackground(types.contains(patchType) ? new Color(30, 60, 30) : ColorScheme.DARKER_GRAY_COLOR);
            if (onChanged != null) onChanged.run();
        });
        return btn;
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
