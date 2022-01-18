package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.binaryOperations.JoinProcessingNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.utils.TestCSVBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.*;
import static com.graphicalcsvprocessing.graphicalcsvprocessing.processors.JoinProcessor.JoinType;

/**
 * Note: unit testing operations is on the code running
 * More extensive result verifying tests will be carried out in User Acceptance Testing
 * Treating the rows for unit test as though the columns data are unique and so map one to one max
 */
public class JoinProcessorTest {

    private final TestCSVBuilder testCSVBuilder = new TestCSVBuilder();

    private CSV[] orderedData;

    private CSV[] reorderedData;

    private JoinProcessingNode j;

    private final String leftCol = "Attendance.Attendant";

    private final String rightCol = "Scores.StudentNum";

    @Before
    public void setUp() throws IOException {
        String rightFilename = "Scores.csv";
        String leftFilename = "Attendance.csv";

        Map<String, CSV> inputData = testCSVBuilder.buildCsvInput(leftFilename, rightFilename);

        orderedData = new CSV[] {inputData.get(leftFilename), inputData.get(rightFilename)};
        reorderedData = new CSV[] {inputData.get(rightFilename), inputData.get(leftFilename)};
    }

    @Test
    public void leftJoinCreatesExpectedData() throws IOException {
        j = new JoinProcessingNode(
            "joinProcessingTest",
            "processing",
            "join",
            leftCol,
            rightCol,
            JoinType.LEFT
        );

        CSV result = JoinProcessor.join(j, orderedData);

        assertNotNull(result);

        assertEquals(13, result.getHeaders().size());
        checkHeaders(orderedData, result, false);

        assertEquals(11, result.getRecords().size());
    }

    @Test
    public void rightJoinCreatesExpectedData() throws IOException {
        j = new JoinProcessingNode(
                "joinProcessingTest",
                "processing", "join",
                leftCol,
                rightCol,
                JoinType.RIGHT
        );

        CSV result = JoinProcessor.join(j, orderedData);

        assertNotNull(result);

        assertEquals(13, result.getHeaders().size());
        checkHeaders(orderedData, result, true);

        assertEquals(10, result.getRecords().size());
    }

    @Test
    public void innerJoinCreatesExpectedData() throws IOException {
        j = new JoinProcessingNode(
                "joinProcessingTest",
                "processing", "join",
                leftCol,
                rightCol,
                JoinType.INNER
        );

        CSV result = JoinProcessor.join(j, orderedData);

        assertNotNull(result);

        assertEquals(13, result.getHeaders().size());
        checkHeaders(orderedData, result, false);

        assertEquals(7, result.getRecords().size());
    }

    @Test
    public void innerJoinCreatesSameExpectedDataWhenReversedDataInput() throws IOException {
        j = new JoinProcessingNode(
                "joinProcessingTest",
                "processing", "join",
                rightCol,
                leftCol,
                JoinType.INNER
        );

        CSV result = JoinProcessor.join(j, reorderedData);

        assertNotNull(result);

        assertEquals(13, result.getHeaders().size());
        checkHeaders(reorderedData, result, false);

        assertEquals(7, result.getRecords().size());
    }

    @Test
    public void outerJoinCreatesExpectedData() throws IOException {
        j = new JoinProcessingNode(
                "joinProcessingTest",
                "processing", "join",
                leftCol,
                rightCol,
                JoinType.OUTER
        );

        CSV result = JoinProcessor.join(j, orderedData);

        assertNotNull(result);

        assertEquals(13, result.getHeaders().size());
        checkHeaders(orderedData, result, false);

        assertEquals(14, result.getRecords().size());
    }

    @Test
    public void outerJoinCreatesSameExpectedDataWhenReversedDataInput() throws IOException {
        j = new JoinProcessingNode(
                "joinProcessingTest",
                "processing", "join",
                rightCol,
                leftCol,
                JoinType.OUTER
        );

        CSV result = JoinProcessor.join(j, reorderedData);

        assertNotNull(result);

        assertEquals(13, result.getHeaders().size());
        checkHeaders(reorderedData, result, false);

        assertEquals(14, result.getRecords().size());
    }

    @Test
    public void throwsIllegalArgumentExceptionIfColumnNameIsNotFound() throws IOException {
        j = new JoinProcessingNode(
                "joinProcessingTest",
                "processing", "join",
                leftCol,
                rightCol,
                JoinType.OUTER
        );

        try {
            JoinProcessor.join(j, reorderedData);
            fail("Expected an IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "Mapping for Attendance.Attendant not found, expected one of [Scores.StudentNum, Scores.Score]",
                    e.getMessage()
            );
        }
    }

    private void checkHeaders(CSV[] orderedData, CSV result, boolean rightJoin) {
        int first = 0;
        int second = 1;

        if (rightJoin) {
            first = 1;
            second = 0;
        }

        for (int i = 0; i < result.getHeaders().size(); i++) {
            if (i < orderedData[first].getHeaders().size())
                assertEquals(
                        orderedData[first].getHeaders().get(i),
                        result.getHeaders().get(i)
                );
            else
                assertEquals(
                        orderedData[second].getHeaders().get(i - orderedData[first].getHeaders().size()),
                        result.getHeaders().get(i)
                );
        }
    }
}