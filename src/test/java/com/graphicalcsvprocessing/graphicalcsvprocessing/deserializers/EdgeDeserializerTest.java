package com.graphicalcsvprocessing.graphicalcsvprocessing.deserializers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.edges.Edge;
import org.junit.Test;

import static org.junit.Assert.*;

public class EdgeDeserializerTest {

    private static final String VALID_EDGE = "{ \"from\": \"2\", \"to\": \"3\" }";
    private static final String INVALID_EDGE_TO = "{ \"from\": \"2\" }";
    private static final String INVALID_EDGE_FROM = "{ \"to\": \"2\" }";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void shouldDeserializeEdgeWhenValid() throws JsonProcessingException {
        Edge e = objectMapper.readValue(VALID_EDGE, Edge.class);

        assertNotNull(e);
        assertEquals("2", e.getFrom());
        assertEquals("3", e.getTo());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenMissingToOrFrom() throws JsonProcessingException {
        for (String s : new String[] {INVALID_EDGE_TO, INVALID_EDGE_FROM}) {
            try {
                objectMapper.readValue(s, Edge.class);
                fail("Expected and illegal argument exception to be thrown");
            } catch (IllegalArgumentException e) {
                assertEquals("Edge must contain both 'to' and 'from' values", e.getMessage());
            }
        }
    }
}