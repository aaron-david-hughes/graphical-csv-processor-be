package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.binaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CorrespondingCSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.JoinProcessor;
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
public class JoinProcessingNodeTest {

    @Mock
    List<CSV> csvData;

    @Mock
    CSV csv;

    @Mock
    CSV left;

    @Mock
    CSV right;

    JoinProcessingNode j = new JoinProcessingNode(
            "testId",
            "processing",
            "join",
            "testLeft",
            "testRight",
            JoinProcessor.JoinType.LEFT
    );

    @Test
    public void shouldThrowIllegalArgumentExceptionIfDataSuppliedIsLowerThanExpected() {
        when(csvData.size()).thenReturn(1);

        try {
            j.process(csvData);
            fail("Expected IllegalArgumentException");
        } catch (IOException e) {
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "Node 'testId' with incorrect number of inputs, expected '2', but received '1'",
                    e.getMessage()
            );
        }
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionIfDataSuppliedIsHigherThanExpected() {
        when(csvData.size()).thenReturn(3);

        try {
            j.process(csvData);
            fail("Expected IllegalArgumentException");
        } catch (IOException e) {
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "Node 'testId' with incorrect number of inputs, expected '2', but received '3'",
                    e.getMessage()
            );
        }
    }

    @Test
    public void shouldReturnResultOfJoinProcessingIfCorrectNumberOfDataElementsIsCorrect() {
        when(csvData.size()).thenReturn(2);

        try (
            MockedStatic<JoinProcessor> joinProcessorMockedStatic = Mockito.mockStatic(JoinProcessor.class);
            MockedStatic<ColumnNameService> columnNameServiceMockedStatic = Mockito.mockStatic(ColumnNameService.class)
        ) {
            CorrespondingCSV leftCorrespondingCSV = new CorrespondingCSV("", left);
            CorrespondingCSV rightCorrespondingCSV = new CorrespondingCSV("", right);

            columnNameServiceMockedStatic
                    .when(() -> ColumnNameService.deduceColumnName("testLeft", csvData))
                    .thenReturn(leftCorrespondingCSV);

            columnNameServiceMockedStatic
                    .when(() -> ColumnNameService.deduceColumnName("testRight", csvData))
                    .thenReturn(rightCorrespondingCSV);

            joinProcessorMockedStatic
                    .when(() -> JoinProcessor.join(leftCorrespondingCSV, rightCorrespondingCSV, j.joinType))
                    .thenReturn(csv);

            CSV result = j.process(csvData);

            assertEquals(csv, result);
        } catch (IOException e) {
            fail("No exception expected");
        }
    }

    @Test
    public void testNumberOfInboundEdgesAllowedIsBinary() {
        assertEquals(2, j.getAllowedNumberEdges());
    }
}