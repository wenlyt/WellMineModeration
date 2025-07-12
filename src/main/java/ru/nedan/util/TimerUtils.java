package ru.nedan.util;

public class TimerUtils {
    private long last;

    public TimerUtils() {
        updateLast();
    }

    public void updateLast() {
        last = System.currentTimeMillis();
    }

    public boolean timeElapsed(long ms) {
        return System.currentTimeMillis() - last >= ms;
    }
}
