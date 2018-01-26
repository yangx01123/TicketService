package com.util;

import static org.junit.Assert.*;

/**
 * Created by Yang on 25/01/2018.
 */
import com.util.UniqId;
import org.junit.Test;

import static org.junit.Assert.assertNotSame;

/**
 * Test class for UniqId.
 */
public class UniqIdTest {

    @Test
    public void uniqValidation() {
        int numberRun = 50;
        for (int i = 0; i < numberRun; i++) {
            assertNotSame("Validate uniqueness of long",
                    UniqId.uniqueCurrentTimeMS(), UniqId.uniqueCurrentTimeMS());
            assertNotSame("Validate uniqueness of int",
                    UniqId.uniqueInt(), UniqId.uniqueInt());
        }
    }
}
