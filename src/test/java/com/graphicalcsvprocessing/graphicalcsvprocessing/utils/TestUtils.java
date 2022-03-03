package com.graphicalcsvprocessing.graphicalcsvprocessing.utils;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import org.apache.commons.csv.CSVRecord;

import java.util.HashMap;
import java.util.Map;

public class TestUtils {

    public static Map<String, Integer> countRowsWithSameColumnValue(CSV csv, String column) {
        Map<String, Integer> count = new HashMap<>();

        for (CSVRecord csvRecord : csv.getRecords()) {
            String s = csvRecord.get(column);

            if (count.containsKey(s)) {
                count.put(s, count.get(s) + 1);
            } else {
                count.put(s, 1);
            }
        }

        return count;
    }
}
