package com.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Unique ID generator.
 */
public class UniqId {
    private static final AtomicLong LAST_TIME_MS = new AtomicLong();
    private static AtomicInteger at = new AtomicInteger(0);

    /**
     * Get an unique long value from system time.
     *
     * @return long value that unique among other callers.
     */
    public static synchronized long uniqueCurrentTimeMS() {
        long now = System.currentTimeMillis();
        while (true) {
            long lastTime = LAST_TIME_MS.get();
            if (lastTime >= now)
                now = lastTime + 1;
            if (LAST_TIME_MS.compareAndSet(lastTime, now))
                return now;
        }
    }

    /**
     * Get an unique int value.
     *
     * @return int value that unique among other callers.
     */
    public static synchronized int uniqueInt() {
        return at.incrementAndGet();
    }

}
