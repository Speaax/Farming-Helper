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
                    parentPanel.showRunDetail(customRun);
                }
            }
        };
        addMouseListener(mouseAdapter);
        nameLabel.addMouseListener(mouseAdapter);
        container.addMouseListener(mouseAdapter);
    }

    private void syncStartButtonState() {
        if (plugin.getFarmingTeleportOverlay().isCustomRunMode()
                && customRun.getName() != null
                && customRun.getName().equals(plugin.getFarmingTeleportOverlay().getActiveCustomRunName())) {
            startStopButton.setStartStopState(true);
        }
    }
}
