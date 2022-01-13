package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.Node;

import java.io.IOException;
import java.util.List;

public abstract class ProcessingOperationNode extends Node {
    protected ProcessingOperationNode(String id, String group, String operation) {
        super(id, group, operation);
    }

    public abstract CSV process(List<CSV> csvData) throws IOException;
}
