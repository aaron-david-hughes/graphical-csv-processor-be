package com.graphicalcsvprocessing.graphicalcsvprocessing.services;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.GraphicalDataModel;
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
 * nodes will be represented as CSV objects stored in a local map variable
 * this map will be on name attributes for files if the node has one (file nodes)
 * if no name attribute it will done on searching map by node id
 */
@Service
public class CsvProcessorService {

    public OutputStream process(GraphicalDataModel gdm, MultipartFile[] csvFiles) throws IOException {
        Map<String, CSV> csvData = readInputCsvList(csvFiles);

        prepareCsvData(gdm, csvData);

        validateRequest(gdm, csvData);

        CSV csv = processNodes(gdm, csvData);

        return csv.getOutputStream();
    }

    public void prepareCsvData(GraphicalDataModel gdm, Map<String, CSV> csvData) {
        //only one open file node allowed for a specific file name
        for (Node node : gdm.getNodes()) {
            if (node instanceof OpenFileNode) {
                String name = ((OpenFileNode) node).getName();
                csvData.put(node.getId(), csvData.remove(name));
            }
        }
    }

    protected void validateRequest(GraphicalDataModel gdm, Map<String, CSV> csvData) {
        //ensure at least one input file
        if (csvData.isEmpty())
            throw new IllegalArgumentException("Request must have input data supplied");

        //ensure for every open_file node there is a file which matches it's name
        for (Node node : gdm.getNodes()) {
            if (node instanceof OpenFileNode && !csvData.containsKey(node.getId()))
                throw new IllegalArgumentException("Some open_file nodes refer to missing file");
        }
    }

    protected CSV processNodes(GraphicalDataModel gdm, Map<String, CSV> csvData) throws IOException {
        CSV result = gdm.process(csvData);

        if (result == null) {
            throw new IllegalArgumentException("Request must contain process nodes. None present.");
        }

        return result;
    }

    protected Map<String, CSV> readInputCsvList(MultipartFile[] csvFiles) throws IOException {
        Map<String, CSV> csvData = new HashMap<>();

        for (MultipartFile csv : csvFiles) {
            InputStreamReader reader = new InputStreamReader(new BOMInputStream(new ByteArrayInputStream(csv.getBytes()), false));
            CSVParser parser = new CSVParser(reader, CSVFormat.Builder.create().setHeader().build());
            csvData.put(csv.getOriginalFilename(), new CSV(parser));
        }

        return csvData;
    }
}
