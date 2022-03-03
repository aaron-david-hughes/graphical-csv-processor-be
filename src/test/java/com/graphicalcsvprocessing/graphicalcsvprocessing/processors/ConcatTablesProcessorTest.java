package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.utils.TestCSVBuilder;
import org.apache.commons.csv.CSVRecord;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.utils.ProcessingUtils.listToString;
import static org.junit.Assert.*;

public class ConcatTablesProcessorTest {

    Map<String, CSV> csvs;

    @Before
    public void setUp() throws IOException {
        csvs = new TestCSVBuilder().buildCsvInput(
                "Scores.csv",
                "Attendance.csv",
                "AttendanceUsingStudentNumber.csv"
        );
    }

    @Test
    public void shouldConcatTwoUnrelatedCsvsByHavingAsManyHeadersAsTheTwoInputsSum() throws IOException {
        CSV[] inputs = {csvs.get("Attendance.csv"), csvs.get("Scores.csv")};
        CSV output = ConcatTablesProcessor.concat(inputs[0], inputs[1]);

        assertEquals(
                inputs[0].getHeaders().size() + inputs[1].getHeaders().size(),
                output.getHeaders().size()
        );
        assertEquals(
                inputs[0].getRecords().size() + inputs[1].getRecords().size(),
                output.getRecords().size()
        );

        List<CSVRecord> outputRecords = output.getRecords();
        List<CSVRecord> input1Records = inputs[0].getRecords();
        for (int i = 0; i < input1Records.size(); i++) {
            CSVRecord inputRecord = input1Records.get(i);
            CSVRecord correspondingOutputRecord = outputRecords.get(i);

            assertEquals(
                    listToString(inputRecord.toList()) + ",".repeat(inputs[1].getHeaders().size()),
                    listToString(correspondingOutputRecord.toList())
            );
        }

        List<CSVRecord> input2Records = inputs[1].getRecords();
        for (int i = 0; i < input2Records.size(); i++) {
            CSVRecord inputRecord = input2Records.get(i);
            CSVRecord correspondingOutputRecord = outputRecords.get(i + input1Records.size());

            assertEquals(
                    ",".repeat(inputs[0].getHeaders().size()) + listToString(inputRecord.toList()),
                    listToString(correspondingOutputRecord.toList())
            );
        }
    }

    @Test
    public void shouldConcatTwoCsvsWithSimilarColumnHeadersToEnsureNoDuplicateHeaders() throws IOException {
        CSV[] inputs = {csvs.get("AttendanceUsingStudentNumber.csv"), csvs.get("Scores.csv")};
        CSV output = ConcatTablesProcessor.concat(inputs[0], inputs[1]);

        int numberOfExpectedDuplicateHeaders = 1;

        assertEquals(
                inputs[0].getHeaders().size() + inputs[1].getHeaders().size() - numberOfExpectedDuplicateHeaders,
                output.getHeaders().size()
        );
        assertEquals(
                inputs[0].getRecords().size() + inputs[1].getRecords().size(),
                output.getRecords().size()
        );

        List<CSVRecord> outputRecords = output.getRecords();
        List<CSVRecord> input1Records = inputs[0].getRecords();
        for (int i = 0; i < input1Records.size(); i++) {
            CSVRecord inputRecord = input1Records.get(i);
            CSVRecord correspondingOutputRecord = outputRecords.get(i);

            assertEquals(
                    listToString(inputRecord.toList()) + ",".repeat(inputs[1].getHeaders().size() - numberOfExpectedDuplicateHeaders),
                    listToString(correspondingOutputRecord.toList())
            );
        }

        List<CSVRecord> input2Records = inputs[1].getRecords();
        for (int i = 0; i < input2Records.size(); i++) {
            CSVRecord inputRecord = input2Records.get(i);
            CSVRecord correspondingOutputRecord = outputRecords.get(i + input1Records.size());

            for (String s : inputs[1].getHeaders()) {
                assertEquals(inputRecord.get(s), correspondingOutputRecord.get(s));
            }
        }
    }

}