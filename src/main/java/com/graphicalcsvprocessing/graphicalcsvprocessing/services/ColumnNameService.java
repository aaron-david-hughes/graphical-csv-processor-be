package com.graphicalcsvprocessing.graphicalcsvprocessing.services;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CorrespondingCSV;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ColumnNameService {
    private ColumnNameService() {}

    public static CorrespondingCSV deduceColumnName(String columnName, List<CSV> csvData) {
        if (columnName.contains(".")) {
            List<CSV> csvMatches = csvData.stream()
                    .filter(csv -> csv.getHeaders().contains(columnName))
                    .collect(Collectors.toList());

            return validate(csvMatches, columnName, columnName);
        }

        List<CSV> csvMatches = new ArrayList<>();
        String col = "";

        for (CSV csv : csvData) {
            for (String hdr : csv.getHeaders()) {
                if (hdr.matches(".*\\." + columnName)) {
                    col = hdr;
                    csvMatches.add(csv);
                }
            }
        }

        return validate(csvMatches, col, columnName);
    }

    private static CorrespondingCSV validate(List<CSV> csvMatches, String identifiedColumnName, String suppliedColumnName) {
        switch (csvMatches.size()) {
            case 0:
                throw new IllegalArgumentException("No column name found or deducible: " + suppliedColumnName);
            case 1:
                return new CorrespondingCSV(identifiedColumnName, csvMatches.get(0));
            default:
                throw new IllegalArgumentException("Column name is ambiguous: " + suppliedColumnName);
        }
    }
}
