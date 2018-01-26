package com.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Yang on 25/01/2018.
 */
public class ConfigTest {
    @Test
    public void valueLoading() {
        Config cf = new Config();
        assertEquals("", 297, Config.totalNumSeats);
        assertEquals("", 5, Config.holdingAge);
    }
}