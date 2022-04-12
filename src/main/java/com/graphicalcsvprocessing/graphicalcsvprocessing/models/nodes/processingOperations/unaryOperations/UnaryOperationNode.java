package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.ProcessingOperationNode;

/**
 * model to represent unary node operations, that is those nodes which accept a single file input
 */
public abstract class UnaryOperationNode extends ProcessingOperationNode implements UnaryOperation {

    protected UnaryOperationNode(String id, String group, String operation) {
        super(id, group, operation);
    }

    @Override
    public int getAllowedNumberEdges() {
        return NUMBER_INBOUND_EDGES;
    }
}
