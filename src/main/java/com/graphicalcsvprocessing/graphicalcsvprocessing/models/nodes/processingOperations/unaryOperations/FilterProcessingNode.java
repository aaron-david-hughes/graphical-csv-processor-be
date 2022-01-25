package com.graphicalcsvprocessing.graphicalcsvprocessing.models.nodes.processingOperations.unaryOperations;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CorrespondingCSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.processors.FilterProcessor;
import com.graphicalcsvprocessing.graphicalcsvprocessing.services.ColumnNameService;

import java.io.IOException;
import java.util.List;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.processors.FilterProcessor.FilterType;

public class FilterProcessingNode extends UnaryOperationNode {
    protected String column;
    protected String condition;
    protected FilterType filterType;
    protected boolean equal;

    public FilterProcessingNode(String id, String group, String operation, String column, String condition, FilterType filterType, boolean equal) {
        super(id, group, operation);
        this.column = column;
        this.condition = condition;
        this.filterType = filterType;
        this.equal = equal;
    }

    @Override
    public CSV process(List<CSV> csvData) throws IOException {
        super.process(csvData);

        CorrespondingCSV csv = ColumnNameService.deduceColumnName(column, csvData);

        return FilterProcessor.filter(csv.getCsv(), csv.getColumnName(), this);
    }

    public String getColumn() {
        return column;
    }

    public String getCondition() {
        return condition;
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public boolean isEqual() {
        return equal;
    }
}
