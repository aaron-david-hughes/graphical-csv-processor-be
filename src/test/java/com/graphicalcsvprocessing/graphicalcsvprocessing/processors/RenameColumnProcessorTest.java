package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.utils.TestCSVBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class RenameColumnProcessorTest {

    CSV csv;

    @Before
    public void setUp() throws IOException {
        csv = new TestCSVBuilder().buildCsvInput("Alphabetical.csv").get("Alphabetical.csv");
    }

    @Test
    public void shouldRenameColumnAsSpecified() throws IOException {
        CSV output = RenameColumnProcessor.renameColumn(csv, "Alphabet", "Test.New_Column");

        assertEquals(csv.getHeaders().size(), output.getHeaders().size());
        assertEquals(csv.getRecords().size(), output.getRecords().size());
        assertFalse(output.getHeaders().contains("Alphabet"));
        assertTrue(output.getHeaders().contains("Test.New_Column"));
    }

}