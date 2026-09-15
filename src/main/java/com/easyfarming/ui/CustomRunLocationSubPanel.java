package com.easyfarming.ui;

import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.core.Location;
import com.easyfarming.core.Teleport;
import com.easyfarming.customrun.LocationCatalog;
import com.easyfarming.customrun.NavigationTextOverrides;
import com.easyfarming.customrun.PatchTypes;
import com.easyfarming.customrun.RunLocation;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.game.ItemManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * One location as its own sub-panel: location name header (draggable for reorder), patch icons, teleport dropdown below.
 * Selected patch icons show in visit order and can be dragged to reorder; header can be collapsed to minimize the panel.
 */
public class CustomRunLocationSubPanel extends JPanel {
    private static final int PATCH_ICON_SIZE = 36;
    private static final int GRIMY_RANARR_WEED = 207;
    private static final int PATCH_DRAG_THRESHOLD_PX = 6;
    private static final String CLIENT_PROP_PATCH_TYPE = "patchType";

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

    private String draggedPatchType = null;
    private Point patchDragPressPoint = null;
    private boolean patchDragMoved = false;

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
        expandCollapseLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleExpanded();
            }
        });
        headerPanel.add(expandCollapseLabel);

        if (onDragStart != null) {
            JLabel gripLabel = new JLabel("\u22EE");
            gripLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
            gripLabel.setCursor(new Cursor(Cursor.MOVE_CURSOR));
            gripLabel.setToolTipText("Drag to reorder");
            gripLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
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
            nameLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
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

        JButton editNavTextButton = new JButton("\u270E");
        editNavTextButton.setToolTipText("Edit navigation text for this teleport");
        editNavTextButton.setFocusable(false);
        editNavTextButton.setMargin(new Insets(2, 6, 2, 6));
        editNavTextButton.setForeground(Color.WHITE);
        editNavTextButton.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        editNavTextButton.addActionListener(e -> openNavigationTextEditor());
        teleportRow.add(editNavTextButton);

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

    private void openNavigationTextEditor() {
        Object selected = teleportCombo.getSelectedItem();
        if (selected == null) {
            return;
        }
        String teleportOption = (String) selected;
        Teleport teleport = findTeleport(teleportOption);
        if (teleport == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not find teleport details for " + teleportOption.replace('_', ' ') + ".",
                    "Edit navigation text",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        NavigationTextOverrides overrides = plugin.getNavigationTextOverrides();
        String defaultText = teleport.getDescription() != null ? teleport.getDescription() : "";
        String currentText = overrides.resolve(locationName, teleport);

        JTextArea textArea = new JTextArea(currentText, 5, 40);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.add(new JLabel("Navigation text for " + locationName + " · " + teleportOption.replace('_', ' ')), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        Object[] options = {"Save", "Reset to default", "Cancel"};
        int result = JOptionPane.showOptionDialog(
                this,
                panel,
                "Edit navigation text",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);

        if (result == 0) {
            String edited = textArea.getText() != null ? textArea.getText().trim() : "";
            if (edited.isEmpty() || edited.equals(defaultText)) {
                overrides.clearOverride(locationName, teleportOption);
            } else {
                overrides.setOverride(locationName, teleportOption, edited);
            }
        } else if (result == 1) {
            overrides.clearOverride(locationName, teleportOption);
        }
    }

    private Teleport findTeleport(String teleportOption) {
        LocationCatalog catalog = plugin.getLocationCatalog();
        List<String> patchTypes = runLocation.getPatchTypes();
        if (patchTypes != null) {
            for (String patchType : patchTypes) {
                Location loc = catalog.getLocationForPatch(locationName, patchType);
                Teleport match = findTeleportOnLocation(loc, teleportOption);
                if (match != null) {
                    return match;
                }
            }
        }
        List<String> available = catalog.getPatchTypesAtLocation(locationName);
        if (available != null) {
            for (String patchType : available) {
                Location loc = catalog.getLocationForPatch(locationName, patchType);
                Teleport match = findTeleportOnLocation(loc, teleportOption);
                if (match != null) {
                    return match;
                }
            }
        }
        return null;
    }

    private static Teleport findTeleportOnLocation(Location loc, String teleportOption) {
        if (loc == null || teleportOption == null) {
            return null;
        }
        for (Teleport teleport : loc.getTeleportOptions()) {
            if (teleportOption.equals(teleport.getEnumOption())) {
                return teleport;
            }
        }
        return null;
    }

    private void refreshPatchIcons() {
        patchIconsPanel.removeAll();
        LocationCatalog catalog = plugin.getLocationCatalog();
        List<String> available = catalog.getPatchTypesAtLocation(locationName);
        if (available == null) {
            available = new ArrayList<>();
        }
        List<String> selected = runLocation.getPatchTypes() != null
                ? runLocation.getPatchTypes()
                : new ArrayList<>();

        // Selected patches first in visit order, then unselected available types.
        List<String> displayOrder = new ArrayList<>();
        for (String patchType : selected) {
            if (available.contains(patchType)) {
                displayOrder.add(patchType);
            }
        }
        for (String patchType : available) {
            if (!selected.contains(patchType)) {
                displayOrder.add(patchType);
            }
        }

        boolean canReorder = selected.size() > 1;
        for (String patchType : displayOrder) {
            boolean isSelected = selected.contains(patchType);
            patchIconsPanel.add(makePatchIconButton(patchType, isSelected, canReorder && isSelected));
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

    private JButton makePatchIconButton(String patchType, boolean selected, boolean draggable) {
        int itemId = itemIdForPatchType(patchType);
        String tooltip = displayName(patchType);
        if (draggable) {
            tooltip = tooltip + " — drag to reorder visit order";
        }
        JButton btn = new JButton();
        btn.putClientProperty(CLIENT_PROP_PATCH_TYPE, patchType);
        btn.setPreferredSize(new Dimension(PATCH_ICON_SIZE, PATCH_ICON_SIZE));
        btn.setFocusable(false);
        btn.setToolTipText(tooltip);
        btn.setBackground(selected ? new Color(30, 60, 30) : ColorScheme.DARKER_GRAY_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        if (draggable) {
            btn.setCursor(new Cursor(Cursor.MOVE_CURSOR));
        }
        if (itemManager != null) {
            itemManager.getImage(itemId).addTo(btn);
        } else {
            btn.setText(displayName(patchType));
        }

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!SwingUtilities.isLeftMouseButton(e)) {
                    return;
                }
                patchDragPressPoint = e.getPoint();
                patchDragMoved = false;
                draggedPatchType = draggable ? patchType : null;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!SwingUtilities.isLeftMouseButton(e)) {
                    return;
                }
                try {
                    if (draggedPatchType != null && patchDragMoved) {
                        String dropBefore = findPatchTypeAtScreenPoint(e.getLocationOnScreen());
                        if (dropBefore != null && !dropBefore.equals(draggedPatchType)) {
                            reorderPatch(draggedPatchType, dropBefore);
                        }
                    } else if (!patchDragMoved) {
                        togglePatchType(patchType);
                    }
                } finally {
                    draggedPatchType = null;
                    patchDragPressPoint = null;
                    patchDragMoved = false;
                }
            }
        });
        btn.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedPatchType == null || patchDragPressPoint == null) {
                    return;
                }
                int dx = e.getX() - patchDragPressPoint.x;
                int dy = e.getY() - patchDragPressPoint.y;
                if (Math.abs(dx) + Math.abs(dy) >= PATCH_DRAG_THRESHOLD_PX) {
                    patchDragMoved = true;
                }
            }
        });
        return btn;
    }

    private String findPatchTypeAtScreenPoint(Point screenPoint) {
        Point inPanel = new Point(screenPoint);
        SwingUtilities.convertPointFromScreen(inPanel, patchIconsPanel);
        Component at = patchIconsPanel.getComponentAt(inPanel);
        while (at != null && at != patchIconsPanel) {
            Object prop = (at instanceof JComponent)
                    ? ((JComponent) at).getClientProperty(CLIENT_PROP_PATCH_TYPE)
                    : null;
            if (prop instanceof String) {
                List<String> selected = runLocation.getPatchTypes();
                if (selected != null && selected.contains((String) prop)) {
                    return (String) prop;
                }
                return null;
            }
            at = at.getParent();
        }
        return null;
    }

    private void togglePatchType(String patchType) {
        if (refreshingFromRun) {
            return;
        }
        List<String> types = runLocation.getPatchTypes();
        if (types == null) {
            types = new ArrayList<>();
            runLocation.setPatchTypes(types);
        }
        if (types.contains(patchType)) {
            types.remove(patchType);
        } else {
            types.add(patchType);
        }
        refreshPatchIcons();
        if (onChanged != null) {
            onChanged.run();
        }
    }

    /**
     * Moves {@code movedType} so it appears immediately before {@code dropBeforeType} in visit order.
     * Package-visible for unit tests.
     */
    static void reorderPatchTypes(List<String> types, String movedType, String dropBeforeType) {
        if (types == null || movedType == null || dropBeforeType == null) {
            return;
        }
        if (!types.contains(movedType) || !types.contains(dropBeforeType) || movedType.equals(dropBeforeType)) {
            return;
        }
        types.remove(movedType);
        int insertIndex = types.indexOf(dropBeforeType);
        if (insertIndex < 0) {
            types.add(movedType);
        } else {
            types.add(insertIndex, movedType);
        }
    }

    private void reorderPatch(String movedType, String dropBeforeType) {
        List<String> types = runLocation.getPatchTypes();
        if (types == null) {
            return;
        }
        reorderPatchTypes(types, movedType, dropBeforeType);
        refreshPatchIcons();
        if (onChanged != null) {
            onChanged.run();
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
