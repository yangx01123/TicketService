package com.walmart;

import com.util.Config;
import com.util.UniqId;

import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

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
    private static ConcurrentHashMap.KeySetView<Seat, Boolean> vacant;
    private static ConcurrentHashMap.KeySetView<Seat, Boolean> holded;
    private static ConcurrentHashMap.KeySetView<Seat, Boolean> committed;
    private static SeatHoldingMap seatHoldings;
    private static Timer timer;
    private static int interval;

    public TicketServiceImp() {
        vacant = ConcurrentHashMap.newKeySet();
        for (int i = 0; i < Config.totalNumSeats; i++) {
            vacant.add(new Seat(i));
        }
        holded = ConcurrentHashMap.newKeySet();
        committed = ConcurrentHashMap.newKeySet();
        seatHoldings = new SeatHoldingMap();
        // start a thread to scan and remove expired items.
        timer = new Timer();
        interval = Config.holdingAge;
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
//                seatHoldings.cleanExpiredSeatHold(Config.holdingAge);
                long stalePoint = System.nanoTime() - Config.holdingAge * 1000000000;
                for (Map.Entry<Integer, SeatHold> entry : seatHoldings.entrySet()) {
                    if (stalePoint > entry.getValue().get_creationTime()) {
                        seatHoldings.remove(entry.getKey());
                        for (Seat s : entry.getValue().get_seats()) {
                            moveSeat(holded, vacant, s);
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
        return vacant.size();
    }

    @Override
    public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
        int i = numSeats;
        SeatHold seatHold = new SeatHold();
        for (Iterator<Seat> it = vacant.iterator(); it.hasNext() && 0 < i; --i) {
            Seat s = it.next();
            s.set_customerEmail(customerEmail);
            moveSeat(vacant, holded, s);
            seatHold.addSeat(s);
        }
        seatHoldings.put(seatHold.get_id(), seatHold);
        return seatHold;
    }

    @Override
    public String reserveSeats(int seatHoldId, String customerEmail) {
        String confirmationCode = String.valueOf(UniqId.getInstance().uniqueCurrentTimeMS());
        SeatHold sh = seatHoldings.get(seatHoldId);
        if (sh == null)
            return "Invalid";
        for (Seat s : sh.get_seats()) {
            s.set_confirmCode(confirmationCode);
            s.set_customerEmail(customerEmail);
            moveSeat(holded, committed, s);
        }
        seatHoldings.remove(seatHoldId);
        return confirmationCode;
    }

    @Override
    public String toString() {
        return String.format("[vacant: %d, holded: %d, committed: %d]", vacant.size(), holded.size(), committed.size());
    }

    private void moveSeat(ConcurrentHashMap.KeySetView<Seat, Boolean> src,
                          ConcurrentHashMap.KeySetView<Seat, Boolean> dst, Seat s) {
        dst.add(s);
        src.remove(s);
    }
}
