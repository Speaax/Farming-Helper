package com.easyfarming;

import javax.swing.*;
import java.awt.*;

/**
 * A JButton that can toggle between "Start" and "Stop" states.
 * Changes its text and background color based on the current state.
 * Used for run type buttons (Herb Run, Tree Run, etc.) in the plugin panel.
 */
public class StartStopJButton extends JButton {
    private String originalText;

    public StartStopJButton(String text) {
        super(text, null);

        this.originalText = text;
        this.setStartStopState(false);
    }

    public void setStartStopState(boolean started)
    {
        String startOrStop = started ? "Stop" : "Start";

        this.setText(startOrStop);
        this.setBackground(started ? Color.RED : Color.BLACK);
    }
}