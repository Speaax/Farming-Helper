package com.easyfarming.ui;

import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.EasyFarmingPanel;
import com.easyfarming.StartStopJButton;
import com.easyfarming.customrun.CustomRun;
import com.easyfarming.customrun.CustomRunStorage;
import com.easyfarming.core.Location;
import com.easyfarming.core.Teleport;
import com.easyfarming.customrun.LocationCatalog;
import com.easyfarming.customrun.PatchTypes;
import com.easyfarming.customrun.RunLocation;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Detail view for a custom run: editable name, filter bar (2x3), then one sub-panel per location (patch icons + teleport).
 * No location dropdown: each location is its own sub-panel. Filter: yellow = show locations with that patch; green = enable that patch at all locations.
 */
public class CustomRunDetailPanel extends JPanel {
    private final EasyFarmingPlugin plugin;
    private final EasyFarmingPanel parentPanel;
    private final CustomRun customRun;
    private final boolean isNewRun;
    /** Name when the panel was opened; used when saving to find the run in the loaded list (in case user renamed). */
    private final String originalRunName;
    private final net.runelite.client.game.ItemManager itemManager;

    private final CustomRunFilterBar filterBar;
    private final JPanel locationsContainer = new JPanel();
    private final JTextField runNameField;
    /** Order and RunLocation per location name (catalog order). */
    private final List<RunLocation> runLocationsInOrder = new ArrayList<>();
    private final Map<String, CustomRunLocationSubPanel> subPanelsByLocation = new LinkedHashMap<>();
    private String draggedLocationName;
    private AWTEventListener dragEndListener;
    /** Prevents re-entry when syncing/refreshing (e.g. combo firing during refreshFromRunLocation). */
    private boolean inRowChangedOrRefresh = false;

