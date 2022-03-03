package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.utils.TestCSVBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.utils.TestUtils.countRowsWithSameColumnValue;
import static org.junit.Assert.*;

public class UniqueColumnProcessorTest {

    CSV validCsv;
    CSV invalidCsv;

    @Before
    public void setUp() throws IOException {
        Map<String, CSV> csvs = new TestCSVBuilder().buildCsvInput("MergeRows.csv", "MergeRowsClash.csv");

        validCsv = csvs.get("MergeRows.csv");
        invalidCsv = csvs.get("MergeRowsClash.csv");
    }

    @Test
    public void shouldCreateCsvWithEntirelyUniqueRowsOnSpecifiedColumn() throws IOException {
        CSV output = UniqueColumnProcessor.uniqueColumn(validCsv, "StudentNumber");

        Map<String, Integer> rowCounts = countRowsWithSameColumnValue(output, "StudentNumber");

        for (String rowValue : rowCounts.keySet()) {
            assertEquals((Integer) 1, rowCounts.get(rowValue));
        }
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenDataInAnyRowsClash() throws IOException {
        try {
            UniqueColumnProcessor.uniqueColumn(invalidCsv, "StudentNumber");
            fail("Expected an illegal argument exception to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Cell values in the relevant merge rows clash.", e.getCause().getMessage());
        }
    }

}