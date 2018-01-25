package com.app;

import com.util.UniqId;

import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import static com.util.Config.holdingAge;
import static com.util.Config.totalNumSeats;
import static com.util.Config.pool;

/**
 * Find the number of seats available within the venue
 * Note: available seats are seats that are neither held nor reserved.
 * <p>
 * Find and hold the best available seats on behalf of a customer
 * Note: each ticket hold should expire within a set number of seconds.
 * <p>
 * Reserve and commit a specific group of held seats for a customer
 */
public class TicketServiceImp implements TicketService {
    private static ConcurrentHashMap.KeySetView<Seat, Boolean> _vacant;
    private static ConcurrentHashMap.KeySetView<Seat, Boolean> _holded;
    private static ConcurrentHashMap.KeySetView<Seat, Boolean> _committed;
    private static ConcurrentHashMap<Integer, SeatHold> seatHoldings;
    private static Timer timer;
    private static int interval;

    public TicketServiceImp() {
        _vacant = ConcurrentHashMap.newKeySet();
        for (int i = 0; i < totalNumSeats; i++) {
            _vacant.add(new Seat(i));
        }
        _holded = ConcurrentHashMap.newKeySet();
        _committed = ConcurrentHashMap.newKeySet();
        seatHoldings = new ConcurrentHashMap<>();
        // start a thread to scan and remove expired items.
        timer = new Timer();
        interval = holdingAge;
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                long stalePoint = System.nanoTime() - holdingAge * 1000000000;
                for (Map.Entry<Integer, SeatHold> entry : seatHoldings.entrySet()) {
                    if (stalePoint > entry.getValue().get_creationTime()) {
                        seatHoldings.remove(entry.getKey());
                        for (Seat s : entry.getValue().get_seats()) {
                            moveSeat(_holded, _vacant, s);
                        }
                    }
                }
                if (interval == 1)
                    timer.cancel();
                --interval;
            }
        }, 1000, 1000);
    }

    @Override
    public int numSeatsAvailable() {
        return _vacant.size();
    }

    @Override
    public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
        int i = numSeats;
        SeatHold seatHold = new SeatHold();
        for (Iterator<Seat> it = _vacant.iterator(); it.hasNext() && 0 < i; --i) {
            Seat s = it.next();
            s.set_customerEmail(customerEmail);
            moveSeat(_vacant, _holded, s);
            seatHold.addSeat(s);
        }
        seatHoldings.put(seatHold.get_id(), seatHold);
        return seatHold;
    }

    @Override
    public String reserveSeats(int seatHoldId, String customerEmail) {
        String confirmationCode = String.valueOf(UniqId.uniqueCurrentTimeMS());
        SeatHold sh = seatHoldings.get(seatHoldId);
        if (sh == null)
            return "Invalid";
        for (Seat s : sh.get_seats()) {
            s.set_confirmCode(confirmationCode);
            s.set_customerEmail(customerEmail);
            moveSeat(_holded, _committed, s);
        }
        seatHoldings.remove(seatHoldId);
        return confirmationCode;
    }

    @Override
    public String toString() {
        return String.format("[vacant: %d, holded: %d, committed: %d]", _vacant.size(), _holded.size(), _committed.size());
    }

    private void moveSeat(ConcurrentHashMap.KeySetView<Seat, Boolean> src,
                          ConcurrentHashMap.KeySetView<Seat, Boolean> dst, Seat s) {
        dst.add(s);
        src.remove(s);
    }

    public ConcurrentHashMap.KeySetView<Seat, Boolean> get(pool fieldName) {
        switch (fieldName) {
            case vacant:
                return _vacant;
            case holded:
                return _holded;
            case committed:
                return _committed;
            default:
                return null;
        }
    }
}
