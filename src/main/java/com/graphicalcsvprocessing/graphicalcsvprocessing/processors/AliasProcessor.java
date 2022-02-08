package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.util.*;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.utils.ProcessingUtils.*;

public class AliasProcessor {

    private AliasProcessor() {}

    public static CSV alias(CSV input, String alias) throws IOException {
        List<String> oldHeaders = input.getHeaders();
        Set<String> aliasHeaders = new HashSet<>();
        List<String> aliasHeadersList = new ArrayList<>();

        for (String oldHeader : oldHeaders) {
            String[] split = oldHeader.split("\\.");
            String col = split.length > 1 ? split[1] : split[0];
            aliasHeaders.add(alias + "." + col);
            aliasHeadersList.add(alias + "." + col);
        }

        if (oldHeaders.size() != aliasHeaders.size()) {
            throw new IllegalArgumentException("Not all unqualified columns are unique in the input data set.");
        }

        return createCSV(getStringBuilder(aliasHeadersList, input.getRecords()));
    }

    public static CSV dropAlias(CSV input) throws IOException {
        List<String> headers = input.getHeaders();
        Set<String> duplicateHeaderCheck = new HashSet<>();

        for (String header : headers) {
            int idx = header.indexOf('.');

            if (idx >= 0 && idx < header.length() - 1) {
                header = header.substring(idx + 1);
            }

            duplicateHeaderCheck.add(header);
        }

        if (duplicateHeaderCheck.size() != headers.size()) {
            throw new IllegalArgumentException("Alias removal results in duplicate column headers");
        }

        return createCSV(getStringBuilder(headers, input.getRecords()));
    }

    private static StringBuilder getStringBuilder(List<String> headers, List<CSVRecord> records) {
        StringBuilder sb = new StringBuilder().append(listToString(headers)).append("\n");

        for (CSVRecord csvRecord : records) {
            List<String> row = new ArrayList<>(csvRecord.toList());
            sb.append(listToString(row)).append("\n");
        }

        return sb;
    }
}
