package com.graphicalcsvprocessing.graphicalcsvprocessing.controller;

import com.graphicalcsvprocessing.graphicalcsvprocessing.config.Config;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.services.CsvProcessorService;
import com.graphicalcsvprocessing.graphicalcsvprocessing.utils.TestControllerConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@ContextConfiguration(classes = {TestControllerConfig.class, Config.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class GraphicalCsvControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @SpyBean
    private CsvProcessorService csvProcessorService;

    @Mock
    private CSV csv;

    private final Map<String, CSV> resultCsvs = new HashMap<>();

    private final MockMultipartFile[] csvs = new MockMultipartFile[1];

    private final String graph = jsonGraph();

    @Before
    public void setUp() throws IOException {
        csvs[0] = new MockMultipartFile("csvData", "Scores.csv",
                MediaType.TEXT_PLAIN_VALUE, Files.readAllBytes(new File("src/test/resources/Scores.csv").toPath()));

        resultCsvs.put("test.csv", csv);
        Mockito.doReturn(resultCsvs).when(csvProcessorService).process(any(), any());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.writeBytes(csvs[0].getBytes());
        Mockito.doReturn(outputStream).when(csv).getOutputStream();
    }

    @Test
    public void shouldMakeSuccessfulPostCallAgainstProcessEndpoint() throws Exception {
        MockMvc mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mvc.perform(MockMvcRequestBuilders.multipart("/process")
                .file("csvFiles", csvs[0].getBytes())
                .param("graph", graph)
        ).andExpect(status().isOk());
    }

    @Test
    public void shouldGet400PostCallAgainstProcessEndpointMissingCsvFilesFormParam() throws Exception {
        MockMvc mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mvc.perform(MockMvcRequestBuilders.multipart("/process")
                .param("graph", graph)
        ).andExpect(status().is(400));
    }

    @Test
    public void shouldGet400PostCallAgainstProcessEndpointMissingGraphParam() throws Exception {
        MockMvc mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mvc.perform(MockMvcRequestBuilders.multipart("/process")
                .file("csvFiles", csvs[0].getBytes())
        ).andExpect(status().is(400));
    }

    @Test
    public void shouldGet404PostCallAgainstAnyOtherEndpoint() throws Exception {
        MockMvc mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mvc.perform(MockMvcRequestBuilders.multipart("/missing")
                .file("csvFiles", csvs[0].getBytes())
                .param("graph", graph)
        ).andExpect(status().is(404));
    }

    private String jsonGraph() {
        return "{\n" +
                "  \"nodes\": [ \n" +
                "    {\n" +
                "      \"id\": \"1\",\n" +
                "      \"group\": \"file\", \n" +
                "      \"operation\":\"open_file\",\n" +
                "      \"name\": \"Scores.csv\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"2\",\n" +
                "      \"group\": \"file\", \n" +
                "      \"operation\": \"write_file\",\n" +
                "      \"name\":\"test.csv\"\n" +
                "    } \n" +
                "  ], \n" +
                "  \"edges\": [  \n" +
                "    { \n" +
                "      \"from\": \"1\",\n" +
                "      \"to\": \"2\",\n" +
                "      \"priority\":\"y\"\n" +
                "    } \n" +
                "  ]\n" +
                "}";
    }
}