package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CorrespondingCSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.MergeColumnsProcessor;
import com.graphicalcsvprocessing.graphicalcsvprocessing.services.ColumnNameService;

import java.io.IOException;
import java.util.List;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.processors.MergeColumnsProcessor.MergeType;

/**
 * model to represent merge columns operation
 */
public class MergeColumnsProcessingNode extends UnaryOperationNode {

    protected String column1;
    protected String column2;
    protected String mergeColName;
    protected MergeType mergeType;

    public MergeColumnsProcessingNode(String id, String group, String operation, String column1, String column2, String mergeColName, MergeType mergeType) {
        super(id, group, operation);
        this.column1 = column1;
        this.column2 = column2;
        this.mergeColName = mergeColName;
        this.mergeType = mergeType;
    }

    @Override
    public CSV process(List<CSV> csvData) throws IOException {
        super.process(csvData);

        CorrespondingCSV csv = ColumnNameService.deduceColumnName(column1, csvData);
        this.column1 = csv.getColumnName();

        csv = ColumnNameService.deduceColumnName(column2, csvData);
        this.column2 = csv.getColumnName();

        ColumnNameService.validateColumnName(mergeColName);

        return MergeColumnsProcessor.merge(csvData.get(0), this);
    }

    public String getColumn1() {
        return column1;
    }

    public String getColumn2() {
        return column2;
    }

    public String getMergeColName() {
        return mergeColName;
    }

    public MergeType getMergeType() {
        return mergeType;
    }
}
