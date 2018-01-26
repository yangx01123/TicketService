package com.app;

import com.util.UniqId;

import java.util.concurrent.ConcurrentHashMap;

/**
 * SeatHold class to identify the specific seats and related information.
 */
public class SeatHold {
    private int _id;
    private ConcurrentHashMap.KeySetView<Seat, Boolean> _seats;
    private long _creationTime;

    public SeatHold() {
        _id = UniqId.uniqueInt();
        _seats = ConcurrentHashMap.newKeySet();
        _creationTime = System.nanoTime();
    }

    public int get_id() {
        return _id;
    }

    public boolean addSeat(Seat seat) {
        return _seats.add(seat);
    }

    public ConcurrentHashMap.KeySetView<Seat, Boolean> get_seats() {
        return _seats;
    }

    public long get_creationTime() {
        return _creationTime;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        sb.append(_creationTime);
        for (Seat s : _seats) {
            sb.append(s.toString());
        }
        sb.append("}");
        return sb.toString();
    }

}
