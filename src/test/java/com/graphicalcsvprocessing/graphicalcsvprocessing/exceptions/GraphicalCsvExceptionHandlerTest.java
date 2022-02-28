package com.graphicalcsvprocessing.graphicalcsvprocessing.exceptions;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;

import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class GraphicalCsvExceptionHandlerTest {

    private final GraphicalCsvExceptionHandler handler = new GraphicalCsvExceptionHandler();

    @Mock
    private WebRequest request;

    @Test
    public void shouldReturn400WithFileProblemMessageWhenGeneralIOException() {
        IOException e = new IOException();

        ResponseEntity<Object> response = handler.handleIOException(e, request);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Problem with input files", response.getBody());
        assertEquals(1, response.getHeaders().size());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
    }

    @Test
    public void shouldReturn400WithGraphProblemMessageWhenGeneralJsonParseOrMappingException() {
        IOException[] exceptions = {
                new JsonMappingException(null, "test-msg", new Exception()),
                new JsonParseException(null, "test-msg", new Exception())
        };

        for (IOException e : exceptions) {
            ResponseEntity<Object> response = handler.handleIOException(e, request);

            assertEquals(400, response.getStatusCodeValue());
            assertEquals("Problem with supplied graph", response.getBody());
            assertEquals(1, response.getHeaders().size());
            assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        }
    }

    @Test
    public void shouldReturn400WithRootIllegalArgumentExceptionMessageWhenJsonParseOrMappingExceptionWithIllegalArgumentExceptionCause() {
        IOException[] exceptions = {
                new JsonMappingException(null, "test-msg", new IllegalArgumentException("root-test-msg")),
                new JsonParseException(null, "test-msg", new IllegalArgumentException("root-test-msg"))
        };

        for (IOException e : exceptions) {
            ResponseEntity<Object> response = handler.handleIOException(e, request);

            assertEquals(400, response.getStatusCodeValue());
            assertEquals("root-test-msg", response.getBody());
            assertEquals(1, response.getHeaders().size());
            assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        }
    }

    @Test
    public void shouldReturn400WithExceptionMessageWhenGeneralIllegalArgumentException() {
        IllegalArgumentException e = new IllegalArgumentException("test-msg");

        ResponseEntity<Object> response = handler.handleIllegalArgumentExceptions(e, request);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("test-msg", response.getBody());
        assertEquals(1, response.getHeaders().size());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
    }

    @Test
    public void shouldReturn400WithExceptionMessageWhenGeneralMultipartException() {
        MultipartException e = new MultipartException("test-msg");

        ResponseEntity<Object> response = handler.handleMultipartExceptions(e, request);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("test-msg", response.getBody());
        assertEquals(1, response.getHeaders().size());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
    }

    @Test
    public void shouldReturn500WithGeneralMessageWhenGeneralException() {
        Exception e = new Exception("test-msg");

        ResponseEntity<Object> response = handler.handleThrowableCatchAll(e, request);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("A general processing error has occurred", response.getBody());
        assertEquals(1, response.getHeaders().size());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
    }
}