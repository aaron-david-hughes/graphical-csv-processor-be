package com.graphicalcsvprocessing.graphicalcsvprocessing.models;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.io.input.BOMInputStream;
import org.junit.Before;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.Assert.*;

public class CSVTest {

    @Before
    public void setUp() throws IOException {
        //TODO create a csv parser genuinely as this will make the unit test much easier - or mock a lot - either may be ok
//        String =
//        InputStreamReader reader = new InputStreamReader(new BOMInputStream(new ByteArrayInputStream(sb.toString().getBytes()), false));
//        new CSVParser(reader, CSVFormat.Builder.create().setHeader().build()));
    }
}