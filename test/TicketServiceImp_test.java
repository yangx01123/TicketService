import com.util.UniqId;
import com.walmart.SeatHold;
import com.walmart.TicketServiceImp;
import org.junit.Test;

import static com.util.Config.*;
import static com.util.Config.pool.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * Created by Yang on 23/01/2018.
 */
public class TicketServiceImp_test {
    private TicketServiceImp tsi = new TicketServiceImp();

    @Test
    public void singleThread() {
        int numSeats = 10;

        SeatHold sh = tsi.findAndHoldSeats(numSeats, "test@" + UniqId.getInstance().uniqueCurrentTimeMS());
        int firstId = sh.get_id();
        synchronized (Thread.currentThread()) {
            try {
                Thread.sleep((holdingAge + 1) * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        assertEquals("Test of expired holdings (vacant pool) " + tsi, totalNumSeats, tsi.get(vacant).size());
        assertEquals("Test of expired holdings (holded pool) " + tsi, 0, tsi.get(pool.holded).size());
        assertEquals("Test of expired holdings (committed pool) " + tsi, 0, tsi.get(pool.committed).size());

        sh = tsi.findAndHoldSeats(numSeats, "test@" + UniqId.getInstance().uniqueCurrentTimeMS());
        int secondId = sh.get_id();
        synchronized (Thread.currentThread()) {
            try {
                Thread.sleep((holdingAge - 1) * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        assertEquals("Test of non-expired holdings (vacant pool) " + tsi, totalNumSeats - numSeats, tsi.get(vacant).size());
        assertEquals("Test of non-expired holdings (holded pool) " + tsi, numSeats, tsi.get(pool.holded).size());
        assertEquals("Test of non-expired holdings (committed pool) " + tsi, 0, tsi.get(pool.committed).size());

        assertNotSame(String.format("SeatHold ID: %s should be different from SeatHold ID: %s", firstId, secondId), firstId, secondId);

        String confirmCode = tsi.reserveSeats(secondId, "test@" + UniqId.getInstance().uniqueCurrentTimeMS());
        assertEquals("Test of committed holdings (vacant pool) " + tsi, totalNumSeats - numSeats, tsi.get(vacant).size());
        assertEquals("Test of committed holdings (holded pool) " + tsi, 0, tsi.get(pool.holded).size());
        assertEquals("Test of committed holdings (committed pool) " + tsi, numSeats, tsi.get(pool.committed).size());
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
            SeatHold sh = tsi.findAndHoldSeats(numSeats, "test@" + UniqId.getInstance().uniqueCurrentTimeMS());
            System.out.println(sh.get_seats().size());
            System.out.println(tsi);
        };
    }

    private Runnable createReserveThread(int numSeats) {
        return () -> {
            SeatHold sh = tsi.findAndHoldSeats(numSeats, "test@" + UniqId.getInstance().uniqueCurrentTimeMS());
            String confirmCode = tsi.reserveSeats(sh.get_id(), "test@" + UniqId.getInstance().uniqueCurrentTimeMS());
            System.out.println(confirmCode);
            System.out.println(sh.get_seats().size());
            System.out.println(tsi);
        };
    }

}
