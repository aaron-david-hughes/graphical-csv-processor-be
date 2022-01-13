package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.binaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CorrespondingCSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.JoinProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.JoinProcessor.JoinType;
import com.graphicalcsvprocessing.graphicalcsvprocessing.services.ColumnNameService;

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
        if (this.getAllowedNumberEdges() != csvData.size()) {
            throw new IllegalArgumentException("Join node with incorrect number of inputs, expected '" +
                this.getAllowedNumberEdges() + "', but received '" + csvData.size() + "'");
        }

        return JoinProcessor.join(this, orderData(csvData));
    }

    protected CSV[] orderData(List<CSV> csvData) {
        List<CSV> csvList = new ArrayList<>();

        CorrespondingCSV csv = ColumnNameService.deduceColumnName(leftCol, csvData);
        csvList.add(csv.getCsv());
        this.leftCol = csv.getColumnName();

        csv = ColumnNameService.deduceColumnName(rightCol, csvData);
        csvList.add(csv.getCsv());
        this.rightCol = csv.getColumnName();

        return csvList.toArray(new CSV[0]);
    }

    public String getLeftCol() {
        return leftCol;
    }

    public String getRightCol() {
        return rightCol;
    }

    public JoinType getJoinType() {
        return joinType;
    }
}
