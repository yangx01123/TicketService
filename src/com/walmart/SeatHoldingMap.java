package com.walmart;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Yang on 22/01/2018.
 */
public class SeatHoldingMap extends ConcurrentHashMap<Integer, SeatHold> {
    public void cleanExpiredSeatHold(int age) {
        long stalePoint = System.nanoTime() - age * 1000000000;
        for (Entry<Integer, SeatHold> entry : this.entrySet()) {
            if (stalePoint > entry.getValue().get_creationTime()) {
                this.remove(entry.getKey());
            }
        }
    }
}
