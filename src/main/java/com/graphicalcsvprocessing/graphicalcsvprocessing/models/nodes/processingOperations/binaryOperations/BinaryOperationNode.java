package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.binaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.ProcessingOperationNode;

public abstract class BinaryOperationNode extends ProcessingOperationNode implements BinaryOperation {

    protected BinaryOperationNode(String id, String group, String operation) {
        super(id, group, operation);
    }

    @Override
    public int getAllowedNumberEdges() {
        return NUMBER_INBOUND_EDGES;
    }
}
