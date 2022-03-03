package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.binaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CorrespondingCSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.ConcatTablesProcessor;
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
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConcatTablesProcessingNodeTest {

    @Mock
    List<CSV> csvData;

    @Mock
    CSV csv;

    @Mock
    CSV input1;

    @Mock
    CSV input2;

    ConcatTablesProcessingNode node = new ConcatTablesProcessingNode(
            "testId", "testProcessing", "concat_tables"
    );

    @Test
    public void shouldThrowIllegalArgumentExceptionIfDataSuppliedIsLowerThanExpected() {
        when(csvData.size()).thenReturn(1);

        try {
            node.process(csvData);
            fail("Expected IllegalArgumentException");
        } catch (IOException e) {
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "Join node with incorrect number of inputs, expected '2', but received '1'",
                    e.getMessage()
            );
        }
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionIfDataSuppliedIsHigherThanExpected() {
        when(csvData.size()).thenReturn(3);

        try {
            node.process(csvData);
            fail("Expected IllegalArgumentException");
        } catch (IOException e) {
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "Join node with incorrect number of inputs, expected '2', but received '3'",
                    e.getMessage()
            );
        }
    }

    @Test
    public void shouldReturnResultOfJoinProcessingIfCorrectNumberOfDataElementsIsCorrect() {
        when(csvData.size()).thenReturn(2);
        when(csvData.get(0)).thenReturn(input1);
        when(csvData.get(1)).thenReturn(input2);

        try (
                MockedStatic<ConcatTablesProcessor> concatTablesProcessorMockedStatic = Mockito.mockStatic(ConcatTablesProcessor.class)
        ) {
            concatTablesProcessorMockedStatic
                    .when(() -> ConcatTablesProcessor.concat(input1, input2))
                    .thenReturn(csv);

            CSV result = node.process(csvData);

            assertEquals(csv, result);
        } catch (IOException e) {
            fail("No exception expected");
        }
    }

}