package com.easyfarming.ui;

import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.customrun.LocationCatalog;
import com.easyfarming.customrun.PatchTypes;
import com.easyfarming.customrun.RunLocation;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.game.ItemManager;
import net.runelite.api.gameval.ItemID;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * One editable RunLocation row: location dropdown and Remove; patch icons (toggleable); teleport dropdown below icons.
 */
public class CustomRunLocationRowPanel extends JPanel {
    private static final int PATCH_ICON_SIZE = 36;

    private final EasyFarmingPlugin plugin;
    private final ItemManager itemManager;
    private final RunLocation runLocation;
    private final Runnable onRemove;
    private final Runnable onChanged;

    private final JComboBox<String> locationCombo;
    private final JComboBox<String> teleportCombo;
    private final JPanel patchIconsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
    private final List<JButton> patchIconButtons = new ArrayList<>();

    public CustomRunLocationRowPanel(EasyFarmingPlugin plugin, ItemManager itemManager, RunLocation runLocation,
                                    Runnable onRemove, Runnable onChanged) {
        this.plugin = plugin;
        this.itemManager = itemManager;
        this.runLocation = runLocation;
        this.onRemove = onRemove;
        this.onChanged = onChanged;

        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setBorder(new EmptyBorder(5, 10, 5, 10));

        LocationCatalog catalog = plugin.getLocationCatalog();
        List<String> allNames = catalog.getAllLocationNames();

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        topRow.setOpaque(false);

        JLabel locLabel = new JLabel("Location");
        locLabel.setForeground(Color.WHITE);
        topRow.add(locLabel);
        locationCombo = new JComboBox<>(allNames.toArray(new String[0]));
        locationCombo.setRenderer(enumRenderer());
        locationCombo.setSelectedItem(runLocation.getLocationName());
        if (locationCombo.getSelectedItem() == null && !allNames.isEmpty()) {
            locationCombo.setSelectedIndex(0);
        }
        locationCombo.addActionListener(e -> onLocationSelected());
        topRow.add(locationCombo);

        JButton removeBtn = new JButton("Remove");
        removeBtn.setFocusable(false);
        removeBtn.addActionListener(e -> {
            if (onRemove != null) onRemove.run();
        });
        topRow.add(removeBtn);

        add(topRow, BorderLayout.NORTH);

        patchIconsPanel.setOpaque(false);
        add(patchIconsPanel, BorderLayout.CENTER);

        JPanel teleportRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        teleportRow.setOpaque(false);
        JLabel teleLabel = new JLabel("Teleport");
        teleLabel.setForeground(Color.WHITE);
        teleportRow.add(teleLabel);
        teleportCombo = new JComboBox<>();
        refreshTeleportOptions();
        teleportCombo.setRenderer(enumRenderer());
        teleportCombo.addActionListener(e -> {
            Object sel = teleportCombo.getSelectedItem();
            if (sel != null) runLocation.setTeleportOption((String) sel);
            if (onChanged != null) onChanged.run();
        });
        teleportRow.add(teleportCombo);
        add(teleportRow, BorderLayout.SOUTH);

        onLocationSelected();
    }

    private void onLocationSelected() {
        Object sel = locationCombo.getSelectedItem();
        String name = sel != null ? (String) sel : null;
        runLocation.setLocationName(name);
        refreshTeleportOptions();
        refreshPatchIcons();
        if (onChanged != null) onChanged.run();
    }

    private void refreshTeleportOptions() {
        LocationCatalog catalog = plugin.getLocationCatalog();
        String name = runLocation.getLocationName();
        List<String> opts = name != null ? catalog.getTeleportOptionsForLocation(name) : new ArrayList<>();
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
        patchIconButtons.clear();
        LocationCatalog catalog = plugin.getLocationCatalog();
        String name = runLocation.getLocationName();
        List<String> available = name != null ? catalog.getPatchTypesAtLocation(name) : new ArrayList<>();
        List<String> selected = runLocation.getPatchTypes() != null ? runLocation.getPatchTypes() : new ArrayList<>();
        for (String patchType : available) {
            JButton iconBtn = makePatchIconButton(patchType, selected.contains(patchType));
            patchIconButtons.add(iconBtn);
            patchIconsPanel.add(iconBtn);
        }
        patchIconsPanel.revalidate();
        patchIconsPanel.repaint();
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
            case PatchTypes.HERB:
                return ItemID.UNIDENTIFIED_RANARR;
            case PatchTypes.FLOWER:
                return ItemID.LIMPWURT_ROOT;
            case PatchTypes.ALLOTMENT:
                return ItemID.WATERMELON;
            case PatchTypes.TREE:
                return ItemID.PLANTPOT_OAK_SAPLING;
            case PatchTypes.FRUIT_TREE:
                return ItemID.PLANTPOT_APPLE_SAPLING;
            case PatchTypes.HOPS:
                return ItemID.BARLEY;
            default:
                return ItemID.UNIDENTIFIED_RANARR;
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

    private static DefaultListCellRenderer enumRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) ((JLabel) c).setText(((String) value).replace("_", " "));
                return c;
            }
        };
    }
}
