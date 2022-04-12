package com.graphicalcsvprocessing.graphicalcsvprocessing.models;

/**
 * model to enable linking column names supplied by user and the relevant CSV
 */
public class CorrespondingCSV {
    private final String columnName;
    private final CSV csv;

    public CorrespondingCSV(String columnName, CSV csv) {
        this.columnName = columnName;
        this.csv = csv;
    }

    public String getColumnName() {
        return columnName;
    }

    public CSV getCsv() {
        return csv;
    }
}
