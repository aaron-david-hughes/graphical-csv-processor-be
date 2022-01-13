package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;

import java.util.List;

public class FilterProcessingNode extends UnaryOperationNode {
    protected String condition;

    public FilterProcessingNode(String id, String group, String operation, String condition) {
        super(id, group, operation);
        this.condition = condition;
    }

    @Override
    public CSV process(List<CSV> csvData) {
        return null;
    }
}
