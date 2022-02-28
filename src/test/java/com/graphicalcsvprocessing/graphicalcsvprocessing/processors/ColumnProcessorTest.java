package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.utils.TestCSVBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class ColumnProcessorTest {

    CSV csv;

    @Before
    public void setUp () throws IOException {
        Map<String, CSV> csvs = new TestCSVBuilder().buildCsvInput("Attendance.csv");
        csv = csvs.get("Attendance.csv");
    }

    @Test
    public void shouldTakeSpecifiedColumnsWhenTheyAllExist() throws IOException {
        List<String> l = asList("Prac 1", "Prac 2", "Prac 5");
        CSV output = ColumnProcessor.takeColumns(csv, l);

        assertNotNull(output);

        List<String> outputHeaders = output.getHeaders();

        assertEquals(3, outputHeaders.size());
        for (String outputHeader : outputHeaders) {
            assertTrue(l.contains(outputHeader));
        }
    }

    @Test
    public void shouldThrowExceptionIfAnyTakenColumnIsUnidentifiable() throws IOException {
        try {
            ColumnProcessor.takeColumns(csv, asList("Prac 1", "Prac 2", "not present"));
            fail("Expected an illegal argument exception to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Column name not found in input 'not present'.", e.getMessage());
        }
    }

    @Test
    public void shouldDropSpecifiedColumnsWhenTheyAllExist() throws IOException {
        List<String> l = asList("Prac 1", "Prac 2", "Prac 5");
        CSV output = ColumnProcessor.dropColumns(csv, l);

        assertNotNull(output);

        List<String> outputHeaders = output.getHeaders();

        assertEquals(csv.getRecords().size() - 3, outputHeaders.size());
        for (String outputHeader : outputHeaders) {
            assertFalse(l.contains(outputHeader));
        }
    }

    @Test
    public void shouldThrowExceptionIfAnyDroppedColumnIsUnidentifiable() throws IOException {
        try {
            ColumnProcessor.dropColumns(csv, asList("Prac 1", "Prac 2", "not present"));
            fail("Expected an illegal argument exception to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Column name not found in input 'not present'.", e.getMessage());
        }
    }
}