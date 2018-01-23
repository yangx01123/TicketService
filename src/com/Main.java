package com;

import com.util.Config;
import com.util.UniqId;
import com.walmart.Seat;
import com.walmart.SeatHold;
import com.walmart.TicketService;
import com.walmart.TicketServiceImp;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    // TODO: use JUnit test instead of tested in main().
    public static void main(String[] args) {
        TicketServiceImp tsi = new TicketServiceImp();
//        ((Runnable) () -> {
//            SeatHold sh = tsi.findAndHoldSeats(5, "test@" + UniqId.getInstance().uniqueCurrentTimeMS());
//            System.out.println(sh);
//        }).run();
        ((Runnable) () -> {
            SeatHold sh = tsi.findAndHoldSeats(5, "test@" + UniqId.getInstance().uniqueCurrentTimeMS());
//            System.out.println(sh);
            synchronized (Thread.currentThread()) {
                try {
//                    Thread.sleep(Config.holdingAge * 1000 + 1);
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    System.out.println("Error");
                }
            }
            System.out.println("seatHoldId: "+sh.get_id());
            String confirmCode = tsi.reserveSeats(sh.get_id(), "test@" + UniqId.getInstance().uniqueCurrentTimeMS());
            System.out.println(sh);
            System.out.println(confirmCode);
            System.out.println(tsi);
        }).run();

    }
}
