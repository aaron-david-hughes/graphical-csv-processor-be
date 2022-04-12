package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CorrespondingCSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.UniqueColumnProcessor;
import com.graphicalcsvprocessing.graphicalcsvprocessing.services.ColumnNameService;

import java.io.IOException;
import java.util.List;

/**
 * model to represent unique column operation
 */
public class UniqueColumnProcessingNode extends UnaryOperationNode {

    protected String column;

    public UniqueColumnProcessingNode(String id, String group, String operation, String column) {
        super(id, group, operation);
        this.column = column;
    }

    @Override
    public CSV process(List<CSV> csvData) throws IOException {
        super.process(csvData);

        CorrespondingCSV csv = ColumnNameService.deduceColumnName(column, csvData);

        return UniqueColumnProcessor.uniqueColumn(csv.getCsv(), csv.getColumnName());
    }

    public String getColumn() {
        return column;
    }
}
