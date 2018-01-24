import com.util.Config;
import com.util.UniqId;
import com.walmart.SeatHold;
import com.walmart.TicketServiceImp;
import org.junit.Test;

import static com.util.Config.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Created by Yang on 23/01/2018.
 */
public class TicketServiceImp_test {
    private TicketServiceImp tsi = new TicketServiceImp();

    @Test
    public void test_singleThread() {
        int numSeats = 10;

        SeatHold sh = tsi.findAndHoldSeats(numSeats, "test@" + UniqId.getInstance().uniqueCurrentTimeMS());
        int firstId = sh.get_id();
        synchronized (Thread.currentThread()) {
            try {
                Thread.sleep((holdingAge + 1) * 1000);
            } catch (InterruptedException e) {
                System.out.println("Error");
            }
        }
        assertEquals("Test of expired holdings (vacant pool) " + tsi, totalNumSeats, tsi.get(pool.vacant).size());
        assertEquals("Test of expired holdings (holded pool) " + tsi, 0, tsi.get(pool.holded).size());
        assertEquals("Test of expired holdings (committed pool) " + tsi, 0, tsi.get(pool.committed).size());

        sh = tsi.findAndHoldSeats(numSeats, "test@" + UniqId.getInstance().uniqueCurrentTimeMS());
        int secondId = sh.get_id();
        synchronized (Thread.currentThread()) {
            try {
                Thread.sleep((holdingAge - 1) * 1000);
            } catch (InterruptedException e) {
                System.out.println("Error");
            }
        }
        assertEquals("Test of non-expired holdings (vacant pool) " + tsi, totalNumSeats - numSeats, tsi.get(pool.vacant).size());
        assertEquals("Test of non-expired holdings (holded pool) " + tsi, numSeats, tsi.get(pool.holded).size());
        assertEquals("Test of non-expired holdings (committed pool) " + tsi, 0, tsi.get(pool.committed).size());

        assertNotSame(String.format("SeatHold ID: %s should be different from SeatHold ID: %s", firstId, secondId), firstId, secondId);

        String confirmCode = tsi.reserveSeats(secondId, "test@" + UniqId.getInstance().uniqueCurrentTimeMS());
        assertEquals("Test of committed holdings (vacant pool) " + tsi, totalNumSeats - numSeats, tsi.get(pool.vacant).size());
        assertEquals("Test of committed holdings (holded pool) " + tsi, 0, tsi.get(pool.holded).size());
        assertEquals("Test of committed holdings (committed pool) " + tsi, numSeats, tsi.get(pool.committed).size());
        System.out.println(sh);
        System.out.println(confirmCode);
        System.out.println(tsi);
    }

    @Test
    public void multiThread() {

    }
}
