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
        csvData = new TestCSVBuilder().buildCsvInput("AttendanceUsingStudentNumber.csv", "AliasScores.csv");
    }

    @Test
    public void deduceColumnNameShouldReturnFullColumnNameIfQualifiedColumnNameSuppliedAndNoAmbiguityPossible() {
        CorrespondingCSV csv = ColumnNameService.deduceColumnName(
                "Prac 1",
                new ArrayList<>(csvData.values())
        );

        assertNotNull(csv);
        assertEquals("Prac 1", csv.getColumnName());
        assertEquals(csvData.get("AttendanceUsingStudentNumber.csv"), csv.getCsv());
    }

    @Test
    public void deduceColumnNameShouldReturnFullColumnNameIfUnqualifiedColumnNameSuppliedAndNoAmbiguityPossible() {
        CorrespondingCSV csv = ColumnNameService.deduceColumnName(
                "Score",
                new ArrayList<>(csvData.values())
        );

        assertNotNull(csv);
        assertEquals("Scores.Score", csv.getColumnName());
        assertEquals(csvData.get("AliasScores.csv"), csv.getCsv());
    }

    @Test
    public void deduceColumnNameShouldReturnFullColumnNameIfQualifiedColumnNameSuppliedAndAmbiguityPossible() {
        CorrespondingCSV csv = ColumnNameService.deduceColumnName(
                "Scores.StudentNum",
                new ArrayList<>(csvData.values())
        );

        assertNotNull(csv);
        assertEquals("Scores.StudentNum", csv.getColumnName());
        assertEquals(csvData.get("AliasScores.csv"), csv.getCsv());
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

    @Test
    public void validateAliasShouldReturnAliasWhenValidAliasSupplied() {
        String[] aliases = {"Test", "Test_", "test", "test ", "1", "1-"};

        for (String s : aliases) {
            assertEquals(s, ColumnNameService.validateAlias(s));
        }
    }

    @Test
    public void validateAliasShouldThrowExceptionWhenInvalidAliasSupplied() {
        String[] aliases = {"_Test", " Test_", "-test", ";';", "    ", "\n", "valid-until-now;"};

        for (String s : aliases) {
            try {
                ColumnNameService.validateAlias(s);
                fail("Expected an illegal argument exception to be thrown.");
            } catch (IllegalArgumentException e) {
                assertEquals("Alias supplied (may be a filename) of input file must start with a letter and " +
                        "contain only a-z, A-Z, 0-9, space, underscore, hyphen.", e.getMessage());
            }
        }
    }

    @Test
    public void validateColumnNameShouldReturnAliasWhenValidAliasSupplied() {
        String[] aliases = {"Test", "Test_.P", "test.9", "test .k ", "1.p-", "1-.p_d-"};

        for (String s : aliases) {
            assertEquals(s, ColumnNameService.validateColumnName(s));
        }
    }

    @Test
    public void validateColumnNameShouldThrowExceptionWhenInvalidAliasSupplied() {
        String[] aliases = {"_Test", " Test_.", "-test._", "test.p4.e", ";';", "    ", "\n", "valid-until-now;"};

        for (String s : aliases) {
            try {
                ColumnNameService.validateColumnName(s);
                fail("Expected an illegal argument exception to be thrown.");
            } catch (IllegalArgumentException e) {
                assertEquals("Column name supplied '" + s + "' does not match allowed input of alphanumeric characters, spaces, " +
                        "hyphens, underscores, and maximum one '.' in the middle of the name", e.getMessage());
            }
        }
    }
}