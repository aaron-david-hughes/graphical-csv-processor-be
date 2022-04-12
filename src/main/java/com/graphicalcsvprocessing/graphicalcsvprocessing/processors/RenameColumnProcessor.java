package com.graphicalcsvprocessing.graphicalcsvprocessing.processors;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.graphicalcsvprocessing.graphicalcsvprocessing.utils.ProcessingUtils.createCSV;
import static com.graphicalcsvprocessing.graphicalcsvprocessing.utils.ProcessingUtils.listToString;

/**
 * processor to rename a specified column in the input in linear time
 */
public class RenameColumnProcessor implements Processor {

    private RenameColumnProcessor() {}

    public static CSV renameColumn(CSV input, String column, String newName) throws IOException {
        List<String> headers = input.getHeaders();

        headers.set(input.getHeaderMap().get(column), newName);

        StringBuilder sb = new StringBuilder().append(listToString(headers)).append("\n");

        for (CSVRecord csvRecord : input.getRecords()) {
            sb.append(listToString(new ArrayList<>(csvRecord.toList()))).append("\n");
        }

        return createCSV(sb);
    }
}
