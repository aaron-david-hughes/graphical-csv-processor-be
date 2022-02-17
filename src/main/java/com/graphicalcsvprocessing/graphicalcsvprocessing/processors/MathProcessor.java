package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.utils.ProcessingUtils.createCSV;
import static com.graphicalcsvprocessing.graphicalcsvprocessing.utils.ProcessingUtils.listToString;

public class MathProcessor implements Processor {

    private MathProcessor() {}

    //consider empty/string values being used...
    public static CSV row(CSV input, StatisticalType mathOp, String newColName, String... columns) throws IOException {
        List<Integer> columnIndexes = Arrays.stream(columns).map(input.getHeaderMap()::get).collect(Collectors.toList());

        if (columnIndexes.isEmpty()) throw new IllegalArgumentException("No columns supplied to average");

        List<String> headers = input.getHeaders();
        headers.add(newColName);

        StringBuilder sb = new StringBuilder().append(listToString(headers)).append("\n");

        for (CSVRecord csvRecord : input.getRecords()) {
            List<String> values = new ArrayList<>(csvRecord.toList());

            DoubleSummaryStatistics stats = columnIndexes.stream()
                    .mapToDouble(columnIdx -> Double.parseDouble(values.get(columnIdx)))
                    .summaryStatistics();

            double rowResult = mathOp.statisticalOperation.apply(stats);

            values.add(String.valueOf(rowResult));

            sb.append(listToString(values)).append("\n");
        }

        return createCSV(sb);
    }

    public static CSV column(CSV input, StatisticalType  mathOp, String column) throws IOException {
        int columnIdx = input.getHeaderMap().get(column);

        DoubleSummaryStatistics stats = input.getRecords().stream()
                .mapToDouble(csvRecord -> Double.parseDouble(csvRecord.toList().get(columnIdx)))
                .summaryStatistics();

        double result = mathOp.statisticalOperation.apply(stats);

        StringBuilder sb = new StringBuilder().append(mathOp.toString()).append("\n").append(result).append("\n");

        return createCSV(sb);
    }

    @SuppressWarnings("unused")
    public enum StatisticalType {
        AVERAGE(DoubleSummaryStatistics::getAverage),
        COUNT(doubleSummaryStatistics -> Double.valueOf(doubleSummaryStatistics.getCount())),
        MAX(DoubleSummaryStatistics::getMax),
        MIN(DoubleSummaryStatistics::getMin),
        SUM(DoubleSummaryStatistics::getSum);

        StatisticalType(Function<DoubleSummaryStatistics, Double> statisticalOperation) {
            this.statisticalOperation = statisticalOperation;
        }

        Function<DoubleSummaryStatistics, Double> statisticalOperation;
    }
}
