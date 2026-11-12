package com.workspace.app.util;

import javax.swing.Timer;
import java.awt.event.ActionListener;

public class Debouncer {
    private final int delayMs;
    private Timer timer;

    public Debouncer(int delayMs) {
        this.delayMs = delayMs;
    }

    public void debounce(Runnable action) {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        ActionListener listener = e -> {
            timer.stop();
            action.run();
        };
        timer = new Timer(delayMs, listener);
        timer.setRepeats(false);
        timer.start();
    }

    public void cancel() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }
}
