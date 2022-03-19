package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CorrespondingCSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.BasicMathProcessor;
import com.graphicalcsvprocessing.graphicalcsvprocessing.services.ColumnNameService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.processors.BasicMathProcessor.MathOperation.ADD;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RowBasicMathProcessingNodeTest {

    @Mock
    List<CSV> csvData;

    @Mock
    CSV csv;

    @Mock
    CSV input;

    RowBasicMathProcessingNode node = new RowBasicMathProcessingNode(
            "testId",
            "processing",
            "row_basic_math",
            "col1",
            "col2",
            "newCol",
            ADD,
            true
    );

    @Test
    public void shouldThrowIllegalArgumentExceptionIfDataSuppliedIsLowerThanExpected() {
        when(csvData.size()).thenReturn(0);

        try {
            node.process(csvData);
            fail("Expected IllegalArgumentException");
        } catch (IOException e) {
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "Node 'testId' with incorrect number of inputs, expected '1', but received '0'",
                    e.getMessage()
            );
        }
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionIfDataSuppliedIsHigherThanExpected() {
        when(csvData.size()).thenReturn(2);

        try {
            node.process(csvData);
            fail("Expected IllegalArgumentException");
        } catch (IOException e) {
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "Node 'testId' with incorrect number of inputs, expected '1', but received '2'",
                    e.getMessage()
            );
        }
    }

    @Test
    public void shouldProcessLiteralWhenLiteralIsTrue() {
        when(csvData.size()).thenReturn(1);

        try (
                MockedStatic<BasicMathProcessor> basicMathProcessorMockedStatic = Mockito.mockStatic(BasicMathProcessor.class);
                MockedStatic<ColumnNameService> columnNameServiceMockedStatic = Mockito.mockStatic(ColumnNameService.class)
        ) {
            columnNameServiceMockedStatic
                    .when(() -> ColumnNameService.deduceColumnName("col1", csvData))
                    .thenReturn(new CorrespondingCSV("test.col1", input));

            columnNameServiceMockedStatic
                    .when(() -> ColumnNameService.validateColumnName("newCol"))
                    .thenReturn("newCol");

            basicMathProcessorMockedStatic
                    .when(() -> BasicMathProcessor.processLiteral(eq(input), eq(ADD), eq("test.col1"), eq("col2"), eq("newCol")))
                    .thenReturn(csv);

            CSV result = node.process(csvData);

            assertEquals(csv, result);
        } catch (IOException e) {
            fail("No exception expected");
        }
    }

    @Test
    public void shouldProcessNoLiteralWhenLiteralFalse() {
        when(csvData.size()).thenReturn(1);

        try (
                MockedStatic<BasicMathProcessor> basicMathProcessorMockedStatic = Mockito.mockStatic(BasicMathProcessor.class);
                MockedStatic<ColumnNameService> columnNameServiceMockedStatic = Mockito.mockStatic(ColumnNameService.class)
        ) {
            node.literal = false;

            columnNameServiceMockedStatic
                    .when(() -> ColumnNameService.deduceColumnName("col1", csvData))
                    .thenReturn(new CorrespondingCSV("test.col1", input));

            columnNameServiceMockedStatic
                    .when(() -> ColumnNameService.deduceColumnName("col2", csvData))
                    .thenReturn(new CorrespondingCSV("test.col2", input));

            columnNameServiceMockedStatic
                    .when(() -> ColumnNameService.validateColumnName("newCol"))
                    .thenReturn("newCol");

            basicMathProcessorMockedStatic
                    .when(() -> BasicMathProcessor.process(eq(input), eq(ADD), eq("test.col1"), eq("test.col2"), eq("newCol")))
                    .thenReturn(csv);

            CSV result = node.process(csvData);

            assertEquals(csv, result);
        } catch (IOException e) {
            fail("No exception expected");
        }
    }
}