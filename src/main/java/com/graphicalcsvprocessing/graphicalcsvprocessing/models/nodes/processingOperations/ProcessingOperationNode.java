package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.Node;

import java.io.IOException;
import java.util.List;

/**
 * model enabling the identification of the of processing node operations in the query
 */
public abstract class ProcessingOperationNode extends Node {
    protected ProcessingOperationNode(String id, String group, String operation) {
        super(id, group, operation);
    }

    public CSV process(List<CSV> csvData) throws IOException {
        if (this.getAllowedNumberEdges() != csvData.size()) {
            throw new IllegalArgumentException("Node '" + this.getId() + "' with incorrect number of inputs, expected '" +
                    this.getAllowedNumberEdges() + "', but received '" + csvData.size() + "'");
        }

        return null;
    }
}
