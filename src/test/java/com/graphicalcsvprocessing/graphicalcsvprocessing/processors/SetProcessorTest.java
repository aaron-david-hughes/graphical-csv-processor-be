package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.utils.TestCSVBuilder;
import org.apache.commons.csv.CSVRecord;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class SetProcessorTest {

    Map<String, CSV> csvs;

    @Before
    public void setUp() throws IOException {
        csvs = new TestCSVBuilder().buildCsvInput("Scores.csv", "ScoresSubset.csv", "ScoresEmpty.csv", "Alphabetical.csv");
    }

    @Test
    public void shouldGiveGenuineComplimentWhenSubsetIsAProperSubset() {
        CSV set = csvs.get("Scores.csv");
        CSV subset = csvs.get("ScoresSubset.csv");
        CSV output = SetProcessor.getCompliment(set, subset, "StudentNum");

        assertEquals(set.getRecords().size() - subset.getRecords().size(), output.getRecords().size());

        List<String> setColumnValues = comparisonList(set, "StudentNum");
        List<String> subsetColumnValues = comparisonList(subset, "StudentNum");

        for (CSVRecord csvRecord : output.getRecords()) {
            String colValue = csvRecord.get("StudentNum");

            assertTrue(setColumnValues.contains(colValue));
            assertFalse(subsetColumnValues.contains(colValue));
        }
    }

    @Test
    public void shouldGiveEmptyCsvWhenSubsetIsSet() {
        CSV set = csvs.get("Scores.csv");
        CSV subset = csvs.get("Scores.csv");
        CSV output = SetProcessor.getCompliment(set, subset, "StudentNum");

        assertEquals(0, output.getRecords().size());
    }

    @Test
    public void shouldReturnSetWhenEitherSetOrSubsetIsEmpty() {
        CSV scores = csvs.get("Scores.csv");
        CSV empty = csvs.get("ScoresEmpty.csv");
        CSV[][] testSets = {{scores, empty}, {empty, scores}};

        for (CSV[] testSet : testSets) {
            CSV set = testSet[0];
            CSV subset = testSet[1];
            CSV output = SetProcessor.getCompliment(set, subset, "StudentNum");

            assertEquals(testSet[0], output);
        }
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenTwoSetsAreNotComparable() {
        try {
            SetProcessor.getCompliment(csvs.get("Scores.csv"), csvs.get("Alphabetical.csv"), "StudentNum");
            fail("Expected an illegal argument exception to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Compliment must be ran on CSVs with the same headers", e.getMessage());
        }
    }

    public List<String> comparisonList(CSV csv, String column) {
        return csv.getRecords().stream().map(csvRecord -> csvRecord.get(column)).collect(Collectors.toList());
    }
}