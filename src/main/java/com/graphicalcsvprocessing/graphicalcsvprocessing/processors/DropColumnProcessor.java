package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.utils.ProcessingUtils.*;

public class DropColumnProcessor implements Processor {

    private DropColumnProcessor() {}

    public static CSV dropColumn(CSV input, String deducedColumnName) throws IOException {
        List<String> headers = input.getHeaders();

        int colIdx = headers.indexOf(deducedColumnName);

        if (colIdx < 0) {
            throw new IllegalArgumentException("Column name not found in input '" + deducedColumnName + "'.");
        }

        headers.remove(colIdx);

        List<CSVRecord> records = input.getRecords();

        StringBuilder sb = new StringBuilder().append(listToString(headers)).append("\n");

        for (CSVRecord csvRecord : records) {
            List<String> row = new ArrayList<>(csvRecord.toList());
            row.remove(colIdx);
            sb.append(listToString(row)).append("\n");
        }

        return createCSV(sb);
    }
}
