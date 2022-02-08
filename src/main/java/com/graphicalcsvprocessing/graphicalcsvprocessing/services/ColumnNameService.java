package com.graphicalcsvprocessing.graphicalcsvprocessing.services;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CorrespondingCSV;

import java.util.ArrayList;
import java.util.List;

public class ColumnNameService {
    private ColumnNameService() {}

    public static String validateAlias(String alias) {
        if (alias != null && alias.matches("^[a-zA-Z0-9].[a-zA-Z0-9 _-]*$")) {
            return alias;
        }

        throw new IllegalArgumentException(
            "Alias supplied (may be a filename) of input file must start with a letter and " +
                    "contain only a-z, A-Z, 0-9, space, underscore, hyphen."
        );
    }

    public static String validateColumnName(String columnName) {
        if (columnName != null && columnName.matches("^[a-zA-Z0-9].[a-zA-Z0-9 _-]*(\\.[a-zA-Z0-9 _-]+)?$")) {
            return columnName;
        }

        throw new IllegalArgumentException(
                String.format(
                        "Column name supplied '%s' does not match allowed input of alphanumeric characters, spaces, " +
                                "hyphens, underscores, and maximum one '.' in the middle of the name",
                        columnName
                )
        );
    }

    public static CorrespondingCSV deduceColumnName(String columnName, List<CSV> csvData) {
        if (columnName.contains(".")) {
            List<CSV> csvMatches = new ArrayList<>();

            for (CSV csv : csvData) {
                Integer i = csv.getHeaderMap().get(columnName);
                if (i != null) csvMatches.add(csv);
            }

            return validate(csvMatches, columnName, columnName);
        }

        List<CSV> csvMatches = new ArrayList<>();
        String col = "";

        for (CSV csv : csvData) {
            for (String hdr : csv.getHeaders()) {
                if (hdr.endsWith("." + columnName) || hdr.equals(columnName)) {
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
