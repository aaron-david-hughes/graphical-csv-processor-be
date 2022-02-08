package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import org.apache.commons.csv.CSVRecord;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.services.ColumnNameService.deduceColumnName;
import static com.graphicalcsvprocessing.graphicalcsvprocessing.services.ColumnNameService.validateColumnName;

public class SetProcessor implements Processor {

    private SetProcessor() {}

    private static final String HEADER_ERROR_MSG = "Compliment must be ran on CSVs with the same headers";

    public static CSV getCompliment(CSV set, CSV subset, String setHeader, String subsetHeader) {
        if (!isComparableSet(set, subset, setHeader, subsetHeader)) throw new IllegalArgumentException(HEADER_ERROR_MSG);

        List<CSVRecord> records = set.getRecords();
        List<CSVRecord> subsetRecords = subset.getRecords();
        int setHeaderIdx = set.getHeaderMap().get(setHeader);
        int subsetHeaderIdx = subset.getHeaderMap().get(subsetHeader);

        Set<String> presentInSubset = subsetRecords.stream()
                .map(csvRecord -> csvRecord.get(subsetHeaderIdx))
                .collect(Collectors.toSet());

        List<CSVRecord> complimentRecords = records.stream()
                .filter(csvRecord -> !presentInSubset.contains(csvRecord.get(setHeaderIdx)))
                .collect(Collectors.toList());

        return new CSV(set.getHeaders(), set.getHeaderMap(), complimentRecords);
    }

    private static boolean isComparableSet(CSV set, CSV subset, String setHeader, String subsetHeader) {
        List<String> headers = set.getHeaders();
        List<String> subsetHeaders = subset.getHeaders();

        if (headers.size() == subsetHeaders.size()) {
            List<String> strippedAliasSubsetHeaders = subsetHeaders.stream()
                    .filter(header -> !header.equals(subsetHeader))
                    .map(s -> validateColumnName(s).contains(".") ? s.substring(s.indexOf('.') + 1) : s)
                    .collect(Collectors.toList());

            for (String header : strippedAliasSubsetHeaders) {
                try {
                    String superHeader = deduceColumnName(header, Collections.singletonList(set)).getColumnName();
                    headers.remove(superHeader);
                } catch (IllegalArgumentException e) {
                    return false;
                }
            }

            return headers.size() == 1 && headers.get(0).equals(setHeader);
        } else {
            return false;
        }
    }
}
