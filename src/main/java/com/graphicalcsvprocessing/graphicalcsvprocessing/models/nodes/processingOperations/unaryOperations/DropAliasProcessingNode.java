package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.AliasProcessor;

import java.io.IOException;
import java.util.List;

/**
 * model to represent drop alias operation
 */
public class DropAliasProcessingNode extends UnaryOperationNode {

    public DropAliasProcessingNode(String id, String group, String operation) {
        super(id, group, operation);
    }

    @Override
    public CSV process(List<CSV> csvData) throws IOException {
        super.process(csvData);

        return AliasProcessor.dropAlias(csvData.get(0));
    }
}
