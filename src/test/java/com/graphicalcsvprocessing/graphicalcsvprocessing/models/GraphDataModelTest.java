package com.graphicalcsvprocessing.graphicalcsvprocessing.models;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.edges.Edge;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.Node;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.fileOperations.OpenFileNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.fileOperations.WriteFileNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.binaryOperations.JoinProcessingNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.JoinProcessor;
import com.graphicalcsvprocessing.graphicalcsvprocessing.utils.TestCSVBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class GraphDataModelTest {

    private final Node[] nodes = {
        new OpenFileNode("open1", "files", "open_file", "Attendance.csv"),
        new OpenFileNode("open2", "files", "open_file", "Scores.csv"),
        new JoinProcessingNode(
                "join",
                "processing",
                "join",
                "Attendance.Attendant",
                "Scores.StudentNum",
                JoinProcessor.JoinType.LEFT
        ),
        new WriteFileNode("write", "files", "write_file", "Renamed.csv")
    };

    private final Edge[] edges = {
        new Edge("open1", "join"),
        new Edge("open2", "join"),
        new Edge("join", "write")
    };

    private Map<String, CSV> csvData;

    private final GraphDataModel gdm = new GraphDataModel(nodes, edges);

    @Before
    public void setUp() throws IOException {
        TestCSVBuilder dataBuilder = new TestCSVBuilder();
        csvData = dataBuilder.buildCsvInput("Attendance.csv", "Scores.csv");
        dataBuilder.prepareCsvData(gdm, csvData);
    }

    @Test
    public void nullNodesArraySuppliedReturnsEmptyMap() throws IOException {
        GraphDataModel gdm = new GraphDataModel(null, edges);

        Map<String, CSV> processResults = gdm.process(csvData);

        assertNotNull(processResults);
        assertTrue(processResults.isEmpty());
    }

    @Test
    public void nullEdgesArraySuppliedReturnsEmptyMap() throws IOException {
        GraphDataModel gdm = new GraphDataModel(nodes, null);

        Map<String, CSV> processResults = gdm.process(csvData);

        assertNotNull(processResults);
        assertTrue(processResults.isEmpty());
    }

    @Test
    public void emptyNodesArrayReturnsEmptyMap() throws IOException {
        GraphDataModel gdm = new GraphDataModel(new Node[0], edges);

        Map<String, CSV> processResults = gdm.process(csvData);

        assertNotNull(processResults);
        assertTrue(processResults.isEmpty());
    }

    @Test
    public void emptyEdgesArrayReturnsEmptyMap() throws IOException {
        GraphDataModel gdm = new GraphDataModel(nodes, new Edge[0]);

        Map<String, CSV> processResults = gdm.process(csvData);

        assertNotNull(processResults);
        assertTrue(processResults.isEmpty());
    }

    @Test
    public void nullCsvDataSetSupplied() throws IOException {
        Map<String, CSV> processResults = gdm.process(null);

        assertNotNull(processResults);
        assertTrue(processResults.isEmpty());
    }

    @Test
    public void emptyCsvDataSetSupplied() throws IOException {
        Map<String, CSV> processResults = gdm.process(new HashMap<>());

        assertNotNull(processResults);
        assertTrue(processResults.isEmpty());
    }

    @Test
    public void openNodeHandlingFailsWhenReferencesMissingInputData() throws IOException {
        Map<String, CSV> smallerCsvSet = new HashMap<>(csvData);
        smallerCsvSet.remove("open1");

        try {
            gdm.process(smallerCsvSet);
        } catch (IllegalArgumentException e) {
            assertEquals("Some open file nodes refer to files which have not been supplied", e.getMessage());
        }
    }

    @Test
    public void writeNodeHandlingFailsWhenIncorrectNumberOfEdgeInputs() throws IOException {
        GraphDataModel gdm = new GraphDataModel(nodes, new Edge[] {
            new Edge("open1", "join"),
            new Edge("open2", "join")
        });

        try {
            gdm.process(csvData);
        } catch (IllegalArgumentException e) {
            assertEquals("Write File Nodes must have '1' input", e.getMessage());
        }
    }

    @Test
    public void processingNodeAtEndOfProcessingLineIncludesResultInProcessReturn() throws IOException {
        GraphDataModel gdm = new GraphDataModel(
                Arrays.stream(nodes).filter(node -> !(node instanceof WriteFileNode)).toArray(Node[]::new), new Edge[] {
                new Edge("open1", "join"),
                new Edge("open2", "join")
        });

        Map<String, CSV> processResults = gdm.process(csvData);

        assertNotNull(processResults);
        assertEquals(1, processResults.size());
        assertNotNull(processResults.get("join"));
    }

    @Test
    public void processingCannotBeginAsNoIndependentNodes() throws IOException {
        GraphDataModel gdm = new GraphDataModel(
            new Node[] {
                new WriteFileNode("write1", "files", "write_file", "Renamed1.csv"),
                new WriteFileNode("write2", "files", "write_file", "Renamed2.csv"),
                new WriteFileNode("write3", "files", "write_file", "Renamed3.csv")
            },
            new Edge[] {
                new Edge("write1", "write2"),
                new Edge("write2", "write3"),
                new Edge("write3", "write1")
            }
        );

        try {
            gdm.process(csvData);
        } catch (IllegalArgumentException e) {
            assertEquals("Graph has codependency within - likely due to loop", e.getMessage());
        }
    }

    //loop => after first iteration doesn't process any nodes => illegal arg exception
    @Test
    public void processingCannotContinueAsNoAdditionalProcessingCarriedOutInIterationRound() throws IOException {
        GraphDataModel gdm = new GraphDataModel(
                new Node[] {
                    new OpenFileNode("open1", "files", "open_file", "Attendance.csv"),
                    new WriteFileNode("write1", "files", "write_file", "Renamed1.csv"),
                    new WriteFileNode("write2", "files", "write_file", "Renamed2.csv"),
                    new WriteFileNode("write3", "files", "write_file", "Renamed3.csv")
                },
                new Edge[] {
                    new Edge("write1", "write2"),
                    new Edge("write2", "write3"),
                    new Edge("write3", "write1")
                }
        );

        try {
            gdm.process(csvData);
        } catch (IllegalArgumentException e) {
            assertEquals("Graph has codependency within - likely due to loop", e.getMessage());
        }
    }

    @Test
    public void processShouldReturnNamedFileWhenGraphSuppliedIsValid() throws IOException {
        Map<String, CSV> processResults = gdm.process(csvData);

        assertNotNull(processResults);
        assertEquals(1, processResults.size());
        assertNotNull(processResults.get("Renamed.csv"));
    }

    @Test
    public void getNodesReturnsNodeArray() {
        assertArrayEquals(nodes, gdm.getNodes());
    }

    @Test
    public void getEdgesReturnsEdgeArray() {
        assertArrayEquals(edges, gdm.getEdges());
    }
}