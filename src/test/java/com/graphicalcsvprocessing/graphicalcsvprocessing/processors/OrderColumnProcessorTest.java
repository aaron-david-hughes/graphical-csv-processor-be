package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.utils.TestCSVBuilder;
import org.apache.commons.csv.CSVRecord;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.*;

public class OrderColumnProcessorTest {

    CSV alphabetical;
    CSV numeric;

    @Before
    public void setUp() throws IOException {
        Map<String, CSV> csvs = new TestCSVBuilder().buildCsvInput("Alphabetical.csv", "Scores.csv");

        alphabetical = csvs.get("Alphabetical.csv");
        numeric = csvs.get("Scores.csv");
    }

    @Test
    public void shouldOrderTheInputCsvAlphabeticallyAscending() {
        CSV output = OrderColumnProcessor.order(alphabetical, "Alphabet", OrderColumnProcessor.OrderType.ALPHABETICAL_ORDER_ASC);

        String s = "";

        for (CSVRecord csvRecord : output.getRecords()) {
            String s1 = csvRecord.get("Alphabet").toLowerCase();
            assertTrue(s.compareTo(s1) < 0);
            s = s1;
        }
    }

    @Test
    public void shouldOrderTheInputCsvAlphabeticallyDescending() {
        CSV output = OrderColumnProcessor.order(alphabetical, "Alphabet", OrderColumnProcessor.OrderType.ALPHABETICAL_ORDER_DESC);

        String s = "zzzzzzzzzz";

        for (CSVRecord csvRecord : output.getRecords()) {
            String s1 = csvRecord.get("Alphabet").toLowerCase();
            assertTrue(s.compareTo(s1) > 0);
            s = s1;
        }
    }

    @Test
    public void shouldOrderTheInputCsvNumericallyAscending() {
        CSV output = OrderColumnProcessor.order(numeric, "Score", OrderColumnProcessor.OrderType.NUMERIC_ORDER_ASC);

        double d = 0.0;

        for (CSVRecord csvRecord : output.getRecords()) {
            double d1 = Double.parseDouble(csvRecord.get("Score"));
            assertTrue(d <= d1);
            d = d1;
        }
    }

    @Test
    public void shouldOrderTheInputCsvNumericallyDescending() {
        CSV output = OrderColumnProcessor.order(numeric, "Score", OrderColumnProcessor.OrderType.NUMERIC_ORDER_DESC);

        double d = 100.1;

        for (CSVRecord csvRecord : output.getRecords()) {
            double d1 = Double.parseDouble(csvRecord.get("Score"));
            assertTrue(d >= d1);
            d = d1;
        }
    }

    @Test
    public void shouldThrowNumberFormatExceptionWhenNumericOrderAppliedNonNumericColumn() {
        try {
            OrderColumnProcessor.order(alphabetical, "Alphabet", OrderColumnProcessor.OrderType.NUMERIC_ORDER_DESC);
            fail("Expected a number format exception ot be thrown");
        } catch (NumberFormatException ignore) {}
    }

}