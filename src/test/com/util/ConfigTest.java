package com.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tester for Config class.
 */
public class ConfigTest {
    @Test
    public void valueLoading() {
        assertEquals("Validate 'totalNumSeats' from configure file", 297, Config.totalNumSeats);
        assertEquals("Validate 'holdingAge' from configure file", 5, Config.holdingAge);
    }
}