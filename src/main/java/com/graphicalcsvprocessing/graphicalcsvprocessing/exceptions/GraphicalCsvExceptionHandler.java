package com.graphicalcsvprocessing.graphicalcsvprocessing.exceptions;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;

/**
 * Handling anticipated exceptions and providing a catch all handler
 */
@ControllerAdvice
public class GraphicalCsvExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = IOException.class)
    protected ResponseEntity<Object> handleIOException(IOException ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = "Problem with input files";

        if (ex instanceof JsonMappingException || ex instanceof JsonParseException) {
            if (ex.getCause() instanceof IllegalArgumentException)
                return handleIllegalArgumentExceptions((IllegalArgumentException) ex.getCause(), request);

            body = "Problem with supplied graph";
        }

        return handleExceptionInternal(ex, body, headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    protected ResponseEntity<Object> handleIllegalArgumentExceptions(IllegalArgumentException ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return handleExceptionInternal(ex, ex.getMessage(), headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = MultipartException.class)
    protected ResponseEntity<Object> handleMultipartExceptions(MultipartException ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return handleExceptionInternal(ex, ex.getMessage(), headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = Exception.class)
    protected ResponseEntity<Object> handleThrowableCatchAll(Exception ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return handleExceptionInternal(
                ex, "A general processing error has occurred",
                headers, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
