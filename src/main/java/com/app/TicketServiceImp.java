package com.app;

import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.util.UniqId;

import static com.util.Config.holdingAge;
import static com.util.Config.totalNumSeats;

/**
 * Reserve and commit a specific group of held seats for a customer
 */
public class TicketServiceImp implements TicketService {
    private static final Logger logger = LogManager.getLogger(TicketServiceImp.class);
    private static ConcurrentHashMap.KeySetView<Seat, Boolean> _vacant;
    private static ConcurrentHashMap.KeySetView<Seat, Boolean> _holded;
    private static ConcurrentHashMap.KeySetView<Seat, Boolean> _reserved;
    private static ConcurrentHashMap<Integer, SeatHold> seatHoldings;
    private static Timer timer;
    private static int interval;

    public TicketServiceImp() {
        _vacant = ConcurrentHashMap.newKeySet();
        for (int i = 0; i < totalNumSeats; i++) {
            _vacant.add(new Seat(i));
        }
        _holded = ConcurrentHashMap.newKeySet();
        _reserved = ConcurrentHashMap.newKeySet();
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

    /**
     * Find the number of seats available within the venue
     * Note: available seats are seats that are neither held nor reserved.
     *
     * @param numSeats      the number of seats to find and hold
     * @param customerEmail unique identifier for the customer
     * @return a SeatHold object
     */
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

        logger.trace(String.format("%d seats are held by id[%d]",
                seatHold.get_seats().size(), seatHold.get_id()));
        logger.trace(String.format("Current seats distribution %s", this));
        return seatHold;
    }

    /**
     * Find and hold the best available seats on behalf of a customer
     * Note: each ticket hold should expire within a set number of seconds.
     *
     * @param seatHoldId    the seat hold identifier
     * @param customerEmail the email address of the customer to which the seat hold is assigned
     * @return a String of confirmation code.
     */
    @Override
    public String reserveSeats(int seatHoldId, String customerEmail) {
        String confirmationCode = String.valueOf(UniqId.uniqueCurrentTimeMS());
        SeatHold sh = seatHoldings.get(seatHoldId);
        if (sh == null)
            return "Invalid";
        for (Seat s : sh.get_seats()) {
            s.set_confirmCode(confirmationCode);
            s.set_customerEmail(customerEmail);
            moveSeat(_holded, _reserved, s);
        }
        seatHoldings.remove(seatHoldId);
        return confirmationCode;
    }

    @Override
    public String toString() {
        return String.format("[vacant: %d, holded: %d, reserved: %d]",
                _vacant.size(), _holded.size(), _reserved.size());
    }

    private void moveSeat(ConcurrentHashMap.KeySetView<Seat, Boolean> src,
                          ConcurrentHashMap.KeySetView<Seat, Boolean> dst, Seat s) {
        dst.add(s);
        src.remove(s);
    }

    public ConcurrentHashMap.KeySetView<Seat, Boolean> get_vacant() {
        return _vacant;
    }

    public ConcurrentHashMap.KeySetView<Seat, Boolean> get_holded() {
        return _holded;
    }

    public ConcurrentHashMap.KeySetView<Seat, Boolean> get_reserved() {
        return _reserved;
    }
}
