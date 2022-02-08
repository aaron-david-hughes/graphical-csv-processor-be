package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import org.apache.commons.csv.CSVRecord;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SetProcessor implements Processor {

    private SetProcessor() {}

    private static final String HEADER_ERROR_MSG = "Compliment must be ran on CSVs with the same headers";

    public static CSV getCompliment(CSV set, CSV subset, String keyHeader) {
        if (!isComparableSet(set, subset)) throw new IllegalArgumentException(HEADER_ERROR_MSG);

        List<CSVRecord> records = set.getRecords();
        List<CSVRecord> subsetRecords = subset.getRecords();
        int setHeaderIdx = set.getHeaderMap().get(keyHeader);
        int subsetHeaderIdx = subset.getHeaderMap().get(keyHeader);

        Set<String> presentInSubset = subsetRecords.stream()
                .map(csvRecord -> csvRecord.get(subsetHeaderIdx))
                .collect(Collectors.toSet());

        List<CSVRecord> complimentRecords = records.stream()
                .filter(csvRecord -> !presentInSubset.contains(csvRecord.get(setHeaderIdx)))
                .collect(Collectors.toList());

        return new CSV(set.getHeaders(), set.getHeaderMap(), complimentRecords);
    }

    private static boolean isComparableSet(CSV set, CSV subset) {
        List<String> headers = set.getHeaders();
        List<String> subsetHeaders = subset.getHeaders();

        if (headers.size() == subsetHeaders.size()) {
            for (int i = headers.size() - 1; i >= 0; i--) {
                if (!headers.get(i).equals(subsetHeaders.get(i))) return false;
                headers.remove(i);
            }

            return headers.isEmpty();
        }

        return false;
    }
}
