package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.binaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.ConcatTablesProcessor;

import java.io.IOException;
import java.util.List;

public class ConcatTablesProcessingNode extends BinaryOperationNode {

    public ConcatTablesProcessingNode(String id, String group, String operation) {
        super(id, group, operation);
    }

    @Override
    public CSV process(List<CSV> csvData) throws IOException {
        super.process(csvData);

        return ConcatTablesProcessor.concat(csvData.get(0), csvData.get(1));
    }
}
