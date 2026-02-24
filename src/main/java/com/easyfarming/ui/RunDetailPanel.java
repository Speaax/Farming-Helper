package com.easyfarming.ui;

import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.EasyFarmingPanel;
import com.easyfarming.models.CustomRun;
import com.easyfarming.StartStopJButton;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class RunDetailPanel extends JPanel {
    private final EasyFarmingPlugin plugin;
    private final EasyFarmingPanel parentPanel;
    private final String runName;
    private final net.runelite.client.game.ItemManager itemManager;
    private final java.util.List<LocationConfigPanel> locationPanels = new java.util.ArrayList<>();

    public RunDetailPanel(EasyFarmingPlugin plugin, EasyFarmingPanel parentPanel, String runName, net.runelite.client.game.ItemManager itemManager) {
        this.plugin = plugin;
        this.parentPanel = parentPanel;
        this.runName = runName;
        this.itemManager = itemManager;

        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        headerPanel.setBorder(new EmptyBorder(5, 5, 10, 5));
        
        JButton backButton = new JButton("<");
        backButton.setPreferredSize(new Dimension(40, 30));
        backButton.setFocusable(false);
        backButton.setToolTipText("Back to Overview");
        backButton.addActionListener(e -> parentPanel.showOverview());
        headerPanel.add(backButton, BorderLayout.WEST);
        
        JLabel titleLabel = new JLabel(runName);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(net.runelite.client.ui.FontManager.getRunescapeBoldFont());
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        StartStopJButton startButton = new StartStopJButton(runName);
        startButton.setPreferredSize(new Dimension(80, 25));
        startButton.addActionListener(e -> {
            boolean toggleToStop = startButton.getText().equals("Start");
            startButton.setStartStopState(toggleToStop);
            if (toggleToStop) {
                plugin.startCustomRun(runName);
            } else {
                plugin.getFarmingTeleportOverlay().removeOverlay();
                plugin.setOverlayActive(false);
            }
        });
        headerPanel.add(startButton, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);

        // Body (Scrollable)
        JPanel scrollContentWrapper = new JPanel(new BorderLayout());
        scrollContentWrapper.setBackground(ColorScheme.DARK_GRAY_COLOR);

        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setBackground(ColorScheme.DARK_GRAY_COLOR);
        scrollContent.setBorder(new EmptyBorder(0, 10, 10, 10));

        // ── Tool toggle buttons ──────────────────────────────────────────
        JPanel toolsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        toolsRow.setBackground(ColorScheme.DARK_GRAY_COLOR);
        toolsRow.setBorder(new EmptyBorder(0, 0, 4, 0));

        toolsRow.add(makeToolButton(net.runelite.api.ItemID.SPADE,         "Spade",     true,  false, null));
        toolsRow.add(makeToolButton(net.runelite.api.ItemID.MAGIC_SECATEURS, "Secateurs", plugin.isToolSecateurs(), true,
                on -> plugin.setToolSecateurs(on)));
        toolsRow.add(makeToolButton(net.runelite.api.ItemID.SEED_DIBBER,   "Dibber",    plugin.isToolDibber(), true,
                on -> plugin.setToolDibber(on)));
        toolsRow.add(makeToolButton(net.runelite.api.ItemID.RAKE,          "Rake",      plugin.isToolRake(), true,
                on -> plugin.setToolRake(on)));

        scrollContent.add(toolsRow);

        // Separator between tools and patch filters
        JPanel separator = new JPanel();
        separator.setBackground(new Color(60, 60, 60));
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separator.setPreferredSize(new Dimension(0, 1));
        scrollContent.add(separator);
        scrollContent.add(Box.createRigidArea(new Dimension(0, 6)));

        // Filters Panel
        JPanel filtersPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        filtersPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        filtersPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        
        CustomRun currentRun = plugin.getCustomRunManager().getCustomRun(runName);
        if (currentRun == null) {
            // Guard clause if run is somehow null
            add(new JLabel("Error: Run not found"), BorderLayout.CENTER);
            return;
        }

        Runnable saveCallback = () -> {
            plugin.getCustomRunManager().updateCustomRun(currentRun);
        };

        filtersPanel.add(createTriStateButton("Herb", currentRun, saveCallback));
        filtersPanel.add(createTriStateButton("Allotment", currentRun, saveCallback));
        filtersPanel.add(createTriStateButton("Flower", currentRun, saveCallback));
        
        scrollContent.add(filtersPanel);

        // Locations
        com.easyfarming.ui.components.DragAndDropReorderPane dndPane = new com.easyfarming.ui.components.DragAndDropReorderPane();
        dndPane.setBackground(ColorScheme.DARK_GRAY_COLOR);

        List<String> standardTeleports = Arrays.asList("Ardy cloak", "Portal nexus", "Fairy ring", "Ectophial", "Spirit tree"); // Expanded dummy teleports
        
        if (currentRun.getLocations() != null) {
            for (com.easyfarming.models.CustomLocation location : currentRun.getLocations()) {
                LocationConfigPanel lPanel = new LocationConfigPanel(location, standardTeleports, itemManager, saveCallback);
                lPanel.putClientProperty("CustomLocation", location);
                lPanel.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(0, 0, 5, 0), BorderFactory.createMatteBorder(1, 1, 1, 1, ColorScheme.DARK_GRAY_COLOR)));
                dndPane.addDraggableComponent(lPanel, lPanel.getHeaderPanel());
                locationPanels.add(lPanel);
            }
        }

        dndPane.setReorderListener(() -> {
            List<com.easyfarming.models.CustomLocation> newOrder = new java.util.ArrayList<>();
            for (Component c : dndPane.getComponents()) {
                if (c instanceof LocationConfigPanel) {
                    com.easyfarming.models.CustomLocation loc = (com.easyfarming.models.CustomLocation) ((LocationConfigPanel)c).getClientProperty("CustomLocation");
                    if (loc != null) {
                        newOrder.add(loc);
                    }
                }
            }
            if (!newOrder.isEmpty()) {
                currentRun.setLocations(newOrder);
                saveCallback.run();
            }
        });

        scrollContent.add(dndPane);
        
        scrollContentWrapper.add(scrollContent, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(scrollContentWrapper);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scrollPane.getViewport().setBackground(ColorScheme.DARK_GRAY_COLOR);
        
        add(scrollPane, BorderLayout.CENTER);
        
        applyFilters(currentRun);
    }

    private void setPatchesActive(String typeFilter, boolean active, com.easyfarming.models.CustomRun currentRun) {
        String lowerType = typeFilter.toLowerCase();
        for (com.easyfarming.models.CustomLocation loc : currentRun.getLocations()) {
            if (loc.getEnabledPatches() != null) {
                for (String patch : loc.getEnabledPatches()) {
                    if (patch.toLowerCase().contains(lowerType)) {
                        loc.getPatchActiveStates().put(patch, active);
                    }
                }
            }
        }
        for (LocationConfigPanel panel : locationPanels) {
            panel.refreshPatchButtons();
        }
    }

    private void applyFilters(com.easyfarming.models.CustomRun currentRun) {
        java.util.List<String> activeFilters = new java.util.ArrayList<>();
        for (java.util.Map.Entry<String, Integer> entry : currentRun.getFilterStates().entrySet()) {
            if (entry.getValue() > 0) {
                activeFilters.add(entry.getKey().toLowerCase());
            }
        }
        
        for (LocationConfigPanel panel : locationPanels) {
            com.easyfarming.models.CustomLocation loc = (com.easyfarming.models.CustomLocation) panel.getClientProperty("CustomLocation");
            if (loc == null) continue;
            
            if (activeFilters.isEmpty()) {
                panel.setVisible(false); // Hide everything if no filters active
            } else {
                boolean hasMatchingPatch = false;
                if (loc.getEnabledPatches() != null) {
                    for (String patch : loc.getEnabledPatches()) {
                        String lowerPatch = patch.toLowerCase();
                        for (String filter : activeFilters) {
                            if (lowerPatch.contains(filter)) {
                                hasMatchingPatch = true;
                                break;
                            }
                        }
                        if (hasMatchingPatch) break;
                    }
                }
                panel.setVisible(hasMatchingPatch);
            }
        }
        this.revalidate();
        this.repaint();
    }

    private JButton createTriStateButton(String name, com.easyfarming.models.CustomRun currentRun, Runnable saveCallback) {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(50, 30));
        btn.setFocusable(false);

        int initialState = currentRun.getFilterStates().getOrDefault(name, 0);
        btn.putClientProperty("tri_state", initialState);
        
        Color yellowState = new Color(60, 60, 30);
        Color greenState = new Color(30, 60, 30);

        if (initialState == 0) btn.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        else if (initialState == 1) btn.setBackground(yellowState);
        else btn.setBackground(greenState);
        
        if (itemManager != null) {
            int itemId = -1;
            if (name.toLowerCase().contains("herb")) itemId = net.runelite.api.ItemID.GRIMY_GUAM_LEAF;
            else if (name.toLowerCase().contains("allotment")) itemId = net.runelite.api.ItemID.WATERMELON;
            else if (name.toLowerCase().contains("flower")) itemId = net.runelite.api.ItemID.LIMPWURT_ROOT;
            else if (name.toLowerCase().contains("hops")) itemId = net.runelite.api.ItemID.BARLEY;
            else if (name.toLowerCase().contains("fruit tree")) itemId = net.runelite.api.ItemID.PINEAPPLE;
            else if (name.toLowerCase().contains("tree")) itemId = net.runelite.api.ItemID.LOGS;
            
            if (itemId != -1) {
                itemManager.getImage(itemId).addTo(btn);
            } else {
                btn.setText(name);
            }
        } else {
            btn.setText(name);
        }
        
        btn.setToolTipText(name + " Filter");
        
        btn.addActionListener(e -> {
            int state = (int) btn.getClientProperty("tri_state");
            state = (state + 1) % 3;
            btn.putClientProperty("tri_state", state);
            
            if (state == 0) {
                btn.setBackground(ColorScheme.DARKER_GRAY_COLOR);
                setPatchesActive(name, false, currentRun);
            } else if (state == 1) {
                btn.setBackground(yellowState); 
                // yellow state = filter
            } else {
                btn.setBackground(greenState);
                // green state = enable all
                setPatchesActive(name, true, currentRun);
            }
            
            currentRun.getFilterStates().put(name, state);
            if (saveCallback != null) saveCallback.run();
            
            applyFilters(currentRun);
        });
        
        return btn;
    }

    private JButton makeToolButton(int itemId, String tooltip, boolean active,
                                   boolean toggleable, java.util.function.Consumer<Boolean> onToggle) {
        final boolean[] state = {active};
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(36, 36));
        btn.setFocusable(false);
        btn.setToolTipText(tooltip);
        applyToolButtonStyle(btn, state[0]);

        if (itemManager != null) {
            itemManager.getImage(itemId).addTo(btn);
        } else {
            btn.setText(tooltip);
        }

        if (!toggleable) {
            btn.setEnabled(false);
        } else {
            btn.addActionListener(e -> {
                state[0] = !state[0];
                applyToolButtonStyle(btn, state[0]);
                if (onToggle != null) onToggle.accept(state[0]);
            });
        }
        return btn;
    }

    private static void applyToolButtonStyle(JButton btn, boolean on) {
        btn.setBackground(on ? new Color(30, 60, 30) : ColorScheme.DARKER_GRAY_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
    }
}
