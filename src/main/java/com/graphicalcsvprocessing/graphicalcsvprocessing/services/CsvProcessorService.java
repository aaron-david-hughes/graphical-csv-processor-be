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

@Service
public class CsvProcessorService {

    public Map<String, CSV> process(GraphDataModel gdm, MultipartFile[] csvFiles) throws IOException {
        Map<String, CSV> csvData = readInputCsvList(csvFiles);

        prepareCsvData(gdm, csvData);

        //change to returning the list when know how to return multiple files
        return processNodes(gdm, csvData);
    }

    public void prepareCsvData(GraphDataModel gdm, Map<String, CSV> csvData) {
        //only one open file node allowed for a specific file name
        for (Node node : gdm.getNodes()) {
            if (node instanceof OpenFileNode) {
                String name = ((OpenFileNode) node).getName();
                csvData.put(node.getId(), csvData.remove(name));
            }
        }
    }

    protected Map<String, CSV> processNodes(GraphDataModel gdm, Map<String, CSV> csvData) throws IOException {
        Map<String, CSV> results = gdm.process(csvData);

        if (results.isEmpty()) {
            throw new IllegalArgumentException("Request has no return file(s)");
        }

        return results;
    }

    protected Map<String, CSV> readInputCsvList(MultipartFile[] csvFiles) throws IOException {
        if (csvFiles.length == 0) throw new IllegalArgumentException("No files supplied");

        Map<String, CSV> csvData = new HashMap<>();

        for (MultipartFile csv : csvFiles) {
            //find a way to prefix filename to each header
            BOMInputStream bom = new BOMInputStream(new ByteArrayInputStream(csv.getBytes()), false);

            StringBuilder headers = new StringBuilder();
            byte[] nextByte = bom.readNBytes(1);

            //TODO: this should be checked against other file encoding standards (works against UTF8)
            while (nextByte[0] != 13 && nextByte[0] != 10) {
                headers.append(new String(nextByte));
                nextByte = bom.readNBytes(1);
            }

            String[] headersArray = headers.toString().split(",");

            for (int i = 0; i < headersArray.length; i++) {
                headersArray[i] = csv.getOriginalFilename() != null
                        ? csv.getOriginalFilename().replace(".csv", "") + "." + headersArray[i]
                        : headersArray[i];
            }

            InputStreamReader reader = new InputStreamReader(bom);

            CSVParser parser = new CSVParser(reader, CSVFormat.Builder.create().setHeader(headersArray).build());
            csvData.put(csv.getOriginalFilename(), new CSV(parser));
        }

        return csvData;
    }
}
