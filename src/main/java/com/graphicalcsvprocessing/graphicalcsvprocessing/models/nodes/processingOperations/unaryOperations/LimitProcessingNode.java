package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.LimitProcessor;

import java.io.IOException;
import java.util.List;

/**
 * model to represent limit number of records operations
 */
public class LimitProcessingNode extends UnaryOperationNode {

    protected int limit;

    public LimitProcessingNode(String id, String group, String operation, int limit) {
        super(id, group, operation);
        this.limit = limit;
    }

    @Override
    public CSV process(List<CSV> csvData) throws IOException {
        super.process(csvData);

        if (limit < 0) {
            throw new IllegalArgumentException("Limit may not be negative");
        }

        return LimitProcessor.limit(csvData.get(0), limit);
    }
}
