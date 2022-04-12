package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CorrespondingCSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.ColumnProcessor;
import com.graphicalcsvprocessing.graphicalcsvprocessing.services.ColumnNameService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * model to represent drop columns operation
 */
public class DropColumnProcessingNode extends UnaryOperationNode {
    protected String[] columns;

    public DropColumnProcessingNode(String id, String group, String operation, String[] columns) {
        super(id, group, operation);
        this.columns = columns;
    }

    @Override
    public CSV process(List<CSV> csvData) throws IOException {
        super.process(csvData);

        List<String> columnList = new ArrayList<>();
        CorrespondingCSV csv;

        for (String column : columns) {
            csv = ColumnNameService.deduceColumnName(column, csvData);
            columnList.add(csv.getColumnName());
        }

        return ColumnProcessor.dropColumns(csvData.get(0), columnList);
    }
}
