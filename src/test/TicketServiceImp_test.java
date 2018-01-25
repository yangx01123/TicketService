import com.app.SeatHold;
import com.app.TicketServiceImp;
import com.util.UniqId;
import org.junit.Test;

import static com.util.Config.holdingAge;
import static com.util.Config.pool.*;
import static com.util.Config.totalNumSeats;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * Test class for TicketServiceImp.
 */
public class TicketServiceImp_test {
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

    }

    @Test
    public void singleThread() {
        int numSeats = 10;

        SeatHold sh = tsi.findAndHoldSeats(numSeats, getTestEmail());
        int firstId = sh.get_id();
        sleep(holdingAge + 1);
        assertEquals("Test of expired holdings (vacant pool) " + tsi, totalNumSeats, tsi.get(vacant).size());
        assertEquals("Test of expired holdings (holded pool) " + tsi, 0, tsi.get(holded).size());
        assertEquals("Test of expired holdings (committed pool) " + tsi, 0, tsi.get(committed).size());

        sh = tsi.findAndHoldSeats(numSeats, getTestEmail());
        int secondId = sh.get_id();
        assertEquals("Test of non-expired holdings (vacant pool) " + tsi, totalNumSeats - numSeats, tsi.get(vacant).size());
        assertEquals("Test of non-expired holdings (holded pool) " + tsi, numSeats, tsi.get(holded).size());
        assertEquals("Test of non-expired holdings (committed pool) " + tsi, 0, tsi.get(committed).size());

        assertNotSame(String.format("SeatHold ID: %s should be different from SeatHold ID: %s", firstId, secondId), firstId, secondId);

        String confirmCode = tsi.reserveSeats(secondId, getTestEmail());
        assertEquals("Test of committed holdings (vacant pool) " + tsi, totalNumSeats - numSeats, tsi.get(vacant).size());
        assertEquals("Test of committed holdings (holded pool) " + tsi, 0, tsi.get(holded).size());
        assertEquals("Test of committed holdings (committed pool) " + tsi, numSeats, tsi.get(committed).size());
        System.out.println(sh);
        System.out.println(confirmCode);
        System.out.println(tsi);
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
        assertEquals("Test of holding competing", totalNumSeats, tsi.get(vacant).size() + tsi.get(holded).size() + tsi.get(committed).size());
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
        assertEquals("Test of reserve competing", totalNumSeats, tsi.get(vacant).size() + tsi.get(holded).size() + tsi.get(committed).size());
    }

    private Runnable createHoldThread(int numSeats) {
        return () -> {
            SeatHold sh = tsi.findAndHoldSeats(numSeats, getTestEmail());
            System.out.println(sh.get_seats().size());
            System.out.println(tsi);
        };
    }

    private Runnable createReserveThread(int numSeats) {
        return () -> {
            SeatHold sh = tsi.findAndHoldSeats(numSeats, getTestEmail());
            String confirmCode = tsi.reserveSeats(sh.get_id(), getTestEmail());
            System.out.println(confirmCode);
            System.out.println(sh.get_seats().size());
            System.out.println(tsi);
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
        assertEquals(expected, sh.get_seats().size());
        System.out.println(sh);
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