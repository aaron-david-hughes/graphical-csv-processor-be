package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CorrespondingCSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.MathProcessor;
import com.graphicalcsvprocessing.graphicalcsvprocessing.services.ColumnNameService;

import java.io.IOException;
import java.util.List;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.processors.MathProcessor.StatisticalType;

public class ColumnMathProcessingNode extends UnaryOperationNode {

    protected String column;
    protected StatisticalType mathOp;

    public ColumnMathProcessingNode(String id, String group, String operation, String column, StatisticalType mathOp) {
        super(id, group, operation);
        this.column = column;
        this.mathOp = mathOp;
    }

    @Override
    public CSV process(List<CSV> csvData) throws IOException {
        super.process(csvData);

        CorrespondingCSV csv = ColumnNameService.deduceColumnName(column, csvData);

        return MathProcessor.column(csv.getCsv(), mathOp, csv.getColumnName());
    }
}
