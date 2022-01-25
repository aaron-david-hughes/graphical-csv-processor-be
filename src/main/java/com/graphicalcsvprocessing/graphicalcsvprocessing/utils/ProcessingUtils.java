package com.graphicalcsvprocessing.graphicalcsvprocessing.utils;

import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.io.input.BOMInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ProcessingUtils {
    private ProcessingUtils() {}

    public static CSV createCSV(StringBuilder sb) throws IOException {
        InputStreamReader reader = new InputStreamReader(new BOMInputStream(new ByteArrayInputStream(sb.toString().getBytes()), false));
        return new CSV(new CSVParser(reader, CSVFormat.Builder.create().setHeader().build()));
    }

    public static String listToString(List<String> input) {
        if (input == null) return "";

        String s = input.toString();
        return s.substring(1, s.length() - 1).replaceAll("null", "").replaceAll(",\\s", ",");
    }
}
