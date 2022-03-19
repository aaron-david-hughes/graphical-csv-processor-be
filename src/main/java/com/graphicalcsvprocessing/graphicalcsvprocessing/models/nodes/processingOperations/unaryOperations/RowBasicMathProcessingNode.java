package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CorrespondingCSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.BasicMathProcessor;
import com.graphicalcsvprocessing.graphicalcsvprocessing.services.ColumnNameService;

import java.io.IOException;
import java.util.List;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.processors.BasicMathProcessor.MathOperation;

public class RowBasicMathProcessingNode extends UnaryOperationNode {

    protected String column1;
    protected String value;
    protected String newName;
    protected MathOperation mathOperation;
    protected boolean literal;

    public RowBasicMathProcessingNode(String id, String group, String operation, String column1, String value, String newName, MathOperation mathOperation, boolean literal) {
        super(id, group, operation);
        this.column1 = column1;
        this.value = value;
        this.newName = newName;
        this.mathOperation = mathOperation;
        this.literal = literal;
    }

    @Override
    public CSV process(List<CSV> csvData) throws IOException {
        super.process(csvData);

        CorrespondingCSV csv = ColumnNameService.deduceColumnName(column1, csvData);

        if (literal) {
            return BasicMathProcessor.processLiteral(csv.getCsv(), mathOperation, csv.getColumnName(), value, ColumnNameService.validateColumnName(newName));
        }

        String deducedCol1 = csv.getColumnName();
        csv = ColumnNameService.deduceColumnName(value, csvData);

        return BasicMathProcessor.process(csv.getCsv(), mathOperation, deducedCol1, csv.getColumnName(), ColumnNameService.validateColumnName(newName));
    }
}
