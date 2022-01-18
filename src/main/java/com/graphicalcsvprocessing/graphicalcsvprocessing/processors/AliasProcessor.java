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

        StringBuilder sb = new StringBuilder().append(listToString(aliasHeadersList)).append("\n");

        for (CSVRecord csvRecord : input.getRecords()) {
            List<String> row = new ArrayList<>(csvRecord.toList());
            sb.append(listToString(row)).append("\n");
        }

        return createCSV(sb);
    }
}
