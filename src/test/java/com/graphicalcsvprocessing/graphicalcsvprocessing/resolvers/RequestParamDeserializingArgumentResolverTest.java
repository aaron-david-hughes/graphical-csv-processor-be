package com.graphicalcsvprocessing.graphicalcsvprocessing.resolvers;

import com.fasterxml.jackson.core.JsonParseException;
import com.graphicalcsvprocessing.graphicalcsvprocessing.annotations.RequestParamObject;
import com.graphicalcsvprocessing.graphicalcsvprocessing.models.GraphDataModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

import java.lang.reflect.Method;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequestParamDeserializingArgumentResolverTest {

    RequestParamDeserializingArgumentResolver resolver = new RequestParamDeserializingArgumentResolver();

    @Mock
    NativeWebRequest webRequest;

    @Test
    public void supportsParameterReturnsTrueWhenParameterHasRequestParamObjectAnnotation() {
        assertTrue(resolver.supportsParameter(getParamFromMethod(0)));
    }

    @Test
    public void supportsParameterReturnsFalseWhenParameterDoesNotHaveRequestParamObjectAnnotation() {
        assertFalse(resolver.supportsParameter(getParamFromMethod(1)));
    }

    @Test
    public void resolverCreatesObjectFromBodyOfRequestWhenValid() throws Exception {
        when(webRequest.getParameterValues("test")).thenReturn(new String[] {jsonGraphDataModel(true)});

        Object obj = resolver.resolveArgument(
                getParamFromMethod( 0),
                null,
                webRequest,
                null);

        assertNotNull(obj);
        assertTrue(obj instanceof GraphDataModel);

        GraphDataModel gdm = (GraphDataModel) obj;
        assertNotNull(gdm.getNodes());
        assertNotNull(gdm.getEdges());
        assertTrue(gdm.getNodes().length > 0);
        assertTrue(gdm.getEdges().length > 0);
    }

    @Test
    public void resolverThrowsExceptionFromBodyOfRequestWhenInvalid() throws Exception {
        when(webRequest.getParameterValues("test")).thenReturn(new String[] {jsonGraphDataModel(false)});

        try {
            resolver.resolveArgument(
                    getParamFromMethod(0),
                    null,
                    webRequest,
                    null);
            fail("JsonParseException expected");
        } catch (JsonParseException e) {
            assertTrue(e.getMessage().startsWith("Unexpected end-of-input"));
        }
    }

    @Test
    public void resolverStopsEmptyResultWhenRequiredButNotPresent() throws Exception {
        when(webRequest.getParameterValues("test")).thenReturn(new String[] {""});

        try {
            resolver.resolveArgument(
                    getParamFromMethod(0),
                    null,
                    webRequest,
                    null);
        } catch (IllegalArgumentException e) {
            assertEquals("'test' request param may not be null", e.getMessage());
        }
    }

    @Test
    public void resolverThrowsExceptionWhenRequestParamNotPresent() throws Exception {
        try {
            resolver.resolveArgument(
                    getParamFromMethod(2),
                    null,
                    webRequest,
                    null);
        } catch (IllegalArgumentException e) {
            assertEquals("Graph request param must be of type String", e.getMessage());
        }
    }

    @Test
    public void resolverThrowsExceptionWhenRequestParamPresentButNull() throws Exception {
        when(webRequest.getParameterValues("test")).thenReturn(new String[] {null});

        try {
            resolver.resolveArgument(
                    getParamFromMethod(2),
                    null,
                    webRequest,
                    null);
        } catch (IllegalArgumentException e) {
            assertEquals("Graph request param must be of type String", e.getMessage());
        }
    }

    @Test
    public void resolverAllowsEmptyResultWhenNotRequiredAndPresentAsEmptyString() throws Exception {
        when(webRequest.getParameterValues("test")).thenReturn(new String[] {""});

        Object obj = resolver.resolveArgument(
                getParamFromMethod(2),
                null,
                webRequest,
                null);

        assertNull(obj);
    }

    @Test
    public void resolverAllowsEmptyResultWhenNotRequiredAndPresentAsBlankString() throws Exception {
        when(webRequest.getParameterValues("test")).thenReturn(new String[] {"  \n    "});

        Object obj = resolver.resolveArgument(
                getParamFromMethod(2),
                null,
                webRequest,
                null);

        assertNull(obj);
    }


    //ignore
    public boolean testParameterMethod(
            @RequestParamObject(name = "test") GraphDataModel gdm,
            GraphDataModel notAnnotated,
            @RequestParamObject(name = "test", required = false) GraphDataModel gdm1
    ) {
        if (gdm != null)
            return testParameterMethod(null, null, null);
        return notAnnotated == null && gdm1 == null;
    }

    private MethodParameter getParamFromMethod(int paramIdx) {
        for (Method method : RequestParamDeserializingArgumentResolverTest.class.getMethods()) {
            if (method.getName().equals("testParameterMethod"))
                return MethodParameter.forParameter(method.getParameters()[paramIdx]);
        }

        throw new IllegalArgumentException("Method not found");
    }

    private String jsonGraphDataModel(boolean valid) {
        return "{\n" +
                "  \"nodes\": [ \n" +
                "    {\n" +
                "      \"id\": \"1\",\n" +
                "      \"group\": \"file\", \n" +
                "      \"operation\":\"open_file\",\n" +
                "      \"name\": \"Scores.csv\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"2\",\n" +
                "      \"group\": \"processing\", \n" +
                "      \"operation\": \"join\",\n" +
                "      \"joinType\":\"left\",\n" +
                "      \"onLeft\": \"AttendanceUsingStudentNum.StudentNum\",\n" +
                "      \"onRight\": \"Scores.StudentNum\"\n" +
                "    }, \n" +
                "    {\n" +
                "      \"id\": \"0\",\n" +
                "      \"group\": \"file\",\n" +
                "      \"operation\": \"open_file\", \n" +
                "      \"name\": \"AttendanceUsingStudentNum.csv\" \n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"3\",\n" +
                "      \"group\": \"file\",\n" +
                "      \"operation\": \"open_file\", \n" +
                "      \"name\": \"Attendance.csv\" \n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"4\",\n" +
                "      \"group\": \"processing\", \n" +
                "      \"operation\": \"join\",\n" +
                "      \"joinType\":\"left\",\n" +
                "      \"onLeft\": \"StudentNum\",\n" +
                "      \"onRight\": \"Attendant\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"5\",\n" +
                "      \"group\": \"file\", \n" +
                "      \"operation\": \n" +
                "      \"write_file\",\n" +
                "      \"name\": \"interim.csv\"\n" +
                "    } \n" +
                "  ], \n" +
                "  \"edges\": [  \n" +
                "    { \n" +
                "      \"from\": \"0\",\n" +
                "      \"to\": \"2\",\n" +
                "      \"priority\":\"y\"\n" +
                "    }, \n" +
                "    {\n" +
                "      \"from\": \"1\",\n" +
                "      \"to\": \"2\",\n" +
                "      \"priority\":\"n\" \n" +
                "    },\n" +
                "    {\n" +
                "      \"from\":\"2\",\n" +
                "      \"to\":\"5\",\n" +
                "      \"priority\":\"n\"\n" +
                "    },\n" +
                "    { \n" +
                "      \"from\": \"5\",\n" +
                "      \"to\": \"4\",\n" +
                "      \"priority\":\"y\"\n" +
                "    },\n" +
                "    { \n" +
                "      \"from\": \"3\",\n" +
                "      \"to\": \"4\",\n" +
                "      \"priority\":\"n\"\n" +
                "    }\n" +
                "  ]\n" +
                (valid ? "}" : "");
    }
}