package com.util;

import java.util.ResourceBundle;

/**
 * System configuration variables.
 */
public class Config {
    public static int totalNumSeats;
    public static int holdingAge;

    static {
        ResourceBundle rb = ResourceBundle.getBundle("config");
        totalNumSeats = Integer.valueOf(rb.getString("totalNumSeats"));
        holdingAge = Integer.valueOf(rb.getString("holdingAge"));
    }
}
