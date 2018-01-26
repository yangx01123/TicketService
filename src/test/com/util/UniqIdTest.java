package com.util;

import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * Tester for UniqId class.
 */
public class UniqIdTest {

    @Test
    public void uniqValidation() {
        int numberRun = 50;
        HashSet<Long> longSet = new HashSet<>();
        HashSet<Integer> intSet = new HashSet<>();
        for (int i = 0; i < numberRun; i++) {
            assertEquals("Check uniqueness of long generator",
                    true, longSet.add(UniqId.uniqueCurrentTimeMS()));
            assertEquals("Check uniqueness of int generator",
                    true, intSet.add(UniqId.uniqueInt()));
        }
    }
}
