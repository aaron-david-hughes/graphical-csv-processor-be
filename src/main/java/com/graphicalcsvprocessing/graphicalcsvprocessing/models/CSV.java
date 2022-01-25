package com.graphicalcsvprocessing.graphicalcsvprocessing.models;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.utils.ProcessingUtils.listToString;

public class CSV {

    private final List<String> headers;
    private final Map<String, Integer> headerMap;
    private final List<CSVRecord> records;

    public CSV(CSVParser parser) throws IOException {
        this.headers = parser.getHeaderNames();
        this.headerMap = parser.getHeaderMap();
        this.records = parser.getRecords();

        parser.close();
    }

    public CSV(List<String> headers, Map<String, Integer> headerMap, List<CSVRecord> records) {
        this.headers = headers;
        this.headerMap = headerMap;
        this.records = records;
    }

    public CSV(CSV csv) {
        this.headers = csv.getHeaders();
        this.headerMap = csv.getHeaderMap();
        this.records = csv.getRecords();
    }

    public List<String> getHeaders() {
        return new ArrayList<>(this.headers);
    }

    public Map<String, Integer> getHeaderMap() {
        return new HashMap<>(headerMap);
    }

    public List<CSVRecord> getRecords() {
        return new ArrayList<>(this.records);
    }

    public OutputStream getOutputStream() {
        StringBuilder sb = new StringBuilder().append(listToString(headers)).append("\n");

        for (CSVRecord entry : records) {
            sb.append(listToString(entry.toList())).append("\n");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.writeBytes(sb.substring(0, sb.length() - 1).getBytes());
        return outputStream;
    }
}
