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

public class RunOverviewListPanel extends JPanel {
    private final EasyFarmingPlugin plugin;
    private final EasyFarmingPanel parentPanel;
    private final String runName;
    private final StartStopJButton startStopButton;
    private final JPopupMenu popupMenu;

    public RunOverviewListPanel(EasyFarmingPlugin plugin, EasyFarmingPanel parentPanel, String runName, StartStopJButton startStopButton, JPopupMenu popupMenu) {
        this.plugin = plugin;
        this.parentPanel = parentPanel;
        this.runName = runName;
        this.startStopButton = startStopButton;
        this.popupMenu = popupMenu;

        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create a wrapper for hover effects and clicking
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);

        // Top section: Name and Button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel nameLabel = new JLabel(runName);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(net.runelite.client.ui.FontManager.getRunescapeBoldFont());
        headerPanel.add(nameLabel, BorderLayout.WEST);

        // Remove the startStopButton from its previous container if necessary
        if (startStopButton.getParent() != null) {
            startStopButton.getParent().remove(startStopButton);
        }
        
        // Reset the start stop button so it fits nice
        startStopButton.setPreferredSize(new Dimension(80, 25));
        headerPanel.add(startStopButton, BorderLayout.EAST);
        
        container.add(headerPanel, BorderLayout.NORTH);

        // Middle section: Icons and Progress (Stub for now)
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setOpaque(false);
        statusPanel.setBorder(new EmptyBorder(10, 0, 5, 0));
        
        // Calculate the number of enabled patches and ready patches
        int totalPatches = 0;
        int readyPatches = 0;
        com.easyfarming.models.CustomRun currentRun = plugin.getCustomRunManager().getCustomRun(runName);
        if (currentRun != null && currentRun.getLocations() != null) {
            com.easyfarming.runelite.farming.FarmingWorld fw = plugin.getFarmingWorld();
            for (com.easyfarming.models.CustomLocation loc : currentRun.getLocations()) {
                if (loc.getEnabledPatches() != null) {
                    for (String patchName : loc.getEnabledPatches()) {
                        if (loc.getPatchActiveStates().getOrDefault(patchName, false)) {
                            totalPatches++;
                            if (fw != null && plugin.getFarmingTracker() != null) {
                                for (java.util.Set<com.easyfarming.runelite.farming.FarmingPatch> patchSet : fw.getTabs().values()) {
                                    for (com.easyfarming.runelite.farming.FarmingPatch patch : patchSet) {
                                        String regionName = patch.getRegion().getName();
                                        String implName = patch.getImplementation().name().toLowerCase();
                                        implName = implName.substring(0, 1).toUpperCase() + implName.substring(1).replace("_", " ");
                                        String builtName = (patch.getName() + " " + implName).trim();
                                        
                                        if (regionName.equals(loc.getName()) && builtName.equals(patchName)) {
                                            com.easyfarming.runelite.farming.PatchPrediction prediction = plugin.getFarmingTracker().predictPatch(patch);
                                            // Handle ready states
                                            if (prediction != null && 
                                                (prediction.getCropState() == com.easyfarming.runelite.farming.CropState.HARVESTABLE ||
                                                 prediction.getCropState() == com.easyfarming.runelite.farming.CropState.DEAD ||
                                                 prediction.getCropState() == com.easyfarming.runelite.farming.CropState.DISEASED ||
                                                 prediction.getProduce() == com.easyfarming.runelite.farming.Produce.WEEDS)) {
                                                readyPatches++;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        JLabel progressText = new JLabel(readyPatches + "/" + totalPatches + " ready");
        progressText.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        statusPanel.add(progressText, BorderLayout.WEST);

        // Progress line (stub)
        JProgressBar progressBar = new JProgressBar();
        progressBar.setValue(totalPatches == 0 ? 0 : (int)(((double)readyPatches / totalPatches) * 100));
        progressBar.setStringPainted(false);
        progressBar.setPreferredSize(new Dimension(100, 4));
        progressBar.setForeground(ColorScheme.BRAND_ORANGE);
        progressBar.setBackground(ColorScheme.DARK_GRAY_COLOR);
        
        statusPanel.add(progressBar, BorderLayout.SOUTH);

        container.add(statusPanel, BorderLayout.CENTER);

        add(container, BorderLayout.CENTER);

        // Hover and Click mechanics
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
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger() && popupMenu != null) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger() && popupMenu != null) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    parentPanel.showRunDetail(runName);
                }
            }
        };

        addMouseListener(mouseAdapter);
        nameLabel.addMouseListener(mouseAdapter);
        progressText.addMouseListener(mouseAdapter);
        statusPanel.addMouseListener(mouseAdapter);
        headerPanel.addMouseListener(mouseAdapter);
        container.addMouseListener(mouseAdapter);
    }
}
