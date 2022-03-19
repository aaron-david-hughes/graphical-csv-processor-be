package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.utils.TestCSVBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class StatisticalMathProcessorTest {

    CSV csv;

    @Before
    public void setUp() throws IOException {
        csv = new TestCSVBuilder().buildCsvInput("Attendance.csv").get("Attendance.csv");
    }

    @Test
    public void rowMathShouldAddColumnToTheEndOfTheCsv() throws IOException {
        StatisticalMathProcessor.StatisticalType[] types = {
                StatisticalMathProcessor.StatisticalType.MAX,
                StatisticalMathProcessor.StatisticalType.MIN,
                StatisticalMathProcessor.StatisticalType.SUM,
                StatisticalMathProcessor.StatisticalType.AVERAGE,
                StatisticalMathProcessor.StatisticalType.COUNT,
        };

        for (StatisticalMathProcessor.StatisticalType type : types) {
            CSV output = StatisticalMathProcessor.row(csv, type, "testCol", "Prac 1", "Prac 2", "Prac 3", "Prac 8");

            assertEquals(csv.getHeaders().size() + 1, output.getHeaders().size());
            assertEquals("testCol", output.getHeaders().get(csv.getHeaders().size()));
        }
    }

    @Test
    public void columnMathShouldAddColumnToNewCSV() throws IOException {
        StatisticalMathProcessor.StatisticalType[] types = {
                StatisticalMathProcessor.StatisticalType.MAX,
                StatisticalMathProcessor.StatisticalType.MIN,
                StatisticalMathProcessor.StatisticalType.SUM,
                StatisticalMathProcessor.StatisticalType.AVERAGE,
                StatisticalMathProcessor.StatisticalType.COUNT,
        };

        for (StatisticalMathProcessor.StatisticalType type : types) {
            CSV output = StatisticalMathProcessor.column(csv, type, "Prac 1");

            assertEquals(1, output.getHeaders().size());
            assertEquals(type.toString(), output.getHeaders().get(0));
        }
    }

}