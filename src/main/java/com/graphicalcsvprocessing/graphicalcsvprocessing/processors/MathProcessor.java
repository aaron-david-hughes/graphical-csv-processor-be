package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.utils.ProcessingUtils.createCSV;
import static com.graphicalcsvprocessing.graphicalcsvprocessing.utils.ProcessingUtils.listToString;

public class MathProcessor implements Processor {

    private MathProcessor() {}

    public static CSV rowAverage(CSV input, String newColName, String... columns) throws IOException {
        List<Integer> columnIndexes = Arrays.stream(columns).map(input.getHeaderMap()::get).collect(Collectors.toList());

        List<String> headers = input.getHeaders();
        headers.add(newColName);

        StringBuilder sb = new StringBuilder().append(listToString(input.getHeaders())).append("\n");

        for (CSVRecord csvRecord : input.getRecords()) {
            List<String> values = new ArrayList<>(csvRecord.toList());

            double sum = 0;
            double count = 0;

            for (int i : columnIndexes) {
                sum += Double.parseDouble(values.get(i));
                count++;
            }

            if (count == 0) throw new IllegalArgumentException("No columns supplied to average");

            double avg = sum / count;

            values.add(String.valueOf(avg));

            sb.append(listToString(values)).append("\n");
        }

        return createCSV(sb);
    }
}
