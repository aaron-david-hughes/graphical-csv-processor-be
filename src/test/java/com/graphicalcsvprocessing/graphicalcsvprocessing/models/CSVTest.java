package com.graphicalcsvprocessing.graphicalcsvprocessing.models;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.*;

public class CSVTest {

    CSV csv;

    @Before
    public void setUp() throws IOException {
        //TODO create a csv parser genuinely as this will make the unit test much easier - or mock a lot - either may be ok
        File file = new File("src/test/resources/Attendance.csv");
        MockMultipartFile f = new MockMultipartFile(
                "Attendance.csv",
                "Attendance.csv",
                MediaType.TEXT_PLAIN_VALUE,
                Files.readAllBytes(file.toPath())
        );

        InputStreamReader reader = new InputStreamReader(
                new BOMInputStream(new ByteArrayInputStream(f.getBytes()), false)
        );

        csv = new CSV(new CSVParser(reader, CSVFormat.Builder.create().setHeader().build()));
    }

    @Test
    public void testGetHeaders() {
        List<String> headers = csv.getHeaders();

        assertEquals(11, headers.size());
        assertEquals("Attendant", headers.get(0));
    }

    @Test
    public void testGetRecords() {
        List<CSVRecord> records = csv.getRecords();

        assertEquals(11, records.size());
        assertEquals("40232084", records.get(0).get(0));
    }

    @Test
    public void testGetOutputStream() {
        OutputStream outputStream = csv.getOutputStream();

        assertTrue(outputStream instanceof ByteArrayOutputStream);

        String s = outputStream.toString();
        String[] lines = s.split("\n");

        assertTrue(lines[0].startsWith("Attendant"));
        assertTrue(lines[1].startsWith("40232084"));
    }
}