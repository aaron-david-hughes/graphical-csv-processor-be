package com.graphicalcsvprocessing.graphicalcsvprocessing.resolvers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphicalcsvprocessing.graphicalcsvprocessing.annotations.RequestParamObject;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Objects;

public class RequestParamDeserializingArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestParamObject.class);
    }

    protected boolean checkRequired(MethodParameter parameter) {
        RequestParamObject annotation = parameter.getParameterAnnotation(RequestParamObject.class);
        return annotation != null && annotation.required() && !parameter.isOptional();
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) throws Exception {
        Object value = new RequestParamDeserializingArgumentStringResolver()
                .resolveArgument(parameter, null, webRequest, null);

        if (!(value instanceof String)) {
            throw new IllegalArgumentException("Graph request param must be of type String");
        }

        String json = (String) value;

        Object arg = !json.isEmpty() && !json.isBlank()
                ? new ObjectMapper().readValue(json, parameter.getParameterType())
                : null;

        if (checkRequired(parameter) && arg == null) {
            throw new IllegalArgumentException(String.format("'%s' request param may not be null",
                    Objects.requireNonNull(parameter.getParameterAnnotation(RequestParamObject.class)).name()));
        }

        return arg;
    }

    private static class RequestParamDeserializingArgumentStringResolver extends RequestParamMethodArgumentResolver {
        public RequestParamDeserializingArgumentStringResolver() {
            super(true);
        }

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return parameter.hasParameterAnnotation(RequestParamObject.class);
        }

        @Override
        protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
            RequestParamObject ann = parameter.getParameterAnnotation(RequestParamObject.class);

            return ann != null ?
                    new RequestParamDeserializingArgumentStringResolver.RequestParamDeserializingNamedValueInfo(ann) :
                    new RequestParamDeserializingArgumentStringResolver.RequestParamDeserializingNamedValueInfo();
        }

        private static class RequestParamDeserializingNamedValueInfo extends NamedValueInfo {
            public RequestParamDeserializingNamedValueInfo() {
                super("", false, null);
            }

            public RequestParamDeserializingNamedValueInfo(RequestParamObject annotation) {
                super(annotation.name(), annotation.required(), null);
            }
        }
    }
}
