package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations.FilterProcessingNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.utils.TestCSVBuilder;
import org.apache.commons.csv.CSVRecord;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class FilterProcessorTest {

    CSV csv;

    @Before
    public void setUp() throws IOException {
        csv = new TestCSVBuilder().buildCsvInput("Scores.csv").get("Scores.csv");
    }

    @Test
    public void shouldFilterOnStringEquality() {
        FilterProcessingNode node = new FilterProcessingNode(
                "test", "test", "test",
                "StudentNum",
                "40232090",
                FilterProcessor.FilterType.STRING_EQUALITY,
                true);

        CSV output = FilterProcessor.filter(csv, "StudentNum", node);

        List<CSVRecord> outputRecords = output.getRecords();
        assertEquals(1, outputRecords.size());
        assertEquals("40232090", outputRecords.get(0).get("StudentNum"));
    }

    @Test
    public void shouldFilterOnStringInequality() {
        FilterProcessingNode node = new FilterProcessingNode(
                "test", "test", "test",
                "StudentNum",
                "40232090",
                FilterProcessor.FilterType.STRING_EQUALITY,
                false);

        CSV output = FilterProcessor.filter(csv, "StudentNum", node);

        List<CSVRecord> outputRecords = output.getRecords();
        assertEquals(csv.getRecords().size() - 1, outputRecords.size());
        for (CSVRecord csvRecord : outputRecords) {
            assertNotEquals("40232090", csvRecord.get("StudentNum"));
        }
    }

    @Test
    public void shouldFilterOnNumericEquality() {
        FilterProcessingNode node = new FilterProcessingNode(
                "test", "test", "test",
                "StudentNum",
                "40232090",
                FilterProcessor.FilterType.NUMERIC_EQUALITY,
                true);

        CSV output = FilterProcessor.filter(csv, "StudentNum", node);

        List<CSVRecord> outputRecords = output.getRecords();
        assertEquals(1, outputRecords.size());
        assertEquals("40232090", outputRecords.get(0).get("StudentNum"));
    }

    @Test
    public void shouldFilterOnNumericInequality() {
        FilterProcessingNode node = new FilterProcessingNode(
                "test", "test", "test",
                "StudentNum",
                "40232090",
                FilterProcessor.FilterType.NUMERIC_EQUALITY,
                false);

        CSV output = FilterProcessor.filter(csv, "StudentNum", node);

        List<CSVRecord> outputRecords = output.getRecords();
        assertEquals(csv.getRecords().size() - 1, outputRecords.size());
        for (CSVRecord csvRecord : outputRecords) {
            assertNotEquals("40232090", csvRecord.get("StudentNum"));
        }
    }

    @Test
    public void shouldFilterOnGreaterThan() {
        FilterProcessingNode node = new FilterProcessingNode(
                "test", "test", "test",
                "Score",
                "55",
                FilterProcessor.FilterType.GREATER_THAN,
                true);

        CSV output = FilterProcessor.filter(csv, "Score", node);

        List<CSVRecord> outputRecords = output.getRecords();
        assertEquals(4, outputRecords.size());
        for (CSVRecord csvRecord : outputRecords) {
            assertTrue(Long.parseLong(csvRecord.get("Score")) > 55);
        }
    }

    @Test
    public void shouldFilterOnGreaterThanOrEqual() {
        FilterProcessingNode node = new FilterProcessingNode(
                "test", "test", "test",
                "Score",
                "55",
                FilterProcessor.FilterType.GREATER_THAN_OR_EQUAL,
                true);

        CSV output = FilterProcessor.filter(csv, "Score", node);

        List<CSVRecord> outputRecords = output.getRecords();
        assertEquals(5, outputRecords.size());
        for (CSVRecord csvRecord : outputRecords) {
            assertTrue(Long.parseLong(csvRecord.get("Score")) >= 55);
        }
    }

    @Test
    public void shouldFilterOnLessThan() {
        FilterProcessingNode node = new FilterProcessingNode(
                "test", "test", "test",
                "Score",
                "55",
                FilterProcessor.FilterType.LESS_THAN,
                true);

        CSV output = FilterProcessor.filter(csv, "Score", node);

        List<CSVRecord> outputRecords = output.getRecords();
        assertEquals(5, outputRecords.size());
        for (CSVRecord csvRecord : outputRecords) {
            assertTrue(Long.parseLong(csvRecord.get("Score")) < 55);
        }
    }

    @Test
    public void shouldFilterOnLessThanOrEqual() {
        FilterProcessingNode node = new FilterProcessingNode(
                "test", "test", "test",
                "Score",
                "55",
                FilterProcessor.FilterType.LESS_THAN_OR_EQUAL,
                true);

        CSV output = FilterProcessor.filter(csv, "Score", node);

        List<CSVRecord> outputRecords = output.getRecords();
        assertEquals(6, outputRecords.size());
        for (CSVRecord csvRecord : outputRecords) {
            assertTrue(Long.parseLong(csvRecord.get("Score")) <= 55);
        }
    }

    @Test
    public void shouldThrowExceptionWhenConditionIsNotParsableToDouble() {
        FilterProcessor.FilterType[] filterTypes = {
                FilterProcessor.FilterType.NUMERIC_EQUALITY,
                FilterProcessor.FilterType.GREATER_THAN,
                FilterProcessor.FilterType.GREATER_THAN_OR_EQUAL,
                FilterProcessor.FilterType.LESS_THAN,
                FilterProcessor.FilterType.LESS_THAN_OR_EQUAL,
        };

        for (FilterProcessor.FilterType filterType : filterTypes) {
            try {
                FilterProcessingNode node = new FilterProcessingNode(
                        "test", "test", "test",
                        "StudentNum",
                        "seven",
                        filterType,
                        true);

                FilterProcessor.filter(csv, "StudentNum", node);
                fail("Expected an illegal argument exception to be thrown");
            } catch (IllegalArgumentException e) {
                assertEquals("Cannot carry out numeric filter on cell without number", e.getMessage());
            }
        }
    }
}