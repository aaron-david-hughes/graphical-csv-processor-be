package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.ProcessingOperationNode;

public abstract class UnaryOperationNode extends ProcessingOperationNode implements UnaryOperation {

    protected UnaryOperationNode(String id, String group, String operation) {
        super(id, group, operation);
    }

    @Override
    public int getAllowedNumberEdges() {
        return NUMBER_INBOUND_EDGES;
    }
}
