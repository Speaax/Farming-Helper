package com.easyfarming.ui;

import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.EasyFarmingPanel;
import com.easyfarming.StartStopJButton;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * One run-type row in the overview list (same style as before): name + Start button, click to open detail.
 */
public class RunOverviewListPanel extends JPanel {
    private final EasyFarmingPlugin plugin;
    private final EasyFarmingPanel parentPanel;
    private final String runName;
    private final StartStopJButton startStopButton;

    public RunOverviewListPanel(EasyFarmingPlugin plugin, EasyFarmingPanel parentPanel, String runName, StartStopJButton startStopButton) {
        this.plugin = plugin;
        this.parentPanel = parentPanel;
        this.runName = runName;
        this.startStopButton = startStopButton;

        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel nameLabel = new JLabel(runName);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(net.runelite.client.ui.FontManager.getRunescapeBoldFont());
        headerPanel.add(nameLabel, BorderLayout.WEST);

        if (startStopButton.getParent() != null) {
            startStopButton.getParent().remove(startStopButton);
        }
        startStopButton.setPreferredSize(new Dimension(80, 25));
        headerPanel.add(startStopButton, BorderLayout.EAST);

        container.add(headerPanel, BorderLayout.NORTH);
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
                    parentPanel.showRunDetail(runName);
                }
            }
        };

        addMouseListener(mouseAdapter);
        nameLabel.addMouseListener(mouseAdapter);
        headerPanel.addMouseListener(mouseAdapter);
        container.addMouseListener(mouseAdapter);
    }
}
