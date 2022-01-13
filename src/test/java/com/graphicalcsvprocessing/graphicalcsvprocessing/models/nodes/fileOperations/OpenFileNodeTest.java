package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.fileOperations;

import org.junit.Test;

import static org.junit.Assert.*;

public class OpenFileNodeTest {

    private final OpenFileNode o = new OpenFileNode("testOpenFile", "files", "open_file", "test.csv");

    @Test
    public void testGetName() {
        assertEquals("test.csv", o.getName());
    }

    @Test
    public void testGetId() {
        assertEquals("testOpenFile", o.getId());
    }

    @Test
    public void testGetGroup() {
        assertEquals("files", o.getGroup());
    }

    @Test
    public void testGetOperation() {
        assertEquals("open_file", o.getOperation());
    }

    @Test
    public void testGetAllowedNumberOfEdges() {
        assertEquals(0, o.getAllowedNumberEdges());
    }
}