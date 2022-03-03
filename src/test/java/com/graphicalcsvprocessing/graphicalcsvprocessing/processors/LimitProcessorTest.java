package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.utils.TestCSVBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class LimitProcessorTest {

    CSV csv;

    @Before
    public void setUp() throws IOException {
        csv = new TestCSVBuilder().buildCsvInput("Scores.csv").get("Scores.csv");
    }

    @Test
    public void shouldProduceACsvWithRecordNumberEqualToLimit() {
        int[] limits = {0, 1, 9, 10};

        for (int limit : limits) {
            CSV output = LimitProcessor.limit(csv, limit);

            assertEquals(limit, output.getRecords().size());
        }
    }

    @Test
    public void shouldReturnInputCscIfLimitIsLargerThanRecordNumber() {
        int limit = 11;

        CSV output = LimitProcessor.limit(csv, limit);

        assertEquals(csv, output);
    }

    @Test
    public void shouldNotAcceptALimitLessThanZero() {
        int limit = -1;

        try {
            LimitProcessor.limit(csv, limit);
            fail("Expected an illegal argument exception to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Limit may not be negative", e.getMessage());
        }
    }
}