package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CorrespondingCSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.util.*;
import java.util.function.BinaryOperator;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.utils.ProcessingUtils.createCSV;
import static com.graphicalcsvprocessing.graphicalcsvprocessing.utils.ProcessingUtils.listToString;

/**
 * processor to join two input files on specified column equality in O(mn) time where
 * m is the number of records in the first input and n is the number of records in the second input
 *
 * conscious effort to implement a hash join means the performance improves up to linear time values are unique across the column
 */
public class JoinProcessor implements Processor {

    public static CSV join(CorrespondingCSV left, CorrespondingCSV right, JoinType orderedJoinType) throws IOException {
        CorrespondingCSV superiorCorrespondingCSV = orderedJoinType.getSuperiorCSV.apply(left, right);
        CorrespondingCSV inferiorCorrespondingCSV = orderedJoinType.getInferiorCSV.apply(left, right);

        CSV superior = superiorCorrespondingCSV.getCsv();
        String superiorColumn = superiorCorrespondingCSV.getColumnName();

        CSV inferior = inferiorCorrespondingCSV.getCsv();
        String inferiorColumn = inferiorCorrespondingCSV.getColumnName();

        int superiorIdx = superior.getHeaderMap().get(superiorColumn);
        int inferiorIdx = inferior.getHeaderMap().get(inferiorColumn);

        List<String> superiorHeaders = superior.getHeaders();
        List<String> inferiorHeaders = inferior.getHeaders();

        List<CSVRecord> superiorRecords = superior.getRecords();
        List<CSVRecord> inferiorRecords = inferior.getRecords();

        StringBuilder sb = new StringBuilder(listToString(superiorHeaders) + "," + listToString(inferiorHeaders) + "\n");

        Map<String, List<Integer>> superiorValueRanges = getValueRanges(superior, superiorIdx);
        Map<String, List<Integer>> inferiorValueRanges = getValueRanges(inferior, inferiorIdx);

        for (Map.Entry<String, List<Integer>> entry : superiorValueRanges.entrySet()) {
            for (int i : entry.getValue()) {
                if (!inferiorValueRanges.containsKey(entry.getKey())) {
                    if (orderedJoinType.includeSuperiorNonMatch) {
                        sb.append(listToString(superiorRecords.get(i).toList()))
                                .append(",".repeat(inferiorHeaders.size()))
                                .append("\n");
                    }

                    continue;
                }

                for (int j : inferiorValueRanges.get(entry.getKey())) {
                    sb.append(listToString(superiorRecords.get(i).toList()))
                            .append(",")
                            .append(listToString(inferiorRecords.get(j).toList()))
                            .append("\n");
                }
            }

            inferiorValueRanges.remove(entry.getKey());
        }

        if (orderedJoinType.includeInferiorNonMatch) {
            sb.append(inferiorNonMatches(superiorHeaders, inferiorRecords, inferiorValueRanges));
        }

        return createCSV(sb);
    }

    private static Map<String, List<Integer>> getValueRanges(CSV csv, int columnIdx) {
        Map<String, List<Integer>> valuesRanges = new LinkedHashMap<>();
        List<CSVRecord> records = csv.getRecords();

        for (int i = 0; i < records.size(); i++) {
            String value = records.get(i).get(columnIdx);

            if (valuesRanges.containsKey(value)) valuesRanges.get(value).add(i);
            else valuesRanges.put(value, Collections.singletonList(i));
        }

        return valuesRanges;
    }

    private static StringBuilder inferiorNonMatches(List<String> superiorHeaders, List<CSVRecord> inferiorRecords, Map<String, List<Integer>> inferiorValueRanges) {
        StringBuilder inferiorRecordSb = new StringBuilder();
        for (Map.Entry<String, List<Integer>> entry : inferiorValueRanges.entrySet()) {
            for (int i : entry.getValue()) {
                inferiorRecordSb.append(",".repeat(superiorHeaders.size()))
                        .append(listToString(inferiorRecords.get(i).toList()))
                        .append("\n");
            }
        }

        return inferiorRecordSb;
    }

    public enum JoinType {
        LEFT(
                (left, right) -> left,
                (left, right) -> right,
                true,
                false
        ),
        RIGHT(
                (left, right) -> right,
                (left, right) -> left,
                true,
                false
        ),
        INNER(
                (left, right) -> left,
                (left, right) -> right,
                false,
                false
        ),
        OUTER(
                (left, right) -> left,
                (left, right) -> right,
                true,
                true
        );

        JoinType(
                BinaryOperator<CorrespondingCSV> getSuperiorCSV,
                BinaryOperator<CorrespondingCSV> getInferiorCSV,
                boolean includeSuperiorNonMatch,
                boolean includeInferiorNonMatch
        ) {
            this.getSuperiorCSV = getSuperiorCSV;
            this.getInferiorCSV = getInferiorCSV;
            this.includeSuperiorNonMatch = includeSuperiorNonMatch;
            this.includeInferiorNonMatch = includeInferiorNonMatch;
        }

        BinaryOperator<CorrespondingCSV> getSuperiorCSV;
        BinaryOperator<CorrespondingCSV> getInferiorCSV;

        boolean includeSuperiorNonMatch;
        boolean includeInferiorNonMatch;
    }
}
