package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.utils.ProcessingUtils.*;

public class ColumnProcessor implements Processor {

    private ColumnProcessor() {}

    public static CSV dropColumns(CSV input, List<String> columns) throws IOException {
        return getReturnColumns(input, columns, true);
    }

    public static CSV takeColumns(CSV input, List<String> columns) throws IOException {
        return getReturnColumns(input, columns, false);
    }

    private static CSV getReturnColumns(CSV input, List<String> columns, boolean removeSpecifiedColumns) throws IOException {
        List<String> headers = input.getHeaders();
        List<String> removedHeaders = new ArrayList<>();

        List<Integer> specifiedColumnsIndexes = new ArrayList<>();

        for (String col : columns) {
            int colIdx = headers.indexOf(col);

            if (colIdx < 0) {
                throw new IllegalArgumentException("Column name not found in input '" + col + "'.");
            }

            specifiedColumnsIndexes.add(colIdx);
        }

        Collections.sort(specifiedColumnsIndexes);
        Collections.reverse(specifiedColumnsIndexes);

        for (int colIdx : specifiedColumnsIndexes) {
            removedHeaders.add(headers.remove(colIdx));
        }

        Collections.reverse(removedHeaders);

        StringBuilder sb = new StringBuilder()
                .append(listToString(removeSpecifiedColumns ? headers : removedHeaders))
                .append("\n");

        List<CSVRecord> records = input.getRecords();

        for (CSVRecord csvRecord : records) {
            List<String> row = new ArrayList<>(csvRecord.toList());
            List<String> removedRow = new ArrayList<>();

            for (int colIdx : specifiedColumnsIndexes) {
                removedRow.add(row.remove(colIdx));
            }

            Collections.reverse(removedRow);

            sb.append(listToString(removeSpecifiedColumns ? row : removedRow)).append("\n");
        }

        return createCSV(sb);
    }
}
