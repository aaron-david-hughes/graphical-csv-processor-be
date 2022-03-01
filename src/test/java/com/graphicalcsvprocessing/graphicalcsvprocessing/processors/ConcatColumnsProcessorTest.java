package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.utils.TestCSVBuilder;
import org.apache.commons.csv.CSVRecord;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ConcatColumnsProcessorTest {

    private CSV csv;

    @Before
    public void setUp() throws IOException {
        csv = new TestCSVBuilder().buildCsvInput("Attendance.csv").get("Attendance.csv");
    }

    @Test
    public void shouldConcatTwoColumnsAndAddToEndOfCsv() throws IOException {
        CSV output = ConcatColumnsProcessor.concat(csv, "Attendant", "Prac 4", "testConcatColumn");

        assertNotNull(output);
        assertEquals(csv.getHeaders().size() + 1, output.getHeaders().size());
        assertEquals("testConcatColumn", output.getHeaders().get(output.getHeaders().size() - 1));
        assertEquals(csv.getRecords().size(), output.getRecords().size());

        for (int i = 0; i < output.getRecords().size(); i++) {
            CSVRecord csvRecord = output.getRecords().get(i);
            CSVRecord oldRecord = csv.getRecords().get(i);
            assertEquals(csvRecord.get("testConcatColumn"), oldRecord.get("Attendant") + oldRecord.get("Prac 4"));
        }
    }
}