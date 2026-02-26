package com.easyfarming.ui;

import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.EasyFarmingPanel;
import com.easyfarming.StartStopJButton;
import com.easyfarming.customrun.CustomRun;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * One custom run row in the overview: name + Start, click to open detail.
 */
public class CustomRunOverviewRow extends JPanel {
    private final EasyFarmingPlugin plugin;
    private final EasyFarmingPanel parentPanel;
    private final CustomRun customRun;
    private final StartStopJButton startStopButton;

    public CustomRunOverviewRow(EasyFarmingPlugin plugin, EasyFarmingPanel parentPanel, CustomRun customRun) {
        this.plugin = plugin;
        this.parentPanel = parentPanel;
        this.customRun = customRun;

        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        startStopButton = new StartStopJButton(customRun.getName());
        startStopButton.setPreferredSize(new Dimension(80, 25));
        startStopButton.addActionListener(e -> {
            boolean toggleToStop = startStopButton.getText().equals("Start");
            startStopButton.setStartStopState(toggleToStop);
            if (toggleToStop) {
                parentPanel.startCustomRun(customRun);
            } else {
                plugin.getFarmingTeleportOverlay().removeOverlay();
                plugin.setOverlayActive(false);
            }
        });
        syncStartButtonState();

        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        JLabel nameLabel = new JLabel(customRun.getName() != null ? customRun.getName() : "New Run");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(net.runelite.client.ui.FontManager.getRunescapeBoldFont());
        container.add(nameLabel, BorderLayout.WEST);
        container.add(startStopButton, BorderLayout.EAST);
        add(container, BorderLayout.CENTER);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(ColorScheme.DARK_GRAY_HOVER_COLOR);
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(ColorScheme.DARKER_GRAY_COLOR);
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    Component src = (Component) e.getSource();
                    if (src == startStopButton || SwingUtilities.isDescendingFrom(src, startStopButton)) {
                        return;
                    }
                    parentPanel.showRunDetail(customRun);
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    showContextMenu(e);
                }
            }
        };
        addMouseListener(mouseAdapter);
        nameLabel.addMouseListener(mouseAdapter);
        container.addMouseListener(mouseAdapter);
    }

    private void showContextMenu(MouseEvent e) {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem renameItem = new JMenuItem("Rename");
        renameItem.addActionListener(ev -> renameRun());
        popup.add(renameItem);
        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(ev -> deleteRun());
        popup.add(deleteItem);
        popup.show(e.getComponent(), e.getX(), e.getY());
    }

    private void renameRun() {
        String currentName = customRun.getName() != null ? customRun.getName() : "";
        String newName = (String) JOptionPane.showInputDialog(this, "Run name:", "Rename custom run", JOptionPane.PLAIN_MESSAGE, null, null, currentName);
        if (newName == null) return;
        newName = newName.trim();
        if (newName.isEmpty()) return;
        customRun.setName(newName);
        plugin.getCustomRunStorage().save(plugin.getCustomRunStorage().load());
        parentPanel.refreshOverviewList();
    }

    private void deleteRun() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete \"" + (customRun.getName() != null ? customRun.getName() : "run") + "\"?",
                "Delete custom run",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        List<CustomRun> runs = plugin.getCustomRunStorage().load();
        runs.removeIf(r -> r == customRun || (customRun.getName() != null && customRun.getName().equals(r.getName())));
        plugin.getCustomRunStorage().save(runs);
        parentPanel.refreshOverviewList();
    }

    private void syncStartButtonState() {
        if (plugin.getFarmingTeleportOverlay().isCustomRunMode()
                && customRun.getName() != null
                && customRun.getName().equals(plugin.getFarmingTeleportOverlay().getActiveCustomRunName())) {
            startStopButton.setStartStopState(true);
        }
    }
}
