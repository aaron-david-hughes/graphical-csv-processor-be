package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.utils.ProcessingUtils.createCSV;
import static com.graphicalcsvprocessing.graphicalcsvprocessing.utils.ProcessingUtils.listToString;

/**
 * processor to make the data set unique across the column specified unless a merge which occurs results in a data clash
 */
public class UniqueColumnProcessor implements Processor {

    private UniqueColumnProcessor() {}

    public static CSV uniqueColumn(CSV input, String column) throws IOException {
        int idx = input.getHeaderMap().get(column);

        List<CSVRecord> rows = input.getRecords();

        Map<Integer, String> currentOrder = new ConcurrentHashMap<>();
        IntStream.range(0, rows.size()).parallel().forEach(i -> currentOrder.put(i, rows.get(i).get(idx)));

        Map<String, List<Integer>> uniqueValuesAndIndexes = new ConcurrentHashMap<>();
        IntStream.range(0, input.getRecords().size()).parallel().forEach(i -> {
            if (uniqueValuesAndIndexes.containsKey(rows.get(i).get(idx))) {
                uniqueValuesAndIndexes.get(rows.get(i).get(idx)).add(i);
            } else {
                List<Integer> l = new ArrayList<>();
                l.add(i);
                uniqueValuesAndIndexes.put(rows.get(i).get(idx), l);
            }
        });

        List<List<String>> newRows = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            String value = currentOrder.get(i);
            List<Integer> mergeIndexes = uniqueValuesAndIndexes.get(value);

            if (mergeIndexes == null) continue;

            Collections.sort(mergeIndexes);
            newRows.add(MergeRowsProcessor.merge(input, mergeIndexes));
            uniqueValuesAndIndexes.remove(value);
        }

        StringBuilder sb = new StringBuilder().append(listToString(input.getHeaders())).append("\n");

        for (List<String> row : newRows) {
            sb.append(listToString(row)).append("\n");
        }

        return createCSV(sb);
    }
}
