package com.app;

import com.util.Log;
import org.junit.Test;

import com.util.UniqId;

import static com.util.Config.holdingAge;
import static com.util.Config.totalNumSeats;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * Test class for TicketServiceImp.
 */
public class TicketServiceImpTest {

    private TicketServiceImp tsi = new TicketServiceImp();

    @Test
    public void numSeatsAvailable() throws Exception {
        assertEquals(totalNumSeats, tsi.numSeatsAvailable());
    }

    @Test
    public void findAndHoldSeats() throws Exception {
        holdMatch(totalNumSeats);
        sleep(holdingAge + 1);
        holdMatch(0);
        sleep(holdingAge + 1);
        holdMatch(totalNumSeats + 1);
    }

    @Test
    public void reserveSeats() throws Exception {
        sleep(holdingAge + 1);
        reserveMatch(totalNumSeats);
        sleep(holdingAge + 1);
    }

    @Test
    public void singleThread() {
        int numSeats = 10;

        SeatHold sh = tsi.findAndHoldSeats(numSeats, getTestEmail());
        Log.logHold(sh, tsi);
        int firstId = sh.get_id();
        sleep(holdingAge + 1);
        assertEquals("Test expired holdings (vacant pool)", totalNumSeats, tsi.get_vacant().size());
        assertEquals("Test expired holdings (holded pool)", 0, tsi.get_holded().size());
        assertEquals("Test expired holdings (reserved pool)", 0, tsi.get_reserved().size());

        sh = tsi.findAndHoldSeats(numSeats, getTestEmail());
        Log.logHold(sh, tsi);
        int secondId = sh.get_id();
        assertEquals("Test non-expired holdings (vacant pool)", totalNumSeats - numSeats, tsi.get_vacant().size());
        assertEquals("Test non-expired holdings (holded pool)", numSeats, tsi.get_holded().size());
        assertEquals("Test non-expired holdings (reserved pool)", 0, tsi.get_reserved().size());

        assertNotSame(String.format("SeatHold ID: %s should be different from SeatHold ID: %s", firstId, secondId), firstId, secondId);

        String confirmCode = tsi.reserveSeats(secondId, getTestEmail());
        Log.logReserve(sh, confirmCode, tsi);
        assertEquals("Test reserved holdings (vacant pool)", totalNumSeats - numSeats, tsi.get_vacant().size());
        assertEquals("Test reserved holdings (holded pool)", 0, tsi.get_holded().size());
        assertEquals("Test reserved holdings (reserved pool)", numSeats, tsi.get_reserved().size());
    }

    @Test
    public void multiThread_holdCompeting() {
        int numSeats = totalNumSeats / 3 + 5;
        Runnable r1 = createHoldThread(numSeats);
        Runnable r2 = createHoldThread(numSeats);
        Runnable r3 = createHoldThread(numSeats);
        r1.run();
        r2.run();
        r3.run();
        assertEquals("Test holding competing",
                totalNumSeats, tsi.get_vacant().size() + tsi.get_holded().size() + tsi.get_reserved().size());
    }

    @Test
    public void multiThread_reserveCompeting() {
        int numSeats = totalNumSeats / 3 + 5;
        Runnable r1 = createReserveThread(numSeats);
        Runnable r2 = createReserveThread(numSeats);
        Runnable r3 = createReserveThread(numSeats);
        r1.run();
        r2.run();
        r3.run();
        assertEquals("Test reserve competing", totalNumSeats, tsi.get_vacant().size() + tsi.get_holded().
                size() + tsi.get_reserved().size());
    }

    private Runnable createHoldThread(int numSeats) {
        return () -> {
            SeatHold sh = tsi.findAndHoldSeats(numSeats, getTestEmail());
            Log.logHold(sh, tsi);
        };
    }

    private Runnable createReserveThread(int numSeats) {
        return () -> {
            SeatHold sh = tsi.findAndHoldSeats(numSeats, getTestEmail());
            String confirmCode = tsi.reserveSeats(sh.get_id(), getTestEmail());
            Log.logReserve(sh, confirmCode, tsi);
        };
    }

    private String getTestEmail() {
        return "test@" + UniqId.uniqueCurrentTimeMS();
    }

    private void holdMatch(int numHoldSeats) {
        int expected = numHoldSeats;
        if (numHoldSeats > totalNumSeats)
            expected = totalNumSeats;
        SeatHold sh = tsi.findAndHoldSeats(numHoldSeats, getTestEmail());
        Log.logHold(sh, tsi);
        assertEquals(expected, sh.get_seats().size());
    }

    private void reserveMatch(int numReserveSeats) {
        int expected = numReserveSeats;
        if (numReserveSeats > totalNumSeats)
            expected = totalNumSeats;
        SeatHold sh = tsi.findAndHoldSeats(numReserveSeats, getTestEmail());
        String confirmCode = tsi.reserveSeats(sh.get_id(), getTestEmail());
        Log.logReserve(sh, confirmCode, tsi);
        assertEquals(expected, tsi.get_reserved().size());
    }

    private void sleep(int sec) {
        synchronized (Thread.currentThread()) {
            try {
                Thread.sleep(sec * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}