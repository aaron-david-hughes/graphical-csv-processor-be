package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.binaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CorrespondingCSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.ConcatTablesProcessor;
import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.UniqueColumnProcessor;
import com.graphicalcsvprocessing.graphicalcsvprocessing.services.ColumnNameService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class OrProcessingNode extends BinaryOperationNode {

    protected String column;

    public OrProcessingNode(String id, String group, String operation, String column) {
        super(id, group, operation);
        this.column = column;
    }

    @Override
    public CSV process(List<CSV> csvData) throws IOException {
        super.process(csvData);

        CSV concatCSV = ConcatTablesProcessor.concat(csvData.get(0), csvData.get(1));

        CorrespondingCSV csv = ColumnNameService.deduceColumnName(column, Collections.singletonList(concatCSV));

        return UniqueColumnProcessor.uniqueColumn(csv.getCsv(), csv.getColumnName());
    }
}
