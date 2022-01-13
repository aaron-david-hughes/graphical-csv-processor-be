package com.graphicalcsvprocessing.graphicalcsvprocessing.models;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class CorrespondingCSVTest {

    @Mock
    private CSV csv;

    private CorrespondingCSV correspondingCSV;

    @Before
    public void setUp() {
        correspondingCSV = new CorrespondingCSV("col", csv);
    }

    @Test
    public void testGetCsv() {
        assertEquals(csv, correspondingCSV.getCsv());
    }

    @Test
    public void testGetColumnName() {
        assertEquals("col", correspondingCSV.getColumnName());
    }
}