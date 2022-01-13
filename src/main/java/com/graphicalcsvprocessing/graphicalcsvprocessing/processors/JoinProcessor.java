package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.binaryOperations.JoinProcessingNode;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.util.*;
import java.util.function.UnaryOperator;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.utils.ProcessingUtils.createCSV;
import static com.graphicalcsvprocessing.graphicalcsvprocessing.utils.ProcessingUtils.listToString;

/**
 * join logic:
 *      left join (default):
 *          will go through each record in left entered data, including a record for each time the relevant
 *          right column matches - or simply itself with null on right columns if no matches found
 *
 *      right join:
 *          will go through each record in right entered data, including a record for each time the relevant
 *          left column matches - or simply itself with null on left columns if no matches found
 *
 *      inner join:
 *          will go through each record in left entered data, including a record for each time the relevant
 *          right column matches - omitting the record if no matches found
 *
 *      outer join:
 *          will go through each record in left entered data, including a record for each time the relevant
 *          right column matches - but include both data sets records which do not have a match additionally
 */
public class JoinProcessor implements Processor {

    public static CSV join(JoinProcessingNode node, CSV[] inputs) throws IOException {
        CSV left = inputs[0];
        CSV right = inputs[1];

        switch (node.getJoinType()) {
            case RIGHT:
                return rightJoin(left, right, node.getLeftCol(), node.getRightCol());
            case INNER:
                return innerJoin(left, right, node.getLeftCol(), node.getRightCol());
            case OUTER:
                return outerJoin(left, right, node.getLeftCol(), node.getRightCol());
            case LEFT:
            default:
                return leftJoin(left, right, node.getLeftCol(), node.getRightCol());
        }
    }

    public static CSV leftJoin(CSV left, CSV right, String onLeft, String onRight) throws IOException {
        return join(JoinType.LEFT, left, right, onLeft, onRight);
    }

    public static CSV rightJoin(CSV left, CSV right, String onLeft, String onRight) throws IOException {
        return join(JoinType.RIGHT, right, left, onRight, onLeft);
    }

    public static CSV innerJoin(CSV left, CSV right, String onLeft, String onRight) throws IOException {
        return join(JoinType.INNER, left, right, onLeft, onRight);
    }

    public static CSV outerJoin(CSV left, CSV right, String onLeft, String onRight) throws IOException {
        return join(JoinType.OUTER, left, right, onLeft, onRight);
    }

    /**
     * Method is O(MN) where M and N are the lengths of the two record sets
     *
     * Will have to consider what happens when column names clash in csv records...
     *      - on join both of them
     *      - on join is neither of them
     *      - on join is one but not the other
     *
     * This has been accepted by adding Filename prefixes to column names
     * SQL return data in the same way on a simple join
     * If desire is to filter or rename columns etc, those are additional functions...
     */
    private static CSV join(JoinType joinType, CSV superior, CSV inferior, String onSuperior, String onInferior) throws IOException {
        StringBuilder result = new StringBuilder(listToString(superior.getHeaders()) + "," + listToString(inferior.getHeaders()) + "\n");

        boolean includeNonMatchStrategy = joinType.includeNonMatches.apply(true);
        Set<CSVRecord> inferiorMatches = new HashSet<>();

        for (CSVRecord superiorRow : superior.getRecords()) {
            List<String> matches = new ArrayList<>();

            for (CSVRecord inferiorRow : inferior.getRecords()) {
                if (superiorRow.get(onSuperior).equals(inferiorRow.get(onInferior))) {
                    matches.add(listToString(superiorRow.toList()) + "," + listToString(inferiorRow.toList()) + "\n");
                    inferiorMatches.add(inferiorRow);
                }
            }

            if (matches.isEmpty() && includeNonMatchStrategy) {
                matches.add(listToString(superiorRow.toList()) + ",".repeat(inferior.getHeaders().size()) + "\n");
            }

            matches.forEach(result::append);
        }

        if (joinType == JoinType.OUTER) {
            List<CSVRecord> missingRecords = inferior.getRecords();
            missingRecords.removeAll(inferiorMatches);

            for (CSVRecord row : missingRecords) {
                result.append(",".repeat(superior.getHeaders().size()))
                        .append(listToString(row.toList()))
                        .append("\n");
            }
        }

        return createCSV(result);
    }

    public enum JoinType {
        LEFT(isSuperior -> isSuperior),
        RIGHT(isSuperior -> isSuperior),
        INNER(isSuperior -> false),
        OUTER(isSuperior -> true);

        JoinType(UnaryOperator<Boolean> includeNonMatches) {
            this.includeNonMatches = includeNonMatches;
        }

        private final UnaryOperator<Boolean> includeNonMatches;
    }
}
