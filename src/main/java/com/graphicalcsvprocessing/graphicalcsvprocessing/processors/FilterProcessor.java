package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations.FilterProcessingNode;
import org.apache.commons.csv.CSVRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.utils.ProcessingUtils.parseDouble;

public class FilterProcessor implements Processor {

    private FilterProcessor() {}

    private static final String NUMERIC_FILTER_ON_NO_NUMERIC = "Cannot carry out numeric filter on cell without number";

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
            try {
                double cDbl = parseDouble(c);
                double vDbl = parseDouble(v);

                return cDbl == vDbl;
            } catch (Exception e) {
                throw new IllegalArgumentException(NUMERIC_FILTER_ON_NO_NUMERIC, e);
            }
        }),
        GREATER_THAN((c, v) -> {
            try {
                double cDbl = parseDouble(c);
                double vDbl = parseDouble(v);

                return cDbl > vDbl;
            } catch (Exception e) {
                throw new IllegalArgumentException(NUMERIC_FILTER_ON_NO_NUMERIC, e);
            }
        }),
        GREATER_THAN_OR_EQUAL((c, v) -> {
            try {
                double cDbl = parseDouble(c);
                double vDbl = parseDouble(v);

                return cDbl >= vDbl;
            } catch (Exception e) {
                throw new IllegalArgumentException(NUMERIC_FILTER_ON_NO_NUMERIC, e);
            }
        }),
        LESS_THAN((c, v) -> {
            try {
                double cDbl = parseDouble(c);
                double vDbl = parseDouble(v);

                return cDbl < vDbl;
            } catch (Exception e) {
                throw new IllegalArgumentException(NUMERIC_FILTER_ON_NO_NUMERIC, e);
            }
        }),
        LESS_THAN_OR_EQUAL((c, v) -> {
            try {
                double cDbl = parseDouble(c);
                double vDbl = parseDouble(v);

                return cDbl <= vDbl;
            } catch (Exception e) {
                throw new IllegalArgumentException(NUMERIC_FILTER_ON_NO_NUMERIC, e);
            }
        });

        FilterType(BiPredicate<String, String> predicate) {
            this.predicate = predicate;
        }

        private final BiPredicate<String, String> predicate;
    }
}
