package com.easyfarming.ui;

import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.EasyFarmingPanel;
import com.easyfarming.customrun.CustomRun;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Overview list: "Custom runs" section with saved custom runs and "+ New custom run" card.
 */
public class OverviewPanel extends JPanel {
    private final EasyFarmingPlugin plugin;
    private final EasyFarmingPanel parentPanel;
    private final JPanel contentPanel;
    private boolean creatingRun;

    public OverviewPanel(EasyFarmingPlugin plugin, EasyFarmingPanel parentPanel) {
        this.plugin = plugin;
        this.parentPanel = parentPanel;

        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        rebuildList();

        add(contentPanel, BorderLayout.NORTH);
    }

    public void rebuildList() {
        contentPanel.removeAll();

        JPanel customTitlePanel = new JPanel(new BorderLayout());
        customTitlePanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        customTitlePanel.setBorder(new EmptyBorder(0, 0, 8, 0));
        JLabel customTitle = new JLabel("Custom runs:");
        customTitle.setForeground(Color.WHITE);
        customTitlePanel.add(customTitle, BorderLayout.WEST);
        contentPanel.add(customTitlePanel);

        List<CustomRun> customRuns = plugin.getCustomRunStorage().load();
        for (CustomRun run : customRuns) {
            contentPanel.add(new CustomRunOverviewRow(plugin, parentPanel, run));
            contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JPanel addCard = new JPanel(new BorderLayout());
        addCard.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        addCard.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel addLabel = new JLabel("+ New custom run");
        addLabel.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        addCard.add(addLabel, BorderLayout.CENTER);
        addCard.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addCard.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (creatingRun) {
                    return;
                }
                creatingRun = true;
                try {
                    String name = (String) JOptionPane.showInputDialog(
                            OverviewPanel.this,
                            "Enter a name for the new run:",
                            "New custom run",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            null,
                            "New Run");
                    if (name == null) return;
                    name = name.trim();
                    if (name.isEmpty()) name = "New Run";
                    CustomRun newRun = new CustomRun(name, new java.util.ArrayList<>());
                    List<CustomRun> runs = plugin.getCustomRunStorage().load();
                    runs.add(newRun);
                    plugin.getCustomRunStorage().save(runs);
                    parentPanel.showRunDetail(newRun);
                } finally {
                    creatingRun = false;
                }
            }
        });
        contentPanel.add(addCard);

        revalidate();
        repaint();
    }
}
