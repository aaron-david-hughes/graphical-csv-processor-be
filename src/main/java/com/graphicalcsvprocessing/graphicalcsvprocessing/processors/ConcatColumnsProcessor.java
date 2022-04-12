package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.util.List;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.utils.ProcessingUtils.createCSV;
import static com.graphicalcsvprocessing.graphicalcsvprocessing.utils.ProcessingUtils.listToString;

/**
 * processor to execute concatenation of two columns in linear time
 */
public class ConcatColumnsProcessor implements Processor {

    private ConcatColumnsProcessor() {}

    public static CSV concat(CSV input, String column1, String column2, String concatHeader) throws IOException {
        String headers = listToString(input.getHeaders()).concat(",").concat(concatHeader);
        StringBuilder sb = new StringBuilder().append(headers).append("\n");

        int column1Idx = input.getHeaderMap().get(column1);
        int column2Idx = input.getHeaderMap().get(column2);

        List<CSVRecord> records = input.getRecords();

        for (CSVRecord csvRecord : records) {
            String s = listToString(csvRecord.toList())
                    .concat(",")
                    .concat(csvRecord.get(column1Idx) + csvRecord.get((column2Idx)));

            sb.append(s).append("\n");
        }

        return createCSV(sb);
    }
}
