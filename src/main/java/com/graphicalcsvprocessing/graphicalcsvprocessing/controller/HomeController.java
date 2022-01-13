package com.graphicalcsvprocessing.graphicalcsvprocessing.controller;

import com.graphicalcsvprocessing.graphicalcsvprocessing.annotations.RequestParamObject;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.CSV;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.GraphDataModel;
import com.graphicalcsvprocessing.graphicalcsvprocessing.services.CsvProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
public class HomeController {

    @Autowired
    CsvProcessorService csvProcessorService;

    // TODO
    //  1. some rudimentary form of exception handling
    //  2. an integration guide end point with details of requirements of input - probably just a readme
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
}
