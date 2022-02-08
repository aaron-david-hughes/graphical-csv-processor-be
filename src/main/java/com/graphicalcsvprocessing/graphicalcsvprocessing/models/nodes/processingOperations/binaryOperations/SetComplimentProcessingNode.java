package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.binaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CorrespondingCSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.SetProcessor;
import com.graphicalcsvprocessing.graphicalcsvprocessing.services.ColumnNameService;

import java.io.IOException;
import java.util.List;

public class SetComplimentProcessingNode extends BinaryOperationNode {

    protected String setColumn;
    protected String subsetColumn;

    public SetComplimentProcessingNode(String id, String group, String operation, String setColumn, String subsetColumn) {
        super(id, group, operation);
        this.setColumn = setColumn;
        this.subsetColumn = subsetColumn;
    }

    @Override
    public CSV process(List<CSV> csvData) throws IOException {
        super.process(csvData);

        CorrespondingCSV csv = ColumnNameService.deduceColumnName(setColumn, csvData);
        CorrespondingCSV subsetCsv = ColumnNameService.deduceColumnName(subsetColumn, csvData);

        return SetProcessor.getCompliment(csv.getCsv(), subsetCsv.getCsv(), csv.getColumnName(), subsetCsv.getColumnName());
    }
}
