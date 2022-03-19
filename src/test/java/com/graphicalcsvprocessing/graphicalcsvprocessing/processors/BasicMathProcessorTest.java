package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.utils.TestCSVBuilder;
import org.apache.commons.csv.CSVRecord;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.processors.BasicMathProcessor.MathOperation.*;
import static org.junit.Assert.*;

public class BasicMathProcessorTest {

    CSV csv;

    @Before
    public void setUp() throws IOException {
        csv = new TestCSVBuilder().buildCsvInput("Attendance.csv").get("Attendance.csv");
    }

    @Test
    public void shouldAddLiteralInNewColumn() throws IOException {
        CSV output = BasicMathProcessor.processLiteral(csv, ADD, "Prac 1", "2", "newCol");

        for (int i = 0; i < output.getRecords().size(); i ++) {
            assertEquals(
                    Double.parseDouble(csv.getRecords().get(i).get("Prac 1")) + 2,
                    Double.parseDouble(output.getRecords().get(i).get("newCol")),
                    0.0
            );
        }
    }

    @Test
    public void shouldSubtractLiteralInNewColumn() throws IOException {
        CSV output = BasicMathProcessor.processLiteral(csv, SUBTRACT, "Prac 1", "2", "newCol");

        for (int i = 0; i < output.getRecords().size(); i ++) {
            assertEquals(
                    Double.parseDouble(csv.getRecords().get(i).get("Prac 1")) - 2,
                    Double.parseDouble(output.getRecords().get(i).get("newCol")),
                    0.0
            );
        }
    }

    @Test
    public void shouldMultiplyLiteralInNewColumn() throws IOException {
        CSV output = BasicMathProcessor.processLiteral(csv, MULTIPLY, "Prac 1", "2", "newCol");

        for (int i = 0; i < output.getRecords().size(); i ++) {
            assertEquals(
                    Double.parseDouble(csv.getRecords().get(i).get("Prac 1")) * 2,
                    Double.parseDouble(output.getRecords().get(i).get("newCol")),
                    0.0
            );
        }
    }

    @Test
    public void shouldDivideLiteralInNewColumn() throws IOException {
        CSV output = BasicMathProcessor.processLiteral(csv, DIVIDE, "Prac 1", "2", "newCol");

        for (int i = 0; i < output.getRecords().size(); i ++) {
            assertEquals(
                    Double.parseDouble(csv.getRecords().get(i).get("Prac 1")) / 2,
                    Double.parseDouble(output.getRecords().get(i).get("newCol")),
                    0.0
            );
        }
    }

    @Test
    public void shouldModuloLiteralInNewColumn() throws IOException {
        CSV output = BasicMathProcessor.processLiteral(csv, MODULO, "Prac 1", "2", "newCol");

        for (int i = 0; i < output.getRecords().size(); i ++) {
            assertEquals(
                    Double.parseDouble(csv.getRecords().get(i).get("Prac 1")) % 2,
                    Double.parseDouble(output.getRecords().get(i).get("newCol")),
                    0.0
            );
        }
    }

    @Test
    public void shouldAddInNewColumn() throws IOException {
        CSV output = BasicMathProcessor.process(csv, ADD, "Prac 1", "Prac 2", "newCol");

        for (int i = 0; i < output.getRecords().size(); i ++) {
            assertEquals(
                    Double.parseDouble(csv.getRecords().get(i).get("Prac 1")) + Double.parseDouble(csv.getRecords().get(i).get("Prac 2")),
                    Double.parseDouble(output.getRecords().get(i).get("newCol")),
                    0.0
            );
        }
    }

    @Test
    public void shouldSubtractInNewColumn() throws IOException {
        CSV output = BasicMathProcessor.process(csv, SUBTRACT, "Prac 1", "Prac 2", "newCol");

        for (int i = 0; i < output.getRecords().size(); i ++) {
            assertEquals(
                    Double.parseDouble(csv.getRecords().get(i).get("Prac 1")) - Double.parseDouble(csv.getRecords().get(i).get("Prac 2")),
                    Double.parseDouble(output.getRecords().get(i).get("newCol")),
                    0.0
            );
        }
    }

    @Test
    public void shouldMultiplyInNewColumn() throws IOException {
        CSV output = BasicMathProcessor.process(csv, MULTIPLY, "Prac 1", "Prac 2", "newCol");

        for (int i = 0; i < output.getRecords().size(); i ++) {
            assertEquals(
                    Double.parseDouble(csv.getRecords().get(i).get("Prac 1")) * Double.parseDouble(csv.getRecords().get(i).get("Prac 2")),
                    Double.parseDouble(output.getRecords().get(i).get("newCol")),
                    0.0
            );
        }
    }

    @Test
    public void shouldDivideInNewColumn() throws IOException {
        CSV output = BasicMathProcessor.process(csv, DIVIDE, "Prac 1", "Prac 2", "newCol");

        for (int i = 0; i < output.getRecords().size(); i ++) {
            assertEquals(
                    Double.parseDouble(csv.getRecords().get(i).get("Prac 1")) / Double.parseDouble(csv.getRecords().get(i).get("Prac 2")),
                    Double.parseDouble(output.getRecords().get(i).get("newCol")),
                    0.0
            );
        }
    }

    @Test
    public void shouldModuloInNewColumn() throws IOException {
        CSV output = BasicMathProcessor.process(csv, MODULO, "Prac 1", "Prac 2", "newCol");

        for (int i = 0; i < output.getRecords().size(); i ++) {
            assertEquals(
                    Double.parseDouble(csv.getRecords().get(i).get("Prac 1")) % Double.parseDouble(csv.getRecords().get(i).get("Prac 2")),
                    Double.parseDouble(output.getRecords().get(i).get("newCol")),
                    0.0
            );
        }
    }
}