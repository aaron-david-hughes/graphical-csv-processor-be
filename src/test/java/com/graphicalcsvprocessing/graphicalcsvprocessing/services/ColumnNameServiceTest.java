package com.graphicalcsvprocessing.graphicalcsvprocessing.services;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CorrespondingCSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.utils.TestCSVBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.*;

public class ColumnNameServiceTest {

    Map<String, CSV> csvData;

    @Before
    public void setUp() throws IOException {
        csvData = new TestCSVBuilder().buildCsvInput("AttendanceUsingStudentNumber.csv", "Scores.csv");
    }

    @Test
    public void deduceColumnNameShouldReturnFullColumnNameIfQualifiedColumnNameSuppliedAndNoAmbiguityPossible() {
        CorrespondingCSV csv = ColumnNameService.deduceColumnName(
                "AttendanceUsingStudentNumber.Prac 1",
                new ArrayList<>(csvData.values())
        );

        assertNotNull(csv);
        assertEquals("AttendanceUsingStudentNumber.Prac 1", csv.getColumnName());
        assertEquals(csvData.get("AttendanceUsingStudentNumber.csv"), csv.getCsv());
    }

    @Test
    public void deduceColumnNameShouldReturnFullColumnNameIfUnqualifiedColumnNameSuppliedAndNoAmbiguityPossible() {
        CorrespondingCSV csv = ColumnNameService.deduceColumnName(
                "Prac 1",
                new ArrayList<>(csvData.values())
        );

        assertNotNull(csv);
        assertEquals("AttendanceUsingStudentNumber.Prac 1", csv.getColumnName());
        assertEquals(csvData.get("AttendanceUsingStudentNumber.csv"), csv.getCsv());
    }

    @Test
    public void deduceColumnNameShouldReturnFullColumnNameIfQualifiedColumnNameSuppliedAndAmbiguityPossible() {
        CorrespondingCSV csv = ColumnNameService.deduceColumnName(
                "AttendanceUsingStudentNumber.StudentNum",
                new ArrayList<>(csvData.values())
        );

        assertNotNull(csv);
        assertEquals("AttendanceUsingStudentNumber.StudentNum", csv.getColumnName());
        assertEquals(csvData.get("AttendanceUsingStudentNumber.csv"), csv.getCsv());
    }

    @Test
    public void deduceColumnNameShouldThrowIllegalArgumentExceptionIfUnqualifiedColumnNameSuppliedAndAmbiguityPossible() {
        try {
            ColumnNameService.deduceColumnName(
                    "StudentNum",
                    new ArrayList<>(csvData.values())
            );
            fail("Expected an IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Column name is ambiguous: StudentNum", e.getMessage());
        }
    }

    @Test
    public void deduceColumnNameShouldThrowIllegalArgumentExceptionIfColumnNameSuppliedNotFound() {
        try {
            ColumnNameService.deduceColumnName(
                    "DoesNotExist",
                    new ArrayList<>(csvData.values())
            );
            fail("Expected an IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("No column name found or deducible: DoesNotExist", e.getMessage());
        }
    }
}