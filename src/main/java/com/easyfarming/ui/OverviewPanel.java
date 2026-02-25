package com.easyfarming.ui;

import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.EasyFarmingPanel;
import com.easyfarming.StartStopJButton;
import com.easyfarming.customrun.CustomRun;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Overview list: "Pick a farm run:" with fixed run-type cards (Herb, Tree, Fruit Tree, Hops)
 * and a "Custom runs" section listing saved custom runs plus "+" to add one.
 */
public class OverviewPanel extends JPanel {
    private static final String[] RUN_TYPES = { "Herb Run", "Tree Run", "Fruit Tree Run", "Hops Run" };

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

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        titlePanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        JLabel title = new JLabel("Pick a farm run:");
        title.setForeground(Color.WHITE);
        titlePanel.add(title, BorderLayout.WEST);
        contentPanel.add(titlePanel);

        com.easyfarming.FarmingTeleportOverlay overlay = plugin.getFarmingTeleportOverlay();
        for (String runName : RUN_TYPES) {
            StartStopJButton startBtn = new StartStopJButton(runName);
            startBtn.setPreferredSize(new Dimension(80, 25));
            startBtn.addActionListener(e -> {
                boolean toggleToStop = startBtn.getText().equals("Start");
                startBtn.setStartStopState(toggleToStop);
                if (toggleToStop) {
                    parentPanel.startRun(runName);
                } else {
                    plugin.getFarmingTeleportOverlay().removeOverlay();
                    plugin.setOverlayActive(false);
                }
            });
            if ("Herb Run".equals(runName) && overlay.herbRun) startBtn.setStartStopState(true);
            else if ("Tree Run".equals(runName) && overlay.treeRun) startBtn.setStartStopState(true);
            else if ("Fruit Tree Run".equals(runName) && overlay.fruitTreeRun) startBtn.setStartStopState(true);
            else if ("Hops Run".equals(runName) && overlay.hopsRun) startBtn.setStartStopState(true);
            contentPanel.add(new RunOverviewListPanel(plugin, parentPanel, runName, startBtn));
            contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JPanel customTitlePanel = new JPanel(new BorderLayout());
        customTitlePanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        customTitlePanel.setBorder(new EmptyBorder(15, 0, 8, 0));
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
                    CustomRun newRun = new CustomRun("New Run", new java.util.ArrayList<>());
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
