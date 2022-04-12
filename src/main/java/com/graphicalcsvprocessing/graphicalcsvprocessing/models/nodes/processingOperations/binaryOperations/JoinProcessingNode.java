package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.binaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CorrespondingCSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.JoinProcessor;

import java.io.IOException;
import java.util.List;

import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.JoinProcessor.JoinType;
import com.graphicalcsvprocessing.graphicalcsvprocessing.services.ColumnNameService;

/**
 * model to represent a join operation
 */
public class JoinProcessingNode extends BinaryOperationNode {
    protected String leftCol;
    protected String rightCol;
    protected JoinType joinType;

    public JoinProcessingNode(String id, String group, String operation, String leftCol, String rightCol, JoinType joinType) {
        super(id, group, operation);
        this.leftCol = leftCol;
        this.rightCol = rightCol;
        this.joinType = joinType;
    }

    @Override
    public CSV process(List<CSV> csvData) throws IOException {
        super.process(csvData);

        CorrespondingCSV left = ColumnNameService.deduceColumnName(leftCol, csvData);
        CorrespondingCSV right = ColumnNameService.deduceColumnName(rightCol, csvData);

        return JoinProcessor.join(left, right, joinType);
    }
}
