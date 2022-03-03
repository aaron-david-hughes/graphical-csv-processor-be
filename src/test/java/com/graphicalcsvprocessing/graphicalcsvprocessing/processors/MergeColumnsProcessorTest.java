package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations.MergeColumnsProcessingNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.utils.TestCSVBuilder;
import org.apache.commons.csv.CSVRecord;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class MergeColumnsProcessorTest {

    Map<String, CSV> csvs;

    @Before
    public void setUp() throws IOException {
        csvs = new TestCSVBuilder().buildCsvInput("Merge.csv", "MergeClash.csv", "MergeNumeric.csv");
    }

    @Test
    public void shouldMergeDataWhenNotClashingStringEquality() throws IOException {
        String[][] columnOrders = {{"Score1", "Score2"}, {"Score2", "Score1"}};

        for (String[] columnOrder : columnOrders) {
            CSV input = csvs.get("Merge.csv");
            MergeColumnsProcessingNode node = new MergeColumnsProcessingNode(
                    "test", "test", "test",
                    columnOrder[0],
                    columnOrder[1],
                    "Score",
                    MergeColumnsProcessor.MergeType.STRING_EQUALITY
            );

            CSV output = MergeColumnsProcessor.merge(input, node);

            assertEquals(input.getHeaders().size() - 1, output.getHeaders().size());
            assertEquals(input.getRecords().size(), output.getRecords().size());

            List<CSVRecord> inputRecords = input.getRecords();
            List<CSVRecord> outputRecords = output.getRecords();
            for (int i = 0; i < inputRecords.size(); i++) {
                CSVRecord inputRecord = inputRecords.get(i);
                CSVRecord outputRecord = outputRecords.get(i);

                if (isNullSpacesOrEmpty(inputRecord.get("Score1"))) {
                    assertEquals(outputRecord.get("Score"), inputRecord.get("Score2"));
                } else {
                    assertEquals(outputRecord.get("Score"), inputRecord.get("Score1"));
                }
            }
        }
    }

    @Test
    public void shouldMergeDataWhenNotClashingNumericEquality() throws IOException {
        String[][] columnOrders = {{"Score1", "Score2"}, {"Score2", "Score1"}};

        for (String[] columnOrder : columnOrders) {
            CSV input = csvs.get("MergeNumeric.csv");
            MergeColumnsProcessingNode node = new MergeColumnsProcessingNode(
                    "test", "test", "test",
                    columnOrder[0],
                    columnOrder[1],
                    "Score",
                    MergeColumnsProcessor.MergeType.NUMERIC_EQUALITY
            );

            CSV output = MergeColumnsProcessor.merge(input, node);

            assertEquals(input.getHeaders().size() - 1, output.getHeaders().size());
            assertEquals(input.getRecords().size(), output.getRecords().size());

            List<CSVRecord> inputRecords = input.getRecords();
            List<CSVRecord> outputRecords = output.getRecords();
            for (int i = 0; i < inputRecords.size(); i++) {
                CSVRecord inputRecord = inputRecords.get(i);
                CSVRecord outputRecord = outputRecords.get(i);

                if (isNullSpacesOrEmpty(inputRecord.get("Score1"))) {
                    assertEquals(outputRecord.get("Score"), inputRecord.get("Score2"));
                } else {
                    assertEquals(outputRecord.get("Score"), inputRecord.get("Score1"));
                }
            }
        }
    }

    @Test
    public void shouldThrowExceptionWhenDataToBeMergedIsClashing() throws IOException {
        String[][] columnOrders = {{"Score1", "Score2"}, {"Score2", "Score1"}};

        for (String[] columnOrder : columnOrders) {
            CSV input = csvs.get("MergeClash.csv");
            MergeColumnsProcessingNode node = new MergeColumnsProcessingNode(
                    "test", "test", "test",
                    columnOrder[0],
                    columnOrder[1],
                    "Score",
                    MergeColumnsProcessor.MergeType.STRING_EQUALITY
            );

            try {
                MergeColumnsProcessor.merge(input, node);
                fail("Expected an illegal argument exception to be thrown");
            } catch (IllegalArgumentException e) {
                assertEquals("Merge cannot be carried out as data in columns clash.", e.getMessage());
            }
        }
    }

    private boolean isNullSpacesOrEmpty(String v) {
        return v == null || v.isEmpty() || v.isBlank();
    }
}