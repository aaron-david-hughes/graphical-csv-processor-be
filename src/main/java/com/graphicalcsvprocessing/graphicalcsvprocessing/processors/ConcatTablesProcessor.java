package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.utils.ProcessingUtils.createCSV;
import static com.graphicalcsvprocessing.graphicalcsvprocessing.utils.ProcessingUtils.listToString;

/**
 * processor to concatenate two input files together in O(mn) where
 * n is the number of records in the the second input and m is the number of columns in the second input
 */
public class ConcatTablesProcessor implements Processor {

    private ConcatTablesProcessor() {}

    public static CSV concat(CSV input1, CSV input2) throws IOException {
        Map<String, Integer> headers = new LinkedHashMap<>();

        List<String> headers1 = input1.getHeaders();
        for (String header : headers1) {
            headers.put(header, input1.getHeaderMap().get(header));
        }

        int newIdx = headers1.size();
        for (String header : input2.getHeaders()) {
            if (headers.get(header) == null) {
                headers.put(header, newIdx);
                newIdx++;
            }
        }

        StringBuilder sb = new StringBuilder();

        for (String header : headers.keySet()) {
            sb.append(header).append(",");
        }
        sb.replace(sb.length() - 1, sb.length(), "\n");

        for (CSVRecord csvRecord : input1.getRecords()) {
            sb.append(listToString(csvRecord.toList()))
                    .append(",".repeat(headers.size() - input1.getHeaderMap().size()))
                    .append("\n");
        }

        List<String> headers2 = input2.getHeaders();
        for (CSVRecord csvRecord : input2.getRecords()) {
            String[] row = new String[headers.size()];

            for (int i = 0; i < headers2.size(); i++) {
                int idx = headers.get(headers2.get(i));
                row[idx] = csvRecord.get(i);
            }

            sb.append(listToString(Arrays.stream(row).collect(Collectors.toList()))).append("\n");
        }

        return createCSV(sb);
    }
}
