package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.utils.ProcessingUtils.createCSV;
import static com.graphicalcsvprocessing.graphicalcsvprocessing.utils.ProcessingUtils.listToString;

public class MergeRowsProcessor implements Processor {

    private MergeRowsProcessor() {}

    public static CSV merge(CSV input, String column, String value) throws IOException {
        //basically will not merge unless values are equal or one is null while other is okay.
        int columnIdx = input.getHeaderMap().get(column);
        int mergeRowIdx = -1;

        List<CSVRecord> rows = input.getRecords();
        List<CSVRecord> mergeRows = new ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).get(columnIdx).equals(value)) {
                mergeRows.add(rows.get(i));

                if (mergeRowIdx < 0) mergeRowIdx = i;
            }
        }

        rows.removeAll(mergeRows);

        if (mergeRows.size() == 1) return input;

        List<String> headers = input.getHeaders();
        List<String> outcomeRow = new ArrayList<>();

        for (int i = 0; i < headers.size(); i++) {
            outcomeRow.add(assertNoColumnValuesClash(mergeRows, i));
        }

        StringBuilder sb = new StringBuilder().append(listToString(headers)).append("\n");

        for (int i = 0; i < rows.size(); i++) {
            if (mergeRowIdx == i) {
                sb.append(listToString(outcomeRow)).append("\n");
            }
            sb.append(listToString(rows.get(i).toList())).append("\n");
        }

        return createCSV(sb);
    }

    private static boolean nullBlankOrEmpty(String v) {
        return v == null || v.isEmpty() || v.isBlank();
    }

    private static String assertNoColumnValuesClash(List<CSVRecord> rows, int colIdx) {
        String currentValue = null;

        for (CSVRecord value : rows) {
            String cellValue = value.get(colIdx);
            if (!nullBlankOrEmpty(cellValue)) {
                if (currentValue == null || currentValue.equals(cellValue)) currentValue = cellValue;
                else throw new IllegalArgumentException("Cell values in the relevant merge rows clash.");
            }
        }

        return currentValue;
    }
}
