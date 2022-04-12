package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations.MergeColumnsProcessingNode;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.utils.ProcessingUtils.*;

/**
 * processor to merge two columns of an input file together provided no data clashes occur in linear time
 */
public class MergeColumnsProcessor implements Processor {

    public static CSV merge(CSV input, MergeColumnsProcessingNode node) throws IOException {
        int col1Idx = input.getHeaderMap().get(node.getColumn1());
        int col2Idx = input.getHeaderMap().get(node.getColumn2());

        if (col1Idx > col2Idx) {
            int temp = col1Idx;
            col1Idx = col2Idx;
            col2Idx = temp;
        }

        List<String> headers = input.getHeaders();

        headers.remove(col2Idx);
        headers.set(col1Idx, node.getMergeColName());

        StringBuilder sb = new StringBuilder().append(listToString(headers)).append("\n");

        for (CSVRecord csvRecord : input.getRecords()) {
            List<String> cells = new ArrayList<>(csvRecord.toList());

            String col1Value = cells.get(col1Idx);
            String col2Value = cells.get(col2Idx);

            if (!node.getMergeType().predicate.test(col1Value, col2Value)) {
                throw new IllegalArgumentException("Merge cannot be carried out as data in columns clash.");
            }

            if (!nullBlankOrEmpty(col2Value) && nullBlankOrEmpty(col1Value)) {
                cells.set(col1Idx, cells.get(col2Idx));
            }

            cells.remove(col2Idx);
            sb.append(listToString(cells)).append("\n");
        }

        return createCSV(sb);
    }

    private static boolean nullBlankOrEmpty(String v) {
        return v == null || v.isEmpty() || v.isBlank();
    }

    public enum MergeType {
        STRING_EQUALITY((v1, v2) -> {
            if (nullBlankOrEmpty(v1) || nullBlankOrEmpty(v2)) {
                return true;
            }

            return v1.equals(v2);
        }),
        NUMERIC_EQUALITY((v1, v2) -> {
            if (nullBlankOrEmpty(v1) || nullBlankOrEmpty(v2)) {
                return true;
            }

            double v1Dbl = parseDouble(v1);
            double v2Dbl = parseDouble(v2);

            return v1Dbl == v2Dbl;
        });

        MergeType(BiPredicate<String, String> predicate) {
            this.predicate = predicate;
        }

        BiPredicate<String, String> predicate;
    }
}
