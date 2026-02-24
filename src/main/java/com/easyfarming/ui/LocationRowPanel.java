package com.easyfarming.ui;

import com.easyfarming.core.Location;
import com.easyfarming.core.Teleport;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * One location row in the run detail view (same style as LocationConfigPanel):
 * collapsible header with location name, body with teleport dropdown.
 * Uses master's Location only; teleport dropdown shows options and current selection.
 */
public class LocationRowPanel extends JPanel {
    private final Location location;
    private final JPanel headerPanel;
    private final JPanel mainBodyContainer;
    private boolean isExpanded = true;
    private final JLabel toggleLabel;

    public LocationRowPanel(Location location) {
        this.location = location;

        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, ColorScheme.DARK_GRAY_COLOR));

        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
        headerPanel.setBorder(new EmptyBorder(5, 10, 5, 10));

        JLabel nameLabel = new JLabel(location.getName());
        nameLabel.setFont(FontManager.getRunescapeBoldFont());
        nameLabel.setForeground(Color.WHITE);
        headerPanel.add(nameLabel, BorderLayout.WEST);

        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        controlsPanel.setOpaque(false);
        toggleLabel = new JLabel("−");
        toggleLabel.setFont(FontManager.getRunescapeBoldFont());
        toggleLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        toggleLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        controlsPanel.add(toggleLabel);
        headerPanel.add(controlsPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        mainBodyContainer = new JPanel(new BorderLayout());
        mainBodyContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        mainBodyContainer.setBorder(new EmptyBorder(10, 10, 10, 10));

        List<String> teleportNames = location.getTeleportOptions() == null
                ? new ArrayList<>()
                : location.getTeleportOptions().stream()
                        .map(Teleport::getEnumOption)
                        .collect(Collectors.toList());

        if (!teleportNames.isEmpty()) {
            JPanel telePanel = new JPanel(new BorderLayout());
            telePanel.setOpaque(false);
            telePanel.setBorder(new EmptyBorder(10, 0, 0, 0));
            JLabel teleLabel = new JLabel("Teleport");
            teleLabel.setForeground(Color.WHITE);
            JComboBox<String> teleCombo = new JComboBox<>(teleportNames.toArray(new String[0]));
            teleCombo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value != null) {
                        ((JLabel) c).setText(((String) value).replace("_", " "));
                    }
                    return c;
                }
            });
            Teleport selected = location.getSelectedTeleport();
            if (selected != null) {
                teleCombo.setSelectedItem(selected.getEnumOption());
            } else {
                teleCombo.setSelectedIndex(0);
            }
            telePanel.add(teleLabel, BorderLayout.WEST);
            telePanel.add(teleCombo, BorderLayout.EAST);
            mainBodyContainer.add(telePanel, BorderLayout.SOUTH);
        }

        add(mainBodyContainer, BorderLayout.CENTER);

        MouseAdapter toggleAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                isExpanded = !isExpanded;
                mainBodyContainer.setVisible(isExpanded);
                toggleLabel.setText(isExpanded ? "−" : "+");
                revalidate();
                repaint();
            }
        };
        headerPanel.addMouseListener(toggleAdapter);
        nameLabel.addMouseListener(toggleAdapter);
        toggleLabel.addMouseListener(toggleAdapter);
    }

    public JPanel getHeaderPanel() {
        return headerPanel;
    }
}
