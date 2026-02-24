package com.easyfarming.ui.components;

import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class DragAndDropReorderPane extends JPanel {
    private Component draggedComponent = null;
    private int dragSourceIndex = -1;
    private int dragYOffset = 0;
    private boolean isDragging = false;

    private ReorderListener reorderListener;

    public interface ReorderListener {
        void onReorder();
    }

    public DragAndDropReorderPane() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(ColorScheme.DARK_GRAY_COLOR);
    }

    public void setReorderListener(ReorderListener listener) {
        this.reorderListener = listener;
    }

    public void addDraggableComponent(Component comp, Component dragHandle) {
        // Add listeners to enable dragging from the handle
        dragHandle.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    draggedComponent = comp;
                    dragSourceIndex = getComponentIndex(comp);
                    dragYOffset = SwingUtilities.convertPoint(dragHandle, e.getPoint(), comp).y;
                    isDragging = true;
                    dragHandle.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && isDragging) {
                    isDragging = false;
                    draggedComponent = null;
                    dragSourceIndex = -1;
                    dragYOffset = 0;
                    dragHandle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isDragging) {
                    dragHandle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!isDragging) {
                    dragHandle.setCursor(Cursor.getDefaultCursor());
                }
            }
        });

        dragHandle.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging && draggedComponent != null) {
                    Point p = SwingUtilities.convertPoint(dragHandle, e.getPoint(), DragAndDropReorderPane.this);
                    
                    // 2D distance-to-center hysteresis logic prevents jitter in both WrapLayout and BoxLayout
                    int targetIndex = -1;
                    double minDist = Double.MAX_VALUE;
                    Component[] components = getComponents();
                    
                    for (int i = 0; i < components.length; i++) {
                        Component c = components[i];
                        if (c != draggedComponent && c.isVisible()) {
                            int cMidX = c.getX() + c.getWidth() / 2;
                            int cMidY = c.getY() + c.getHeight() / 2;
                            double dist = Math.hypot(p.x - cMidX, p.y - cMidY);
                            
                            // Must be deeply within the target component's bounds to swap, preventing jitter
                            double threshold = Math.min(c.getWidth(), c.getHeight()) * 0.4;
                            
                            if (dist < threshold && dist < minDist) {
                                minDist = dist;
                                targetIndex = i;
                            }
                        }
                    }

                    if (targetIndex != -1 && targetIndex != dragSourceIndex) {
                        // Swap components
                        remove(draggedComponent);
                        add(draggedComponent, targetIndex);
                        
                        // Update source index
                        dragSourceIndex = getComponentIndex(draggedComponent);
                        
                        revalidate();
                        repaint();
                        
                        if (reorderListener != null) {
                            reorderListener.onReorder();
                        }
                    }
                }
            }
        });

        add(comp);
    }

    private int getComponentIndex(Component comp) {
        Component[] components = getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] == comp) {
                return i;
            }
        }
        return -1;
    }
}
