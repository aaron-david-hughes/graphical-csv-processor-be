package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import org.apache.commons.csv.CSVRecord;

import java.util.List;

public class LimitProcessor implements Processor {

    private LimitProcessor() {}

    public static CSV limit(CSV input, int limit) {
        List<CSVRecord> csvRecords = input.getRecords();

        if (csvRecords.size() < limit) limit = csvRecords.size();

        return new CSV(input.getHeaders(), input.getHeaderMap(), csvRecords.subList(0, limit));
    }
}
