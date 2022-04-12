package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.utils.ProcessingUtils.*;

/**
 * processor to execute basic math operations of add, subtract, modulo, division and multiplication in linear time
 */
public class BasicMathProcessor implements Processor {

    public static CSV process(CSV input, MathOperation mathOp, String col1, String col2, String newColName) throws IOException {
        List<String> headers = input.getHeaders();
        headers.add(newColName);

        StringBuilder sb = new StringBuilder().append(listToString(headers)).append("\n");

        int colIdx1 = input.getHeaderMap().get(col1);
        int colIdx2 = input.getHeaderMap().get(col2);

        for (CSVRecord csvRecord : input.getRecords()) {
            double result = mathOp.operation.apply(csvRecord.get(colIdx1), csvRecord.get(colIdx2));

            List<String> values = new ArrayList<>(csvRecord.toList());
            values.add(String.valueOf(result));

            sb.append(listToString(values)).append("\n");
        }

        return createCSV(sb);
    }

    public static CSV processLiteral(CSV input, MathOperation mathOp, String col1, String value, String newColName) throws IOException {
        List<String> headers = input.getHeaders();
        headers.add(newColName);

        StringBuilder sb = new StringBuilder().append(listToString(headers)).append("\n");

        int colIdx1 = input.getHeaderMap().get(col1);

        for (CSVRecord csvRecord : input.getRecords()) {
            double result = mathOp.operation.apply(csvRecord.get(colIdx1), value);

            List<String> values = new ArrayList<>(csvRecord.toList());
            values.add(String.valueOf(result));

            sb.append(listToString(values)).append("\n");
        }

        return createCSV(sb);
    }

    public enum MathOperation {
        ADD((s1, s2) -> parseDouble(s1) + parseDouble(s2)),
        DIVIDE((s1, s2) -> parseDouble(s1) / parseDouble(s2)),
        MODULO((s1, s2) -> parseDouble(s1) % parseDouble(s2)),
        MULTIPLY((s1, s2) -> parseDouble(s1) * parseDouble(s2)),
        SUBTRACT((s1, s2) -> parseDouble(s1) - parseDouble(s2));

        MathOperation(BiFunction<String, String, Double> operation) {
            this.operation = operation;
        }

        BiFunction<String, String, Double> operation;
    }
}
