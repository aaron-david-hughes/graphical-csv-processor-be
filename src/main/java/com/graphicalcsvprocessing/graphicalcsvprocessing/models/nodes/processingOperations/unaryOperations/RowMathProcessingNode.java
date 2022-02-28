package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.MathProcessor;
import com.graphicalcsvprocessing.graphicalcsvprocessing.services.ColumnNameService;

import java.io.IOException;
import java.util.List;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.processors.MathProcessor.StatisticalType;

public class RowMathProcessingNode extends UnaryOperationNode {

    protected String[] columns;
    protected String newName;
    protected StatisticalType mathOp;

    public RowMathProcessingNode(String id, String group, String operation, String[] columns, String newName, StatisticalType mathOp) {
        super(id, group, operation);
        this.columns = columns;
        this.newName = newName;
        this.mathOp = mathOp;
    }

    @Override
    public CSV process(List<CSV> csvData) throws IOException {
        super.process(csvData);

        if (columns.length == 0) throw new IllegalArgumentException("Columns list must not be empty");

        for (String s : columns) {
            ColumnNameService.deduceColumnName(s, csvData);
        }

        return MathProcessor.row(csvData.get(0), mathOp, ColumnNameService.validateColumnName(newName), columns);
    }
}