package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.utils.ProcessingUtils.createCSV;
import static com.graphicalcsvprocessing.graphicalcsvprocessing.utils.ProcessingUtils.listToString;

public class MergeRowsProcessor implements Processor {

    private MergeRowsProcessor() {}

    public static CSV mergeRow(CSV input, String column, String value) throws IOException {
        int columnIdx = input.getHeaderMap().get(column);
        int mergeRowIdx = -1;

        List<CSVRecord> rows = input.getRecords();
        List<Integer> mergeIndexes = new ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).get(columnIdx).equals(value)) {
                mergeIndexes.add(i);

                if (mergeRowIdx < 0) mergeRowIdx = i;
            }
        }

        if (mergeIndexes.size() < 2) return input;

        rows.removeAll(mergeIndexes.stream().map(rows::get).collect(Collectors.toList()));

        List<String> outcomeRow = merge(input, mergeIndexes);

        StringBuilder sb = new StringBuilder().append(listToString(input.getHeaders())).append("\n");

        for (int i = 0; i < rows.size(); i++) {
            if (mergeRowIdx == i) {
                sb.append(listToString(outcomeRow)).append("\n");
            }
            sb.append(listToString(rows.get(i).toList())).append("\n");
        }

        return createCSV(sb);
    }

    public static List<String> merge(CSV input, List<Integer> mergeIndexes) {
        List<CSVRecord> rows = input.getRecords();
        List<CSVRecord> mergeRows = mergeIndexes.stream().map(rows::get).collect(Collectors.toList());

        if (mergeRows.size() == 1) return new ArrayList<>(mergeRows.get(0).toList());

        List<String> headers = input.getHeaders();
        Map<Integer, String> outcomeRowValues = new ConcurrentHashMap<>();

        IntStream.range(0, headers.size()).parallel()
                .forEach(i -> outcomeRowValues.put(i, assertNoColumnValuesClash(mergeRows, i)));

        List<String> outcomeRow = new ArrayList<>();

        for (int i = 0; i < outcomeRowValues.size(); i++)
            outcomeRow.add(outcomeRowValues.get(i));

        return outcomeRow;
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
