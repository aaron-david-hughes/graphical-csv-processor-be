package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CorrespondingCSV;
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

    private CSV[] csvs;

    private final String col0 = "Attendant";

    private final String col1 = "StudentNum";

    @Before
    public void setUp() throws IOException {
        String rightFilename = "Scores.csv";
        String leftFilename = "Attendance.csv";

        Map<String, CSV> inputData = testCSVBuilder.buildCsvInput(leftFilename, rightFilename);

        csvs = new CSV[] {inputData.get(leftFilename), inputData.get(rightFilename)};
    }

    @Test
    public void leftJoinCreatesExpectedData() throws IOException {
        CorrespondingCSV left = new CorrespondingCSV(col0, csvs[0]);
        CorrespondingCSV right = new CorrespondingCSV(col1, csvs[1]);

        CSV result = JoinProcessor.join(left, right, JoinType.LEFT);

        assertNotNull(result);

        assertEquals(13, result.getHeaders().size());
        checkHeaders(csvs, result, false);

        assertEquals(11, result.getRecords().size());
    }

    @Test
    public void rightJoinCreatesExpectedData() throws IOException {
        CorrespondingCSV left = new CorrespondingCSV(col0, csvs[0]);
        CorrespondingCSV right = new CorrespondingCSV(col1, csvs[1]);

        CSV result = JoinProcessor.join(left, right, JoinType.RIGHT);

        assertNotNull(result);

        assertEquals(13, result.getHeaders().size());
        checkHeaders(csvs, result, true);

        assertEquals(10, result.getRecords().size());
    }

    @Test
    public void innerJoinCreatesExpectedData() throws IOException {
        CorrespondingCSV left = new CorrespondingCSV(col0, csvs[0]);
        CorrespondingCSV right = new CorrespondingCSV(col1, csvs[1]);

        CSV result = JoinProcessor.join(left, right, JoinType.INNER);

        assertNotNull(result);

        assertEquals(13, result.getHeaders().size());
        checkHeaders(csvs, result, false);

        assertEquals(7, result.getRecords().size());
    }

    @Test
    public void innerJoinCreatesSameExpectedDataWhenReversedDataInput() throws IOException {
        CorrespondingCSV left = new CorrespondingCSV(col1, csvs[1]);
        CorrespondingCSV right = new CorrespondingCSV(col0, csvs[0]);

        CSV result = JoinProcessor.join(left, right, JoinType.INNER);

        assertNotNull(result);

        assertEquals(13, result.getHeaders().size());
        checkHeaders(csvs, result, true);

        assertEquals(7, result.getRecords().size());
    }

    @Test
    public void outerJoinCreatesExpectedData() throws IOException {
        CorrespondingCSV left = new CorrespondingCSV(col0, csvs[0]);
        CorrespondingCSV right = new CorrespondingCSV(col1, csvs[1]);

        CSV result = JoinProcessor.join(left, right, JoinType.OUTER);

        assertNotNull(result);

        assertEquals(13, result.getHeaders().size());
        checkHeaders(csvs, result, false);

        assertEquals(14, result.getRecords().size());
    }

    @Test
    public void outerJoinCreatesSameExpectedDataWhenReversedDataInput() throws IOException {
        CorrespondingCSV left = new CorrespondingCSV(col1, csvs[1]);
        CorrespondingCSV right = new CorrespondingCSV(col0, csvs[0]);

        CSV result = JoinProcessor.join(left, right, JoinType.OUTER);

        assertNotNull(result);

        assertEquals(13, result.getHeaders().size());
        checkHeaders(csvs, result, true);

        assertEquals(14, result.getRecords().size());
    }

    private void checkHeaders(CSV[] orderedData, CSV result, boolean reverse) {
        int first = 0;
        int second = 1;

        if (reverse) {
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