package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations.FilterProcessingNode;
import org.apache.commons.csv.CSVRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public class FilterProcessor implements Processor {

    private FilterProcessor() {}

    public static CSV filter(CSV operand, String column, FilterProcessingNode node) {
        List<CSVRecord> matches = new ArrayList<>();
        List<CSVRecord> notMatches = new ArrayList<>();

        int colIdx = operand.getHeaderMap().get(column);

        for (CSVRecord csvRecord : operand.getRecords()) {
            if (node.getFilterType().predicate.test(csvRecord.get(colIdx), node.getCondition())) {
                matches.add(csvRecord);
            } else {
                notMatches.add(csvRecord);
            }
        }

        return new CSV(operand.getHeaders(), operand.getHeaderMap(), node.isEqual() ? matches : notMatches);
    }

    public enum FilterType {
        STRING_EQUALITY(String::equals),
        NUMERIC_EQUALITY((c, v) -> {
            double cDbl = Double.parseDouble(c);
            double vDbl = Double.parseDouble(v);

            return cDbl == vDbl;
        }),
        GREATER_THAN((c, v) -> {
            double cDbl = Double.parseDouble(c);
            double vDbl = Double.parseDouble(v);

            return cDbl > vDbl;
        }),
        GREATER_THAN_OR_EQUAL((c, v) -> {
            double cDbl = Double.parseDouble(c);
            double vDbl = Double.parseDouble(v);

            return cDbl >= vDbl;
        }),
        LESS_THAN((c, v) -> {
            double cDbl = Double.parseDouble(c);
            double vDbl = Double.parseDouble(v);

            return cDbl < vDbl;
        }),
        LESS_THAN_OR_EQUAL((c, v) -> {
            double cDbl = Double.parseDouble(c);
            double vDbl = Double.parseDouble(v);

            return cDbl <= vDbl;
        });

        FilterType(BiPredicate<String, String> predicate) {
            this.predicate = predicate;
        }

        private final BiPredicate<String, String> predicate;
    }
}
