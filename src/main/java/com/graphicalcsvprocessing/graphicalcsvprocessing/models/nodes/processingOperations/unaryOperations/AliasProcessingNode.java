package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.AliasProcessor;

import java.io.IOException;
import java.util.List;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.services.ColumnNameService.validateAlias;

public class AliasProcessingNode extends UnaryOperationNode {

    protected String alias;

    public AliasProcessingNode(String id, String group, String operation, String alias) {
        super(id, group, operation);
        this.alias = alias;
    }

    @Override
    public CSV process(List<CSV> csvData) throws IOException {
        super.process(csvData);

        return AliasProcessor.alias(csvData.get(0), validateAlias(alias));
    }
}
