package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import org.apache.commons.csv.CSVRecord;

import java.util.ArrayList;
import java.util.List;

public class FilterProcessor implements Processor {

    private FilterProcessor() {}

    public static CSV filter(CSV operand, String column, String value, boolean equal) {
        return equalityFilter(operand, column, value, equal);
    }

    private static CSV equalityFilter(CSV operand, String column, String value, boolean equal) {
        List<CSVRecord> matches = new ArrayList<>();
        List<CSVRecord> notMatches = new ArrayList<>();

        for (CSVRecord csvRecord : operand.getRecords()) {
            if (csvRecord.get(column).equals(value)) {
                matches.add(csvRecord);
            } else {
                notMatches.add(csvRecord);
            }
        }

        return new CSV(operand.getHeaders(), equal ? matches : notMatches);
    }
}
