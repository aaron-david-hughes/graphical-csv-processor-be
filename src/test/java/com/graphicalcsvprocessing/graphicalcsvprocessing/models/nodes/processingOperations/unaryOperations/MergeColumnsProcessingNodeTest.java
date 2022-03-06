package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CorrespondingCSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.MergeColumnsProcessor;
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
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MergeColumnsProcessingNodeTest {

    @Mock
    List<CSV> csvData;

    @Mock
    CSV csv;

    @Mock
    CSV input;

    MergeColumnsProcessingNode node = new MergeColumnsProcessingNode(
            "testId",
            "processing",
            "merge_columns",
            "input1",
            "input2",
            "test",
            MergeColumnsProcessor.MergeType.NUMERIC_EQUALITY
    );

    @Test
    public void shouldGetColumn1() {
        assertEquals("input1", node.getColumn1());
    }

    @Test
    public void shouldGetColumn2() {
        assertEquals("input2", node.getColumn2());
    }

    @Test
    public void shouldGetMergeColName() {
        assertEquals("test", node.getMergeColName());
    }

    @Test
    public void shouldGetMergeType() {
        assertEquals(MergeColumnsProcessor.MergeType.NUMERIC_EQUALITY, node.getMergeType());
    }

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
    public void shouldReturnResultOfMergeColumnsProcessingIfCorrectNumberOfDataElementsIsCorrect() {
        when(csvData.size()).thenReturn(1);
        when(csvData.get(0)).thenReturn(input);

        try (
                MockedStatic<MergeColumnsProcessor> mergeColumnsProcessorMockedStatic = Mockito.mockStatic(MergeColumnsProcessor.class);
                MockedStatic<ColumnNameService> columnNameServiceMockedStatic = Mockito.mockStatic(ColumnNameService.class)
        ) {
            columnNameServiceMockedStatic
                    .when(() -> ColumnNameService.deduceColumnName("input1", csvData))
                    .thenReturn(new CorrespondingCSV("test.input1", input));

            columnNameServiceMockedStatic
                    .when(() -> ColumnNameService.deduceColumnName("input2", csvData))
                    .thenReturn(new CorrespondingCSV("test.input2", input));

            columnNameServiceMockedStatic
                    .when(() -> ColumnNameService.validateColumnName("test"))
                    .thenReturn("test");

            mergeColumnsProcessorMockedStatic
                    .when(() -> MergeColumnsProcessor.merge(input, node))
                    .thenReturn(csv);

            CSV result = node.process(csvData);

            assertEquals(csv, result);
        } catch (IOException e) {
            fail("No exception expected");
        }
    }
}