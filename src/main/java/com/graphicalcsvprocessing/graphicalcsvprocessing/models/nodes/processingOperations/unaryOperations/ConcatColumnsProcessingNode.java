package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CorrespondingCSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.ConcatColumnsProcessor;
import com.graphicalcsvprocessing.graphicalcsvprocessing.services.ColumnNameService;

import java.io.IOException;
import java.util.List;

/**
 * model to represent concatenation of columns operation
 */
public class ConcatColumnsProcessingNode extends UnaryOperationNode {

    protected String column1;
    protected String column2;
    protected String concatHeader;

    public ConcatColumnsProcessingNode(String id, String group, String operation, String column1, String column2, String concatHeader) {
        super(id, group, operation);
        this.column1 = column1;
        this.column2 = column2;
        this.concatHeader = concatHeader;
    }

    @Override
    public CSV process(List<CSV> csvData) throws IOException {
        super.process(csvData);

        CorrespondingCSV csv1 = ColumnNameService.deduceColumnName(column1, csvData);
        CorrespondingCSV csv2 = ColumnNameService.deduceColumnName(column2, csvData);
        String validatedHeader = ColumnNameService.validateColumnName(concatHeader);

        return ConcatColumnsProcessor.concat(csv1.getCsv(), csv1.getColumnName(), csv2.getColumnName(), validatedHeader);
    }
}
