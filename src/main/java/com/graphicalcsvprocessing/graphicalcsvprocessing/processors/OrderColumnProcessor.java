package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import org.apache.commons.csv.CSVRecord;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

public class OrderColumnProcessor implements Processor {

    private OrderColumnProcessor() {}

    public static CSV order(CSV input, String column, OrderType orderType) {
        int colIdx = input.getHeaderMap().get(column);

        List<CSVRecord> ordered = orderType.orderFunc.apply(input, colIdx);

        return new CSV(input.getHeaders(), input.getHeaderMap(), ordered);
    }

    @SuppressWarnings("unused")
    public enum OrderType {
        ALPHABETICAL_ORDER_ASC((input, columnIdx) -> {
            List<CSVRecord> records = input.getRecords();
            records.sort((record1, record2) -> String.CASE_INSENSITIVE_ORDER.compare(record1.get(columnIdx), record2.get(columnIdx)));
            return records;
        }),
        ALPHABETICAL_ORDER_DESC((input, columnIdx) -> {
            List<CSVRecord> records = input.getRecords();
            records.sort((record1, record2) -> String.CASE_INSENSITIVE_ORDER.compare(record1.get(columnIdx), record2.get(columnIdx)));
            Collections.reverse(records);
            return records;
        }),
        NUMERIC_ORDER_ASC((input, columnIdx) -> {
            List<CSVRecord> records = input.getRecords();
            records.sort(Comparator.comparingDouble(csvRecord -> Double.parseDouble(csvRecord.get(columnIdx))));
            return records;
        }),
        NUMERIC_ORDER_DESC((input, columnIdx) -> {
            List<CSVRecord> records = input.getRecords();
            records.sort(Comparator.comparingDouble(csvRecord -> Double.parseDouble(csvRecord.get(columnIdx))));
            Collections.reverse(records);
            return records;
        });

        OrderType(BiFunction<CSV, Integer, List<CSVRecord>> orderFunc) {
            this.orderFunc = orderFunc;
        }

        BiFunction<CSV, Integer, List<CSVRecord>> orderFunc;
    }
}

