package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.ProcessingOperationNode;

import java.io.IOException;
import java.util.List;

public abstract class UnaryOperationNode extends ProcessingOperationNode implements UnaryOperation {

    protected UnaryOperationNode(String id, String group, String operation) {
        super(id, group, operation);
    }

    @Override
    public int getAllowedNumberEdges() {
        return NUMBER_INBOUND_EDGES;
    }

    @Override
    public CSV process(List<CSV> csvData) throws IOException {
        if (this.getAllowedNumberEdges() != csvData.size()) {
            throw new IllegalArgumentException("Unary node '" + this.getId() + "' with incorrect number of inputs, expected '" +
                    this.getAllowedNumberEdges() + "', but received '" + csvData.size() + "'");
        }

        return null;
    }
}
