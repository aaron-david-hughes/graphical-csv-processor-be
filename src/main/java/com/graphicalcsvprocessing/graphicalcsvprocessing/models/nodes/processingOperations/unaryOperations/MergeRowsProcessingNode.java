package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.MergeRowsProcessor;

import java.io.IOException;
import java.util.List;

public class MergeRowsProcessingNode extends UnaryOperationNode {

    protected String column;
    protected String value;

    public MergeRowsProcessingNode(String id, String group, String operation, String column, String value) {
        super(id, group, operation);
        this.column = column;
        this.value = value;
    }

    @Override
    public CSV process(List<CSV> csvData) throws IOException {
        super.process(csvData);

        return MergeRowsProcessor.merge(csvData.get(0), column, value);
    }

    public String getColumn() {
        return column;
    }

    public String getValue() {
        return value;
    }
}
