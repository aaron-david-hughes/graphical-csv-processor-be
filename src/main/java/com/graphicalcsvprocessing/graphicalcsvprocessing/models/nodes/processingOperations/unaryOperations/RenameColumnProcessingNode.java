package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CorrespondingCSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.RenameColumnProcessor;
import com.graphicalcsvprocessing.graphicalcsvprocessing.services.ColumnNameService;

import java.io.IOException;
import java.util.List;

public class RenameColumnProcessingNode extends UnaryOperationNode {

    protected String column;
    protected String newName;

    public RenameColumnProcessingNode(String id, String group, String operation, String column, String newName) {
        super(id, group, operation);
        this.column = column;
        this.newName = newName;
    }

    @Override
    public CSV process(List<CSV> csvData) throws IOException {
        super.process(csvData);

        CorrespondingCSV csv = ColumnNameService.deduceColumnName(column, csvData);

        return RenameColumnProcessor.renameColumn(
                csv.getCsv(), csv.getColumnName(), ColumnNameService.validateColumnName(newName)
        );
    }
}
