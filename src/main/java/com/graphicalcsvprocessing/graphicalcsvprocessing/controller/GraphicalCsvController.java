package com.graphicalcsvprocessing.graphicalcsvprocessing.controller;

import com.graphicalcsvprocessing.graphicalcsvprocessing.annotations.RequestParamObject;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.GraphDataModel;
import com.graphicalcsvprocessing.graphicalcsvprocessing.services.CsvProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
public class GraphicalCsvController {

    @Autowired
    CsvProcessorService csvProcessorService;

    /**
     * maps POST /process to this handler
     *
     * deserializes graph and controls file delivery so that by the time handler runs all request aspects are validated
     *
     * returns a ZIP of files to be returned after process
     */
    @PostMapping("/process")
    public void postProcess(
            HttpServletResponse response,
            @RequestParam(name = "csvFiles") MultipartFile[] csvFiles,
            @RequestParamObject(name = "graph") GraphDataModel gdm
    ) throws IOException {
        Map<String, CSV> returnFiles = csvProcessorService.process(gdm, csvFiles);

        try (ZipOutputStream zippedOut = new ZipOutputStream(response.getOutputStream())) {
            for (Map.Entry<String, CSV> file : returnFiles.entrySet()) {
                ByteArrayOutputStream outputStream = (ByteArrayOutputStream) file.getValue().getOutputStream();

                ZipEntry e = new ZipEntry(file.getKey());
                e.setSize(outputStream.size());
                e.setTime(System.currentTimeMillis());

                zippedOut.putNextEntry(e);
                StreamUtils.copy(new ByteArrayInputStream(outputStream.toByteArray()), zippedOut);
                zippedOut.closeEntry();
            }
            zippedOut.finish();
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=results.zip");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @GetMapping("/")
    public void health(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
