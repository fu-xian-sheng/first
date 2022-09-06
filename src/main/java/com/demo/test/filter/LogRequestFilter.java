package com.demo.test.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 请求日志打印
 * A filter which logs web requests that lead to an error in the system.
 */
@Slf4j
@Component
public class LogRequestFilter extends OncePerRequestFilter implements Ordered {

    // put filter at the end of all other filters to make sure we are processing after all others
    private int order = Ordered.LOWEST_PRECEDENCE - 8;
    private DefaultErrorAttributes errorAttributes;

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        // pass through filter chain to do the actual request handling
        filterChain.doFilter(wrappedRequest, wrappedResponse);
        int status = wrappedResponse.getStatus();

        Map<String, Object> trace = getTrace(wrappedRequest, status);

        // body can only be read after the actual request handling was done!
        getRequestBody(wrappedRequest, trace);
        getResponseBody(wrappedResponse, trace);
        wrappedResponse.copyBodyToResponse();
        logTrace(wrappedRequest, trace);
    }

    private void getRequestBody(ContentCachingRequestWrapper request, Map<String, Object> trace) {
        // wrap request to make sure we can read the body of the request (otherwise it will be consumed by the actual
        // request handler)
        ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                String payload;
                try {
                    payload = new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException ex) {
                    payload = "[unknown]";
                }

                trace.put("requestBody", payload);
            }
        }
    }

    private void getResponseBody(ContentCachingResponseWrapper response, Map<String, Object> trace) {
        // wrap request to make sure we can read the body of the request (otherwise it will be consumed by the actual
        // request handler)
        ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                String payload;
                try {
                    payload = new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException ex) {
                    payload = "[unknown]";
                }

                trace.put("responseBody", payload);
            }
        }
    }

    private void logTrace(HttpServletRequest request, Map<String, Object> trace) {
        Object method = trace.get("method");
        Object path = trace.get("path");
        Object statusCode = trace.get("statusCode");
        if ("/health".equals(path) && 200 == (int)statusCode) {
            return;
        }
        logger.info(String.format("%s %s produced an error status code '%s'. Trace: '%s'", method, path, statusCode,
                trace));
    }

    protected Map<String, Object> getTrace(HttpServletRequest request, int status) {
        Throwable exception = (Throwable) request.getAttribute("javax.servlet.error.exception");

        Map<String, Object> trace = new LinkedHashMap<String, Object>();
        trace.put("method", request.getMethod());
        trace.put("path", request.getRequestURI());
        trace.put("query", request.getQueryString());
        trace.put("header", getRequestHeaders(request));
        trace.put("statusCode", status);
        ErrorAttributeOptions of = ErrorAttributeOptions.of(ErrorAttributeOptions.Include.STACK_TRACE);
        if (exception != null && this.errorAttributes != null) {
            WebRequest webRequest = new ServletWebRequest(request);
            trace.put("error", this.errorAttributes.getErrorAttributes(webRequest,of));
//                    .getErrorAttributes(new ServletRequestAttributes(request), true));
        }

        return trace;
    }

    private Map<String, String> getRequestHeaders(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            map.put(headerName, request.getHeader(headerName));
        }
        return map;
    }
}
