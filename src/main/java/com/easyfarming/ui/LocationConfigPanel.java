package com.easyfarming.ui;

import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.api.ItemID;

import com.easyfarming.models.CustomLocation;
import com.easyfarming.ui.components.DragAndDropReorderPane;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class LocationConfigPanel extends JPanel {
    private final CustomLocation location;
    private final DragAndDropReorderPane bodyPanel;
    private final JPanel headerPanel;
    private boolean isExpanded = true;
    private final JLabel toggleLabel;
    private final net.runelite.client.game.ItemManager itemManager;
    private final Runnable saveCallback;

    private JPanel mainBodyContainer;

    public LocationConfigPanel(CustomLocation location, List<String> teleportNames, net.runelite.client.game.ItemManager itemManager, Runnable saveCallback) {
        this.location = location;
        this.itemManager = itemManager;
        this.saveCallback = saveCallback;
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, ColorScheme.DARK_GRAY_COLOR));

        // Header
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
        headerPanel.setBorder(new EmptyBorder(5, 10, 5, 10));

        JLabel nameLabel = new JLabel(location.getName());
        nameLabel.setFont(FontManager.getRunescapeBoldFont());
        nameLabel.setForeground(Color.WHITE);
        headerPanel.add(nameLabel, BorderLayout.WEST);

        // Header controls (Toggle)
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        controlsPanel.setOpaque(false);
        
        toggleLabel = new JLabel("−");
        toggleLabel.setFont(FontManager.getRunescapeBoldFont());
        toggleLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        toggleLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        controlsPanel.add(toggleLabel);

        headerPanel.add(controlsPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Main Body Container
        mainBodyContainer = new JPanel(new BorderLayout());
        mainBodyContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        mainBodyContainer.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Patches Reorder Pane
        bodyPanel = new DragAndDropReorderPane();
        bodyPanel.setLayout(new com.easyfarming.ui.components.WrapLayout(FlowLayout.LEFT, 5, 5));
        bodyPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        // Patches
        if (location.getEnabledPatches() != null) {
            for (String patch : location.getEnabledPatches()) {
                JPanel pPanel = createPatchPanel(patch);
                Component handle = pPanel.getComponent(0);
                bodyPanel.addDraggableComponent(pPanel, handle);
            }
        }
        
        bodyPanel.setReorderListener(() -> {
            List<String> newOrder = new ArrayList<>();
            for (Component c : bodyPanel.getComponents()) {
                if (c instanceof JPanel && ((JPanel)c).getComponentCount() > 0) {
                    Component inner = ((JPanel)c).getComponent(0);
                    if (inner instanceof JButton) {
                        String patchName = (String) ((JButton)inner).getClientProperty("patchName");
                        if (patchName != null) {
                            newOrder.add(patchName);
                        }
                    }
                }
            }
            if (saveCallback != null && !newOrder.isEmpty()) {
                location.setEnabledPatches(newOrder);
                saveCallback.run();
            }
        });

        mainBodyContainer.add(bodyPanel, BorderLayout.CENTER);

        // Teleport
        if (teleportNames != null && !teleportNames.isEmpty()) {
            JPanel telePanel = new JPanel(new BorderLayout());
            telePanel.setOpaque(false);
            telePanel.setBorder(new EmptyBorder(10, 0, 0, 0));
            JLabel teleLabel = new JLabel("Teleport");
            teleLabel.setForeground(Color.WHITE);
            JComboBox<String> teleCombo = new JComboBox<>(teleportNames.toArray(new String[0]));
            
            if (location.getTeleportOption() != null) {
                teleCombo.setSelectedItem(location.getTeleportOption());
            }
            
            teleCombo.addActionListener(e -> {
                String selected = (String) teleCombo.getSelectedItem();
                location.setTeleportOption(selected);
                if (saveCallback != null) {
                    saveCallback.run();
                }
            });

            telePanel.add(teleLabel, BorderLayout.WEST);
            telePanel.add(teleCombo, BorderLayout.EAST);
            mainBodyContainer.add(telePanel, BorderLayout.SOUTH);
        }

        add(mainBodyContainer, BorderLayout.CENTER);

        // Toggle logic
        MouseAdapter toggleAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                isExpanded = !isExpanded;
                mainBodyContainer.setVisible(isExpanded);
                if (isExpanded) {
                    mainBodyContainer.setBorder(new EmptyBorder(10, 10, 10, 10));
                } else {
                    mainBodyContainer.setBorder(new EmptyBorder(0, 0, 0, 0));
                }
                toggleLabel.setText(isExpanded ? "−" : "+");
                revalidate();
                repaint();
            }
        };
        headerPanel.addMouseListener(toggleAdapter);
        nameLabel.addMouseListener(toggleAdapter);
        toggleLabel.addMouseListener(toggleAdapter);
    }

    private JPanel createPatchPanel(String patchName) {
        JPanel p = new JPanel(new com.easyfarming.ui.components.WrapLayout(FlowLayout.LEFT, 5, 2));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(2, 0, 2, 0));

        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(50, 30));
        btn.setFocusable(false);
        btn.putClientProperty("patchName", patchName);
        
        // Dark Green for selected, Dark Gray for disabled
        Color greenState = new Color(30, 60, 30);
        
        boolean isActive = location.getPatchActiveStates().getOrDefault(patchName, false);
        
        btn.putClientProperty("is_active", isActive);
        btn.setBackground(isActive ? greenState : ColorScheme.DARKER_GRAY_COLOR);
        btn.setToolTipText(patchName);

        if (itemManager != null) {
            int itemId = -1;
            if (patchName.toLowerCase().contains("herb")) itemId = ItemID.GRIMY_GUAM_LEAF;
            else if (patchName.toLowerCase().contains("allotment")) itemId = ItemID.WATERMELON;
            else if (patchName.toLowerCase().contains("flower")) itemId = ItemID.LIMPWURT_ROOT;
            else if (patchName.toLowerCase().contains("hops")) itemId = ItemID.BARLEY;
            else if (patchName.toLowerCase().contains("fruit tree")) itemId = ItemID.PINEAPPLE;
            else if (patchName.toLowerCase().contains("tree")) itemId = ItemID.LOGS;
            
            if (itemId != -1) {
                itemManager.getImage(itemId).addTo(btn);
            } else {
                btn.setText(patchName);
                btn.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
            }
        } else {
            btn.setText(patchName);
            btn.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        }

        btn.addActionListener(e -> {
            boolean active = (boolean) btn.getClientProperty("is_active");
            active = !active;
            btn.putClientProperty("is_active", active);
            btn.setBackground(active ? greenState : ColorScheme.DARKER_GRAY_COLOR);
            
            location.getPatchActiveStates().put(patchName, active);
            if (saveCallback != null) {
                saveCallback.run();
            }
        });

        p.add(btn);
        return p;
    }

    public void refreshPatchButtons() {
        if (bodyPanel == null) return;
        for (Component c : bodyPanel.getComponents()) {
            if (c instanceof JPanel && ((JPanel)c).getComponentCount() > 0) {
                Component inner = ((JPanel)c).getComponent(0);
                if (inner instanceof JButton) {
                    JButton btn = (JButton) inner;
                    String patchName = (String) btn.getClientProperty("patchName");
                    if (patchName != null) {
                        boolean isActive = location.getPatchActiveStates().getOrDefault(patchName, false);
                        btn.putClientProperty("is_active", isActive);
                        Color greenState = new Color(30, 60, 30);
                        btn.setBackground(isActive ? greenState : ColorScheme.DARKER_GRAY_COLOR);
                    }
                }
            }
        }
        revalidate();
        repaint();
    }

    public JPanel getHeaderPanel() {
        return headerPanel;
    }
}
