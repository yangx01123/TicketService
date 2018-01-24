package com.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Unique ID generator.
 */
public class UniqId {
    private static final AtomicLong LAST_TIME_MS = new AtomicLong();
    private static AtomicInteger at = new AtomicInteger(0);

    // Singleton
    private volatile static UniqId instance;
    public static UniqId getInstance() {
        if (instance == null) {
            synchronized (UniqId.class) {
                if (instance == null) {
                    instance = new UniqId();
                }
            }
        }
        return instance;
    }

    /**
     * Get unique long value by current system time in milliseconds.
     * @return long value that unique among other callers.
     */
    public long uniqueCurrentTimeMS() {
        long now = System.currentTimeMillis();
        while (true) {
            long lastTime = LAST_TIME_MS.get();
            if (lastTime >= now)
                now = lastTime + 1;
            if (LAST_TIME_MS.compareAndSet(lastTime, now))
                return now;
        }
    }

    public int uniqueInt() {
        return at.incrementAndGet();
    }

}
