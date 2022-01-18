package com.graphicalcsvprocessing.graphicalcsvprocessing.utils;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.services.CsvProcessorService;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This helper class enables creation of example data to be supplied in tests of operations
 */
public class TestCSVBuilder extends CsvProcessorService {

    public Map<String, CSV> buildCsvInput(String... files) throws IOException {
        return super.readInputCsvList(this.prepareMockMultipartFileArray(files));
    }

    public MultipartFile[] prepareMockMultipartFileArray(String... files) throws IOException {
        List<MockMultipartFile> inputs = new ArrayList<>();

        for (String filename : files) {
            File file = new File("src/test/resources/" + filename);
            inputs.add(
                    new MockMultipartFile(filename, filename,
                            MediaType.TEXT_PLAIN_VALUE, Files.readAllBytes(file.toPath()))
            );
        }

        return inputs.toArray(MultipartFile[]::new);
    }
}
