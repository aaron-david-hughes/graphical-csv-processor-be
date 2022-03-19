package com.graphicalcsvprocessing.graphicalcsvprocessing.utils;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ProcessingUtilsTest {

    @Test
    public void createCSVWorksWhenValidStringSupplied() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("header1, header2\n");
        sb.append("value11, value21\n");
        sb.append("value12, value22\n");
        sb.append("value13, value23\n");
        sb.append("value14, value24\n");


        CSV result = ProcessingUtils.createCSV(sb);

        assertNotNull(result);
        assertEquals(2, result.getHeaders().size());
        assertEquals(4, result.getRecords().size());
    }

    @Test
    public void createCSVSuppliesEmptyCSVIfStringInvalid() throws IOException {
        StringBuilder sb = new StringBuilder();

        CSV result = ProcessingUtils.createCSV(sb);

        assertNotNull(result);
        assertEquals(0, result.getHeaders().size());
        assertEquals(0, result.getRecords().size());
    }

    @Test
    public void listToStringReturnsEmptyStringWhenNullSupplied() {
        String s = ProcessingUtils.listToString(null);

        assertEquals("", s);
    }

    @Test
    public void listToStringReturnsStringWithNoSpaceFollowingCommasWhenGenuineListSupplied() {
        List<String> input = new ArrayList<>();
        input.add("hello");
        input.add("world");
        input.add("hello");
        input.add("world");

        String s = ProcessingUtils.listToString(input);

        assertEquals("hello,world,hello,world", s);
    }

    @Test
    public void listToStringMaintainsSpacesAndCommasWhenNotMatchingCommaSpaceRegex() {
        List<String> input = new ArrayList<>();
        input.add("hello,world");
        input.add("hello world");

        String s = ProcessingUtils.listToString(input);

        assertEquals("hello,world,hello world", s);
    }

    @Test
    public void parseDoubleShouldReturnZeroWhenStringArgIsNullOrEmpty() {
        String[] strings = {null, ""};

        for (String s : strings) {
            assertEquals(0.0, ProcessingUtils.parseDouble(s), 0);
        }
    }

    @Test
    public void parseDoubleShouldReturnValidDoubleWhenStringIsParsableToDouble() {
        assertEquals(3.142, ProcessingUtils.parseDouble("3.142"), 0);
    }

    @Test
    public void parseDoubleShouldThrowNumberFormatExceptionWhenNonEmptyStringNotParsableToDouble() {
        try {
            ProcessingUtils.parseDouble("Hello World!");
        } catch (Exception e) {
            assertEquals(NumberFormatException.class, e.getClass());
        }
    }
}