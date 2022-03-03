package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.utils.TestCSVBuilder;
import org.apache.commons.csv.CSVRecord;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class MathProcessorTest {

    CSV csv;

    @Before
    public void setUp() throws IOException {
        csv = new TestCSVBuilder().buildCsvInput("Attendance.csv").get("Attendance.csv");
    }

    @Test
    public void rowMathShouldAddColumnToTheEndOfTheCsv() throws IOException {
        MathProcessor.StatisticalType[] types = {
                MathProcessor.StatisticalType.MAX,
                MathProcessor.StatisticalType.MIN,
                MathProcessor.StatisticalType.SUM,
                MathProcessor.StatisticalType.AVERAGE,
                MathProcessor.StatisticalType.COUNT,
        };

        for (MathProcessor.StatisticalType type : types) {
            CSV output = MathProcessor.row(csv, type, "testCol", "Prac 1", "Prac 2", "Prac 3", "Prac 8");

            assertEquals(csv.getHeaders().size() + 1, output.getHeaders().size());
            assertEquals("testCol", output.getHeaders().get(csv.getHeaders().size()));
        }
    }

    @Test
    public void columnMathShouldAddColumnToTheEndOfTheCsv() throws IOException {
        MathProcessor.StatisticalType[] types = {
                MathProcessor.StatisticalType.MAX,
                MathProcessor.StatisticalType.MIN,
                MathProcessor.StatisticalType.SUM,
                MathProcessor.StatisticalType.AVERAGE,
                MathProcessor.StatisticalType.COUNT,
        };

        for (MathProcessor.StatisticalType type : types) {
            CSV output = MathProcessor.column(csv, type, "Prac 1");

            assertEquals(1, output.getHeaders().size());
            assertEquals(type.toString(), output.getHeaders().get(0));
        }
    }

}