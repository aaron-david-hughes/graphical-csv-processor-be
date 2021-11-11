package com.graphicalcsvprocessing.graphicalcsvprocessing.controller;

import com.graphicalcsvprocessing.graphicalcsvprocessing.annotations.RequestParamObject;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.GraphicalDataModel;
import com.graphicalcsvprocessing.graphicalcsvprocessing.services.CsvProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Controller
public class HomeController {

    @Autowired
    CsvProcessorService csvProcessorService;

    // TODO
    //  1. some rudimentary form of exception handling
    //  2. an integration guide end point with details of requirements of input - probs just a readme
    //  3. check join expected behaviour on same column names in both files (joined on and otherwise)

    @PostMapping("/process")
    public ResponseEntity<Resource> postProcess(
            @RequestParam(name = "csvFiles") MultipartFile[] csvFiles,
            @RequestParamObject(name = "graph") GraphicalDataModel gdm
    ) throws IOException {
        ByteArrayOutputStream outputStream = (ByteArrayOutputStream) csvProcessorService.process(gdm, csvFiles);

        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(outputStream.toByteArray()));

        return ResponseEntity
                .ok()
                .contentLength(outputStream.size())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
