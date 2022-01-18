package com.graphicalcsvprocessing.graphicalcsvprocessing.services;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.GraphDataModel;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.edges.Edge;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.Node;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.fileOperations.OpenFileNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.fileOperations.WriteFileNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.binaryOperations.JoinProcessingNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.JoinProcessor;
import com.graphicalcsvprocessing.graphicalcsvprocessing.utils.TestCSVBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CsvProcessorServiceTest {

    CsvProcessorService csvProcessorService = new CsvProcessorService();

    MultipartFile[] multipartFiles;

    @Before
    public void setUp() throws IOException {
        multipartFiles = new TestCSVBuilder()
                .prepareMockMultipartFileArray("Attendance.csv", "AttendanceUsingStudentNumber.csv", "Scores.csv");
    }


    //tests about input set up
    //read input nodes
    @Test
    public void readInputCsvListThrowsIllegalArgumentExceptionWhenNullSupplied() throws IOException {
        try {
            csvProcessorService.readInputCsvList(null);
            fail("Expected an Illegal Argument Exception to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("No files supplied", e.getMessage());
        }
    }

    @Test
    public void readInputCsvListThrowsIllegalArgumentExceptionWhenEmptyArraySupplied() throws IOException {
        try {
            csvProcessorService.readInputCsvList(new MultipartFile[0]);
            fail("Expected an Illegal Argument Exception to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("No files supplied", e.getMessage());
        }
    }

    @Test
    public void readInputCsvListThrowsIllegalArgumentExceptionWhenArrayWithNullFileSupplied() throws IOException {
        try {
            csvProcessorService.readInputCsvList(new MultipartFile[] {null});
            fail("Expected an Illegal Argument Exception to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Files supplied are invalid", e.getMessage());
        }
    }

    @Test
    public void readInputCsvListThrowsIllegalArgumentExceptionWhenEmptyFileSupplied() throws IOException {
        MultipartFile[] multipartFiles = new TestCSVBuilder().prepareMockMultipartFileArray("Empty.csv");
        try {
            csvProcessorService.readInputCsvList(multipartFiles);
            fail("Expected an Illegal Argument Exception to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("File 'Empty.csv' is empty.", e.getMessage());
        }
    }

    @Test
    public void readInputCsvListReturnsSingletonMapWhenSingleFileSupplied() throws IOException {
        Map<String, CSV> csvData = csvProcessorService.readInputCsvList(
                new TestCSVBuilder().prepareMockMultipartFileArray("Attendance.csv"));

        assertNotNull(csvData);
        assertEquals(1, csvData.size());
        validateCsv(csvData, "Attendance.csv");
    }

    @Test
    public void readInputCsvListReturnsMapOfSizeEqualToNumberOfFilesSuppliedIfValid() throws IOException {
        Map<String, CSV> csvData = csvProcessorService.readInputCsvList(multipartFiles);

        assertNotNull(csvData);
        assertEquals(3, csvData.size());
        validateCsv(csvData, "Attendance.csv");
        validateCsv(csvData, "AttendanceUsingStudentNumber.csv");
        validateCsv(csvData, "Scores.csv");
    }

    @Test
    public void readInputCsvListThrowsIllegalArgumentExceptionWhenFilenameIsInvalidAsAnIdentifier() throws IOException {
        String[] invalidFilenamesForIdentifiers = {null, "", " ", "     ", "\n"};

        for (String s : invalidFilenamesForIdentifiers) {
            File file = new File("src/test/resources/Attendance.csv");
            MockMultipartFile f = new MockMultipartFile(
                    "name", s, MediaType.TEXT_PLAIN_VALUE, Files.readAllBytes(file.toPath()));

            try {
                csvProcessorService.readInputCsvList(new MultipartFile[] {f});
                fail("Expected an illegal argument exception to be thrown");
            } catch (IllegalArgumentException e) {
                assertEquals("Alias supplied (may be a filename) of input file must start with a letter and " +
                        "contain only a-z, A-Z, 0-9, space, underscore, hyphen.", e.getMessage());
            }
        }
    }

    @Test
    public void readInputCsvListThrowsIllegalArgumentExceptionWhenFilenameIsNull() throws IOException {
        File file = new File("src/test/resources/Attendance.csv");
        MockMultipartFile f = new MockMultipartFile(
                "null", null, MediaType.TEXT_PLAIN_VALUE, Files.readAllBytes(file.toPath()));

        try {
            csvProcessorService.readInputCsvList(new MultipartFile[] {f});
            fail("Expected an illegal argument exception to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Alias supplied (may be a filename) of input file must start with a letter and " +
                    "contain only a-z, A-Z, 0-9, space, underscore, hyphen.", e.getMessage());
        }
    }

    @Test
    public void prepareCsvDataWhenGraphProcessingIsNull() throws IOException {
        GraphDataModel[] invalidGraphs = {
                null,
                new GraphDataModel(null, null)
        };

        for (GraphDataModel gdm : invalidGraphs) {
            Map<String, CSV> csvData = csvProcessorService.readInputCsvList(multipartFiles);

            assertNotNull(csvData);
            assertEquals(3, csvData.size());
            validateCsv(csvData, "Attendance.csv");
            validateCsv(csvData, "AttendanceUsingStudentNumber.csv");
            validateCsv(csvData, "Scores.csv");

            try {
                csvProcessorService.prepareCsvData(gdm, csvData);
                fail("Expected an Illegal Argument Exception to be thrown");
            } catch (IllegalArgumentException e) {
                assertEquals("Problem with processing graph inputs", e.getMessage());
            }
        }
    }

    @Test
    public void prepareCsvDataWhenGraphProcessingIsInvalid() throws IOException {
        GraphDataModel[] invalidGraphs = {
                new GraphDataModel(new Node[] {
                        new WriteFileNode("write1", "files", "write_file", "rename.csv"),
                        new WriteFileNode("write2", "files", "write_file", "rename.csv"),
                        new WriteFileNode("write3", "files", "write_file", "rename.csv")
                }, null),
                new GraphDataModel(new Node[] {
                        new OpenFileNode("open1", "files", "open_file", "NotAttendance.csv"),
                        new OpenFileNode("open2", "files", "open_file", "AttendanceUsingStudentNumber.csv"),
                        new OpenFileNode("open3", "files", "open_file", "Scores.csv")
                }, null),
                new GraphDataModel(new Node[] {
                        new OpenFileNode("open1", "files", "open_file", "NotAttendance.csv"),
                        new OpenFileNode("open2", "files", "open_file", "NotAttendanceUsingStudentNumber.csv"),
                        new OpenFileNode("open3", "files", "open_file", "NotScores.csv")
                }, null)
        };

        for (GraphDataModel gdm : invalidGraphs) {
            Map<String, CSV> csvData = csvProcessorService.readInputCsvList(multipartFiles);

            assertNotNull(csvData);
            assertEquals(3, csvData.size());
            validateCsv(csvData, "Attendance.csv");
            validateCsv(csvData, "AttendanceUsingStudentNumber.csv");
            validateCsv(csvData, "Scores.csv");

            try {
                csvProcessorService.prepareCsvData(gdm, csvData);
                fail("Expected an Illegal Argument Exception to be thrown");
            } catch (IllegalArgumentException e) {
                assertEquals("Not all supplied files have a corresponding Open File Node.", e.getMessage());
            }
        }
    }

    @Test
    public void prepareCsvDataWhenGraphProcessingIsValid() throws IOException {
        Map<String, CSV> csvData = csvProcessorService.readInputCsvList(multipartFiles);

        assertNotNull(csvData);
        assertEquals(3, csvData.size());
        validateCsv(csvData, "Attendance.csv");
        validateCsv(csvData, "AttendanceUsingStudentNumber.csv");
        validateCsv(csvData, "Scores.csv");


        csvProcessorService.prepareCsvData(
            new GraphDataModel(new Node[] {
                new OpenFileNode("open1", "files", "open_file", "Attendance.csv"),
                new OpenFileNode("open2", "files", "open_file", "AttendanceUsingStudentNumber.csv"),
                new OpenFileNode("open3", "files", "open_file", "Scores.csv")
            }, null),
            csvData
        );

        assertNotNull(csvData);
        assertEquals(3, csvData.size());
        validateCsv(csvData, "open1");
        validateCsv(csvData, "open2");
        validateCsv(csvData, "open3");
    }

    @Test
    public void processNodesHandlesScenarioOfNoReturnFiles() throws IOException {
        GraphDataModel gdm = mock(GraphDataModel.class);
        Map<String, CSV> csvData = new HashMap<>();

        when(gdm.process(csvData)).thenReturn(new HashMap<>());

        try {
            csvProcessorService.processNodes(gdm, csvData);
            fail("Expected an Illegal Argument Exception to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Request has no return file(s)", e.getMessage());
        }
    }

    //essentially integration tests at this point

    @Test
    public void processReturnsListOfFilesAfterProcessingSimpleGraph() throws IOException {
        GraphDataModel gdm = new GraphDataModel(new Node[] {
                new OpenFileNode("open1", "files", "open_file", "Attendance.csv"),
                new OpenFileNode("open2", "files", "open_file", "Scores.csv"),
                new JoinProcessingNode(
                        "join1", "processing", "join",
                        "Scores.StudentNum", "Attendance.Attendant", JoinProcessor.JoinType.LEFT
                )
        }, new Edge[] {
                new Edge("open1", "join1"),
                new Edge("open2", "join1")
        });

        Map<String, CSV> csvReturns = csvProcessorService.process(
                gdm, new TestCSVBuilder().prepareMockMultipartFileArray("Attendance.csv", "Scores.csv")
        );

        assertNotNull(csvReturns);
        assertEquals(1, csvReturns.size());
        validateCsv(csvReturns, "join1");
    }

    //helpers
    private void validateCsv(Map<String, CSV> csvData, String name) {
        CSV csv = csvData.get(name);
        assertNotNull(csv);
        assertTrue(csv.getHeaders().size() > 0);
        assertTrue(csv.getRecords().size() > 0);
    }
}