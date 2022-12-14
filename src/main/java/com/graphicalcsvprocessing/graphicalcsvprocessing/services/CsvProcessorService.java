package com.graphicalcsvprocessing.graphicalcsvprocessing.services;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.GraphDataModel;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.Node;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.fileOperations.OpenFileNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * service which handles the processing of the graph, validating input files with graph beforehand
 */
@Service
public class CsvProcessorService {

    public Map<String, CSV> process(GraphDataModel gdm, MultipartFile[] csvFiles) throws IOException {
        Map<String, CSV> csvData = readInputCsvList(csvFiles);

        prepareCsvData(gdm, csvData);

        return processNodes(gdm, csvData);
    }

    public void prepareCsvData(GraphDataModel gdm, Map<String, CSV> csvData) {
        int openFileCount = 0;

        try {
            for (Node node : gdm.getNodes()) {
                if (node instanceof OpenFileNode) {
                    String name = ((OpenFileNode) node).getName();
                    csvData.put(node.getId(), csvData.remove(name));
                    openFileCount++;
                }
            }
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Problem with processing graph inputs");
        }

        if (openFileCount == 0 || openFileCount != csvData.size())
            throw new IllegalArgumentException("Not all supplied files have a corresponding Open File Node.");
    }

    protected Map<String, CSV> processNodes(GraphDataModel gdm, Map<String, CSV> csvData) throws IOException {
        Map<String, CSV> results = gdm.process(csvData);

        if (results.isEmpty()) {
            throw new IllegalArgumentException("Request has no return file(s)");
        }

        return results;
    }

    protected Map<String, CSV> readInputCsvList(MultipartFile[] csvFiles) throws IOException {
        if (csvFiles == null || csvFiles.length == 0)
            throw new IllegalArgumentException("No files supplied");

        Map<String, CSV> csvData = new HashMap<>();

        for (MultipartFile csv : csvFiles) {
            if (csv == null)
                throw new IllegalArgumentException("Files supplied are invalid");

            BOMInputStream bom = new BOMInputStream(new ByteArrayInputStream(csv.getBytes()), false);

            InputStreamReader reader = new InputStreamReader(bom);

            CSVParser parser = new CSVParser(reader, CSVFormat.Builder.create().setHeader().build());
            csvData.put(csv.getOriginalFilename(), new CSV(parser));
        }

        return csvData;
    }
}
