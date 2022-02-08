package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CorrespondingCSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.OrderColumnProcessor;
import com.graphicalcsvprocessing.graphicalcsvprocessing.services.ColumnNameService;

import java.io.IOException;
import java.util.List;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.processors.OrderColumnProcessor.*;

public class OrderColumnProcessingNode extends UnaryOperationNode {

    protected String column;
    protected OrderType orderType;

    public OrderColumnProcessingNode(String id, String group, String operation, String column, OrderType orderType) {
        super(id, group, operation);
        this.column = column;
        this.orderType = orderType;
    }

    @Override
    public CSV process(List<CSV> csvData) throws IOException {
        super.process(csvData);

        CorrespondingCSV csv = ColumnNameService.deduceColumnName(column, csvData);

        return OrderColumnProcessor.order(csv.getCsv(), csv.getColumnName(), orderType);
    }
}
