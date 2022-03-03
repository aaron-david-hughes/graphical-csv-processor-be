package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import org.apache.commons.csv.CSVRecord;

import java.util.List;

public class LimitProcessor implements Processor {

    private LimitProcessor() {}

    public static CSV limit(CSV input, int limit) {
        List<CSVRecord> csvRecords = input.getRecords();

        if (limit < 0) throw new IllegalArgumentException("Limit may not be negative");

        if (csvRecords.size() < limit) return input;

        return new CSV(input.getHeaders(), input.getHeaderMap(), csvRecords.subList(0, limit));
    }
}
