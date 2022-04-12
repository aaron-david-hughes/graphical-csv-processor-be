package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.binaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CorrespondingCSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.SetProcessor;
import com.graphicalcsvprocessing.graphicalcsvprocessing.services.ColumnNameService;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * model to represent a set compliment operation
 */
public class SetComplementProcessingNode extends BinaryOperationNode {

    protected String keyHeader;

    public SetComplementProcessingNode(String id, String group, String operation, String keyHeader) {
        super(id, group, operation);
        this.keyHeader = keyHeader;
    }

    @Override
    public CSV process(List<CSV> csvData) throws IOException {
        super.process(csvData);

        csvData.sort(Comparator.comparingInt(csv -> csv.getRecords().size()));

        CorrespondingCSV csv = ColumnNameService.deduceColumnName(keyHeader, Collections.singletonList(csvData.get(1)));

        return SetProcessor.getComplement(csvData.get(1), csvData.get(0), csv.getColumnName());
    }
}