    public CustomRunDetailPanel(EasyFarmingPlugin plugin, EasyFarmingPanel parentPanel,
                                CustomRun customRun, boolean isNewRun,
                                net.runelite.client.game.ItemManager itemManager) {
        this.plugin = plugin;
        this.parentPanel = parentPanel;
        this.customRun = customRun;
        this.isNewRun = isNewRun;
        this.originalRunName = customRun.getName() != null ? customRun.getName() : "";
        this.itemManager = itemManager;

        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        filterBar = new CustomRunFilterBar(itemManager);
        filterBar.setOnFilterChanged(this::onFilterChanged);

        syncRunWithCatalog();

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        headerPanel.setBorder(new EmptyBorder(5, 5, 10, 5));

        JButton backButton = new JButton("<");
        backButton.setPreferredSize(new Dimension(40, 30));
        backButton.setFocusable(false);
        backButton.setToolTipText("Back to Overview");
        backButton.addActionListener(e -> {
            saveRun();
            parentPanel.showOverview();
        });
        headerPanel.add(backButton, BorderLayout.WEST);

        runNameField = new JTextField(customRun.getName() != null ? customRun.getName() : "New Run", 20);
        runNameField.setForeground(Color.WHITE);
        runNameField.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        runNameField.setCaretColor(Color.WHITE);
        runNameField.setFont(net.runelite.client.ui.FontManager.getRunescapeBoldFont());
        runNameField.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        JButton saveButton = new JButton("Save");
        saveButton.setFocusable(false);
        saveButton.setToolTipText("Save run and stay on edit screen (saves current name and all locations as shown)");
        saveButton.addActionListener(e -> saveRun());
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titleRow.setBackground(ColorScheme.DARK_GRAY_COLOR);
        titleRow.add(runNameField);
        titlePanel.add(titleRow);
        JPanel saveRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
        saveRow.setBackground(ColorScheme.DARK_GRAY_COLOR);
        saveRow.add(saveButton);
        titlePanel.add(saveRow);
        headerPanel.add(titlePanel, BorderLayout.CENTER);

        JPanel headerButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        headerButtons.setBackground(ColorScheme.DARK_GRAY_COLOR);
        StartStopJButton startButton = new StartStopJButton(customRun.getName());
        startButton.setPreferredSize(new Dimension(80, 25));
        startButton.addActionListener(e -> {
            boolean toggleToStop = startButton.getText().equals("Start");
            startButton.setStartStopState(toggleToStop);
            if (toggleToStop) {
                plugin.setCustomRunToolInclusion(
                    filterBar.isSecateursIncluded(),
                    filterBar.isDibberIncluded(),
                    filterBar.isRakeIncluded());
                parentPanel.startCustomRun(customRun);
            } else {
                plugin.getFarmingTeleportOverlay().removeOverlay();
                plugin.setOverlayActive(false);
            }
        });
        syncStartButtonState(startButton);
        headerButtons.add(startButton);
        headerPanel.add(headerButtons, BorderLayout.EAST);
        JLabel filterHint = new JLabel("Tools: grey = not included, green = included. Patch: yellow = filter, green = enable at all.");
        filterHint.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        filterHint.setFont(filterHint.getFont().deriveFont(10f));
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        northPanel.add(headerPanel);
        northPanel.add(filterBar);
        northPanel.add(filterHint);
        add(northPanel, BorderLayout.NORTH);

        locationsContainer.setLayout(new BoxLayout(locationsContainer, BoxLayout.Y_AXIS));
        locationsContainer.setBackground(ColorScheme.DARK_GRAY_COLOR);
        locationsContainer.setBorder(new EmptyBorder(0, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(locationsContainer);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(ColorScheme.DARK_GRAY_COLOR);
        add(scrollPane, BorderLayout.CENTER);

        syncFilterStateFromRun();
        // Restore saved tool requirement state
        filterBar.setSecateursIncluded(customRun.isIncludeSecateurs());
        filterBar.setDibberIncluded(customRun.isIncludeDibber());
        filterBar.setRakeIncluded(customRun.isIncludeRake());
        refreshLocationVisibility();
    }

    /** Update filter bar from run data: green if all enabled, yellow if some enabled, neutral if none. */
    private void syncFilterStateFromRun() {
        LocationCatalog catalog = plugin.getLocationCatalog();
        for (String patchType : PatchTypes.ALL) {
            boolean allEnabled = true;
            int someEnabled = 0;
            int locationsWithType = 0;
            for (RunLocation rl : runLocationsInOrder) {
                List<String> available = catalog.getPatchTypesAtLocation(rl.getLocationName());
                if (available != null && available.contains(patchType)) {
                    locationsWithType++;
                    if (rl.getPatchTypes().contains(patchType)) {
                        someEnabled++;
                    } else {
                        allEnabled = false;
                    }
                }
            }
            int state;
            if (locationsWithType > 0 && allEnabled) {
                state = 2; // green
            } else if (someEnabled > 0) {
                state = 1; // yellow (some but not all)
            } else {
                state = 0; // neutral
            }
            filterBar.setFilterState(patchType, state);
        }
    }

    /** Ensure we have one RunLocation per catalog location (in catalog order); merge with saved run. */
    private void syncRunWithCatalog() {
        LocationCatalog catalog = plugin.getLocationCatalog();
        List<String> names = catalog.getAllLocationNames();
        Map<String, RunLocation> byName = new LinkedHashMap<>();
        for (RunLocation rl : customRun.getLocations()) {
            if (rl.getLocationName() != null) {
                byName.put(rl.getLocationName(), rl);
            }
        }
        runLocationsInOrder.clear();
        for (String name : names) {
            RunLocation rl = byName.get(name);
            if (rl == null) {
                List<String> teleports = catalog.getTeleportOptionsForLocation(name);
                String defaultTeleport = null;
                List<String> patchTypes = catalog.getPatchTypesAtLocation(name);
                if (!patchTypes.isEmpty()) {
                    Location location = catalog.getLocationForPatch(name, patchTypes.get(0));
                    if (location != null) {
                        Teleport selected = location.getSelectedTeleport();
                        if (selected != null && selected.getEnumOption() != null) {
                            defaultTeleport = selected.getEnumOption();
                        }
                    }
                }
                if (defaultTeleport == null) {
                    defaultTeleport = teleports.isEmpty() ? null : teleports.get(0);
                }
                rl = new RunLocation(name, defaultTeleport, new ArrayList<>());
            }
            runLocationsInOrder.add(rl);
        }
        customRun.getLocations().clear();
        customRun.getLocations().addAll(runLocationsInOrder);
    }

    private void onFilterChanged(String patchType, int fromState, int toState) {
        if (toState == 2) {
            applyEnablePatchEverywhere(patchType);
        } else if (fromState == 2 && toState == 0) {
            applyDisablePatchEverywhere(patchType);
        }
        refreshLocationVisibility();
    }

    private void applyEnablePatchEverywhere(String patchType) {
        LocationCatalog catalog = plugin.getLocationCatalog();
        for (RunLocation rl : runLocationsInOrder) {
            List<String> available = catalog.getPatchTypesAtLocation(rl.getLocationName());
            if (available != null && available.contains(patchType)) {
                if (!rl.getPatchTypes().contains(patchType)) {
                    rl.getPatchTypes().add(patchType);
                }
            }
        }
        refreshSubPanels();
    }

    private void applyDisablePatchEverywhere(String patchType) {
        for (RunLocation rl : runLocationsInOrder) {
            rl.getPatchTypes().remove(patchType);
        }
        refreshSubPanels();
    }

    private void refreshSubPanels() {
        for (CustomRunLocationSubPanel sub : subPanelsByLocation.values()) {
            sub.refreshFromRunLocation();
        }
    }

    private void refreshLocationVisibility() {
        locationsContainer.removeAll();
        if (!filterBar.hasAnyYellowOrGreenFilter()) {
            JLabel noFilter = new JLabel("Select a filter (yellow or green) to show locations.");
            noFilter.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
            locationsContainer.add(noFilter);
            locationsContainer.revalidate();
            locationsContainer.repaint();
            return;
        }
        java.util.Set<String> filterTypes = filterBar.getYellowOrGreenFilterTypes();
        LocationCatalog catalog = plugin.getLocationCatalog();
        for (RunLocation rl : runLocationsInOrder) {
            String name = rl.getLocationName();
            List<String> atLocation = catalog.getPatchTypesAtLocation(name);
            boolean show = false;
            for (String t : filterTypes) {
                if (atLocation != null && atLocation.contains(t)) {
                    show = true;
                    break;
                }
            }
            if (show) {
                CustomRunLocationSubPanel sub = subPanelsByLocation.get(name);
                if (sub == null) {
                    sub = new CustomRunLocationSubPanel(plugin, itemManager, name, rl, this::onRowChanged, this::onLocationDragStart);
                    subPanelsByLocation.put(name, sub);
                } else {
                    sub.refreshFromRunLocation();
                }
                locationsContainer.add(sub);
                locationsContainer.add(Box.createRigidArea(new Dimension(0, 6)));
            }
        }
        locationsContainer.revalidate();
        locationsContainer.repaint();
    }

    private void onRowChanged() {
        if (inRowChangedOrRefresh) return;
        inRowChangedOrRefresh = true;
        try {
            syncFilterStateFromRun();
            refreshLocationVisibility();
        } finally {
            inRowChangedOrRefresh = false;
        }
    }

    private void onLocationDragStart(String locationName) {
        this.draggedLocationName = locationName;
        if (dragEndListener != null) {
            Toolkit.getDefaultToolkit().removeAWTEventListener(dragEndListener);
        }
        dragEndListener = event -> {
            if (event.getID() != MouseEvent.MOUSE_RELEASED) return;
            if (!(event instanceof MouseEvent)) return;
            MouseEvent me = (MouseEvent) event;
            Toolkit.getDefaultToolkit().removeAWTEventListener(dragEndListener);
            dragEndListener = null;
            if (draggedLocationName == null) return;
            Component src = (Component) event.getSource();
            Point pt = new Point(me.getX(), me.getY());
            SwingUtilities.convertPointToScreen(pt, src);
            SwingUtilities.convertPointFromScreen(pt, locationsContainer);
            if (!locationsContainer.contains(pt)) {
                draggedLocationName = null;
                return;
            }
            Component at = locationsContainer.getComponentAt(pt);
            CustomRunLocationSubPanel dropPanel = findLocationSubPanelParent(at);
            if (dropPanel == null || dropPanel.getLocationName().equals(draggedLocationName)) {
                draggedLocationName = null;
                return;
            }
            reorderLocation(draggedLocationName, dropPanel.getLocationName());
            draggedLocationName = null;
        };
        Toolkit.getDefaultToolkit().addAWTEventListener(dragEndListener, java.awt.AWTEvent.MOUSE_EVENT_MASK);
    }

    private static CustomRunLocationSubPanel findLocationSubPanelParent(Component c) {
        while (c != null) {
            if (c instanceof CustomRunLocationSubPanel) return (CustomRunLocationSubPanel) c;
            c = c.getParent();
        }
        return null;
    }

    private void reorderLocation(String movedName, String dropBeforeName) {
        RunLocation moved = null;
        int fromIndex = -1;
        for (int i = 0; i < runLocationsInOrder.size(); i++) {
            if (runLocationsInOrder.get(i).getLocationName().equals(movedName)) {
                moved = runLocationsInOrder.get(i);
                fromIndex = i;
                break;
            }
        }
        if (moved == null) return;
        runLocationsInOrder.remove(fromIndex);
        int insertIndex = -1;
        for (int i = 0; i < runLocationsInOrder.size(); i++) {
            if (runLocationsInOrder.get(i).getLocationName().equals(dropBeforeName)) {
                insertIndex = i;
                break;
            }
        }
        if (insertIndex < 0) insertIndex = runLocationsInOrder.size();
        runLocationsInOrder.add(insertIndex, moved);
        customRun.getLocations().clear();
        customRun.getLocations().addAll(runLocationsInOrder);
        refreshLocationVisibility();
    }

    /** Sets the Start/Stop button to Stop if this run is currently the active custom run. */
    private void syncStartButtonState(StartStopJButton startButton) {
        if (plugin.getFarmingTeleportOverlay().isCustomRunMode()
                && customRun.getName() != null
                && customRun.getName().equals(plugin.getFarmingTeleportOverlay().getActiveCustomRunName())) {
            startButton.setStartStopState(true);
        }
    }

    /** Saves the config state as it exists on button press: name, tool requirements, and all locations from UI. */
    private void saveRun() {
        // 1. Commit name from text field
        if (runNameField != null) {
            String currentName = runNameField.getText();
            if (currentName != null && !currentName.trim().isEmpty()) {
                customRun.setName(currentName.trim());
            }
        }
        // 2. Commit tool requirement state from filter bar
        customRun.setIncludeSecateurs(filterBar.isSecateursIncluded());
        customRun.setIncludeDibber(filterBar.isDibberIncluded());
        customRun.setIncludeRake(filterBar.isRakeIncluded());
        // 3. Commit location order and ensure customRun.getLocations() matches current UI
        customRun.getLocations().clear();
        customRun.getLocations().addAll(runLocationsInOrder);
        // 4. Persist: load current list and update or add this run
        CustomRunStorage storage = plugin.getCustomRunStorage();
        List<CustomRun> runs = storage.load();
        if (isNewRun) {
            runs.add(customRun);
        } else {
            // Find by original name so we still find the run if the user renamed it
            for (int i = 0; i < runs.size(); i++) {
                if (Objects.equals(originalRunName, runs.get(i).getName())) {
                    runs.set(i, customRun);
                    break;
                }
            }
        }
        storage.save(runs);
    }
}
