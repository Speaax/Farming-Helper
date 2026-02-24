package com.easyfarming.ui;

import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.EasyFarmingPanel;
import com.easyfarming.models.CustomRun;
import com.easyfarming.StartStopJButton;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class OverviewPanel extends JPanel {
    private final EasyFarmingPlugin plugin;
    private final EasyFarmingPanel parentPanel;
    private final JPanel contentPanel;

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

        // Create the title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        titlePanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        JLabel title = new JLabel("Pick a farm run:");
        title.setForeground(Color.WHITE);
        titlePanel.add(title, BorderLayout.WEST);

        // Add Create New Run button (+)
        JButton addButton = new JButton("+");
        addButton.setPreferredSize(new Dimension(30, 20));
        addButton.setFocusable(false);
        addButton.setToolTipText("Create a new custom farm run");
        addButton.addActionListener(e -> {
            String newRunName = JOptionPane.showInputDialog(
                    this,
                    "Enter a name for the new farm run:",
                    "New Custom Run",
                    JOptionPane.PLAIN_MESSAGE
            );

            if (newRunName != null && !newRunName.trim().isEmpty()) {
                java.util.Map<String, java.util.List<String>> regionPatchesMap = new java.util.LinkedHashMap<>();
                
                com.easyfarming.runelite.farming.FarmingWorld fw = plugin.getFarmingWorld();
                if (fw != null) {
                    for (java.util.Set<com.easyfarming.runelite.farming.FarmingPatch> patchSet : fw.getTabs().values()) {
                        for (com.easyfarming.runelite.farming.FarmingPatch patch : patchSet) {
                            String regionName = patch.getRegion().getName();
                            String implName = patch.getImplementation().name().toLowerCase();
                            implName = implName.substring(0, 1).toUpperCase() + implName.substring(1).replace("_", " ");
                            String patchName = (patch.getName() + " " + implName).trim();
                            
                            regionPatchesMap.computeIfAbsent(regionName, k -> new ArrayList<>()).add(patchName);
                        }
                    }
                }
                
                java.util.List<com.easyfarming.models.CustomLocation> defaultLocations = new ArrayList<>();
                for (java.util.Map.Entry<String, java.util.List<String>> entry : regionPatchesMap.entrySet()) {
                    defaultLocations.add(new com.easyfarming.models.CustomLocation(entry.getKey(), entry.getValue(), "Select Teleport"));
                }
                
                CustomRun newRun = new CustomRun(newRunName.trim(), defaultLocations);
                plugin.getCustomRunManager().addCustomRun(newRun);
                rebuildList(); // Refresh UI
            }
        });
        titlePanel.add(addButton, BorderLayout.EAST);
        
        contentPanel.add(titlePanel);

        // Add the dynamic custom runs
        List<CustomRun> customRuns = plugin.getCustomRunManager().getCustomRuns();
        if (customRuns != null && !customRuns.isEmpty()) {
            for (CustomRun run : customRuns) {
                // Generate a blank start/stop button for custom runs... it's a stub right now representing the active tracking state
                StartStopJButton customBtn = new StartStopJButton(run.getName());
                customBtn.addActionListener(event -> {
                   boolean toggleToStop = customBtn.getText().equals("Start"); 
                   customBtn.setStartStopState(toggleToStop);
                   if (toggleToStop) {
                       plugin.startCustomRun(run.getName());
                   } else {
                       plugin.getFarmingTeleportOverlay().removeOverlay();
                       plugin.setOverlayActive(false);
                   }
                });

                JPopupMenu popupMenu = new JPopupMenu();
                JMenuItem renameItem = new JMenuItem("Rename");
                renameItem.addActionListener(e -> {
                    String newRunName = JOptionPane.showInputDialog(
                            this,
                            "Enter a new name for the farm run:",
                            run.getName()
                    );
                    if (newRunName != null && !newRunName.trim().isEmpty() && !newRunName.equals(run.getName())) {
                        plugin.getCustomRunManager().renameCustomRun(run.getName(), newRunName.trim());
                        rebuildList();
                    }
                });

                JMenuItem deleteItem = new JMenuItem("Delete");
                deleteItem.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(
                            this,
                            "Are you sure you want to delete '" + run.getName() + "'?",
                            "Confirm Delete",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        plugin.getCustomRunManager().deleteCustomRun(run.getName());
                        rebuildList();
                    }
                });

                popupMenu.add(renameItem);
                popupMenu.add(deleteItem);

                contentPanel.add(new RunOverviewListPanel(plugin, parentPanel, run.getName(), customBtn, popupMenu));
                contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        revalidate();
        repaint();
    }
}

