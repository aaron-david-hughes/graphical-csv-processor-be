package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.utils.TestCSVBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.utils.TestUtils.countRowsWithSameColumnValue;
import static org.junit.Assert.*;

public class MergeRowsProcessorTest {

    CSV validCsv;
    CSV invalidCsv;

    @Before
    public void setUp() throws IOException {
        Map<String, CSV> csvs = new TestCSVBuilder().buildCsvInput("MergeRows.csv", "MergeRowsClash.csv");
        validCsv = csvs.get("MergeRows.csv");
        invalidCsv = csvs.get("MergeRowsClash.csv");
    }

    @Test
    public void shouldMakeNoChangeToCsvIfSingleRowMatchesValueToBeMerged() throws IOException {
        CSV output = MergeRowsProcessor.mergeRow(validCsv, "StudentNumber", "1114");

        assertEquals(validCsv, output);
    }

    @Test
    public void shouldMakeNoChangeToCsvIfNoRowsMatchValueToBeMerged() throws IOException {
        CSV output = MergeRowsProcessor.mergeRow(validCsv, "StudentNumber", "1115");

        assertEquals(validCsv, output);
    }

    @Test
    public void shouldMergeTwoRowsWithSomeMissingValuesInInputCsvPuttingMergedRowInFirstAppearingPosition() throws IOException {
        CSV output = MergeRowsProcessor.mergeRow(validCsv, "StudentNumber", "1112");

        assertEquals(validCsv.getRecords().size() - 1, output.getRecords().size());
        assertEquals((Integer) 1, countRowsWithSameColumnValue(output, "StudentNumber").get("1112"));
    }

    @Test
    public void shouldMergeTwoExactlyEqualRowsInInputCsvPuttingMergedRowInFirstAppearingPosition() throws IOException {
        CSV output = MergeRowsProcessor.mergeRow(validCsv, "StudentNumber", "1113");

        assertEquals(validCsv.getRecords().size() - 1, output.getRecords().size());
        assertEquals((Integer) 1, countRowsWithSameColumnValue(output, "StudentNumber").get("1113"));
    }

    @Test
    public void shouldMergeThreeRowsInInputCsvPuttingMergedRowInFirstAppearingPosition() throws IOException {
        CSV output = MergeRowsProcessor.mergeRow(validCsv, "StudentNumber", "1111");

        assertEquals(validCsv.getRecords().size() - 2, output.getRecords().size());
        assertEquals((Integer) 1, countRowsWithSameColumnValue(output, "StudentNumber").get("1111"));
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenDataInMergingRowsClashes() throws IOException {
        try {
            MergeRowsProcessor.mergeRow(invalidCsv, "StudentNumber", "1113");
            fail("Expected an illegal argument exception to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Cell values in the relevant merge rows clash.", e.getCause().getMessage());
        }
    }
}