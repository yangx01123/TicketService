package com.util;

import com.app.SeatHold;
import com.app.TicketServiceImp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Customized log utilities.
 */
public class Log {

    private static final Logger logger = LogManager.getLogger(Log.class);

    public static void logHold(SeatHold seatHold, TicketServiceImp tsi) {
        logger.trace(String.format("%d seats are held by[%d]",
                seatHold.get_seats().size(), seatHold.get_id()));
        logger.trace(String.format("Current seats distribution %s", tsi));
    }

    public static void logReserve(SeatHold seatHold, String confirmationCode, TicketServiceImp tsi) {
        logger.trace(String.format("%d seats are reserved with confirmCode[%s]",
                seatHold.get_seats().size(), confirmationCode));
        logger.trace(String.format("Current seats distribution %s", tsi));
    }
}
