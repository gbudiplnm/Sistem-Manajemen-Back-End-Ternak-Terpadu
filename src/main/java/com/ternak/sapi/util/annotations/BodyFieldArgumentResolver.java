package com.ternak.sapi.util.annotations;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@Component
public class BodyFieldArgumentResolver implements HandlerMethodArgumentResolver {

    private final ObjectMapper objectMapper;

    public BodyFieldArgumentResolver(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(BodyField.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);

        String fieldName = parameter.getParameterAnnotation(BodyField.class).value();

        // Read body once, cache in attribute to avoid multiple reads
        @SuppressWarnings("unchecked")
        Map<String, Object> jsonBody = (Map<String, Object>) servletRequest.getAttribute("cachedRequestBody");

        if (jsonBody == null) {
            try {
                jsonBody = objectMapper.readValue(servletRequest.getInputStream(), Map.class);
                servletRequest.setAttribute("cachedRequestBody", jsonBody);
            } catch (IOException e) {
                throw new HttpMessageNotReadableException("Unable to read request body", e, null);
            }
        }

        Object value = jsonBody.get(fieldName);

        if (value == null) return null;

        // Convert value to expected type
        return objectMapper.convertValue(value, parameter.getParameterType());
    }
}

