package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.utils.TestCSVBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class AliasProcessorTest {

    CSV scores;

    CSV aliasScores;

    CSV someAliasScores;

    CSV aliasProblem;

    @Before
    public void setUp() throws IOException {
        Map<String, CSV> csvs = new TestCSVBuilder().buildCsvInput(
                "Scores.csv", "AliasScores.csv", "SomeAliasScores.csv", "AliasProblem.csv"
        );

        scores = csvs.get("Scores.csv");
        aliasScores = csvs.get("AliasScores.csv");
        someAliasScores = csvs.get("SomeAliasScores.csv");
        aliasProblem = csvs.get("AliasProblem.csv");
    }

    @Test
    public void shouldAddAliasToCsvWhichDoesNotHaveAnExistingAliasPrior() throws IOException {
        CSV output = AliasProcessor.alias(scores, "test");

        assertNotNull(output);
        assertEquals(scores.getRecords().size(), output.getRecords().size());

        List<String> outputHeaders = output.getHeaders();
        List<String> scoresHeaders = scores.getHeaders();

        assertEquals(scoresHeaders.size(), outputHeaders.size());
        for (int i = 0; i < outputHeaders.size(); i++) {
            assertEquals("test." + scoresHeaders.get(i), outputHeaders.get(i));
        }
    }

    @Test
    public void shouldReplaceAliasToCsvWhichHasAnExistingAliasPrior() throws IOException {
        CSV output = AliasProcessor.alias(aliasScores, "test");

        assertNotNull(output);
        assertEquals(aliasScores.getRecords().size(), output.getRecords().size());

        List<String> outputHeaders = output.getHeaders();
        List<String> scoresHeaders = aliasScores.getHeaders();

        assertEquals(scoresHeaders.size(), outputHeaders.size());
        for (int i = 0; i < outputHeaders.size(); i++) {
            assertEquals("test." + scoresHeaders.get(i).split("\\.")[1], outputHeaders.get(i));
        }
    }

    @Test
    public void shouldReplaceAliasToCsvWhichHasSomeExistingAliasPrior() throws IOException {
        CSV output = AliasProcessor.alias(someAliasScores, "test");

        assertNotNull(output);
        assertEquals(someAliasScores.getRecords().size(), output.getRecords().size());

        List<String> outputHeaders = output.getHeaders();
        List<String> scoresHeaders = someAliasScores.getHeaders();

        assertEquals(scoresHeaders.size(), outputHeaders.size());
        for (int i = 0; i < outputHeaders.size(); i++) {
            String[] headerParts = scoresHeaders.get(i).split("\\.");
            assertEquals("test." + (headerParts.length > 1 ? headerParts[1] : headerParts[0]), outputHeaders.get(i));
        }
    }

    @Test
    public void shouldThrowExceptionWhenAliasAdditionWillProduceAmbiguousColumnHeaders() throws IOException {
        try {
            AliasProcessor.alias(aliasProblem, "test");
            fail("Expected an illegal argument exception to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Not all unqualified columns are unique in the input data set.", e.getMessage());
        }
    }

    @Test
    public void shouldDropAliasOnCsvWithAPriorAlias() throws IOException {
        CSV output = AliasProcessor.dropAlias(aliasScores);

        assertNotNull(output);
        assertEquals(aliasScores.getRecords().size(), output.getRecords().size());

        List<String> outputHeaders = output.getHeaders();
        List<String> scoresHeaders = aliasScores.getHeaders();

        assertEquals(scoresHeaders.size(), outputHeaders.size());
        for (int i = 0; i < outputHeaders.size(); i++) {
            assertEquals(scoresHeaders.get(i).split("\\.")[1], outputHeaders.get(i));
        }
    }

    @Test
    public void shouldKeepColumnNamesWhenNoAliasPresentToDrop() throws IOException {
        CSV output = AliasProcessor.dropAlias(scores);

        assertNotNull(output);
        assertEquals(scores.getRecords().size(), output.getRecords().size());

        List<String> outputHeaders = output.getHeaders();
        List<String> scoresHeaders = scores.getHeaders();

        assertEquals(scoresHeaders.size(), outputHeaders.size());
        for (int i = 0; i < outputHeaders.size(); i++) {
            assertEquals(scoresHeaders.get(i), outputHeaders.get(i));
        }
    }

    @Test
    public void shouldDropAliasOnlyWhenAliasPresentOnColumnHeader() throws IOException {
        CSV output = AliasProcessor.dropAlias(someAliasScores);

        assertNotNull(output);
        assertEquals(someAliasScores.getRecords().size(), output.getRecords().size());

        List<String> outputHeaders = output.getHeaders();
        List<String> scoresHeaders = someAliasScores.getHeaders();

        assertEquals(scoresHeaders.size(), outputHeaders.size());
        for (int i = 0; i < outputHeaders.size(); i++) {
            String[] columnParts = scoresHeaders.get(i).split("\\.");
            assertEquals(columnParts.length > 1 ? columnParts[1] : columnParts[0], outputHeaders.get(i));
        }
    }

    @Test
    public void shouldThrowExceptionWhenAliasDropWillProduceAmbiguousColumnHeaders() throws IOException {
        try {
            AliasProcessor.dropAlias(aliasProblem);
            fail("Expected an illegal argument exception to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Alias removal results in duplicate column headers", e.getMessage());
        }
    }
}