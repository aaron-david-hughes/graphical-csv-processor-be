package com.graphicalcsvprocessing.graphicalcsvprocessing.deserializers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.Node;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.fileOperations.OpenFileNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.fileOperations.WriteFileNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.binaryOperations.ConcatTablesProcessingNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.binaryOperations.JoinProcessingNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.binaryOperations.OrProcessingNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.binaryOperations.SetComplementProcessingNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations.*;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class NodeDeserializerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void deserializeThrowsIllegalArgumentExceptionWhenGeneralNodeAttributesMissing() throws JsonProcessingException {
        try {
            objectMapper.readValue("{\n" +
                    "            \"operation\": \"open_file\",\n" +
                    "            \"group\": \"test\",\n" +
                    "            \"name\": \"test-file.csv\"\n" +
                    "        }", Node.class);
            fail("Expected an illegal argument exception to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Node missing necessary attribute - refer to README.md", e.getMessage());
        }
    }

    @Test
    public void deserializeThrowsIllegalArgumentExceptionWhenSpecificNodeAttributesMissing() throws JsonProcessingException {
        try {
            objectMapper.readValue("{\n" +
                    "            \"operation\": \"open_file\",\n" +
                    "            \"group\": \"test\",\n" +
                    "            \"id\": \"1\"\n" +
                    "        }", Node.class);
            fail("Expected an illegal argument exception to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Node missing necessary attribute - refer to README.md", e.getMessage());
        }
    }

    @Test
    public void deserializesThrowsIllegalArgumentExceptionWhenNodeOperationRequestedIsNotRegistered() throws JsonProcessingException {
        try {
            objectMapper.readValue(generateJson("test_not_existent_op", new HashMap<>()), Node.class);
            fail("Expected an illegal argument exception to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("No nodes matching operation: test_not_existent_op", e.getMessage());
        }
    }

    @Test
    public void deserializesOpenFileNode() throws JsonProcessingException {
        Map<String, String> m = new HashMap<>();
        m.put("name", "test-file.csv");

        Node n = objectMapper.readValue(generateJson("open_file", m), Node.class);

        assertNotNull(n);
        assertEquals(OpenFileNode.class, n.getClass());
    }

    @Test
    public void deserializesWriteFileNode() throws JsonProcessingException {
        Map<String, String> m = new HashMap<>();
        m.put("name", "test-file.csv");

        Node n = objectMapper.readValue(generateJson("write_file", m), Node.class);

        assertNotNull(n);
        assertEquals(WriteFileNode.class, n.getClass());
    }

    @Test
    public void deserializesJoinNode() throws JsonProcessingException {
        Map<String, String> m = new HashMap<>();
        m.put("onLeft", "left");
        m.put("onRight", "right");
        m.put("joinType", "inner");

        Node n = objectMapper.readValue(generateJson("join", m), Node.class);

        assertNotNull(n);
        assertEquals(JoinProcessingNode.class, n.getClass());
    }

    @Test
    public void deserializesFilterNode() throws JsonProcessingException {
        Map<String, String> m = new HashMap<>();
        m.put("column", "col");
        m.put("condition", "2");
        m.put("equal", "true");
        m.put("filterType", "string_equality");

        Node n = objectMapper.readValue(generateJson("filter", m), Node.class);

        assertNotNull(n);
        assertEquals(FilterProcessingNode.class, n.getClass());
    }

    @Test
    public void deserializesTakeColumnNode() throws JsonProcessingException {
        Map<String, String> m = new HashMap<>();
        m.put("columns", "col1,col2,col3");

        Node n = objectMapper.readValue(generateJson("take_columns", m), Node.class);

        assertNotNull(n);
        assertEquals(TakeColumnProcessingNode.class, n.getClass());
    }

    @Test
    public void deserializesDropColumnNode() throws JsonProcessingException {
        Map<String, String> m = new HashMap<>();
        m.put("columns", "col1,col2,col3");

        Node n = objectMapper.readValue(generateJson("drop_columns", m), Node.class);

        assertNotNull(n);
        assertEquals(DropColumnProcessingNode.class, n.getClass());
    }

    @Test
    public void deserializesAliasNode() throws JsonProcessingException {
        Map<String, String> m = new HashMap<>();
        m.put("alias", "test");

        Node n = objectMapper.readValue(generateJson("alias", m), Node.class);

        assertNotNull(n);
        assertEquals(AliasProcessingNode.class, n.getClass());
    }

    @Test
    public void deserializesRenameColumnNode() throws JsonProcessingException {
        Map<String, String> m = new HashMap<>();
        m.put("column", "col1");
        m.put("newName", "col2");

        Node n = objectMapper.readValue(generateJson("rename_column", m), Node.class);

        assertNotNull(n);
        assertEquals(RenameColumnProcessingNode.class, n.getClass());
    }

    @Test
    public void deserializesMergeColumnsNode() throws JsonProcessingException {
        Map<String, String> m = new HashMap<>();
        m.put("column1", "col1");
        m.put("column2", "col2");
        m.put("mergeColName", "col3");
        m.put("mergeType", "string_equality");

        Node n = objectMapper.readValue(generateJson("merge_columns", m), Node.class);

        assertNotNull(n);
        assertEquals(MergeColumnsProcessingNode.class, n.getClass());
    }

    @Test
    public void deserializesMergeRowsNode() throws JsonProcessingException {
        Map<String, String> m = new HashMap<>();
        m.put("column", "col");
        m.put("value", "test");

        Node n = objectMapper.readValue(generateJson("merge_rows", m), Node.class);

        assertNotNull(n);
        assertEquals(MergeRowsProcessingNode.class, n.getClass());
    }

    @Test
    public void deserializesLimitNode() throws JsonProcessingException {
        Map<String, String> m = new HashMap<>();
        m.put("limit", "30");

        Node n = objectMapper.readValue(generateJson("limit", m), Node.class);

        assertNotNull(n);
        assertEquals(LimitProcessingNode.class, n.getClass());
    }

    @Test
    public void deserializesConcatTablesNode() throws JsonProcessingException {
        Map<String, String> m = new HashMap<>();

        Node n = objectMapper.readValue(generateJson("concat_tables", m), Node.class);

        assertNotNull(n);
        assertEquals(ConcatTablesProcessingNode.class, n.getClass());
    }

    @Test
    public void deserializesUniqueColumnNode() throws JsonProcessingException {
        Map<String, String> m = new HashMap<>();
        m.put("column", "col");

        Node n = objectMapper.readValue(generateJson("unique_column", m), Node.class);

        assertNotNull(n);
        assertEquals(UniqueColumnProcessingNode.class, n.getClass());
    }

    @Test
    public void deserializesOrNode() throws JsonProcessingException {
        Map<String, String> m = new HashMap<>();
        m.put("column", "col");

        Node n = objectMapper.readValue(generateJson("or", m), Node.class);

        assertNotNull(n);
        assertEquals(OrProcessingNode.class, n.getClass());
    }

    @Test
    public void deserializesOrderNode() throws JsonProcessingException {
        Map<String, String> m = new HashMap<>();
        m.put("column", "col");
        m.put("orderType", "alphabetical_order_asc");

        Node n = objectMapper.readValue(generateJson("order_column", m), Node.class);

        assertNotNull(n);
        assertEquals(OrderColumnProcessingNode.class, n.getClass());
    }

    @Test
    public void deserializesDropAliasNode() throws JsonProcessingException {
        Map<String, String> m = new HashMap<>();

        Node n = objectMapper.readValue(generateJson("drop_alias", m), Node.class);

        assertNotNull(n);
        assertEquals(DropAliasProcessingNode.class, n.getClass());
    }

    @Test
    public void deserializesSetComplementNode() throws JsonProcessingException {
        Map<String, String> m = new HashMap<>();
        m.put("keyHeader", "col");

        Node n = objectMapper.readValue(generateJson("set_complement", m), Node.class);

        assertNotNull(n);
        assertEquals(SetComplementProcessingNode.class, n.getClass());
    }

    @Test
    public void deserializesConcatColumnNode() throws JsonProcessingException {
        Map<String, String> m = new HashMap<>();
        m.put("column1", "col1");
        m.put("column2", "col2");
        m.put("concatHeader", "col3");

        Node n = objectMapper.readValue(generateJson("concat_columns", m), Node.class);

        assertNotNull(n);
        assertEquals(ConcatColumnsProcessingNode.class, n.getClass());
    }

    @Test
    public void deserializesRowMathNode() throws JsonProcessingException {
        Map<String, String> m = new HashMap<>();
        m.put("columns", "col1,col2,col3");
        m.put("newName", "col4");
        m.put("mathOperation", "min");

        Node n = objectMapper.readValue(generateJson("row_math", m), Node.class);

        assertNotNull(n);
        assertEquals(RowMathProcessingNode.class, n.getClass());
    }

    @Test
    public void deserializesColumnMathNode() throws JsonProcessingException {
        Map<String, String> m = new HashMap<>();
        m.put("column", "col1");
        m.put("mathOperation", "min");

        Node n = objectMapper.readValue(generateJson("column_math", m), Node.class);

        assertNotNull(n);
        assertEquals(ColumnMathProcessingNode.class, n.getClass());
    }

    private String generateJson(String op, Map<String, String> specifics) {
        List<String> specificAttributes = specifics.entrySet().stream()
                .map(entry -> "\"" + entry.getKey() + "\": \"" + entry.getValue() + "\"")
                .collect(Collectors.toList());

        String specificsStr = String.join(",", specificAttributes);

        return "{" +
                "\"id\": \"1\"," +
                "\"group\": \"test\"," +
                "\"operation\": \"" + op + "\"" +
                (
                    specificAttributes.isEmpty()
                        ? ""
                        : "," + specificsStr
                ) +
                "}";
    }
}