package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.fileOperations;

import org.junit.Test;

import static org.junit.Assert.*;

public class WriteFileNodeTest {

    private final WriteFileNode w = new WriteFileNode("testWriteFile", "files", "write_file", "test.csv");

    @Test
    public void testGetName() {
        assertEquals("test.csv", w.getName());
    }

    @Test
    public void testGetId() {
        assertEquals("testWriteFile", w.getId());
    }

    @Test
    public void testGetGroup() {
        assertEquals("files", w.getGroup());
    }

    @Test
    public void testGetOperation() {
        assertEquals("write_file", w.getOperation());
    }

    @Test
    public void testGetAllowedNumberOfEdges() {
        assertEquals(1, w.getAllowedNumberEdges());
    }
}