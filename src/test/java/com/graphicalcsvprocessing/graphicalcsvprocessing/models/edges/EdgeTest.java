package com.graphicalcsvprocessing.graphicalcsvprocessing.models.edges;

import org.junit.Test;

import static org.junit.Assert.*;

public class EdgeTest {

    Edge e = new Edge("fromId", "toId");

    @Test
    public void testGetFrom() {
        assertEquals("fromId", e.getFrom());
    }

    @Test
    public void testGetTo() {
        assertEquals("toId", e.getTo());
    }
}