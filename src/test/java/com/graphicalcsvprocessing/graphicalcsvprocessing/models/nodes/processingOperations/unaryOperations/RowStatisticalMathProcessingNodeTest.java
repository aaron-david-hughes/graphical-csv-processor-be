package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CorrespondingCSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.StatisticalMathProcessor;
import com.graphicalcsvprocessing.graphicalcsvprocessing.services.ColumnNameService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RowStatisticalMathProcessingNodeTest {

    @Mock
    List<CSV> csvData;

    @Mock
    CSV csv;

    @Mock
    CSV input;

    RowStatisticalMathProcessingNode node = new RowStatisticalMathProcessingNode(
            "testId",
            "processing",
            "row_math",
            new String[] {
                    "col1",
                    "col2"
            },
            "testCol",
            StatisticalMathProcessor.StatisticalType.COUNT
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
    public void shouldThrowIllegalArgumentExceptionIfNoColumnsSpecified() throws IOException {
        when(csvData.size()).thenReturn(1);

        RowStatisticalMathProcessingNode illegalNode = new RowStatisticalMathProcessingNode(
                "testId",
                "processing",
                "row_math",
                new String[] {},
                "testCol",
                StatisticalMathProcessor.StatisticalType.COUNT
        );

        try {
            illegalNode.process(csvData);
            fail("Expected an illegal argument exception to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Columns list must not be empty", e.getMessage());
        }
    }

    @Test
    public void shouldReturnResultOfRowMathProcessingIfCorrectNumberOfDataElementsIsCorrect() {
        when(csvData.size()).thenReturn(1);
        when(csvData.get(0)).thenReturn(input);

        try (
                MockedStatic<StatisticalMathProcessor> mathProcessorMockedStatic = Mockito.mockStatic(StatisticalMathProcessor.class);
                MockedStatic<ColumnNameService> columnNameServiceMockedStatic = Mockito.mockStatic(ColumnNameService.class)
        ) {
            columnNameServiceMockedStatic
                    .when(() -> ColumnNameService.deduceColumnName("col1", csvData))
                    .thenReturn(new CorrespondingCSV("test.col1", input));

            columnNameServiceMockedStatic
                    .when(() -> ColumnNameService.deduceColumnName("col2", csvData))
                    .thenReturn(new CorrespondingCSV("test.col2", input));

            columnNameServiceMockedStatic
                    .when(() -> ColumnNameService.validateColumnName("testCol"))
                    .thenReturn("testCol");

            mathProcessorMockedStatic
                    .when(() -> StatisticalMathProcessor.row(eq(input), eq(StatisticalMathProcessor.StatisticalType.COUNT), eq("testCol"), any()))
                    .thenReturn(csv);

            CSV result = node.process(csvData);

            assertEquals(csv, result);
        } catch (IOException e) {
            fail("No exception expected");
        }
    }
}