package com.easyfarming;

import javax.swing.*;
import java.awt.*;

/**
 * A JButton that toggles between "Start" and "Stop". Display text is just "Start" or "Stop".
 */
public class StartStopJButton extends JButton {

    public StartStopJButton(String runName) {
        super("Start", null);
        setBackground(Color.BLACK);
    }

    public void setStartStopState(boolean started) {
        setText(started ? "Stop" : "Start");
        setBackground(started ? Color.RED : Color.BLACK);
    }
}